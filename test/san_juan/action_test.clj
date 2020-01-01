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
      (is (= 4 (count (build-modify-costs 0 false s0))))
      (is (= [{:name :sugar-mill, :kind :production, :cost 2, :vp 1, :count 8}
              {:name :prefecture, :kind :violet, :cost 3, :vp 2, :count 3}
              {:name :trading-post, :kind :violet, :cost 2, :vp 1, :count 3}
              {:name :aqueduct, :kind :violet, :cost 3, :vp 2, :count 3}]
             (build-modify-costs 0 false s0))))))

;; The End