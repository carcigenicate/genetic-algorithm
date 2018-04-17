(ns genetic-algorithm.text-ui.main
  (:require [genetic-algorithm.judged-population :as jp]
            [clojure.edn :as edn]
            [clojure.string :as s]
            [genetic-algorithm.settings :as gs]
            [helpers.general-helpers :as g]
            [genetic-algorithm.fitness-function :as ff]
            [genetic-algorithm.gene-f-helpers :as geh]

            [linear-generator.syntax-macro :as fsm]

            [magic-square.ga :as msf]
            [genetic-algorithm.test-fit-funcs.linear-rabbits :as lafflr]
            [genetic-algorithm.test-fit-funcs.linear-calculator :as lafflc]
            [genetic-algorithm.random-seq.random-seq :as rs]))

(def global-rand-gen (g/new-rand-gen))

(defn perc [current target]
  (double (/ current target)))

(defn format-pop [pop]
  (s/join "\n" (map #(into {} %) pop)))

; TODO: Time estimates
(defn main-procedure [pop settings display-every target-fitness max-iterations rand-gen]
  (let [f-perc #(str (* 100 %) "%")]
    (loop [i 0
           acc-pop pop]
      (if (< i max-iterations)
        (let [evolved (jp/advance acc-pop settings rand-gen)
              best-fitness (:score (first evolved))

              i-perc (perc i max-iterations)
              score-perc (perc best-fitness target-fitness)] ; Won't work for error targets

          (when (zero? (rem i display-every))
            (println (str i " " best-fitness " - "
                          (f-perc i-perc) " " (f-perc score-perc) "\n"
                          (format-pop (take 5 evolved)) "\n\n")))
          (if (< best-fitness target-fitness)
            (recur (inc i) evolved)
            evolved))

        acc-pop))))

; -----

(defn- parse-args [raw-args]
  (->> raw-args
       (map #(let [r (edn/read-string %)]
               (if (symbol? r) (str r) r)))
       (partition 2)
       (map vec)
       (into {})))

(defn -main [& args]
  (let [good-args (parse-args args)]
    good-args))

(def target-coll ())

(def test-settings
  (gs/map->Settings {:mutate-chance   0.7
                     :cross-chance    0.3
                     :gene-f          (geh/long-range-gen 0 rs/t-max global-rand-gen)
                     :keep-perc       0.1
                     :sort-f          <
                     :pop-size        5000
                     :sequence-length rs/seq-length
                     :fitness-f       (ff/fit-func true rs/fit-func)}))

(def test-pop (jp/population-of test-settings))

(defn test-proc []
  (first
    (main-procedure test-pop test-settings
                    100 9e99 5e8 global-rand-gen)))

(def parsed-JSON {:id "05d8d404-b3f6-46d1-a0f9-dbdab7e0261f",
                  :date {:date "2015-01-10T19:11:41.000Z"},
                  :total {:GBP 57.45}})

(defn filter-by-date [date jsons]
  (filter #(s/starts-with? (get-in % [:date :date]) date)
          jsons))