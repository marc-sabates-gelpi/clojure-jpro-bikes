(ns jpro-bikes.core
  (:gen-class))

(defn stop
  []
  (when-let [s jpro-bikes.server/server]
    (.close s)))

(defn -main
  ""
  [& args]
  (println "Hello, World!"))
