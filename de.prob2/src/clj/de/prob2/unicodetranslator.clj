(ns de.prob2.unicodetranslator
  (:require [clojure.string :as string]))

#_(defmulti lex [s] (first s))

#_(defmethod lex \: [s] (cond
						(.startsWith s ":!") :bcmsuch
            (.startsWith s ":=") :bcmeq
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

(defn extract-identifier [s])
(defn extract-other [s])

(defn handle-letters [s pattern token]
    (let [p-with  (re-pattern (str "^" pattern "\\s?"))]
        (if (re-matches p-with s) [token (drop (len pattern) s)] (extract-identifier s))))

(defmulti lex first)
(defmethod lex \! [s] [:forall (rest s)])
(defmethod lex \# [s] [:exists (rest s)])
(defmethod lex \c [s] (handle-letters s "circ" :bcomp))
(defmethod lex \% [s] [:lambda (rest s)])
(defmethod lex \& [s] [:land   (rest s)])
(defmethod lex \f [s] (handle-letters s "false" :false))
(defmethod lex \I [s] (cond
                        (re-matches #"^INTER\s?" s) [:inter (drop 5 s] 
                        (re-matches #"^INT\s?" s)   [:intg  (drop 3 s)]
                        :else                   (extract-identifier s)))
(defmethod lex \* [s] (cond
                        (.startsWith s "**")    [:cprod (drop 2 s)]
                        "*"                     [:mult  (rest s)]))
(defmethod lex \+ [s] (cond 
                        (.startsWith s "+->")   [:pfun  (drop 3 s)]
                        (.startsWith s "+>>")   [:psur  (drop 3 s)]
                        :else                   :fail))
(defmethod lex \- [s] (cond 
                        (.startsWith s "-->")   [:tfun  (drop 3 s)]
                        (.startsWith s "->>")   [:tsur  (drop 3 s)]
                        "-"                     [:minus (rest s)]))
(defmethod lex \. [s] (cond
                        (.startsWith s "..")    [:dotdot (drop 2 s)]
                        "."                     [:qdot  (rest s)]))
(defmethod lex \N [s] (cond 
                        (re-matches #"^NAT1\s?" s) [:nat1  (drop 4 s)]
                        (re-matches #"^NAT\s?" s) [:nat (drop 3 s)]))
(defmethod lex \n [s] (handle-letters s "not" :lnot))
(defmethod lex \/ [s] (cond
                        (.startsWith s "/<:")   [:notsubseteq  (drop 3 s)]
                        (.startsWith s "/<<:")  [:notsubset  (drop 4 s)]
                        (.startsWith s "/\\")   [:binter  (drop 2 s)]
                        (.startsWith s "/:")    [:notin   (drop 2 s)]
                        (.startsWith s "/=")    [:neq     (drop 2 s)]
                        (.startsWith s "/|\\")  [:take    (drop 3 s)]
                        "/"                     [:div     (rest s)]))
(defmethod lex \o [s] (cond
                        (re-matches #"^oftype\s?" s)  [:oftype (drop 6 s)]
                        (re-matches #"^or\s?")        [:lor    (drop 2 s)]
                        :else                   (extract-identifier s)))
(defmethod lex \P [s] (cond
                        (re-matches #"^POW1\s?" s)    [:pow1   (drop 4 s)]
                        (re-matches #"^POW\s?"  s)    [:pow    (drop 3 s)]))
(defmethod lex \t [s] (handle-letters s "true" :btrue))
(defmethod lex \U [s] (handle-letters s "UNION" :union))
(defmethod lex \: [s] (cond
                        (.startsWith s ":|")  [:bcmsuch  (drop 2 s)]
                        (.startsWith s ":=")  [:bcmeq    (drop 2 s)]
                        (.startsWith s "::")  [:bcmin    (drop 2 s)]
                        ":"                   [:in       (rest s)])) 
(defmethod lex \{ [s] (cond
                        (.startsWith s "{}")  [:emptyset (drop 2 s)]
                        "{"                   (extract-other s)))
(defmethod lex \< [s] (cond
                        (.startsWith s "<<->>") [:strel  (drop 5 s)]
                        (.startsWith s "<->>")  [:srel   (drop 4 s)]
                        (.startsWith s "<<->")  [:trel   (drop 4 s)]
                        (.startsWith s "<->")   [:rel    (drop 3 s)]
                        (.startsWith s "<=>")   [:leqv   (drop 3 s)]
                        (.startsWith s "<<|")   [:domsub (drop 3 s)]
                        (.startsWith s "<<:")   [:subset (drop 3 s)]
                        (.startsWith s "<=")    [:leq    (drop 2 s)]
                        (.startsWith s "<+")    [:ovl    (drop 2 s)]
                        (.startsWith s "<|")    [:domres (drop 2 s)]
                        (.startsWith s "<:")    [:subseteq (drop 2 s)]))
    \\ [{:ascii "\\", :token :setminus, :unicode "∖"} {:ascii "\\/", :token :bunion, :unicode "∪"} {:ascii "\\fcomp", :token :fcomp, :unicode ";"} {:ascii "\\expn", :token :expn, :unicode "^"} {:ascii "\\mid", :token :mid, :unicode "∣"} {:ascii "\\|/", :token :drop, :unicode "\\|/"}],
\| [{:ascii "|->", :token :mapsto, :unicode "↦"} {:ascii "|>", :token :ranres, :unicode "▷"} {:ascii "|>>", :token :ransub, :unicode "⩥"} {:ascii "||", :token :pprod, :unicode "∥"}],
\= [{:ascii "=>", :token :limp, :unicode "⇒"}],
\> [{:ascii "><", :token :dprod, :unicode "⊗"} {:ascii ">+>", :token :pinj, :unicode "⤔"} {:ascii ">->", :token :tinj, :unicode "↣"} {:ascii ">->>", :token :tbij, :unicode "⤖"} {:ascii ">=", :token :geq, :unicode "≥"}],
\~ [{:ascii "~", :token :conv, :unicode "∼"}]}
