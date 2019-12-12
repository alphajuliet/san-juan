;; action.clj
;; andrewj 2019-12-12

(ns san-juan.action
  (:require [san-juan.state :refer :all]
            [san-juan.hash-calc :as h]
            [lentes.core :as l]
            [random-seed.core :as r]))

;;-----------------------
;; Utilities


;;-----------------------

(defn pick-role
  "A player picks a role."
  [r p state]

  (->> state
       (l/over (l/key :roles) #(disj % r))
       (l/put (comp (l/key :player) (l/nth p) (l/key :role)) r)))

(defn play-card
  "Play a card from a hand to an area."
  [card p state]
  (->> state
       (l/over (_player_hand p card) dec)))

(defn random-card
  "Pick a random card from a given pile."
  [cards]
  (r/rand-nth (h/hash-enumerate cards)))

(defn move-card
  "Move a card from one pile to another."
  [card _src _dest state]
  (->> state
       (l/over _src (partial h/hash-sub {card 1}))
       (l/over _dest (partial h/hash-add {card 1}))))

(defn deal-card
  "Deal a card from the deck to player p's hand."
  ;; deal-card :: Integer -> State -> State
  [p state]
  (let [card (random-card (:deck state))]
    (move-card card _deck (_player p :hand) state)))

(defn deal-n-cards
  "Deal `n` random cards from the deck to player `p`'s hand."
  ;; deal-n-cards :: Integer -> State -> State
  [n p state]
  (reduce (fn [st _]
            (deal-card p st))
          state
          (range n)))

(defn do-all-players
  "Apply a given function `(f st p)` to all players."
  [f state]
  (let [nplayers (count (:player state))]
    (reduce f state (range nplayers))))

(defn init-game
  "Initialise the game with `n` players."
  [n]
  (->> (empty-state n)
   (do-all-players (fn [st p] (move-card :indigo-plant _deck (_player p :area) st)))
   (do-all-players (fn [st p] (deal-n-cards 4 p st)))))

(def s0 (init-game 4))

;; The End))