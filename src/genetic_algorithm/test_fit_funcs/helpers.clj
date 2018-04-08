(ns genetic-algorithm.test-fit-funcs.helpers)

(defn range-error [n c-min c-max]
  (if (<= c-min n c-max)
    0
    (if (< n c-min)
      (- c-min n)
      (- n c-max))))