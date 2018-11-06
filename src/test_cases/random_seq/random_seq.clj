(ns test-cases.random-seq.random-seq
  (:require [helpers.general-helpers :as g]))

(def rand-gen (g/new-rand-gen))

(def seq-length 100)
(def t-max 1000)
(def target-seq (vec (repeatedly seq-length #(g/random-int 0 t-max rand-gen))))

(defn fit-func [genes]
  (->> genes
    (map #(Math/abs ^long (- % %2)) target-seq)
    (apply +)))