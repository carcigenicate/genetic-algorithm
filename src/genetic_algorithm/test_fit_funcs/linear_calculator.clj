(ns genetic-algorithm.test-fit-funcs.linear-calculator
  (:require [genetic-algorithm.fitness-function :as ff]
            [genetic-algorithm.test-fit-funcs.helpers :as ffh]
            [genetic-algorithm.text-ui.linear-generator :as fflg]))

"A calculator company produces a scientific calculator and a graphing calculator. Long-term projections indicate an expected demand of at least 100 scientific and 80 graphing calculators each day. Because of limitations on production capacity, no more than 200 scientific and 170 graphing calculators can be made daily. To satisfy a shipping contract, a total of at least 200 calculators much be shipped each day.

If each scientific calculator sold results in a $2 loss, but each graphing calculator produces a $5 profit, how many of each type should be made daily to maximize net profits?

The solution is 100 scientific calculators and 170 graphing calculators"

(defn constraint-error [x y]
  (+ (ffh/range-error x 80 170)
     (ffh/range-error y 100 200)
     (max 0 (- 200 (+ x y)))))

(defn calc-fitness [[x y]]
  (+ (* x 5)
     (* y -2)
     (* 6 (- (constraint-error x y)))))

(def fit-func (ff/fit-func true calc-fitness))

(def gen-fit-func (ff/fit-func true (fflg/fit-func-for [:s :g]
                                                       :maximize (+ (* :s -2) (* :g 5))
                                                       :subject-to (<= 100 :s 200)
                                                                   (<= 80 :g 170)
                                                                   (<= 200 (+ :s :g) 1e6))))

; Maximize = P(rofit)
; Minimize = C(ost)

#_
(->Linear-Problem (fn [vars] ; Profit
                    (+ (* (:s vars) -2) (* (:g vars) 5)))

                  (fn [vars] ; Constraints
                    (total-range-error
                      [100 (:s vars) 200]
                      [80 (:g vars) 170]
                      [200 (+ (:s vars) (:g vars))])))