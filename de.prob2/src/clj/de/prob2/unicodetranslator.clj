(ns de.prob2.unicodetranslator
  (:require [clojure.string :as string]))

#_(defmulti lex [s] (first s))

#_(defmethod lex \: [s] (cond
						(starts-with? s ":!") :bcmsuch
            (starts-with? s ":=") :bcmeq
            :else                :in))

(def tokens
[{:token :in          :ascii ":"     :unicode "\u2208"}
{:token :notsubseteq :ascii "/<:"    :unicode "\u2288"}
{:token :notsubset   :ascii "/<<:"   :unicode "\u2284"}
{:token :subseteq    :ascii "<:"     :unicode "\u2286"}
{:token :setminus    :ascii "\\"     :unicode "\u2216"}
{:token :dotdot      :ascii ".."     :unicode "\u2025"}
{:token :nat1        :ascii "NAT1"   :unicode "\u2115\u0031"}
{:token :nat         :ascii "NAT"    :unicode "\u2115"}
{:token :emptyset    :ascii "{}"     :unicode "\u2205"}
{:token :bcmsuch     :ascii ":|"     :unicode "\u2223"}
{:token :bfalse      :ascii "false"  :unicode "\u22a5"}
{:token :forall      :ascii "!"      :unicode "\u2200"}
{:token :exists      :ascii "#"      :unicode "\u2203"}
{:token :mapsto      :ascii "|->"    :unicode "\u21a6"}
{:token :btrue       :ascii "true"   :unicode "\u22a4"}
{:token :subset      :ascii "<<:"    :unicode "\u2282"}
{:token :bunion      :ascii "\\/"    :unicode "\u222a"}
{:token :binter      :ascii "/\\"    :unicode "\u2229"}
{:token :domres      :ascii "<|"     :unicode "\u25c1"}
{:token :ranres      :ascii "|>"     :unicode "\u25b7"}
{:token :domsub      :ascii "<<|"    :unicode "\u2a64"}
{:token :ransub      :ascii "|>>"    :unicode "\u2a65"}
{:token :lambda      :ascii "%"      :unicode "\u03bb"}
{:token :oftype      :ascii "oftype" :unicode "\u2982"}
{:token :notin       :ascii "/:"     :unicode "\u2209"}
{:token :cprod       :ascii "**"     :unicode "\u00d7"}
{:token :union       :ascii "UNION"  :unicode "\u22c3"}
{:token :inter       :ascii "INTER"  :unicode "\u22c2"}
{:token :fcomp       :ascii "\\fcomp" :unicode "\u003b"}
{:token :bcomp       :ascii "circ"   :unicode "\u2218"}
{:token :strel       :ascii "<<->>"  :unicode "\ue102"}
{:token :dprod       :ascii "><"     :unicode "\u2297"}
{:token :pprod       :ascii "||"     :unicode "\u2225"}
{:token :bcmeq       :ascii ":="     :unicode "\u2254"}
{:token :bcmin       :ascii "::"     :unicode "\u2208"}
{:token :intg        :ascii "INT"    :unicode "\u2124"}
{:token :land        :ascii "&"      :unicode "\u2227"}
{:token :limp        :ascii "=>"     :unicode "\u21d2"}
{:token :leqv        :ascii "<=>"    :unicode "\u21d4"}
{:token :lnot        :ascii "not"    :unicode "\u00ac"}
{:token :qdot        :ascii "."      :unicode "\u00b7"}
{:token :conv        :ascii "~"      :unicode "\u223c"}
{:token :trel        :ascii "<<->"   :unicode "\ue100"}
{:token :srel        :ascii "<->>"   :unicode "\ue101"}
{:token :pfun        :ascii "+->"    :unicode "\u21f8"}
{:token :tfun        :ascii "-->"    :unicode "\u2192"}
{:token :pinj        :ascii ">+>"    :unicode "\u2914"}
{:token :tinj        :ascii ">->"    :unicode "\u21a3"}
{:token :psur        :ascii "+>>"    :unicode "\u2900"}
{:token :tsur        :ascii "->>"    :unicode "\u21a0"}
{:token :tbij        :ascii ">->>"   :unicode "\u2916"}
{:token :expn        :ascii "\\expn" :unicode "\u005e"}
{:token :lor         :ascii "or"     :unicode "\u2228"}
{:token :pow1        :ascii "POW1"   :unicode "\u2119\u0031"}
{:token :pow         :ascii "POW"    :unicode "\u2119"}
{:token :mid         :ascii "\\mid"  :unicode "\u2223"}
{:token :neq         :ascii "/="     :unicode "\u2260"}
{:token :rel         :ascii "<->"    :unicode "\u2194"}
{:token :ovl         :ascii "<+"     :unicode "\ue103"}
{:token :leq         :ascii "<="     :unicode "\u2264"}
{:token :geq         :ascii ">="     :unicode "\u2265"}
{:token :div         :ascii "/"      :unicode "\u00f7"}
{:token :mult        :ascii "*"      :unicode "\u2217"}
{:token :minus       :ascii "-"      :unicode "\u2212"}
{:token :take        :ascii "/|\\"   :unicode "/|\\"}
{:token :drop        :ascii "\\|/"   :unicode "\\|/"}])


(defn ascii [s] s)

(defn unicode [s] s)

(defn tokenize [s]
  (let [sp (string/split s #"\s+")]))

(defn starts-with? 
    "tests a sequence and a prefix to see if the sequence starts with the prefix"
    [l prefix]
    (if (> (count prefix) (count l)) 
        false
        (=  (take (count prefix) l) (seq prefix))))

(defn extract-identifier [s])
(def whitespace #{\newline \space \tab \formfeed \backspace \return})

(defn match? 
    "checks to see if the pattern is a prefix to the sequence. However, after the sequence there needs to be either a whitespace or the end"
    [s pattern]
    (let [p      (seq pattern)
          pcount (count p)]
        (when (starts-with? s p) (or (= (count s) pcount) (whitespace (nth s pcount))))))

(defn handle-letters [s pattern token]
    (if (match? s pattern) [token (drop (count pattern) s)] (extract-identifier s)))

(defmulti lex first)
(defmethod lex \! [s] [:forall (rest s)])
(defmethod lex \# [s] [:exists (rest s)])
(defmethod lex \c [s] (handle-letters s "circ" :bcomp))
(defmethod lex \% [s] [:lambda (rest s)])
(defmethod lex \& [s] [:land   (rest s)])
(defmethod lex \f [s] (handle-letters s "false" :false))
(defmethod lex \I [s] (cond
                        (match? s "INTER") [:inter (drop 5 s)] 
                        (match? s "INT")   [:intg  (drop 3 s)]
                        :else                   (extract-identifier s)))
(defmethod lex \* [s] (cond
                        (starts-with? s "**")    [:cprod (drop 2 s)]
                        "*"                     [:mult  (rest s)]))
(defmethod lex \+ [s] (cond 
                        (starts-with? s "+->")   [:pfun  (drop 3 s)]
                        (starts-with? s "+>>")   [:psur  (drop 3 s)]
                        :else                    [\+     (rest s)]))
(defmethod lex \- [s] (cond 
                        (starts-with? s "-->")   [:tfun  (drop 3 s)]
                        (starts-with? s "->>")   [:tsur  (drop 3 s)]
                        "-"                     [:minus (rest s)]))
(defmethod lex \. [s] (cond
                        (starts-with? s "..")    [:dotdot (drop 2 s)]
                        "."                     [:qdot  (rest s)]))
(defmethod lex \N [s] (cond 
                        (match? s "NAT1") [:nat1  (drop 4 s)]
                        (match? s "NAT")  [:nat (drop 3 s)]
                        :else             (extract-identifier s)))
(defmethod lex \n [s] (handle-letters s "not" :lnot))
(defmethod lex \/ [s] (cond
                        (starts-with? s "/<:")   [:notsubseteq  (drop 3 s)]
                        (starts-with? s "/<<:")  [:notsubset  (drop 4 s)]
                        (starts-with? s "/\\")   [:binter  (drop 2 s)]
                        (starts-with? s "/:")    [:notin   (drop 2 s)]
                        (starts-with? s "/=")    [:neq     (drop 2 s)]
                        (starts-with? s "/|\\")  [:take    (drop 3 s)]
                        "/"                      [:div     (rest s)]))
(defmethod lex \o [s] (cond
                        (match? s "oftype")  [:oftype (drop 6 s)]
                        (match? s "or")        [:lor    (drop 2 s)]
                        :else                   (extract-identifier s)))
(defmethod lex \P [s] (cond
                        (match? s "POW1")    [:pow1   (drop 4 s)]
                        (match? s "POW")    [:pow    (drop 3 s)]
                        :else               (extract-identifier s)))
(defmethod lex \t [s] (handle-letters s "true" :btrue))
(defmethod lex \U [s] (handle-letters s "UNION" :union))
(defmethod lex \: [s] (cond
                        (starts-with? s ":|")  [:bcmsuch  (drop 2 s)]
                        (starts-with? s ":=")  [:bcmeq    (drop 2 s)]
                        (starts-with? s "::")  [:bcmin    (drop 2 s)]
                        ":"                    [:in       (rest s)])) 
(defmethod lex \{ [s] (cond
                        (starts-with? s "{}")  [:emptyset (drop 2 s)]
                        "{"                     [\{        (rest s)]))
(defmethod lex \< [s] (cond
                        (starts-with? s "<<->>") [:strel  (drop 5 s)]
                        (starts-with? s "<->>")  [:srel   (drop 4 s)]
                        (starts-with? s "<<->")  [:trel   (drop 4 s)]
                        (starts-with? s "<->")   [:rel    (drop 3 s)]
                        (starts-with? s "<=>")   [:leqv   (drop 3 s)]
                        (starts-with? s "<<|")   [:domsub (drop 3 s)]
                        (starts-with? s "<<:")   [:subset (drop 3 s)]
                        (starts-with? s "<=")    [:leq    (drop 2 s)]
                        (starts-with? s "<+")    [:ovl    (drop 2 s)]
                        (starts-with? s "<|")    [:domres (drop 2 s)]
                        (starts-with? s "<:")    [:subseteq (drop 2 s)]
                        \<                       [\<      (rest s)]))
(defmethod lex \\ [s] (cond
                        (starts-with? s "\\/")      [:bunion   (drop 2 s)]
                        (starts-with? s "\\fcomp")  [:fcomp    (drop 5 s)]
                        (starts-with? s "\\expn")   [:expn     (drop 5 s)]
                        (starts-with? s "\\mid")    [:mid      (drop 4 s)]
                        (starts-with? s "\\|/")     [:drop     (drop 3 s)]
                        \\                          [:setminus (rest s)]))
(defmethod lex \| [s] (cond
                        (starts-with? s "|->")      [:mapsto   (drop 3 s)]
                        (starts-with? s "|>>")      [:ransub   (drop 3 s)]
                        (starts-with? s "||")       [:pprod    (drop 2 s)]
                        (starts-with? s "|>")       [:ranres   (drop 2 s)]
                        \|                          [\|        (rest s)]))
(defmethod lex \= [s] (cond 
                        (starts-with? s "=>")       [:limp  (drop 2 s)]
                        "="                         [\=     (rest s)]))
(defmethod lex \> [s] (cond
                        (starts-with? s "><")       [:dprod (drop 2 s)]
                        (starts-with? s ">+>")      [:pinj  (drop 3 s)]
                        (starts-with? s ">->>")     [:tbij  (drop 4 s)]
                        (starts-with? s ">->")      [:tinj  (drop 3 s)]
                        (starts-with? s ">=")       [:geq   (drop 2 s)]
                        \>                          [\>     (rest s)]))
(defmethod lex \~ [s] [:conv  (rest s)])

(defn tokenize ([s] (tokenize s [])
    ([s tokens] (if (empty? s) (reverse tokens)
                    (let [[token r] (lex s)]
                        (recur r (cons t tokens)))))))