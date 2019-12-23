;; modifier.clj
;; andrewj 2019-12-21

(ns san-juan.modifier
  #_(:require [san-juan.state :refer :all]))

;;-----------------------
(defmulti modify
  (fn [action caller] [(:action action) caller]))

;;-----------------------
;; Builder modifiers
;; - Picker: cost is 1 less for all cards.
;; - Smithy: cost is 1 less if a production building.
;; - Poorhouse: take an extra card after building a production building if 0 or 1 cards remaining in the hand.
;; - Black market: use up to 2 goods to offset cost of any building.
;; - Carpenter: take an extra card after building a violet building.
;; - Quarry: cost is 1 less if a violet building.
;; - Library: cost-2 for all buildings.

(defmethod modify [:builder :picker]
  [action modifier]
  (update-in action [:cost] dec))

(defmethod modify [:builder :smithy]
  [action modifier]
  (if (= :production (card-val :kind (:build action)))
    (update-in action [:cost] dec)
    action))

(defmethod modify [:builder :poorhouse]
  [action modifier]
  (if (= :production (card-val :kind (:build action)))
    (update-in action [:take] inc)
    action))

;;-----------------------
;; Catch-all
(defmethod modify :default
  [action modifier]
  action)





;; The End