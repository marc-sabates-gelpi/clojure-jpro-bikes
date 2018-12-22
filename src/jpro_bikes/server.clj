(ns jpro-bikes.server
  (:require [aleph.http :as http]
            [bidi.bidi :as bidi]
            [bidi.ring :refer [make-handler]]
            [jpro-bikes.bike :as bike]
            [jpro-bikes.user :as user]
            [yada.yada :as yada]))

(defn make-root-handler
  []
  (yada/handler "Jpro Bikes!"))

(defn make-bikes-handler
  []
  (yada/resource
   {:access-control
    {:realms
     {"default"
      {:authentication-schemes
       [{:scheme "Basic"
         :verify
         (fn [[user-id given-password]]
           (when-let [user (get user/users user-id)]
             (when (= given-password (:jpro-bikes.user/password user))
               {:jpro-bikes.user/user (dissoc user :jpro-bikes.user/password)})))}]
       :authorization
       {:validate
        (fn [ctx creds]
          (when creds ctx))}}}}
    
    :methods
    {:get
     {:produces
      {:media-type "text/plain" :charset "utf8"}
      :response (fn [_]
                  (bike/get-bike-points bike/leyton bike/radius))}}}))

(def routes ["" 
             {"/" (make-root-handler)
              "/bikes" (make-bikes-handler)}])

(def handler (make-handler routes))

(def server (http/start-server handler {:port 8080}))
