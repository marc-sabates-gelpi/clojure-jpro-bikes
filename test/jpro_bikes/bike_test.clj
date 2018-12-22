(ns jpro-bikes.bike-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [jpro-bikes.bike :as bike]
            [clojure.spec.test.alpha :as stest]))

(deftest within?-spec-test
  (testing "within?"
    (is (every? (comp nil? :failure) (stest/check `bike/within?)))))

(deftest get-property-spec-test
  (testing "get-property"
    (is (every? (comp nil? :failure) (stest/check `bike/get-property)))))

;; FIXME: Spec tests are failing due to nil values
#_(deftest get-bike-points-spec-test
  (testing "get-bike-points"
    (is (every? (comp nil? :failure) (stest/check `bike/get-bike-points)))))
