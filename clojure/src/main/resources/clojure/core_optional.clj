(in-ns 'clojure.core)


;;;;;;;;;;;;;;;;at this point all the support for syntax-quote exists;;;;;;;;;;;;;;;;;;;;;;
(defmacro delay
    "Takes a body of expressions and yields a Delay object that will
    invoke the body only the first time it is forced (with force or deref/@), and
    will cache the result and return it on all subsequent force
    calls. See also - realized?"
    {:added "1.0"}
    [& body]
    (list 'new 'clojure.lang.Delay (list* `^{:once true} fn* [] body)))

(defn delay?
    "returns true if x is a Delay created with delay"
    {:added "1.0"
     :static true}
    [x] (instance? clojure.lang.Delay x))

(defn force
    "If x is a Delay, returns the (possibly cached) value of its expression, else returns x"
    {:added "1.0"
    :static true}
    [x] (. clojure.lang.Delay (force x)))

(defn inc'
          "Returns a number one greater than num. Supports arbitrary precision.
           See also: inc"
    {:inline (fn [x] `(. clojure.lang.Numbers (incP ~x)))
     :added "1.0"}
    [x] (. clojure.lang.Numbers (incP x)))

;;math stuff

(defn +'
         "Returns the sum of nums. (+) returns 0. Supports arbitrary precision.
         See also: +"
    {:inline (nary-inline 'addP)
     :inline-arities >1?
     :added "1.0"}
    ([] 0)
    ([x] (cast Number x))
    ([x y] (. clojure.lang.Numbers (addP x y)))
    ([x y & more]
      (reduce1 +' (+' x y) more)))

(defn *'
         "Returns the product of nums. (*) returns 1. Supports arbitrary precision.
         See also: *"
    {:inline (nary-inline 'multiplyP)
     :inline-arities >1?
     :added "1.0"}
    ([] 1)
    ([x] (cast Number x))
    ([x y] (. clojure.lang.Numbers (multiplyP x y)))
    ([x y & more]
      (reduce1 *' (*' x y) more)))

(defn -'
         "If no ys are supplied, returns the negation of x, else subtracts
         the ys from x and returns the result. Supports arbitrary precision.
         See also: -"
    {:inline (nary-inline 'minusP)
     :inline-arities >0?
     :added "1.0"}
    ([x] (. clojure.lang.Numbers (minusP x)))
    ([x y] (. clojure.lang.Numbers (minusP x y)))
    ([x y & more]
      (reduce1 -' (-' x y) more)))

(defn dec'
           "Returns a number one less than num. Supports arbitrary precision.
           See also: dec"
    {:inline (fn [x] `(. clojure.lang.Numbers (decP ~x)))
     :added "1.0"}
    [x] (. clojure.lang.Numbers (decP x)))

(defn unchecked-inc-int
    "Returns a number one greater than x, an int.
    Note - uses a primitive operator subject to overflow."
    {:inline (fn [x] `(. clojure.lang.Numbers (unchecked_int_inc ~x)))
     :added "1.0"}
    [x] (. clojure.lang.Numbers (unchecked_int_inc x)))

(defn unchecked-dec-int
    "Returns a number one less than x, an int.
    Note - uses a primitive operator subject to overflow."
    {:inline (fn [x] `(. clojure.lang.Numbers (unchecked_int_dec ~x)))
     :added "1.0"}
    [x] (. clojure.lang.Numbers (unchecked_int_dec x)))

(defn unchecked-dec
    "Returns a number one less than x, a long.
    Note - uses a primitive operator subject to overflow."
    {:inline (fn [x] `(. clojure.lang.Numbers (unchecked_dec ~x)))
     :added "1.0"}
    [x] (. clojure.lang.Numbers (unchecked_dec x)))

(defn unchecked-negate-int
    "Returns the negation of x, an int.
    Note - uses a primitive operator subject to overflow."
    {:inline (fn [x] `(. clojure.lang.Numbers (unchecked_int_negate ~x)))
     :added "1.0"}
    [x] (. clojure.lang.Numbers (unchecked_int_negate x)))

(defn unchecked-negate
    "Returns the negation of x, a long.
    Note - uses a primitive operator subject to overflow."
    {:inline (fn [x] `(. clojure.lang.Numbers (unchecked_minus ~x)))
     :added "1.0"}
    [x] (. clojure.lang.Numbers (unchecked_minus x)))

(defn unchecked-add-int
    "Returns the sum of x and y, both int.
    Note - uses a primitive operator subject to overflow."
    {:inline (fn [x y] `(. clojure.lang.Numbers (unchecked_int_add ~x ~y)))
     :added "1.0"}
    [x y] (. clojure.lang.Numbers (unchecked_int_add x y)))

(defn unchecked-add
    "Returns the sum of x and y, both long.
    Note - uses a primitive operator subject to overflow."
    {:inline (fn [x y] `(. clojure.lang.Numbers (unchecked_add ~x ~y)))
     :added "1.0"}
    [x y] (. clojure.lang.Numbers (unchecked_add x y)))

(defn unchecked-subtract-int
    "Returns the difference of x and y, both int.
    Note - uses a primitive operator subject to overflow."
    {:inline (fn [x y] `(. clojure.lang.Numbers (unchecked_int_subtract ~x ~y)))
     :added "1.0"}
    [x y] (. clojure.lang.Numbers (unchecked_int_subtract x y)))

(defn unchecked-subtract
    "Returns the difference of x and y, both long.
    Note - uses a primitive operator subject to overflow."
    {:inline (fn [x y] `(. clojure.lang.Numbers (unchecked_minus ~x ~y)))
     :added "1.0"}
    [x y] (. clojure.lang.Numbers (unchecked_minus x y)))

(defn unchecked-multiply-int
    "Returns the product of x and y, both int.
    Note - uses a primitive operator subject to overflow."
    {:inline (fn [x y] `(. clojure.lang.Numbers (unchecked_int_multiply ~x ~y)))
     :added "1.0"}
    [x y] (. clojure.lang.Numbers (unchecked_int_multiply x y)))

(defn unchecked-multiply
    "Returns the product of x and y, both long.
    Note - uses a primitive operator subject to overflow."
    {:inline (fn [x y] `(. clojure.lang.Numbers (unchecked_multiply ~x ~y)))
     :added "1.0"}
    [x y] (. clojure.lang.Numbers (unchecked_multiply x y)))

(defn unchecked-divide-int
    "Returns the division of x by y, both int.
    Note - uses a primitive operator subject to truncation."
    {:inline (fn [x y] `(. clojure.lang.Numbers (unchecked_int_divide ~x ~y)))
     :added "1.0"}
    [x y] (. clojure.lang.Numbers (unchecked_int_divide x y)))

(defn unchecked-remainder-int
    "Returns the remainder of division of x by y, both int.
    Note - uses a primitive operator subject to truncation."
    {:inline (fn [x y] `(. clojure.lang.Numbers (unchecked_int_remainder ~x ~y)))
     :added "1.0"}
    [x y] (. clojure.lang.Numbers (unchecked_int_remainder x y)))

(defn quot
    "quot[ient] of dividing numerator by denominator."
    {:added "1.0"
     :static true
     :inline (fn [x y] `(. clojure.lang.Numbers (quotient ~x ~y)))}
    [num div]
    (. clojure.lang.Numbers (quotient num div)))

(defn rem
    "remainder of dividing numerator by denominator."
    {:added "1.0"
     :static true
     :inline (fn [x y] `(. clojure.lang.Numbers (remainder ~x ~y)))}
    [num div]
    (. clojure.lang.Numbers (remainder num div)))

(defn rationalize
    "returns the rational value of num"
    {:added "1.0"
     :static true}
    [num]
    (. clojure.lang.Numbers (rationalize num)))

;;Bit ops

(defn bit-not
    "Bitwise complement"
    {:inline (fn [x] `(. clojure.lang.Numbers (not ~x)))
     :added "1.0"}
    [x] (. clojure.lang.Numbers not x))

(defn bit-xor
    "Bitwise exclusive or"
    {:inline (nary-inline 'xor)
     :inline-arities >1?
     :added "1.0"}
    ([x y] (. clojure.lang.Numbers xor x y))
    ([x y & more]
      (reduce1 bit-xor (bit-xor x y) more)))

(defn bit-and-not
    "Bitwise and with complement"
    {:inline (nary-inline 'andNot)
     :inline-arities >1?
     :added "1.0"
     :static true}
    ([x y] (. clojure.lang.Numbers andNot x y))
    ([x y & more]
      (reduce1 bit-and-not (bit-and-not x y) more)))


(defn bit-clear
    "Clear bit at index n"
    {:added "1.0"
     :static true}
    [x n] (. clojure.lang.Numbers clearBit x n))

(defn bit-set
    "Set bit at index n"
    {:added "1.0"
     :static true}
    [x n] (. clojure.lang.Numbers setBit x n))

(defn bit-flip
    "Flip bit at index n"
    {:added "1.0"
     :static true}
    [x n] (. clojure.lang.Numbers flipBit x n))

(defn bit-test
    "Test bit at index n"
    {:added "1.0"
     :static true}
    [x n] (. clojure.lang.Numbers testBit x n))


(defn bit-shift-left
    "Bitwise shift left"
    {:inline (fn [x n] `(. clojure.lang.Numbers (shiftLeft ~x ~n)))
     :added "1.0"}
    [x n] (. clojure.lang.Numbers shiftLeft x n))

(defn bit-shift-right
    "Bitwise shift right"
    {:inline (fn [x n] `(. clojure.lang.Numbers (shiftRight ~x ~n)))
     :added "1.0"}
    [x n] (. clojure.lang.Numbers shiftRight x n))

(defn unsigned-bit-shift-right
    "Bitwise shift right, without sign-extension."
    {:inline (fn [x n] `(. clojure.lang.Numbers (unsignedShiftRight ~x ~n)))
     :added "1.6"}
    [x n] (. clojure.lang.Numbers unsignedShiftRight x n))

(defn rseq
    "Returns, in constant time, a seq of the items in rev (which
    can be a vector or sorted-map), in reverse order. If rev is empty returns nil"
    {:added "1.0"
     :static true}
    [^clojure.lang.Reversible rev]
    (. rev (rseq)))

(defmacro locking
    "Executes exprs in an implicit do, while holding the monitor of x.
    Will release the monitor of x in all circumstances."
    {:added "1.0"}
    [x & body]
    `(let [lockee# ~x]
       (try
         (monitor-enter lockee#)
         ~@body
         (finally
           (monitor-exit lockee#)))))

(defmacro ->>
    "Threads the expr through the forms. Inserts x as the
    last item in the first form, making a list of it if it is not a
    list already. If there are more forms, inserts the first form as the
    last item in second form, etc."
    {:added "1.1"}
    [x & forms]
    (loop [x x, forms forms]
      (if forms
        (let [form (first forms)
              threaded (if (seq? form)
                         (with-meta `(~(first form) ~@(next form)  ~x) (meta form))
                         (list form x))]
          (recur threaded (next forms)))
        x)))

(defn remove-all-methods
  "Removes all of the methods of multimethod."
  {:added "1.2"
   :static true}
  [^clojure.lang.MultiFn multifn]
  (.reset multifn))

(defn remove-method
  "Removes the method of multimethod associated with dispatch-value."
  {:added "1.0"
   :static true}
  [^clojure.lang.MultiFn multifn dispatch-val]
  (. multifn removeMethod dispatch-val))



(defn methods
  "Given a multimethod, returns a map of dispatch values -> dispatch fns"
  {:added "1.0"
   :static true}
  [^clojure.lang.MultiFn multifn] (.getMethodTable multifn))

(defn get-method
  "Given a multimethod and a dispatch value, returns the dispatch fn
  that would apply to that value, or nil if none apply and no default"
  {:added "1.0"
   :static true}
  [^clojure.lang.MultiFn multifn dispatch-val] (.getMethod multifn dispatch-val))

(defn prefers
  "Given a multimethod, returns a map of preferred value -> set of other values"
  {:added "1.0"
   :static true}
  [^clojure.lang.MultiFn multifn] (.getPreferTable multifn))

(defmacro if-some
    "bindings => binding-form test

     If test is not nil, evaluates then with binding-form bound to the
     value of test, if not, yields else"
    {:added "1.6"}
    ([bindings then]
      `(if-some ~bindings ~then nil))
    ([bindings then else & oldform]
      (assert-args
        (vector? bindings) "a vector for its binding"
        (nil? oldform) "1 or 2 forms after binding vector"
        (= 2 (count bindings)) "exactly 2 forms in binding vector")
      (let [form (bindings 0) tst (bindings 1)]
        `(let [temp# ~tst]
           (if (nil? temp#)
             ~else
             (let [~form temp#]
               ~then))))))


(defn bound-fn*
  "Returns a function, which will install the same bindings in effect as in
  the thread at the time bound-fn* was called and then call f with any given
  arguments. This may be used to define a helper function which runs on a
  different thread, but needs the same bindings in place."
  {:added "1.1"
   :static true}
  [f]
  (let [bindings (get-thread-bindings)]
    (fn [& args]
      (apply with-bindings* bindings f args))))

(defmacro bound-fn
  "Returns a function defined by the given fntail, which will install the
  same bindings in effect as in the thread at the time bound-fn was called.
  This may be used to define a helper function which runs on a different
  thread, but needs the same bindings in place."
  {:added "1.1"}
  [& fntail]
  `(bound-fn* (fn ~@fntail)))

(defn find-var
  "Returns the global var named by the namespace-qualified symbol, or
  nil if no var with that name."
  {:added "1.0"
   :static true}
  [sym] (. clojure.lang.Var (find sym)))


(defn binding-conveyor-fn
    {:private true
     :added "1.3"}
    [f]
    (let [frame (clojure.lang.Var/cloneThreadBindingFrame)]
      (fn
        ([]
          (clojure.lang.Var/resetThreadBindingFrame frame)
          (f))
        ([x]
          (clojure.lang.Var/resetThreadBindingFrame frame)
          (f x))
        ([x y]
          (clojure.lang.Var/resetThreadBindingFrame frame)
          (f x y))
        ([x y z]
          (clojure.lang.Var/resetThreadBindingFrame frame)
          (f x y z))
        ([x y z & args]
          (clojure.lang.Var/resetThreadBindingFrame frame)
          (apply f x y z args)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Refs ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn agent
    "Creates and returns an agent with an initial value of state and
    zero or more options (in any order):

    :meta metadata-map

    :validator validate-fn

    :error-handler handler-fn

    :error-mode mode-keyword

    If metadata-map is supplied, it will become the metadata on the
    agent. validate-fn must be nil or a side-effect-free fn of one
    argument, which will be passed the intended new state on any state
    change. If the new state is unacceptable, the validate-fn should
    return false or throw an exception.  handler-fn is called if an
    action throws an exception or if validate-fn rejects a new state --
    see set-error-handler! for details.  The mode-keyword may be either
    :continue (the default if an error-handler is given) or :fail (the
    default if no error-handler is given) -- see set-error-mode! for
    details."
    {:added "1.0"
     :static true
     }
    ([state & options]
      (let [a (new clojure.lang.Agent state)
            opts (apply hash-map options)]
        (setup-reference a options)
        (when (:error-handler opts)
          (.setErrorHandler a (:error-handler opts)))
        (.setErrorMode a (or (:error-mode opts)
                           (if (:error-handler opts) :continue :fail)))
        a)))

(defn set-agent-send-executor!
    "Sets the ExecutorService to be used by send"
    {:added "1.5"}
    [executor]
    (set! clojure.lang.Agent/pooledExecutor executor))

(defn set-agent-send-off-executor!
    "Sets the ExecutorService to be used by send-off"
    {:added "1.5"}
    [executor]
    (set! clojure.lang.Agent/soloExecutor executor))

(defn send-via
    "Dispatch an action to an agent. Returns the agent immediately.
    Subsequently, in a thread supplied by executor, the state of the agent
    will be set to the value of:

    (apply action-fn state-of-agent args)"
    {:added "1.5"}
    [executor ^clojure.lang.Agent a f & args]
    (.dispatch a (binding [*agent* a] (binding-conveyor-fn f)) args executor))

(defn send
    "Dispatch an action to an agent. Returns the agent immediately.
    Subsequently, in a thread from a thread pool, the state of the agent
    will be set to the value of:

    (apply action-fn state-of-agent args)"
    {:added "1.0"
     :static true}
    [^clojure.lang.Agent a f & args]
    (apply send-via clojure.lang.Agent/pooledExecutor a f args))

(defn send-off
    "Dispatch a potentially blocking action to an agent. Returns the
    agent immediately. Subsequently, in a separate thread, the state of
    the agent will be set to the value of:

    (apply action-fn state-of-agent args)"
    {:added "1.0"
     :static true}
    [^clojure.lang.Agent a f & args]
    (apply send-via clojure.lang.Agent/soloExecutor a f args))

(defn release-pending-sends
    "Normally, actions sent directly or indirectly during another action
    are held until the action completes (changes the agent's
    state). This function can be used to dispatch any pending sent
    actions immediately. This has no impact on actions sent during a
    transaction, which are still held until commit. If no action is
    occurring, does nothing. Returns the number of actions dispatched."
    {:added "1.0"
     :static true}
    [] (clojure.lang.Agent/releasePendingSends))

(defn add-watch
    "Adds a watch function to an agent/atom/var/ref reference. The watch
    fn must be a fn of 4 args: a key, the reference, its old-state, its
    new-state. Whenever the reference's state might have been changed,
    any registered watches will have their functions called. The watch fn
    will be called synchronously, on the agent's thread if an agent,
    before any pending sends if agent or ref. Note that an atom's or
    ref's state may have changed again prior to the fn call, so use
    old/new-state rather than derefing the reference. Note also that watch
    fns may be called from multiple threads simultaneously. Var watchers
    are triggered only by root binding changes, not thread-local
    set!s. Keys must be unique per reference, and can be used to remove
    the watch with remove-watch, but are otherwise considered opaque by
    the watch mechanism."
    {:added "1.0"
     :static true}
    [^clojure.lang.IRef reference key fn] (.addWatch reference key fn))

(defn remove-watch
    "Removes a watch (set by add-watch) from a reference"
    {:added "1.0"
     :static true}
    [^clojure.lang.IRef reference key]
    (.removeWatch reference key))



(defn restart-agent
    "When an agent is failed, changes the agent state to new-state and
    then un-fails the agent so that sends are allowed again.  If
    a :clear-actions true option is given, any actions queued on the
    agent that were being held while it was failed will be discarded,
    otherwise those held actions will proceed.  The new-state must pass
    the validator if any, or restart will throw an exception and the
    agent will remain failed with its old state and error.  Watchers, if
    any, will NOT be notified of the new state.  Throws an exception if
    the agent is not failed."
    {:added "1.2"
     :static true
     }
    [^clojure.lang.Agent a, new-state & options]
    (let [opts (apply hash-map options)]
      (.restart a new-state (if (:clear-actions opts) true false))))

(defn set-error-handler!
    "Sets the error-handler of agent a to handler-fn.  If an action
    being run by the agent throws an exception or doesn't pass the
    validator fn, handler-fn will be called with two arguments: the
    agent and the exception."
    {:added "1.2"
     :static true}
    [^clojure.lang.Agent a, handler-fn]
    (.setErrorHandler a handler-fn))

(defn error-handler
    "Returns the error-handler of agent a, or nil if there is none.
    See set-error-handler!"
    {:added "1.2"
     :static true}
    [^clojure.lang.Agent a]
    (.getErrorHandler a))

(defn set-error-mode!
    "Sets the error-mode of agent a to mode-keyword, which must be
    either :fail or :continue.  If an action being run by the agent
    throws an exception or doesn't pass the validator fn, an
    error-handler may be called (see set-error-handler!), after which,
    if the mode is :continue, the agent will continue as if neither the
    action that caused the error nor the error itself ever happened.

    If the mode is :fail, the agent will become failed and will stop
    accepting new 'send' and 'send-off' actions, and any previously
    queued actions will be held until a 'restart-agent'.  Deref will
    still work, returning the state of the agent before the error."
    {:added "1.2"
     :static true}
    [^clojure.lang.Agent a, mode-keyword]
    (.setErrorMode a mode-keyword))

(defn error-mode
    "Returns the error-mode of agent a.  See set-error-mode!"
    {:added "1.2"
     :static true}
    [^clojure.lang.Agent a]
    (.getErrorMode a))

(defn agent-errors
    "DEPRECATED: Use 'agent-error' instead.
    Returns a sequence of the exceptions thrown during asynchronous
    actions of the agent."
    {:added "1.0"
     :deprecated "1.2"}
    [a]
    (when-let [e (agent-error a)]
      (list e)))

(defn clear-agent-errors
    "DEPRECATED: Use 'restart-agent' instead.
    Clears any exceptions thrown during asynchronous actions of the
    agent, allowing subsequent actions to occur."
    {:added "1.0"
     :deprecated "1.2"}
    [^clojure.lang.Agent a] (restart-agent a (.deref a)))

(defn shutdown-agents
    "Initiates a shutdown of the thread pools that back the agent
    system. Running actions will complete, but no new actions will be
    accepted"
    {:added "1.0"
     :static true}
    [] (. clojure.lang.Agent shutdown))


(defn atom
    "Creates and returns an Atom with an initial value of x and zero or
    more options (in any order):

    :meta metadata-map

    :validator validate-fn

    If metadata-map is supplied, it will become the metadata on the
    atom. validate-fn must be nil or a side-effect-free fn of one
    argument, which will be passed the intended new state on any state
    change. If the new state is unacceptable, the validate-fn should
    return false or throw an exception."
    {:added "1.0"
     :static true}
    ([x] (new clojure.lang.Atom x))
    ([x & options] (setup-reference (atom x) options)))

(defn swap!
    "Atomically swaps the value of atom to be:
    (apply f current-value-of-atom args). Note that f may be called
    multiple times, and thus should be free of side effects.  Returns
    the value that was swapped in."
    {:added "1.0"
     :static true}
    ([^clojure.lang.Atom atom f] (.swap atom f))
    ([^clojure.lang.Atom atom f x] (.swap atom f x))
    ([^clojure.lang.Atom atom f x y] (.swap atom f x y))
    ([^clojure.lang.Atom atom f x y & args] (.swap atom f x y args)))

(defn compare-and-set!
    "Atomically sets the value of atom to newval if and only if the
    current value of the atom is identical to oldval. Returns true if
    set happened, else false"
    {:added "1.0"
     :static true}
    [^clojure.lang.Atom atom oldval newval] (.compareAndSet atom oldval newval))

(defn reset!
    "Sets the value of atom to newval without regard for the
    current value. Returns newval."
    {:added "1.0"
     :static true}
    [^clojure.lang.Atom atom newval] (.reset atom newval))

(defn set-validator!
    "Sets the validator-fn for a var/ref/agent/atom. validator-fn must be nil or a
    side-effect-free fn of one argument, which will be passed the intended
    new state on any state change. If the new state is unacceptable, the
    validator-fn should return false or throw an exception. If the current state (root
    value if var) is not acceptable to the new validator, an exception
    will be thrown and the validator will not be changed."
    {:added "1.0"
     :static true}
    [^clojure.lang.IRef iref validator-fn] (. iref (setValidator validator-fn)))

(defn get-validator
    "Gets the validator-fn for a var/ref/agent/atom."
    {:added "1.0"
     :static true}
    [^clojure.lang.IRef iref] (. iref (getValidator)))

(defn reset-meta!
    "Atomically resets the metadata for a namespace/var/ref/agent/atom"
    {:added "1.0"
     :static true}
    [^clojure.lang.IReference iref metadata-map] (.resetMeta iref metadata-map))

(defn alter
    "Must be called in a transaction. Sets the in-transaction-value of
    ref to:

    (apply fun in-transaction-value-of-ref args)

    and returns the in-transaction-value of ref."
    {:added "1.0"
     :static true}
    [^clojure.lang.Ref ref fun & args]
    (. ref (alter fun args)))

(defn ref-set
    "Must be called in a transaction. Sets the value of ref.
    Returns val."
    {:added "1.0"
     :static true}
    [^clojure.lang.Ref ref val]
    (. ref (set val)))

(defn ref-history-count
    "Returns the history count of a ref"
    {:added "1.1"
     :static true}
    [^clojure.lang.Ref ref]
    (.getHistoryCount ref))

(defn ref-min-history
    "Gets the min-history of a ref, or sets it and returns the ref"
    {:added "1.1"
     :static true}
    ([^clojure.lang.Ref ref]
      (.getMinHistory ref))
    ([^clojure.lang.Ref ref n]
      (.setMinHistory ref n)))

(defn ref-max-history
    "Gets the max-history of a ref, or sets it and returns the ref"
    {:added "1.1"
     :static true}
    ([^clojure.lang.Ref ref]
      (.getMaxHistory ref))
    ([^clojure.lang.Ref ref n]
      (.setMaxHistory ref n)))

(defn ensure
    "Must be called in a transaction. Protects the ref from modification
    by other transactions.  Returns the in-transaction-value of
    ref. Allows for more concurrency than (ref-set ref @ref)"
    {:added "1.0"
     :static true}
    [^clojure.lang.Ref ref]
    (. ref (touch))
    (. ref (deref)))

(defmacro io!
    "If an io! block occurs in a transaction, throws an
    IllegalStateException, else runs body in an implicit do. If the
    first expression in body is a literal string, will use that as the
    exception message."
    {:added "1.0"}
    [& body]
    (let [message (when (string? (first body)) (first body))
          body (if message (next body) body)]
      `(if (clojure.lang.LockingTransaction/isRunning)
         (throw (new IllegalStateException ~(or message "I/O in transaction")))
         (do ~@body))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; fn stuff ;;;;;;;;;;;;;;;;

(defn juxt
    "Takes a set of functions and returns a fn that is the juxtaposition
    of those fns.  The returned fn takes a variable number of args, and
    returns a vector containing the result of applying each fn to the
    args (left-to-right).
    ((juxt a b c) x) => [(a x) (b x) (c x)]"
    {:added "1.1"
     :static true}
    ([f]
      (fn
        ([] [(f)])
        ([x] [(f x)])
        ([x y] [(f x y)])
        ([x y z] [(f x y z)])
        ([x y z & args] [(apply f x y z args)])))
    ([f g]
      (fn
        ([] [(f) (g)])
        ([x] [(f x) (g x)])
        ([x y] [(f x y) (g x y)])
        ([x y z] [(f x y z) (g x y z)])
        ([x y z & args] [(apply f x y z args) (apply g x y z args)])))
    ([f g h]
      (fn
        ([] [(f) (g) (h)])
        ([x] [(f x) (g x) (h x)])
        ([x y] [(f x y) (g x y) (h x y)])
        ([x y z] [(f x y z) (g x y z) (h x y z)])
        ([x y z & args] [(apply f x y z args) (apply g x y z args) (apply h x y z args)])))
    ([f g h & fs]
      (let [fs (list* f g h fs)]
        (fn
          ([] (reduce1 #(conj %1 (%2)) [] fs))
          ([x] (reduce1 #(conj %1 (%2 x)) [] fs))
          ([x y] (reduce1 #(conj %1 (%2 x y)) [] fs))
          ([x y z] (reduce1 #(conj %1 (%2 x y z)) [] fs))
          ([x y z & args] (reduce1 #(conj %1 (apply %2 x y z args)) [] fs))))))

;;;;;;;;;;;;;;;;;;; sequence fns  ;;;;;;;;;;;;;;;;;;;;;;;
(defn sequence
    "Coerces coll to a (possibly empty) sequence, if it is not already
    one. Will not force a lazy seq. (sequence nil) yields ()"
    {:added "1.0"
     :static true}
    [coll]
    (if (seq? coll) coll
      (or (seq coll) ())))

(def
    ^{:tag Boolean
      :doc "Returns false if (pred x) is logical true for every x in
  coll, else true."
      :arglists '([pred coll])
      :added "1.0"}
    not-every? (comp not every?))

(defn drop-last
    "Return a lazy sequence of all but the last n (default 1) items in coll"
    {:added "1.0"
     :static true}
    ([s] (drop-last 1 s))
    ([n s] (map (fn [x _] x) s (drop n s))))

(defn take-last
    "Returns a seq of the last n items in coll.  Depending on the type
    of coll may be no better than linear time.  For vectors, see also subvec."
    {:added "1.1"
     :static true}
    [n coll]
    (loop [s (seq coll), lead (seq (drop n coll))]
      (if lead
        (recur (next s) (next lead))
        s)))

(defn cycle
    "Returns a lazy (infinite!) sequence of repetitions of the items in coll."
    {:added "1.0"
     :static true}
    [coll] (lazy-seq
             (when-let [s (seq coll)]
               (concat s (cycle s)))))

(defn split-at
    "Returns a vector of [(take n coll) (drop n coll)]"
    {:added "1.0"
     :static true}
    [n coll]
    [(take n coll) (drop n coll)])

(defn split-with
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

(defn merge-with
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

(defn zipmap
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

(defn line-seq
    "Returns the lines of text from rdr as a lazy sequence of strings.
    rdr must implement java.io.BufferedReader."
    {:added "1.0"
     :static true}
    [^java.io.BufferedReader rdr]
    (when-let [line (.readLine rdr)]
      (cons line (lazy-seq (line-seq rdr)))))

(defn comparator
    "Returns an implementation of java.util.Comparator based upon pred."
    {:added "1.0"
     :static true}
    [pred]
    (fn [x y]
      (cond (pred x y) -1 (pred y x) 1 :else 0)))

(defn await
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

(defn ^:static await1 [^clojure.lang.Agent a]
    (when (pos? (.getQueueCount a))
      (await a))
    a)

(defn await-for
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

(defn read-line
    "Reads the next line from stream that is the current value of *in* ."
    {:added "1.0"
     :static true}
    []
    (if (instance? clojure.lang.LineNumberingPushbackReader *in*)
      (.readLine ^clojure.lang.LineNumberingPushbackReader *in*)
      (.readLine ^java.io.BufferedReader *in*)))

(defn read-string
    "Reads one object from the string s.

    Note that read-string can execute code (controlled by *read-eval*),
    and as such should be used only with trusted sources.

    For data structure interop use clojure.edn/read-string"
    {:added "1.0"
     :static true}
    [s] (clojure.lang.RT/readString s))

(defmacro memfn
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

(defmacro time
    "Evaluates expr and prints the time it took.  Returns the value of
   expr."
    {:added "1.0"}
    [expr]
    `(let [start# (. System (nanoTime))
           ret# ~expr]
       (prn (str "Elapsed time: " (/ (double (- (. System (nanoTime)) start#)) 1000000.0) " msecs"))
       ret#))


(defn aclone
    "Returns a clone of the Java array. Works on arrays of known
    types."
    {:inline (fn [a] `(. clojure.lang.RT (aclone ~a)))
     :added "1.0"}
    [array] (. clojure.lang.RT (aclone array)))

(defn aset
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

(def-aset
    ^{:doc "Sets the value at the index/indices. Works on arrays of long. Returns val."
      :added "1.0"}
    aset-long setLong long)

(def-aset
    ^{:doc "Sets the value at the index/indices. Works on arrays of boolean. Returns val."
      :added "1.0"}
    aset-boolean setBoolean boolean)

(def-aset
    ^{:doc "Sets the value at the index/indices. Works on arrays of float. Returns val."
      :added "1.0"}
    aset-float setFloat float)

(def-aset
    ^{:doc "Sets the value at the index/indices. Works on arrays of double. Returns val."
      :added "1.0"}
    aset-double setDouble double)

(def-aset
    ^{:doc "Sets the value at the index/indices. Works on arrays of short. Returns val."
      :added "1.0"}
    aset-short setShort short)

(def-aset
    ^{:doc "Sets the value at the index/indices. Works on arrays of byte. Returns val."
      :added "1.0"}
    aset-byte setByte byte)

(def-aset
    ^{:doc "Sets the value at the index/indices. Works on arrays of char. Returns val."
      :added "1.0"}
    aset-char setChar char)

(defn to-array-2d
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

(defn create-struct
    "Returns a structure basis object."
    {:added "1.0"
     :static true}
    [& keys]
    (. clojure.lang.PersistentStructMap (createSlotMap keys)))

(defmacro defstruct
    "Same as (def name (create-struct keys...))"
    {:added "1.0"
     :static true}
    [name & keys]
    `(def ~name (create-struct ~@keys)))

(defn struct-map
    "Returns a new structmap instance with the keys of the
    structure-basis. keyvals may contain all, some or none of the basis
    keys - where values are not supplied they will default to nil.
    keyvals can also contain keys not in the basis."
    {:added "1.0"
     :static true}
    [s & inits]
    (. clojure.lang.PersistentStructMap (create s inits)))

(defn struct
    "Returns a new structmap instance with the keys of the
    structure-basis. vals must be supplied for basis keys in order -
    where values are not supplied they will default to nil."
    {:added "1.0"
     :static true}
    [s & vals]
    (. clojure.lang.PersistentStructMap (construct s vals)))

(defn accessor
    "Returns a fn that, given an instance of a structmap with the basis,
    returns the value at the key.  The key must be in the basis. The
    returned function should be (slightly) more efficient than using
    get, but such use of accessors should be limited to known
    performance-critical areas."
    {:added "1.0"
     :static true}
    [s key]
    (. clojure.lang.PersistentStructMap (getAccessor s key)))

(defn load-string
    "Sequentially read and evaluate the set of forms contained in the
    string"
    {:added "1.0"
     :static true}
    [s]
    (let [rdr (-> (java.io.StringReader. s)
                (clojure.lang.LineNumberingPushbackReader.))]
      (load-reader rdr)))

(defn ns-refers
    "Returns a map of the refer mappings for the namespace."
    {:added "1.0"
     :static true}
    [ns]
    (let [ns (the-ns ns)]
      (filter-key val (fn [^clojure.lang.Var v] (and (instance? clojure.lang.Var v)
                                                  (not= ns (.ns v))))
        (ns-map ns))))

(defn ns-aliases
    "Returns a map of the aliases for the namespace."
    {:added "1.0"
     :static true}
    [ns]
    (.getAliases (the-ns ns)))

(defn ns-unalias
    "Removes the alias for the symbol from the namespace."
    {:added "1.0"
     :static true}
    [ns sym]
    (.removeAlias (the-ns ns) sym))

(defn var-get
    "Gets the value in the var object"
    {:added "1.0"
     :static true}
    [^clojure.lang.Var x] (. x (get)))

(defn var-set
    "Sets the value in the var object to val. The var must be
   thread-locally bound."
    {:added "1.0"
     :static true}
    [^clojure.lang.Var x val] (. x (set val)))

(defmacro with-local-vars
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

(defn array-map
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

(defn seque
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



(defn empty?
    "Returns true if coll has no items - same as (not (seq coll)).
    Please use the idiom (seq x) rather than (not (empty? x))"
    {:added "1.0"
     :static true}
    [coll] (not (seq coll)))

(defn coll?
    "Returns true if x implements IPersistentCollection"
    {:added "1.0"
     :static true}
    [x] (instance? clojure.lang.IPersistentCollection x))

(defn list?
    "Returns true if x implements IPersistentList"
    {:added "1.0"
     :static true}
    [x] (instance? clojure.lang.IPersistentList x))

(defn set?
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

(defn fn?
    "Returns true if x implements Fn, i.e. is an object created via fn."
    {:added "1.0"
     :static true}
    [x] (instance? clojure.lang.Fn x))


(defn associative?
    "Returns true if coll implements Associative"
    {:added "1.0"
     :static true}
    [coll] (instance? clojure.lang.Associative coll))

(defn sequential?
    "Returns true if coll implements Sequential"
    {:added "1.0"
     :static true}
    [coll] (instance? clojure.lang.Sequential coll))

(defn sorted?
    "Returns true if coll implements Sorted"
    {:added "1.0"
     :static true}
    [coll] (instance? clojure.lang.Sorted coll))

(defn counted?
    "Returns true if coll implements count in constant time"
    {:added "1.0"
     :static true}
    [coll] (instance? clojure.lang.Counted coll))

(defn reversible?
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
