;; modifier.clj
;; andrewj 2019-12-21

(ns san-juan.modifier
  (:require [san-juan.state :refer :all]))

;;-----------------------
(defmulti modify
  "A multimethod for handling modification to a card's properties according to role, whether the player picked the role, and a modifier card."
  {:type "∀ a b. Role -> Card -> Map a b -> Card"}
  (fn [role modifier _]
    [role modifier]))

;;-----------------------
;; Builder modifiers
;; - Picker: cost is 1 less for all targets.
;; - Smithy: cost is 1 less if a production building.
;; - Poorhouse: take an extra CARD after building a production building if 0 or 1 targets remaining in the hand.
;; - Black market: use up to 2 goods to offset cost of any building.
;; - Carpenter: take an extra card after building a violet building.
;; - Quarry: cost is 1 less if a violet building.
;; - Library: cost-2 for all buildings.

(defmethod modify [:build-cost :picker]
  [_ _ target]
  "Picker: cost is 1 less for all cards."
  (update target :cost dec))

(defmethod modify [:build-cost :smithy]
  [_ modifier target]
  "Smithy: cost is 1 less if a production building."
  (if (#{:production} (:kind target))
    (update target :cost dec)
    target))

(defmethod modify [:build-cost :quarry]
  [_ modifier target]
  "Quarry: cost is 1 less if a violet building."
  (if (#{:violet} (:kind target))
    (update target :cost dec)
    target))

(defmethod modify [:build-take :poorhouse]
  [_ modifier target]
  "Poorhouse: take an extra card after building a production building if 0 or 1 targets remaining in the hand."
  (if (#{:production} (:kind target))
    (update-in target [:take] inc)
    target))

(defmethod modify [:build-take :carpenter]
  [_ modifier target]
  "Carpenter: take an extra card after building a violet building."
  (if (#{:violet} (:kind target))
    (update-in target [:take] inc)
    target))

;;-----------------------
;; Catch-all
(defmethod modify :default
  [_ _ target]
  "Default case, return the target unchanged."
  target)

;;-----------------------
(defn modify-hand
  "Run modifications over all the hand cards based on the modifier cards."
  {:type "Role -> [Card] -> [Card] -> [CardX]"}
  [role modifiers hand-cards]
  (let [handx (map (partial get all-cards) hand-cards)]
    (map
     (fn [card]
       (reduce (fn [acc elt] (modify role elt acc))
               card
               modifiers))
     handx)))

;; The End