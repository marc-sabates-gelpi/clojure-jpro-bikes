(ns jpro-bikes.spec
  (:require [clojure.spec.alpha :as s]))

(s/def :jpro-bikes.map-point/lon (s/double-in :min -180.0 :max 180.0 :NaN? false :infinite? false))
(s/def :jpro-bikes.map-point/lat (s/double-in :min -90.0 :max 90.0 :NaN? false :infinite? false))
(s/def :jpro-bikes/map-point (s/keys :req-un [:jpro-bikes.map-point/lat
                                              :jpro-bikes.map-point/lon]))
