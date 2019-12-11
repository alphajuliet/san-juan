;; state.clj
;; andrewj 2019-12-11

(ns san-juan.state
  (:require [clojure.pprint :as pp]))

(defrecord Card [name building cost vp count])

(def deck
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


;; The End