;; action.clj
;; andrewj 2019-12-12

(ns san-juan.action
  (:require [san-juan.state :refer :all]
            [san-juan.hash-calc :as h]
            [lentes.core :as l]
            [random-seed.core :as r]))

;;-----------------------
;; Utilities
(defn removev
  "Remove `elt` from vector `v`."
  [v elt]
  (filterv (complement #{elt}) v))

;;-----------------------
(defn pick-role
  "A player `p` picks a role `r`."
  ;; Role -> Integer -> State -> State
  [r p state]
  (->> state
       (l/over (l/key :roles) #(disj % r))
       (l/put (comp (l/key :player) (l/nth p) (l/key :role)) r)))

;;-----------------------
(defn play-card
  "Play a card from a hand to an area."
  ;; Card -> Integer -> State -> State
  [card p state]
  (->> state
       (l/over (_player_hand p card) dec)))

;;-----------------------
(defn random-card
  "Pick a random card from a given pile."
  ;; Map Card Integer -> Card
  [cards]
  (first (shuffle cards)))

(defn move-card
  "Move a card from one pile to another."
  ;; Card -> Lens -> Lens -> State -> State
  [card _src _dest state]
  (->> state
       (l/over _src #(removev % card))
       (l/over _dest #(conj % card))))

;;-----------------------
(defn deal-card
  "Deal a card from the deck to player `p`'s hand."
  ;; deal-card :: Integer -> State -> State
  [p state]
  (let [card (random-card (:deck state))]
    (move-card card _deck (_player p :hand) state)))

(defn deal-n-cards
  "Deal `n` random cards from the deck to the hand of player `p`."
  ;; deal-n-cards :: Integer -> State -> State
  [n p state]
  (reduce (fn [st _] (deal-card p st))
          state
          (range n)))

;;-----------------------
(defn do-all-players
  "Apply reducing function `f` to all players."
  ;; (State -> Integer -> State) -> State -> State
  [f state]
  (let [nplayers (count (:player state))]
    (reduce f state (range nplayers))))

;;-----------------------
(defn init-game
  "Initialise the game with `n` players."
  ;; Integer -> State
  [n]
  (->> (empty-state n)
       ;; Place an indigo plant in everyone's area
       (do-all-players
        (fn [st p]
          (move-card :indigo-plant _deck (_player p :area) st)))

       ;; Deal 4 cards to each player's hand
       (do-all-players
        (fn [st p]
          (deal-n-cards 4 p st)))))

;;-----------------------
#_(defmulti play-role
    "A player picks and executes one of the roles."
    :role ;; dispatch value
    [p state])

(defn build
  "A player builds in their area, using zero or more designated cards to pay for it."
  ;; Integer -> Card -> Seq Card -> State -> State
  [p building payment-cards state]
  ;; Pre-conditions:
  ;; - Must have card in hand
  ;; - If violet, the card cannot already have been played.
  {:pre [(some #{building} (l/focus (_player p :hand) state))
         (or (= :production (card-val :kind building))
             (complement (some #{building} (l/focus (_player p :area) state))))]}
  
  (move-card building (_player p :hand) (_player p :area) state))


(def s0 (init-game 4))

;; The End))