(ns jpro-bikes.server
  (:require [aleph.http :as http]
            [cheshire.core :refer [generate-string]]
            [bidi.ring :refer [make-handler]]
            [hiccup.core :refer [html]]
            [markdown.core :refer [md-to-html-string]]
            [jpro-bikes.bike :as bike]
            [jpro-bikes.user :as user]
            [yada.yada :as yada]))

(defonce server (atom nil))

(def basic-auth {:access-control
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
                       (when creds ctx))}}}}})

(defn render-bike-point
  [{:keys [:name :num-bikes] :as _b-point}]
  [:tr [:td name] [:td num-bikes]])

(defn bike-stats-html
  [_]
  (if-let [bike-points (bike/get-bike-points bike/leyton)]
    (html [:h2 "Bike points near Leyton"
           [:table
            [:tr [:th "Name"] [:th "Available bikes"]]
            (map render-bike-point bike-points)]])
    (html [:h2 "There are no bike points to display.."])))

(defn bike-stats-json
  [_]
  (generate-string (or (bike/get-bike-points bike/leyton) '())))

(defn make-root-handler
  []
  (yada/handler (yada/resource {:produces "text/html"
                                :response (md-to-html-string (slurp "README.md"))})))

(defn make-bikes-handler
  []
  (yada/resource
   (merge basic-auth
          {:methods
           {:get
            {:produces
             {:media-type "text/html" :charset "utf8"}
             :response bike-stats-html}}})))

(defn make-bikes-handler-json
  []
  (yada/resource
   (merge basic-auth
          {:methods
           {:get
            {:produces
             {:media-type "application/json" :charset "utf8"}
             :response bike-stats-json}}})))

(def routes ["" 
             {"/" (make-root-handler)
              "/bikes" {"" (make-bikes-handler)
                        "/json" (make-bikes-handler-json)}}])

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
