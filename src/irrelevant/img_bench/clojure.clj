(ns irrelevant.img-bench.clojure
  (:require [helpers.general-helpers :as g])
  (:import [java.awt.image BufferedImage ImageObserver DataBufferInt]
           [java.awt Color Canvas Frame Graphics2D]))

(def width 640)
(def height 480)
(def cs [16711680 167123])
(defonce image (BufferedImage. width height BufferedImage/TYPE_INT_RGB))
(defonce pixels (-> image .getRaster .getDataBuffer .getData))

(defonce canvas (doto (proxy [Frame] []
                        (update [g]
                          (.paint this g))
                        (paint [^Graphics2D g]
                          (.drawImage g, ^BufferedImage image, 0, 0 nil)))
                  (.setSize width height)
                  (.setBackground Color/black)
                  (.setFocusableWindowState false)
                  (.setVisible true)))

(defmacro for-loop [[sym init check change :as params] & steps]
  `(loop [~sym ~init value# nil]
     (if ~check
       (let [new-value# (do ~@steps)]
         (recur ~change new-value#))
       value#)))

(println "Starting...")

(g/bench
  (for-loop [k 0 (< k 2) (inc k)]
     (for-loop [c 0 (< c 2) (inc c)]
      (for-loop [i 0 (< i width) (inc i)]
        (for-loop [j 0 (< j height) (inc j)]
          (aset-int pixels (+ i (* j width)) (get cs c))))
      (.repaint ^Canvas canvas))))

(println "Next test...")
#_
(g/bench
  (doseq [k (range 2)]
    (doseq [c (range 2)]
      (doseq [i (range width)]
        (doseq [j (range height)]
          (aset-int pixels (+ i (* j width)) (get cs c))))
      (.repaint ^Canvas canvas))))