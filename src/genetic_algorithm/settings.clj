(ns genetic-algorithm.settings)

; TODO: Should sequence-length be a property?
; TODO:  It's only needed when the population is first created.

(defrecord Settings [mutate-chance cross-chance gene-f keep-perc sort-f pop-size sequence-length fitness-f])