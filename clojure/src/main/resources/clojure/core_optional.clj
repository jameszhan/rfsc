(in-ns 'clojure.core)









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



(defn supers
  "Returns the immediate and indirect superclasses and interfaces of c, if any"
  {:added "1.0"
   :static true}
  [^Class class]
  (loop [ret (set (bases class)) cs ret]
    (if (seq cs)
      (let [c (first cs) bs (bases c)]
        (recur (into1 ret bs) (into1 (disj cs c) bs)))
      (not-empty ret))))

(defn isa?
  "Returns true if (= child parent), or child is directly or indirectly derived from
  parent, either via a Java type inheritance relationship or a
  relationship established via derive. h must be a hierarchy obtained
  from make-hierarchy, if not supplied defaults to the global
  hierarchy"
  {:added "1.0"}
  ([child parent] (isa? global-hierarchy child parent))
  ([h child parent]
    (or (= child parent)
      (and (class? parent) (class? child)
        (. ^Class parent isAssignableFrom child))
      (contains? ((:ancestors h) child) parent)
      (and (class? child) (some #(contains? ((:ancestors h) %) parent) (supers child)))
      (and (vector? parent) (vector? child)
        (= (count parent) (count child))
        (loop [ret true i 0]
          (if (or (not ret) (= i (count parent)))
            ret
            (recur (isa? h (child i) (parent i)) (inc i))))))))




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
