(ns irrelevant.knights.corrected)

(defn board
  [side-length]
  (->> (range (* side-length side-length)) ; Create all the numbered cells
       (partition side-length) ; Split them into rows
       (mapv vec))) ; Turn the board into a 2D vector

(defn knight-moves
  ([node]
   (knight-moves node nil)) ; Default parent to nil

  ([node parent]
   (let [offsets [6 10 15 17]
         pos (map #(+ node %) offsets) ; Just map over the offsets so you aren't writing them twice.
         neg (map #(- node %) offsets)

         ; I'm reversing neg here so it matches your previous output. It seems valid without,
         ;  it just gives different answers.
         all-neighbours (vec (concat (reverse neg) pos)) ; Then stick the two together.
         possible-neighbours (vec (remove #(or (neg? %) (> % 63)) all-neighbours))]
     {:neighbours possible-neighbours :current-node node :parent-node parent})))

(defn nodes
  ([starting-node]
   (nodes starting-node nil)) ; Default to nil, just like before

  ([starting-node parent]
   (let [movements (knight-moves starting-node)]
     ; You could use a full fn here instead of relying on the function macro
     ;  if you want a better identifier than %
     (mapv #(knight-moves % (knight-moves starting-node parent))
           (:neighbours movements)))))

(defn backtracking
  ([final-node] (backtracking final-node []))
  ([final-node path]
   (if (:parent-node final-node)
     (recur (:parent-node final-node) (merge path (:current-node final-node)))
     (reverse (distinct path)))))

(defn find-path
  ([starting-node goal-node]
   (find-path (knight-moves starting-node) goal-node []))
  ([starting-node goal-node path]
    ;; If not yet arrived at goal
   (if-not (= (:current-node starting-node) goal-node)
     ;; Create a new node with the current-node as parent and add it to path.
     ;; Iterate through all nodes to find the path.
     (let [new-node (nodes (:current-node starting-node) starting-node)
           path (concat path new-node)]
       (recur (first path) goal-node (vec (concat (rest path) new-node))))
     ;; If path found, return the path
     (backtracking starting-node))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (find-path 27 28))

(-main)
; => (27 12 18 28)