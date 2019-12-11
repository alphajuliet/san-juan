;; state.clj
;; andrewj 2019-12-11

(ns san-juan.state
  #_(:require [clojure.pprint :as pp]))

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
                   vp
                   is-governor])

;;-----------------------
(defn empty-state
  "Game state"
  [nplayers]
  {:pre [(<= 2 nplayers 4)]}
  {:deck (enumerate-cards all-cards)
   :player (vec (repeat nplayers (->Player [] [] [] 0 false)))
   :role nil})

;;-----------------------
;; Accessors

(defn player
  "Access player n in a state."
  [n state]
  {:pre [(<= 0 n 3)]}
  (-> state
      :player
      (nth n)))

;; The End