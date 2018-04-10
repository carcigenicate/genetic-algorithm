(ns genetic-algorithm.gene-f-helpers
  (:require [helpers.general-helpers :as g]))

(defn double-range-gen
  "Returns a 0-arity function that returns a random double.
  max-n is exclusive."
  [min-n max-n rand-gen]
  (fn [] (g/random-double min-n max-n rand-gen)))

(defn long-range-gen
  "Returns a 0-arity function that returns a random long.
  max-n is exclusive."
  [min-n max-n rand-gen]
  (fn [] (g/random-int min-n max-n rand-gen)))

(defn from-coll-gen
  "Returns 0-arity function that returns a random element of the collection."
  [coll rand-gen]
  (fn [] (g/random-from-collection coll rand-gen)))