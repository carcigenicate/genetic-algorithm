(ns genetic-algorithm.judged-population
  (:require [genetic-algorithm.judged-genes :as jg]
            [helpers.general-helpers :as g]
            [genetic-algorithm.simple-genes :as gag]
            [genetic-algorithm.settings :as gs]))

(defn unjudged-pop [pop]
  (mapv jg/unjudged-genes pop))

(defn population-of
  ([pop-size sequence-length gene-f]
   (unjudged-pop
     (repeatedly pop-size #(gag/sequence-of gene-f sequence-length))))

  ([{:keys [pop-size gene-f sequence-length] :as settings}]
   (population-of pop-size sequence-length gene-f)))

(defn produce-raw-child [j-pop cross-chance rand-gen]
  (gag/randomly-cross (:genes (g/random-from-collection j-pop rand-gen))
                      (:genes (g/random-from-collection j-pop rand-gen))
                      cross-chance
                      rand-gen))

(defn maybe-mutate-children [raw-children gene-f mutate-chance rand-gen]
  (mapv #(gag/maybe-mutate-random % gene-f mutate-chance rand-gen) raw-children))

(defn- thin-judged-population
  "Returns a sorted population with the least fit sequences removed.
  Least fit is determied by the sort-f. Either < or >.
  Most fit sequence is first."
  [j-pop sort-f keep-perc]
  (let [sorted (sort-by :score sort-f j-pop)
        remove-i (* keep-perc (count j-pop))]
    (subvec (vec sorted) 0 remove-i)))

; TODO: Use maybe or force judge? Add setting.
(defn- judge-population [pop fitness-f]
  (mapv #(jg/maybe-judge % fitness-f) pop))

(defn- repopulate [sorted-j-pop pop-size gene-f cross-chance mutate-chance rand-gen]
  (let [n-needed (- pop-size (count sorted-j-pop))
        raw-children (repeatedly n-needed
                                 #(produce-raw-child sorted-j-pop cross-chance rand-gen))]
    (into sorted-j-pop
          (unjudged-pop
            (maybe-mutate-children raw-children gene-f mutate-chance rand-gen)))))

(defn advance [j-pop settings rand-gen]
  (let [{:keys [fitness-f sort-f keep-perc pop-size
                cross-chance gene-f mutate-chance]} settings]
    (-> j-pop
        (judge-population fitness-f)
        (thin-judged-population sort-f keep-perc)
        (repopulate pop-size gene-f cross-chance mutate-chance rand-gen))))

(defn advance-by [population settings iterations rand-gen]
  (nth
    (iterate #(advance % settings rand-gen) population)
    iterations))
#_
(defn test-proc []
  (let [r (g/new-rand-gen 99)
        pop [[0 0 0 0]
             [1 2 3 4]
             [5 6 7 8]
             [1 3 5 7]
             [2 4 6 8]
             [1 9 3 7]
             [2 0 3 9]]

        uj-pop (unjudged-pop pop)
        j-pop (judge-population uj-pop (partial apply +))

        thin-pop (thin-judged-population j-pop < 0.5)

        repop (repopulate thin-pop 10 0.5 r)]
    repop))