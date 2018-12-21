(ns jpro-bikes.bike
  (:require [cheshire.core :refer [parse-string]]
            [clojure.math.numeric-tower :as math]
            [clojure.spec.alpha :as s]
            [jpro.bikes.spec]))

(s/def ::lat :jpro-bikes.map-point/lat)
(s/def ::lon :jpro-bikes.map-point/lon)
(s/def ::id string?)
(s/def ::commonName string?)
(s/def ::key string?)
(s/def ::value any?)
(s/def ::additionalProperties (s/keys :req-un [::key
                                               ::value]))
(s/def ::bike-point (s/keys :req-un [::lat
                                     ::lon
                                     ::id
                                     ::commonName
                                     ::additionalProperties]))

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

(s/fdef
    :args (s/cat :centre :jpro-bikes/map-point :radius :jpro-bikes/radius :bike-point :jpro-bikes.bike/bike-point)
    :ret boolean?)

(defn get-property
  "Return the additional property `property`'s value."
  [properties property]
  (some->> properties
           (filter (comp (hash-set property) :key))
           first
           :value))

(s/fdef
    :args (s/cat :properties ::additionalProperties :property string?)
    :ret any?)

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
