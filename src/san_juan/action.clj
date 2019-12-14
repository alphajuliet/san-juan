;; action.clj
;; andrewj 2019-12-12

(ns san-juan.action
  (:require [san-juan.state :refer :all]
            [random-seed.core :as r]))

;;-----------------------
;; Utilities
(defn removev
  "Remove at most one instance of `elt` from vector `v`."
  [v elt]
  (let [i (.indexOf v elt)]
    (if (neg? i)
      v
      ; else
      (into (subvec v 0 i) (subvec v (inc i))))))

;;-----------------------
(defn pick-role
  "Player `p` picks a role `r`."
  ;; Role -> Integer -> State -> State
  [r p state]
  (-> state
      (update-in [:roles] #(disj % r))
      (assoc-in [:player p :role] r)))

;;-----------------------
(defn random-card
  "Pick a random card from a given pile."
  ;; Seq Card -> Card
  [cards]
  (r/rand-nth cards))

(defn move-card
  "Move a card from one pile to another."
  ;; forall a. Card -> Vector a -> Vector a -> State -> State
  [card _src _dest state]
  (-> state
      (update-in _src #(removev % card))
      (update-in _dest #(conj % card))))

;;-----------------------
(defn deal-card
  "Deal a card from the deck to player `p`'s hand."
  ;; deal-card :: Integer -> State -> State
  [p state]
  (let [card (random-card (:deck state))]
    (move-card card [:deck] [:player p :hand] state)))

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
  "Initialise the game with `n` players and random seed."
  ;; Integer -> Integer -> State
  [n seed]
  (r/set-random-seed! seed)
  (->> (empty-state n)
       ;; Place an indigo plant in everyone's area
       (do-all-players
        (fn [st p]
          (move-card :indigo-plant [:deck] [:player p :area] st)))

       ;; Deal 4 cards to each player's hand
       (do-all-players
        (fn [st p]
          (deal-n-cards 4 p st)))))

;;-----------------------
#_(defmulti play-role
    "A player picks and executes one of the roles."
    :role ;; dispatch value
    [p state])

;;-----------------------
(defn build
  "A player builds in their area, using zero or more designated cards to pay for it. 
   This function does not check on the costs or applying modifier cards but does check on validity of the card movements."
  ;; Integer -> Card -> Seq Card -> State -> State
  [p building payment-cards state]

  ;; Pre-conditions:
  ;; - Must have card in hand
  ;; - If violet, the card cannot already have been played.
  {:pre [(some #{building} (get-in state [:player p :hand]))
         (or (= :production (card-val :kind building))
             (complement (some #{building} (get-in state [:player p :area]))))]}

  (as-> state ss
    (move-card building [:player p :hand] [:player p :area] ss)
    (reduce (fn [st card] (move-card card [:player p :hand] [:discards] st))
            ss payment-cards)))


  ;; Modifier cards for Builder
  ;; - Smithy: cost is 1 less if a production building.
  ;; - Poorhouse: take an extra card after building a production building if 0 or 1 cards remaining in the hand.
  ;; - Black market: use up to 2 goods to offset cost of any building.
  ;; - Carpenter: take an extra card after building a violet building.
  ;; - Quarry: cost is 1 less if a violet building.
  ;; - Library: cost-2 for all buildings.




;;-----------------------
(defn produce [])
(defn trade [])
(defn councillor [])
(defn prospect [])

(def s0 (init-game 4 0))

;; The End))