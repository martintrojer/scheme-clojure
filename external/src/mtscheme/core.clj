(ns mtscheme.core
  (:use [mtscheme.parser])
  (:use [mtscheme.interpreter])
  (:gen-class))

(defn- repl [[res env]]
  (println res)
  (print "=> ")
  (flush)
  (if-let [l (read-line)]
    (recur (_eval (parse l) env))))

(defn -main [& args]
  (repl (_eval (parse "(display \"mtscheme 0.1\")") global-env)))