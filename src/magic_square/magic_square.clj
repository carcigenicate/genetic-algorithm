(ns magic-square.magic-square
  (:require [helpers.general-helpers :as g]))

(defn- =?
  ([] nil)
  ([arg] arg)
  ([arg & args]
   (reduce #(if (= % %2) % (reduced nil))
           arg
           args)))

(defn- sum [nums]
  (apply + nums))

(defn make-square [width contents]
  (mapv vec
        (partition width contents)))

; ----- Accessors ----- All return a 2D vector of the described parts of the square
(defn- rows [square]
  square)

(defn- cols [square]
  (apply mapv vector square))

; Wrapping in a vector for consistency
(defn- tl-br-diag [square]
  [(mapv get square (range))])

(defn- tr-bl-diag [square]
  [(mapv get square (range (dec (count square)) -1 -1))])

(defn- common-sum-of? [square accessor-f]
  (apply =?
         (map sum (accessor-f square))))

(defn square-sums [square]
  (flatten
    (map #(map sum (% square))
          [rows cols tl-br-diag tr-bl-diag])))

; Eww, and probably slower than it needs to be. There will only ever be 4 sums, so it could probably be hard coded.
(defn sum-error [square]
  (->> square
       (square-sums)
       (sort <)
       ; Get the absolute difference of sums.
       ; TODO: Use partition to make pairs?
       (reduce (fn [[s l] n]
                 [(+ s (if l
                         (Math/abs ^long (- l n))
                         0))
                  n])
               [0 nil])
       (first)))

(defn uniqueness-error [square]
  (let [sl (count square)
        unique-nums (-> square (flatten) (set) (count))]
    (* (Math/abs ^long (- unique-nums (* sl sl)))
       1)))

(defn magic? [square]
  (apply =?
         (map #(common-sum-of? square %)
               [rows cols tl-br-diag tr-bl-diag])))

(def magic-test
  [[2 7 6]
   [9 5 1]
   [4 3 8]])

(def non-magic-test
  (assoc-in magic-test [0 0] 5))