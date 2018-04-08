(ns genetic-algorithm.judged-genes
  (:require [clojure.set :as set]
            [genetic-algorithm.fitness-function :as ff])
  (:import (clojure.lang Ratio)))

(def unjudged-score ::unjudged)

(defrecord Judged-Genes [score genes])

(defn unjudged-genes [genes]
  (->Judged-Genes unjudged-score genes))

(defn judged? [j-genes]
  (not= unjudged-score (:score j-genes)))

(defn force-judge
  "Updates the genes' fitness score as decided by fitness-f."
  [j-genes fitness-f]
  (assoc j-genes :score (fitness-f (:genes j-genes))))

(defn maybe-judge
  "Only judges j-genes if they haven't been judged before, or if the fitness-function is pure/not known to be pure.
  fitness-f can be a plain function (which is assumed to be impure), or a Fitness-Function."
  [j-genes fitness-f]
  (if (and (:pure? fitness-f) (judged? j-genes))
    j-genes
    (force-judge j-genes fitness-f)))


