(in-ns 'clojure.core)





;;;;;;;;;;;;;;;;;;; sequence fns  ;;;;;;;;;;;;;;;;;;;;;;;
#_(defn sequence
    "Coerces coll to a (possibly empty) sequence, if it is not already
    one. Will not force a lazy seq. (sequence nil) yields ()"
    {:added "1.0"
     :static true}
    [coll]
    (if (seq? coll) coll
      (or (seq coll) ())))

#_(def
    ^{:tag Boolean
      :doc "Returns false if (pred x) is logical true for every x in
  coll, else true."
      :arglists '([pred coll])
      :added "1.0"}
    not-every? (comp not every?))

#_(defn drop-last
    "Return a lazy sequence of all but the last n (default 1) items in coll"
    {:added "1.0"
     :static true}
    ([s] (drop-last 1 s))
    ([n s] (map (fn [x _] x) s (drop n s))))

#_(defn take-last
    "Returns a seq of the last n items in coll.  Depending on the type
    of coll may be no better than linear time.  For vectors, see also subvec."
    {:added "1.1"
     :static true}
    [n coll]
    (loop [s (seq coll), lead (seq (drop n coll))]
      (if lead
        (recur (next s) (next lead))
        s)))

#_(defn cycle
    "Returns a lazy (infinite!) sequence of repetitions of the items in coll."
    {:added "1.0"
     :static true}
    [coll] (lazy-seq
             (when-let [s (seq coll)]
               (concat s (cycle s)))))

#_(defn split-at
    "Returns a vector of [(take n coll) (drop n coll)]"
    {:added "1.0"
     :static true}
    [n coll]
    [(take n coll) (drop n coll)])

#_(defn split-with
    "Returns a vector of [(take-while pred coll) (drop-while pred coll)]"
    {:added "1.0"
     :static true}
    [pred coll]
    [(take-while pred coll) (drop-while pred coll)])





(defn iterate
    "Returns a lazy sequence of x, (f x), (f (f x)) etc. f must be free of side-effects"
    {:added "1.0"
     :static true}
    [f x] (cons x (lazy-seq (iterate f (f x)))))

(defn range
    "Returns a lazy seq of nums from start (inclusive) to end
    (exclusive), by step, where start defaults to 0, step to 1, and end to
    infinity. When step is equal to 0, returns an infinite sequence of
    start. When start is equal to end, returns empty list."
    {:added "1.0"
     :static true}
    ([] (range 0 Double/POSITIVE_INFINITY 1))
    ([end] (range 0 end 1))
    ([start end] (range start end 1))
    ([start end step]
      (lazy-seq
        (let [b (chunk-buffer 32)
              comp (cond (or (zero? step) (= start end)) not=
                     (pos? step) <
                     (neg? step) >)]
          (loop [i start]
            (if (and (< (count b) 32)
                  (comp i end))
              (do
                (chunk-append b i)
                (recur (+ i step)))
              (chunk-cons (chunk b)
                (when (comp i end)
                  (range i end step)))))))))

#_(defn merge-with
    "Returns a map that consists of the rest of the maps conj-ed onto
    the first.  If a key occurs in more than one map, the mapping(s)
    from the latter (left-to-right) will be combined with the mapping in
    the result by calling (f val-in-result val-in-latter)."
    {:added "1.0"
     :static true}
    [f & maps]
    (when (some identity maps)
      (let [merge-entry (fn [m e]
                          (let [k (key e) v (val e)]
                            (if (contains? m k)
                              (assoc m k (f (get m k) v))
                              (assoc m k v))))
            merge2 (fn [m1 m2]
                     (reduce1 merge-entry (or m1 {}) (seq m2)))]
        (reduce1 merge2 maps))))

#_(defn zipmap
    "Returns a map with the keys mapped to the corresponding vals."
    {:added "1.0"
     :static true}
    [keys vals]
    (loop [map {}
           ks (seq keys)
           vs (seq vals)]
      (if (and ks vs)
        (recur (assoc map (first ks) (first vs))
          (next ks)
          (next vs))
        map)))

#_(defn line-seq
    "Returns the lines of text from rdr as a lazy sequence of strings.
    rdr must implement java.io.BufferedReader."
    {:added "1.0"
     :static true}
    [^java.io.BufferedReader rdr]
    (when-let [line (.readLine rdr)]
      (cons line (lazy-seq (line-seq rdr)))))

#_(defn comparator
    "Returns an implementation of java.util.Comparator based upon pred."
    {:added "1.0"
     :static true}
    [pred]
    (fn [x y]
      (cond (pred x y) -1 (pred y x) 1 :else 0)))

#_(defn await
    "Blocks the current thread (indefinitely!) until all actions
    dispatched thus far, from this thread or agent, to the agent(s) have
    occurred.  Will block on failed agents.  Will never return if
    a failed agent is restarted with :clear-actions true."
    {:added "1.0"
     :static true}
    [& agents]
    (io! "await in transaction"
      (when *agent*
        (throw (new Exception "Can't await in agent action")))
      (let [latch (new java.util.concurrent.CountDownLatch (count agents))
            count-down (fn [agent] (. latch (countDown)) agent)]
        (doseq [agent agents]
          (send agent count-down))
        (. latch (await)))))

#_(defn ^:static await1 [^clojure.lang.Agent a]
    (when (pos? (.getQueueCount a))
      (await a))
    a)

#_(defn await-for
    "Blocks the current thread until all actions dispatched thus
    far (from this thread or agent) to the agents have occurred, or the
    timeout (in milliseconds) has elapsed. Returns logical false if
    returning due to timeout, logical true otherwise."
    {:added "1.0"
     :static true}
    [timeout-ms & agents]
    (io! "await-for in transaction"
      (when *agent*
        (throw (new Exception "Can't await in agent action")))
      (let [latch (new java.util.concurrent.CountDownLatch (count agents))
            count-down (fn [agent] (. latch (countDown)) agent)]
        (doseq [agent agents]
          (send agent count-down))
        (. latch (await  timeout-ms (. java.util.concurrent.TimeUnit MILLISECONDS))))))

(defmacro dotimes
    "bindings => name n

    Repeatedly executes body (presumably for side-effects) with name
    bound to integers from 0 through n-1."
    {:added "1.0"}
    [bindings & body]
    (assert-args
      (vector? bindings) "a vector for its binding"
      (= 2 (count bindings)) "exactly 2 forms in binding vector")
    (let [i (first bindings)
          n (second bindings)]
      `(let [n# (long ~n)]
         (loop [~i 0]
           (when (< ~i n#)
             ~@body
             (recur (unchecked-inc ~i)))))))

(defn into
    "Returns a new coll consisting of to-coll with all of the items of
    from-coll conjoined."
    {:added "1.0"}
    [to from]
    (let [ret to items (seq from)]
      (if items
        (recur (conj ret (first items)) (next items))
        ret)))

;;;;;;;;;;;;;;;;;;;;; editable collections ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn assoc!
  "When applied to a transient map, adds mapping of key(s) to
  val(s). When applied to a transient vector, sets the val at index.
  Note - index must be <= (count vector). Returns coll."
  {:added "1.1"
   :static true}
  ([^clojure.lang.ITransientAssociative coll key val] (.assoc coll key val))
  ([^clojure.lang.ITransientAssociative coll key val & kvs]
    (let [ret (.assoc coll key val)]
      (if kvs
        (recur ret (first kvs) (second kvs) (nnext kvs))
        ret))))

(defn dissoc!
  "Returns a transient map that doesn't contain a mapping for key(s)."
  {:added "1.1"
   :static true}
  ([^clojure.lang.ITransientMap map key] (.without map key))
  ([^clojure.lang.ITransientMap map key & ks]
    (let [ret (.without map key)]
      (if ks
        (recur ret (first ks) (next ks))
        ret))))

(defn pop!
  "Removes the last item from a transient vector. If
  the collection is empty, throws an exception. Returns coll"
  {:added "1.1"
   :static true}
  [^clojure.lang.ITransientVector coll]
  (.pop coll))

(defn disj!
  "disj[oin]. Returns a transient set of the same (hashed/sorted) type, that
  does not contain key(s)."
  {:added "1.1"
   :static true}
  ([set] set)
  ([^clojure.lang.ITransientSet set key]
    (. set (disjoin key)))
  ([^clojure.lang.ITransientSet set key & ks]
    (let [ret (. set (disjoin key))]
      (if ks
        (recur ret (first ks) (next ks))
        ret))))

(defn unchecked-byte
  "Coerce to byte. Subject to rounding or truncation."
  {:inline (fn  [x] `(. clojure.lang.RT (uncheckedByteCast ~x)))
   :added "1.3"}
  [^Number x] (clojure.lang.RT/uncheckedByteCast x))

(defn unchecked-short
  "Coerce to short. Subject to rounding or truncation."
  {:inline (fn  [x] `(. clojure.lang.RT (uncheckedShortCast ~x)))
   :added "1.3"}
  [^Number x] (clojure.lang.RT/uncheckedShortCast x))

(defn unchecked-char
  "Coerce to char. Subject to rounding or truncation."
  {:inline (fn  [x] `(. clojure.lang.RT (uncheckedCharCast ~x)))
   :added "1.3"}
  [x] (. clojure.lang.RT (uncheckedCharCast x)))

(defn unchecked-int
  "Coerce to int. Subject to rounding or truncation."
  {:inline (fn  [x] `(. clojure.lang.RT (uncheckedIntCast ~x)))
   :added "1.3"}
  [^Number x] (clojure.lang.RT/uncheckedIntCast x))

(defn unchecked-long
  "Coerce to long. Subject to rounding or truncation."
  {:inline (fn  [x] `(. clojure.lang.RT (uncheckedLongCast ~x)))
   :added "1.3"}
  [^Number x] (clojure.lang.RT/uncheckedLongCast x))

(defn unchecked-float
  "Coerce to float. Subject to rounding."
  {:inline (fn  [x] `(. clojure.lang.RT (uncheckedFloatCast ~x)))
   :added "1.3"}
  [^Number x] (clojure.lang.RT/uncheckedFloatCast x))

(defn unchecked-double
  "Coerce to double. Subject to rounding."
  {:inline (fn  [x] `(. clojure.lang.RT (uncheckedDoubleCast ~x)))
   :added "1.3"}
  [^Number x] (clojure.lang.RT/uncheckedDoubleCast x))


(defn number?
  "Returns true if x is a Number"
  {:added "1.0"
   :static true}
  [x]
  (instance? Number x))

(defn mod
  "Modulus of num and div. Truncates toward negative infinity."
  {:added "1.0"
   :static true}
  [num div]
  (let [m (rem num div)]
    (if (or (zero? m) (= (pos? num) (pos? div)))
      m
      (+ m div))))

(defn ratio?
  "Returns true if n is a Ratio"
  {:added "1.0"
   :static true}
  [n] (instance? clojure.lang.Ratio n))

(defn numerator
  "Returns the numerator part of a Ratio."
  {:tag BigInteger
   :added "1.2"
   :static true}
  [r]
  (.numerator ^clojure.lang.Ratio r))

(defn denominator
  "Returns the denominator part of a Ratio."
  {:tag BigInteger
   :added "1.2"
   :static true}
  [r]
  (.denominator ^clojure.lang.Ratio r))

(defn decimal?
  "Returns true if n is a BigDecimal"
  {:added "1.0"
   :static true}
  [n] (instance? BigDecimal n))

(defn float?
  "Returns true if n is a floating point number"
  {:added "1.0"
   :static true}
  [n]
  (or (instance? Double n)
    (instance? Float n)))

(defn rational?
  "Returns true if n is a rational number"
  {:added "1.0"
   :static true}
  [n]
  (or (integer? n) (ratio? n) (decimal? n)))

(defn bigint
  "Coerce to BigInt"
  {:tag clojure.lang.BigInt
   :static true
   :added "1.3"}
  [x] (cond
        (instance? clojure.lang.BigInt x) x
        (instance? BigInteger x) (clojure.lang.BigInt/fromBigInteger x)
        (decimal? x) (bigint (.toBigInteger ^BigDecimal x))
        (float? x)  (bigint (. BigDecimal valueOf (double x)))
        (ratio? x) (bigint (.bigIntegerValue ^clojure.lang.Ratio x))
        (number? x) (clojure.lang.BigInt/valueOf (long x))
        :else (bigint (BigInteger. x))))

(defn biginteger
  "Coerce to BigInteger"
  {:tag BigInteger
   :added "1.0"
   :static true}
  [x] (cond
        (instance? BigInteger x) x
        (instance? clojure.lang.BigInt x) (.toBigInteger ^clojure.lang.BigInt x)
        (decimal? x) (.toBigInteger ^BigDecimal x)
        (float? x) (.toBigInteger (. BigDecimal valueOf (double x)))
        (ratio? x) (.bigIntegerValue ^clojure.lang.Ratio x)
        (number? x) (BigInteger/valueOf (long x))
        :else (BigInteger. x)))

(defn bigdec
  "Coerce to BigDecimal"
  {:tag BigDecimal
   :added "1.0"
   :static true}
  [x] (cond
        (decimal? x) x
        (float? x) (. BigDecimal valueOf (double x))
        (ratio? x) (/ (BigDecimal. (.numerator ^clojure.lang.Ratio x)) (.denominator ^clojure.lang.Ratio x))
        (instance? clojure.lang.BigInt x) (.toBigDecimal ^clojure.lang.BigInt x)
        (instance? BigInteger x) (BigDecimal. ^BigInteger x)
        (number? x) (BigDecimal/valueOf (long x))
        :else (BigDecimal. x)))

#_(defn read-line
    "Reads the next line from stream that is the current value of *in* ."
    {:added "1.0"
     :static true}
    []
    (if (instance? clojure.lang.LineNumberingPushbackReader *in*)
      (.readLine ^clojure.lang.LineNumberingPushbackReader *in*)
      (.readLine ^java.io.BufferedReader *in*)))

#_(defn read-string
    "Reads one object from the string s.

    Note that read-string can execute code (controlled by *read-eval*),
    and as such should be used only with trusted sources.

    For data structure interop use clojure.edn/read-string"
    {:added "1.0"
     :static true}
    [s] (clojure.lang.RT/readString s))

#_(defmacro memfn
    "Expands into code that creates a fn that expects to be passed an
    object and any args and calls the named instance method on the
    object passing the args. Use when you want to treat a Java method as
    a first-class fn. name may be type-hinted with the method receiver's
    type in order to avoid reflective calls."
    {:added "1.0"}
    [name & args]
    (let [t (with-meta (gensym "target")
              (meta name))]
      `(fn [~t ~@args]
         (. ~t (~name ~@args)))))

#_(defmacro time
    "Evaluates expr and prints the time it took.  Returns the value of
   expr."
    {:added "1.0"}
    [expr]
    `(let [start# (. System (nanoTime))
           ret# ~expr]
       (prn (str "Elapsed time: " (/ (double (- (. System (nanoTime)) start#)) 1000000.0) " msecs"))
       ret#))


#_(defn aclone
    "Returns a clone of the Java array. Works on arrays of known
    types."
    {:inline (fn [a] `(. clojure.lang.RT (aclone ~a)))
     :added "1.0"}
    [array] (. clojure.lang.RT (aclone array)))

#_(defn aset
    "Sets the value at the index/indices. Works on Java arrays of
    reference types. Returns val."
    {:inline (fn [a i v] `(. clojure.lang.RT (aset ~a (int ~i) ~v)))
     :inline-arities #{3}
     :added "1.0"}
    ([array idx val]
      (. Array (set array idx val))
      val)
    ([array idx idx2 & idxv]
      (apply aset (aget array idx) idx2 idxv)))

#_(def-aset
    ^{:doc "Sets the value at the index/indices. Works on arrays of long. Returns val."
      :added "1.0"}
    aset-long setLong long)

#_(def-aset
    ^{:doc "Sets the value at the index/indices. Works on arrays of boolean. Returns val."
      :added "1.0"}
    aset-boolean setBoolean boolean)

#_(def-aset
    ^{:doc "Sets the value at the index/indices. Works on arrays of float. Returns val."
      :added "1.0"}
    aset-float setFloat float)

#_(def-aset
    ^{:doc "Sets the value at the index/indices. Works on arrays of double. Returns val."
      :added "1.0"}
    aset-double setDouble double)

#_(def-aset
    ^{:doc "Sets the value at the index/indices. Works on arrays of short. Returns val."
      :added "1.0"}
    aset-short setShort short)

#_(def-aset
    ^{:doc "Sets the value at the index/indices. Works on arrays of byte. Returns val."
      :added "1.0"}
    aset-byte setByte byte)

#_(def-aset
    ^{:doc "Sets the value at the index/indices. Works on arrays of char. Returns val."
      :added "1.0"}
    aset-char setChar char)

#_(defn to-array-2d
    "Returns a (potentially-ragged) 2-dimensional array of Objects
    containing the contents of coll, which can be any Collection of any
    Collection."
    {:tag "[[Ljava.lang.Object;"
     :added "1.0"
     :static true}
    [^java.util.Collection coll]
    (let [ret (make-array (. Class (forName "[Ljava.lang.Object;")) (. coll (size)))]
      (loop [i 0 xs (seq coll)]
        (when xs
          (aset ret i (to-array (first xs)))
          (recur (inc i) (next xs))))
      ret))

#_(defn create-struct
    "Returns a structure basis object."
    {:added "1.0"
     :static true}
    [& keys]
    (. clojure.lang.PersistentStructMap (createSlotMap keys)))

#_(defmacro defstruct
    "Same as (def name (create-struct keys...))"
    {:added "1.0"
     :static true}
    [name & keys]
    `(def ~name (create-struct ~@keys)))

#_(defn struct-map
    "Returns a new structmap instance with the keys of the
    structure-basis. keyvals may contain all, some or none of the basis
    keys - where values are not supplied they will default to nil.
    keyvals can also contain keys not in the basis."
    {:added "1.0"
     :static true}
    [s & inits]
    (. clojure.lang.PersistentStructMap (create s inits)))

#_(defn struct
    "Returns a new structmap instance with the keys of the
    structure-basis. vals must be supplied for basis keys in order -
    where values are not supplied they will default to nil."
    {:added "1.0"
     :static true}
    [s & vals]
    (. clojure.lang.PersistentStructMap (construct s vals)))

#_(defn accessor
    "Returns a fn that, given an instance of a structmap with the basis,
    returns the value at the key.  The key must be in the basis. The
    returned function should be (slightly) more efficient than using
    get, but such use of accessors should be limited to known
    performance-critical areas."
    {:added "1.0"
     :static true}
    [s key]
    (. clojure.lang.PersistentStructMap (getAccessor s key)))

#_(defn load-string
    "Sequentially read and evaluate the set of forms contained in the
    string"
    {:added "1.0"
     :static true}
    [s]
    (let [rdr (-> (java.io.StringReader. s)
                (clojure.lang.LineNumberingPushbackReader.))]
      (load-reader rdr)))

#_(defn ns-refers
    "Returns a map of the refer mappings for the namespace."
    {:added "1.0"
     :static true}
    [ns]
    (let [ns (the-ns ns)]
      (filter-key val (fn [^clojure.lang.Var v] (and (instance? clojure.lang.Var v)
                                                  (not= ns (.ns v))))
        (ns-map ns))))

#_(defn ns-aliases
    "Returns a map of the aliases for the namespace."
    {:added "1.0"
     :static true}
    [ns]
    (.getAliases (the-ns ns)))

#_(defn ns-unalias
    "Removes the alias for the symbol from the namespace."
    {:added "1.0"
     :static true}
    [ns sym]
    (.removeAlias (the-ns ns) sym))

#_(defn var-get
    "Gets the value in the var object"
    {:added "1.0"
     :static true}
    [^clojure.lang.Var x] (. x (get)))

#_(defn var-set
    "Sets the value in the var object to val. The var must be
   thread-locally bound."
    {:added "1.0"
     :static true}
    [^clojure.lang.Var x val] (. x (set val)))

#_(defmacro with-local-vars
    "varbinding=> symbol init-expr

    Executes the exprs in a context in which the symbols are bound to
    vars with per-thread bindings to the init-exprs.  The symbols refer
    to the var objects themselves, and must be accessed with var-get and
    var-set"
    {:added "1.0"}
    [name-vals-vec & body]
    (assert-args
      (vector? name-vals-vec) "a vector for its binding"
      (even? (count name-vals-vec)) "an even number of forms in binding vector")
    `(let [~@(interleave (take-nth 2 name-vals-vec)
               (repeat '(.. clojure.lang.Var create setDynamic)))]
       (. clojure.lang.Var (pushThreadBindings (hash-map ~@name-vals-vec)))
       (try
         ~@body
         (finally (. clojure.lang.Var (popThreadBindings))))))

#_(defn array-map
    "Constructs an array-map. If any keys are equal, they are handled as
    if by repeated uses of assoc."
    {:added "1.0"
     :static true}
    ([] (. clojure.lang.PersistentArrayMap EMPTY))
    ([& keyvals]
      (clojure.lang.PersistentArrayMap/createAsIfByAssoc (to-array keyvals))))

(defmacro comment
  "Ignores body, yields nil"
  {:added "1.0"}
  [& body])

(defmacro lazy-cat
    "Expands to code which yields a lazy sequence of the concatenation
    of the supplied colls.  Each coll expr is not evaluated until it is
    needed.

    (lazy-cat xs ys zs) === (concat (lazy-seq xs) (lazy-seq ys) (lazy-seq zs))"
    {:added "1.0"}
    [& colls]
    `(concat ~@(map #(list `lazy-seq %) colls)))

(defn prn-str
  "prn to a string, returning it"
  {:tag String
   :added "1.0"
   :static true}
  [& xs]
  (with-out-str
    (apply prn xs)))

(defn println-str
  "println to a string, returning it"
  {:tag String
   :added "1.0"
   :static true}
  [& xs]
  (with-out-str
    (apply println xs)))

(defmacro assert
  "Evaluates expr and throws an exception if it does not evaluate to
  logical true."
  {:added "1.0"}
  ([x]
    (when *assert*
      `(when-not ~x
         (throw (new AssertionError (str "Assert failed: " (pr-str '~x)))))))
  ([x message]
    (when *assert*
      `(when-not ~x
         (throw (new AssertionError (str "Assert failed: " ~message "\n" (pr-str '~x))))))))

(defn test
  "test [v] finds fn at key :test in var metadata and calls it,
  presuming failure will throw exception"
  {:added "1.0"}
  [v]
  (let [f (:test (meta v))]
    (if f
      (do (f) :ok)
      :no-test)))


(defn re-seq
  "Returns a lazy sequence of successive matches of pattern in string,
  using java.util.regex.Matcher.find(), each such match processed with
  re-groups."
  {:added "1.0"
   :static true}
  [^java.util.regex.Pattern re s]
  (let [m (re-matcher re s)]
    ((fn step []
       (when (. m (find))
         (cons (re-groups m) (lazy-seq (step))))))))

(defn rand
  "Returns a random floating point number between 0 (inclusive) and
  n (default 1) (exclusive)."
  {:added "1.0"
   :static true}
  ([] (. Math (random)))
  ([n] (* n (rand))))

(defn rand-int
  "Returns a random integer between 0 (inclusive) and n (exclusive)."
  {:added "1.0"
   :static true}
  [n] (int (rand n)))

(defn max-key
  "Returns the x for which (k x), a number, is greatest."
  {:added "1.0"
   :static true}
  ([k x] x)
  ([k x y] (if (> (k x) (k y)) x y))
  ([k x y & more]
    (reduce1 #(max-key k %1 %2) (max-key k x y) more)))

(defn min-key
  "Returns the x for which (k x), a number, is least."
  {:added "1.0"
   :static true}
  ([k x] x)
  ([k x y] (if (< (k x) (k y)) x y))
  ([k x y & more]
    (reduce1 #(min-key k %1 %2) (min-key k x y) more)))

(defn distinct
  "Returns a lazy sequence of the elements of coll with duplicates removed"
  {:added "1.0"
   :static true}
  [coll]
  (let [step (fn step [xs seen]
               (lazy-seq
                 ((fn [[f :as xs] seen]
                    (when-let [s (seq xs)]
                      (if (contains? seen f)
                        (recur (rest s) seen)
                        (cons f (step (rest s) (conj seen f))))))
                   xs seen)))]
    (step coll #{})))



(defn replace
  "Given a map of replacement pairs and a vector/collection, returns a
  vector/seq with any elements = a key in smap replaced with the
  corresponding val in smap"
  {:added "1.0"
   :static true}
  [smap coll]
  (if (vector? coll)
    (reduce1 (fn [v i]
               (if-let [e (find smap (nth v i))]
                 (assoc v i (val e))
                 v))
      coll (range (count coll)))
    (map #(if-let [e (find smap %)] (val e) %) coll)))


(defmacro with-precision
  "Sets the precision and rounding mode to be used for BigDecimal operations.

  Usage: (with-precision 10 (/ 1M 3))
  or:    (with-precision 10 :rounding HALF_DOWN (/ 1M 3))

  The rounding mode is one of CEILING, FLOOR, HALF_UP, HALF_DOWN,
  HALF_EVEN, UP, DOWN and UNNECESSARY; it defaults to HALF_UP."
  {:added "1.0"}
  [precision & exprs]
  (let [[body rm] (if (= (first exprs) :rounding)
                    [(next (next exprs))
                     `((. java.math.RoundingMode ~(second exprs)))]
                    [exprs nil])]
    `(binding [*math-context* (java.math.MathContext. ~precision ~@rm)]
       ~@body)))

(defn mk-bound-fn
  {:private true}
  [^clojure.lang.Sorted sc test key]
  (fn [e]
    (test (.. sc comparator (compare (. sc entryKey e) key)) 0)))

(defn subseq
  "sc must be a sorted collection, test(s) one of <, <=, > or
  >=. Returns a seq of those entries with keys ek for
  which (test (.. sc comparator (compare ek key)) 0) is true"
  {:added "1.0"
   :static true}
  ([^clojure.lang.Sorted sc test key]
    (let [include (mk-bound-fn sc test key)]
      (if (#{> >=} test)
        (when-let [[e :as s] (. sc seqFrom key true)]
          (if (include e) s (next s)))
        (take-while include (. sc seq true)))))
  ([^clojure.lang.Sorted sc start-test start-key end-test end-key]
    (when-let [[e :as s] (. sc seqFrom start-key true)]
      (take-while (mk-bound-fn sc end-test end-key)
        (if ((mk-bound-fn sc start-test start-key) e) s (next s))))))

(defn rsubseq
  "sc must be a sorted collection, test(s) one of <, <=, > or
  >=. Returns a reverse seq of those entries with keys ek for
  which (test (.. sc comparator (compare ek key)) 0) is true"
  {:added "1.0"
   :static true}
  ([^clojure.lang.Sorted sc test key]
    (let [include (mk-bound-fn sc test key)]
      (if (#{< <=} test)
        (when-let [[e :as s] (. sc seqFrom key false)]
          (if (include e) s (next s)))
        (take-while include (. sc seq false)))))
  ([^clojure.lang.Sorted sc start-test start-key end-test end-key]
    (when-let [[e :as s] (. sc seqFrom end-key false)]
      (take-while (mk-bound-fn sc start-test start-key)
        (if ((mk-bound-fn sc end-test end-key) e) s (next s))))))

(defn repeatedly
  "Takes a function of no args, presumably with side effects, and
  returns an infinite (or length n if supplied) lazy sequence of calls
  to it"
  {:added "1.0"
   :static true}
  ([f] (lazy-seq (cons (f) (repeatedly f))))
  ([n f] (take n (repeatedly f))))

(defn add-classpath
  "DEPRECATED

  Adds the url (String or URL object) to the classpath per
  URLClassLoader.addURL"
  {:added "1.0"
   :deprecated "1.1"}
  [url]
  (println "WARNING: add-classpath is deprecated")
  (clojure.lang.RT/addURL url))






(defn mix-collection-hash
  "Mix final collection hash for ordered or unordered collections.
   hash-basis is the combined collection hash, count is the number
   of elements included in the basis. Note this is the hash code
   consistent with =, different from .hashCode.
   See http://clojure.org/data_structures#hash for full algorithms."
  {:added "1.6"
   :static true}
  ^long
  [^long hash-basis ^long count] (clojure.lang.Murmur3/mixCollHash hash-basis count))

(defn hash-ordered-coll
  "Returns the hash code, consistent with =, for an external ordered
   collection implementing Iterable.
   See http://clojure.org/data_structures#hash for full algorithms."
  {:added "1.6"
   :static true}
  ^long
  [coll] (clojure.lang.Murmur3/hashOrdered coll))

(defn hash-unordered-coll
  "Returns the hash code, consistent with =, for an external unordered
   collection implementing Iterable. For maps, the iterator should
   return map entries whose hash is computed as
     (hash-ordered-coll [k v]).
   See http://clojure.org/data_structures#hash for full algorithms."
  {:added "1.6"
   :static true}
  ^long
  [coll] (clojure.lang.Murmur3/hashUnordered coll))



(defmacro definline
  "Experimental - like defmacro, except defines a named function whose
  body is the expansion, calls to which may be expanded inline as if
  it were a macro. Cannot be used with variadic (&) args."
  {:added "1.0"}
  [name & decl]
  (let [[pre-args [args expr]] (split-with (comp not vector?) decl)]
    `(do
       (defn ~name ~@pre-args ~args ~(apply (eval (list `fn args expr)) args))
       (alter-meta! (var ~name) assoc :inline (fn ~name ~args ~expr))
       (var ~name))))

(defn empty
  "Returns an empty collection of the same category as coll, or nil"
  {:added "1.0"
   :static true}
  [coll]
  (when (instance? clojure.lang.IPersistentCollection coll)
    (.empty ^clojure.lang.IPersistentCollection coll)))




(import '(java.util.concurrent BlockingQueue LinkedBlockingQueue))

#_(defn seque
    "Creates a queued seq on another (presumably lazy) seq s. The queued
    seq will produce a concrete seq in the background, and can get up to
    n items ahead of the consumer. n-or-q can be an integer n buffer
    size, or an instance of java.util.concurrent BlockingQueue. Note
    that reading from a seque can block if the reader gets ahead of the
    producer."
    {:added "1.0"
     :static true}
    ([s] (seque 100 s))
    ([n-or-q s]
      (let [^BlockingQueue q (if (instance? BlockingQueue n-or-q)
                               n-or-q
                               (LinkedBlockingQueue. (int n-or-q)))
            NIL (Object.) ;nil sentinel since LBQ doesn't support nils
            agt (agent (lazy-seq s)) ; never start with nil; that signifies we've already put eos
            log-error (fn [q e]
                        (if (.offer q q)
                          (throw e)
                          e))
            fill (fn [s]
                   (when s
                     (if (instance? Exception s) ; we failed to .offer an error earlier
                       (log-error q s)
                       (try
                         (loop [[x & xs :as s] (seq s)]
                           (if s
                             (if (.offer q (if (nil? x) NIL x))
                               (recur xs)
                               s)
                             (when-not (.offer q q) ; q itself is eos sentinel
                               ()))) ; empty seq, not nil, so we know to put eos next time
                         (catch Exception e
                           (log-error q e))))))
            drain (fn drain []
                    (lazy-seq
                      (let [x (.take q)]
                        (if (identical? x q) ;q itself is eos sentinel
                          (do @agt nil)  ;touch agent just to propagate errors
                          (do
                            (send-off agt fill)
                            (cons (if (identical? x NIL) nil x) (drain)))))))]
        (send-off agt fill)
        (drain))))

(defn- is-annotation? [c]
  (and (class? c)
    (.isAssignableFrom java.lang.annotation.Annotation c)))

(defn- is-runtime-annotation? [^Class c]
  (boolean
    (and (is-annotation? c)
      (when-let [^java.lang.annotation.Retention r
                 (.getAnnotation c java.lang.annotation.Retention)]
        (= (.value r) java.lang.annotation.RetentionPolicy/RUNTIME)))))

(defn- descriptor [^Class c] (clojure.asm.Type/getDescriptor c))

(declare process-annotation)
(defn- add-annotation [^clojure.asm.AnnotationVisitor av name v]
  (cond
    (vector? v) (let [avec (.visitArray av name)]
                  (doseq [vval v]
                    (add-annotation avec "value" vval))
                  (.visitEnd avec))
    (symbol? v) (let [ev (eval v)]
                  (cond
                    (instance? java.lang.Enum ev)
                    (.visitEnum av name (descriptor (class ev)) (str ev))
                    (class? ev) (.visit av name (clojure.asm.Type/getType ev))
                    :else (throw (IllegalArgumentException.
                                   (str "Unsupported annotation value: " v " of class " (class ev))))))
    (seq? v) (let [[nested nv] v
                   c (resolve nested)
                   nav (.visitAnnotation av name (descriptor c))]
               (process-annotation nav nv)
               (.visitEnd nav))
    :else (.visit av name v)))

(defn- process-annotation [av v]
  (if (map? v)
    (doseq [[k v] v]
      (add-annotation av (name k) v))
    (add-annotation av "value" v)))

(defn- add-annotations
  ([visitor m] (add-annotations visitor m nil))
  ([visitor m i]
    (doseq [[k v] m]
      (when (symbol? k)
        (when-let [c (resolve k)]
          (when (is-annotation? c)
            ;this is known duck/reflective as no common base of ASM Visitors
            (let [av (if i
                       (.visitParameterAnnotation visitor i (descriptor c)
                         (is-runtime-annotation? c))
                       (.visitAnnotation visitor (descriptor c)
                         (is-runtime-annotation? c)))]
              (process-annotation av v)
              (.visitEnd av))))))))


(defn alter-var-root
  "Atomically alters the root binding of var v by applying f to its
  current value plus any args"
  {:added "1.0"
   :static true}
  [^clojure.lang.Var v f & args] (.alterRoot v f args))

(defn bound?
  "Returns true if all of the vars provided as arguments have any bound value, root or thread-local.
   Implies that deref'ing the provided vars will succeed. Returns true if no vars are provided."
  {:added "1.2"
   :static true}
  [& vars]
  (every? #(.isBound ^clojure.lang.Var %) vars))

(defn thread-bound?
  "Returns true if all of the vars provided as arguments have thread-local bindings.
   Implies that set!'ing the provided vars will succeed.  Returns true if no vars are provided."
  {:added "1.2"
   :static true}
  [& vars]
  (every? #(.getThreadBinding ^clojure.lang.Var %) vars))



(defn tree-seq
  "Returns a lazy sequence of the nodes in a tree, via a depth-first walk.
   branch? must be a fn of one arg that returns true if passed a node
   that can have children (but may not).  children must be a fn of one
   arg that returns a sequence of the children. Will only be called on
   nodes for which branch? returns true. Root is the root node of the
  tree."
  {:added "1.0"
   :static true}
  [branch? children root]
  (let [walk (fn walk [node]
               (lazy-seq
                 (cons node
                   (when (branch? node)
                     (mapcat walk (children node))))))]
    (walk root)))

(defn file-seq
  "A tree seq on java.io.Files"
  {:added "1.0"
   :static true}
  [dir]
  (tree-seq
    (fn [^java.io.File f] (. f (isDirectory)))
    (fn [^java.io.File d] (seq (. d (listFiles))))
    dir))

(defn xml-seq
  "A tree seq on the xml elements as per xml/parse"
  {:added "1.0"
   :static true}
  [root]
  (tree-seq
    (complement string?)
    (comp seq :content)
    root))

(defn special-symbol?
  "Returns true if s names a special form"
  {:added "1.0"
   :static true}
  [s]
  (contains? (. clojure.lang.Compiler specials) s))

(defn var?
  "Returns true if v is of type clojure.lang.Var"
  {:added "1.0"
   :static true}
  [v] (instance? clojure.lang.Var v))

(defn derive
  "Establishes a parent/child relationship between parent and
  tag. Parent must be a namespace-qualified symbol or keyword and
  child can be either a namespace-qualified symbol or keyword or a
  class. h must be a hierarchy obtained from make-hierarchy, if not
  supplied defaults to, and modifies, the global hierarchy."
  {:added "1.0"}
  ([tag parent]
    (assert (namespace parent))
    (assert (or (class? tag) (and (instance? clojure.lang.Named tag) (namespace tag))))

    (alter-var-root #'global-hierarchy derive tag parent) nil)
  ([h tag parent]
    (assert (not= tag parent))
    (assert (or (class? tag) (instance? clojure.lang.Named tag)))
    (assert (instance? clojure.lang.Named parent))

    (let [tp (:parents h)
          td (:descendants h)
          ta (:ancestors h)
          tf (fn [m source sources target targets]
               (reduce1 (fn [ret k]
                          (assoc ret k
                            (reduce1 conj (get targets k #{}) (cons target (targets target)))))
                 m (cons source (sources source))))]
      (or
        (when-not (contains? (tp tag) parent)
          (when (contains? (ta tag) parent)
            (throw (Exception. (print-str tag "already has" parent "as ancestor"))))
          (when (contains? (ta parent) tag)
            (throw (Exception. (print-str "Cyclic derivation:" parent "has" tag "as ancestor"))))
          {:parents (assoc (:parents h) tag (conj (get tp tag #{}) parent))
           :ancestors (tf (:ancestors h) tag td parent ta)
           :descendants (tf (:descendants h) parent ta tag td)})
        h))))

(declare flatten)

(defn underive
  "Removes a parent/child relationship between parent and
  tag. h must be a hierarchy obtained from make-hierarchy, if not
  supplied defaults to, and modifies, the global hierarchy."
  {:added "1.0"}
  ([tag parent] (alter-var-root #'global-hierarchy underive tag parent) nil)
  ([h tag parent]
    (let [parentMap (:parents h)
          childsParents (if (parentMap tag)
                          (disj (parentMap tag) parent) #{})
          newParents (if (not-empty childsParents)
                       (assoc parentMap tag childsParents)
                       (dissoc parentMap tag))
          deriv-seq (flatten (map #(cons (key %) (interpose (key %) (val %)))
                               (seq newParents)))]
      (if (contains? (parentMap tag) parent)
        (reduce1 #(apply derive %1 %2) (make-hierarchy)
          (partition 2 deriv-seq))
        h))))


(defn distinct?
  "Returns true if no two of the arguments are ="
  {:tag Boolean
   :added "1.0"
   :static true}
  ([x] true)
  ([x y] (not (= x y)))
  ([x y & more]
    (if (not= x y)
      (loop [s #{x y} [x & etc :as xs] more]
        (if xs
          (if (contains? s x)
            false
            (recur (conj s x) etc))
          true))
      false)))

(defn resultset-seq
  "Creates and returns a lazy sequence of structmaps corresponding to
  the rows in the java.sql.ResultSet rs"
  {:added "1.0"}
  [^java.sql.ResultSet rs]
  (let [rsmeta (. rs (getMetaData))
        idxs (range 1 (inc (. rsmeta (getColumnCount))))
        keys (map (comp keyword #(.toLowerCase ^String %))
               (map (fn [i] (. rsmeta (getColumnLabel i))) idxs))
        check-keys
        (or (apply distinct? keys)
          (throw (Exception. "ResultSet must have unique column labels")))
        row-struct (apply create-struct keys)
        row-values (fn [] (map (fn [^Integer i] (. rs (getObject i))) idxs))
        rows (fn thisfn []
               (when (. rs (next))
                 (cons (apply struct row-struct (row-values)) (lazy-seq (thisfn)))))]
    (rows)))

(defn iterator-seq
  "Returns a seq on a java.util.Iterator. Note that most collections
  providing iterators implement Iterable and thus support seq directly."
  {:added "1.0"
   :static true}
  [iter]
  (clojure.lang.IteratorSeq/create iter))

(defn enumeration-seq
  "Returns a seq on a java.util.Enumeration"
  {:added "1.0"
   :static true}
  [e]
  (clojure.lang.EnumerationSeq/create e))

(defn use
    "Like 'require, but also refers to each lib's namespace using
    clojure.core/refer. Use :use in the ns macro in preference to calling
    this directly.

    'use accepts additional options in libspecs: :exclude, :only, :rename.
    The arguments and semantics for :exclude, :only, and :rename are the same
    as those documented for clojure.core/refer."
    {:added "1.0"}
    [& args] (apply load-libs :require :use args))

(defn loaded-libs
    "Returns a sorted set of symbols naming the currently loaded libs"
    {:added "1.0"}
    [] @*loaded-libs*)

(defn compile
    "Compiles the namespace named by the symbol lib into a set of
    classfiles. The source for the lib must be in a proper
    classpath-relative directory. The output files will go into the
    directory specified by *compile-path*, and that directory too must
    be in the classpath."
    {:added "1.0"}
    [lib]
    (binding [*compile-files* true]
      (load-one lib true true))
    lib)

;;;;;;;;;;;;; nested associative ops ;;;;;;;;;;;

(defn assoc-in
  "Associates a value in a nested associative structure, where ks is a
  sequence of keys and v is the new value and returns a new nested structure.
  If any levels do not exist, hash-maps will be created."
  {:added "1.0"
   :static true}
  [m [k & ks] v]
  (if ks
    (assoc m k (assoc-in (get m k) ks v))
    (assoc m k v)))



#_(defn empty?
    "Returns true if coll has no items - same as (not (seq coll)).
    Please use the idiom (seq x) rather than (not (empty? x))"
    {:added "1.0"
     :static true}
    [coll] (not (seq coll)))

#_(defn coll?
    "Returns true if x implements IPersistentCollection"
    {:added "1.0"
     :static true}
    [x] (instance? clojure.lang.IPersistentCollection x))

#_(defn list?
    "Returns true if x implements IPersistentList"
    {:added "1.0"
     :static true}
    [x] (instance? clojure.lang.IPersistentList x))

#_(defn set?
    "Returns true if x implements IPersistentSet"
    {:added "1.0"
     :static true}
    [x] (instance? clojure.lang.IPersistentSet x))

#_(defn ifn?
    "Returns true if x implements IFn. Note that many data structures
    (e.g. sets and maps) implement IFn"
    {:added "1.0"
     :static true}
    [x] (instance? clojure.lang.IFn x))

#_(defn fn?
    "Returns true if x implements Fn, i.e. is an object created via fn."
    {:added "1.0"
     :static true}
    [x] (instance? clojure.lang.Fn x))


#_(defn associative?
    "Returns true if coll implements Associative"
    {:added "1.0"
     :static true}
    [coll] (instance? clojure.lang.Associative coll))

#_(defn sequential?
    "Returns true if coll implements Sequential"
    {:added "1.0"
     :static true}
    [coll] (instance? clojure.lang.Sequential coll))

#_(defn sorted?
    "Returns true if coll implements Sorted"
    {:added "1.0"
     :static true}
    [coll] (instance? clojure.lang.Sorted coll))

#_(defn counted?
    "Returns true if coll implements count in constant time"
    {:added "1.0"
     :static true}
    [coll] (instance? clojure.lang.Counted coll))

#_(defn reversible?
    "Returns true if coll implements Reversible"
    {:added "1.0"
     :static true}
    [coll] (instance? clojure.lang.Reversible coll))

(defmacro condp
  "Takes a binary predicate, an expression, and a set of clauses.
  Each clause can take the form of either:

  test-expr result-expr

  test-expr :>> result-fn

  Note :>> is an ordinary keyword.

  For each clause, (pred test-expr expr) is evaluated. If it returns
  logical true, the clause is a match. If a binary clause matches, the
  result-expr is returned, if a ternary clause matches, its result-fn,
  which must be a unary function, is called with the result of the
  predicate as its argument, the result of that call being the return
  value of condp. A single default expression can follow the clauses,
  and its value will be returned if no clause matches. If no default
  expression is provided and no clause matches, an
  IllegalArgumentException is thrown."
  {:added "1.0"}

  [pred expr & clauses]
  (let [gpred (gensym "pred__")
        gexpr (gensym "expr__")
        emit (fn emit [pred expr args]
               (let [[[a b c :as clause] more]
                     (split-at (if (= :>> (second args)) 3 2) args)
                     n (count clause)]
                 (cond
                   (= 0 n) `(throw (IllegalArgumentException. (str "No matching clause: " ~expr)))
                   (= 1 n) a
                   (= 2 n) `(if (~pred ~a ~expr)
                              ~b
                              ~(emit pred expr more))
                   :else `(if-let [p# (~pred ~a ~expr)]
                            (~c p#)
                            ~(emit pred expr more)))))
        gres (gensym "res__")]
    `(let [~gpred ~pred
           ~gexpr ~expr]
       ~(emit gpred gexpr clauses))))

(defmacro letfn
  "fnspec ==> (fname [params*] exprs) or (fname ([params*] exprs)+)

  Takes a vector of function specs and a body, and generates a set of
  bindings of functions to their names. All of the names are available
  in all of the definitions of the functions, as well as the body."
  {:added "1.0", :forms '[(letfn [fnspecs*] exprs*)],
   :special-form true, :url nil}
  [fnspecs & body]
  `(letfn* ~(vec (interleave (map first fnspecs)
                   (map #(cons `fn %) fnspecs)))
     ~@body))
