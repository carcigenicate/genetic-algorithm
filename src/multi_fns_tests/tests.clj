(ns multi-fns-tests.tests
  (:require [clojure.set :as set]))

(defn common-type [coll]
  (when (apply = (map type coll))
    (type (first coll))))

(defn common-supers [coll]
  (reduce (fn [acc-super v]
            (let [v-supers (set (supers (type v)))]
              (println acc-super v-supers)
              (cond
                (= (type v) acc-super) acc-super
                (v-supers acc-super) (v-supers acc-super)
                :else (reduced nil))))
          (type (first coll))
          coll))

(defn common-supers [coll]
  (let [supers-set #(set (supers (type %)))]
    (reduce (fn [acc-supers x]
              (set/intersection acc-supers (supers-set x)))
            (supers-set (first coll))
            coll)))

(defn common-supers2 [coll]
  (let [supers-set #(set (supers (type %)))]
    (->> coll
         (map supers-set)
         (reduce set/intersection (supers-set (first coll))))))

(defn inheritance-depth
  "Returns how far from Object a class has inherited from."
  [^Class c]
  (if (= c Object)
    0
    (inc (inheritance-depth (or (.getSuperclass c) Object)))))

(defn most-specific-super
  "Finds the class with the longest inheritence chain, or nil if there there's a tie or the coll is empty."
  [classes]
  (let [most-specific (->> classes
                           (distinct)
                           (map #(vector % (inheritance-depth %)))
                           (sort-by second >)
                           (partition-by second)
                           (first))]

    (println most-specific)

    (when (= (count most-specific) 1)
      (ffirst most-specific))))

(defmulti foo (fn [& args] (->> args
                                (map type)
                                (most-specific-super))))

(defmethod foo String
  [& args]
  (println "All strings"))

(defmethod foo Long
  [& args]
  (println "All longs"))

(defmethod foo Number
  [& args]
  (println "All numbers"))

(defmethod foo :default
  [& args]
  (println "Default"))