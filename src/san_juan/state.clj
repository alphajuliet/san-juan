;; state.clj
;; andrewj 2019-12-11

(ns san-juan.state)

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
(defrecord Card [name   ;; name of the card
                 kind   ;; production or violet card
                 cost   ;; build cost
                 vp     ;; base victory points
                 count  ;; number of cards with this name
                 ])

(def all-cards
  (into {} (map (juxt :name identity))
        [; Production buildings
         (->Card :indigo-plant :production 1 1 10)
         (->Card :sugar-mill :production 2 1 8)
         (->Card :tobacco-storage :production 3 2 8)
         (->Card :coffee-roaster :production 4 2 8)
         (->Card :silver-smelter :production 5 3 8)
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
         (->Card :palace :violet 6 0 2)]))

(defn extract-vals
  "Extract only the requested keys k from map m."
  [k m]
  (map #(select-keys % k) m))

;;-----------------------
;; Default player state
(defrecord Player [area    ;; played buildings :: [Card]
                   hand    ;; hand cards :: [Card]
                   goods   ;; produced goods but not yet traded :: [Integer]
                   chapel  ;; chapel cards :: [Card]
                   vp      ;; number of VPs :: Integer >= 0
                   ])

(def empty-player
  (map->Player {:area []
                :hand []
                :goods []
                :chapel []
                :vp 0}))

(def all-roles
  ;; :type Set Role
  "All available roles."
  #{:builder :producer :trader :councillor :prospector})

;;-----------------------
;; Define the game state
(defrecord State [deck     ;; the deck of unused cards :: [Card]
                  discards ;; discard pile :: [Card]
                  player   ;; player hand and their play area :: [Player]
                  turn     ;; current turn number :: Integer >= 0
                  roles    ;; available roles :: #{Role}
                  ])

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