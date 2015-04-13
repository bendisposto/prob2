(ns de.prob2.unicodetranslator
  (:require [clojure.string :as string]))

(def token-map
{:in         {:token :in          :ascii ":"      :unicode "\u2208"}
:notsubseteq {:token :notsubseteq :ascii "/<:"    :unicode "\u2288"}
:notsubset   {:token :notsubset   :ascii "/<<:"   :unicode "\u2284"}
:subseteq    {:token :subseteq    :ascii "<:"     :unicode "\u2286"}
:setminus    {:token :setminus    :ascii "\\"     :unicode "\u2216"}
:dotdot      {:token :dotdot      :ascii ".."     :unicode "\u2025"}
:nat1        {:token :nat1        :ascii "NAT1"   :unicode "\u2115\u0031"}
:nat         {:token :nat         :ascii "NAT"    :unicode "\u2115"}
:emptyset    {:token :emptyset    :ascii "{}"     :unicode "\u2205"}
:bcmsuch     {:token :bcmsuch     :ascii ":|"     :unicode ":\u2223"}
:bfalse      {:token :bfalse      :ascii "false"  :unicode "\u22a5"}
:forall      {:token :forall      :ascii "!"      :unicode "\u2200"}
:exists      {:token :exists      :ascii "#"      :unicode "\u2203"}
:mapsto      {:token :mapsto      :ascii "|->"    :unicode "\u21a6"}
:btrue       {:token :btrue       :ascii "true"   :unicode "\u22a4"}
:subset      {:token :subset      :ascii "<<:"    :unicode "\u2282"}
:bunion      {:token :bunion      :ascii "\\/"    :unicode "\u222a"}
:binter      {:token :binter      :ascii "/\\"    :unicode "\u2229"}
:domres      {:token :domres      :ascii "<|"     :unicode "\u25c1"}
:ranres      {:token :ranres      :ascii "|>"     :unicode "\u25b7"}
:domsub      {:token :domsub      :ascii "<<|"    :unicode "\u2a64"}
:ransub      {:token :ransub      :ascii "|>>"    :unicode "\u2a65"}
:lambda      {:token :lambda      :ascii "%"      :unicode "\u03bb"}
:oftype      {:token :oftype      :ascii "oftype" :unicode "\u2982"}
:notin       {:token :notin       :ascii "/:"     :unicode "\u2209"}
:cprod       {:token :cprod       :ascii "**"     :unicode "\u00d7"}
:union       {:token :union       :ascii "UNION"  :unicode "\u22c3"}
:inter       {:token :inter       :ascii "INTER"  :unicode "\u22c2"}
:fcomp       {:token :fcomp       :ascii "\\fcomp" :unicode "\u003b"}
:bcomp       {:token :bcomp       :ascii "circ"   :unicode "\u2218"}
:strel       {:token :strel       :ascii "<<->>"  :unicode "\ue102"}
:dprod       {:token :dprod       :ascii "><"     :unicode "\u2297"}
:pprod       {:token :pprod       :ascii "||"     :unicode "\u2225"}
:bcmeq       {:token :bcmeq       :ascii ":="     :unicode "\u2254"}
:bcmin       {:token :bcmin       :ascii "::"     :unicode ":\u2208"}
:intg        {:token :intg        :ascii "INT"    :unicode "\u2124"}
:land        {:token :land        :ascii "&"      :unicode "\u2227"}
:limp        {:token :limp        :ascii "=>"     :unicode "\u21d2"}
:leqv        {:token :leqv        :ascii "<=>"    :unicode "\u21d4"}
:lnot        {:token :lnot        :ascii "not"    :unicode "\u00ac"}
:qdot        {:token :qdot        :ascii "."      :unicode "\u00b7"}
:conv        {:token :conv        :ascii "~"      :unicode "\u223c"}
:trel        {:token :trel        :ascii "<<->"   :unicode "\ue100"}
:srel        {:token :srel        :ascii "<->>"   :unicode "\ue101"}
:pfun        {:token :pfun        :ascii "+->"    :unicode "\u21f8"}
:tfun        {:token :tfun        :ascii "-->"    :unicode "\u2192"}
:pinj        {:token :pinj        :ascii ">+>"    :unicode "\u2914"}
:tinj        {:token :tinj        :ascii ">->"    :unicode "\u21a3"}
:psur        {:token :psur        :ascii "+>>"    :unicode "\u2900"}
:tsur        {:token :tsur        :ascii "->>"    :unicode "\u21a0"}
:tbij        {:token :tbij        :ascii ">->>"   :unicode "\u2916"}
:expn        {:token :expn        :ascii "\\expn" :unicode "\u005e"}
:lor         {:token :lor         :ascii "or"     :unicode "\u2228"}
:pow1        {:token :pow1        :ascii "POW1"   :unicode "\u2119\u0031"}
:pow         {:token :pow         :ascii "POW"    :unicode "\u2119"}
:mid         {:token :mid         :ascii "\\mid"  :unicode "\u2223"}
:neq         {:token :neq         :ascii "/="     :unicode "\u2260"}
:rel         {:token :rel         :ascii "<->"    :unicode "\u2194"}
:ovl         {:token :ovl         :ascii "<+"     :unicode "\ue103"}
:leq         {:token :leq         :ascii "<="     :unicode "\u2264"}
:geq         {:token :geq         :ascii ">="     :unicode "\u2265"}
:div         {:token :div         :ascii "/"      :unicode "\u00f7"}
:mult        {:token :mult        :ascii "*"      :unicode "\u2217"}
:minus       {:token :minus       :ascii "-"      :unicode "\u2212"}
:take        {:token :take        :ascii "/|\\"   :unicode "/|\\"}
:drop        {:token :drop        :ascii "\\|/"   :unicode "\\|/"}})

(defn starts-with? 
    "tests a sequence and a prefix to see if the sequence starts with the prefix"
    [l prefix]
    (if (> (count prefix) (count l)) 
        false
        (=  (take (count prefix) l) (seq prefix))))

(def whitespace-or-sep #{\newline \space \tab \formfeed \backspace \return \(})
(def digit #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9})

(defn extract-identifier [s]
    (let [[h t] (split-with #(Character/isUnicodeIdentifierPart %) s)]
        [(apply str h) t]))

(defn extract-number [s]
    (let [[h t] (split-with digit s)]
        [(apply str h) t]))

(defn match? 
    "checks to see if the pattern is a prefix to the sequence. However, after the sequence there needs to be either a whitespace or the end"
    [s pattern]
    (let [p      (seq pattern)
          pcount (count p)]
        (when (starts-with? s p) (or (= (count s) pcount) (whitespace-or-sep (nth s pcount))))))

(defn handle-letters [s pattern token]
    (if (match? s pattern) [token (drop (count pattern) s)] (extract-identifier s)))

(defmulti lex first)
(defmethod lex \! [s] [:forall (rest s)])
(defmethod lex \# [s] [:exists (rest s)])
(defmethod lex \c [s] (handle-letters s "circ" :bcomp))
(defmethod lex \% [s] [:lambda (rest s)])
(defmethod lex \& [s] [:land   (rest s)])
(defmethod lex \f [s] (handle-letters s "false" :bfalse))
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
                        (starts-with? s ":\u2223") [:bcmsuch (drop 2 s)]
                        (starts-with? s ":\u2208") [:bcmin (drop 2 s)]
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
                        \|                          [:mid (rest s)]))
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

(defmethod lex \~ [s]  [:conv  (rest s)])
(defmethod lex \⤀ [s] [:psur (rest s)])
(defmethod lex \ [s]  [:trel (rest s)])
(defmethod lex \↠ [s]  [:tsur (rest s)])
(defmethod lex \∀ [s] [:forall (rest s)])
(defmethod lex \≠ [s] [:neq (rest s)])
(defmethod lex \ [s] [:srel (rest s)])
(defmethod lex \◁ [s] [:domres (rest s)])
(defmethod lex \ [s] [:strel (rest s)])
(defmethod lex \⦂ [s] [:oftype (rest s)])
(defmethod lex \⊂ [s] [:subset (rest s)])
(defmethod lex \⋂ [s] [:inter (rest s)])
(defmethod lex \ [s] [:ovl (rest s)])
(defmethod lex \↣ [s] [:tinj (rest s)])
(defmethod lex \∃ [s] [:exists (rest s)])
(defmethod lex \∣ [s] [:mid (rest s)])
(defmethod lex \⋃ [s] [:union (rest s)])
(defmethod lex \ℤ [s] [:intg (rest s)])
(defmethod lex \≤ [s] [:leq (rest s)])
(defmethod lex \⩤ [s] [:domsub (rest s)])
(defmethod lex \⊄ [s] [:notsubset (rest s)])
(defmethod lex \⊤ [s] [:btrue (rest s)])
(defmethod lex \‥ [s] [:dotdot (rest s)])
(defmethod lex \∅ [s] [:emptyset (rest s)])
(defmethod lex \∥ [s] [:pprod (rest s)])
(defmethod lex \≥ [s] [:geq (rest s)])
(defmethod lex \⩥ [s] [:ransub (rest s)])
(defmethod lex \⊥ [s] [:bfalse (rest s)])
(defmethod lex \↦ [s] [:mapsto (rest s)])
(defmethod lex \⊆ [s] [:subseteq (rest s)])
(defmethod lex \∧ [s] [:land (rest s)])
(defmethod lex \∈ [s] [:in (rest s)])
(defmethod lex \∨ [s] [:lor (rest s)])
(defmethod lex \⊈ [s] [:notsubseteq (rest s)])
(defmethod lex \∉ [s] [:notin (rest s)])
(defmethod lex \∩ [s] [:binter (rest s)])
(defmethod lex \∪ [s] [:bunion (rest s)])
(defmethod lex \¬ [s] [:lnot (rest s)])
(defmethod lex \→ [s] [:tfun (rest s)])
(defmethod lex \⇒ [s] [:limp (rest s)])
(defmethod lex \− [s] [:minus (rest s)])
(defmethod lex \⤔ [s] [:pinj (rest s)])
(defmethod lex \↔ [s] [:rel (rest s)])
(defmethod lex \⇔ [s] [:leqv (rest s)])
(defmethod lex \≔ [s] [:bcmeq (rest s)])
(defmethod lex \⤖ [s] [:tbij (rest s)])
(defmethod lex \∖ [s] [:setminus (rest s)])
(defmethod lex \· [s] [:qdot (rest s)])
(defmethod lex \× [s] [:cprod (rest s)])
(defmethod lex \÷ [s] [:div (rest s)])
(defmethod lex \▷ [s] [:ranres (rest s)])
(defmethod lex \∗ [s] [:mult (rest s)])
(defmethod lex \⊗ [s] [:dprod (rest s)])
(defmethod lex \⇸ [s] [:pfun (rest s)])
(defmethod lex \∘ [s] [:bcomp (rest s)])
(defmethod lex \; [s] [:fcomp (rest s)])
(defmethod lex \λ [s] [:lambda (rest s)])
(defmethod lex \∼ [s] [:conv (rest s)])
(defmethod lex \^ [s] [:token :expn, (rest s)])
(defmethod lex \ℙ [s] (cond
                        (starts-with? s "ℙ1") [:pow1 (drop 2 s)]
                        \ℙ                    [:pow  (rest s)]))
(defmethod lex \ℕ [s] (cond 
                        (starts-with? s "ℕ1") [:nat1 (drop 2 s)]
                        \ℕ                    [:nat  (rest s)]))
(defmethod lex \_ [s] (extract-identifier s))
(defmethod lex :default [s] (let [c (first s)] 
                                (cond 
                                    (Character/isUnicodeIdentifierStart c) (extract-identifier s)
                                    (digit c) (extract-identifier s)
                                    :else  [c (rest s)])))

(defn tokenize ([s] (tokenize s []))
    ([s tokens] (if (empty? s) (reverse tokens)
                    (let [[token r] (lex s)]
                        (recur r (cons token tokens))))))

(defn ascii [s] 
    (let [token-stream (tokenize s)]
        (apply str (map #(:ascii (get token-map % {:ascii %})) token-stream))))

(defn unicode [s] 
    (let [token-stream (tokenize s)]
        (apply str (map #(:unicode (get token-map % {:unicode %})) token-stream))))