(ns san-juan.action-test
  (:require [clojure.test :refer :all]
            [san-juan.action :refer :all]))

(deftest action-tests
  (testing "removev"
    (is (= [:a :c] (removev [:a :b :c] :b)))
    (is (= [:a :c :b] (removev [:a :b :c :b] :b)))
    (is (= [:a :b :c] (removev [:a :b :c] :d)))))

(deftest builder-tests
  (testing "build-modify-costs"
    (let [s0 (init-game 4 0)
          s1 (move-card :smithy [:deck] [:player 0 :area] s0)]
      (is (= 4 (count (builder-modify-costs 0 false s0))))
      (is (= '(2 3 2 3) (map :cost (builder-modify-costs 0 false s0))))
      (is (= 4 (count (builder-affordable-buildings 0 false s1)))))))

;; The End