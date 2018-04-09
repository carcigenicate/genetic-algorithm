(ns genetic-algorithm.text-ui.main
  (:require [genetic-algorithm.judged-population :as jp]
            [clojure.edn :as edn]
            [clojure.string :as s]
            [genetic-algorithm.settings :as gs]
            [helpers.general-helpers :as g]
            [genetic-algorithm.fitness-function :as ff]

            [genetic-algorithm.test-fit-funcs.linear-rabbits :as lafflr]
            [genetic-algorithm.test-fit-funcs.linear-calculator :as lafflc]))

(def global-rand-gen (g/new-rand-gen 99))

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

          (recur (inc i) evolved))

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

(def fitness-func (ff/fit-func true #(apply + %)))

(def test-settings (gs/->Settings 0.5 0.4 (set (range -10 10))
                                  0.2 > 1000 2 lafflc/gen-fit-func))

(def test-pop (jp/random-population (:pop-size test-settings)
                                    (:sequence-length test-settings)
                                    (:gene-set test-settings)
                                    (g/new-rand-gen 99)))

; TODO: CHANGE THE LIST OF POSSIBLE GENES TO A GENERATOR FUNCTION!!!!1!111!!!!!11!!!!!!

(defn test-proc []
  (main-procedure test-pop test-settings
                  50 1e6 1e7 global-rand-gen))