(ns jpro-bikes.demo)

(defn my-partition-v1
  ;; FIXME: It does return a vector and it is not lazy.
  [n coll]
  (loop [remaining coll partitioned []]
    (if (empty? remaining)
      partitioned
      (recur (drop n remaining) (conj partitioned (take n remaining))))))

(defn my-partition-v2
  [n coll]
  (when-not (empty? coll)
    (let [current (take n coll)]
      (when (= n (count current))
        (lazy-seq (cons current (my-partition-v2 n (drop n coll))))))))
