(ns mtscheme.parser)

;; tokenizer

(defn- tokenize
  "Extracts a tagged-list of tokens from a string"
  [s]

  (let [string (fn [acc s]
                 (let [[fst snd & rst] s
                       r (apply str rst)]
                   (cond
                    (= fst \") [acc (str snd r)]
                    (not (nil? fst)) (recur (str acc fst) (str snd r))
                    :else (throw (Exception. "malformed string")))))        
        
        token (fn [acc s]
                (let [[fst & rst] s
                      r (apply str rst)]
                  (cond
                   (nil? fst) [acc ""]
                   (= fst \)) [acc s]     
                   (Character/isWhitespace fst) [acc r]
                   :else (recur (str acc fst) r))))
        
        do-tokenize (fn [acc s]
                      (let [[fst snd & rst] s
                            r (apply str rst)
                            r2 (str snd r)]
                        (cond
                         (nil? fst) acc
                         (Character/isWhitespace fst) (recur acc r2)
                         (= fst \() (recur (conj acc [:open]) r2)
                         (= fst \)) (recur (conj acc [:close]) r2)
                         (= fst \") (let [[s t] (string "" r2)]
                                      (recur (conj acc [:string s]) t))
                         (and (= fst \-) (Character/isDigit snd)) (let [[n t] (token (str \- snd) r)]
                                                                    (recur (conj acc [:number n]) t))
                         (and (= fst \+) (Character/isDigit snd)) (let [[n t] (token (str \+ snd) r)]
                                                                    (recur (conj acc [:number n]) t))
                         :else (let [[s t] (token fst r2)]
                                 (recur (conj acc [:symbol s]) t)))))]
    (do-tokenize [] s)))

;(tokenize "(Kalle)")
;(tokenize "(\"Kalle\")")
;(tokenize "-12")
;(tokenize "+12")
;(tokenize "12")
;(tokenize "(+ 1 1)")

;; parser

(defn- parse-all
  "Parse a string into a list of expressions"
  [s]

  (let [map-token (fn [[tok val]]
                    (cond
                     (= tok :number) (Double/parseDouble val)
                     (= tok :string) val
                     (= tok :symbol) (try
                                       (Double/parseDouble (str val))
                                       (catch Exception e (keyword (str val))))
                     :else (throw (Exception. "syntax error"))))
        
        do-parse (fn do-parse [acc toks]
                   (let [[fst & rst] toks
                         [tok val] fst]
                     (cond
                      (nil? tok) acc
                      (= tok :open) (let [[e t] (do-parse [] rst)]
                                      (recur (conj acc e) t))
                      (= tok :close) [acc rst]
                      :else (recur (conj acc (map-token fst)) rst))))]    

    (let [toks (tokenize s)]
      (do-parse [] toks))))

(defn parse
  "Parses a string into a list of expressions, returns the first combination"
  [s]
;  (println "---")
  (first (parse-all s)))

;(parse-all "kalle olle")
;(parse "\"kalle\"")
;(parse "12")
;(parse-all "(kalle +12 24) (olle -12)")
;(parse "(+ 1 1)")

