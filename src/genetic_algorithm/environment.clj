(ns genetic-algorithm.environment
  (:require [genetic-algorithm.judged-population :as jp]
            [helpers.general-helpers :as g]
            [genetic-algorithm.settings :as gs]))

(defrecord Environment [population settings])


