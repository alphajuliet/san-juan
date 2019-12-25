;; modifier.clj
;; andrewj 2019-12-21

(ns san-juan.modifier
  (:require [san-juan.state :refer :all]))

;;-----------------------
(defmulti modify
  "Define a multimethod for handling modifications of actions based on cards or roles.
   This uses double dispatch on action and card/role."
  {:type "Action -> Card -> State -> Action"}
  (fn [action caller _]
    [(:action action) caller]))

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
  [action modifier _]
  "Picker: cost is 1 less for all cards."
  (update-in action [:cost] dec))

(defmethod modify [:builder :smithy]
  [action modifier _]
  "Smithy: cost is 1 less if a production building."
  (if (= :production (card-val :kind (:build action)))
    (update-in action [:cost] dec)
    action))

(defmethod modify [:builder :poorhouse]
  [action modifier _]
  "Poorhouse: take an extra card after building a production building if 0 or 1 cards remaining in the hand."
  (if (= :production (card-val :kind (:build action)))
    (update-in action [:take] inc)
    action))

(defmethod modify [:builder :carpenter]
  [action modifier _]
  "Carpenter: take an extra card after building a violet building."
  (if (= :violet (card-val :kind (:build action)))
    (update-in action [:take] inc)
    action))

(defmethod modify [:builder :quarry]
  [action modifier _]
  "Quarry: cost is 1 less if a violet building."
  (if (= :violet (card-val :kind (:build action)))
    (update-in action [:cost] dec)
    action))

;;-----------------------
;; Catch-all
(defmethod modify :default
  [action _ _]
  "Default case, return action unchanged."
  action)

;; The End