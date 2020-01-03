(ns san-juan.modifier-test
  (:require [clojure.test :refer :all]
            [san-juan.state :refer :all]
            [san-juan.modifier :refer :all]
            [san-juan.action :refer :all]))

(deftest modifier-tests
  (testing "modify"
    (let []
      (is (= 2 (:cost (modify :build-cost :indigo-plant (:sugar-mill all-cards)))))
      (is (= 1 (:cost (modify :build-cost :picker (:sugar-mill all-cards)))))
      (is (= 1 (:cost (modify :build-cost :smithy (:sugar-mill all-cards)))))
      (is (= 3 (:cost (modify :build-cost :smithy (:prefecture all-cards)))))
      (is (= 2 (:cost (modify :build-cost :quarry (:prefecture all-cards)))))))

  (testing "modify-hand"
    (let [h1 (get-in s1 [:player 0 :hand])
          a1 (get-in s1 [:player 0 :area])
          m1 (modify-hand :build-cost a1 h1)]
      (is (= 4 (count h1)))
      (is (= '(1 3 2 3) (map :cost m1))))))


;; The End