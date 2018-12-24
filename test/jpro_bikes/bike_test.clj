(ns jpro-bikes.bike-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [jpro-bikes.bike :as bike]
            [clojure.spec.test.alpha :as stest]))

(deftest coordinates?-spec-test
  (testing "coordinates?"
    (is (every? (comp nil? :failure) (stest/check `bike/coordinates?)))))

(deftest distance-centre-spec-test
  (testing "distance-centre"
    (is (every? (comp nil? :failure) (stest/check `bike/distance-centre)))))

(deftest get-property-spec-test
  (testing "get-property"
    (is (every? (comp nil? :failure) (stest/check `bike/get-property)))))

(deftest distance-centre-unit-test
  (testing "distance-centre"
    (is (= 0.14142135623730953 (#'jpro-bikes.bike/distance-centre {:lat 0.0 :lon 0.0} {:lat 0.1 :lon 0.1})))
    (is (= 0.14142135623730953 (#'jpro-bikes.bike/distance-centre {:lat 0.0 :lon 0.0} {:lat -0.1 :lon -0.1})))))

(deftest make-bike-point-spec-test
  (testing "make-bike-point"
    (is (every? (comp nil? :failure) (stest/check `bike/make-bike-point)))))
