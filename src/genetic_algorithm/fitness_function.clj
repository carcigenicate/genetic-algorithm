(ns genetic-algorithm.fitness-function
  (:import (clojure.lang IFn)))

(defrecord Fitness-Function [fitness-f pure?]
  IFn
  (invoke [this sequence] ((:fitness-f this) sequence)))

(defn fit-func [pure? f]
  (->Fitness-Function f pure?))