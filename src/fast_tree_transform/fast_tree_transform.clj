(ns fast-tree-transform.fast-tree-transform
  (:require [fast-tree-transform.test-data :as td]
            [fast-tree-transform.test-data-gen :as tdg]

            [clojure.walk :as w]

            [criterium.core :as c]))

(def default-price 100)

(defn prime? [n]
  (not
    (or (zero? n)
        (some #(zero? (rem n %)) (range 2 n)))))

(defn nth-prime [n]
  (nth (filter prime? (range))
       n))

(defn expensive-transform [e]
  (if (number? e)
    (nth-prime default-price)
    e))

; ----- Simple usage without any parallel aspect
(defn transform-data [nested-map]
  (w/postwalk expensive-transform nested-map))

; ----- Puts each call in a future so it's run in a thread pool
(defn future-transform [e]
  (if (number? e)
    (future (expensive-transform e))
    e))

; ----- The second pass to resolve each future
(defn resolve-transform [e]
  (if (future? e)
    @e
    e))

; ----- Tie them both together
(defn future-transform-data [nested-map]
  (->> nested-map
      (w/postwalk future-transform)
      (w/postwalk resolve-transform)))

(defn bench [d]
  (c/quick-bench
    (transform-data d))

  (Thread/sleep 5000)

  (c/quick-bench
    (future-transform-data d)))