(ns jpro-bikes.bikes
  (:require [cheshire.core :refer [parse-string]]
            [clojure.math.numeric-tower :as math]))

(def ^:const bike-points-url "https://push-api-radon.tfl.gov.uk/BikePoint?app_key=55f39a3412591e1541de6123f7c81ee7&app_id=a30e978f")
(def ^:const centre {:lat  51.5696734 :lon -0.0156810})
(def ^:const radius 0.09)

(defn within?
  [centre
   radius
   {:keys [lat lon] :or {lat 90 lon 180} :as _bike-point}]
  (let [d (math/sqrt (+ (math/expt (- lat (:lat centre)) 2)
                        (math/expt (- lon (:lon centre)) 2)))]
    (>= radius d)))

(defn get-property
  "Return the additional property `property`'s value."
  [properties property]
  (some->> properties
           (filter (comp (hash-set property) :key))
           first
           :value))

(defn get-bike-points
  "Get the bike points within the defined circle from the `center` and `radius`."
  [centre radius]
  (let [all-bike-points #_(-> bike-points-url
                            slurp
                            (parse-string true))
        (-> "resources/bike-points.edn"
            slurp
            clojure.edn/read-string)]
    (->> all-bike-points
         (filter (partial within? centre radius))
         (map (fn [{:keys [id commonName additionalProperties]}]
                {:id id
                 :name commonName
                 :num-bikes (get-property additionalProperties "NbBikes")
                 :num-empty-docks (get-property additionalProperties "NbEmptyDocks")
                 :num-docks (get-property additionalProperties "NbDocks")})))))
