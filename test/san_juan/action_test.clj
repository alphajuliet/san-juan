(ns san-juan.core-test
  (:require [clojure.test :refer :all]
            [san-juan.action :refer :all]))

(deftest action-tests
  (testing "removev"
    (is (= [:a :c] (removev [:a :b :c] :b)))
    (is (= [:a :c :b] (removev [:a :b :c :b] :b)))
    (is (= [:a :b :c] (removev [:a :b :c] :d)))))

;; The End