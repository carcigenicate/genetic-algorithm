(ns genetic-algorithm.test-fit-funcs.linear-cabinets
  (:require [genetic-algorithm.test-fit-funcs.helpers :as ffh]
            [genetic-algorithm.fitness-function :as ff]))

"You need to buy some filing cabinets. You know that Cabinet X costs $10 per unit, requires six square feet of floor space, and holds eight cubic feet of files. Cabinet Y costs $20 per unit, requires eight square feet of floor space, and holds twelve cubic feet of files. You have been given $140 for this purchase, though you don't have to spend that much. The office has room for no more than 72 square feet of cabinets. How many of which model should you buy, in order to maximize storage volume?"


(def x-cost 10)
(def y-cost 20)

(def x-volume 8)
(def y-volume 12)

(def x-floorspace 6)
(def y-floorspace 8)

(def max-money 140)

(def max-floorspace 72)

(defn constraint-error [x y]
  (let [t-cost (+ (* x x-cost) (* y y-cost))
        t-space (+ (* x x-floorspace) (* y y-floorspace))]
    (+ (max 0 (- t-cost max-money))
       (max 0 (- t-space max-floorspace)))))

(defn calc-fitness [[x y]]
  (+ (* x x-volume)
     (* y y-volume)
     (* (- (inc y-volume)) (constraint-error x y))))

(def fit-func (ff/fit-func true calc-fitness))