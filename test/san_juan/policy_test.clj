(ns san-juan.policy-test
  (:require [clojure.test :refer :all]
            [san-juan.action :refer :all]
            [san-juan.policy :refer :all]))

(deftest policy-test
  (testing "build-options"
    (let [opts-0 (build-options 0 false s0)
          opts-1 (build-options 0 true s0)]
      (is (= 4 (count opts-0)))
      (is (= :prefecture ((comp :build first) opts-1)))
      (is (= 3 ((comp :cost first) opts-0)))
      (is (= 2 ((comp :cost first) opts-1))))))

;; The End