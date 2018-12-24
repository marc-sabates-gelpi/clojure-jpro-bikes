(ns jpro-bikes.bike
  (:require [cheshire.core :refer [parse-string]]
            [clojure.edn :as edn]          
            [clojure.math.numeric-tower :as math]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [jpro-bikes.spec]))

(s/def ::lat :jpro-bikes.map-point/lat)
(s/def ::lon :jpro-bikes.map-point/lon)
(s/def ::id string?)
(s/def ::commonName string?)
(s/def ::key #{"NbBikes" "NbDocks" "NbEmptyDocks"})
(s/def ::value (s/with-gen string?
                 #(s/gen #{"58" "88" "9" "93" "83" "3" "51" "50" "34" "69" "49" "22" "87" "26" "4" "8" "28" "60" "14" "82" "59" "89" "61" "57" "68" "30" "21" "96" "80" "33" "20" "67" "81" "47" "98" "19" "17" "25" "73" "78" "15" "42" "7" "66" "44" "5" "100" "48" "53" "90" "18" "36" "12" "13" "27" "62" "75" "24" "76" "35" "6" "97" "94" "99" "38" "70" "77" "39" "1" "63" "84" "0" "43" "95" "74" "37" "46" "11" "45" "56" "32" "55" "85" "2" "72" "54" "16" "41" "91" "10" "65" "40" "31" "71" "86" "64" "92" "23" "52" "79"})))
(s/def ::additionalProperty (s/keys :req-un [::key
                                             ::value]))
(s/def ::additionalProperties (s/coll-of ::additionalProperty))
(s/def ::tfl-bike-point (s/keys :req-un [::lat
                                         ::lon
                                         ::id
                                         ::commonName
                                         ::additionalProperties]))
(s/def ::name string?)
(s/def ::num-bikes nat-int?)
(s/def ::num-empty-docks nat-int?)
(s/def ::num-docks nat-int?)
(s/def ::bike-point (s/keys :req-un [::id
                                     ::name
                                     ::num-bikes
                                     ::num-empty-docks
                                     ::num-docks
                                     ::lat
                                     ::lon]))

(def ^:const bike-points-url "https://push-api-radon.tfl.gov.uk/BikePoint?app_key=55f39a3412591e1541de6123f7c81ee7&app_id=a30e978f")
(def ^:const leyton {:lat 51.560558 :lon -0.015465})

(defn- coordinates?
  "Return `true` if `bike-point` has latitude and longitude; false otherwise."
  [{:keys [lat lon] :as _bike-point}]
  (and (not (nil? lat)) (not (nil? lon))))

(s/fdef coordinates?
  :args (s/cat :bike-point :jpro-bikes.bike/tfl-bike-point)
  :ret boolean?)

(defn distance-centre
  "Retrun the distance to the `centre`."
  [centre {:keys [lat lon] :as _point}]
  (math/sqrt (+ (math/expt (- lat (:lat centre)) 2)
                (math/expt (- lon (:lon centre)) 2))))

(s/fdef distance-centre
  :args (s/cat :centre :jpro-bikes/map-point :point :jpro-bikes.bike/tfl-bike-point)
  :ret (s/and double? (complement neg?)))

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

(defn- make-bike-point
  [{:keys [id commonName additionalProperties lat lon] :as _bike-point}]
  {:id id
   :name commonName
   :num-bikes (-> additionalProperties
                  (get-property "NbBikes")
                  edn/read-string
                  (or 0))
   :num-empty-docks (-> additionalProperties
                        (get-property "NbEmptyDocks")
                        edn/read-string
                        (or 0))
   :num-docks (-> additionalProperties
                  (get-property "NbDocks")
                  edn/read-string
                  (or 0))
   :lat lat
   :lon lon})

(s/fdef make-bike-point
  :args (s/cat :bike-point :jpro-bikes.bike/tfl-bike-point)
  :ret :jpro-bikes.bike/bike-point)

(defn get-bike-points
  "Get the bike points within the defined cirular area from the `center` and `radius`."
  [centre]
  (let [all-bike-points (-> bike-points-url
                            slurp
                            (parse-string true))]
    (->> all-bike-points
         (filter coordinates?)
         (sort-by (partial distance-centre centre))
         (take 5)
         (map make-bike-point)
         seq)))

(s/fdef get-bike-points
  :args (s/cat :centre :jpro-bikes/map-point)
  :ret (s/nilable (s/coll-of :jpro-bikes.bike/bike-point :min-count 1)))
