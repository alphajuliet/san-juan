;; game.clj
;; andrewj 2019-12-15

(ns san-juan.game
  (:require [san-juan.state :refer :all]
            [san-juan.action :refer :all]))


(defn builder-options
  [])

;; Modifier cards for Builder
  ;; - Smithy: cost is 1 less if a production building.
  ;; - Poorhouse: take an extra card after building a production building if 0 or 1 cards remaining in the hand.
  ;; - Black market: use up to 2 goods to offset cost of any building.
  ;; - Carpenter: take an extra card after building a violet building.
  ;; - Quarry: cost is 1 less if a violet building.
  ;; - Library: cost-2 for all buildings.


;; The End