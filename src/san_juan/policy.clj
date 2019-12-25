;; policy.clj
;; andrewj 2019-12-25

(ns san-juan.policy
  (:require [san-juan.state :refer :all]
            [san-juan.modifier :refer :all]
            [san-juan.action :refer :all]))

;;-----------------------
(defn build-options
  "Available actions for player `p` to build something. Set `isPicker` true if player `p` picked the builder action."
  {:type "Integer -> Boolean -> State -> [Action]"}
  [p is-picker state]
  (let [hand-cards (get-in state [:player p :hand])
        area-cards (get-in state [:player p :area])
        n (count hand-cards)]
    (for [c hand-cards]
      (let [cost (card-val :cost c) ;; base cost
            action (map->Action {:action :builder
                                 :player p
                                 :is-picker is-picker
                                 :build c
                                 :pay []
                                 :cost cost
                                 :take 0})]
        ;; Cumulatively apply the built modifier cards
        (apply-modifiers p action state)))))

;; The End