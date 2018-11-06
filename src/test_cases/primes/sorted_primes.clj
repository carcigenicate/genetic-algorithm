(ns test-cases.primes.sorted-primes)

(def sort-error-fact 3)
(def prime-error-fact 1)

(defn prime? [n]
  (not
    (or (zero? n)
        (some #(zero? (rem n %)) (range 2 n)))))

(defn sorted-error [comp-f coll]
  (loop [[n & rest-n] coll
         last-n nil
         acc-error 0]
    (if n
      (recur rest-n n (if (and (some? last-n) (not (comp-f last-n n)))
                        (inc acc-error)
                        acc-error))
      acc-error)))

(defn prime-error [coll]
  (reduce #(if (prime? %2) % (inc %)) 0 coll))

(defn fitness-f [genes]
  (+ (* sort-error-fact (min (sorted-error < genes)
                             (sorted-error > genes)))

     (* prime-error-fact (prime-error genes))))
