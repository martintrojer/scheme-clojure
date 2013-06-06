(ns mtscheme.tester
  (:refer-clojure :exclude [cond cons let])
  (:require [clojure.test :as ct])
  (:use [mtscheme.core]))

; addition
(+ 1 2)
(+ 1)

; subtraction
(- 1 2)
(- 1)

; multiplication
(* 2 3.14)
(* 1)

; division
(/ 9 3)
(/ 2)

; equality
(= 2 2)
(= 1)
(= 1 (+ 1 1) 1)

; greater and less than
(> 2 2)
(> 2 1)
(> 1)
(< 2 2)
(< 1 2)
(< 1)

; greater or equal and less or equal than
(>= 2 2)
(>= 2 1)
(>= 1)
(<= 2 2)
(<= 1 2)
(<= 1)

; not
(not (= 1 1))
(not (not (= 1 1)))

; define vars
(ct/deftest test-define-vars
  (do
    (define kalle 4)
    (ct/is (= kalle 4)))
  (do
    (define olle (+ 1 1 1))
    (ct/is (= olle (+ 1 1 1)))))

; if
(if (< 2 1) 10 11)
(if (< (+ 1 1 1) 1) 11 (* 2 5))

; cond
(defn test-fn [x]
  (cond ((< x 0) (- 0 x)) ((= x 0) 100) (else x)))

(ct/deftest test-cond
  (ct/is (= (test-fn -1) 1))
  (ct/is (= (test-fn 0) 100))
  (ct/is (= (test-fn 1) 1)))

; cons
(ct/deftest test-cons
  (ct/is (= (cons 1 2) '(1 2)))
  (ct/is (= (cons 1 (cons 2 (cons 3 4))) '(1 2 3 4)))
  (ct/is (= (cons 1 (cons 2 3)) '(1 2 3)))
  (ct/is (= (cons (cons 1 2) (cons 3 4)) '((1 2) 3 4)))
  (ct/is (= (cons (- 2 1) (cons 2 (+ 1 1 1))) '(1 2 3)))
  (ct/is (= (cons "kalle" 2) '("kalle" 2))))

(macroexpand '(cons (- 2 1) 1))
(cons (- 2 1) 1)

(macroexpand-1 '(cons 1 (cons 2 (cons 3 4))))
(cons 1 (cons 2 (cons 3 4)))

(macroexpand '(cons (- 2 1) (cons 2 (+ 1 1 1))))
(cons (- 2 1) (cons 2 (+ 1 1 1)))

; list
(list 1 2)
(list 5 (list 1 1) 2)
(list 1 "kalle")

; append
(append (list 1) (list 2))
(append '(1 2) '(3 4))
(append '(1) (list 2 '(3)))

; car / cdr
(car (list 1 2))
(car (list (list 1) 2))
(cdr (list 1 2))
(cdr (list 1 2 3))
(cdr ())

; null?
(null? (list 1))
(null? (cdr (list 1)))
(null? (cdr (list 1 2)))
(null? (cdr (cdr (list 1 2))))

; define functions
(ct/deftest test-define-funcs
  (do
    (define (factorial x) (if (= x 0) 1 (* x (factorial (- x 1)))))
    (ct/is (= (factorial (+ 5 5)) 3628800))))

; let
(ct/deftest test-let
  (do
    (ct/is (= (let ((a 1)) a) 1))
    (ct/is (= (let ((a (+ 1 1))(b 3)) (+ a b)) 5))))

; begin

; mutable container
(defn container[]
  (clojure.core/let [col (java.util.Vector.)]
    (fn [v]
      (.add col v)
      (.toString col))))

(ct/deftest test-begin
  (do
    (define (foreach f l) (if (not (null? l)) (begin (f (car l)) (foreach f (cdr l)))))
    (def vs (container))
    (foreach vs [1 2 3])
    (ct/is (= (vs 0) "[1, 2, 3, 0]"))))

; lambda
(ct/deftest test-lambda
  (do
    (define (adder val) (lambda (x) (+ x val)))
    (define add4 (adder 4))
    (ct/is (= 8 (add4 4)))))

; ----

(ct/run-tests)
