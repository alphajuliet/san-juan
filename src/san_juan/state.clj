;; state.clj
;; andrewj 2019-12-11

(ns san-juan.state)

;;-----------------------
;; Utilities

(defn enumerate-cards
  "For each card in the given deck, add `count` copies of `name`, and concatenate into a single list."
  {:type "∀ a. Map a Integer -> [a]"}
  [d]
  (reduce
   (fn [accum element]
     (into accum (repeat (:count element) (:name element))))
   [] d))

;;-----------------------
;; Define an extended card with all its attributes
;; ^{:type "CardX"}
(defrecord CardX [name   ;; name of the card :: CardX
                  kind   ;; production or violet card
                  cost   ;; build cost
                  vp     ;; base victory points
                  count  ;; number of cards with this name
                  ])

(def all-cards
  ;; ^{:type "Map Card CardX"}
  (into {} (map (juxt :name identity))
        [; Production buildings
         (->CardX :indigo-plant :production 1 1 10)
         (->CardX :sugar-mill :production 2 1 8)
         (->CardX :tobacco-storage :production 3 2 8)
         (->CardX :coffee-roaster :production 4 2 8)
         (->CardX :silver-smelter :production 5 3 8)
         ; Violet buildings
         (->CardX :smithy :violet 1 1 3)
         (->CardX :gold-mine :violet 1 1 3)
         (->CardX :archive :violet 1 1 3)
         (->CardX :poorhouse :violet 2 1 3)
         (->CardX :black-market :violet 2 1 3)
         (->CardX :trading-post :violet 2 1 3)
         (->CardX :market-stand :violet 2 1 3)
         (->CardX :well :violet 2 1 3)
         (->CardX :crane :violet 2 1 3)
         (->CardX :chapel :violet 3 2 3)
         (->CardX :tower :violet 3 2 3)
         (->CardX :aqueduct :violet 3 2 3)
         (->CardX :carpenter :violet 3 2 3)
         (->CardX :prefecture :violet 3 2 3)
         (->CardX :market-hall :violet 4 2 3)
         (->CardX :quarry :violet 4 2 3)
         (->CardX :library :violet 5 3 3)
         (->CardX :statue :violet 3 3 3)
         (->CardX :victory-column :violet 4 4 3)
         (->CardX :hero :violet 5 5 3)
         (->CardX :guild-hall :violet 6 0 2)
         (->CardX :city-hall :violet 6 0 2)
         (->CardX :triumphal-arch :violet 6 0 2)
         (->CardX :palace :violet 6 0 2)]))

(defn extract-vals
  "Extract only the requested keys k from map m."
  {:type "∀ a. [a] -> Map a b -> Map a b"}
  [k m]
  (map #(select-keys % k) m))

;;-----------------------
;; Default player state
(defrecord Player [area    ;; played buildings :: [CardX]
                   hand    ;; hand cards :: [CardX]
                   goods   ;; produced goods but not yet traded :: [Integer]
                   chapel  ;; chapel cards :: [CardX]
                   vp      ;; number of VPs :: Integer >= 0
                   ])

(def empty-player
  (map->Player {:area []
                :hand []
                :goods []
                :chapel []
                :vp 0}))

(def all-roles
  ;; ^{:type "Set Role"}
  "All available roles."
  #{:builder :producer :trader :councillor :prospector})

;;-----------------------
;; Define the game state
(defrecord State [deck     ;; the deck of unused cards :: [CardX]
                  discards ;; discard pile :: [CardX]
                  player   ;; player hand and their play area :: [Player]
                  turn     ;; current turn number :: Integer >= 0
                  roles    ;; available roles :: #{Role}
                  ])

(defn nplayers
  "Number of players in the game."
  {:type "State -> Integer"}
  [state]
  (count (:player state)))

(defn empty-state
  "Empty game state"
  {:type "Integer -> State"}
  [nplayers]
  {:pre [(<= 2 nplayers 4)]}
  (map->State {:deck (enumerate-cards (vals all-cards))
               :discards []
               :player (vec (repeat nplayers empty-player))
               :turn 0
               :roles all-roles}))

;; The End