(ns genetic-algorithm.test-fit-funcs.linear-rabbits
  (:require [genetic-algorithm.fitness-function :as ff]))

"In order to ensure optimal health (and thus accurate test results), a lab technician needs to feed the rabbits a daily diet containing a minimum of 24 grams (g) of fat, 36 g of carbohydrates, and 4 g of protien. But the rabbits should be fed no more than five ounces of food a day.\nRather than order rabbit food that is custom-blended, it is cheaper to order Food X and Food Y, and blend them for an optimal mix. Food X contains 8 g of fat, 12 g of carbohydrates, and 2 g of protein per ounce, and costs $0.20 per ounce. Food Y contains 12 g of fat, 12 g of carbohydrates, and 1 g of protein per ounce, at a cost of $0.30 per ounce."

(def g-per-oz 28.3495)

(def min-fat 24)
(def min-carb 36)
(def min-prot 4)

(def max-food (* 5 g-per-oz))

(def x-fat 8)
(def x-carb 12)
(def x-prot 2)
(def x-cost (/ 0.2 g-per-oz))

(def y-fat 12)
(def y-carb 12)
(def y-prot 1)
(def y-cost (/ 0.3 g-per-oz))

(defn constraint-error [x y]
  (let [t-food (+ x y)
        t-fat (+ (* x x-fat) (* y y-fat))
        t-carb (+ (* x x-carb) (* y y-carb))
        t-prot (+ (* x x-prot) (* y y-prot))]
    (+ (max 0 (- t-food max-food)) ; 100 - 140 = -40
       (max 0 (- min-fat t-fat)) ; 24 - 8 = 16; 8 - 24 = -16
       (max 0 (- min-carb t-carb))
       (max 0 (- min-prot t-prot)))))

(defn calc-fitness [[x y]]
  (- (- (constraint-error x y))
     (+ (* x x-cost) (* y y-cost))))

(def fit-func (ff/fit-func true calc-fitness))