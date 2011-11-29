; $ mkdir classes
; $ export CLASSPATH=$CLASSPATH:./classes:.
; $ clojure -e "(compile 'mtscheme)"

(ns mtscheme
  (:gen-class)
  (:refer-clojure :exclude [cond cons let]))

; cond
(defmacro cond [& args]
  (when args
    (clojure.core/let [fst (ffirst args)]
                      (list `if (if (= fst (symbol "else")) :else fst)
                            (second (first args))
                            (clojure.core/cons 'cond (next args))))))

;(clojure.walk/macroexpand-all
;(macroexpand-1
;  '(cond ((< x 0) (- 0 x)) ((= x 0) 100) (else x)))

;(macroexpand-1
;(clojure.walk/macroexpand-all
;  '(cond ((> 3 2) 'greater)
;         ((< 3 2) 'less)))

(defn cons [fst snd]
  (clojure.core/cons fst (if (coll? snd) snd [snd])))

; append
(def append concat)

; car / cdr
(def car first)
(def cdr rest)

; null?
(def null? empty?)

;(coll? 1)

; define
(defmacro define [name-and-params body]
  (if (coll? name-and-params)             ; function or var definition?
	  (clojure.core/let [aname (first name-and-params) 
                      params (rest name-and-params)]
                     `(defn ~aname [~@params] ~body))
    `(def ~name-and-params ~body)))

;(defmacro define [& args]
;  `(def ~@args))
; (macroexpand '(define lisa 1))
; (macroexpand '(define nisse (+ 1 1 1)))

; let
(defmacro let [& args]
  (clojure.core/let [body (first (rest args))
                     vars (reduce concat [] (first args))]
                    `(clojure.core/let [~@vars] ~body)))

; (macroexpand '(let lisa 1))             ; serror
; (macroexpand '(let lisa 1 (+ lisa 1)))  ; serror
; (macroexpand '(let lisa (+ 1 1)))       ; serror
; (macroexpand '(let (a 1)))              ; serror
; (macroexpand '(let ((a 1))))  
; (macroexpand '(let ((a 1)) a))
; (macroexpand '(let ((a 1)(b 2))(+ a b)))
; (macroexpand '(let (a 1 b 2)))
; (macroexpand (let ((a (+ 1 1))(b 3)) (+ a b)))

; begin

(defmacro begin [& args]
  `(do ~@args))

; lambda 
(defmacro lambda [& args]
  (clojure.core/let [fst (first args)
                     snd (second args)]
                    `(fn [~@fst] ~snd)))
           
; (macroexpand-1 '(lambda (x) (+ x v)))

; display
(def display println)


