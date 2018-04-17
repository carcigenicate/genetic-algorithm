(ns linear-generator.fit-func-generator)

(defn range-error
  "Returns how out of bounds n is from [c-min c-max]. Bounds are inclusive.
c-min defaults to 0 if not supplied."
  ([c-min n c-max]
   (cond
     (<= c-min n c-max) 0
     (> c-min n) (- c-min n)
     :else (- n c-max)))

  ([n c-max]
   (range-error 0 n c-max)))

(defn total-range-error
  "Sums the total range error defined by each range-vec.
  Each vec must be a seq of 2 or 3 items, representing either [range-min n range-max],
    or [n range-max] which assumes a min of 0.
  Delegates to range-error."
  [& range-vecs]
  (->> range-vecs
       (map #(apply range-error %))
       (apply +)))

(defn evaluate-problem
  "Returns a fitness value based on the profit and constraint functions, and the var values via var-map.
  Constraint-mult multiplies the constarint error to make up for the profit function overcoming constraint error.
   Should be greater than the largest profit multiplier in the profit function."
  [profit-f error-f var-map constraint-mult]
  (+ (profit-f var-map)
     (- (* constraint-mult (error-f var-map)))))