(ns jpro-bikes.bike
  (:require [cheshire.core :refer [parse-string]]
            [clojure.edn :as edn]          
            [clojure.math.numeric-tower :as math]
            [clojure.spec.alpha :as s]
            [jpro-bikes.spec]))

(s/def ::lat :jpro-bikes.map-point/lat)
(s/def ::lon :jpro-bikes.map-point/lon)
(s/def ::id string?)
(s/def ::commonName string?)
(s/def ::key string?)
(s/def ::value any?)
(s/def ::additionalProperties (s/keys :req-un [::key
                                               ::value]))
(s/def ::tfl-bike-point (s/keys :req-un [::lat
                                         ::lon
                                         ::id
                                         ::commonName
                                         ::additionalProperties]))
(s/def ::num-bikes nat-int?)
(s/def ::num-empty-docks nat-int?)
(s/def ::num-docks nat-int?)
(s/def ::bike-point (s/keys :req-un [::id
                                     ::name
                                     ::num-bikes
                                     ::num-empty-docks
                                     ::num-docks]))

(def ^:const bike-points-url "https://push-api-radon.tfl.gov.uk/BikePoint?app_key=55f39a3412591e1541de6123f7c81ee7&app_id=a30e978f")
(def ^:const leyton {:lat  51.5696734 :lon -0.0156810})
(def ^:const radius 0.09)

(defn- within?
  "Return `true` if `bike-point`'s distance to `centrer` is less or equal than `radius`; `false` otherwise."
  [centre
   radius
   {:keys [lat lon] :or {lat 90 lon 180} :as _bike-point}]
  (let [d (math/sqrt (+ (math/expt (- lat (:lat centre)) 2)
                        (math/expt (- lon (:lon centre)) 2)))]
    (>= radius d)))

(s/fdef within?
  :args (s/cat :centre :jpro-bikes/map-point :radius :jpro-bikes/radius :bike-point :jpro-bikes.bike/tfl-bike-point)
  :ret boolean?)

(defn- get-property
  "Return the additional property `property`'s value."
  [properties property]
  (some->> properties
           (filter (comp (hash-set property) :key))
           first
           :value))

(s/fdef get-property
  :args (s/cat :properties ::additionalProperties :property string?)
  :ret any?)

(defn get-bike-points
  "Get the bike points within the defined cirular area from the `center` and `radius`."
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
                 :num-bikes (edn/read-string (get-property additionalProperties "NbBikes"))
                 :num-empty-docks (edn/read-string (get-property additionalProperties "NbEmptyDocks"))
                 :num-docks (edn/read-string (get-property additionalProperties "NbDocks"))})))))

(s/fdef get-bike-points
  :args (s/cat :centre :jpro-bikes/map-point :radius :jpro-bikes/radius)
  :ret (s/coll-of :jpro-bikes.bike/bike-point :min-count 0))
