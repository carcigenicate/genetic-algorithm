(ns genetic-algorithm.simple-genes
  (:require [helpers.general-helpers :as g]

            [genetic-algorithm.helpers :as gah]))

(defn sequence-of [filler seq-length]
  (vec (repeat seq-length filler)))

(defn random-seqeunce-of [gene-f seq-length]
  (vec (repeatedly seq-length gene-f)))

(defn mutate-random [raw-genes gene-set rand-gen]
  (assoc raw-genes (gah/random-index raw-genes rand-gen)
               (g/random-from-collection gene-set rand-gen)))

(defn gen-cross-points [seq-length cross-chance rand-gen]
  (filter (fn [_] (g/random-perc cross-chance rand-gen))
          (range seq-length)))

(defn cross
  "Swaps the genes at the cross-point indices, and returns the new genes."
  [raw-genes other-raw-genes cross-points]
  (if (empty? cross-points)
    raw-genes
    (->> cross-points
         (map other-raw-genes)
         (interleave cross-points)
         (apply assoc raw-genes))))

#_ ; Very inefficient
(defn running-cross [genes other-genes cross-points]
  (let [cp-set (set cross-points)]
    (->> (map vector (range) genes other-genes)

      (reduce (fn [[g1? acc] [i g1 g2]]
                (let [g1?' (if (cp-set i) (not g1?) g1?)]
                  [g1?' (conj acc (if g1?' g1 g2))]))
              [true []])

      (second))))

(defn randomly-cross [raw-genes other-raw-genes cross-chance rand-gen]
  (let [cross-points (gen-cross-points (count raw-genes) cross-chance rand-gen)]
    (cross raw-genes other-raw-genes cross-points)))

(defn maybe-mutate-random [raw-genes gene-set mutate-chance rand-gen]
  (if (g/random-perc mutate-chance rand-gen)
    (mutate-random raw-genes gene-set rand-gen)
    raw-genes))

