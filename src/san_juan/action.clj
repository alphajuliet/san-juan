;; action.clj
;; andrewj 2019-12-12

(ns san-juan.action
  (:require [san-juan.state :refer :all]
            [san-juan.modifier :refer :all]
            [random-seed.core :as r])
  (:refer-clojure :exclude [rand rand-int rand-nth]))

;;-----------------------
;; Utilities
(defn removev
  "Remove at most one instance of `elt` from a vector."
  {:type "∀ a. Vector a -> a -> Vector a"}
  [coll elt]
  (let [i (.indexOf coll elt)]
    (if (neg? i)
      coll
      ; else
      (into (subvec coll 0 i) (subvec coll (inc i))))))

;;-----------------------
(defn pick-role
  "Player `p` picks a role `r`."
  {:type "Role -> Integer -> State -> State"}
  [r p state]
  (-> state
      (update-in [:roles] #(disj % r))
      (assoc-in [:player p :role] r)))

;;-----------------------
(defn random-card
  "Pick a random card from a given pile."
  {:type "Seq Card -> Card"}
  [cards]
  (r/rand-nth cards))

(defn move-card
  "Move a card from one pile to another."
  {:type "∀ a. Card -> Vector a -> Vector a -> State -> State"}
  [card _src _dest state]
  (-> state
      (update-in _src #(removev % card))
      (update-in _dest #(conj % card))))

;;-----------------------
(defn deal-card
  "Deal a card from the deck to player `p`'s hand."
  {:type "Integer -> State -> State"}
  [p state]
  (let [card (random-card (:deck state))]
    (move-card card [:deck] [:player p :hand] state)))

(defn deal-n-cards
  "Deal `n` random cards from the deck to the hand of player `p`."
  {:type "Integer -> State -> State"}
  [n p state]
  (reduce (fn [st _] (deal-card p st))
          state
          (range n)))

;;-----------------------
(defn do-all-players
  "Apply reducing function `f` to all players."
  {:type "(State -> Integer -> State) -> State -> State"}
  [f state]
  (let [nplayers (count (:player state))]
    (reduce f state (range nplayers))))

;;-----------------------
(defn init-game
  "Initialise the game with `n` players and random seed."
  {:type "Integer -> Integer -> State"}
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
(defrecord Action [player  ;; player performing the action
                   action    ;; type of action
                   build   ;; card to build (Builder)
                   cost    ;; cost to build the card (Builder)
                   pay     ;; cards that will be used to pay (Builder)
                   take    ;; how many cards to take (all)
                   sell    ;; cards to sell (Trader)
                   produce ;; cards to produce (Producer)
                   keep    ;; cards to keep (Councillor)
                   ])

;;-----------------------
(defn build
  "A player builds in their area, using zero or more designated hand cards and goods cards to pay for it. 
   This function does not check on the costs or applying modifier cards but does check on validity of the card movements."
  {:type "Action -> State"}
  [{:keys [player build pay take] :as action} state]

  ;; Pre-conditions:
  ;; - Must have card in hand
  ;; - If violet, the card cannot already have been played.
  {:pre [(some #{build} (get-in state [:player player :hand]))
         (or (= :production (card-val :kind build))
             (complement (some #{build} (get-in state [:player player :area]))))]}

  (let [_hand [:player player :hand]
        _area [:player player :area]
        hand-cards (get-in state _hand)]
    (as-> state ss
      (move-card (:build action) _hand _area ss)
      (reduce (fn [st card] (move-card card _hand [:discards] st))
              ss pay)
      (deal-n-cards take player ss))))


(defn builder-options
  "Available builder actions for a given player `p`."
  {:type "Integer -> State -> [Action]"}
  [p state]
  (let [hand-cards (get-in state [:player p :hand])
        area-cards (get-in state [:player p :area])
        n (count hand-cards)]
    (for [c hand-cards]
      (let [cost (card-val :cost c) ;; base cost
            action (map->Action {:action :builder
                                 :player p
                                 :build c
                                 :cost cost
                                 :pay []
                                 :take 0})]
        ;; Cumulatively apply the built modifier cards
        (reduce (fn [acc elt] (modify acc elt))
                action
                area-cards)))))

;;-----------------------
(defn produce [])
(defn trade [])
(defn councillor [])
(defn prospect [])

(def s0
  (init-game 4 0))

;; The End))
