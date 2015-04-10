(ns de.prob2.unicodetranslator
  (:require [clojure.string :as str]))

(defmulti lex (first s))

(defmethod lex \: [s] (cond
						(.startsWith s ":!") "x"))

(def tokens
[{:token :in          :ascii ":"      :latex "\in"            :unicode "\u2208"}
{:token :notsubseteq :ascii "/<:"    :latex "\notsubseteq"   :unicode "\u2288"}
{:token :notsubset   :ascii "/<<:"   :latex "\notsubset"     :unicode "\u2284"}
{:token :subseteq    :ascii "<:"     :latex "\subseteq"      :unicode "\u2286"}
{:token :setminus    :ascii "\"      :latex "\setminus"      :unicode "\u2216"}
{:token :dotdot      :ascii ".."     :latex "\0xpto"         :unicode "\u2025"}
{:token :nat1        :ascii "NAT1"   :latex "\nat1"          :unicode "\u2115\u0031""}
{:token :nat         :ascii "NAT"    :latex "\nat"           :unicode "\u2115"}
{:token :emptyset    :ascii "{}"     :latex "\emptyset"      :unicode "\u2205"}
{:token :bcmsuch     :ascii ":|"     :latex "\bcmsuch"       :unicode "\u2223"}
{:token :bfalse      :ascii "false"  :latex "\bfalse"        :unicode "\u22a5"}
{:token :forall      :ascii "!"      :latex "\forall"        :unicode "\u2200"}
{:token :exists      :ascii "#"      :latex "\exists"        :unicode "\u2203"}
{:token :mapsto      :ascii "|->"    :latex "\mapsto"        :unicode "\u21a6"}
{:token :btrue       :ascii "true"   :latex "\btrue"         :unicode "\u22a4"}
{:token :subset      :ascii "<<:"    :latex "\subset"        :unicode "\u2282"}
{:token :bunion      :ascii "\/"     :latex "\bunion"        :unicode "\u222a"}
{:token :binter      :ascii "/\"     :latex "\binter"        :unicode "\u2229"}
{:token :domres      :ascii "<|"     :latex "\domres"        :unicode "\u25c1"}
{:token :ranres      :ascii "|>"     :latex "\ranres"        :unicode "\u25b7"}
{:token :domsub      :ascii "<<|"    :latex "\domsub"        :unicode "\u2a64"}
{:token :ransub      :ascii "|>>"    :latex "\ransub"        :unicode "\u2a65"}
{:token :lambda      :ascii "%"      :latex "\lambda"        :unicode "\u03bb"}
{:token :oftype      :ascii "oftype" :latex "\oftype"        :unicode "\u2982"}
{:token :notin       :ascii "/:"     :latex "\notin"         :unicode "\u2209"}
{:token :cprod       :ascii "**"     :latex "\cprod"         :unicode "\u00d7"}
{:token :union       :ascii "UNION"  :latex "\Union"         :unicode "\u22c3"}
{:token :inter       :ascii "INTER"  :latex "\Inter"         :unicode "\u22c2"}
{:token :fcomp       :ascii "\fcomp"                         :unicode "\u003b"}
{:token :bcomp       :ascii "circ"   :latex "\bcomp"         :unicode "\u2218"}
{:token :strel       :ascii "<<->>"  :latex "\strel"         :unicode "\ue102"}
{:token :dprod       :ascii "><"     :latex "\dprod"         :unicode "\u2297"}
{:token :pprod       :ascii "||"     :latex "\pprod"         :unicode "\u2225"}
{:token :bcmeq       :ascii ":="     :latex "\bcmeq"         :unicode "\u2254"}
{:token :bcmin       :ascii "::"     :latex "\bcmin"         :unicode "\u2208"}
{:token :intg        :ascii "INT"    :latex "\intg"          :unicode "\u2124"}
{:token :land        :ascii "&"      :latex "\land"          :unicode "\u2227"}
{:token :limp        :ascii "=>"     :latex "\limp"          :unicode "\u21d2"}
{:token :leqv        :ascii "<=>"    :latex "\leqv"          :unicode "\u21d4"}
{:token :lnot        :ascii "not"    :latex "\lnot"          :unicode "\u00ac"}
{:token :qdot        :ascii "."      :latex "\qdot"          :unicode "\u00b7"}
{:token :conv        :ascii "~"      :latex "\conv"          :unicode "\u223c"}
{:token :trel        :ascii "<<->"   :latex "\trel"          :unicode "\ue100"}
{:token :srel        :ascii "<->>"   :latex "\srel"          :unicode "\ue101"}
{:token :pfun        :ascii "+->"    :latex "\pfun"          :unicode "\u21f8"}
{:token :tfun        :ascii "-->"    :latex "\tfun"          :unicode "\u2192"}
{:token :pinj        :ascii ">+>"    :latex "\pinj"          :unicode "\u2914"}
{:token :tinj        :ascii ">->"    :latex "\tinj"          :unicode "\u21a3"}
{:token :psur        :ascii "+>>"    :latex "\psur"          :unicode "\u2900"}
{:token :tsur        :ascii "->>"    :latex "\tsur"          :unicode "\u21a0"}
{:token :tbij        :ascii ">->>"   :latex "\tbij"          :unicode "\u2916"}
{:token :expn        :ascii "\expn"                          :unicode "\u005e"}
{:token :lor         :ascii "or"     :latex "\lor"           :unicode "\u2228"}
{:token :pow1        :ascii "POW1"   :latex "\pow1"          :unicode "\u2119\u0031"}
{:token :pow         :ascii "POW"    :latex "\pow"           :unicode "\u2119"}
{:token :mid         :ascii "\mid"   :latex "|"              :unicode "\u2223"}
{:token :neq         :ascii "/="     :latex "\neq"           :unicode "\u2260"}
{:token :rel         :ascii "<->'    :latex "\rel"           :unicode "\u2194"}
{:token :ovl         :ascii "<+"     :latex "\ovl"           :unicode "\ue103"}
{:token :leq         :ascii "<="     :latex "\leq"           :unicode "\u2264"}
{:token :geq         :ascii ">="     :latex "\geq"           :unicode "\u2265"}
{:token :div         :ascii "/"      :latex "\div"           :unicode "\u00f7"}
{:token :mult        :ascii "*"                              :unicode "\u2217"}
{:token :minus       :ascii "-"                              :unicode "\u2212"}
{:token :take        :ascii "/|\\"                            :unicode "/|\\"}
{:token :drop        :ascii "\\|/"                            :unicode "\\|/"}])


(defn ascii [s] s)

(defn unicode [s] s)

(defn tokenize [s]
  (let [sp (str/split s #"\s+")]))