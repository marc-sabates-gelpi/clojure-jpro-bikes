(ns jpro-bikes.core
  (:require [jpro-bikes.server])
  (:gen-class))

(defn stop
  []
  (when-let [s jpro-bikes.server/server]
    (.close s)))

(defn -main
  ""
  [& args]
  (println "Hello, World!"))
