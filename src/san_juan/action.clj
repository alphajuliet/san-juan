;; andrewj 2019-12-12
;; action.clj

(ns san-juan.action
  (:require [san-juan.state :refer :all]
            [san-juan.modifier :refer :all]
            [random-seed.core :as r])
  (:refer-clojure :exclude [rand rand-int rand-nth]))

;;-----------------------
;; Utilities
(defn removev
  "Remove at most one instance of `elt` from a vector."
  {:type ∀ a. Vector a -> a -> Vector a}
  [coll elt]
  (let [i (.indexOf coll elt)]
    (if (neg? i)
      coll
      ; else
      (into (subvec coll 0 i) (subvec coll (inc i))))))

;;-----------------------
(defn pick-role
  "Player `p` picks a role `r`."
  {:type Role -> Integer -> State -> State}
  [r p state]
  (-> state
      (update-in [:roles] #(disj % r))
      (assoc-in [:player p :role] r)))

;;-----------------------
(defn random-card
  "Pick a random card from a given pile."
  {:type Seq Card -> Card}
  [cards]
  (r/rand-nth cards))

(defn move-card
  "Move a card from one pile to another."
  {:type ∀ a. Card -> Vector a -> Vector a -> State -> State}
  [card _src _dest state]
  (-> state
      (update-in _src #(removev % card))
      (update-in _dest #(conj % card))))

;;-----------------------
(defn deal-card
  "Deal a card from the deck to player `p`'s hand."
  {:type Integer -> State -> State}
  [p state]
  (let [card (random-card (:deck state))]
    (move-card card [:deck] [:player p :hand] state)))

(defn deal-n-cards
  "Deal `n` random cards from the deck to the hand of player `p`."
  {:type Integer -> State -> State}
  [n p state]
  (reduce (fn [st _] (deal-card p st))
          state
          (range n)))

;;-----------------------
(defn do-all-players
  "Apply reducing function `f` to all players."
  {:type (State -> Integer -> State) -> State -> State}
  [f state]
  (let [nplayers (count (:player state))]
    (reduce f state (range nplayers))))

;;-----------------------
(defn init-game
  "Initialise the game with `n` players and random seed."
  {:type Integer -> Integer -> State}
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
                   action  ;; type of action
                   build   ;; card to build (Builder)
                   cost    ;; cost to build the card (Builder)
                   pay     ;; cards that will be used to pay (Builder)
                   take    ;; how many cards to take (all)
                   sell    ;; cards to sell (Trader)
                   produce ;; cards to produce (Producer)
                   keep    ;; cards to keep (Councillor)
                   ])

;;-----------------------
(defn build-modify-costs
  "Modify the costs of the hand cards based on the area cards."
  {:type ∀ a b. Integer -> Boolean -> State -> Map a b}
  [player picker? state]
  {:pre [(boolean? picker?)
         (<= 0 player (dec (count (:player state))))]}

  (let [hand-cards (get-in state [:player player :hand])
        area-cards (get-in state [:player player :area])
        a (if picker? (conj area-cards :picker) area-cards)]
    (modify-hand :build-cost a hand-cards)))

(defn build-filter-cards
  "Filter the affordable cards, i.e. those with a cost less than the remaining hand cards."
  {:type ∀ a b. Map a b -> Map a b}
  [hand-cards]
  (filter #(<= (:cost %) (dec (count hand-cards))) hand-cards))


(defn build-move-cards
  "A player builds in their area, using zero or more designated hand cards and goods cards to pay for it."
  {:type ∀ a b. Map a b -> State}
  [{:keys [player build pay take] :as action} state]

  ;; Pre-conditions:
  ;; - Must have card in hand
  ;; - If violet, the card cannot already have been played.
  {:pre [(some #{build} (get-in state [:player player :hand]))
         (or (= :production (:kind build))
             (complement (some #{build} (get-in state [:player player :area]))))]}

  (let [_hand [:player player :hand]
        _area [:player player :area]
        hand-cards (get-in state _hand)]
    (as-> state ss
      (move-card (:build action) _hand _area ss)
      (reduce (fn [st card] (move-card card _hand [:discards] st))
              ss pay)
      (deal-n-cards take player ss))))


;;-----------------------
#_(defn produce
    "A player produces goods."
    {:type }
    [{:keys [player produce] :as action} state]

    (let [(prod-cards (-> (get-in state [:player player :area])
                          (filter #(= :production (card-val :kind %)))))
          act (apply-modifiers player action state)]
      (as-> state ss
        ())))

;;-----------------------
(defn trade [])

;;-----------------------
(defn councillor [])

;;-----------------------
(defn prospect [])

;;-----------------------
(def s0
  "Initial state with 4 players and a seed of 0."
  (init-game 4 0))

(def s1 
  "s0 plus a Smithy card in player 0's area."
  (move-card :smithy [:deck] [:player 0 :area] s0))
;; The End))