(ns magic-square.ga
  (:require [magic-square.magic-square :as ms]
            [genetic-algorithm.gene-f-helpers :as geh]
            [genetic-algorithm.settings :as gs]
            [genetic-algorithm.fitness-function :as ff]
            [helpers.general-helpers :as g]))

(def rand-gen (g/new-rand-gen))

(defn- fit-func [side-length genes]
  (let [sq (ms/make-square side-length genes)]
    (+ (ms/uniqueness-error sq)
       (ms/sum-error sq))))

(defn new-fit-func [side-length]
  (partial fit-func side-length))

(def sq-side-length 4)
(def sq-side-sq (* sq-side-length sq-side-length))

(def sq-settings
  (gs/map->Settings {:mutate-chance   0.7
                     :cross-chance    0.5
                     :gene-f          (geh/long-range-gen
                                        1 (inc sq-side-sq)
                                        rand-gen)
                     :keep-perc       0.6
                     :sort-f          <
                     :pop-size        10000
                     :sequence-length sq-side-sq
                     :fitness-f       (ff/fit-func true
                                                   (new-fit-func sq-side-length))}))