(ns jpro-bikes.server
  (:require [aleph.http :as http]
            [bidi.ring :refer [make-handler]]
            [hiccup.core :refer [html]]
            [jpro-bikes.bike :as bike]
            [jpro-bikes.user :as user]
            [yada.yada :as yada]))

(defonce server (atom nil))

(defn render-bike-point
  [{:keys [:name :num-bikes] :as _b-point}]
  [:tr [:td name] [:td num-bikes]])

(defn bikes-stats-html
  [_]
  (if-let [bike-points (bike/get-bike-points bike/leyton bike/radius)]
    (->> [:h2 "Bike points near Leyton"
          (into [:table
                 [:tr [:th "Name"] [:th "Available bikes"]]
                 (map render-bike-point bike-points)])]
         html)
    (html [:h2 "There are no bike points to display.."])))

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
      {:media-type "text/html" :charset "utf8"}
      :response bikes-stats-html}}}))

(def routes ["" 
             {"/" (make-root-handler)
              "/bikes" (make-bikes-handler)}])

(def handler (make-handler routes))

(defn start-server
  []
  (when-not @server
    (println "Starting server")
    (reset! server (http/start-server handler {:port 8080}))))

(defn stop-server
  []
  (when @server
    (println "Stopping server")
    (reset! server (.close @server))))
