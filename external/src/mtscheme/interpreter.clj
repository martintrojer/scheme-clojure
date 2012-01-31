(ns mtscheme.interpreter
  (:use [mtscheme.parser]))

; -------------------------------------------------------
; util functitons

(def ^{:dynamic true
       :doc "Enable debug tracing"}
  *debug* (atom false))

; (swap! *debug* (fn [st] (not st)))

(defmacro dprn
  "Print string if in debug mode"
  [& s]
  `(when @*debug*
    (println ~@s)))
; (macroexpand '( dprn "kalle" 1 2))

(defn- lookup
  "Lookup a key in a stack of environment maps"
  [key env]
  (let [do-lookup (fn [[fst & rst]]    
                    (if (contains? fst key) ; can't use if-let because lookup value can be false!
                      (key fst)
                      (when-not (nil? rst)
                        (recur rst))))]    
    (dprn "lookup" key)
    (let [r (do-lookup env)]
      (if-not (nil? r)           
        r
        (throw (Exception. (format "unbond symbol %s" key)))))))
; (lookup :c [{:a 1 :b 2} {:c 3}])

(declare _eval)

(defmacro get-evval
  "Get the result (and skip the env) of a eval-ed expression"
  [exp env]
  `(first (_eval ~exp ~env)))
; (macroexpand '(get-evval [:list 1 2] {}))

(defn- core-fn
  "Return a 'core' function based on operator f (such as +/-/> etc"
  [f name]
  (fn [ps env]
    (dprn name ps)
    (let [vs (map #(let [[r, _] (_eval % env)] r) ps)]
      [(reduce f vs) env])))

; -------------------------------------------------------
; eval / apply

(declare _apply)

(defn _eval
  "Evaluate an expression with in a given envrionemnt and return the the result and a new environment"
  [exp env]
  (dprn "eval" exp)

  (cond
   ; self-evaluating?
   (or (number? exp) (string? exp) (fn? exp)) [exp env]

   ; var reference to be looked up in env
   (keyword? exp) [(lookup exp env) env]
   
   ; parsed combinations (function calls)
   (vector? exp) (let [[fst & rst] exp
                       [r e] (_eval fst env)]
                   (dprn "comb" fst rst "(" r e ")") 
                   (cond
                    ; built-in function calls
                    (fn? r) (_apply r rst e)
                    ; user defined function/lambda calls
                    (list? r) (let [[args body] r
                                    n (zipmap args (map #(get-evval % e) rst))
                                    new-env (cons n e)]
                                (_eval body new-env))  ; eval the first form only
                    :else [exp env]))

   :else (throw (Exception. (format "invalid interpreter state %s %s" (str exp) (str env))))))

(defn _apply
  "Applies a fn with given args and environment"
  [f args env]
  (dprn "apply" f args)
  (f args env))

; -------------------------------------------------------
; core functions

(defn- _not [[fst & rst] env]
  (dprn "not" fst rst)
  [(not (get-evval fst env)) env])

(defn- _if [[cond pos neg] env]
  (let [r (get-evval cond env)]
    (if r
      (_eval pos env)
      (if-not (nil? neg)             ; handle the case when there is no not case        
        (_eval neg env)
        [nil env]))))

(defn- _cond [exps env]
  (let [do-exp (fn [[cond pos]]
                 (dprn "cond-do-exp" cond pos)
                 (if (= cond :else)   ; the special "else" case
                   [true, (_eval pos env)]
                   (let [[r, _] (_eval cond env)]  
                     (if r
                       [true, (_eval pos env)]
                       [false, nil]))))
        
        run-exps (fn [[fst & rst]]
                   (dprn "cond-run-exprs" fst rst)
                   (if-not (nil? fst)      
                     (let [[status res] (do-exp fst)]
                       (if status
                         res
                         (recur rst)))
                     [nil env]))]

    (dprn "cond" exps)
    (run-exps exps)))

(defn- _cons [[fst snd] env]
  (dprn "cons" fst snd)
  (let [f (get-evval fst env)
        s (get-evval snd env)]
    (if (nil? s)
      [(list f) env]
      (if (seq? s)
        [(cons f s) env]
        [(list f s) env]))))

(defn- _list [exps env]
  (let [do-exps (fn [acc [fst & rst]]
                  (dprn "list-do-exps" acc fst)
                  (if-not (nil? fst)
                    (recur (conj acc (get-evval fst env)) rst)
                    acc))]    
    (dprn "list" exps)
    [(seq (do-exps [] exps)) env]))

(defn- _append [exps env]
  (let [do-exps (fn [acc [fst & rst]]
                  (dprn "append-do-exps" acc fst)
                  (if-not (nil? fst)
                    (recur (concat acc (get-evval fst env)) rst)
                    acc))]
    (dprn "append" exps)
    [(do-exps [] exps) env]))

(defn- _begin [exps env]
  (let [do-exps (fn [[fst & rst] env r]
                  (dprn "begin-do-exps" fst rst env)
                  (if-not (nil? fst)
                    (let [[r e] (_eval fst env)]
                      (recur rst e r))
                    [r env]))]
    (dprn "begin" exps)
    (do-exps exps env nil)))

(defn- _car [[fst] env]
  (dprn "car" fst)
  [(first (get-evval fst env)) env])

(defn- _cdr [[fst] env]
  (dprn "cdr" fst)
  [(rest (get-evval fst env)) env])

(defn- _null [[fst] env]
  (dprn "null?" fst)
  (let [r (get-evval fst env)]
    [(or (nil? r) (empty? r)) env]))

(defn- _let [[binds body] env]
  (let [do-bind (fn [acc [fst & rst]]
                  (dprn "let-do-bind" acc fst rst)
                  (if-not (nil? fst)
                    (let [[name val] fst]
                      (recur (assoc acc name (get-evval val env)) rst))
                    acc))]
    (dprn "let" binds body)
    (let [nenv (cons (do-bind {} binds) env)]
      (dprn "let-body" body nenv)
      [(get-evval body nenv) env])))

(defn- _display [[fst] env]
  (dprn "display" fst)
  (println (get-evval fst env))
  [nil env])

(defn- _newline [[fst] env]
  (dprn "newline" fst)
  (println "")
  [nil env])

(defn- _define [[name exp] env]
  (dprn "define" name exp)
  (if (vector? name)
    ; defining a function
    (let [[f-name & args] name
          nenv (assoc (first env) f-name (list args exp))]
      [nil (cons nenv (rest env))])
    ; defining a var
    (let [nenv (assoc (first env) name (get-evval exp env))]
      [nil (cons nenv (rest env))])))

(defn _lambda [[args exp] env]
  ; optimization - replace stuff in exp not listed in args
  (let [do-exp (fn [acc ps [fst & rst]]
                 (dprn "lambda-do-exp" acc fst)
                 (if-not (nil? fst)
                   (if-not (nil? (get ps fst))
                     (recur (conj acc fst) ps rst)       ; an arg just conj
                     (let [p (lookup fst env)]           ; not a arg, look it up
                       (if-not (nil? p)
                         (recur (conj acc p) ps rst)
                         (throw (Exception. (format "unbound symbol %s" fst))))))
                   acc))]
    (dprn "lambda" args exp env)
    [(list (seq args) (do-exp [] (set args) exp)) env]))

(def global-env [{:+ (core-fn + "add"), :- (core-fn -"sub"), :* (core-fn * "mul")
                  (keyword "/") (core-fn / "div"), := (core-fn = "eq"), :> (core-fn > "gt")
                  :>= (core-fn >= "gte"), :< (core-fn < "lt"), :<= (core-fn <= "lte")
                  :not _not, :define _define, :if _if, :cond _cond, :cons _cons
                  :list _list, :append _append, :car _car, :cdr _cdr, :null? _null
                  :let _let, :begin _begin, :lambda _lambda
                  :display _display, :newline _newline, :true true, :false false}])

