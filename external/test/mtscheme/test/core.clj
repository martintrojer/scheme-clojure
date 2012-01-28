(ns mtscheme.test.core
  (:use [mtscheme.parser])
  (:use [mtscheme.interpreter])
  (:use [clojure.test]))

(defn test-eq-env [s r e]
  (is (= (get-evval (parse s) e) r)))

(defn test-eq [s r]
  (test-eq-env s r global-env))

(defn get-env-env [s e]
  (let [[_ env] (_eval (parse s) e)]
    env))

(defn get-env [s]
  (get-env-env s global-env))

(deftest number
  (test-eq "3.14" 3.14))
(deftest string
  (test-eq "\"kalle\"" "kalle"))
(deftest var
  (test-eq-env "kalle" 1.0 [{:kalle 1}]))

(deftest add
  (test-eq "(+ 1 2)" (+ 1 2))
  (test-eq "(+ 1 (+ 2 3))" (+ 1 (+ 2 3)))
  (test-eq "(+ 1)" 1)
  (test-eq "(+ 1 1 1)" (+ 1 1 1))
  (test-eq-env "(+ 1 kalle)" 2.0 (conj global-env {:kalle 1})))

(deftest sub
  (test-eq "(- 1 2)" (- 1 2))
  (test-eq "(- 1 (- 2 3))" (- 1 (- 2 3)))
;  (test-eq "(- 1)" (- 1))
  (test-eq "(- 1 1 1)" (- 1 1 1)))

(deftest mul
  (test-eq "(* 2 3.14)" (* 2.0 3.14))
  (test-eq "(+ 1 (* 2 3))" (+ 1 (* 2 3)))
  (test-eq "(* 1)" (* 1))
  (test-eq "(* 2 1 2 2)" (* 2 1 2 2)))

(deftest div
  (test-eq "(/ 9 3)" (/ 9.0 3.0))
  (test-eq "(+ 1 (/ 2 3))" (+ 1.0 (/ 2.0 3.0)))
  (test-eq "(/ 1)" (/ 1.0))
;  (test-eq "(/ 2)" (/ 2.0))
  (test-eq "(/ 1 2 3)" (/ 1.0 2.0 3.0))
  )

(deftest eq
  (test-eq "(= 2 2)" (= 2 2))
  (test-eq "(= 2 (+ 1 1))" (= 2 (+ 1 1)))
;  (test-eq "(= 1)" (= 1))
  (test-eq "(= 1 1 (+ 1 1) 1)" (= 1 1 (+ 1 1) 1)))

(deftest gt
  (test-eq "(> 2 2)" (> 2 2))
  (test-eq "(> 1 2)" (> 1 2))
  (test-eq "(> 2 1)" (> 2 1))
  (test-eq "(> (+ 1 1 1) 2)" (> (+ 1 1 1) 2))
;  (test-eq "(> 1)" (> 1))
;  (test-eq "(> 1 1 (+ 1 1) 1)" (> 1 1 (+ 1 1) 1))
  )

(deftest ge
  (test-eq "(>= 2 2)" (>= 2 2))
  (test-eq "(>= 1 2)" (>= 1 2))
  (test-eq "(>= 2 1)" (>= 2 1))
  (test-eq "(>= (+ 1 1 1) 2)" (>= (+ 1 1 1) 2)))

(deftest lt
  (test-eq "(< 2 2)" (< 2 2))
  (test-eq "(< 1 2)" (< 1 2))
  (test-eq "(< 2 1)" (< 2 1))
  (test-eq "(< (+ 1 1 1) 2)" (< (+ 1 1 1) 2)))

(deftest le
  (test-eq "(<= 2 2)" (<= 2 2))
  (test-eq "(<= 1 2)" (<= 1 2))
  (test-eq "(<= 2 1)" (<= 2 1))
  (test-eq "(<= (+ 1 1 1) 2)" (<= (+ 1 1 1) 2)))

(deftest not-
  (test-eq "(not (= 1 1))" false)
  (test-eq "(not (not (= 1 1)))" true))

(deftest define
  (test-eq-env "lisa" 4 (get-env "(define lisa 4)"))
  (test-eq-env "nisse" 3 (get-env "(define nisse (+ 1 1 1))")))

(deftest if-
  (test-eq "(if (< 2 1) 10 11)" 11)
  (test-eq "(if (< (+ 1 1 1) 1) 11 (* 2 5))" 10)
  (test-eq "(if true 1 2)" 1)
  (test-eq "(if false 1 2)" 2)
  (test-eq "(if false 1)" nil))

(deftest cond-
  (test-eq "(cond (true 1) ((= 1 2) 2))" 1)
  (test-eq "(cond ((= 1 2) 1) (true 2))" 2)
  (test-eq "(cond (false 1) (false 2) (else 3))" 3)
  (test-eq "(cond (false 1) (false 2))" nil))

(deftest cons-
  (test-eq "(cons 1 2)" (list 1 2))
  (test-eq "(cons 1 (cons 2 3))" (list 1 2 3))
  (test-eq "(cons 1 (cons 2 (cons 3 4)))" (list 1 2 3 4))
  (test-eq "(cons (cons 1 2) 3)" (list (list 1 2) 3)))

(deftest list-
  (test-eq "(list 1)" (list 1))
  (test-eq "(list 1 2)" (list 1 2))
  (test-eq "(list 1 (list 2 3) 4)" (list 1 (list 2 3) 4))
  (test-eq "(list 1 \"kalle\")" (list 1 "kalle"))
;  (test-eq "(list)" (list))
  )

(deftest append-
  (test-eq "(append (list 1 2))" (list 1 2))
  (test-eq "(append (list 1 2) (list 3 4))" (list 1 2 3 4))
  (test-eq "(append (list 1) (list 2 (list 3)))" (list 1 2 (list 3))))

(deftest car-
  (test-eq "(car (list 1 2))" (first (list 1 2)))
  (test-eq "(car (list (list 1) 2))" (first (list (list 1) 2))))

(deftest cdr-
  (test-eq "(cdr (list 1))" (rest (list 1)))
  (test-eq "(cdr (list 1 2))" (rest (list 1 2)))
  (test-eq "(cdr (list 1 (list 2 3)))" (rest (list 1 (list 2 3))))
  (test-eq "(cdr (list (list 1)))" (rest (list (list 1)))))

(deftest null?
  (test-eq "(null? (list 1))" (empty? (list 1)))
  (test-eq "(null? (cdr (list 1)))" (empty? (rest (list 1))))
  (test-eq "(null? (cdr (cdr (list 1))))" (empty? (rest (rest (list 1)))))
  (test-eq "(null? (list))" (empty? (list))))

(deftest let-
  (test-eq "(let ((a 1)) a)" 1)
  (test-eq "(let ((a 1)(b (+ 1 1))) (+ a b))" 3))

(deftest begin-
  (test-eq "(begin 1 2)" 2)
  (test-eq "(begin (define x 2) x)" 2))

(deftest function
  (test-eq-env "(add 1 2)" 3 (get-env "(define (add a b) (+ a b))"))
  (test-eq-env "(fact (+ 5 5))" 3628800 (get-env "(define (fact x) (if (= x 0) 1 (* x (fact (- x 1)))))"))
  (test-eq-env "(add 1 3)" 4 (get-env "(define (add a b) (begin (define (worker x y) (+ x y)) (worker a b)))")))

(deftest lambda
  (let [e (get-env "(define (adder val) (lambda (x) (+ x val)))")]
    (test-eq-env "(add4 4)" 8 (get-env-env "(define add4 (adder 4))" e)))
  (test-eq-env
   "(map (lambda (x) (* x x)) (list 1 2 3))" (list 1 4 9)
   (get-env "(define (map f l) (if (not (null? l)) (cons (f (car l)) (map f (cdr l)))))")))

(comment 
  (let [[_ nenv] (_eval (parse "(define (foreach f l) (if (not (null? l)) (begin (f (car l)) (foreach f (cdr l)))))") env)]
    (_eval (parse "(foreach display (list 1 2 3))") nenv))
)

