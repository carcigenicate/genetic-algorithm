(ns test-cases.linear-generator.syntax-macro
  (:require [test-cases.linear-generator.fit-func-generator :as fg]))

; TODO: Generalize the number? part
(defn- find-number
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

(defn- min-number [coll]
  (find-number coll 1 (partial apply min)))

(defn- replace-select-keys-with-call
  "Replaces all keywords present in key-set in the nested coll with (:keyword map-sym)."
  [coll key-set map-sym]
  (let [key-set' (set key-set)]
    (->> coll
         (map (fn [e]
                (cond
                  (and (keyword? e) (key-set' e)) (list e map-sym)
                  (coll? e) (replace-select-keys-with-call e key-set' map-sym)
                  :else e)))

         (apply list))))

(defn- process-profit-expr
  "Returns a processed profit function from the given profit expression."
  [profit-expr var-keys map-sym]
  `(fn [~map-sym]
     ~(replace-select-keys-with-call profit-expr var-keys map-sym)))

; TODO: Throw parse error for too many args?
; TODO `resolve` both op syms so backticks can be used in testing?
; TODO: Generalize and allow for more ops? Accept a map of symbol->transformations?
(defn- standardize-op [error-expr]
  (let [correct-op '<=
        [op & args] error-expr]
    (if (= op correct-op)
      error-expr
      (apply list correct-op (reverse args)))))

(defn- process-error-exprs [error-exprs var-keys map-sym]
  (let [procd (->> error-exprs
                   (map #(replace-select-keys-with-call % var-keys map-sym))
                   (map standardize-op)
                   (mapv #(vec (drop 1 %))))]

    `(fn [~map-sym]
       (apply fg/total-range-error ~procd))))

(defn- process-prob-error-exprs
  "Returns a pair of [profit-f-expr error-f-expr]"
  [var-keys profit-expr error-exprs]
  (let [map-sym (gensym 'lp-obj-for-map)
        prof-f (process-profit-expr profit-expr var-keys map-sym)
        cons-f (process-error-exprs error-exprs var-keys map-sym)]

    [prof-f cons-f]))

(defn genes+vars->var-map
  "Accepts a gene sequence and a list of keyword vars, and returns a map mapping each gene to a var."
  [genes vars]
  (into {}
    (map vector vars genes)))

(defn profit-prefix-f [min-max-key]
  (let [second-char (nth (str min-max-key) 2)]
    (case second-char
      \a '+ ; max
      \i '-))) ; min

(defmacro fit-func-for
  "Accepts a linear porgramming-like syntax, and evaluates to a profit-seeking fitness function that accepts a vector
   of genes, and returns the calculated fitness."
  [vars min-max-key profit subject-key & constraints]
  (let [; How much more significant errors are than profit. Arbitrary.
        profit-error-mult 5

        ; Find the largest number in the profit expression so we can make sure the
        ; contraint error overcomes any profit from illegal results.
        ; Assumes only * and + will be used, and use of + and * won't be mixed.
        max-prof-mult (max-number profit) ; Find the
        error-mult (* profit-error-mult max-prof-mult)

        std-prof (list (profit-prefix-f min-max-key) profit)

        [p-expr e-expr] (process-prob-error-exprs (set vars) std-prof constraints)]

    `(fn [genes#]
       (let [var-map# (genes+vars->var-map genes# ~vars)]
         (fg/evaluate-problem ~p-expr ~e-expr var-map# ~error-mult)))))