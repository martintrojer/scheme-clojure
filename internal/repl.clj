(ns mtscheme-repl
  (:refer-clojure :exclude [cond cons let])
  (:use mtscheme))

(defn repl [res]
  (println res)
  (print "=> ")
  (flush)
  (clojure.core/let [l (read-line)]
                    (recur (eval (read-string l)))))

(repl "mtscheme 0.1")
