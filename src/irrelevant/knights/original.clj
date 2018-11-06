(ns irrelevant.knights.original)

(def board
  "- Not used anywhere yet, just shows how the board looks like. -"
  [(vec (range 0 8))
   (vec (range 8 16))
   (vec (range 16 24))
   (vec (range 24 32))
   (vec (range 32 40))
   (vec (range 40 48))
   (vec (range 48 56))
   (vec (range 56 64))])

(defn knight-moves
  "Get all the allowed moves of a knight at `node` position.
  Excludes all the moves that go out of the board."
  ([node]
   (let [a (- node 17)
         b (- node 15)
         c (- node 10)
         d (- node 6)
         e (+ node 6)
         f (+ node 10)
         g (+ node 15)
         h (+ node 17)
         all-neighbours [a b c d e f g h]
         possible-neighbours (vec (remove #(or (neg? %) (> % 63)) all-neighbours))]
     {:neighbours possible-neighbours :current-node node :parent-node nil}))
  ([node parent]
   (let [a (- node 17)
         b (- node 15)
         c (- node 10)
         d (- node 6)
         e (+ node 6)
         f (+ node 10)
         g (+ node 15)
         h (+ node 17)
         all-neighbours [a b c d e f g h]
         possible-neighbours (vec (remove #(or (neg? %) (> % 63)) all-neighbours))]
     {:neighbours possible-neighbours :current-node node :parent-node parent})))

;; (knight-moves 35)

(defn nodes
  "Get all the neighbours and subsequent neighbours of `starting-node`.
  Given a `node`, return a vector of maps with all the possible neighbours and
  subsequent neighbours.
  Given a `node` and a `parent`, return a vector of maps with all the possible
  nodes and subsequent neighbours and save its parent location."
  ([starting-node]
   (let [movements (knight-moves starting-node)]
     ;; For each neighbours of `starting-node`, calculate its subsequent neighbours.
     (vec (for [node (:neighbours movements)]
            (knight-moves node (knight-moves starting-node))))))
  ([starting-node parent]
   (let [movements (knight-moves starting-node)]
     (vec (for [node (:neighbours movements)]
            (knight-moves node (knight-moves starting-node parent)))))))

;; (nodes 35)

(defn backtracking
  "Return the parent location of each subsequent node in `path`."
  ([final-node] (backtracking final-node []))
  ([final-node path]
   (if (:parent-node final-node)
     (recur (:parent-node final-node) (merge path (:current-node final-node)))
     (reverse (distinct path)))))

(defn find-path
  "Given a `starting-node`, calculate the path to the `goal-node`"
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

;; (find-path 27 28)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (find-path 27 28))

(-main)
; => (27 12 18 28)