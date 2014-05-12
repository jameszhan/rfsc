;   Copyright (c) Rich Hickey. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns ^{:doc "The core Clojure language."
      :author "Rich Hickey"}
  clojure.core)

(def unquote)
(def unquote-splicing)

(def
  ^{:arglists '([& items])
    :doc "Creates a new list containing the items."
    :added "1.0"}
  list (. clojure.lang.PersistentList creator))

(def
  ^{:arglists '([x seq])
    :doc "Returns a new seq where x is the first element and seq is the rest."
    :added "1.0"
    :static true}
  cons (fn* ^:static cons [x seq] (. clojure.lang.RT (cons x seq))))

;during bootstrap we don't have destructuring let, loop or fn, will redefine later
(def
  ^{:macro true
    :added "1.0"}
  let (fn* let [&form &env & decl] (cons 'let* decl)))

(def
  ^{:macro true
    :added "1.0"}
  loop (fn* loop [&form &env & decl] (cons 'loop* decl)))

(def
  ^{:macro true
    :added "1.0"}
  fn (fn* fn [&form &env & decl]
       (.withMeta ^clojure.lang.IObj (cons 'fn* decl)
         (.meta ^clojure.lang.IMeta &form))))

(def
  ^{:arglists '([coll])
    :doc "Returns the first item in the collection. Calls seq on its
    argument. If coll is nil, returns nil."
    :added "1.0"
    :static true}
  first (fn ^:static first [coll] (. clojure.lang.RT (first coll))))

(def
  ^{:arglists '([coll])
    :tag clojure.lang.ISeq
    :doc "Returns a seq of the items after the first. Calls seq on its
  argument.  If there are no more items, returns nil."
    :added "1.0"
    :static true}
  next (fn ^:static next [x] (. clojure.lang.RT (next x))))

(def
  ^{:arglists '([coll])
    :tag clojure.lang.ISeq
    :doc "Returns a possibly empty seq of the items after the first. Calls seq on its
  argument."
    :added "1.0"
    :static true}
  rest (fn ^:static rest [x] (. clojure.lang.RT (more x))))

(def
  ^{:arglists '([coll x] [coll x & xs])
    :doc "conj[oin]. Returns a new collection with the xs
    'added'. (conj nil item) returns (item).  The 'addition' may
    happen at different 'places' depending on the concrete type."
    :added "1.0"
    :static true}
  conj (fn ^:static conj
         ([coll x] (. clojure.lang.RT (conj coll x)))
         ([coll x & xs]
           (if xs
             (recur (conj coll x) (first xs) (next xs))
             (conj coll x)))))

(def
  ^{:doc "Same as (first (next x))"
    :arglists '([x])
    :added "1.0"
    :static true}
  second (fn ^:static second [x] (first (next x))))

(def
  ^{:doc "Same as (first (first x))"
    :arglists '([x])
    :added "1.0"
    :static true}
  ffirst (fn ^:static ffirst [x] (first (first x))))

(def
  ^{:doc "Same as (next (first x))"
    :arglists '([x])
    :added "1.0"
    :static true}
  nfirst (fn ^:static nfirst [x] (next (first x))))

(def
  ^{:doc "Same as (first (next x))"
    :arglists '([x])
    :added "1.0"
    :static true}
  fnext (fn ^:static fnext [x] (first (next x))))

(def
  ^{:doc "Same as (next (next x))"
    :arglists '([x])
    :added "1.0"
    :static true}
  nnext (fn ^:static nnext [x] (next (next x))))

(def
  ^{:arglists '(^clojure.lang.ISeq [coll])
    :doc "Returns a seq on the collection. If the collection is
    empty, returns nil.  (seq nil) returns nil. seq also works on
    Strings, native Java arrays (of reference types) and any objects
    that implement Iterable."
    :tag clojure.lang.ISeq
    :added "1.0"
    :static true}
  seq (fn ^:static seq ^clojure.lang.ISeq [coll] (. clojure.lang.RT (seq coll))))

(def
  ^{:arglists '([^Class c x])
    :doc "Evaluates x and tests if it is an instance of the class
    c. Returns true or false"
    :added "1.0"}
  instance? (fn instance? [^Class c x] (. c (isInstance x))))

(def
  ^{:arglists '([x])
    :doc "Return true if x implements ISeq"
    :added "1.0"
    :static true}
  seq? (fn ^:static seq? [x] (instance? clojure.lang.ISeq x)))

(def
  ^{:arglists '([x])
    :doc "Return true if x is a Character"
    :added "1.0"
    :static true}
  char? (fn ^:static char? [x] (instance? Character x)))

(def
  ^{:arglists '([x])
    :doc "Return true if x is a String"
    :added "1.0"
    :static true}
  string? (fn ^:static string? [x] (instance? String x)))

(def
  ^{:arglists '([x])
    :doc "Return true if x implements IPersistentMap"
    :added "1.0"
    :static true}
  map? (fn ^:static map? [x] (instance? clojure.lang.IPersistentMap x)))

(def
  ^{:arglists '([x])
    :doc "Return true if x implements IPersistentVector"
    :added "1.0"
    :static true}
  vector? (fn ^:static vector? [x] (instance? clojure.lang.IPersistentVector x)))

(def
  ^{:arglists '([map key val] [map key val & kvs])
    :doc "assoc[iate]. When applied to a map, returns a new map of the
    same (hashed/sorted) type, that contains the mapping of key(s) to
    val(s). When applied to a vector, returns a new vector that
    contains val at index. Note - index must be <= (count vector)."
    :added "1.0"
    :static true}
  assoc
  (fn ^:static assoc
    ([map key val] (. clojure.lang.RT (assoc map key val)))
    ([map key val & kvs]
      (let [ret (assoc map key val)]
        (if kvs
          (if (next kvs)
            (recur ret (first kvs) (second kvs) (nnext kvs))
            (throw (IllegalArgumentException.
                     "assoc expects even number of arguments after map/vector, found odd number")))
          ret)))))

;;;;;;;;;;;;;;;;; metadata ;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def
  ^{:arglists '([obj])
    :doc "Returns the metadata of obj, returns nil if there is no metadata."
    :added "1.0"
    :static true}
  meta (fn ^:static meta [x]
         (if (instance? clojure.lang.IMeta x)
           (. ^clojure.lang.IMeta x (meta)))))

(def
  ^{:arglists '([^clojure.lang.IObj obj m])
    :doc "Returns an object of the same type and value as obj, with
    map m as its metadata."
    :added "1.0"
    :static true}
  with-meta (fn ^:static with-meta [^clojure.lang.IObj x m]
              (. x (withMeta m))))

(def ^{:private true :dynamic true}
  assert-valid-fdecl (fn [fdecl]))

(def
  ^{:private true}
  sigs
  (fn [fdecl]
    (assert-valid-fdecl fdecl)
    (let [asig
          (fn [fdecl]
            (let [arglist (first fdecl)
                  ;elide implicit macro args
                  arglist (if (clojure.lang.Util/equals '&form (first arglist))
                            (clojure.lang.RT/subvec arglist 2 (clojure.lang.RT/count arglist))
                            arglist)
                  body (next fdecl)]
              (if (map? (first body))
                (if (next body)
                  (with-meta arglist (conj (if (meta arglist) (meta arglist) {}) (first body)))
                  arglist)
                arglist)))]
      (if (seq? (first fdecl))
        (loop [ret [] fdecls fdecl]
          (if fdecls
            (recur (conj ret (asig (first fdecls))) (next fdecls))
            (seq ret)))
        (list (asig fdecl))))))


(def
  ^{:arglists '([coll])
    :doc "Return the last item in coll, in linear time"
    :added "1.0"
    :static true}
  last (fn ^:static last [s]
         (if (next s)
           (recur (next s))
           (first s))))

(def
  ^{:arglists '([coll])
    :doc "Return a seq of all but the last item in coll, in linear time"
    :added "1.0"
    :static true}
  butlast (fn ^:static butlast [s]
            (loop [ret [] s s]
              (if (next s)
                (recur (conj ret (first s)) (next s))
                (seq ret)))))

(def

  ^{:doc "Same as (def name (fn [params* ] exprs*)) or (def
    name (fn ([params* ] exprs*)+)) with any doc-string or attrs added
    to the var metadata. prepost-map defines a map with optional keys
    :pre and :post that contain collections of pre or post conditions."
    :arglists '([name doc-string? attr-map? [params*] prepost-map? body]
                 [name doc-string? attr-map? ([params*] prepost-map? body) + attr-map?])
    :added "1.0"}
  defn (fn defn [&form &env name & fdecl]
         ;; Note: Cannot delegate this check to def because of the call to (with-meta name ..)
         (if (instance? clojure.lang.Symbol name)
           nil
           (throw (IllegalArgumentException. "First argument to defn must be a symbol")))
         (let [m (if (string? (first fdecl))
                   {:doc (first fdecl)}
                   {})
               fdecl (if (string? (first fdecl))
                       (next fdecl)
                       fdecl)
               m (if (map? (first fdecl))
                   (conj m (first fdecl))
                   m)
               fdecl (if (map? (first fdecl))
                       (next fdecl)
                       fdecl)
               fdecl (if (vector? (first fdecl))
                       (list fdecl)
                       fdecl)
               m (if (map? (last fdecl))
                   (conj m (last fdecl))
                   m)
               fdecl (if (map? (last fdecl))
                       (butlast fdecl)
                       fdecl)
               m (conj {:arglists (list 'quote (sigs fdecl))} m)
               m (let [inline (:inline m)
                       ifn (first inline)
                       iname (second inline)]
                   ;; same as: (if (and (= 'fn ifn) (not (symbol? iname))) ...)
                   (if (if (clojure.lang.Util/equiv 'fn ifn)
                         (if (instance? clojure.lang.Symbol iname) false true))
                     ;; inserts the same fn name to the inline fn if it does not have one
                     (assoc m :inline (cons ifn (cons (clojure.lang.Symbol/intern (.concat (.getName ^clojure.lang.Symbol name) "__inliner"))
                                                  (next inline))))
                     m))
               m (conj (if (meta name) (meta name) {}) m)]
           (list 'def (with-meta name m)
             ;;todo - restore propagation of fn name
             ;;must figure out how to convey primitive hints to self calls first
             (cons `fn fdecl)))))

(. (var defn) (setMacro))

(defn cast
  "Throws a ClassCastException if x is not a c, else returns x."
  {:added "1.0"
   :static true}
  [^Class c x]
  (. c (cast x)))

(defn to-array
  "Returns an array of Objects containing the contents of coll, which
  can be any Collection.  Maps to java.util.Collection.toArray()."
  {:tag "[Ljava.lang.Object;"
   :added "1.0"
   :static true}
  [coll] (. clojure.lang.RT (toArray coll)))

(defn vector
  "Creates a new vector containing the args."
  {:added "1.0"
   :static true}
  ([] [])
  ([a] [a])
  ([a b] [a b])
  ([a b c] [a b c])
  ([a b c d] [a b c d])
  ([a b c d & args]
    (. clojure.lang.LazilyPersistentVector (create (cons a (cons b (cons c (cons d args))))))))

(defn vec
  "Creates a new vector containing the contents of coll. Java arrays
  will be aliased and should not be modified."
  {:added "1.0"
   :static true}
  ([coll]
    (if (instance? java.util.Collection coll)
      (clojure.lang.LazilyPersistentVector/create coll)
      (. clojure.lang.LazilyPersistentVector (createOwning (to-array coll))))))

(defn hash-map
  "keyval => key val
  Returns a new hash map with supplied mappings.  If any keys are
  equal, they are handled as if by repeated uses of assoc."
  {:added "1.0"
   :static true}
  ([] {})
  ([& keyvals]
    (. clojure.lang.PersistentHashMap (create keyvals))))

(defn hash-set
  "Returns a new hash set with supplied keys.  Any equal keys are
  handled as if by repeated uses of conj."
  {:added "1.0"
   :static true}
  ([] #{})
  ([& keys]
    (clojure.lang.PersistentHashSet/create keys)))

(defn sorted-map
  "keyval => key val
  Returns a new sorted map with supplied mappings.  If any keys are
  equal, they are handled as if by repeated uses of assoc."
  {:added "1.0"
   :static true}
  ([& keyvals]
    (clojure.lang.PersistentTreeMap/create keyvals)))

(defn sorted-map-by
  "keyval => key val
  Returns a new sorted map with supplied mappings, using the supplied
  comparator.  If any keys are equal, they are handled as if by
  repeated uses of assoc."
  {:added "1.0"
   :static true}
  ([comparator & keyvals]
    (clojure.lang.PersistentTreeMap/create comparator keyvals)))

(defn sorted-set
  "Returns a new sorted set with supplied keys.  Any equal keys are
  handled as if by repeated uses of conj."
  {:added "1.0"
   :static true}
  ([& keys]
    (clojure.lang.PersistentTreeSet/create keys)))

(defn sorted-set-by
  "Returns a new sorted set with supplied keys, using the supplied
  comparator.  Any equal keys are handled as if by repeated uses of
  conj."
  {:added "1.1"
   :static true}
  ([comparator & keys]
    (clojure.lang.PersistentTreeSet/create comparator keys)))


;;;;;;;;;;;;;;;;;;;;
(defn nil?
  "Returns true if x is nil, false otherwise."
  {:tag Boolean
   :added "1.0"
   :static true
   :inline (fn [x] (list 'clojure.lang.Util/identical x nil))}
  [x] (clojure.lang.Util/identical x nil))

(def

  ^{:doc "Like defn, but the resulting function name is declared as a
  macro and will be used as a macro by the compiler when it is
  called."
    :arglists '([name doc-string? attr-map? [params*] body]
                 [name doc-string? attr-map? ([params*] body) + attr-map?])
    :added "1.0"}
  defmacro (fn [&form &env
                name & args]
             (let [prefix (loop [p (list name) args args]
                            (let [f (first args)]
                              (if (string? f)
                                (recur (cons f p) (next args))
                                (if (map? f)
                                  (recur (cons f p) (next args))
                                  p))))
                   fdecl (loop [fd args]
                           (if (string? (first fd))
                             (recur (next fd))
                             (if (map? (first fd))
                               (recur (next fd))
                               fd)))
                   fdecl (if (vector? (first fdecl))
                           (list fdecl)
                           fdecl)
                   add-implicit-args (fn [fd]
                                       (let [args (first fd)]
                                         (cons (vec (cons '&form (cons '&env args))) (next fd))))
                   add-args (fn [acc ds]
                              (if (nil? ds)
                                acc
                                (let [d (first ds)]
                                  (if (map? d)
                                    (conj acc d)
                                    (recur (conj acc (add-implicit-args d)) (next ds))))))
                   fdecl (seq (add-args [] fdecl))
                   decl (loop [p prefix d fdecl]
                          (if p
                            (recur (next p) (cons (first p) d))
                            d))]
               (list 'do
                 (cons `defn decl)
                 (list '. (list 'var name) '(setMacro))
                 (list 'var name)))))


(. (var defmacro) (setMacro))

(defmacro when
  "Evaluates test. If logical true, evaluates body in an implicit do."
  {:added "1.0"}
  [test & body]
  (list 'if test (cons 'do body)))

(defmacro when-not
  "Evaluates test. If logical false, evaluates body in an implicit do."
  {:added "1.0"}
  [test & body]
  (list 'if test nil (cons 'do body)))

(defn false?
  "Returns true if x is the value false, false otherwise."
  {:tag Boolean,
   :added "1.0"
   :static true}
  [x] (clojure.lang.Util/identical x false))

(defn true?
  "Returns true if x is the value true, false otherwise."
  {:tag Boolean,
   :added "1.0"
   :static true}
  [x] (clojure.lang.Util/identical x true))

(defn not
  "Returns true if x is logical false, false otherwise."
  {:tag Boolean
   :added "1.0"
   :static true}
  [x] (if x false true))

(defn some?
  "Returns true if x is not nil, false otherwise."
  {:tag Boolean
   :added "1.6"
   :static true}
  [x] (not (nil? x)))

(defn str
  "With no args, returns the empty string. With one arg x, returns
  x.toString().  (str nil) returns the empty string. With more than
  one arg, returns the concatenation of the str values of the args."
  {:tag String
   :added "1.0"
   :static true}
  (^String [] "")
  (^String [^Object x]
    (if (nil? x) "" (. x (toString))))
  (^String [x & ys]
    ((fn [^StringBuilder sb more]
       (if more
         (recur (. sb (append (str (first more)))) (next more))
         (str sb)))
      (new StringBuilder (str x)) ys)))


(defn symbol?
  "Return true if x is a Symbol"
  {:added "1.0"
   :static true}
  [x] (instance? clojure.lang.Symbol x))

(defn keyword?
  "Return true if x is a Keyword"
  {:added "1.0"
   :static true}
  [x] (instance? clojure.lang.Keyword x))

(defn symbol
  "Returns a Symbol with the given namespace and name."
  {:tag clojure.lang.Symbol
   :added "1.0"
   :static true}
  ([name] (if (symbol? name) name (clojure.lang.Symbol/intern name)))
  ([ns name] (clojure.lang.Symbol/intern ns name)))

(defn gensym
  "Returns a new symbol with a unique name. If a prefix string is
  supplied, the name is prefix# where # is some unique number. If
  prefix is not supplied, the prefix is 'G__'."
  {:added "1.0"
   :static true}
  ([] (gensym "G__"))
  ([prefix-string] (. clojure.lang.Symbol (intern (str prefix-string (str (. clojure.lang.RT (nextID))))))))

(defmacro cond
  "Takes a set of test/expr pairs. It evaluates each test one at a
  time.  If a test returns logical true, cond evaluates and returns
  the value of the corresponding expr and doesn't evaluate any of the
  other tests or exprs. (cond) returns nil."
  {:added "1.0"}
  [& clauses]
  (when clauses
    (list 'if (first clauses)
      (if (next clauses)
        (second clauses)
        (throw (IllegalArgumentException.
                 "cond requires an even number of forms")))
      (cons 'clojure.core/cond (next (next clauses))))))

(defn keyword
  "Returns a Keyword with the given namespace and name.  Do not use :
  in the keyword strings, it will be added automatically."
  {:tag clojure.lang.Keyword
   :added "1.0"
   :static true}
  ([name] (cond (keyword? name) name
            (symbol? name) (clojure.lang.Keyword/intern ^clojure.lang.Symbol name)
            (string? name) (clojure.lang.Keyword/intern ^String name)))
  ([ns name] (clojure.lang.Keyword/intern ns name)))

(defn find-keyword
  "Returns a Keyword with the given namespace and name if one already
  exists.  This function will not intern a new keyword. If the keyword
  has not already been interned, it will return nil.  Do not use :
  in the keyword strings, it will be added automatically."
  {:tag clojure.lang.Keyword
   :added "1.3"
   :static true}
  ([name] (cond (keyword? name) name
            (symbol? name) (clojure.lang.Keyword/find ^clojure.lang.Symbol name)
            (string? name) (clojure.lang.Keyword/find ^String name)))
  ([ns name] (clojure.lang.Keyword/find ns name)))


(defn spread
  {:private true
   :static true}
  [arglist]
  (cond
    (nil? arglist) nil
    (nil? (next arglist)) (seq (first arglist))
    :else (cons (first arglist) (spread (next arglist)))))

(defn list*
  "Creates a new list containing the items prepended to the rest, the
  last of which will be treated as a sequence."
  {:added "1.0"
   :static true}
  ([args] (seq args))
  ([a args] (cons a args))
  ([a b args] (cons a (cons b args)))
  ([a b c args] (cons a (cons b (cons c args))))
  ([a b c d & more]
    (cons a (cons b (cons c (cons d (spread more)))))))

(defn apply
  "Applies fn f to the argument list formed by prepending intervening arguments to args."
  {:added "1.0"
   :static true}
  ([^clojure.lang.IFn f args]
    (. f (applyTo (seq args))))
  ([^clojure.lang.IFn f x args]
    (. f (applyTo (list* x args))))
  ([^clojure.lang.IFn f x y args]
    (. f (applyTo (list* x y args))))
  ([^clojure.lang.IFn f x y z args]
    (. f (applyTo (list* x y z args))))
  ([^clojure.lang.IFn f a b c d & args]
    (. f (applyTo (cons a (cons b (cons c (cons d (spread args)))))))))

(defn vary-meta
  "Returns an object of the same type and value as obj, with
   (apply f (meta obj) args) as its metadata."
  {:added "1.0"
   :static true}
  [obj f & args]
  (with-meta obj (apply f (meta obj) args)))

(defmacro lazy-seq
  "Takes a body of expressions that returns an ISeq or nil, and yields
  a Seqable object that will invoke the body only the first time seq
  is called, and will cache the result and return it on all subsequent
  seq calls. See also - realized?"
  {:added "1.0"}
  [& body]
  (list 'new 'clojure.lang.LazySeq (list* '^{:once true} fn* [] body)))

(defn ^:static ^clojure.lang.ChunkBuffer chunk-buffer ^clojure.lang.ChunkBuffer [capacity]
  (clojure.lang.ChunkBuffer. capacity))

(defn ^:static chunk-append [^clojure.lang.ChunkBuffer b x]
  (.add b x))

(defn ^:static ^clojure.lang.IChunk chunk [^clojure.lang.ChunkBuffer b]
  (.chunk b))

(defn ^:static ^clojure.lang.IChunk chunk-first ^clojure.lang.IChunk [^clojure.lang.IChunkedSeq s]
  (.chunkedFirst s))

(defn ^:static ^clojure.lang.ISeq chunk-rest ^clojure.lang.ISeq [^clojure.lang.IChunkedSeq s]
  (.chunkedMore s))

(defn ^:static ^clojure.lang.ISeq chunk-next ^clojure.lang.ISeq [^clojure.lang.IChunkedSeq s]
  (.chunkedNext s))

(defn ^:static chunk-cons [chunk rest]
  (if (clojure.lang.Numbers/isZero (clojure.lang.RT/count chunk))
    rest
    (clojure.lang.ChunkedCons. chunk rest)))

(defn ^:static chunked-seq? [s]
  (instance? clojure.lang.IChunkedSeq s))

(defn concat
  "Returns a lazy seq representing the concatenation of the elements in the supplied colls."
  {:added "1.0"
   :static true}
  ([] (lazy-seq nil))
  ([x] (lazy-seq x))
  ([x y]
    (lazy-seq
      (let [s (seq x)]
        (if s
          (if (chunked-seq? s)
            (chunk-cons (chunk-first s) (concat (chunk-rest s) y))
            (cons (first s) (concat (rest s) y)))
          y))))
  ([x y & zs]
    (let [cat (fn cat [xys zs]
                (lazy-seq
                  (let [xys (seq xys)]
                    (if xys
                      (if (chunked-seq? xys)
                        (chunk-cons (chunk-first xys)
                          (cat (chunk-rest xys) zs))
                        (cons (first xys) (cat (rest xys) zs)))
                      (when zs
                        (cat (first zs) (next zs)))))))]
      (cat (concat x y) zs))))

;;;;;;;;;;;;;;;;at this point all the support for syntax-quote exists;;;;;;;;;;;;;;;;;;;;;;

(defmacro if-not
  "Evaluates test. If logical false, evaluates and returns then expr, 
  otherwise else expr, if supplied, else nil."
  {:added "1.0"}
  ([test then] `(if-not ~test ~then nil))
  ([test then else]
    `(if (not ~test) ~then ~else)))

(defn identical?
  "Tests if 2 arguments are the same object"
  {:inline (fn [x y] `(. clojure.lang.Util identical ~x ~y))
   :inline-arities #{2}
   :added "1.0"}
  ([x y] (clojure.lang.Util/identical x y)))

;equiv-based
(defn =
  "Equality. Returns true if x equals y, false if not. Same as
  Java x.equals(y) except it also works for nil, and compares
  numbers and collections in a type-independent manner.  Clojure's immutable data
  structures define equals() (and thus =) as a value, not an identity,
  comparison."
  {:inline (fn [x y] `(. clojure.lang.Util equiv ~x ~y))
   :inline-arities #{2}
   :added "1.0"}
  ([x] true)
  ([x y] (clojure.lang.Util/equiv x y))
  ([x y & more]
    (if (clojure.lang.Util/equiv x y)
      (if (next more)
        (recur y (first more) (next more))
        (clojure.lang.Util/equiv y (first more)))
      false)))

(defn not=
  "Same as (not (= obj1 obj2))"
  {:tag Boolean
   :added "1.0"
   :static true}
  ([x] false)
  ([x y] (not (= x y)))
  ([x y & more]
    (not (apply = x y more))))



(defn compare
  "Comparator. Returns a negative number, zero, or a positive number
  when x is logically 'less than', 'equal to', or 'greater than'
  y. Same as Java x.compareTo(y) except it also works for nil, and
  compares numbers and collections in a type-independent manner. x
  must implement Comparable"
  {
    :inline (fn [x y] `(. clojure.lang.Util compare ~x ~y))
    :added "1.0"}
  [x y] (. clojure.lang.Util (compare x y)))

(defmacro and
  "Evaluates exprs one at a time, from left to right. If a form
  returns logical false (nil or false), and returns that value and
  doesn't evaluate any of the other expressions, otherwise it returns
  the value of the last expr. (and) returns true."
  {:added "1.0"}
  ([] true)
  ([x] x)
  ([x & next]
    `(let [and# ~x]
       (if and# (and ~@next) and#))))

(defmacro or
  "Evaluates exprs one at a time, from left to right. If a form
  returns a logical true value, or returns that value and doesn't
  evaluate any of the other expressions, otherwise it returns the
  value of the last expression. (or) returns nil."
  {:added "1.0"}
  ([] nil)
  ([x] x)
  ([x & next]
    `(let [or# ~x]
       (if or# or# (or ~@next)))))

;;;;;;;;;;;;;;;;;;; sequence fns  ;;;;;;;;;;;;;;;;;;;;;;;
(defn zero?
  "Returns true if num is zero, else false"
  {
    :inline (fn [x] `(. clojure.lang.Numbers (isZero ~x)))
    :added "1.0"}
  [x] (. clojure.lang.Numbers (isZero x)))

(defn count
  "Returns the number of items in the collection. (count nil) returns
  0.  Also works on strings, arrays, and Java Collections and Maps"
  {
    :inline (fn [x] `(. clojure.lang.RT (count ~x)))
    :added "1.0"}
  [coll] (clojure.lang.RT/count coll))

(defn int
  "Coerce to int"
  {
    :inline (fn [x] `(. clojure.lang.RT (~(if *unchecked-math* 'uncheckedIntCast 'intCast) ~x)))
    :added "1.0"}
  [x] (. clojure.lang.RT (intCast x)))

(defn nth
  "Returns the value at the index. get returns nil if index out of
  bounds, nth throws an exception unless not-found is supplied.  nth
  also works for strings, Java arrays, regex Matchers and Lists, and,
  in O(n) time, for sequences."
  {:inline (fn [c i & nf] `(. clojure.lang.RT (nth ~c ~i ~@nf)))
   :inline-arities #{2 3}
   :added "1.0"}
  ([coll index] (. clojure.lang.RT (nth coll index)))
  ([coll index not-found] (. clojure.lang.RT (nth coll index not-found))))

(defn <
  "Returns non-nil if nums are in monotonically increasing order,
  otherwise false."
  {:inline (fn [x y] `(. clojure.lang.Numbers (lt ~x ~y)))
   :inline-arities #{2}
   :added "1.0"}
  ([x] true)
  ([x y] (. clojure.lang.Numbers (lt x y)))
  ([x y & more]
    (if (< x y)
      (if (next more)
        (recur y (first more) (next more))
        (< y (first more)))
      false)))

(defn inc
  "Returns a number one greater than num. Does not auto-promote
  longs, will throw on overflow. See also: inc'"
  {:inline (fn [x] `(. clojure.lang.Numbers (~(if *unchecked-math* 'unchecked_inc 'inc) ~x)))
   :added "1.2"}
  [x] (. clojure.lang.Numbers (inc x)))

;; reduce is defined again later after InternalReduce loads
(defn ^:private ^:static
  reduce1
  ([f coll]
    (let [s (seq coll)]
      (if s
        (reduce1 f (first s) (next s))
        (f))))
  ([f val coll]
    (let [s (seq coll)]
      (if s
        (if (chunked-seq? s)
          (recur f
            (.reduce (chunk-first s) f val)
            (chunk-next s))
          (recur f (f val (first s)) (next s)))
        val))))

(defn reverse
  "Returns a seq of the items in coll in reverse order. Not lazy."
  {:added "1.0"
   :static true}
  [coll]
  (reduce1 conj () coll))

;;math stuff
(defn ^:private nary-inline
  ([op] (nary-inline op op))
  ([op unchecked-op]
    (fn
      ([x] (let [op (if *unchecked-math* unchecked-op op)]
             `(. clojure.lang.Numbers (~op ~x))))
      ([x y] (let [op (if *unchecked-math* unchecked-op op)]
               `(. clojure.lang.Numbers (~op ~x ~y))))
      ([x y & more]
        (let [op (if *unchecked-math* unchecked-op op)]
          (reduce1
            (fn [a b] `(. clojure.lang.Numbers (~op ~a ~b)))
            `(. clojure.lang.Numbers (~op ~x ~y)) more))))))

(defn ^:private >1? [n] (clojure.lang.Numbers/gt n 1))
(defn ^:private >0? [n] (clojure.lang.Numbers/gt n 0))

(defn +
  "Returns the sum of nums. (+) returns 0. Does not auto-promote
  longs, will throw on overflow. See also: +'"
  {:inline (nary-inline 'add 'unchecked_add)
   :inline-arities >1?
   :added "1.2"}
  ([] 0)
  ([x] (cast Number x))
  ([x y] (. clojure.lang.Numbers (add x y)))
  ([x y & more]
    (reduce1 + (+ x y) more)))

(defn *
  "Returns the product of nums. (*) returns 1. Does not auto-promote
  longs, will throw on overflow. See also: *'"
  {:inline (nary-inline 'multiply 'unchecked_multiply)
   :inline-arities >1?
   :added "1.2"}
  ([] 1)
  ([x] (cast Number x))
  ([x y] (. clojure.lang.Numbers (multiply x y)))
  ([x y & more]
    (reduce1 * (* x y) more)))

(defn /
  "If no denominators are supplied, returns 1/numerator,
  else returns numerator divided by all of the denominators."
  {:inline (nary-inline 'divide)
   :inline-arities >1?
   :added "1.0"}
  ([x] (/ 1 x))
  ([x y] (. clojure.lang.Numbers (divide x y)))
  ([x y & more]
    (reduce1 / (/ x y) more)))

(defn -
  "If no ys are supplied, returns the negation of x, else subtracts
  the ys from x and returns the result. Does not auto-promote
  longs, will throw on overflow. See also: -'"
  {:inline (nary-inline 'minus 'unchecked_minus)
   :inline-arities >0?
   :added "1.2"}
  ([x] (. clojure.lang.Numbers (minus x)))
  ([x y] (. clojure.lang.Numbers (minus x y)))
  ([x y & more]
    (reduce1 - (- x y) more)))

(defn <=
  "Returns non-nil if nums are in monotonically non-decreasing order,
  otherwise false."
  {:inline (fn [x y] `(. clojure.lang.Numbers (lte ~x ~y)))
   :inline-arities #{2}
   :added "1.0"}
  ([x] true)
  ([x y] (. clojure.lang.Numbers (lte x y)))
  ([x y & more]
    (if (<= x y)
      (if (next more)
        (recur y (first more) (next more))
        (<= y (first more)))
      false)))

(defn >
  "Returns non-nil if nums are in monotonically decreasing order,
  otherwise false."
  {:inline (fn [x y] `(. clojure.lang.Numbers (gt ~x ~y)))
   :inline-arities #{2}
   :added "1.0"}
  ([x] true)
  ([x y] (. clojure.lang.Numbers (gt x y)))
  ([x y & more]
    (if (> x y)
      (if (next more)
        (recur y (first more) (next more))
        (> y (first more)))
      false)))

(defn >=
  "Returns non-nil if nums are in monotonically non-increasing order,
  otherwise false."
  {:inline (fn [x y] `(. clojure.lang.Numbers (gte ~x ~y)))
   :inline-arities #{2}
   :added "1.0"}
  ([x] true)
  ([x y] (. clojure.lang.Numbers (gte x y)))
  ([x y & more]
    (if (>= x y)
      (if (next more)
        (recur y (first more) (next more))
        (>= y (first more)))
      false)))

(defn ==
  "Returns non-nil if nums all have the equivalent
  value (type-independent), otherwise false"
  {:inline (fn [x y] `(. clojure.lang.Numbers (equiv ~x ~y)))
   :inline-arities #{2}
   :added "1.0"}
  ([x] true)
  ([x y] (. clojure.lang.Numbers (equiv x y)))
  ([x y & more]
    (if (== x y)
      (if (next more)
        (recur y (first more) (next more))
        (== y (first more)))
      false)))

(defn max
  "Returns the greatest of the nums."
  {:added "1.0"
   :inline-arities >1?
   :inline (nary-inline 'max)}
  ([x] x)
  ([x y] (. clojure.lang.Numbers (max x y)))
  ([x y & more]
    (reduce1 max (max x y) more)))

(defn min
  "Returns the least of the nums."
  {:added "1.0"
   :inline-arities >1?
   :inline (nary-inline 'min)}
  ([x] x)
  ([x y] (. clojure.lang.Numbers (min x y)))
  ([x y & more]
    (reduce1 min (min x y) more)))

(defn dec
  "Returns a number one less than num. Does not auto-promote
  longs, will throw on overflow. See also: dec'"
  {:inline (fn [x] `(. clojure.lang.Numbers (~(if *unchecked-math* 'unchecked_dec 'dec) ~x)))
   :added "1.2"}
  [x] (. clojure.lang.Numbers (dec x)))


(defn unchecked-inc
  "Returns a number one greater than x, a long.
  Note - uses a primitive operator subject to overflow."
  {:inline (fn [x] `(. clojure.lang.Numbers (unchecked_inc ~x)))
   :added "1.0"}
  [x] (. clojure.lang.Numbers (unchecked_inc x)))

(defn pos?
  "Returns true if num is greater than zero, else false"
  {
    :inline (fn [x] `(. clojure.lang.Numbers (isPos ~x)))
    :added "1.0"}
  [x] (. clojure.lang.Numbers (isPos x)))

(defn neg?
  "Returns true if num is less than zero, else false"
  {
    :inline (fn [x] `(. clojure.lang.Numbers (isNeg ~x)))
    :added "1.0"}
  [x] (. clojure.lang.Numbers (isNeg x)))

;;Bit ops

(defn bit-and
  "Bitwise and"
  {:inline (nary-inline 'and)
   :inline-arities >1?
   :added "1.0"}
  ([x y] (. clojure.lang.Numbers and x y))
  ([x y & more]
    (reduce1 bit-and (bit-and x y) more)))

(defn bit-or
  "Bitwise or"
  {:inline (nary-inline 'or)
   :inline-arities >1?
   :added "1.0"}
  ([x y] (. clojure.lang.Numbers or x y))
  ([x y & more]
    (reduce1 bit-or (bit-or x y) more)))

(defn integer?
  "Returns true if n is an integer"
  {:added "1.0"
   :static true}
  [n]
  (or (instance? Integer n)
    (instance? Long n)
    (instance? clojure.lang.BigInt n)
    (instance? BigInteger n)
    (instance? Short n)
    (instance? Byte n)))

(defn even?
  "Returns true if n is even, throws an exception if n is not an integer"
  {:added "1.0"
   :static true}
  [n] (if (integer? n)
        (zero? (bit-and (clojure.lang.RT/uncheckedLongCast n) 1))
        (throw (IllegalArgumentException. (str "Argument must be an integer: " n)))))

(defn odd?
  "Returns true if n is odd, throws an exception if n is not an integer"
  {:added "1.0"
   :static true}
  [n] (not (even? n)))


;;

(defn complement
  "Takes a fn f and returns a fn that takes the same arguments as f,
  has the same effects, if any, and returns the opposite truth value."
  {:added "1.0"
   :static true}
  [f]
  (fn
    ([] (not (f)))
    ([x] (not (f x)))
    ([x y] (not (f x y)))
    ([x y & zs] (not (apply f x y zs)))))

(defn constantly
  "Returns a function that takes any number of arguments and returns x."
  {:added "1.0"
   :static true}
  [x] (fn [& args] x))

(defn identity
  "Returns its argument."
  {:added "1.0"
   :static true}
  [x] x)

;;Collection stuff

;;list stuff
(defn peek
  "For a list or queue, same as first, for a vector, same as, but much
  more efficient than, last. If the collection is empty, returns nil."
  {:added "1.0"
   :static true}
  [coll] (. clojure.lang.RT (peek coll)))

(defn pop
  "For a list or queue, returns a new list/queue without the first
  item, for a vector, returns a new vector without the last item. If
  the collection is empty, throws an exception.  Note - not the same
  as next/butlast."
  {:added "1.0"
   :static true}
  [coll] (. clojure.lang.RT (pop coll)))

;;map stuff

(defn contains?
  "Returns true if key is present in the given collection, otherwise
  returns false.  Note that for numerically indexed collections like
  vectors and Java arrays, this tests if the numeric key is within the
  range of indexes. 'contains?' operates constant or logarithmic time;
  it will not perform a linear search for a value.  See also 'some'."
  {:added "1.0"
   :static true}
  [coll key] (. clojure.lang.RT (contains coll key)))

(defn get
  "Returns the value mapped to key, not-found or nil if key not present."
  {:inline (fn [m k & nf] `(. clojure.lang.RT (get ~m ~k ~@nf)))
   :inline-arities #{2 3}
   :added "1.0"}
  ([map key]
    (. clojure.lang.RT (get map key)))
  ([map key not-found]
    (. clojure.lang.RT (get map key not-found))))

(defn dissoc
  "dissoc[iate]. Returns a new map of the same (hashed/sorted) type,
  that does not contain a mapping for key(s)."
  {:added "1.0"
   :static true}
  ([map] map)
  ([map key]
    (. clojure.lang.RT (dissoc map key)))
  ([map key & ks]
    (let [ret (dissoc map key)]
      (if ks
        (recur ret (first ks) (next ks))
        ret))))

(defn disj
  "disj[oin]. Returns a new set of the same (hashed/sorted) type, that
  does not contain key(s)."
  {:added "1.0"
   :static true}
  ([set] set)
  ([^clojure.lang.IPersistentSet set key]
    (when set
      (. set (disjoin key))))
  ([set key & ks]
    (when set
      (let [ret (disj set key)]
        (if ks
          (recur ret (first ks) (next ks))
          ret)))))

(defn find
  "Returns the map entry for key, or nil if key not present."
  {:added "1.0"
   :static true}
  [map key] (. clojure.lang.RT (find map key)))

(defn select-keys
  "Returns a map containing only those entries in map whose key is in keys"
  {:added "1.0"
   :static true}
  [map keyseq]
  (loop [ret {} keys (seq keyseq)]
    (if keys
      (let [entry (. clojure.lang.RT (find map (first keys)))]
        (recur
          (if entry
            (conj ret entry)
            ret)
          (next keys)))
      (with-meta ret (meta map)))))

(defn keys
  "Returns a sequence of the map's keys, in the same order as (seq map)."
  {:added "1.0"
   :static true}
  [map] (. clojure.lang.RT (keys map)))

(defn vals
  "Returns a sequence of the map's values, in the same order as (seq map)."
  {:added "1.0"
   :static true}
  [map] (. clojure.lang.RT (vals map)))

(defn key
  "Returns the key of the map entry."
  {:added "1.0"
   :static true}
  [^java.util.Map$Entry e]
  (. e (getKey)))

(defn val
  "Returns the value in the map entry."
  {:added "1.0"
   :static true}
  [^java.util.Map$Entry e]
  (. e (getValue)))

(defn name
  "Returns the name String of a string, symbol or keyword."
  {:tag String
   :added "1.0"
   :static true}
  [x]
  (if (string? x) x (. ^clojure.lang.Named x (getName))))

(defn namespace
  "Returns the namespace String of a symbol or keyword, or nil if not present."
  {:tag String
   :added "1.0"
   :static true}
  [^clojure.lang.Named x]
  (. x (getNamespace)))


(defmacro ..
  "form => fieldName-symbol or (instanceMethodName-symbol args*)

  Expands into a member access (.) of the first member on the first
  argument, followed by the next member on the result, etc. For
  instance:

  (.. System (getProperties) (get \"os.name\"))

  expands to:

  (. (. System (getProperties)) (get \"os.name\"))

  but is easier to write, read, and understand."
  {:added "1.0"}
  ([x form] `(. ~x ~form))
  ([x form & more] `(.. (. ~x ~form) ~@more)))

(defmacro ->
  "Threads the expr through the forms. Inserts x as the
  second item in the first form, making a list of it if it is not a
  list already. If there are more forms, inserts the first form as the
  second item in second form, etc."
  {:added "1.0"}
  [x & forms]
  (loop [x x, forms forms]
    (if forms
      (let [form (first forms)
            threaded (if (seq? form)
                       (with-meta `(~(first form) ~x ~@(next form)) (meta form))
                       (list form x))]
        (recur threaded (next forms)))
      x)))

(def map)

(defn ^:private check-valid-options
  "Throws an exception if the given option map contains keys not listed
  as valid, else returns nil."
  [options & valid-keys]
  (when (seq (apply disj (apply hash-set (keys options)) valid-keys))
    (throw
      (IllegalArgumentException.
        (apply str "Only these options are valid: "
          (first valid-keys)
          (map #(str ", " %) (rest valid-keys)))))))

;;multimethods
(def global-hierarchy)

(defmacro defmulti
  "Creates a new multimethod with the associated dispatch function.
  The docstring and attribute-map are optional.

  Options are key-value pairs and may be one of:

  :default

  The default dispatch value, defaults to :default

  :hierarchy

  The value used for hierarchical dispatch (e.g. ::square is-a ::shape)

  Hierarchies are type-like relationships that do not depend upon type
  inheritance. By default Clojure's multimethods dispatch off of a
  global hierarchy map.  However, a hierarchy relationship can be
  created with the derive function used to augment the root ancestor
  created with make-hierarchy.

  Multimethods expect the value of the hierarchy option to be supplied as
  a reference type e.g. a var (i.e. via the Var-quote dispatch macro #'
  or the var special form)."
  {:arglists '([name docstring? attr-map? dispatch-fn & options])
   :added "1.0"}
  [mm-name & options]
  (let [docstring (if (string? (first options))
                    (first options)
                    nil)
        options (if (string? (first options))
                  (next options)
                  options)
        m (if (map? (first options))
            (first options)
            {})
        options (if (map? (first options))
                  (next options)
                  options)
        dispatch-fn (first options)
        options (next options)
        m (if docstring
            (assoc m :doc docstring)
            m)
        m (if (meta mm-name)
            (conj (meta mm-name) m)
            m)]
    (when (= (count options) 1)
      (throw (Exception. "The syntax for defmulti has changed. Example: (defmulti name dispatch-fn :default dispatch-value)")))
    (let [options (apply hash-map options)
          default (get options :default :default )
          hierarchy (get options :hierarchy #'global-hierarchy)]
      (check-valid-options options :default :hierarchy )
      `(let [v# (def ~mm-name)]
         (when-not (and (.hasRoot v#) (instance? clojure.lang.MultiFn (deref v#)))
           (def ~(with-meta mm-name m)
             (new clojure.lang.MultiFn ~(name mm-name) ~dispatch-fn ~default ~hierarchy)))))))

(defmacro defmethod
  "Creates and installs a new method of multimethod associated with dispatch-value. "
  {:added "1.0"}
  [multifn dispatch-val & fn-tail]
  `(. ~(with-meta multifn {:tag 'clojure.lang.MultiFn}) addMethod ~dispatch-val (fn ~@fn-tail)))

(defn prefer-method
  "Causes the multimethod to prefer matches of dispatch-val-x over dispatch-val-y
   when there is a conflict"
  {:added "1.0"
   :static true}
  [^clojure.lang.MultiFn multifn dispatch-val-x dispatch-val-y]
  (. multifn preferMethod dispatch-val-x dispatch-val-y))

;;;;;;;;; var stuff

(defmacro ^{:private true} assert-args
  [& pairs]
  `(do (when-not ~(first pairs)
         (throw (IllegalArgumentException.
                  (str (first ~'&form) " requires " ~(second pairs) " in " ~'*ns* ":" (:line (meta ~'&form))))))
     ~(let [more (nnext pairs)]
        (when more
          (list* `assert-args more)))))

(defmacro if-let
  "bindings => binding-form test

  If test is true, evaluates then with binding-form bound to the value of
  test, if not, yields else"
  {:added "1.0"}
  ([bindings then]
    `(if-let ~bindings ~then nil))
  ([bindings then else & oldform]
    (assert-args
      (vector? bindings) "a vector for its binding"
      (nil? oldform) "1 or 2 forms after binding vector"
      (= 2 (count bindings)) "exactly 2 forms in binding vector")
    (let [form (bindings 0) tst (bindings 1)]
      `(let [temp# ~tst]
         (if temp#
           (let [~form temp#]
             ~then)
           ~else)))))

(defmacro when-let
  "bindings => binding-form test

  When test is true, evaluates body with binding-form bound to the value of test"
  {:added "1.0"}
  [bindings & body]
  (assert-args
    (vector? bindings) "a vector for its binding"
    (= 2 (count bindings)) "exactly 2 forms in binding vector")
  (let [form (bindings 0) tst (bindings 1)]
    `(let [temp# ~tst]
       (when temp#
         (let [~form temp#]
           ~@body)))))

(defmacro when-some
  "bindings => binding-form test

   When test is not nil, evaluates body with binding-form bound to the
   value of test"
  {:added "1.6"}
  [bindings & body]
  (assert-args
    (vector? bindings) "a vector for its binding"
    (= 2 (count bindings)) "exactly 2 forms in binding vector")
  (let [form (bindings 0) tst (bindings 1)]
    `(let [temp# ~tst]
       (if (nil? temp#)
         nil
         (let [~form temp#]
           ~@body)))))

(defn push-thread-bindings
  "WARNING: This is a low-level function. Prefer high-level macros like
  binding where ever possible.

  Takes a map of Var/value pairs. Binds each Var to the associated value for
  the current thread. Each call *MUST* be accompanied by a matching call to
  pop-thread-bindings wrapped in a try-finally!

      (push-thread-bindings bindings)
      (try
        ...
        (finally
          (pop-thread-bindings)))"
  {:added "1.1"
   :static true}
  [bindings]
  (clojure.lang.Var/pushThreadBindings bindings))

(defn pop-thread-bindings
  "Pop one set of bindings pushed with push-binding before. It is an error to
  pop bindings without pushing before."
  {:added "1.1"
   :static true}
  []
  (clojure.lang.Var/popThreadBindings))

(defn get-thread-bindings
  "Get a map with the Var/value pairs which is currently in effect for the
  current thread."
  {:added "1.1"
   :static true}
  []
  (clojure.lang.Var/getThreadBindings))

(defmacro binding
  "binding => var-symbol init-expr

  Creates new bindings for the (already-existing) vars, with the
  supplied initial values, executes the exprs in an implicit do, then
  re-establishes the bindings that existed before.  The new bindings
  are made in parallel (unlike let); all init-exprs are evaluated
  before the vars are bound to their new values."
  {:added "1.0"}
  [bindings & body]
  (assert-args
    (vector? bindings) "a vector for its binding"
    (even? (count bindings)) "an even number of forms in binding vector")
  (let [var-ize (fn [var-vals]
                  (loop [ret [] vvs (seq var-vals)]
                    (if vvs
                      (recur (conj (conj ret `(var ~(first vvs))) (second vvs))
                        (next (next vvs)))
                      (seq ret))))]
    `(let []
       (push-thread-bindings (hash-map ~@(var-ize bindings)))
       (try
         ~@body
         (finally
           (pop-thread-bindings))))))

(defn with-bindings*
  "Takes a map of Var/value pairs. Installs for the given Vars the associated
  values as thread-local bindings. Then calls f with the supplied arguments.
  Pops the installed bindings after f returned. Returns whatever f returns."
  {:added "1.1"
   :static true}
  [binding-map f & args]
  (push-thread-bindings binding-map)
  (try
    (apply f args)
    (finally
      (pop-thread-bindings))))

(defmacro with-bindings
  "Takes a map of Var/value pairs. Installs for the given Vars the associated
  values as thread-local bindings. The executes body. Pops the installed
  bindings after body was evaluated. Returns the value of body."
  {:added "1.1"}
  [binding-map & body]
  `(with-bindings* ~binding-map (fn [] ~@body)))

(defn agent-error
  "Returns the exception thrown during an asynchronous action of the
  agent if the agent is failed.  Returns nil if the agent is not
  failed."
  {:added "1.2"
   :static true}
  [^clojure.lang.Agent a] (.getError a))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Refs ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn ^{:private true}
  setup-reference [^clojure.lang.ARef r options]
  (let [opts (apply hash-map options)]
    (when (:meta opts)
      (.resetMeta r (:meta opts)))
    (when (:validator opts)
      (.setValidator r (:validator opts)))
    r))

(defn ref
  "Creates and returns a Ref with an initial value of x and zero or
  more options (in any order):

  :meta metadata-map

  :validator validate-fn

  :min-history (default 0)
  :max-history (default 10)

  If metadata-map is supplied, it will become the metadata on the
  ref. validate-fn must be nil or a side-effect-free fn of one
  argument, which will be passed the intended new state on any state
  change. If the new state is unacceptable, the validate-fn should
  return false or throw an exception. validate-fn will be called on
  transaction commit, when all refs have their final values.

  Normally refs accumulate history dynamically as needed to deal with
  read demands. If you know in advance you will need history you can
  set :min-history to ensure it will be available when first needed (instead
  of after a read fault). History is limited, and the limit can be set
  with :max-history."
  {:added "1.0"
   :static true
   }
  ([x] (new clojure.lang.Ref x))
  ([x & options]
    (let [r ^clojure.lang.Ref (setup-reference (ref x) options)
          opts (apply hash-map options)]
      (when (:max-history opts)
        (.setMaxHistory r (:max-history opts)))
      (when (:min-history opts)
        (.setMinHistory r (:min-history opts)))
      r)))

(defn ^:private deref-future
  ([^java.util.concurrent.Future fut]
    (.get fut))
  ([^java.util.concurrent.Future fut timeout-ms timeout-val]
    (try (.get fut timeout-ms java.util.concurrent.TimeUnit/MILLISECONDS)
      (catch java.util.concurrent.TimeoutException e
        timeout-val))))

(defn deref
  "Also reader macro: @ref/@agent/@var/@atom/@delay/@future/@promise. Within a transaction,
  returns the in-transaction-value of ref, else returns the
  most-recently-committed value of ref. When applied to a var, agent
  or atom, returns its current state. When applied to a delay, forces
  it if not already forced. When applied to a future, will block if
  computation not complete. When applied to a promise, will block
  until a value is delivered.  The variant taking a timeout can be
  used for blocking references (futures and promises), and will return
  timeout-val if the timeout (in milliseconds) is reached before a
  value is available. See also - realized?."
  {:added "1.0"
   :static true}
  ([ref] (if (instance? clojure.lang.IDeref ref)
           (.deref ^clojure.lang.IDeref ref)
           (deref-future ref)))
  ([ref timeout-ms timeout-val]
    (if (instance? clojure.lang.IBlockingDeref ref)
      (.deref ^clojure.lang.IBlockingDeref ref timeout-ms timeout-val)
      (deref-future ref timeout-ms timeout-val))))

(defn alter-meta!
  "Atomically sets the metadata for a namespace/var/ref/agent/atom to be:

  (apply f its-current-meta args)

  f must be free of side-effects"
  {:added "1.0"
   :static true}
  [^clojure.lang.IReference iref f & args] (.alterMeta iref f args))


(defn commute
  "Must be called in a transaction. Sets the in-transaction-value of
  ref to:

  (apply fun in-transaction-value-of-ref args)

  and returns the in-transaction-value of ref.

  At the commit point of the transaction, sets the value of ref to be:

  (apply fun most-recently-committed-value-of-ref args)

  Thus fun should be commutative, or, failing that, you must accept
  last-one-in-wins behavior.  commute allows for more concurrency than
  ref-set."
  {:added "1.0"
   :static true}

  [^clojure.lang.Ref ref fun & args]
  (. ref (commute fun args)))

(defmacro sync
  "transaction-flags => TBD, pass nil for now

  Runs the exprs (in an implicit do) in a transaction that encompasses
  exprs and any nested calls.  Starts a transaction if none is already
  running on this thread. Any uncaught exception will abort the
  transaction and flow out of sync. The exprs may be run more than
  once, but any effects on Refs will be atomic."
  {:added "1.0"}
  [flags-ignored-for-now & body]
  `(. clojure.lang.LockingTransaction
     (runInTransaction (fn [] ~@body))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; fn stuff ;;;;;;;;;;;;;;;;

(defn comp
  "Takes a set of functions and returns a fn that is the composition
  of those fns.  The returned fn takes a variable number of args,
  applies the rightmost of fns to the args, the next
  fn (right-to-left) to the result, etc."
  {:added "1.0"
   :static true}
  ([] identity)
  ([f] f)
  ([f g]
    (fn
      ([] (f (g)))
      ([x] (f (g x)))
      ([x y] (f (g x y)))
      ([x y z] (f (g x y z)))
      ([x y z & args] (f (apply g x y z args)))))
  ([f g h]
    (fn
      ([] (f (g (h))))
      ([x] (f (g (h x))))
      ([x y] (f (g (h x y))))
      ([x y z] (f (g (h x y z))))
      ([x y z & args] (f (g (apply h x y z args))))))
  ([f1 f2 f3 & fs]
    (let [fs (reverse (list* f1 f2 f3 fs))]
      (fn [& args]
        (loop [ret (apply (first fs) args) fs (next fs)]
          (if fs
            (recur ((first fs) ret) (next fs))
            ret))))))

(defn partial
  "Takes a function f and fewer than the normal arguments to f, and
  returns a fn that takes a variable number of additional args. When
  called, the returned function calls f with args + additional args."
  {:added "1.0"
   :static true}
  ([f] f)
  ([f arg1]
    (fn [& args] (apply f arg1 args)))
  ([f arg1 arg2]
    (fn [& args] (apply f arg1 arg2 args)))
  ([f arg1 arg2 arg3]
    (fn [& args] (apply f arg1 arg2 arg3 args)))
  ([f arg1 arg2 arg3 & more]
    (fn [& args] (apply f arg1 arg2 arg3 (concat more args)))))

;;;;;;;;;;;;;;;;;;; sequence fns  ;;;;;;;;;;;;;;;;;;;;;;;

(defn every?
  "Returns true if (pred x) is logical true for every x in coll, else
  false."
  {:tag Boolean
   :added "1.0"
   :static true}
  [pred coll]
  (cond
    (nil? (seq coll)) true
    (pred (first coll)) (recur pred (next coll))
    :else false))


(defn some
  "Returns the first logical true value of (pred x) for any x in coll,
  else nil.  One common idiom is to use a set as pred, for example
  this will return :fred if :fred is in the sequence, otherwise nil:
  (some #{:fred} coll)"
  {:added "1.0"
   :static true}
  [pred coll]
  (when (seq coll)
    (or (pred (first coll)) (recur pred (next coll)))))

(def
  ^{:tag Boolean
    :doc "Returns false if (pred x) is logical true for any x in coll,
  else true."
    :arglists '([pred coll])
    :added "1.0"}
  not-any? (comp not some))

;will be redefed later with arg checks
(defmacro dotimes
  "bindings => name n

  Repeatedly executes body (presumably for side-effects) with name
  bound to integers from 0 through n-1."
  {:added "1.0"}
  [bindings & body]
  (let [i (first bindings)
        n (second bindings)]
    `(let [n# (clojure.lang.RT/longCast ~n)]
       (loop [~i 0]
         (when (< ~i n#)
           ~@body
           (recur (unchecked-inc ~i)))))))

(defn map
  "Returns a lazy sequence consisting of the result of applying f to the
  set of first items of each coll, followed by applying f to the set
  of second items in each coll, until any one of the colls is
  exhausted.  Any remaining items in other colls are ignored. Function
  f should accept number-of-colls arguments."
  {:added "1.0"
   :static true}
  ([f coll]
    (lazy-seq
      (when-let [s (seq coll)]
        (if (chunked-seq? s)
          (let [c (chunk-first s)
                size (int (count c))
                b (chunk-buffer size)]
            (dotimes [i size]
              (chunk-append b (f (.nth c i))))
            (chunk-cons (chunk b) (map f (chunk-rest s))))
          (cons (f (first s)) (map f (rest s)))))))
  ([f c1 c2]
    (lazy-seq
      (let [s1 (seq c1) s2 (seq c2)]
        (when (and s1 s2)
          (cons (f (first s1) (first s2))
            (map f (rest s1) (rest s2)))))))
  ([f c1 c2 c3]
    (lazy-seq
      (let [s1 (seq c1) s2 (seq c2) s3 (seq c3)]
        (when (and s1 s2 s3)
          (cons (f (first s1) (first s2) (first s3))
            (map f (rest s1) (rest s2) (rest s3)))))))
  ([f c1 c2 c3 & colls]
    (let [step (fn step [cs]
                 (lazy-seq
                   (let [ss (map seq cs)]
                     (when (every? identity ss)
                       (cons (map first ss) (step (map rest ss)))))))]
      (map #(apply f %) (step (conj colls c3 c2 c1))))))

(defn mapcat
  "Returns the result of applying concat to the result of applying map
  to f and colls.  Thus function f should return a collection."
  {:added "1.0"
   :static true}
  [f & colls]
  (apply concat (apply map f colls)))

(defn filter
  "Returns a lazy sequence of the items in coll for which
  (pred item) returns true. pred must be free of side-effects."
  {:added "1.0"
   :static true}
  ([pred coll]
    (lazy-seq
      (when-let [s (seq coll)]
        (if (chunked-seq? s)
          (let [c (chunk-first s)
                size (count c)
                b (chunk-buffer size)]
            (dotimes [i size]
              (when (pred (.nth c i))
                (chunk-append b (.nth c i))))
            (chunk-cons (chunk b) (filter pred (chunk-rest s))))
          (let [f (first s) r (rest s)]
            (if (pred f)
              (cons f (filter pred r))
              (filter pred r))))))))


(defn remove
  "Returns a lazy sequence of the items in coll for which
  (pred item) returns false. pred must be free of side-effects."
  {:added "1.0"
   :static true}
  [pred coll]
  (filter (complement pred) coll))

(defn take
  "Returns a lazy sequence of the first n items in coll, or all items if
  there are fewer than n."
  {:added "1.0"
   :static true}
  [n coll]
  (lazy-seq
    (when (pos? n)
      (when-let [s (seq coll)]
        (cons (first s) (take (dec n) (rest s)))))))

(defn take-while
  "Returns a lazy sequence of successive items from coll while
  (pred item) returns true. pred must be free of side-effects."
  {:added "1.0"
   :static true}
  [pred coll]
  (lazy-seq
    (when-let [s (seq coll)]
      (when (pred (first s))
        (cons (first s) (take-while pred (rest s)))))))

(defn drop
  "Returns a lazy sequence of all but the first n items in coll."
  {:added "1.0"
   :static true}
  [n coll]
  (let [step (fn [n coll]
               (let [s (seq coll)]
                 (if (and (pos? n) s)
                   (recur (dec n) (rest s))
                   s)))]
    (lazy-seq (step n coll))))

(defn drop-while
  "Returns a lazy sequence of the items in coll starting from the first
  item for which (pred item) returns logical false."
  {:added "1.0"
   :static true}
  [pred coll]
  (let [step (fn [pred coll]
               (let [s (seq coll)]
                 (if (and s (pred (first s)))
                   (recur pred (rest s))
                   s)))]
    (lazy-seq (step pred coll))))


(defn repeat
  "Returns a lazy (infinite!, or length n if supplied) sequence of xs."
  {:added "1.0"
   :static true}
  ([x] (lazy-seq (cons x (repeat x))))
  ([n x] (take n (repeat x))))

(defn replicate
  "DEPRECATED: Use 'repeat' instead.
   Returns a lazy seq of n xs."
  {:added "1.0"
   :deprecated "1.3"}
  [n x] (take n (repeat x)))


(defn merge
  "Returns a map that consists of the rest of the maps conj-ed onto
  the first.  If a key occurs in more than one map, the mapping from
  the latter (left-to-right) will be the mapping in the result."
  {:added "1.0"
   :static true}
  [& maps]
  (when (some identity maps)
    (reduce1 #(conj (or %1 {}) %2) maps)))


(defmacro declare
  "defs the supplied var names with no bindings, useful for making forward declarations."
  {:added "1.0"}
  [& names] `(do ~@(map #(list 'def (vary-meta % assoc :declared true)) names)))


(defn sort
  "Returns a sorted sequence of the items in coll. If no comparator is
  supplied, uses compare.  comparator must implement
  java.util.Comparator.  If coll is a Java array, it will be modified.
  To avoid this, sort a copy of the array."
  {:added "1.0"
   :static true}
  ([coll]
    (sort compare coll))
  ([^java.util.Comparator comp coll]
    (if (seq coll)
      (let [a (to-array coll)]
        (. java.util.Arrays (sort a comp))
        (seq a))
      ())))

(defn sort-by
  "Returns a sorted sequence of the items in coll, where the sort
  order is determined by comparing (keyfn item).  If no comparator is
  supplied, uses compare.  comparator must implement
  java.util.Comparator.  If coll is a Java array, it will be modified.
  To avoid this, sort a copy of the array."
  {:added "1.0"
   :static true}
  ([keyfn coll]
    (sort-by keyfn compare coll))
  ([keyfn ^java.util.Comparator comp coll]
    (sort (fn [x y] (. comp (compare (keyfn x) (keyfn y)))) coll)))

(defn dorun
  "When lazy sequences are produced via functions that have side
  effects, any effects other than those needed to produce the first
  element in the seq do not occur until the seq is consumed. dorun can
  be used to force any effects. Walks through the successive nexts of
  the seq, does not retain the head and returns nil."
  {:added "1.0"
   :static true}
  ([coll]
    (when (seq coll)
      (recur (next coll))))
  ([n coll]
    (when (and (seq coll) (pos? n))
      (recur (dec n) (next coll)))))

(defn doall
  "When lazy sequences are produced via functions that have side
  effects, any effects other than those needed to produce the first
  element in the seq do not occur until the seq is consumed. doall can
  be used to force any effects. Walks through the successive nexts of
  the seq, retains the head and returns it, thus causing the entire
  seq to reside in memory at one time."
  {:added "1.0"
   :static true}
  ([coll]
    (dorun coll)
    coll)
  ([n coll]
    (dorun n coll)
    coll))

(defn nthnext
  "Returns the nth next of coll, (seq coll) when n is 0."
  {:added "1.0"
   :static true}
  [coll n]
  (loop [n n xs (seq coll)]
    (if (and xs (pos? n))
      (recur (dec n) (next xs))
      xs)))

(defn nthrest
  "Returns the nth rest of coll, coll when n is 0."
  {:added "1.3"
   :static true}
  [coll n]
  (loop [n n xs coll]
    (if (and (pos? n) (seq xs))
      (recur (dec n) (rest xs))
      xs)))

(defn partition
  "Returns a lazy sequence of lists of n items each, at offsets step
  apart. If step is not supplied, defaults to n, i.e. the partitions
  do not overlap. If a pad collection is supplied, use its elements as
  necessary to complete last partition upto n items. In case there are
  not enough padding elements, return a partition with less than n items."
  {:added "1.0"
   :static true}
  ([n coll]
    (partition n n coll))
  ([n step coll]
    (lazy-seq
      (when-let [s (seq coll)]
        (let [p (doall (take n s))]
          (when (= n (count p))
            (cons p (partition n step (nthrest s step))))))))
  ([n step pad coll]
    (lazy-seq
      (when-let [s (seq coll)]
        (let [p (doall (take n s))]
          (if (= n (count p))
            (cons p (partition n step pad (nthrest s step)))
            (list (take n (concat p pad)))))))))

;; evaluation

(defn eval
  "Evaluates the form data structure (not text!) and returns the result."
  {:added "1.0"
   :static true}
  [form] (. clojure.lang.Compiler (eval form)))

(defmacro doseq
  "Repeatedly executes body (presumably for side-effects) with
  bindings and filtering as provided by \"for\".  Does not retain
  the head of the sequence. Returns nil."
  {:added "1.0"}
  [seq-exprs & body]
  (assert-args
    (vector? seq-exprs) "a vector for its binding"
    (even? (count seq-exprs)) "an even number of forms in binding vector")
  (let [step (fn step [recform exprs]
               (if-not exprs
                 [true `(do ~@body)]
                 (let [k (first exprs)
                       v (second exprs)]
                   (if (keyword? k)
                     (let [steppair (step recform (nnext exprs))
                           needrec (steppair 0)
                           subform (steppair 1)]
                       (cond
                         (= k :let ) [needrec `(let ~v ~subform)]
                         (= k :while ) [false `(when ~v
                                                 ~subform
                                                 ~@(when needrec [recform]))]
                         (= k :when ) [false `(if ~v
                                                (do
                                                  ~subform
                                                  ~@(when needrec [recform]))
                                                ~recform)]))
                     (let [seq- (gensym "seq_")
                           chunk- (with-meta (gensym "chunk_")
                                    {:tag 'clojure.lang.IChunk})
                           count- (gensym "count_")
                           i- (gensym "i_")
                           recform `(recur (next ~seq-) nil 0 0)
                           steppair (step recform (nnext exprs))
                           needrec (steppair 0)
                           subform (steppair 1)
                           recform-chunk
                           `(recur ~seq- ~chunk- ~count- (unchecked-inc ~i-))
                           steppair-chunk (step recform-chunk (nnext exprs))
                           subform-chunk (steppair-chunk 1)]
                       [true
                        `(loop [~seq- (seq ~v), ~chunk- nil,
                                ~count- 0, ~i- 0]
                           (if (< ~i- ~count-)
                             (let [~k (.nth ~chunk- ~i-)]
                               ~subform-chunk
                               ~@(when needrec [recform-chunk]))
                             (when-let [~seq- (seq ~seq-)]
                               (if (chunked-seq? ~seq-)
                                 (let [c# (chunk-first ~seq-)]
                                   (recur (chunk-rest ~seq-) c#
                                     (int (count c#)) (int 0)))
                                 (let [~k (first ~seq-)]
                                   ~subform
                                   ~@(when needrec [recform]))))))])))))]
    (nth (step nil (seq seq-exprs)) 1)))



;;;;;;;;;;;;;;;;;;;;; editable collections ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn transient
  "Returns a new, transient version of the collection, in constant time."
  {:added "1.1"
   :static true}
  [^clojure.lang.IEditableCollection coll]
  (.asTransient coll))

(defn persistent!
  "Returns a new, persistent version of the transient collection, in
  constant time. The transient collection cannot be used after this
  call, any such use will throw an exception."
  {:added "1.1"
   :static true}
  [^clojure.lang.ITransientCollection coll]
  (.persistent coll))

(defn conj!
  "Adds x to the transient collection, and return coll. The 'addition'
  may happen at different 'places' depending on the concrete type."
  {:added "1.1"
   :static true}
  [^clojure.lang.ITransientCollection coll x]
  (.conj coll x))


;redef into with batch support
(defn ^:private into1
  "Returns a new coll consisting of to-coll with all of the items of
  from-coll conjoined."
  {:added "1.0"
   :static true}
  [to from]
  (if (instance? clojure.lang.IEditableCollection to)
    (persistent! (reduce1 conj! (transient to) from))
    (reduce1 conj to from)))

(defmacro import
  "import-list => (package-symbol class-name-symbols*)

  For each name in class-name-symbols, adds a mapping from name to the
  class named by package.name to the current namespace. Use :import in the ns
  macro in preference to calling this directly."
  {:added "1.0"}
  [& import-symbols-or-lists]
  (let [specs (map #(if (and (seq? %) (= 'quote (first %))) (second %) %)
                import-symbols-or-lists)]
    `(do ~@(map #(list 'clojure.core/import* %)
             (reduce1 (fn [v spec]
                        (if (symbol? spec)
                          (conj v (name spec))
                          (let [p (first spec) cs (rest spec)]
                            (into1 v (map #(str p "." %) cs)))))
               [] specs)))))

(defn into-array
  "Returns an array with components set to the values in aseq. The array's
  component type is type if provided, or the type of the first value in
  aseq if present, or Object. All values in aseq must be compatible with
  the component type. Class objects for the primitive types can be obtained
  using, e.g., Integer/TYPE."
  {:added "1.0"
   :static true}
  ([aseq]
    (clojure.lang.RT/seqToTypedArray (seq aseq)))
  ([type aseq]
    (clojure.lang.RT/seqToTypedArray type (seq aseq))))

(defn ^{:private true}
  array [& items]
  (into-array items))

(defn class
  "Returns the Class of x"
  {:added "1.0"
   :static true}
  ^Class [^Object x] (if (nil? x) x (. x (getClass))))

(defn type
  "Returns the :type metadata of x, or its Class if none"
  {:added "1.0"
   :static true}
  [x]
  (or (get (meta x) :type ) (class x)))


(defn num
  "Coerce to Number"
  {:tag Number
   :inline (fn [x] `(. clojure.lang.Numbers (num ~x)))
   :added "1.0"}
  [x] (. clojure.lang.Numbers (num x)))

(defn long
  "Coerce to long"
  {:inline (fn [x] `(. clojure.lang.RT (longCast ~x)))
   :added "1.0"}
  [^Number x] (clojure.lang.RT/longCast x))

(defn float
  "Coerce to float"
  {:inline (fn [x] `(. clojure.lang.RT (~(if *unchecked-math* 'uncheckedFloatCast 'floatCast) ~x)))
   :added "1.0"}
  [^Number x] (clojure.lang.RT/floatCast x))

(defn double
  "Coerce to double"
  {:inline (fn [x] `(. clojure.lang.RT (doubleCast ~x)))
   :added "1.0"}
  [^Number x] (clojure.lang.RT/doubleCast x))

(defn short
  "Coerce to short"
  {:inline (fn [x] `(. clojure.lang.RT (~(if *unchecked-math* 'uncheckedShortCast 'shortCast) ~x)))
   :added "1.0"}
  [^Number x] (clojure.lang.RT/shortCast x))

(defn byte
  "Coerce to byte"
  {:inline (fn [x] `(. clojure.lang.RT (~(if *unchecked-math* 'uncheckedByteCast 'byteCast) ~x)))
   :added "1.0"}
  [^Number x] (clojure.lang.RT/byteCast x))

(defn char
  "Coerce to char"
  {:inline (fn [x] `(. clojure.lang.RT (~(if *unchecked-math* 'uncheckedCharCast 'charCast) ~x)))
   :added "1.1"}
  [x] (. clojure.lang.RT (charCast x)))

(defn boolean
  "Coerce to boolean"
  {
    :inline (fn [x] `(. clojure.lang.RT (booleanCast ~x)))
    :added "1.0"}
  [x] (clojure.lang.RT/booleanCast x))

(def ^:dynamic ^{:private true} print-initialized false)

(defmulti print-method (fn [x writer]
                         (let [t (get (meta x) :type )]
                           (if (keyword? t) t (class x)))))
(defmulti print-dup (fn [x writer] (class x)))

(defn pr-on
  {:private true
   :static true}
  [x w]
  (if *print-dup*
    (print-dup x w)
    (print-method x w))
  nil)

(defn pr
  "Prints the object(s) to the output stream that is the current value
  of *out*.  Prints the object(s), separated by spaces if there is
  more than one.  By default, pr and prn print in a way that objects
  can be read by the reader"
  {:dynamic true
   :added "1.0"}
  ([] nil)
  ([x]
    (pr-on x *out*))
  ([x & more]
    (pr x)
    (. *out* (append \space))
    (if-let [nmore (next more)]
      (recur (first more) nmore)
      (apply pr more))))

(def ^:private ^String system-newline
  (System/getProperty "line.separator"))

(defn newline
  "Writes a platform-specific newline to *out*"
  {:added "1.0"
   :static true}
  []
  (. *out* (append system-newline))
  nil)

(defn flush
  "Flushes the output stream that is the current value of
  *out*"
  {:added "1.0"
   :static true}
  []
  (. *out* (flush))
  nil)

(defn prn
  "Same as pr followed by (newline). Observes *flush-on-newline*"
  {:added "1.0"
   :static true}
  [& more]
  (apply pr more)
  (newline)
  (when *flush-on-newline*
    (flush)))

(defn print
  "Prints the object(s) to the output stream that is the current value
  of *out*.  print and println produce output for human consumption."
  {:added "1.0"
   :static true}
  [& more]
  (binding [*print-readably* nil]
    (apply pr more)))

(defn println
  "Same as print followed by (newline)"
  {:added "1.0"
   :static true}
  [& more]
  (binding [*print-readably* nil]
    (apply prn more)))

(defn read
  "Reads the next object from stream, which must be an instance of
  java.io.PushbackReader or some derivee.  stream defaults to the
  current value of *in*.

  Note that read can execute code (controlled by *read-eval*),
  and as such should be used only with trusted sources.

  For data structure interop use clojure.edn/read"
  {:added "1.0"
   :static true}
  ([]
    (read *in*))
  ([stream]
    (read stream true nil))
  ([stream eof-error? eof-value]
    (read stream eof-error? eof-value false))
  ([stream eof-error? eof-value recursive?]
    (. clojure.lang.LispReader (read stream (boolean eof-error?) eof-value recursive?))))

(defn subvec
  "Returns a persistent vector of the items in vector from
  start (inclusive) to end (exclusive).  If end is not supplied,
  defaults to (count vector). This operation is O(1) and very fast, as
  the resulting vector shares structure with the original and no
  trimming is done."
  {:added "1.0"
   :static true}
  ([v start]
    (subvec v start (count v)))
  ([v start end]
    (. clojure.lang.RT (subvec v start end))))

(defmacro with-open
  "bindings => [name init ...]

  Evaluates body in a try expression with names bound to the values
  of the inits, and a finally clause that calls (.close name) on each
  name in reverse order."
  {:added "1.0"}
  [bindings & body]
  (assert-args
    (vector? bindings) "a vector for its binding"
    (even? (count bindings)) "an even number of forms in binding vector")
  (cond
    (= (count bindings) 0) `(do ~@body)
    (symbol? (bindings 0)) `(let ~(subvec bindings 0 2)
                              (try
                                (with-open ~(subvec bindings 2) ~@body)
                                (finally
                                  (. ~(bindings 0) close))))
    :else (throw (IllegalArgumentException.
                   "with-open only allows Symbols in bindings"))))

(defmacro doto
  "Evaluates x then calls all of the methods and functions with the
  value of x supplied at the front of the given arguments.  The forms
  are evaluated in order.  Returns x.

  (doto (new java.util.HashMap) (.put \"a\" 1) (.put \"b\" 2))"
  {:added "1.0"}
  [x & forms]
  (let [gx (gensym)]
    `(let [~gx ~x]
       ~@(map (fn [f]
                (if (seq? f)
                  `(~(first f) ~gx ~@(next f))
                  `(~f ~gx)))
           forms)
       ~gx)))

(import '(java.lang.reflect Array))

(defn alength
  "Returns the length of the Java array. Works on arrays of all
  types."
  {:inline (fn [a] `(. clojure.lang.RT (alength ~a)))
   :added "1.0"}
  [array] (. clojure.lang.RT (alength array)))

(defn aget
  "Returns the value at the index/indices. Works on Java arrays of all
  types."
  {:inline (fn [a i] `(. clojure.lang.RT (aget ~a (int ~i))))
   :inline-arities #{2}
   :added "1.0"}
  ([array idx]
    (clojure.lang.Reflector/prepRet (.getComponentType (class array)) (. Array (get array idx))))
  ([array idx & idxs]
    (apply aget (aget array idx) idxs)))

(defmacro
  ^{:private true}
  def-aset [name method coerce]
  `(defn ~name
     {:arglists '([~'array ~'idx ~'val] [~'array ~'idx ~'idx2 & ~'idxv])}
     ([array# idx# val#]
       (. Array (~method array# idx# (~coerce val#)))
       val#)
     ([array# idx# idx2# & idxv#]
       (apply ~name (aget array# idx#) idx2# idxv#))))

(def-aset
  ^{:doc "Sets the value at the index/indices. Works on arrays of int. Returns val."
    :added "1.0"}
  aset-int setInt int)

(defn make-array
  "Creates and returns an array of instances of the specified class of
  the specified dimension(s).  Note that a class object is required.
  Class objects can be obtained by using their imported or
  fully-qualified name.  Class objects for the primitive types can be
  obtained using, e.g., Integer/TYPE."
  {:added "1.0"
   :static true}
  ([^Class type len]
    (. Array (newInstance type (int len))))
  ([^Class type dim & more-dims]
    (let [dims (cons dim more-dims)
          ^"[I" dimarray (make-array (. Integer TYPE) (count dims))]
      (dotimes [i (alength dimarray)]
        (aset-int dimarray i (nth dims i)))
      (. Array (newInstance type dimarray)))))

(defn macroexpand-1
  "If form represents a macro form, returns its expansion,
  else returns form."
  {:added "1.0"
   :static true}
  [form]
  (. clojure.lang.Compiler (macroexpand1 form)))

(defn macroexpand
  "Repeatedly calls macroexpand-1 on form until it no longer
  represents a macro form, then returns it.  Note neither
  macroexpand-1 nor macroexpand expand macros in subforms."
  {:added "1.0"
   :static true}
  [form]
  (let [ex (macroexpand-1 form)]
    (if (identical? ex form)
      form
      (macroexpand ex))))

(defn load-reader
  "Sequentially read and evaluate the set of forms contained in the
  stream/file"
  {:added "1.0"
   :static true}
  [rdr] (. clojure.lang.Compiler (load rdr)))

(defn set
  "Returns a set of the distinct elements of coll."
  {:added "1.0"
   :static true}
  [coll] (clojure.lang.PersistentHashSet/create (seq coll)))

(defn ^{:private true
        :static true}
  filter-key [keyfn pred amap]
  (loop [ret {} es (seq amap)]
    (if es
      (if (pred (keyfn (first es)))
        (recur (assoc ret (key (first es)) (val (first es))) (next es))
        (recur ret (next es)))
      ret)))

(defn find-ns
  "Returns the namespace named by the symbol or nil if it doesn't exist."
  {:added "1.0"
   :static true}
  [sym] (clojure.lang.Namespace/find sym))

(defn create-ns
  "Create a new namespace named by the symbol if one doesn't already
  exist, returns it or the already-existing namespace of the same
  name."
  {:added "1.0"
   :static true}
  [sym] (clojure.lang.Namespace/findOrCreate sym))

(defn remove-ns
  "Removes the namespace named by the symbol. Use with caution.
  Cannot be used to remove the clojure namespace."
  {:added "1.0"
   :static true}
  [sym] (clojure.lang.Namespace/remove sym))

(defn all-ns
  "Returns a sequence of all namespaces."
  {:added "1.0"
   :static true}
  [] (clojure.lang.Namespace/all))

(defn the-ns
  "If passed a namespace, returns it. Else, when passed a symbol,
  returns the namespace named by it, throwing an exception if not
  found."
  {:added "1.0"
   :static true}
  ^clojure.lang.Namespace [x]
  (if (instance? clojure.lang.Namespace x)
    x
    (or (find-ns x) (throw (Exception. (str "No namespace: " x " found"))))))

(defn ns-name
  "Returns the name of the namespace, a symbol."
  {:added "1.0"
   :static true}
  [ns]
  (.getName (the-ns ns)))

(defn ns-map
  "Returns a map of all the mappings for the namespace."
  {:added "1.0"
   :static true}
  [ns]
  (.getMappings (the-ns ns)))

(defn ns-unmap
  "Removes the mappings for the symbol from the namespace."
  {:added "1.0"
   :static true}
  [ns sym]
  (.unmap (the-ns ns) sym))

;(defn export [syms]
;  (doseq [sym syms]
;   (.. *ns* (intern sym) (setExported true))))

(defn ns-publics
  "Returns a map of the public intern mappings for the namespace."
  {:added "1.0"
   :static true}
  [ns]
  (let [ns (the-ns ns)]
    (filter-key val (fn [^clojure.lang.Var v] (and (instance? clojure.lang.Var v)
                                                (= ns (.ns v))
                                                (.isPublic v)))
      (ns-map ns))))

(defn ns-imports
  "Returns a map of the import mappings for the namespace."
  {:added "1.0"
   :static true}
  [ns]
  (filter-key val (partial instance? Class) (ns-map ns)))

(defn ns-interns
  "Returns a map of the intern mappings for the namespace."
  {:added "1.0"
   :static true}
  [ns]
  (let [ns (the-ns ns)]
    (filter-key val (fn [^clojure.lang.Var v] (and (instance? clojure.lang.Var v)
                                                (= ns (.ns v))))
      (ns-map ns))))

(defn refer
  "refers to all public vars of ns, subject to filters.
  filters can include at most one each of:

  :exclude list-of-symbols
  :only list-of-symbols
  :rename map-of-fromsymbol-tosymbol

  For each public interned var in the namespace named by the symbol,
  adds a mapping from the name of the var to the var to the current
  namespace.  Throws an exception if name is already mapped to
  something else in the current namespace. Filters can be used to
  select a subset, via inclusion or exclusion, or to provide a mapping
  to a symbol different from the var's name, in order to prevent
  clashes. Use :use in the ns macro in preference to calling this directly."
  {:added "1.0"}
  [ns-sym & filters]
  (let [ns (or (find-ns ns-sym) (throw (new Exception (str "No namespace: " ns-sym))))
        fs (apply hash-map filters)
        nspublics (ns-publics ns)
        rename (or (:rename fs) {})
        exclude (set (:exclude fs))
        to-do (if (= :all (:refer fs))
                (keys nspublics)
                (or (:refer fs) (:only fs) (keys nspublics)))]
    (when (and to-do (not (instance? clojure.lang.Sequential to-do)))
      (throw (new Exception ":only/:refer value must be a sequential collection of symbols")))
    (doseq [sym to-do]
      (when-not (exclude sym)
        (let [v (nspublics sym)]
          (when-not v
            (throw (new java.lang.IllegalAccessError
                     (if (get (ns-interns ns) sym)
                       (str sym " is not public")
                       (str sym " does not exist")))))
          (. *ns* (refer (or (rename sym) sym) v)))))))

(defn alias
  "Add an alias in the current namespace to another
  namespace. Arguments are two symbols: the alias to be used, and
  the symbolic name of the target namespace. Use :as in the ns macro in preference
  to calling this directly."
  {:added "1.0"
   :static true}
  [alias namespace-sym]
  (.addAlias *ns* alias (the-ns namespace-sym)))

(defn take-nth
  "Returns a lazy seq of every nth item in coll."
  {:added "1.0"
   :static true}
  [n coll]
  (lazy-seq
    (when-let [s (seq coll)]
      (cons (first s) (take-nth n (drop n s))))))

(defn interleave
  "Returns a lazy seq of the first item in each coll, then the second etc."
  {:added "1.0"
   :static true}
  ([] ())
  ([c1] (lazy-seq c1))
  ([c1 c2]
    (lazy-seq
      (let [s1 (seq c1) s2 (seq c2)]
        (when (and s1 s2)
          (cons (first s1) (cons (first s2)
                             (interleave (rest s1) (rest s2))))))))
  ([c1 c2 & colls]
    (lazy-seq
      (let [ss (map seq (conj colls c2 c1))]
        (when (every? identity ss)
          (concat (map first ss) (apply interleave (map rest ss))))))))


(defn ns-resolve
  "Returns the var or Class to which a symbol will be resolved in the
  namespace (unless found in the environment), else nil.  Note that
  if the symbol is fully qualified, the var/Class to which it resolves
  need not be present in the namespace."
  {:added "1.0"
   :static true}
  ([ns sym]
    (ns-resolve ns nil sym))
  ([ns env sym]
    (when-not (contains? env sym)
      (clojure.lang.Compiler/maybeResolveIn (the-ns ns) sym))))

(defn resolve
  "same as (ns-resolve *ns* symbol) or (ns-resolve *ns* &env symbol)"
  {:added "1.0"
   :static true}
  ([sym] (ns-resolve *ns* sym))
  ([env sym] (ns-resolve *ns* env sym)))


;redefine let and loop  with destructuring
(defn destructure [bindings]
  (let [bents (partition 2 bindings)
        pb (fn pb [bvec b v]
             (let [pvec
                   (fn [bvec b val]
                     (let [gvec (gensym "vec__")]
                       (loop [ret (-> bvec (conj gvec) (conj val))
                              n 0
                              bs b
                              seen-rest? false]
                         (if (seq bs)
                           (let [firstb (first bs)]
                             (cond
                               (= firstb '&) (recur (pb ret (second bs) (list `nthnext gvec n))
                                               n
                                               (nnext bs)
                                               true)
                               (= firstb :as ) (pb ret (second bs) gvec)
                               :else (if seen-rest?
                                       (throw (new Exception "Unsupported binding form, only :as can follow & parameter"))
                                       (recur (pb ret firstb (list `nth gvec n nil))
                                         (inc n)
                                         (next bs)
                                         seen-rest?))))
                           ret))))
                   pmap
                   (fn [bvec b v]
                     (let [gmap (gensym "map__")
                           gmapseq (with-meta gmap {:tag 'clojure.lang.ISeq})
                           defaults (:or b)]
                       (loop [ret (-> bvec (conj gmap) (conj v)
                                    (conj gmap) (conj `(if (seq? ~gmap) (clojure.lang.PersistentHashMap/create (seq ~gmapseq)) ~gmap))
                                    ((fn [ret]
                                       (if (:as b)
                                         (conj ret (:as b) gmap)
                                         ret))))
                              bes (reduce1
                                    (fn [bes entry]
                                      (reduce1 #(assoc %1 %2 ((val entry) %2))
                                        (dissoc bes (key entry))
                                        ((key entry) bes)))
                                    (dissoc b :as :or )
                                    {:keys #(if (keyword? %) % (keyword (str %))),
                                     :strs str, :syms #(list `quote %)})]
                         (if (seq bes)
                           (let [bb (key (first bes))
                                 bk (val (first bes))
                                 has-default (contains? defaults bb)]
                             (recur (pb ret bb (if has-default
                                                 (list `get gmap bk (defaults bb))
                                                 (list `get gmap bk)))
                               (next bes)))
                           ret))))]
               (cond
                 (symbol? b) (-> bvec (conj (if (namespace b) (symbol (name b)) b)) (conj v))
                 (keyword? b) (-> bvec (conj (symbol (name b))) (conj v))
                 (vector? b) (pvec bvec b v)
                 (map? b) (pmap bvec b v)
                 :else (throw (new Exception (str "Unsupported binding form: " b))))))
        process-entry (fn [bvec b] (pb bvec (first b) (second b)))]
    (if (every? symbol? (map first bents))
      bindings
      (if-let [kwbs (seq (filter #(keyword? (first %)) bents))]
        (throw (new Exception (str "Unsupported binding key: " (ffirst kwbs))))
        (reduce1 process-entry [] bents)))))

(defmacro let
  "binding => binding-form init-expr

  Evaluates the exprs in a lexical context in which the symbols in
  the binding-forms are bound to their respective init-exprs or parts
  therein."
  {:added "1.0", :special-form true, :forms '[(let [bindings*] exprs*)]}
  [bindings & body]
  (assert-args
    (vector? bindings) "a vector for its binding"
    (even? (count bindings)) "an even number of forms in binding vector")
  `(let* ~(destructure bindings) ~@body))

(defn ^{:private true}
  maybe-destructured
  [params body]
  (if (every? symbol? params)
    (cons params body)
    (loop [params params
           new-params []
           lets []]
      (if params
        (if (symbol? (first params))
          (recur (next params) (conj new-params (first params)) lets)
          (let [gparam (gensym "p__")]
            (recur (next params) (conj new-params gparam)
              (-> lets (conj (first params)) (conj gparam)))))
        `(~new-params
           (let ~lets
             ~@body))))))

;redefine fn with destructuring and pre/post conditions
(defmacro fn
  "params => positional-params* , or positional-params* & next-param
  positional-param => binding-form
  next-param => binding-form
  name => symbol

  Defines a function"
  {:added "1.0", :special-form true,
   :forms '[(fn name? [params*] exprs*) (fn name? ([params*] exprs*) +)]}
  [& sigs]
  (let [name (if (symbol? (first sigs)) (first sigs) nil)
        sigs (if name (next sigs) sigs)
        sigs (if (vector? (first sigs))
               (list sigs)
               (if (seq? (first sigs))
                 sigs
                 ;; Assume single arity syntax
                 (throw (IllegalArgumentException.
                          (if (seq sigs)
                            (str "Parameter declaration "
                              (first sigs)
                              " should be a vector")
                            (str "Parameter declaration missing"))))))
        psig (fn* [sig]
               ;; Ensure correct type before destructuring sig
               (when (not (seq? sig))
                 (throw (IllegalArgumentException.
                          (str "Invalid signature " sig
                            " should be a list"))))
               (let [[params & body] sig
                     _ (when (not (vector? params))
                         (throw (IllegalArgumentException.
                                  (if (seq? (first sigs))
                                    (str "Parameter declaration " params
                                      " should be a vector")
                                    (str "Invalid signature " sig
                                      " should be a list")))))
                     conds (when (and (next body) (map? (first body)))
                             (first body))
                     body (if conds (next body) body)
                     conds (or conds (meta params))
                     pre (:pre conds)
                     post (:post conds)
                     body (if post
                            `((let [~'% ~(if (< 1 (count body))
                                           `(do ~@body)
                                           (first body))]
                                ~@(map (fn* [c] `(assert ~c)) post)
                                ~'%))
                            body)
                     body (if pre
                            (concat (map (fn* [c] `(assert ~c)) pre)
                              body)
                            body)]
                 (maybe-destructured params body)))
        new-sigs (map psig sigs)]
    (with-meta
      (if name
        (list* 'fn* name new-sigs)
        (cons 'fn* new-sigs))
      (meta &form))))

(defmacro loop
  "Evaluates the exprs in a lexical context in which the symbols in
  the binding-forms are bound to their respective init-exprs or parts
  therein. Acts as a recur target."
  {:added "1.0", :special-form true, :forms '[(loop [bindings*] exprs*)]}
  [bindings & body]
  (assert-args
    (vector? bindings) "a vector for its binding"
    (even? (count bindings)) "an even number of forms in binding vector")
  (let [db (destructure bindings)]
    (if (= db bindings)
      `(loop* ~bindings ~@body)
      (let [vs (take-nth 2 (drop 1 bindings))
            bs (take-nth 2 bindings)
            gs (map (fn [b] (if (symbol? b) b (gensym))) bs)
            bfs (reduce1 (fn [ret [b v g]]
                           (if (symbol? b)
                             (conj ret g v)
                             (conj ret g v b g)))
                  [] (map vector bs vs gs))]
        `(let ~bfs
           (loop* ~(vec (interleave gs gs))
             (let ~(vec (interleave bs gs))
               ~@body)))))))

(defmacro when-first
  "bindings => x xs

  Roughly the same as (when (seq xs) (let [x (first xs)] body)) but xs is evaluated only once"
  {:added "1.0"}
  [bindings & body]
  (assert-args
    (vector? bindings) "a vector for its binding"
    (= 2 (count bindings)) "exactly 2 forms in binding vector")
  (let [[x xs] bindings]
    `(when-let [xs# (seq ~xs)]
       (let [~x (first xs#)]
         ~@body))))

(defmacro for
  "List comprehension. Takes a vector of one or more
   binding-form/collection-expr pairs, each followed by zero or more
   modifiers, and yields a lazy sequence of evaluations of expr.
   Collections are iterated in a nested fashion, rightmost fastest,
   and nested coll-exprs can refer to bindings created in prior
   binding-forms.  Supported modifiers are: :let [binding-form expr ...],
   :while test, :when test.

  (take 100 (for [x (range 100000000) y (range 1000000) :while (< y x)] [x y]))"
  {:added "1.0"}
  [seq-exprs body-expr]
  (assert-args
    (vector? seq-exprs) "a vector for its binding"
    (even? (count seq-exprs)) "an even number of forms in binding vector")
  (let [to-groups (fn [seq-exprs]
                    (reduce1 (fn [groups [k v]]
                               (if (keyword? k)
                                 (conj (pop groups) (conj (peek groups) [k v]))
                                 (conj groups [k v])))
                      [] (partition 2 seq-exprs)))
        err (fn [& msg] (throw (IllegalArgumentException. ^String (apply str msg))))
        emit-bind (fn emit-bind [[[bind expr & mod-pairs]
                                  & [[_ next-expr] :as next-groups]]]
                    (let [giter (gensym "iter__")
                          gxs (gensym "s__")
                          do-mod (fn do-mod [[[k v :as pair] & etc]]
                                   (cond
                                     (= k :let ) `(let ~v ~(do-mod etc))
                                     (= k :while ) `(when ~v ~(do-mod etc))
                                     (= k :when ) `(if ~v
                                                     ~(do-mod etc)
                                                     (recur (rest ~gxs)))
                                     (keyword? k) (err "Invalid 'for' keyword " k)
                                     next-groups
                                     `(let [iterys# ~(emit-bind next-groups)
                                            fs# (seq (iterys# ~next-expr))]
                                        (if fs#
                                          (concat fs# (~giter (rest ~gxs)))
                                          (recur (rest ~gxs))))
                                     :else `(cons ~body-expr
                                              (~giter (rest ~gxs)))))]
                      (if next-groups
                        #_ "not the inner-most loop"
                        `(fn ~giter [~gxs]
                           (lazy-seq
                             (loop [~gxs ~gxs]
                               (when-first [~bind ~gxs]
                                 ~(do-mod mod-pairs)))))
                        #_ "inner-most loop"
                        (let [gi (gensym "i__")
                              gb (gensym "b__")
                              do-cmod (fn do-cmod [[[k v :as pair] & etc]]
                                        (cond
                                          (= k :let ) `(let ~v ~(do-cmod etc))
                                          (= k :while ) `(when ~v ~(do-cmod etc))
                                          (= k :when ) `(if ~v
                                                          ~(do-cmod etc)
                                                          (recur
                                                            (unchecked-inc ~gi)))
                                          (keyword? k)
                                          (err "Invalid 'for' keyword " k)
                                          :else `(do (chunk-append ~gb ~body-expr)
                                                   (recur (unchecked-inc ~gi)))))]
                          `(fn ~giter [~gxs]
                             (lazy-seq
                               (loop [~gxs ~gxs]
                                 (when-let [~gxs (seq ~gxs)]
                                   (if (chunked-seq? ~gxs)
                                     (let [c# (chunk-first ~gxs)
                                           size# (int (count c#))
                                           ~gb (chunk-buffer size#)]
                                       (if (loop [~gi (int 0)]
                                             (if (< ~gi size#)
                                               (let [~bind (.nth c# ~gi)]
                                                 ~(do-cmod mod-pairs))
                                               true))
                                         (chunk-cons
                                           (chunk ~gb)
                                           (~giter (chunk-rest ~gxs)))
                                         (chunk-cons (chunk ~gb) nil)))
                                     (let [~bind (first ~gxs)]
                                       ~(do-mod mod-pairs)))))))))))]
    `(let [iter# ~(emit-bind (to-groups seq-exprs))]
       (iter# ~(second seq-exprs)))))

(defmacro with-out-str
  "Evaluates exprs in a context in which *out* is bound to a fresh
  StringWriter.  Returns the string created by any nested printing
  calls."
  {:added "1.0"}
  [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*out* s#]
       ~@body
       (str s#))))

(defmacro with-in-str
  "Evaluates body in a context in which *in* is bound to a fresh
  StringReader initialized with the string s."
  {:added "1.0"}
  [s & body]
  `(with-open [s# (-> (java.io.StringReader. ~s) clojure.lang.LineNumberingPushbackReader.)]
     (binding [*in* s#]
       ~@body)))

(defn pr-str
  "pr to a string, returning it"
  {:tag String
   :added "1.0"
   :static true}
  [& xs]
  (with-out-str
    (apply pr xs)))

(defn print-str
  "print to a string, returning it"
  {:tag String
   :added "1.0"
   :static true}
  [& xs]
  (with-out-str
    (apply print xs)))


(import clojure.lang.ExceptionInfo clojure.lang.IExceptionInfo)
(defn ex-info
  "Create an instance of ExceptionInfo, a RuntimeException subclass
   that carries a map of additional data."
  {:added "1.4"}
  ([msg map]
    (ExceptionInfo. msg map))
  ([msg map cause]
    (ExceptionInfo. msg map cause)))

(defn ex-data
  "Returns exception data (a map) if ex is an IExceptionInfo.
   Otherwise returns nil."
  {:added "1.4"}
  [ex]
  (when (instance? IExceptionInfo ex)
    (.getData ^IExceptionInfo ex)))

(defn re-pattern
  "Returns an instance of java.util.regex.Pattern, for use, e.g. in
  re-matcher."
  {:tag java.util.regex.Pattern
   :added "1.0"
   :static true}
  [s] (if (instance? java.util.regex.Pattern s)
        s
        (. java.util.regex.Pattern (compile s))))

(defn re-matcher
  "Returns an instance of java.util.regex.Matcher, for use, e.g. in
  re-find."
  {:tag java.util.regex.Matcher
   :added "1.0"
   :static true}
  [^java.util.regex.Pattern re s]
  (. re (matcher s)))

(defn re-groups
  "Returns the groups from the most recent match/find. If there are no
  nested groups, returns a string of the entire match. If there are
  nested groups, returns a vector of the groups, the first element
  being the entire match."
  {:added "1.0"
   :static true}
  [^java.util.regex.Matcher m]
  (let [gc (. m (groupCount))]
    (if (zero? gc)
      (. m (group))
      (loop [ret [] c 0]
        (if (<= c gc)
          (recur (conj ret (. m (group c))) (inc c))
          ret)))))

(defn re-matches
  "Returns the match, if any, of string to pattern, using
  java.util.regex.Matcher.matches().  Uses re-groups to return the
  groups."
  {:added "1.0"
   :static true}
  [^java.util.regex.Pattern re s]
  (let [m (re-matcher re s)]
    (when (. m (matches))
      (re-groups m))))

(defn re-find
  "Returns the next regex match, if any, of string to pattern, using
  java.util.regex.Matcher.find().  Uses re-groups to return the
  groups."
  {:added "1.0"
   :static true}
  ([^java.util.regex.Matcher m]
    (when (. m (find))
      (re-groups m)))
  ([^java.util.regex.Pattern re s]
    (let [m (re-matcher re s)]
      (re-find m))))

(defmacro defn-
  "same as defn, yielding non-public def"
  {:added "1.0"}
  [name & decls]
  (list* `defn (with-meta name (assoc (meta name) :private true)) decls))


(defn subs
  "Returns the substring of s beginning at start inclusive, and ending
  at end (defaults to length of string), exclusive."
  {:added "1.0"
   :static true}
  (^String [^String s start] (. s (substring start)))
  (^String [^String s start end] (. s (substring start end))))


(defmacro dosync
  "Runs the exprs (in an implicit do) in a transaction that encompasses
  exprs and any nested calls.  Starts a transaction if none is already
  running on this thread. Any uncaught exception will abort the
  transaction and flow out of dosync. The exprs may be run more than
  once, but any effects on Refs will be atomic."
  {:added "1.0"}
  [& exprs]
  `(sync nil ~@exprs))

(defn hash
  "Returns the hash code of its argument. Note this is the hash code
  consistent with =, and thus is different than .hashCode for Integer,
  Short, Byte and Clojure collections."

  {:added "1.0"
   :static true}
  [x] (. clojure.lang.Util (hasheq x)))

(defn interpose
  "Returns a lazy seq of the elements of coll separated by sep"
  {:added "1.0"
   :static true}
  [sep coll] (drop 1 (interleave (repeat sep) coll)))

(defn class?
  "Returns true if x is an instance of Class"
  {:added "1.0"
   :static true}
  [x] (instance? Class x))


(defn make-hierarchy
  "Creates a hierarchy object for use with derive, isa? etc."
  {:added "1.0"
   :static true}
  [] {:parents {} :descendants {} :ancestors {}})

(def ^{:private true}
  global-hierarchy (make-hierarchy))

(defn not-empty
  "If coll is empty, returns nil, else coll"
  {:added "1.0"
   :static true}
  [coll] (when (seq coll) coll))

(defn bases
  "Returns the immediate superclass and direct interfaces of c, if any"
  {:added "1.0"
   :static true}
  [^Class c]
  (when c
    (let [i (seq (.getInterfaces c))
          s (.getSuperclass c)]
      (if s (cons s i) i))))

(defn parents
  "Returns the immediate parents of tag, either via a Java type
  inheritance relationship or a relationship established via derive. h
  must be a hierarchy obtained from make-hierarchy, if not supplied
  defaults to the global hierarchy"
  {:added "1.0"}
  ([tag] (parents global-hierarchy tag))
  ([h tag] (not-empty
             (let [tp (get (:parents h) tag)]
               (if (class? tag)
                 (into1 (set (bases tag)) tp)
                 tp)))))

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

(defn format
  "Formats a string using java.lang.String.format, see java.util.Formatter for format
  string syntax"
  {:added "1.0"
   :static true}
  ^String [fmt & args]
  (String/format fmt (to-array args)))

(defn printf
  "Prints formatted output, as per format"
  {:added "1.0"
   :static true}
  [fmt & args]
  (print (apply format fmt args)))

(declare gen-class)

(defmacro with-loading-context [& body]
  `((fn loading# []
      (. clojure.lang.Var (pushThreadBindings {clojure.lang.Compiler/LOADER
                                               (.getClassLoader (.getClass ^Object loading#))}))
      (try
        ~@body
        (finally
          (. clojure.lang.Var (popThreadBindings)))))))

(defmacro ns
  "Sets *ns* to the namespace named by name (unevaluated), creating it
  if needed.  references can be zero or more of: (:refer-clojure ...)
  (:require ...) (:use ...) (:import ...) (:load ...) (:gen-class)
  with the syntax of refer-clojure/require/use/import/load/gen-class
  respectively, except the arguments are unevaluated and need not be
  quoted. (:gen-class ...), when supplied, defaults to :name
  corresponding to the ns name, :main true, :impl-ns same as ns, and
  :init-impl-ns true. All options of gen-class are
  supported. The :gen-class directive is ignored when not
  compiling. If :gen-class is not supplied, when compiled only an
  nsname__init.class will be generated. If :refer-clojure is not used, a
  default (refer 'clojure.core) is used.  Use of ns is preferred to
  individual calls to in-ns/require/use/import:

  (ns foo.bar
    (:refer-clojure :exclude [ancestors printf])
    (:require (clojure.contrib sql combinatorics))
    (:use (my.lib this that))
    (:import (java.util Date Timer Random)
             (java.sql Connection Statement)))"
  {:arglists '([name docstring? attr-map? references*])
   :added "1.0"}
  [name & references]
  (let [process-reference
        (fn [[kname & args]]
          `(~(symbol "clojure.core" (clojure.core/name kname))
             ~@(map #(list 'quote %) args)))
        docstring (when (string? (first references)) (first references))
        references (if docstring (next references) references)
        name (if docstring
               (vary-meta name assoc :doc docstring)
               name)
        metadata (when (map? (first references)) (first references))
        references (if metadata (next references) references)
        name (if metadata
               (vary-meta name merge metadata)
               name)
        gen-class-clause (first (filter #(= :gen-class (first %)) references))
        gen-class-call
        (when gen-class-clause
          (list* `gen-class :name (.replace (str name) \- \_) :impl-ns name :main true (next gen-class-clause)))
        references (remove #(= :gen-class (first %)) references)
        ;ns-effect (clojure.core/in-ns name)
        ]
    `(do
       (clojure.core/in-ns '~name)
       (with-loading-context
         ~@(when gen-class-call (list gen-class-call))
         ~@(when (and (not= name 'clojure.core) (not-any? #(= :refer-clojure (first %)) references))
             `((clojure.core/refer '~'clojure.core)))
         ~@(map process-reference references))
       (if (.equals '~name 'clojure.core)
         nil
         (do (dosync (commute @#'*loaded-libs* conj '~name)) nil)))))


(defmacro refer-clojure
  "Same as (refer 'clojure.core <filters>)"
  {:added "1.0"}
  [& filters]
  `(clojure.core/refer '~'clojure.core ~@filters))


(defmacro defonce
  "defs name to have the root value of the expr iff the named var has no root value,
  else expr is unevaluated"
  {:added "1.0"}
  [name expr]
  `(let [v# (def ~name)]
     (when-not (.hasRoot v#)
       (def ~name ~expr))))

;;;;;;;;;;; require/use/load, contributed by Stephen C. Gilardi ;;;;;;;;;;;;;;;;;;

(defonce ^:dynamic
  ^{:private true
    :doc "A ref to a sorted set of symbols representing loaded libs"}
  *loaded-libs* (ref (sorted-set)))

(defonce ^:dynamic
  ^{:private true
    :doc "A stack of paths currently being loaded by this thread"}
  *pending-paths* ())

(defonce ^:dynamic
  ^{:private true :doc "True while a verbose load is pending"}
  *loading-verbosely* false)

(defn- throw-if
  "Throws a CompilerException with a message if pred is true"
  [pred fmt & args]
  (when pred
    (let [^String message (apply format fmt args)
          exception (Exception. message)
          raw-trace (.getStackTrace exception)
          boring? #(not= (.getMethodName ^StackTraceElement %) "doInvoke")
          trace (into-array (drop 2 (drop-while boring? raw-trace)))]
      (.setStackTrace exception trace)
      (throw (clojure.lang.Compiler$CompilerException.
               *file*
               (.deref clojure.lang.Compiler/LINE)
               (.deref clojure.lang.Compiler/COLUMN)
               exception)))))

(defn- libspec?
  "Returns true if x is a libspec"
  [x]
  (or (symbol? x)
    (and (vector? x)
      (or
        (nil? (second x))
        (keyword? (second x))))))

(defn- prependss
  "Prepends a symbol or a seq to coll"
  [x coll]
  (if (symbol? x)
    (cons x coll)
    (concat x coll)))


(defn- root-resource
  "Returns the root directory path for a lib"
  {:tag String}
  [lib]
  (str \/
    (.. (name lib)
      (replace \- \_)
      (replace \. \/))))

(defn- root-directory
  "Returns the root resource path for a lib"
  [lib]
  (let [d (root-resource lib)]
    (subs d 0 (.lastIndexOf d "/"))))


(declare load)

(defn- load-one
  "Loads a lib given its name. If need-ns, ensures that the associated
  namespace exists after loading. If require, records the load so any
  duplicate loads can be skipped."
  [lib need-ns require]
  (load (root-resource lib))
  (throw-if (and need-ns (not (find-ns lib)))
    "namespace '%s' not found after loading '%s'"
    lib (root-resource lib))
  (when require
    (dosync
      (commute *loaded-libs* conj lib))))

(defn- load-all
  "Loads a lib given its name and forces a load of any libs it directly or
  indirectly loads. If need-ns, ensures that the associated namespace
  exists after loading. If require, records the load so any duplicate loads
  can be skipped."
  [lib need-ns require]
  (dosync
    (commute *loaded-libs* #(reduce1 conj %1 %2)
      (binding [*loaded-libs* (ref (sorted-set))]
        (load-one lib need-ns require)
        @*loaded-libs*))))

(defn- load-lib
  "Loads a lib with options"
  [prefix lib & options]
  (throw-if (and prefix (pos? (.indexOf (name lib) (int \.))))
    "Found lib name '%s' containing period with prefix '%s'.  lib names inside prefix lists must not contain periods"
    (name lib) prefix)
  (let [lib (if prefix (symbol (str prefix \. lib)) lib)
        opts (apply hash-map options)
        {:keys [as reload reload-all require use verbose]} opts
        loaded (contains? @*loaded-libs* lib)
        load (cond reload-all
               load-all
               (or reload (not require) (not loaded))
               load-one)
        need-ns (or as use)
        filter-opts (select-keys opts '(:exclude :only :rename :refer ))
        undefined-on-entry (not (find-ns lib))]
    (binding [*loading-verbosely* (or *loading-verbosely* verbose)]
      (if load
        (try
          (load lib need-ns require)
          (catch Exception e
            (when undefined-on-entry
              (remove-ns lib))
            (throw e)))
        (throw-if (and need-ns (not (find-ns lib)))
          "namespace '%s' not found" lib))
      (when (and need-ns *loading-verbosely*)
        (printf "(clojure.core/in-ns '%s)\n" (ns-name *ns*)))
      (when as
        (when *loading-verbosely*
          (printf "(clojure.core/alias '%s '%s)\n" as lib))
        (alias as lib))
      (when (or use (:refer filter-opts))
        (when *loading-verbosely*
          (printf "(clojure.core/refer '%s" lib)
          (doseq [opt filter-opts]
            (printf " %s '%s" (key opt) (print-str (val opt))))
          (printf ")\n"))
        (apply refer lib (mapcat seq filter-opts))))))

(defn- load-libs
  "Loads libs, interpreting libspecs, prefix lists, and flags for
  forwarding to load-lib"
  [& args]
  (let [flags (filter keyword? args)
        opts (interleave flags (repeat true))
        args (filter (complement keyword?) args)]
    ; check for unsupported options
    (let [supported #{:as :reload :reload-all :require :use :verbose :refer }
          unsupported (seq (remove supported flags))]
      (throw-if unsupported
        (apply str "Unsupported option(s) supplied: "
          (interpose \, unsupported))))
    ; check a load target was specified
    (throw-if (not (seq args)) "Nothing specified to load")
    (doseq [arg args]
      (if (libspec? arg)
        (apply load-lib nil (prependss arg opts))
        (let [[prefix & args] arg]
          (throw-if (nil? prefix) "prefix cannot be nil")
          (doseq [arg args]
            (apply load-lib prefix (prependss arg opts))))))))

(defn- check-cyclic-dependency
  "Detects and rejects non-trivial cyclic load dependencies. The
  exception message shows the dependency chain with the cycle
  highlighted. Ignores the trivial case of a file attempting to load
  itself because that can occur when a gen-class'd class loads its
  implementation."
  [path]
  (when (some #{path} (rest *pending-paths*))
    (let [pending (map #(if (= % path) (str "[ " % " ]") %)
                    (cons path *pending-paths*))
          chain (apply str (interpose "->" pending))]
      (throw-if true "Cyclic load dependency: %s" chain))))

;; Public

(defn require
  "Loads libs, skipping any that are already loaded. Each argument is
  either a libspec that identifies a lib, a prefix list that identifies
  multiple libs whose names share a common prefix, or a flag that modifies
  how all the identified libs are loaded. Use :require in the ns macro
  in preference to calling this directly.

  Libs

  A 'lib' is a named set of resources in classpath whose contents define a
  library of Clojure code. Lib names are symbols and each lib is associated
  with a Clojure namespace and a Java package that share its name. A lib's
  name also locates its root directory within classpath using Java's
  package name to classpath-relative path mapping. All resources in a lib
  should be contained in the directory structure under its root directory.
  All definitions a lib makes should be in its associated namespace.

  'require loads a lib by loading its root resource. The root resource path
  is derived from the lib name in the following manner:
  Consider a lib named by the symbol 'x.y.z; it has the root directory
  <classpath>/x/y/, and its root resource is <classpath>/x/y/z.clj. The root
  resource should contain code to create the lib's namespace (usually by using
  the ns macro) and load any additional lib resources.

  Libspecs

  A libspec is a lib name or a vector containing a lib name followed by
  options expressed as sequential keywords and arguments.

  Recognized options:
  :as takes a symbol as its argument and makes that symbol an alias to the
    lib's namespace in the current namespace.
  :refer takes a list of symbols to refer from the namespace or the :all
    keyword to bring in all public vars.

  Prefix Lists

  It's common for Clojure code to depend on several libs whose names have
  the same prefix. When specifying libs, prefix lists can be used to reduce
  repetition. A prefix list contains the shared prefix followed by libspecs
  with the shared prefix removed from the lib names. After removing the
  prefix, the names that remain must not contain any periods.

  Flags

  A flag is a keyword.
  Recognized flags: :reload, :reload-all, :verbose
  :reload forces loading of all the identified libs even if they are
    already loaded
  :reload-all implies :reload and also forces loading of all libs that the
    identified libs directly or indirectly load via require or use
  :verbose triggers printing information about each load, alias, and refer

  Example:

  The following would load the libraries clojure.zip and clojure.set
  abbreviated as 's'.

  (require '(clojure zip [set :as s]))"
  {:added "1.0"}

  [& args]
  (apply load-libs :require args))

(defn load
  "Loads Clojure code from resources in classpath. A path is interpreted as
  classpath-relative if it begins with a slash or relative to the root
  directory for the current namespace otherwise."
  {:added "1.0"}
  [& paths]
  (doseq [^String path paths]
    (let [^String path (if (.startsWith path "/")
                         path
                         (str (root-directory (ns-name *ns*)) \/ path))]
      (when *loading-verbosely*
        (printf "(clojure.core/load \"%s\")\n" path)
        (flush))
      (check-cyclic-dependency path)
      (when-not (= path (first *pending-paths*))
        (binding [*pending-paths* (conj *pending-paths* path)]
          (clojure.lang.RT/load (.substring path 1)))))))

(def ^:dynamic
  ^{:doc "bound in a repl thread to the most recent value printed"
    :added "1.0"}
  *1)

(def ^:dynamic
  ^{:doc "bound in a repl thread to the second most recent value printed"
    :added "1.0"}
  *2)

(def ^:dynamic
  ^{:doc "bound in a repl thread to the third most recent value printed"
    :added "1.0"}
  *3)

(def ^:dynamic
  ^{:doc "bound in a repl thread to the most recent exception caught by the repl"
    :added "1.0"}
  *e)


(defn intern
  "Finds or creates a var named by the symbol name in the namespace
  ns (which can be a symbol or a namespace), setting its root binding
  to val if supplied. The namespace must exist. The var will adopt any
  metadata from the name symbol.  Returns the var."
  {:added "1.0"
   :static true}
  ([ns ^clojure.lang.Symbol name]
    (let [v (clojure.lang.Var/intern (the-ns ns) name)]
      (when (meta name) (.setMeta v (meta name)))
      v))
  ([ns name val]
    (let [v (clojure.lang.Var/intern (the-ns ns) name val)]
      (when (meta name) (.setMeta v (meta name)))
      v)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; var documentation ;;;;;;;;;;;;;;;;;;;;;;;;;;

(alter-meta! #'*agent* assoc :added "1.0")
(alter-meta! #'in-ns assoc :added "1.0")
(alter-meta! #'load-file assoc :added "1.0")

(defmacro add-doc-and-meta {:private true} [name docstring meta]
  `(alter-meta! (var ~name) merge (assoc ~meta :doc ~docstring)))

(add-doc-and-meta *file*
  "The path of the file being evaluated, as a String.

  When there is no file, e.g. in the REPL, the value is not defined."
  {:added "1.0"})

(add-doc-and-meta *command-line-args*
  "A sequence of the supplied command line arguments, or nil if
  none were supplied"
  {:added "1.0"})

(add-doc-and-meta *warn-on-reflection*
  "When set to true, the compiler will emit warnings when reflection is
  needed to resolve Java method calls or field accesses.

  Defaults to false."
  {:added "1.0"})

(add-doc-and-meta *compile-path*
  "Specifies the directory where 'compile' will write out .class
  files. This directory must be in the classpath for 'compile' to
  work.

  Defaults to \"classes\""
  {:added "1.0"})

(add-doc-and-meta *compile-files*
  "Set to true when compiling files, false otherwise."
  {:added "1.0"})

(add-doc-and-meta *unchecked-math*
  "While bound to true, compilations of +, -, *, inc, dec and the
  coercions will be done without overflow checks. Default: false."
  {:added "1.3"})

(add-doc-and-meta *compiler-options*
  "A map of keys to options.
  Note, when binding dynamically make sure to merge with previous value.
  Supported options:
  :elide-meta - a collection of metadata keys to elide during compilation.
  :disable-locals-clearing - set to true to disable clearing, useful for using a debugger
  Alpha, subject to change."
  {:added "1.4"})

(add-doc-and-meta *ns*
  "A clojure.lang.Namespace object representing the current namespace."
  {:added "1.0"})

(add-doc-and-meta *in*
  "A java.io.Reader object representing standard input for read operations.

  Defaults to System/in, wrapped in a LineNumberingPushbackReader"
  {:added "1.0"})

(add-doc-and-meta *out*
  "A java.io.Writer object representing standard output for print operations.

  Defaults to System/out, wrapped in an OutputStreamWriter"
  {:added "1.0"})

(add-doc-and-meta *err*
  "A java.io.Writer object representing standard error for print operations.

  Defaults to System/err, wrapped in a PrintWriter"
  {:added "1.0"})

(add-doc-and-meta *flush-on-newline*
  "When set to true, output will be flushed whenever a newline is printed.

  Defaults to true."
  {:added "1.0"})

(add-doc-and-meta *print-meta*
  "If set to logical true, when printing an object, its metadata will also
  be printed in a form that can be read back by the reader.

  Defaults to false."
  {:added "1.0"})

(add-doc-and-meta *print-dup*
  "When set to logical true, objects will be printed in a way that preserves
  their type when read in later.

  Defaults to false."
  {:added "1.0"})

(add-doc-and-meta *print-readably*
  "When set to logical false, strings and characters will be printed with
  non-alphanumeric characters converted to the appropriate escape sequences.

  Defaults to true"
  {:added "1.0"})

(add-doc-and-meta *read-eval*
  "Defaults to true (or value specified by system property, see below)
   ***This setting implies that the full power of the reader is in play,
   including syntax that can cause code to execute. It should never be
   used with untrusted sources. See also: clojure.edn/read.***

   When set to logical false in the thread-local binding,
   the eval reader (#=) and record/type literal syntax are disabled in read/load.
   Example (will fail): (binding [*read-eval* false] (read-string \"#=(* 2 21)\"))

   The default binding can be controlled by the system property
   'clojure.read.eval' System properties can be set on the command line
   like this:

   java -Dclojure.read.eval=false ...

   The system property can also be set to 'unknown' via
   -Dclojure.read.eval=unknown, in which case the default binding
   is :unknown and all reads will fail in contexts where *read-eval*
   has not been explicitly bound to either true or false. This setting
   can be a useful diagnostic tool to ensure that all of your reads
   occur in considered contexts. You can also accomplish this in a
   particular scope by binding *read-eval* to :unknown
   "
  {:added "1.0"})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; helper files ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(alter-meta! (find-ns 'clojure.core) assoc :doc "Fundamental library of the Clojure language")
(load "core_proxy") ;;TODO it can be removed, but some functionalities will not working
(load "core_print")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; clojure version number ;;;;;;;;;;;;;;;;;;;;;;

(let [properties (with-open [version-stream (.getResourceAsStream
                                              (clojure.lang.RT/baseLoader)
                                              "clojure/version.properties")]
                   (doto (new java.util.Properties)
                     (.load version-stream)))
      version-string (.getProperty properties "version")
      [_ major minor incremental qualifier snapshot]
      (re-matches
        #"(\d+)\.(\d+)\.(\d+)(?:-([a-zA-Z0-9_]+))?(?:-(SNAPSHOT))?"
        version-string)
      clojure-version {:major (Integer/valueOf ^String major)
                       :minor (Integer/valueOf ^String minor)
                       :incremental (Integer/valueOf ^String incremental)
                       :qualifier (if (= qualifier "SNAPSHOT") nil qualifier)}]
  (def ^:dynamic *clojure-version*
    (if (.contains version-string "SNAPSHOT")
      (clojure.lang.RT/assoc clojure-version :interim true)
      clojure-version)))

(add-doc-and-meta *clojure-version*
  "The version info for Clojure core, as a map containing :major :minor
  :incremental and :qualifier keys. Feature releases may increment
  :minor and/or :major, bugfix releases will increment :incremental.
  Possible values of :qualifier include \"GA\", \"SNAPSHOT\", \"RC-x\" \"BETA-x\""
  {:added "1.0"})

(defn
  clojure-version
  "Returns clojure version as a printable string."
  {:added "1.0"}
  []
  (str (:major *clojure-version*)
    "."
    (:minor *clojure-version*)
    (when-let [i (:incremental *clojure-version*)]
      (str "." i))
    (when-let [q (:qualifier *clojure-version*)]
      (when (pos? (count q)) (str "-" q)))
    (when (:interim *clojure-version*)
      "-SNAPSHOT")))

