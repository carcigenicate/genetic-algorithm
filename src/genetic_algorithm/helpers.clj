(ns genetic-algorithm.helpers
  (:require [helpers.general-helpers :as g]))

(defn random-index [coll rand-gen]
  (g/random-int 0 (count coll) rand-gen))