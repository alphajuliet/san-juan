(ns san-juan.core-test
  (:require [clojure.test :refer :all]
            [san-juan.action :refer :all]))

(deftest action-tests
  (testing "removev"
    (is (= [:a :c] (removev [:a :b :c] :b)))
    (is (= [:a :c :b] (removev [:a :b :c :b] :b)))
    (is (= [:a :b :c] (removev [:a :b :c] :d))))

  (testing "build-options"
    (let [opts-0 (build-options 0 false s0)
          opts-1 (build-options 0 true s0)]
      (is (= 4 (count opts-0)))
      (is (= :prefecture ((comp :build first) opts-1)))
      (is (= 3 ((comp :cost first) opts-0)))
      (is (= 2 ((comp :cost first) opts-1))))))



;; The End