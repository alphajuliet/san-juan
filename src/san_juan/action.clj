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
(defn move-card
  "Move a card from one pile to another."
  {:type "∀ a. Card -> Seq a -> Seq a -> State -> State"}
  [card _src _dest state]
  (-> state
      (update-in _src #(removev % card))
      (update-in _dest #(conj % card))))

;;-----------------------
(defn deal-card
  "Deal a random card from the deck to player `p`'s hand."
  {:type "Integer -> State -> State"}
  [p state]
  (let [card (r/rand-nth (:deck state))]
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
  (reduce f state (range (nplayers state))))

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
;; Builder role
;;
;; Steps:
;; 1. Modify the costs of the hand cards based on the area card, or if this player picked the role.
;; 2. Filter the hand cards based on what is affordable.
;; 3. Build and pay with hand cards, and possibly goods cards (via the :black-market card)
;; 4. Take 0 or 1 cards from the deck, based on area cards

(defn builder-modify-costs
  "Modify the costs of the hand cards based on the area cards."
  {:type "Integer -> Boolean -> State -> [CardX]"}
  [player picker? state]
  {:pre [(boolean? picker?)
         (<= 0 player (dec (count (:player state))))]}

  (let [hand-cards (get-in state [:player player :hand])
        area-cards (get-in state [:player player :area])
        a (if picker? (conj area-cards :picker) area-cards)]
    (modify-hand :build-cost a hand-cards)))

(defn builder-filter-cards
  "Filter the affordable cards, i.e. those with a cost less than the remaining hand cards."
  {:type "[CardX] -> [CardX]"}
  [hand-cards]
  (filter #(<= (:cost %) (dec (count hand-cards))) hand-cards))

;; TODO
(defn builder-available-goods
  "Return the available goods that can be used for payment with the :black-market card."
  []
  ())

(defn builder-take-cards
  "Take 0 or 1 card after building."
  {:type "[Card] -> State -> State"}
  [area-cards hand-card]
  (reduce (fn [acc elt] (modify :build-take elt acc)) {:take 0} area-cards))

(defn builder-move-cards
  "A player builds in their area, using zero or more designated hand cards and goods cards to pay for it."
  {:type "∀ a b. Map a b -> State -> State"}
  [{:keys [player build-card payment-cards take-number] :as action} state]

  ;; Pre-conditions:
  ;; - Must have card in hand
  ;; - If violet, the card cannot already have been played.
  {:pre [(some #{build-card} (get-in state [:player player :hand]))
         (or (= :production (:kind build-card))
             (complement (some #{build-card} (get-in state [:player player :area]))))]}

  (let [_hand [:player player :hand]
        _area [:player player :area]
        hand-cards (get-in state _hand)]
    (as-> state <>
      (move-card build-card _hand _area <>)
      (reduce (fn [st card] (move-card card _hand [:discards] st))
              <> payment-cards)
      (deal-n-cards take-number player state))))

(defn builder
  "Play a builder turn with a given policy for which card to build and which cards or goods to pay with."
  {:type "Integer -> Boolean -> Policy -> Policy -> State -> State"}
  [player picker? policy-build state]
  (as-> state <>
       (builder-modify-costs player picker? <>)
       (builder-filter-cards <>)
       ; (builder-available-goods <> state)
       (policy-build <> state)
       (builder-move-cards <>)
       (builder-take-cards <>)))

;;-----------------------
#_(defn produce
    "A player produces goods."
    {:type ""}
    [{:keys [player produce] :as action} state]

    (let [(prod-cards (-> (get-in state [:player player :area])
                          (filter #(= :production (card-val :kind %)))))
          act (apply-modifiers player action state)]
      (as-> state <>
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