(ns jpro-bikes.server
  (:require [aleph.http :as http]
            [bidi.bidi :as bidi]
            [bidi.ring :refer [make-handler] #_(make-handler)]
            [jpro-bikes.bike :as bike]
            [yada.yada :as yada]))

(defn index-handler
  []
  (yada/handler "Hello World!"))

(defn bikes-handler
  []
  (yada/handler #(bike/get-bike-points bike/leyton bike/radius)))

(def handler
  (make-handler ["/" {"index.html" (index-handler)
                      "bikes" (bikes-handler)}]))

(def server (http/start-server handler {:port 8080}))
