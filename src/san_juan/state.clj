;; state.clj
;; andrewj 2019-12-11

(ns san-juan.state
  (:require [lentes.core :as l]))

;;-----------------------
;; Utilities

(defn enumerate-cards
  "For each card in the given deck, add `count` copies of `name`, and concatenate into a single list."
  [d]
  (reduce
   (fn [accum element]
     (into accum (repeat (:count element) (:name element))))
   [] d))

;;-----------------------
;; Define a card
(defrecord Card [name building cost vp count])

(def all-cards
  [; Production buildings
   (->Card :indigo-plant :production 1 1 10)
   (->Card :sugar-mill :production 2 1 8)
   (->Card :tobacco-storage :production 3 2 8)
   (->Card :coffee-roaster :production 4 2 8)
   (->Card :siver-smelter :production 5 3 8)
   ; Violet buildings
   (->Card :smithy :violet 1 1 3)
   (->Card :gold-mine :violet 1 1 3)
   (->Card :archive :violet 1 1 3)
   (->Card :poorhouse :violet 2 1 3)
   (->Card :black-market :violet 2 1 3)
   (->Card :trading-post :violet 2 1 3)
   (->Card :market-stand :violet 2 1 3)
   (->Card :well :violet 2 1 3)
   (->Card :crane :violet 2 1 3)
   (->Card :chapel :violet 3 2 3)
   (->Card :tower :violet 3 2 3)
   (->Card :aqueduct :violet 3 2 3)
   (->Card :carpenter :violet 3 2 3)
   (->Card :prefecture :violet 3 2 3)
   (->Card :market-hall :violet 4 2 3)
   (->Card :quarry :violet 4 2 3)
   (->Card :library :violet 5 3 3)
   (->Card :statue :violet 3 3 3)
   (->Card :victory-column :violet 4 4 3)
   (->Card :hero :violet 5 5 3)
   (->Card :guild-hall :violet 6 0 2)
   (->Card :city-hall :violet 6 0 2)
   (->Card :triumphal-arch :violet 6 0 2)
   (->Card :palace :violet 6 0 2)])

;; Default player state
(defrecord Player [area
                   hand
                   chapel
                   role
                   vp])

(def all-roles
  #{:builder :producer :trader :councillor :prospector})

;;-----------------------
;; Define the State structure
(defrecord State [deck     ;; the deck of unused cards
                  discards ;; discard pile
                  player   ;; player hand and their play area
                  turn     ;; current turn number
                  roles    ;; role cards remaining in this turn
                  ])

(defn empty-state
  "Empty game state"
  [nplayers]
  {:pre [(<= 2 nplayers 4)]}
  (map->State {:deck (reduce (fn [s e] (into s {(:name e), (:count e)})) {} all-cards)
               :discards {}
               :player (vec (repeat nplayers (map->Player {:area {}
                                                           :hand {}
                                                           :chapel {}
                                                           :role nil
                                                           :vp 0})))
               :turn 0
               :roles all-roles}))

;;-----------------------
;; Lenses

(def _deck (l/key :deck))

(defn _player 
  "Lens to key k of player n."
  [n k]
  (comp (l/key :player) (l/nth n) (l/key k)))

(defn _player_hand 
  "Lens to player p's hand card."
  [n card]
  (comp (_player n :hand) (l/key card)))

;; The End