(ns irrelevant.array-tests.vec-vs-array
  (:require [helpers.general-helpers :as g])
  (:import (java.util ArrayList)))

(defn prime? [n]
  (not
    (or (zero? n)
        (some #(zero? (rem n %)) (range 2 n)))))

(defn primes-to-l [n]
  (->> (range 2 n)
       (filter prime?)))

(defn primes-to-v [n]
  (->> (range 2 n)
       (filterv prime?)))

(defn primes []
  (filter prime? (range)))

(defn primes-to-a [n]
  (doseq [arr (make-array Integer/TYPE n)
          i (range n)]
    ()))

