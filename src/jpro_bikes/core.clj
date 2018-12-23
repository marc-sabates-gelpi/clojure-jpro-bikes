(ns jpro-bikes.core
  (:require [jpro-bikes.server :as server])
  (:gen-class))

(defn -main
  ""
  [& args]
  (server/start-server))
