(ns genetic-algorithm.text-ui.linear-generator)

(defrecord Linear-Problem [profit-f constraint-f])

(defn range-error
  ([c-min n c-max]
   (if (<= c-min n c-max)
     0
     (if (< n c-min)
       (- c-min n)
       (- n c-max))))

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
  "Generate a fitness value based on the given profit and constraint functions.
  Constraint-mult multiplies the constarint error to make up for the profit function overcoming constraint error.
  Should be greater than the largest profit multiplier in the profit function."
  [l-prob var-map constraint-mult]
  (let [{:keys [profit-f constraint-f]} l-prob]
    (+ (profit-f var-map)
       (- (* constraint-mult (constraint-f var-map))))))

; TODO: Generalize the number? part
(defn find-number
  "Searches through a nested coll looking for a number as decided by finder-f.
  finder-f is a function taking a list of numbers, and returning a single number.
  Returns default if no numbers are found."
  [coll default finder-f]
  (let [candidates (->> coll
                        (map (fn [e]
                               (cond
                                 (number? e) e
                                 (coll? e) (find-number e default finder-f))))

                        (filter number?))]

    (if (empty? candidates)
      default
      (finder-f candidates))))

(defn max-number [coll]
  (find-number coll 1 (partial apply max)))

(defn min-number [coll]
  (find-number coll 1 (partial apply min)))

(defn replace-keys-with-call [coll map-sym]
  (->> coll
       (map (fn [e]
              (cond
                (keyword? e) (list e map-sym)
                (coll? e) (replace-keys-with-call e map-sym)
                :else e)))

       (apply list)))

(defn process-profit-expr [profit-expr map-sym]
  `(fn [~map-sym]
     ~(replace-keys-with-call profit-expr map-sym)))

; TODO: Throw parse error for too many args?
; TODO `resolve` both op syms so backticks can be used in testing?
; TODO: Generalize and allow for more ops? Accept a map of symbol->transformations?
(defn standardize-op [constraint-expr]
  (let [correct-op '<=
        [op & args] constraint-expr]
    (if (= op correct-op)
      constraint-expr
      (apply list correct-op (reverse args)))))

(defn process-constraint-exprs [constraint-exprs map-sym]
  (let [procd (->> constraint-exprs
                   (map #(replace-keys-with-call % map-sym))
                   (map standardize-op)
                   (mapv #(vec (drop 1 %))))]

    `(fn [~map-sym]
       (apply total-range-error ~procd))))

(defn- linear-problem-expr [profit-expr constraint-exprs]
  (let [map-sym (gensym 'lp-obj-for-map)
        prof-f (process-profit-expr profit-expr map-sym)
        cons-f (process-constraint-exprs constraint-exprs map-sym)]

    `(->Linear-Problem ~prof-f ~cons-f)))

(defn genes+vars->var-map
  "Accepts a gene sequence and a list of keyword vars, and returns a map mapping each gene to a var."
  [genes vars]
  (into {}
        (map vector vars genes)))

(defmacro fit-func-for [vars _ profit _ & constraints]
  (let [profit-error-mult 5 ; How much more significant errors are than profit
        max-prof-mult (max-number profit)
        error-mult (* profit-error-mult max-prof-mult)

        lp (linear-problem-expr profit constraints)]

    `(fn [genes#]
       (let [var-map# (genes+vars->var-map genes# ~vars)]
         (evaluate-problem ~lp var-map# ~error-mult)))))