(ns de.prob2.ascii-tounicode
	(:require [de.prob2.unicodetranslator :refer (unicode)])
	(:use clojure.test))

(deftest TIn
    (is (= (unicode ":") "\u2208")))

(deftest TNotsubseteq
    (is (= (unicode "/<:") "\u2288")))

(deftest TNotsubset
    (is (= (unicode "/<<:") "\u2284")))

(deftest TSubseteq
    (is (= (unicode "<:") "\u2286")))

(deftest TSetminus
    (is (= (unicode "\\") "\u2216")))

(deftest TDotdot
    (is (= (unicode "..") "\u2025")))

(deftest TNat
    (is (= (unicode "NAT") "\u2115")))

(deftest TEmptyset
    (is (= (unicode "{}") "\u2205")))

(deftest TBcmsuch
    (is (= (unicode ":|") ":\u2223")))

(deftest TBfalse
    (is (= (unicode "false") "\u22a5")))

(deftest TForall
    (is (= (unicode "!") "\u2200")))

(deftest TExists
    (is (= (unicode "#") "\u2203")))

(deftest TMapsto
    (is (= (unicode "|->") "\u21a6")))

(deftest TBtrue
    (is (= (unicode "true") "\u22a4")))

(deftest TSubset
    (is (= (unicode "<<:") "\u2282")))

(deftest TBunion
    (is (= (unicode "\\/") "\u222a")))

(deftest TBinter
    (is (= (unicode "/\\") "\u2229")))

(deftest TDomres
    (is (= (unicode "<|") "\u25c1")))

(deftest TRanres
    (is (= (unicode "|>") "\u25b7")))

(deftest TDomsub
    (is (= (unicode "<<|") "\u2a64")))

(deftest TRansub
    (is (= (unicode "|>>") "\u2a65")))

(deftest TLambda
    (is (= (unicode "%") "\u03bb")))

(deftest TOftype
    (is (= (unicode "oftype") "\u2982")))

(deftest TNotin
    (is (= (unicode "/:") "\u2209")))

(deftest TCprod
    (is (= (unicode "**") "\u00d7")))

(deftest TUnion
    (is (= (unicode "UNION") "\u22c3")))

(deftest TInter
    (is (= (unicode "INTER") "\u22c2")))

(deftest TFcomp
    (is (= (unicode ";") "\u003b")))

(deftest TBcomp
    (is (= (unicode "circ") "\u2218")))

(deftest TStrel
    (is (= (unicode "<<->>") "\ue102")))

(deftest TDprod
    (is (= (unicode "><") "\u2297")))

(deftest TPprod
    (is (= (unicode "||") "\u2225")))

(deftest TBcmeq
    (is (= (unicode ":=") "\u2254")))

(deftest TBcmin
    (is (= (unicode "::") ":\u2208")))

(deftest TIntg
    (is (= (unicode "INT") "\u2124")))

(deftest TLand
    (is (= (unicode "&") "\u2227")))

(deftest TLimp
    (is (= (unicode "=>") "\u21d2")))

(deftest TLeqv
    (is (= (unicode "<=>") "\u21d4")))

(deftest TLnot
    (is (= (unicode "not") "\u00ac")))

(deftest TQdot
    (is (= (unicode ".") "\u00b7")))

(deftest TConv
    (is (= (unicode "~") "\u223c")))

(deftest TTrel
    (is (= (unicode "<<->") "\ue100")))

(deftest TSurjectiveRel
    (is (= (unicode "<->>") "\ue101")))

(deftest TPfun
    (is (= (unicode "+->") "\u21f8")))

(deftest TTfun
    (is (= (unicode "-->") "\u2192")))

(deftest TPinj
    (is (= (unicode ">+>") "\u2914")))

(deftest TTinj
    (is (= (unicode ">->") "\u21a3")))

(deftest TPsur
    (is (= (unicode "+>>") "\u2900")))

(deftest TTsur
    (is (= (unicode "->>") "\u21a0")))

(deftest TTbij
    (is (= (unicode ">->>") "\u2916")))

(deftest TExpn
	(is (= (unicode "\u005e") "^"))
	(is (= (unicode "^") "\u005e")))

(deftest TLor
    (is (= (unicode "or") "\u2228")))

(deftest TPow
    (is (= (unicode "POW") "\u2119")))

(deftest TMid
    (is (= (unicode "|") "\u2223")))

(deftest TNeq
    (is (= (unicode "/=") "\u2260")))

(deftest TRel
    (is (= (unicode "<->") "\u2194")))

(deftest TOvl
    (is (= (unicode "<+") "\ue103")))

(deftest TLeq
    (is (= (unicode "<=") "\u2264")))

(deftest TGeq
    (is (= (unicode ">=") "\u2265")))

(deftest TDiv
    (is (= (unicode "/") "\u00f7")))

(deftest TMult
    (is (= (unicode "*") "\u2217")))

(deftest TMinus
    (is (= (unicode "-") "\u2212")))

(deftest TComma
    (is (= (unicode ",") ",")))

(deftest Conjunction
    (is (= (unicode "P & Q") "P \u2227 Q")))

(deftest Disjunction
    (is (= (unicode "P or Q") "P \u2228 Q")))

(deftest Implication
    (is (= (unicode "P => Q") "P \u21d2 Q")))

(deftest Equivalence
    (is (= (unicode "P <=> Q") "P \u21d4 Q")))

(deftest Negation
    (is (= (unicode "not P") "\u00ac P")))

(deftest UniversalQuantification
    (is (= (unicode "!(z).(P => Q)") "\u2200(z)\u00b7(P \u21d2 Q)")))

(deftest UniversalQuantification2
    (is (= (unicode "(!z.P => Q)") "(\u2200z\u00b7P \u21d2 Q)")))

(deftest ExistentialQuantification
    (is (= (unicode "#(z).(P & Q)") "\u2203(z)\u00b7(P \u2227 Q)")))

(deftest ExistentialQuantification2
    (is (= (unicode "(#z.P & Q)") "(\u2203z\u00b7P \u2227 Q)")))

(deftest Substitution
    (is (= (unicode "[G] P") "[G] P")))

(deftest Equality
    (is (= (unicode "E = F") "E = F")))

(deftest Inequality
    (is (= (unicode "E /= F") "E \u2260 F")))

(deftest SingletonSet
    (is (= (unicode "{E}") "{E}")))

(deftest SetEnumeration
    (is (= (unicode "{E, F}") "{E, F}")))

(deftest EmptySet
    (is (= (unicode "{}") "\u2205")))

(deftest SetComprehension
    (is (= (unicode "{z | P}") "{z \u2223 P}")))

(deftest SetComprehension2
    (is (= (unicode "{z . P | F}") "{z \u00b7 P \u2223 F}")))

(deftest SetComprehension3
    (is (= (unicode "{F | P}") "{F \u2223 P}")))

(deftest SetComprehension4
    (is (= (unicode "{x | P}") "{x \u2223 P}")))

(deftest Union
    (is (= (unicode "S \\/ T") "S \u222a T")))

(deftest Intersection
    (is (= (unicode "S /\\ T") "S \u2229 T")))

(deftest Difference
    (is (= (unicode "S-T") "S\u2212T")))

(deftest Difference2
	(is (= (unicode "S \\ T") "S \u2216 T")))

(deftest OrderedPair
    (is (= (unicode "E |-> F") "E \u21a6 F")))

(deftest CartesianProduct
	(is (= (unicode "S * T") "S \u2217 T")))

(deftest CartesianProduct2
    (is (= (unicode "S ** T") "S \u00d7 T")))

(deftest Powerset
    (is (= (unicode "POW(S)") "\u2119(S)")))

(deftest NonEmptySubsets
    (is (= (unicode "POW1(S)") "\u21191(S)")))

(deftest FiniteSets
    (is (= (unicode "finite S") "finite S")))

(deftest FiniteSubsets
    (is (= (unicode "FIN(S)") "FIN(S)")))

(deftest FiniteNonEmptySubsets
    (is (= (unicode "FIN1(S)") "FIN1(S)")))

(deftest Cardinality
    (is (= (unicode "card(S)") "card(S)")))

(deftest Partition
    (is (= (unicode "partition(S,x,y)") "partition(S,x,y)")))

(deftest GeneralizedUnion
    (is (= (unicode "UNION(U)") "\u22c3(U)")))

(deftest GeneralizedUnion2
    (is (= (unicode "UNION (z).(P | E)") "\u22c3 (z)\u00b7(P \u2223 E)")))

(deftest GeneralizedUnion3
    (is (= (unicode "union(U)") "union(U)")))

(deftest QuantifiedUnion
    (is (= (unicode "UNION z.P | S") "\u22c3 z\u00b7P \u2223 S")))

(deftest GeneralizedIntersection
    (is (= (unicode "INTER(U)") "\u22c2(U)")))

(deftest GeneralizedIntersection2
    (is (= (unicode "INTER (z).(P | E)") "\u22c2 (z)\u00b7(P \u2223 E)")))

(deftest SetMembership
    (is (= (unicode "E : S") "E \u2208 S")))

(deftest SetNonMembership
    (is (= (unicode "E /: S") "E \u2209 S")))

(deftest Subset
    (is (= (unicode "S <: T") "S \u2286 T")))

(deftest NotASubset
    (is (= (unicode "S /<: T") "S \u2288 T")))

(deftest ProperSubset
    (is (= (unicode "S <<: T") "S \u2282 T")))

(deftest NotAProperSubset
    (is (= (unicode "S /<<: T") "S \u2284 T")))

(deftest NaturalNumbers
    (is (= (unicode "NAT") "\u2115")))

(deftest PositiveNaturalNumbers
    (is (= (unicode "NAT1") "\u21151")))

(deftest Minimum
    (is (= (unicode "min(S)") "min(S)")))

(deftest Maximum
    (is (= (unicode "max(S)") "max(S)")))

(deftest Sum
    (is (= (unicode "m + n") "m + n")))

(deftest DifferenceAlt
    (is (= (unicode "m - n") "m \u2212 n")))

(deftest Product
	(is (= (unicode "m * n") "m \u2217 n")))

(deftest Quotient
    (is (= (unicode "m / n") "m \u00f7 n")))

(deftest Remainder
    (is (= (unicode "m mod n") "m mod n")))

(deftest Interval
    (is (= (unicode "m .. n") "m \u2025 n")))

(deftest SetSummation
	(is (= (unicode "SIGMA(z).(P | E)") "SIGMA(z)\u00b7(P \u2223 E)")))

(deftest SetProduct
    (is (= (unicode "PI(z).(P | E)") "PI(z)\u00b7(P \u2223 E)")))

(deftest Greater
    (is (= (unicode "m > n") "m > n")))

(deftest Less
    (is (= (unicode "m < n") "m < n")))

(deftest GreaterOrEqual
    (is (= (unicode "m >= n") "m \u2265 n")))

(deftest LessOrEqual
    (is (= (unicode "m <= n") "m \u2264 n")))

(deftest Relations
    (is (= (unicode "S <-> T") "S \u2194 T")))

(deftest Domain
    (is (= (unicode "dom(r)") "dom(r)")))

(deftest Range
    (is (= (unicode "ran(r)") "ran(r)")))

(deftest ForwardComposition
    (is (= (unicode "p ; q") "p ; q")))

(deftest BackwardComposition
    (is (= (unicode "p circ q") "p \u2218 q")))

(deftest Identity
    (is (= (unicode "id(S)") "id(S)")))

(deftest DomainRestriction
    (is (= (unicode "S <| r") "S \u25c1 r")))

(deftest DomainSubtraction
    (is (= (unicode "S <<| r") "S \u2a64 r")))

(deftest RangeRestriction
    (is (= (unicode "r |> T") "r \u25b7 T")))

(deftest RangeSubtraction
    (is (= (unicode "r |>> T") "r \u2a65 T")))

(deftest Inverse
    (is (= (unicode "r~") "r\u223c")))

(deftest relationalImage
    (is (= (unicode "r[S]") "r[S]")))

(deftest RightOverriding
    (is (= (unicode "r1 <+ r2") "r1 \ue103 r2")))

(deftest DirectProduct
    (is (= (unicode "p >< q") "p \u2297 q")))

(deftest ParallelProduct
    (is (= (unicode "p || q") "p \u2225 q")))

(deftest Iteration
    (is (= (unicode "iterate(r,n)") "iterate(r,n)")))

(deftest Closure
    (is (= (unicode "closure(r)") "closure(r)")))

(deftest rClosure
    (is (= (unicode "rclosure(r)") "rclosure(r)")))

(deftest iClosure
    (is (= (unicode "iclosure(r)") "iclosure(r)")))

(deftest Projection1
    (is (= (unicode "prj1(S,T)") "prj1(S,T)")))

(deftest Projection1_1
    (is (= (unicode "prj1") "prj1")))

(deftest Projection2
    (is (= (unicode "prj2(S,T)") "prj2(S,T)")))

(deftest Projection2_1
    (is (= (unicode "prj2") "prj2")))

(deftest PartialFunctions
    (is (= (unicode "S +-> T") "S \u21f8 T")))

(deftest TotalFunctions
    (is (= (unicode "S --> T") "S \u2192 T")))

(deftest PartialInjections
    (is (= (unicode "S >+> T") "S \u2914 T")))

(deftest TotalInjections
    (is (= (unicode "S >-> T") "S \u21a3 T")))

(deftest PartialSurjections
    (is (= (unicode "S +>> T") "S \u2900 T")))

(deftest TotalSurjections
    (is (= (unicode "S ->> T") "S \u21a0 T")))

(deftest Bijections
    (is (= (unicode "S >->> T") "S \u2916 T")))

(deftest LambdaAbstraction
    (is (= (unicode "%z.(P|E)") "\u03bbz\u00b7(P\u2223E)")))

(deftest FunctionApplication
    (is (= (unicode "f(E)") "f(E)")))

(deftest FunctionApplication2
    (is (= (unicode "f(E |-> F)") "f(E \u21a6 F)")))

(deftest FiniteSequences
    (is (= (unicode "seq S") "seq S"))
	(is (= (unicode "seq(S)") "seq(S)")))

(deftest FiniteNonEmptySequences
    (is (= (unicode "seq1(S)") "seq1(S)")))

(deftest InjectiveSequences
    (is (= (unicode "iseq(S)") "iseq(S)")))

(deftest Permutations
    (is (= (unicode "perm(S)") "perm(S)")))

(deftest SequenceConcatenations
    (is (= (unicode "s^t") "s\u005et")))

(deftest Size
    (is (= (unicode "size(s)") "size(s)")))

(deftest Reverse
    (is (= (unicode "rev(s)") "rev(s)")))

(deftest Take
    (is (= (unicode "s /|\\ n") "s /|\\ n")))

(deftest Drop
    (is (= (unicode "s \\|/ n") "s \\|/ n")))

(deftest FirstElement
    (is (= (unicode "first(s)") "first(s)")))

(deftest LastElement
    (is (= (unicode "last(s)") "last(s)")))

(deftest Tail
    (is (= (unicode "tail(s)") "tail(s)")))

(deftest Front
    (is (= (unicode "front(s)") "front(s)")))

(deftest GeneralizedConcatenation
    (is (= (unicode "conc(ss)") "conc(ss)")))

(deftest Substitution2
    (is (= (unicode "[G]P") "[G]P")))

(deftest Skip
    (is (= (unicode "skip") "skip")))

(deftest SimpleSubstitution
    (is (= (unicode "x := E") "x \u2254 E")))

(deftest BooleanSubstitution
    (is (= (unicode "x := bool(P)") "x \u2254 bool(P)")))

(deftest ChoiceFromSet
    (is (= (unicode "x :: S") "x :\u2208 S")))

(deftest ChoiceByPredicate
	(is (= (unicode "x : P") "x \u2208 P")))

(deftest ChoiceByPredicate2
    (is (= (unicode "x :| P") "x :\u2223 P")))

(deftest FunctionalOverride
    (is (= (unicode "f(x) := E") "f(x) \u2254 E")))

(deftest MultipleSubstitution
    (is (= (unicode "x,y := E,F") "x,y \u2254 E,F")))

(deftest ParallelSubstitution
    (is (= (unicode "G || H") "G \u2225 H")))

(deftest SequentialSubstitution
    (is (= (unicode "G ; H") "G ; H")))

(deftest Precondition
    (is (= (unicode "P | G") "P \u2223 G")))

(deftest Context
    (is (= (unicode "CONTEXT") "CONTEXT")))

(deftest Extends
    (is (= (unicode "EXTENDS") "EXTENDS")))

(deftest Sets
    (is (= (unicode "SETS") "SETS")))

(deftest Constants
    (is (= (unicode "CONSTANTS") "CONSTANTS")))

(deftest Axioms
    (is (= (unicode "AXIOMS") "AXIOMS")))

(deftest Theorems
    (is (= (unicode "THEOREMS") "THEOREMS")))

(deftest End
    (is (= (unicode "END") "END")))

(deftest Machine
    (is (= (unicode "MACHINE") "MACHINE")))

(deftest Refines
    (is (= (unicode "REFINES") "REFINES")))

(deftest Sees
    (is (= (unicode "SEES") "SEES")))

(deftest Variables
    (is (= (unicode "VARIABLES") "VARIABLES")))

(deftest Invariant
    (is (= (unicode "INVARIANT") "INVARIANT")))

(deftest Variant
    (is (= (unicode "VARIANT") "VARIANT")))

(deftest Events
    (is (= (unicode "EVENTS") "EVENTS")))

(deftest Any
    (is (= (unicode "ANY") "ANY")))

(deftest Where
    (is (= (unicode "WHERE") "WHERE")))

(deftest With
    (is (= (unicode "WITH") "WITH")))

(deftest Then
    (is (= (unicode "THEN") "THEN")))

(deftest Letter
    (is (= (unicode "abc") "abc")))

(deftest LetterDigit
    (is (= (unicode "abc123") "abc123")))

(deftest LetterUnderscore
    (is (= (unicode "abc_") "abc_")))

(deftest LetterANY
    (is (= (unicode "abcANY") "abcANY"))
    (is (= (unicode "abcany") "abcany")))

(deftest LetterFALSE
    (is (= (unicode "abcFALSE") "abcFALSE"))
    (is (= (unicode "abcfalse") "abcfalse")))

(deftest LetterINTEGER
    (is (= (unicode "abcINTEGER") "abcINTEGER"))
    (is (= (unicode "abcinteger") "abcinteger")))

(deftest LetterINTER
    (is (= (unicode "abcINTER") "abcINTER"))
    (is (= (unicode "abcinter") "abcinter")))

(deftest LetterNAT
    (is (= (unicode "abcNAT") "abcNAT"))
    (is (= (unicode "abcnat") "abcnat")))

(deftest LetterNAT1
    (is (= (unicode "abcNAT1") "abcNAT1"))
    (is (= (unicode "abcnat1") "abcnat1")))

(deftest LetterNATURAL
    (is (= (unicode "abcNATURAL") "abcNATURAL"))
    (is (= (unicode "abcnatural") "abcnatural")))

(deftest LetterNOT
    (is (= (unicode "abcNOT") "abcNOT"))
    (is (= (unicode "abcnot") "abcnot")))

(deftest LetterOR
    (is (= (unicode "abcOR") "abcOR"))
    (is (= (unicode "abcor") "abcor")))

(deftest LetterPOW
    (is (= (unicode "abcPOW") "abcPOW"))
    (is (= (unicode "abcpow") "abcpow")))

(deftest LetterPOW1
    (is (= (unicode "abcPOW1") "abcPOW1"))
    (is (= (unicode "abcpow1") "abcpow1")))

(deftest LetterTRUE
    (is (= (unicode "abcTRUE") "abcTRUE"))
    (is (= (unicode "abctrue") "abctrue")))

(deftest LetterUNION
    (is (= (unicode "abcUNION") "abcUNION"))
    (is (= (unicode "abcunion") "abcunion")))

(deftest LetterDigitUnderscore
    (is (= (unicode "abc123_") "abc123_")))

(deftest LetterDigitANY
    (is (= (unicode "abc123ANY") "abc123ANY"))
    (is (= (unicode "abc123any") "abc123any")))

(deftest LetterDigitFALSE
    (is (= (unicode "abc123FALSE") "abc123FALSE"))
    (is (= (unicode "abc123false") "abc123false")))

(deftest LetterDigitINTEGER
    (is (= (unicode "abc123INTEGER") "abc123INTEGER"))
    (is (= (unicode "abc123integer") "abc123integer")))

(deftest LetterDigitINTER
    (is (= (unicode "abc123INTER") "abc123INTER"))
    (is (= (unicode "abc123inter") "abc123inter")))

(deftest LetterDigitNAT
    (is (= (unicode "abc123NAT") "abc123NAT"))
    (is (= (unicode "abc123nat") "abc123nat")))

(deftest LetterDigitNAT1
    (is (= (unicode "abc123NAT1") "abc123NAT1"))
    (is (= (unicode "abc123nat1") "abc123nat1")))

(deftest LetterDigitNATURAL
    (is (= (unicode "abc123NATURAL") "abc123NATURAL"))
    (is (= (unicode "abc123natural") "abc123natural")))

(deftest LetterDigitNOT
    (is (= (unicode "abc123NOT") "abc123NOT"))
    (is (= (unicode "abc123not") "abc123not")))

(deftest LetterDigitOR
    (is (= (unicode "abc123OR") "abc123OR"))
    (is (= (unicode "abc123or") "abc123or")))

(deftest LetterDigitPOW
    (is (= (unicode "abc123POW") "abc123POW"))
    (is (= (unicode "abc123pow") "abc123pow")))

(deftest LetterDigitPOW1
    (is (= (unicode "abc123POW1") "abc123POW1"))
    (is (= (unicode "abc123pow1") "abc123pow1")))

(deftest LetterDigitTRUE
    (is (= (unicode "abc123TRUE") "abc123TRUE"))
    (is (= (unicode "abc123true") "abc123true")))

(deftest LetterDigitUNION
    (is (= (unicode "abc123UNION") "abc123UNION"))
    (is (= (unicode "abc123union") "abc123union")))

(deftest LetterUnderscoreDigit
    (is (= (unicode "abc_123") "abc_123")))

(deftest LetterUnderscoreANY
    (is (= (unicode "abc_ANY") "abc_ANY"))
    (is (= (unicode "abc_any") "abc_any")))

(deftest LetterUnderscoreFALSE
    (is (= (unicode "abc_FALSE") "abc_FALSE"))
    (is (= (unicode "abc_false") "abc_false")))

(deftest LetterUnderscoreINTEGER
    (is (= (unicode "abc_INTEGER") "abc_INTEGER"))
    (is (= (unicode "abc_integer") "abc_integer")))

(deftest LetterUnderscoreINTER
    (is (= (unicode "abc_INTER") "abc_INTER"))
    (is (= (unicode "abc_inter") "abc_inter")))

(deftest LetterUnderscoreNAT
    (is (= (unicode "abc_NAT") "abc_NAT"))
    (is (= (unicode "abc_nat") "abc_nat")))

(deftest LetterUnderscoreNAT1
    (is (= (unicode "abc_NAT1") "abc_NAT1"))
    (is (= (unicode "abc_nat1") "abc_nat1")))

(deftest LetterUnderscoreNATURAL
    (is (= (unicode "abc_NATURAL") "abc_NATURAL"))
    (is (= (unicode "abc_natural") "abc_natural")))

(deftest LetterUnderscoreNOT
    (is (= (unicode "abc_NOT") "abc_NOT"))
    (is (= (unicode "abc_not") "abc_not")))

(deftest LetterUnderscoreOR
    (is (= (unicode "abc_OR") "abc_OR"))
    (is (= (unicode "abc_or") "abc_or")))

(deftest LetterUnderscorePOW
    (is (= (unicode "abc_POW") "abc_POW"))
    (is (= (unicode "abc_pow") "abc_pow")))

(deftest LetterUnderscorePOW1
    (is (= (unicode "abc_POW1") "abc_POW1"))
    (is (= (unicode "abc_pow1") "abc_pow1")))

(deftest LetterUnderscoreTRUE
    (is (= (unicode "abc_TRUE") "abc_TRUE"))
    (is (= (unicode "abc_true") "abc_true")))

(deftest LetterUnderscoreUNION
    (is (= (unicode "abc_UNION") "abc_UNION"))
    (is (= (unicode "abc_union") "abc_union")))

(deftest LetterANYDigit
    (is (= (unicode "abcANY123") "abcANY123"))
    (is (= (unicode "abcany123") "abcany123")))

(deftest LetterFALSEDigit
    (is (= (unicode "abcFALSE123") "abcFALSE123"))
    (is (= (unicode "abcfalse123") "abcfalse123")))

(deftest LetterINTEGERDigit
    (is (= (unicode "abcINTEGER123") "abcINTEGER123"))
    (is (= (unicode "abcinteger123") "abcinteger123")))

(deftest LetterINTERDigit
    (is (= (unicode "abcINTER123") "abcINTER123"))
    (is (= (unicode "abcinter123") "abcinter123")))

(deftest LetterNATDigit
    (is (= (unicode "abcNAT123") "abcNAT123"))
    (is (= (unicode "abcnat123") "abcnat123")))

(deftest LetterNAT1Digit
    (is (= (unicode "abcNAT1123") "abcNAT1123"))
    (is (= (unicode "abcnat1123") "abcnat1123")))

(deftest LetterNATURALDigit
    (is (= (unicode "abcNATURAL123") "abcNATURAL123"))
    (is (= (unicode "abcnatural123") "abcnatural123")))

(deftest LetterNOTDigit
    (is (= (unicode "abcNOT123") "abcNOT123"))
    (is (= (unicode "abcnot123") "abcnot123")))

(deftest LetterORDigit
    (is (= (unicode "abcOR123") "abcOR123"))
    (is (= (unicode "abcor123") "abcor123")))

(deftest LetterPOWDigit
    (is (= (unicode "abcPOW123") "abcPOW123"))
    (is (= (unicode "abcpow123") "abcpow123")))

(deftest LetterPOW1Digit
    (is (= (unicode "abcPOW1123") "abcPOW1123"))
    (is (= (unicode "abcpow1123") "abcpow1123")))

(deftest LetterTRUEDigit
    (is (= (unicode "abcTRUE123") "abcTRUE123"))
    (is (= (unicode "abctrue123") "abctrue123")))

(deftest LetterUNIONDigit
    (is (= (unicode "abcUNION123") "abcUNION123"))
    (is (= (unicode "abcunion123") "abcunion123")))

(deftest LetterANYUnderscore
    (is (= (unicode "abcANY_") "abcANY_"))
    (is (= (unicode "abcany_") "abcany_")))

(deftest LetterFALSEUnderscore
    (is (= (unicode "abcFALSE_") "abcFALSE_"))
    (is (= (unicode "abcfalse_") "abcfalse_")))

(deftest LetterINTEGERUnderscore
    (is (= (unicode "abcINTEGER_") "abcINTEGER_"))
    (is (= (unicode "abcinteger_") "abcinteger_")))

(deftest LetterINTERUnderscore
    (is (= (unicode "abcINTER_") "abcINTER_"))
    (is (= (unicode "abcinter_") "abcinter_")))

(deftest LetterNATUnderscore
    (is (= (unicode "abcNAT_") "abcNAT_"))
    (is (= (unicode "abcnat_") "abcnat_")))

(deftest LetterNAT1Underscore
    (is (= (unicode "abcNAT1_") "abcNAT1_"))
    (is (= (unicode "abcnat1_") "abcnat1_")))

(deftest LetterNATURALUnderscore
    (is (= (unicode "abcNATURAL_") "abcNATURAL_"))
    (is (= (unicode "abcnatural_") "abcnatural_")))

(deftest LetterNOTUnderscore
    (is (= (unicode "abcNOT_") "abcNOT_"))
    (is (= (unicode "abcnot_") "abcnot_")))

(deftest LetterORUnderscore
    (is (= (unicode "abcOR_") "abcOR_"))
    (is (= (unicode "abcor_") "abcor_")))

(deftest LetterPOWUnderscore
    (is (= (unicode "abcPOW_") "abcPOW_"))
    (is (= (unicode "abcpow_") "abcpow_")))

(deftest LetterPOW1Underscore
    (is (= (unicode "abcPOW1_") "abcPOW1_"))
    (is (= (unicode "abcpow1_") "abcpow1_")))

(deftest LetterTRUEUnderscore
    (is (= (unicode "abcTRUE_") "abcTRUE_"))
    (is (= (unicode "abctrue_") "abctrue_")))

(deftest LetterUNIONUnderscore
    (is (= (unicode "abcUNION_") "abcUNION_"))
    (is (= (unicode "abcunion_") "abcunion_")))

(deftest LetterDigitUnderscoreANY
    (is (= (unicode "abc123_ANY") "abc123_ANY"))
    (is (= (unicode "abc123_any") "abc123_any")))

(deftest LetterDigitUnderscoreFALSE
    (is (= (unicode "abc123_FALSE") "abc123_FALSE"))
    (is (= (unicode "abc123_false") "abc123_false")))

(deftest LetterDigitUnderscoreINTEGER
    (is (= (unicode "abc123_INTEGER") "abc123_INTEGER"))
    (is (= (unicode "abc123_integer") "abc123_integer")))

(deftest LetterDigitUnderscoreINTER
    (is (= (unicode "abc123_INTER") "abc123_INTER"))
    (is (= (unicode "abc123_inter") "abc123_inter")))

(deftest LetterDigitUnderscoreNAT
    (is (= (unicode "abc123_NAT") "abc123_NAT"))
    (is (= (unicode "abc123_nat") "abc123_nat")))

(deftest LetterDigitUnderscoreNAT1
    (is (= (unicode "abc123_NAT1") "abc123_NAT1"))
    (is (= (unicode "abc123_nat1") "abc123_nat1")))

(deftest LetterDigitUnderscoreNATURAL
    (is (= (unicode "abc123_NATURAL") "abc123_NATURAL"))
    (is (= (unicode "abc123_natural") "abc123_natural")))

(deftest LetterDigitUnderscoreNOT
    (is (= (unicode "abc123_NOT") "abc123_NOT"))
    (is (= (unicode "abc123_not") "abc123_not")))

(deftest LetterDigitUnderscoreOR
    (is (= (unicode "abc123_OR") "abc123_OR"))
    (is (= (unicode "abc123_or") "abc123_or")))

(deftest LetterDigitUnderscorePOW
    (is (= (unicode "abc123_POW") "abc123_POW"))
    (is (= (unicode "abc123_pow") "abc123_pow")))

(deftest LetterDigitUnderscorePOW1
    (is (= (unicode "abc123_POW1") "abc123_POW1"))
    (is (= (unicode "abc123_pow1") "abc123_pow1")))

(deftest LetterDigitUnderscoreTRUE
    (is (= (unicode "abc123_TRUE") "abc123_TRUE"))
    (is (= (unicode "abc123_true") "abc123_true")))

(deftest LetterDigitUnderscoreUNION
    (is (= (unicode "abc123_UNION") "abc123_UNION"))
    (is (= (unicode "abc123_union") "abc123_union")))

(deftest LetterDigitANYUnderscore
    (is (= (unicode "abc123ANY_") "abc123ANY_"))
    (is (= (unicode "abc123any_") "abc123any_")))

(deftest LetterDigitFALSEUnderscore
    (is (= (unicode "abc123FALSE_") "abc123FALSE_"))
    (is (= (unicode "abc123false_") "abc123false_")))

(deftest LetterDigitINTEGERUnderscore
    (is (= (unicode "abc123INTEGER_") "abc123INTEGER_"))
    (is (= (unicode "abc123integer_") "abc123integer_")))

(deftest LetterDigitINTERUnderscore
    (is (= (unicode "abc123INTER_") "abc123INTER_"))
    (is (= (unicode "abc123inter_") "abc123inter_")))

(deftest LetterDigitNATUnderscore
    (is (= (unicode "abc123NAT_") "abc123NAT_"))
    (is (= (unicode "abc123nat_") "abc123nat_")))

(deftest LetterDigitNAT1Underscore
    (is (= (unicode "abc123NAT1_") "abc123NAT1_"))
    (is (= (unicode "abc123nat1_") "abc123nat1_")))

(deftest LetterDigitNATURALUnderscore
    (is (= (unicode "abc123NATURAL_") "abc123NATURAL_"))
    (is (= (unicode "abc123natural_") "abc123natural_")))

(deftest LetterDigitNOTUnderscore
    (is (= (unicode "abc123NOT_") "abc123NOT_"))
    (is (= (unicode "abc123not_") "abc123not_")))

(deftest LetterDigitORUnderscore
    (is (= (unicode "abc123OR_") "abc123OR_"))
    (is (= (unicode "abc123or_") "abc123or_")))

(deftest LetterDigitPOWUnderscore
    (is (= (unicode "abc123POW_") "abc123POW_"))
    (is (= (unicode "abc123pow_") "abc123pow_")))

(deftest LetterDigitPOW1Underscore
    (is (= (unicode "abc123POW1_") "abc123POW1_"))
    (is (= (unicode "abc123pow1_") "abc123pow1_")))

(deftest LetterDigitTRUEUnderscore
    (is (= (unicode "abc123TRUE_") "abc123TRUE_"))
    (is (= (unicode "abc123true_") "abc123true_")))

(deftest LetterDigitUNIONUnderscore
    (is (= (unicode "abc123UNION_") "abc123UNION_"))
    (is (= (unicode "abc123union_") "abc123union_")))

(deftest LetterUnderscoreDigitANY
    (is (= (unicode "abc_123ANY") "abc_123ANY"))
    (is (= (unicode "abc_123any") "abc_123any")))

(deftest LetterUnderscoreDigitFALSE
    (is (= (unicode "abc_123FALSE") "abc_123FALSE"))
    (is (= (unicode "abc_123false") "abc_123false")))

(deftest LetterUnderscoreDigitINTEGER
    (is (= (unicode "abc_123INTEGER") "abc_123INTEGER"))
    (is (= (unicode "abc_123integer") "abc_123integer")))

(deftest LetterUnderscoreDigitINTER
    (is (= (unicode "abc_123INTER") "abc_123INTER"))
    (is (= (unicode "abc_123inter") "abc_123inter")))

(deftest LetterUnderscoreDigitANT
    (is (= (unicode "abc_123NAT") "abc_123NAT"))
    (is (= (unicode "abc_123nat") "abc_123nat")))

(deftest LetterUnderscoreDigitNAT1
    (is (= (unicode "abc_123NAT1") "abc_123NAT1"))
    (is (= (unicode "abc_123nat1") "abc_123nat1")))

(deftest LetterUnderscoreDigitNATURAL
    (is (= (unicode "abc_123NATURAL") "abc_123NATURAL"))
    (is (= (unicode "abc_123natural") "abc_123natural")))

(deftest LetterUnderscoreDigitNOT
    (is (= (unicode "abc_123NOT") "abc_123NOT"))
    (is (= (unicode "abc_123not") "abc_123not")))

(deftest LetterUnderscoreDigitOR
    (is (= (unicode "abc_123OR") "abc_123OR"))
    (is (= (unicode "abc_123or") "abc_123or")))

(deftest LetterUnderscoreDigitPOW
    (is (= (unicode "abc_123POW") "abc_123POW"))
    (is (= (unicode "abc_123pow") "abc_123pow")))

(deftest LetterUnderscoreDigitPOW1
    (is (= (unicode "abc_123POW1") "abc_123POW1"))
    (is (= (unicode "abc_123pow1") "abc_123pow1")))

(deftest LetterUnderscoreDigitTRUE
    (is (= (unicode "abc_123TRUE") "abc_123TRUE"))
    (is (= (unicode "abc_123true") "abc_123true")))

(deftest LetterUnderscoreDigitUNION
    (is (= (unicode "abc_123UNION") "abc_123UNION"))
    (is (= (unicode "abc_123union") "abc_123union")))

(deftest LetterUnderscoreANYDigit
    (is (= (unicode "abc_ANY123") "abc_ANY123"))
    (is (= (unicode "abc_any123") "abc_any123")))

(deftest LetterUnderscoreFALSEDigit
    (is (= (unicode "abc_FALSE123") "abc_FALSE123"))
    (is (= (unicode "abc_false123") "abc_false123")))

(deftest LetterUnderscoreINTEGERDigit
    (is (= (unicode "abc_INTEGER123") "abc_INTEGER123"))
    (is (= (unicode "abc_integer123") "abc_integer123")))

(deftest LetterUnderscoreINTERDigit
    (is (= (unicode "abc_INTER123") "abc_INTER123"))
    (is (= (unicode "abc_inter123") "abc_inter123")))

(deftest LetterUnderscoreNATDigit
    (is (= (unicode "abc_NAT123") "abc_NAT123"))
    (is (= (unicode "abc_nat123") "abc_nat123")))

(deftest LetterUnderscoreNAT1Digit
    (is (= (unicode "abc_NAT1123") "abc_NAT1123"))
    (is (= (unicode "abc_nat1123") "abc_nat1123")))

(deftest LetterUnderscoreNATURALDigit
    (is (= (unicode "abc_NATURAL123") "abc_NATURAL123"))
    (is (= (unicode "abc_natural123") "abc_natural123")))

(deftest LetterUnderscoreNOTDigit
    (is (= (unicode "abc_NOT123") "abc_NOT123"))
    (is (= (unicode "abc_not123") "abc_not123")))

(deftest LetterUnderscoreORDigit
    (is (= (unicode "abc_OR123") "abc_OR123"))
    (is (= (unicode "abc_or123") "abc_or123")))

(deftest LetterUnderscorePOWDigit
    (is (= (unicode "abc_POW123") "abc_POW123"))
    (is (= (unicode "abc_pow123") "abc_pow123")))

(deftest LetterUnderscorePOW1Digit
    (is (= (unicode "abc_POW1123") "abc_POW1123"))
    (is (= (unicode "abc_pow1123") "abc_pow1123")))

(deftest LetterUnderscoreTRUEDigit
    (is (= (unicode "abc_TRUE123") "abc_TRUE123"))
    (is (= (unicode "abc_true123") "abc_true123")))

(deftest LetterUnderscoreUNIONDigit
    (is (= (unicode "abc_UNION123") "abc_UNION123"))
    (is (= (unicode "abc_union123") "abc_union123")))

(deftest LetterANYDigitUnderscore
    (is (= (unicode "abcANY123_") "abcANY123_"))
    (is (= (unicode "abcany123_") "abcany123_")))

(deftest LetterFALSEDigitUnderscore
    (is (= (unicode "abcFALSE123_") "abcFALSE123_"))
    (is (= (unicode "abcfalse123_") "abcfalse123_")))

(deftest LetterINTEGERDigitUnderscore
    (is (= (unicode "abcINTEGER123_") "abcINTEGER123_"))
    (is (= (unicode "abcinteger123_") "abcinteger123_")))

(deftest LetterINTERDigitUnderscore
    (is (= (unicode "abcINTER123_") "abcINTER123_"))
    (is (= (unicode "abcinter123_") "abcinter123_")))

(deftest LetterNATDigitUnderscore
    (is (= (unicode "abcNAT123_") "abcNAT123_"))
    (is (= (unicode "abcnat123_") "abcnat123_")))

(deftest LetterNAT1DigitUnderscore
    (is (= (unicode "abcNAT1123_") "abcNAT1123_"))
    (is (= (unicode "abcnat1123_") "abcnat1123_")))

(deftest LetterNATURALDigitUnderscore
    (is (= (unicode "abcNATURAL123_") "abcNATURAL123_"))
    (is (= (unicode "abcnatural123_") "abcnatural123_")))

(deftest LetterNOTDigitUnderscore
    (is (= (unicode "abcNOT123_") "abcNOT123_"))
    (is (= (unicode "abcnot123_") "abcnot123_")))

(deftest LetterORDigitUnderscore
    (is (= (unicode "abcOR123_") "abcOR123_"))
    (is (= (unicode "abcor123_") "abcor123_")))

(deftest LetterPOWDigitUnderscore
    (is (= (unicode "abcPOW123_") "abcPOW123_"))
    (is (= (unicode "abcpow123_") "abcpow123_")))

(deftest LetterPOW1DigitUnderscore
    (is (= (unicode "abcPOW1123_") "abcPOW1123_"))
    (is (= (unicode "abcpow1123_") "abcpow1123_")))

(deftest LetterTRUEDigitUnderscore
    (is (= (unicode "abcTRUE123_") "abcTRUE123_"))
    (is (= (unicode "abctrue123_") "abctrue123_")))

(deftest LetterUNIONDigitUnderscore
    (is (= (unicode "abcUNION123_") "abcUNION123_"))
    (is (= (unicode "abcunion123_") "abcunion123_")))

(deftest LetterANYUnderscoreDigit
    (is (= (unicode "abcANY_123") "abcANY_123"))
    (is (= (unicode "abcany_123") "abcany_123")))

(deftest LetterFALSEUnderscoreDigit
    (is (= (unicode "abcFALSE_123") "abcFALSE_123"))
    (is (= (unicode "abcfalse_123") "abcfalse_123")))

(deftest LetterINTEGERUnderscoreDigit
    (is (= (unicode "abcINTEGER_123") "abcINTEGER_123"))
    (is (= (unicode "abcinteger_123") "abcinteger_123")))

(deftest LetterINTERUnderscoreDigit
    (is (= (unicode "abcINTER_123") "abcINTER_123"))
    (is (= (unicode "abcinter_123") "abcinter_123")))

(deftest LetterNATUnderscoreDigit
    (is (= (unicode "abcNAT_123") "abcNAT_123"))
    (is (= (unicode "abcnat_123") "abcnat_123")))

(deftest LetterNAT1UnderscoreDigit
    (is (= (unicode "abcNAT1_123") "abcNAT1_123"))
    (is (= (unicode "abcnat1_123") "abcnat1_123")))

(deftest LetterNATURALUnderscoreDigit
    (is (= (unicode "abcNATURAL_123") "abcNATURAL_123"))
    (is (= (unicode "abcnatural_123") "abcnatural_123")))

(deftest LetterNOTUnderscoreDigit
    (is (= (unicode "abcNOT_123") "abcNOT_123"))
    (is (= (unicode "abcnot_123") "abcnot_123")))

(deftest LetterORUnderscoreDigit
    (is (= (unicode "abcOR_123") "abcOR_123"))
    (is (= (unicode "abcor_123") "abcor_123")))

(deftest LetterPOWUnderscoreDigit
    (is (= (unicode "abcPOW_123") "abcPOW_123"))
    (is (= (unicode "abcpow_123") "abcpow_123")))

(deftest LetterPOW1UnderscoreDigit
    (is (= (unicode "abcPOW1_123") "abcPOW1_123"))
    (is (= (unicode "abcpow1_123") "abcpow1_123")))

(deftest LetterTRUEUnderscoreDigit
    (is (= (unicode "abcTRUE_123") "abcTRUE_123"))
    (is (= (unicode "abctrue_123") "abctrue_123")))

(deftest LetterUNIONUnderscoreDigit
    (is (= (unicode "abcUNION_123") "abcUNION_123"))
    (is (= (unicode "abcunion_123") "abcunion_123")))

(deftest Digit
    (is (= (unicode "123") "123")))

(deftest DigitLetter
    (is (= (unicode "123abc") "123abc")))

(deftest DigitUnderscore
    (is (= (unicode "123_") "123_")))

(deftest DigitANY
    (is (= (unicode "123ANY") "123ANY"))
    (is (= (unicode "123any") "123any")))

(deftest DigitFALSE
    (is (= (unicode "123FALSE") "123FALSE"))
    (is (= (unicode "123false") "123false")))

(deftest DigitINTEGER
    (is (= (unicode "123INTEGER") "123INTEGER"))
    (is (= (unicode "123integer") "123integer")))

(deftest DigitINTER
    (is (= (unicode "123INTER") "123INTER"))
    (is (= (unicode "123inter") "123inter")))

(deftest DigitNAT
    (is (= (unicode "123NAT") "123NAT"))
    (is (= (unicode "123nat") "123nat")))

(deftest DigitNAT1
    (is (= (unicode "123NAT1") "123NAT1"))
    (is (= (unicode "123nat1") "123nat1")))

(deftest DigitNATURAL
    (is (= (unicode "123NATURAL") "123NATURAL"))
    (is (= (unicode "123natural") "123natural")))

(deftest DigitNOT
    (is (= (unicode "123NOT") "123NOT"))
    (is (= (unicode "123not") "123not")))

(deftest DigitOR
    (is (= (unicode "123OR") "123OR"))
    (is (= (unicode "123or") "123or")))

(deftest DigitPOW
    (is (= (unicode "123POW") "123POW"))
    (is (= (unicode "123pow") "123pow")))

(deftest DigitPOW1
    (is (= (unicode "123POW1") "123POW1"))
    (is (= (unicode "123pow1") "123pow1")))

(deftest DigitTRUE
    (is (= (unicode "123TRUE") "123TRUE"))
    (is (= (unicode "123true") "123true")))

(deftest DigitUNION
    (is (= (unicode "123UNION") "123UNION"))
    (is (= (unicode "123union") "123union")))

(deftest DigitLetterUnderscore
    (is (= (unicode "123abc_") "123abc_")))

(deftest DigitLetterANY
    (is (= (unicode "123abcANY") "123abcANY"))
    (is (= (unicode "123abcany") "123abcany")))

(deftest DigitLetterFALSE
    (is (= (unicode "123abcFALSE") "123abcFALSE"))
    (is (= (unicode "123abcfalse") "123abcfalse")))

(deftest DigitLetterINTEGER
    (is (= (unicode "123abcINTEGER") "123abcINTEGER"))
    (is (= (unicode "123abcinteger") "123abcinteger")))

(deftest DigitLetterINTER
    (is (= (unicode "123abcINTER") "123abcINTER"))
    (is (= (unicode "123abcinter") "123abcinter")))

(deftest DigitLetterNAT
    (is (= (unicode "123abcNAT") "123abcNAT"))
    (is (= (unicode "123abcnat") "123abcnat")))

(deftest DigitLetterNAT1
    (is (= (unicode "123abcNAT1") "123abcNAT1"))
    (is (= (unicode "123abcnat1") "123abcnat1")))

(deftest DigitLetterNATURAL
    (is (= (unicode "123abcNATURAL") "123abcNATURAL"))
    (is (= (unicode "123abcnatural") "123abcnatural")))

(deftest DigitLetterNOT
    (is (= (unicode "123abcNOT") "123abcNOT"))
    (is (= (unicode "123abcnot") "123abcnot")))

(deftest DigitLetterOR
    (is (= (unicode "123abcOR") "123abcOR"))
    (is (= (unicode "123abcor") "123abcor")))

(deftest DigitLetterPOW
    (is (= (unicode "123abcPOW") "123abcPOW"))
    (is (= (unicode "123abcpow") "123abcpow")))

(deftest DigitLetterPOW1
    (is (= (unicode "123abcPOW1") "123abcPOW1"))
    (is (= (unicode "123abcpow1") "123abcpow1")))

(deftest DigitLetterTRUE
    (is (= (unicode "123abcTRUE") "123abcTRUE"))
    (is (= (unicode "123abctrue") "123abctrue")))

(deftest DigitLetterUNION
    (is (= (unicode "123abcUNION") "123abcUNION"))
    (is (= (unicode "123abcunion") "123abcunion")))

(deftest DigitUnderscoreLetter
    (is (= (unicode "123_abc") "123_abc")))

(deftest DigitUnderscoreANY
    (is (= (unicode "123_ANY") "123_ANY"))
    (is (= (unicode "123_any") "123_any")))

(deftest DigitUnderscoreFALSE
    (is (= (unicode "123_FALSE") "123_FALSE"))
    (is (= (unicode "123_false") "123_false")))

(deftest DigitUnderscoreINTEGER
    (is (= (unicode "123_INTEGER") "123_INTEGER"))
    (is (= (unicode "123_integer") "123_integer")))

(deftest DigitUnderscoreINTER
    (is (= (unicode "123_INTER") "123_INTER"))
    (is (= (unicode "123_inter") "123_inter")))

(deftest DigitUnderscoreNAT
    (is (= (unicode "123_NAT") "123_NAT"))
    (is (= (unicode "123_nat") "123_nat")))

(deftest DigitUnderscoreNAT1
    (is (= (unicode "123_NAT1") "123_NAT1"))
    (is (= (unicode "123_nat1") "123_nat1")))

(deftest DigitUnderscoreNATURAL
    (is (= (unicode "123_NATURAL") "123_NATURAL"))
    (is (= (unicode "123_natural") "123_natural")))

(deftest DigitUnderscoreNOT
    (is (= (unicode "123_NOT") "123_NOT"))
    (is (= (unicode "123_not") "123_not")))

(deftest DigitUnderscoreOR
    (is (= (unicode "123_OR") "123_OR"))
    (is (= (unicode "123_or") "123_or")))

(deftest DigitUnderscorePOW
    (is (= (unicode "123_POW") "123_POW"))
    (is (= (unicode "123_pow") "123_pow")))

(deftest DigitUnderscorePOW1
    (is (= (unicode "123_POW1") "123_POW1"))
    (is (= (unicode "123_pow1") "123_pow1")))

(deftest DigitUnderscoreTRUE
    (is (= (unicode "123_TRUE") "123_TRUE"))
    (is (= (unicode "123_true") "123_true")))

(deftest DigitUnderscoreUNION
    (is (= (unicode "123_UNION") "123_UNION"))
    (is (= (unicode "123_union") "123_union")))

(deftest DigitANYLetter
    (is (= (unicode "123ANYabc") "123ANYabc"))
    (is (= (unicode "123anyabc") "123anyabc")))

(deftest DigitFALSELetter
    (is (= (unicode "123FALSEabc") "123FALSEabc"))
    (is (= (unicode "123falseabc") "123falseabc")))

(deftest DigitINTEGERLetter
    (is (= (unicode "123INTEGERabc") "123INTEGERabc"))
    (is (= (unicode "123integerabc") "123integerabc")))

(deftest DigitINTERLetter
    (is (= (unicode "123INTERabc") "123INTERabc"))
    (is (= (unicode "123interabc") "123interabc")))

(deftest DigitNATLetter
    (is (= (unicode "123NATabc") "123NATabc"))
    (is (= (unicode "123natabc") "123natabc")))

(deftest DigitNAT1Letter
    (is (= (unicode "123NAT1abc") "123NAT1abc"))
    (is (= (unicode "123nat1abc") "123nat1abc")))

(deftest DigitNATURALLetter
    (is (= (unicode "123NATURALabc") "123NATURALabc"))
    (is (= (unicode "123naturalabc") "123naturalabc")))

(deftest DigitNOTLetter
    (is (= (unicode "123NOTabc") "123NOTabc"))
    (is (= (unicode "123notabc") "123notabc")))

(deftest DigitORLetter
    (is (= (unicode "123ORabc") "123ORabc"))
    (is (= (unicode "123orabc") "123orabc")))

(deftest DigitPOWLetter
    (is (= (unicode "123POWabc") "123POWabc"))
    (is (= (unicode "123powabc") "123powabc")))

(deftest DigitPOW1Letter
    (is (= (unicode "123POW1abc") "123POW1abc"))
    (is (= (unicode "123pow1abc") "123pow1abc")))

(deftest DigitTRUELetter
    (is (= (unicode "123TRUEabc") "123TRUEabc"))
    (is (= (unicode "123trueabc") "123trueabc")))

(deftest DigitUNIONLetter
    (is (= (unicode "123UNIONabc") "123UNIONabc"))
    (is (= (unicode "123unionabc") "123unionabc")))

(deftest DigitANYUnderscore
    (is (= (unicode "123ANY_") "123ANY_"))
    (is (= (unicode "123any_") "123any_")))

(deftest DigitFALSEUnderscore
    (is (= (unicode "123FALSE_") "123FALSE_"))
    (is (= (unicode "123false_") "123false_")))

(deftest DigitINTEGERUnderscore
    (is (= (unicode "123INTEGER_") "123INTEGER_"))
    (is (= (unicode "123integer_") "123integer_")))

(deftest DigitINTERUnderscore
    (is (= (unicode "123INTER_") "123INTER_"))
    (is (= (unicode "123inter_") "123inter_")))

(deftest DigitNATUnderscore
    (is (= (unicode "123NAT_") "123NAT_"))
    (is (= (unicode "123nat_") "123nat_")))

(deftest DigitNAT1Underscore
    (is (= (unicode "123NAT1_") "123NAT1_"))
    (is (= (unicode "123nat1_") "123nat1_")))

(deftest DigitNATURALUnderscore
    (is (= (unicode "123NATURAL_") "123NATURAL_"))
    (is (= (unicode "123natural_") "123natural_")))

(deftest DigitNOTUnderscore
    (is (= (unicode "123NOT_") "123NOT_"))
    (is (= (unicode "123not_") "123not_")))

(deftest DigitORUnderscore
    (is (= (unicode "123OR_") "123OR_"))
    (is (= (unicode "123or_") "123or_")))

(deftest DigitPOWUnderscore
    (is (= (unicode "123POW_") "123POW_"))
    (is (= (unicode "123pow_") "123pow_")))

(deftest DigitPOW1Underscore
    (is (= (unicode "123POW1_") "123POW1_"))
    (is (= (unicode "123pow1_") "123pow1_")))

(deftest DigitTRUEUnderscore
    (is (= (unicode "123TRUE_") "123TRUE_"))
    (is (= (unicode "123true_") "123true_")))

(deftest DigitUNIONUnderscore
    (is (= (unicode "123UNION_") "123UNION_"))
    (is (= (unicode "123union_") "123union_")))

(deftest DigitLetterUnderscoreANY
    (is (= (unicode "123abc_ANY") "123abc_ANY"))
    (is (= (unicode "123abc_any") "123abc_any")))

(deftest DigitLetterUnderscoreFALSE
    (is (= (unicode "123abc_FALSE") "123abc_FALSE"))
    (is (= (unicode "123abc_false") "123abc_false")))

(deftest DigitLetterUnderscoreINTEGER
    (is (= (unicode "123abc_INTEGER") "123abc_INTEGER"))
    (is (= (unicode "123abc_integer") "123abc_integer")))

(deftest DigitLetterUnderscoreINTER
    (is (= (unicode "123abc_INTER") "123abc_INTER"))
    (is (= (unicode "123abc_inter") "123abc_inter")))

(deftest DigitLetterUnderscoreNAT
    (is (= (unicode "123abc_NAT") "123abc_NAT"))
    (is (= (unicode "123abc_nat") "123abc_nat")))

(deftest DigitLetterUnderscoreNAT1
    (is (= (unicode "123abc_NAT1") "123abc_NAT1"))
    (is (= (unicode "123abc_nat1") "123abc_nat1")))

(deftest DigitLetterUnderscoreNATURAL
    (is (= (unicode "123abc_NATURAL") "123abc_NATURAL"))
    (is (= (unicode "123abc_natural") "123abc_natural")))

(deftest DigitLetterUnderscoreNOT
    (is (= (unicode "123abc_NOT") "123abc_NOT"))
    (is (= (unicode "123abc_not") "123abc_not")))

(deftest DigitLetterUnderscoreOR
    (is (= (unicode "123abc_OR") "123abc_OR"))
    (is (= (unicode "123abc_or") "123abc_or")))

(deftest DigitLetterUnderscorePOW
    (is (= (unicode "123abc_POW") "123abc_POW"))
    (is (= (unicode "123abc_pow") "123abc_pow")))

(deftest DigitLetterUnderscorePOW1
    (is (= (unicode "123abc_POW1") "123abc_POW1"))
    (is (= (unicode "123abc_pow1") "123abc_pow1")))

(deftest DigitLetterUnderscoreTRUE
    (is (= (unicode "123abc_TRUE") "123abc_TRUE"))
    (is (= (unicode "123abc_true") "123abc_true")))

(deftest DigitLetterUnderscoreUNION
    (is (= (unicode "123abc_UNION") "123abc_UNION"))
    (is (= (unicode "123abc_union") "123abc_union")))

(deftest DigitLetterANYUnderscore
    (is (= (unicode "123abcANY_") "123abcANY_"))
    (is (= (unicode "123abcany_") "123abcany_")))

(deftest DigitLetterFALSEUnderscore
    (is (= (unicode "123abcFALSE_") "123abcFALSE_"))
    (is (= (unicode "123abcfalse_") "123abcfalse_")))

(deftest DigitLetterINTEGERUnderscore
    (is (= (unicode "123abcINTEGER_") "123abcINTEGER_"))
    (is (= (unicode "123abcinteger_") "123abcinteger_")))

(deftest DigitLetterINTERUnderscore
    (is (= (unicode "123abcINTER_") "123abcINTER_"))
    (is (= (unicode "123abcinter_") "123abcinter_")))

(deftest DigitLetterNATUnderscore
    (is (= (unicode "123abcNAT_") "123abcNAT_"))
    (is (= (unicode "123abcnat_") "123abcnat_")))

(deftest DigitLetterNAT1Underscore
    (is (= (unicode "123abcNAT1_") "123abcNAT1_"))
    (is (= (unicode "123abcnat1_") "123abcnat1_")))

(deftest DigitLetterNATURALUnderscore
    (is (= (unicode "123abcNATURAL_") "123abcNATURAL_"))
    (is (= (unicode "123abcnatural_") "123abcnatural_")))

(deftest DigitLetterNOTUnderscore
    (is (= (unicode "123abcNOT_") "123abcNOT_"))
    (is (= (unicode "123abcnot_") "123abcnot_")))

(deftest DigitLetterORUnderscore
    (is (= (unicode "123abcOR_") "123abcOR_"))
    (is (= (unicode "123abcor_") "123abcor_")))

(deftest DigitLetterPOWUnderscore
    (is (= (unicode "123abcPOW_") "123abcPOW_"))
    (is (= (unicode "123abcpow_") "123abcpow_")))

(deftest DigitLetterPOW1Underscore
    (is (= (unicode "123abcPOW1_") "123abcPOW1_"))
    (is (= (unicode "123abcpow1_") "123abcpow1_")))

(deftest DigitLetterTRUEUnderscore
    (is (= (unicode "123abcTRUE_") "123abcTRUE_"))
    (is (= (unicode "123abctrue_") "123abctrue_")))

(deftest DigitLetterUNIONUnderscore
    (is (= (unicode "123abcUNION_") "123abcUNION_"))
    (is (= (unicode "123abcunion_") "123abcunion_")))

(deftest DigitUnderscoreLetterANY
    (is (= (unicode "123_abcANY") "123_abcANY"))
    (is (= (unicode "123_abcany") "123_abcany")))

(deftest DigitUnderscoreLetterFALSE
    (is (= (unicode "123_abcFALSE") "123_abcFALSE"))
    (is (= (unicode "123_abcfalse") "123_abcfalse")))

(deftest DigitUnderscoreLetterINTEGER
    (is (= (unicode "123_abcINTEGER") "123_abcINTEGER"))
    (is (= (unicode "123_abcinteger") "123_abcinteger")))

(deftest DigitUnderscoreLetterINTER
    (is (= (unicode "123_abcINTER") "123_abcINTER"))
    (is (= (unicode "123_abcinter") "123_abcinter")))

(deftest DigitUnderscoreLetterNAT
    (is (= (unicode "123_abcNAT") "123_abcNAT"))
    (is (= (unicode "123_abcnat") "123_abcnat")))

(deftest DigitUnderscoreLetterNAT1
    (is (= (unicode "123_abcNAT1") "123_abcNAT1"))
    (is (= (unicode "123_abcnat1") "123_abcnat1")))

(deftest DigitUnderscoreLetterNATURAL
    (is (= (unicode "123_abcNATURAL") "123_abcNATURAL"))
    (is (= (unicode "123_abcnatural") "123_abcnatural")))

(deftest DigitUnderscoreLetterNOT
    (is (= (unicode "123_abcNOT") "123_abcNOT"))
    (is (= (unicode "123_abcnot") "123_abcnot")))

(deftest DigitUnderscoreLetterOR
    (is (= (unicode "123_abcOR") "123_abcOR"))
    (is (= (unicode "123_abcor") "123_abcor")))

(deftest DigitUnderscoreLetterPOW
    (is (= (unicode "123_abcPOW") "123_abcPOW"))
    (is (= (unicode "123_abcpow") "123_abcpow")))

(deftest DigitUnderscoreLetterPOW1
    (is (= (unicode "123_abcPOW1") "123_abcPOW1"))
    (is (= (unicode "123_abcpow1") "123_abcpow1")))

(deftest DigitUnderscoreLetterTRUE
    (is (= (unicode "123_abcTRUE") "123_abcTRUE"))
    (is (= (unicode "123_abctrue") "123_abctrue")))

(deftest DigitUnderscoreLetterUNION
    (is (= (unicode "123_abcUNION") "123_abcUNION"))
    (is (= (unicode "123_abcunion") "123_abcunion")))

(deftest DigitUnderscoreANYLetter
    (is (= (unicode "123_ANYabc") "123_ANYabc"))
    (is (= (unicode "123_anyabc") "123_anyabc")))

(deftest DigitUnderscoreFALSELetter
    (is (= (unicode "123_FALSEabc") "123_FALSEabc"))
    (is (= (unicode "123_falseabc") "123_falseabc")))

(deftest DigitUnderscoreINTEGERLetter
    (is (= (unicode "123_INTEGERabc") "123_INTEGERabc"))
    (is (= (unicode "123_integerabc") "123_integerabc")))

(deftest DigitUnderscoreINTERLetter
    (is (= (unicode "123_INTERabc") "123_INTERabc"))
    (is (= (unicode "123_interabc") "123_interabc")))

(deftest DigitUnderscoreNATLetter
    (is (= (unicode "123_NATabc") "123_NATabc"))
    (is (= (unicode "123_natabc") "123_natabc")))

(deftest DigitUnderscoreNAT1Letter
    (is (= (unicode "123_NAT1abc") "123_NAT1abc"))
    (is (= (unicode "123_nat1abc") "123_nat1abc")))

(deftest DigitUnderscoreNATURALLetter
    (is (= (unicode "123_NATURALabc") "123_NATURALabc"))
    (is (= (unicode "123_naturalabc") "123_naturalabc")))

(deftest DigitUnderscoreNOTLetter
    (is (= (unicode "123_NOTabc") "123_NOTabc"))
    (is (= (unicode "123_notabc") "123_notabc")))

(deftest DigitUnderscoreORLetter
    (is (= (unicode "123_ORabc") "123_ORabc"))
    (is (= (unicode "123_orabc") "123_orabc")))

(deftest DigitUnderscorePOWLetter
    (is (= (unicode "123_POWabc") "123_POWabc"))
    (is (= (unicode "123_powabc") "123_powabc")))

(deftest DigitUnderscorePOW1Letter
    (is (= (unicode "123_POW1abc") "123_POW1abc"))
    (is (= (unicode "123_pow1abc") "123_pow1abc")))

(deftest DigitUnderscoreTRUELetter
    (is (= (unicode "123_TRUEabc") "123_TRUEabc"))
    (is (= (unicode "123_trueabc") "123_trueabc")))

(deftest DigitUnderscoreUNIONLetter
    (is (= (unicode "123_UNIONabc") "123_UNIONabc"))
    (is (= (unicode "123_unionabc") "123_unionabc")))

(deftest DigitANYLetterUnderscore
    (is (= (unicode "123ANYabc_") "123ANYabc_"))
    (is (= (unicode "123anyabc_") "123anyabc_")))

(deftest DigitFALSELetterUnderscore
    (is (= (unicode "123FALSEabc_") "123FALSEabc_"))
    (is (= (unicode "123falseabc_") "123falseabc_")))

(deftest DigitINTEGERLetterUnderscore
    (is (= (unicode "123INTEGERabc_") "123INTEGERabc_"))
    (is (= (unicode "123integerabc_") "123integerabc_")))

(deftest DigitINTERLetterUnderscore
    (is (= (unicode "123INTERabc_") "123INTERabc_"))
    (is (= (unicode "123interabc_") "123interabc_")))

(deftest DigitNATLetterUnderscore
    (is (= (unicode "123NATabc_") "123NATabc_"))
    (is (= (unicode "123natabc_") "123natabc_")))

(deftest DigitNAT1LetterUnderscore
    (is (= (unicode "123NAT1abc_") "123NAT1abc_"))
    (is (= (unicode "123nat1abc_") "123nat1abc_")))

(deftest DigitNATURALLetterUnderscore
    (is (= (unicode "123NATURALabc_") "123NATURALabc_"))
    (is (= (unicode "123naturalabc_") "123naturalabc_")))

(deftest DigitNOTLetterUnderscore
    (is (= (unicode "123NOTabc_") "123NOTabc_"))
    (is (= (unicode "123notabc_") "123notabc_")))

(deftest DigitORLetterUnderscore
    (is (= (unicode "123ORabc_") "123ORabc_"))
    (is (= (unicode "123orabc_") "123orabc_")))

(deftest DigitPOWLetterUnderscore
    (is (= (unicode "123POWabc_") "123POWabc_"))
    (is (= (unicode "123powabc_") "123powabc_")))

(deftest DigitPOW1LetterUnderscore
    (is (= (unicode "123POW1abc_") "123POW1abc_"))
    (is (= (unicode "123pow1abc_") "123pow1abc_")))

(deftest DigitTRUELetterUnderscore
    (is (= (unicode "123TRUEabc_") "123TRUEabc_"))
    (is (= (unicode "123trueabc_") "123trueabc_")))

(deftest DigitUNIONLetterUnderscore
    (is (= (unicode "123UNIONabc_") "123UNIONabc_"))
    (is (= (unicode "123unionabc_") "123unionabc_")))

(deftest DigitANYUnderscoreLetter
    (is (= (unicode "123ANY_abc") "123ANY_abc"))
    (is (= (unicode "123any_abc") "123any_abc")))

(deftest DigitFALSEUnderscoreLetter
    (is (= (unicode "123FALSE_abc") "123FALSE_abc"))
    (is (= (unicode "123false_abc") "123false_abc")))

(deftest DigitINTEGERUnderscoreLetter
    (is (= (unicode "123INTEGER_abc") "123INTEGER_abc"))
    (is (= (unicode "123integer_abc") "123integer_abc")))

(deftest DigitINTERUnderscoreLetter
    (is (= (unicode "123INTER_abc") "123INTER_abc"))
    (is (= (unicode "123inter_abc") "123inter_abc")))

(deftest DigitNATUnderscoreLetter
    (is (= (unicode "123NAT_abc") "123NAT_abc"))
    (is (= (unicode "123nat_abc") "123nat_abc")))

(deftest DigitNAT1UnderscoreLetter
    (is (= (unicode "123NAT1_abc") "123NAT1_abc"))
    (is (= (unicode "123nat1_abc") "123nat1_abc")))

(deftest DigitNATURALUnderscoreLetter
    (is (= (unicode "123NATURAL_abc") "123NATURAL_abc"))
    (is (= (unicode "123natural_abc") "123natural_abc")))

(deftest DigitNOTUnderscoreLetter
    (is (= (unicode "123NOT_abc") "123NOT_abc"))
    (is (= (unicode "123not_abc") "123not_abc")))

(deftest DigitORUnderscoreLetter
    (is (= (unicode "123OR_abc") "123OR_abc"))
    (is (= (unicode "123or_abc") "123or_abc")))

(deftest DigitPOWUnderscoreLetter
    (is (= (unicode "123POW_abc") "123POW_abc"))
    (is (= (unicode "123pow_abc") "123pow_abc")))

(deftest DigitPOW1UnderscoreLetter
    (is (= (unicode "123POW1_abc") "123POW1_abc"))
    (is (= (unicode "123pow1_abc") "123pow1_abc")))

(deftest DigitTRUEUnderscoreLetter
    (is (= (unicode "123TRUE_abc") "123TRUE_abc"))
    (is (= (unicode "123true_abc") "123true_abc")))

(deftest DigitUNIONUnderscoreLetter
    (is (= (unicode "123UNION_abc") "123UNION_abc"))
    (is (= (unicode "123union_abc") "123union_abc")))

(deftest Underscore
    (is (= (unicode "_") "_")))

(deftest UnderscoreLetter
    (is (= (unicode "_abc") "_abc")))

(deftest UnderscoreDigit
    (is (= (unicode "_123") "_123")))

(deftest UnderscoreANY
    (is (= (unicode "_ANY") "_ANY"))
    (is (= (unicode "_any") "_any")))

(deftest UnderscoreFALSE
    (is (= (unicode "_FALSE") "_FALSE"))
    (is (= (unicode "_false") "_false")))

(deftest UnderscoreINTEGER
    (is (= (unicode "_INTEGER") "_INTEGER"))
    (is (= (unicode "_integer") "_integer")))

(deftest UnderscoreINTER
    (is (= (unicode "_INTER") "_INTER"))
    (is (= (unicode "_inter") "_inter")))

(deftest UnderscoreNAT
    (is (= (unicode "_NAT") "_NAT"))
    (is (= (unicode "_nat") "_nat")))

(deftest UnderscoreNAT1
    (is (= (unicode "_NAT1") "_NAT1"))
    (is (= (unicode "_nat1") "_nat1")))

(deftest UnderscoreNATURAL
    (is (= (unicode "_NATURAL") "_NATURAL"))
    (is (= (unicode "_natural") "_natural")))

(deftest UnderscoreNOT
    (is (= (unicode "_NOT") "_NOT"))
    (is (= (unicode "_not") "_not")))

(deftest UnderscoreOR
    (is (= (unicode "_OR") "_OR"))
    (is (= (unicode "_or") "_or")))

(deftest UnderscorePOW
    (is (= (unicode "_POW") "_POW"))
    (is (= (unicode "_pow") "_pow")))

(deftest UnderscorePOW1
    (is (= (unicode "_POW1") "_POW1"))
    (is (= (unicode "_pow1") "_pow1")))

(deftest UnderscoreTRUE
    (is (= (unicode "_TRUE") "_TRUE"))
    (is (= (unicode "_true") "_true")))

(deftest UnderscoreUNION
    (is (= (unicode "_UNION") "_UNION"))
    (is (= (unicode "_union") "_union")))

(deftest UnderscoreLetterDigit
    (is (= (unicode "_abc123") "_abc123")))

(deftest UnderscoreLetterANY
    (is (= (unicode "_123ANY") "_123ANY"))
    (is (= (unicode "_123any") "_123any")))

(deftest UnderscoreLetterFALSE
    (is (= (unicode "_123FALSE") "_123FALSE"))
    (is (= (unicode "_123false") "_123false")))

(deftest UnderscoreLetterINTEGER
    (is (= (unicode "_123INTEGER") "_123INTEGER"))
    (is (= (unicode "_123integer") "_123integer")))

(deftest UnderscoreLetterINTER
    (is (= (unicode "_123INTER") "_123INTER"))
    (is (= (unicode "_123inter") "_123inter")))

(deftest UnderscoreLetterNAT
    (is (= (unicode "_123NAT") "_123NAT"))
    (is (= (unicode "_123nat") "_123nat")))

(deftest UnderscoreLetterNAT1
    (is (= (unicode "_123NAT1") "_123NAT1"))
    (is (= (unicode "_123nat1") "_123nat1")))

(deftest UnderscoreLetterNATURAL
    (is (= (unicode "_123NATURAL") "_123NATURAL"))
    (is (= (unicode "_123natural") "_123natural")))

(deftest UnderscoreLetterNOT
    (is (= (unicode "_123NOT") "_123NOT"))
    (is (= (unicode "_123not") "_123not")))

(deftest UnderscoreLetterOR
    (is (= (unicode "_123OR") "_123OR"))
    (is (= (unicode "_123or") "_123or")))

(deftest UnderscoreLetterPOW
    (is (= (unicode "_123POW") "_123POW"))
    (is (= (unicode "_123pow") "_123pow")))

(deftest UnderscoreLetterPOW1
    (is (= (unicode "_123POW1") "_123POW1"))
    (is (= (unicode "_123pow1") "_123pow1")))

(deftest UnderscoreLetterTRUE
    (is (= (unicode "_123TRUE") "_123TRUE"))
    (is (= (unicode "_123true") "_123true")))

(deftest UnderscoreLetterUNION
    (is (= (unicode "_123UNION") "_123UNION"))
    (is (= (unicode "_123union") "_123union")))

(deftest UnderscoreDigitLetter
    (is (= (unicode "_123abc") "_123abc")))

(deftest UnderscoreDigitANY
    (is (= (unicode "_123ANY") "_123ANY"))
    (is (= (unicode "_123any") "_123any")))

(deftest UnderscoreDigitFALSE
    (is (= (unicode "_123FALSE") "_123FALSE"))
    (is (= (unicode "_123false") "_123false")))

(deftest UnderscoreDigitINTEGER
    (is (= (unicode "_123INTEGER") "_123INTEGER"))
    (is (= (unicode "_123integer") "_123integer")))

(deftest UnderscoreDigitINTER
    (is (= (unicode "_123INTER") "_123INTER"))
    (is (= (unicode "_123inter") "_123inter")))

(deftest UnderscoreDigitNAT
    (is (= (unicode "_123NAT") "_123NAT"))
    (is (= (unicode "_123nat") "_123nat")))

(deftest UnderscoreDigitNAT1
    (is (= (unicode "_123NAT1") "_123NAT1"))
    (is (= (unicode "_123nat1") "_123nat1")))

(deftest UnderscoreDigitNATURAL
    (is (= (unicode "_123NATURAL") "_123NATURAL"))
    (is (= (unicode "_123natural") "_123natural")))

(deftest UnderscoreDigitNOT
    (is (= (unicode "_123NOT") "_123NOT"))
    (is (= (unicode "_123not") "_123not")))

(deftest UnderscoreDigitOR
    (is (= (unicode "_123OR") "_123OR"))
    (is (= (unicode "_123or") "_123or")))

(deftest UnderscoreDigitPOW
    (is (= (unicode "_123POW") "_123POW"))
    (is (= (unicode "_123pow") "_123pow")))

(deftest UnderscoreDigitPOW1
    (is (= (unicode "_123POW1") "_123POW1"))
    (is (= (unicode "_123pow1") "_123pow1")))

(deftest UnderscoreDigitTRUE
    (is (= (unicode "_123TRUE") "_123TRUE"))
    (is (= (unicode "_123true") "_123true")))

(deftest UnderscoreDigitUNION
    (is (= (unicode "_123UNION") "_123UNION"))
    (is (= (unicode "_123union") "_123union")))

(deftest UnderscoreANYLetter
    (is (= (unicode "_ANYabc") "_ANYabc"))
    (is (= (unicode "_anyabc") "_anyabc")))

(deftest UnderscoreFALSELetter
    (is (= (unicode "_FALSEabc") "_FALSEabc"))
    (is (= (unicode "_falseabc") "_falseabc")))

(deftest UnderscoreINTEGERLetter
    (is (= (unicode "_INTEGERabc") "_INTEGERabc"))
    (is (= (unicode "_integerabc") "_integerabc")))

(deftest UnderscoreINTERLetter
    (is (= (unicode "_INTERabc") "_INTERabc"))
    (is (= (unicode "_interabc") "_interabc")))

(deftest UnderscoreNATLetter
    (is (= (unicode "_NATabc") "_NATabc"))
    (is (= (unicode "_natabc") "_natabc")))

(deftest UnderscoreNAT1Letter
    (is (= (unicode "_NAT1abc") "_NAT1abc"))
    (is (= (unicode "_nat1abc") "_nat1abc")))

(deftest UnderscoreNATURALLetter
    (is (= (unicode "_NATURALabc") "_NATURALabc"))
    (is (= (unicode "_naturalabc") "_naturalabc")))

(deftest UnderscoreNOTLetter
    (is (= (unicode "_NOTabc") "_NOTabc"))
    (is (= (unicode "_notabc") "_notabc")))

(deftest UnderscoreORLetter
    (is (= (unicode "_ORabc") "_ORabc"))
    (is (= (unicode "_orabc") "_orabc")))

(deftest UnderscorePOWLetter
    (is (= (unicode "_POWabc") "_POWabc"))
    (is (= (unicode "_powabc") "_powabc")))

(deftest UnderscorePOW1Letter
    (is (= (unicode "_POW1abc") "_POW1abc"))
    (is (= (unicode "_pow1abc") "_pow1abc")))

(deftest UnderscoreTRUELetter
    (is (= (unicode "_TRUEabc") "_TRUEabc"))
    (is (= (unicode "_trueabc") "_trueabc")))

(deftest UnderscoreUNIONLetter
    (is (= (unicode "_UNIONabc") "_UNIONabc"))
    (is (= (unicode "_unionabc") "_unionabc")))

(deftest UnderscoreANYDigit
    (is (= (unicode "_ANY123") "_ANY123"))
    (is (= (unicode "_any123") "_any123")))

(deftest UnderscoreFALSEDigit
    (is (= (unicode "_FALSE123") "_FALSE123"))
    (is (= (unicode "_false123") "_false123")))

(deftest UnderscoreINTEGERDigit
    (is (= (unicode "_INTEGER123") "_INTEGER123"))
    (is (= (unicode "_integer123") "_integer123")))

(deftest UnderscoreINTERDigit
    (is (= (unicode "_INTER123") "_INTER123"))
    (is (= (unicode "_inter123") "_inter123")))

(deftest UnderscoreNATDigit
    (is (= (unicode "_NAT123") "_NAT123"))
    (is (= (unicode "_nat123") "_nat123")))

(deftest UnderscoreNAT1Digit
    (is (= (unicode "_NAT1123") "_NAT1123"))
    (is (= (unicode "_nat1123") "_nat1123")))

(deftest UnderscoreNATURALDigit
    (is (= (unicode "_NATURAL123") "_NATURAL123"))
    (is (= (unicode "_natural123") "_natural123")))

(deftest UnderscoreNOTDigit
    (is (= (unicode "_NOT123") "_NOT123"))
    (is (= (unicode "_not123") "_not123")))

(deftest UnderscoreORDigit
    (is (= (unicode "_OR123") "_OR123"))
    (is (= (unicode "_or123") "_or123")))

(deftest UnderscorePOWDigit
    (is (= (unicode "_POW123") "_POW123"))
    (is (= (unicode "_pow123") "_pow123")))

(deftest UnderscorePOW1Digit
    (is (= (unicode "_POW1123") "_POW1123"))
    (is (= (unicode "_pow1123") "_pow1123")))

(deftest UnderscoreTRUEDigit
    (is (= (unicode "_TRUE123") "_TRUE123"))
    (is (= (unicode "_true123") "_true123")))

(deftest UnderscoreUNIONDigit
    (is (= (unicode "_UNION123") "_UNION123"))
    (is (= (unicode "_union123") "_union123")))

(deftest UnderscoreLetterDigitANY
    (is (= (unicode "_abc123ANY") "_abc123ANY"))
    (is (= (unicode "_abc123any") "_abc123any")))

(deftest UnderscoreLetterDigitFALSE
    (is (= (unicode "_abc123FALSE") "_abc123FALSE"))
    (is (= (unicode "_abc123false") "_abc123false")))

(deftest UnderscoreLetterDigitINTEGER
    (is (= (unicode "_abc123INTEGER") "_abc123INTEGER"))
    (is (= (unicode "_abc123integer") "_abc123integer")))

(deftest UnderscoreLetterDigitINTER
    (is (= (unicode "_abc123INTER") "_abc123INTER"))
    (is (= (unicode "_abc123inter") "_abc123inter")))

(deftest UnderscoreLetterDigitNAT
    (is (= (unicode "_abc123NAT") "_abc123NAT"))
    (is (= (unicode "_abc123nat") "_abc123nat")))

(deftest UnderscoreLetterDigitNAT1
    (is (= (unicode "_abc123NAT1") "_abc123NAT1"))
    (is (= (unicode "_abc123nat1") "_abc123nat1")))

(deftest UnderscoreLetterDigitNATURAL
    (is (= (unicode "_abc123NATURAL") "_abc123NATURAL"))
    (is (= (unicode "_abc123natural") "_abc123natural")))

(deftest UnderscoreLetterDigitNOT
    (is (= (unicode "_abc123NOT") "_abc123NOT"))
    (is (= (unicode "_abc123not") "_abc123not")))

(deftest UnderscoreLetterDigitOR
    (is (= (unicode "_abc123OR") "_abc123OR"))
    (is (= (unicode "_abc123or") "_abc123or")))

(deftest UnderscoreLetterDigitPOW
    (is (= (unicode "_abc123POW") "_abc123POW"))
    (is (= (unicode "_abc123pow") "_abc123pow")))

(deftest UnderscoreLetterDigitPOW1
    (is (= (unicode "_abc123POW1") "_abc123POW1"))
    (is (= (unicode "_abc123pow1") "_abc123pow1")))

(deftest UnderscoreLetterDigitTRUE
    (is (= (unicode "_abc123TRUE") "_abc123TRUE"))
    (is (= (unicode "_abc123true") "_abc123true")))

(deftest UnderscoreLetterDigitUNION
    (is (= (unicode "_abc123UNION") "_abc123UNION"))
    (is (= (unicode "_abc123union") "_abc123union")))

(deftest UnderscoreLetterANYDigit
    (is (= (unicode "_abcANY123") "_abcANY123"))
    (is (= (unicode "_abcany123") "_abcany123")))

(deftest UnderscoreLetterFALSEDigit
    (is (= (unicode "_abcFALSE123") "_abcFALSE123"))
    (is (= (unicode "_abcfalse123") "_abcfalse123")))

(deftest UnderscoreLetterINTEGERDigit
    (is (= (unicode "_abcINTEGER123") "_abcINTEGER123"))
    (is (= (unicode "_abcinteger123") "_abcinteger123")))

(deftest UnderscoreLetterINTERDigit
    (is (= (unicode "_abcINTER123") "_abcINTER123"))
    (is (= (unicode "_abcinter123") "_abcinter123")))

(deftest UnderscoreLetterNATDigit
    (is (= (unicode "_abcNAT123") "_abcNAT123"))
    (is (= (unicode "_abcnat123") "_abcnat123")))

(deftest UnderscoreLetterNAT1Digit
    (is (= (unicode "_abcNAT1123") "_abcNAT1123"))
    (is (= (unicode "_abcnat1123") "_abcnat1123")))

(deftest UnderscoreLetterNATURALDigit
    (is (= (unicode "_abcNATURAL123") "_abcNATURAL123"))
    (is (= (unicode "_abcnatural123") "_abcnatural123")))

(deftest UnderscoreLetterNOTDigit
    (is (= (unicode "_abcNOT123") "_abcNOT123"))
    (is (= (unicode "_abcnot123") "_abcnot123")))

(deftest UnderscoreLetterORDigit
    (is (= (unicode "_abcOR123") "_abcOR123"))
    (is (= (unicode "_abcor123") "_abcor123")))

(deftest UnderscoreLetterPOWDigit
    (is (= (unicode "_abcPOW123") "_abcPOW123"))
    (is (= (unicode "_abcpow123") "_abcpow123")))

(deftest UnderscoreLetterPOW1Digit
    (is (= (unicode "_abcPOW1123") "_abcPOW1123"))
    (is (= (unicode "_abcpow1123") "_abcpow1123")))

(deftest UnderscoreLetterTRUEDigit
    (is (= (unicode "_abcTRUE123") "_abcTRUE123"))
    (is (= (unicode "_abctrue123") "_abctrue123")))

(deftest UnderscoreLetterUNIONDigit
    (is (= (unicode "_abcUNION123") "_abcUNION123"))
    (is (= (unicode "_abcunion123") "_abcunion123")))

(deftest UnderscoreDigitLetterANY
    (is (= (unicode "_123abcANY") "_123abcANY"))
    (is (= (unicode "_123abcany") "_123abcany")))

(deftest UnderscoreDigitLetterFALSE
    (is (= (unicode "_123abcFALSE") "_123abcFALSE"))
    (is (= (unicode "_123abcfalse") "_123abcfalse")))

(deftest UnderscoreDigitLetterINTEGER
    (is (= (unicode "_123abcINTEGER") "_123abcINTEGER"))
    (is (= (unicode "_123abcinteger") "_123abcinteger")))

(deftest UnderscoreDigitLetterINTER
    (is (= (unicode "_123abcINTER") "_123abcINTER"))
    (is (= (unicode "_123abcinter") "_123abcinter")))

(deftest UnderscoreDigitLetterNAT
    (is (= (unicode "_123abcNAT") "_123abcNAT"))
    (is (= (unicode "_123abcnat") "_123abcnat")))

(deftest UnderscoreDigitLetterNAT1
    (is (= (unicode "_123abcNAT1") "_123abcNAT1"))
    (is (= (unicode "_123abcnat1") "_123abcnat1")))

(deftest UnderscoreDigitLetterNATURAL
    (is (= (unicode "_123abcNATURAL") "_123abcNATURAL"))
    (is (= (unicode "_123abcnatural") "_123abcnatural")))

(deftest UnderscoreDigitLetterNOT
    (is (= (unicode "_123abcNOT") "_123abcNOT"))
    (is (= (unicode "_123abcnot") "_123abcnot")))

(deftest UnderscoreDigitLetterOR
    (is (= (unicode "_123abcOR") "_123abcOR"))
    (is (= (unicode "_123abcor") "_123abcor")))

(deftest UnderscoreDigitLetterPOW
    (is (= (unicode "_123abcPOW") "_123abcPOW"))
    (is (= (unicode "_123abcpow") "_123abcpow")))

(deftest UnderscoreDigitLetterPOW1
    (is (= (unicode "_123abcPOW1") "_123abcPOW1"))
    (is (= (unicode "_123abcpow1") "_123abcpow1")))

(deftest UnderscoreDigitLetterTRUE
    (is (= (unicode "_123abcTRUE") "_123abcTRUE"))
    (is (= (unicode "_123abctrue") "_123abctrue")))

(deftest UnderscoreDigitLetterUNION
    (is (= (unicode "_123abcUNION") "_123abcUNION"))
    (is (= (unicode "_123abcunion") "_123abcunion")))

(deftest UnderscoreDigitANYLetter
    (is (= (unicode "_123ANYabc") "_123ANYabc"))
    (is (= (unicode "_123anyabc") "_123anyabc")))

(deftest UnderscoreDigitFALSELetter
    (is (= (unicode "_123FALSEabc") "_123FALSEabc"))
    (is (= (unicode "_123falseabc") "_123falseabc")))

(deftest UnderscoreDigitINTEGERLetter
    (is (= (unicode "_123INTEGERabc") "_123INTEGERabc"))
    (is (= (unicode "_123integerabc") "_123integerabc")))

(deftest UnderscoreDigitINTERLetter
    (is (= (unicode "_123INTERabc") "_123INTERabc"))
    (is (= (unicode "_123interabc") "_123interabc")))

(deftest UnderscoreDigitNATLetter
    (is (= (unicode "_123NATabc") "_123NATabc"))
    (is (= (unicode "_123natabc") "_123natabc")))

(deftest UnderscoreDigitNAT1Letter
    (is (= (unicode "_123NAT1abc") "_123NAT1abc"))
    (is (= (unicode "_123nat1abc") "_123nat1abc")))

(deftest UnderscoreDigitNATURALLetter
    (is (= (unicode "_123NATURALabc") "_123NATURALabc"))
    (is (= (unicode "_123naturalabc") "_123naturalabc")))

(deftest UnderscoreDigitNOTLetter
    (is (= (unicode "_123NOTabc") "_123NOTabc"))
    (is (= (unicode "_123notabc") "_123notabc")))

(deftest UnderscoreDigitORLetter
    (is (= (unicode "_123ORabc") "_123ORabc"))
    (is (= (unicode "_123orabc") "_123orabc")))

(deftest UnderscoreDigitPOWLetter
    (is (= (unicode "_123POWabc") "_123POWabc"))
    (is (= (unicode "_123powabc") "_123powabc")))

(deftest UnderscoreDigitPOW1Letter
    (is (= (unicode "_123POW1abc") "_123POW1abc"))
    (is (= (unicode "_123pow1abc") "_123pow1abc")))

(deftest UnderscoreDigitTRUELetter
    (is (= (unicode "_123TRUEabc") "_123TRUEabc"))
    (is (= (unicode "_123trueabc") "_123trueabc")))

(deftest UnderscoreDigitUNIONLetter
    (is (= (unicode "_123UNIONabc") "_123UNIONabc"))
    (is (= (unicode "_123unionabc") "_123unionabc")))

(deftest UnderscoreANYLetterDigit
    (is (= (unicode "_ANYabc123") "_ANYabc123"))
    (is (= (unicode "_anyabc123") "_anyabc123")))

(deftest UnderscoreFALSELetterDigit
    (is (= (unicode "_FALSEabc123") "_FALSEabc123"))
    (is (= (unicode "_falseabc123") "_falseabc123")))

(deftest UnderscoreINTEGERLetterDigit
	(is (= (unicode "_INTEGERabc123") "_INTEGERabc123"))
	(is (= (unicode "_integerabc123") "_integerabc123")))

(deftest UnderscoreINTERLetterDigit
    (is (= (unicode "_INTERabc123") "_INTERabc123"))
    (is (= (unicode "_interabc123") "_interabc123")))

(deftest UnderscoreNATLetterDigit
    (is (= (unicode "_NATabc123") "_NATabc123"))
    (is (= (unicode "_natabc123") "_natabc123")))

(deftest UnderscoreNAT1LetterDigit
    (is (= (unicode "_NAT1abc123") "_NAT1abc123"))
    (is (= (unicode "_nat1abc123") "_nat1abc123")))

(deftest UnderscoreNATURALLetterDigit
    (is (= (unicode "_NATURALabc123") "_NATURALabc123"))
    (is (= (unicode "_naturalabc123") "_naturalabc123")))

(deftest UnderscoreNOTLetterDigit
    (is (= (unicode "_NOTabc123") "_NOTabc123"))
    (is (= (unicode "_notabc123") "_notabc123")))

(deftest UnderscoreORLetterDigit
    (is (= (unicode "_ORabc123") "_ORabc123"))
    (is (= (unicode "_orabc123") "_orabc123")))

(deftest UnderscorePOWLetterDigit
    (is (= (unicode "_POWabc123") "_POWabc123"))
    (is (= (unicode "_powabc123") "_powabc123")))

(deftest UnderscorePOW1LetterDigit
    (is (= (unicode "_POW1abc123") "_POW1abc123"))
    (is (= (unicode "_pow1abc123") "_pow1abc123")))

(deftest UnderscoreTRUELetterDigit
    (is (= (unicode "_TRUEabc123") "_TRUEabc123"))
    (is (= (unicode "_trueabc123") "_trueabc123")))

(deftest UnderscoreUNIONLetterDigit
    (is (= (unicode "_UNIONabc123") "_UNIONabc123"))
    (is (= (unicode "_unionabc123") "_unionabc123")))

(deftest UnderscoreANYDigitLetter
    (is (= (unicode "_ANY123abc") "_ANY123abc"))
    (is (= (unicode "_any123abc") "_any123abc")))

(deftest UnderscoreFALSEDigitLetter
    (is (= (unicode "_FALSE123abc") "_FALSE123abc"))
    (is (= (unicode "_false123abc") "_false123abc")))

(deftest UnderscoreINTEGERDigitLetter
    (is (= (unicode "_INTEGER123abc") "_INTEGER123abc"))
    (is (= (unicode "_integer123abc") "_integer123abc")))

(deftest UnderscoreINTERDigitLetter
    (is (= (unicode "_INTER123abc") "_INTER123abc"))
    (is (= (unicode "_inter123abc") "_inter123abc")))

(deftest UnderscoreNATDigitLetter
    (is (= (unicode "_NAT123abc") "_NAT123abc"))
    (is (= (unicode "_nat123abc") "_nat123abc")))

(deftest UnderscoreNAT1DigitLetter
    (is (= (unicode "_NAT1123abc") "_NAT1123abc"))
    (is (= (unicode "_nat1123abc") "_nat1123abc")))

(deftest UnderscoreNATURALDigitLetter
    (is (= (unicode "_NATURAL123abc") "_NATURAL123abc"))
    (is (= (unicode "_natural123abc") "_natural123abc")))

(deftest UnderscoreNOTDigitLetter
    (is (= (unicode "_NOT123abc") "_NOT123abc"))
    (is (= (unicode "_not123abc") "_not123abc")))

(deftest UnderscoreORDigitLetter
    (is (= (unicode "_OR123abc") "_OR123abc"))
    (is (= (unicode "_or123abc") "_or123abc")))

(deftest UnderscorePOWDigitLetter
    (is (= (unicode "_POW123abc") "_POW123abc"))
    (is (= (unicode "_pow123abc") "_pow123abc")))

(deftest UnderscorePOW1DigitLetter
    (is (= (unicode "_POW1123abc") "_POW1123abc"))
    (is (= (unicode "_pow1123abc") "_pow1123abc")))

(deftest UnderscoreTRUEDigitLetter
    (is (= (unicode "_TRUE123abc") "_TRUE123abc"))
    (is (= (unicode "_true123abc") "_true123abc")))

(deftest UnderscoreUNIONDigitLetter
    (is (= (unicode "_UNION123abc") "_UNION123abc"))
    (is (= (unicode "_union123abc") "_union123abc")))

(deftest Keyword
	(is (= (unicode "ANY") "ANY")))

(deftest ANYLetter
    (is (= (unicode "ANYabc") "ANYabc"))
    (is (= (unicode "anyabc") "anyabc")))

(deftest FALSELetter
    (is (= (unicode "FALSEabc") "FALSEabc"))
    (is (= (unicode "falseabc") "falseabc")))

(deftest INTEGERLetter
    (is (= (unicode "INTEGERabc") "INTEGERabc"))
    (is (= (unicode "integerabc") "integerabc")))

(deftest INTERLetter
    (is (= (unicode "INTERabc") "INTERabc"))
    (is (= (unicode "interabc") "interabc")))

(deftest NATLetter
    (is (= (unicode "NATabc") "NATabc"))
    (is (= (unicode "natabc") "natabc")))

(deftest NAT1Letter
    (is (= (unicode "NAT1abc") "NAT1abc"))
    (is (= (unicode "nat1abc") "nat1abc")))

(deftest NATURALLetter
    (is (= (unicode "NATURALabc") "NATURALabc"))
    (is (= (unicode "naturalabc") "naturalabc")))

(deftest NOTLetter
    (is (= (unicode "NOTabc") "NOTabc"))
    (is (= (unicode "notabc") "notabc")))

(deftest ORLetter
    (is (= (unicode "ORabc") "ORabc"))
    (is (= (unicode "orabc") "orabc")))

(deftest POWLetter
    (is (= (unicode "POWabc") "POWabc"))
    (is (= (unicode "powabc") "powabc")))

(deftest POW1Letter
    (is (= (unicode "POW1abc") "POW1abc"))
    (is (= (unicode "pow1abc") "pow1abc")))

(deftest TRUELetter
    (is (= (unicode "TRUEabc") "TRUEabc"))
    (is (= (unicode "trueabc") "trueabc")))

(deftest UNIONLetter
    (is (= (unicode "UNIONabc") "UNIONabc"))
    (is (= (unicode "unionabc") "unionabc")))

(deftest ANYDigit
    (is (= (unicode "ANY123") "ANY123"))
    (is (= (unicode "any123") "any123")))

(deftest FALSEDigit
    (is (= (unicode "FALSE123") "FALSE123"))
    (is (= (unicode "false123") "false123")))

(deftest INTEGERDigit
    (is (= (unicode "INTEGER123") "INTEGER123"))
    (is (= (unicode "integer123") "integer123")))

(deftest INTERDigit
    (is (= (unicode "INTER123") "INTER123"))
    (is (= (unicode "inter123") "inter123")))

(deftest NATDigit
    (is (= (unicode "NAT123") "NAT123"))
    (is (= (unicode "nat123") "nat123")))

(deftest NAT1Digit
    (is (= (unicode "NAT1123") "NAT1123"))
    (is (= (unicode "nat1123") "nat1123")))

(deftest NATURALDigit
    (is (= (unicode "NATURAL123") "NATURAL123"))
    (is (= (unicode "natural123") "natural123")))

(deftest NOTDigit
    (is (= (unicode "NOT123") "NOT123"))
    (is (= (unicode "not123") "not123")))

(deftest ORDigit
    (is (= (unicode "OR123") "OR123"))
    (is (= (unicode "or123") "or123")))

(deftest POWDigit
    (is (= (unicode "POW123") "POW123"))
    (is (= (unicode "pow123") "pow123")))

(deftest POW1Digit
    (is (= (unicode "POW1123") "POW1123"))
    (is (= (unicode "pow1123") "pow1123")))

(deftest TRUEDigit
    (is (= (unicode "TRUE123") "TRUE123"))
    (is (= (unicode "true123") "true123")))

(deftest UNIONDigit
    (is (= (unicode "UNION123") "UNION123"))
    (is (= (unicode "union123") "union123")))

(deftest ANYUnderscore
    (is (= (unicode "ANY_") "ANY_"))
    (is (= (unicode "any_") "any_")))

(deftest FALSEUnderscore
    (is (= (unicode "FALSE_") "FALSE_"))
    (is (= (unicode "false_") "false_")))

(deftest INTEGERUnderscore
    (is (= (unicode "INTEGER_") "INTEGER_"))
    (is (= (unicode "integer_") "integer_")))

(deftest INTERUnderscore
    (is (= (unicode "INTER_") "INTER_"))
    (is (= (unicode "inter_") "inter_")))

(deftest NATUnderscore
    (is (= (unicode "NAT_") "NAT_"))
    (is (= (unicode "nat_") "nat_")))

(deftest NAT1Underscore
    (is (= (unicode "NAT1_") "NAT1_"))
    (is (= (unicode "nat1_") "nat1_")))

(deftest NATURALUnderscore
    (is (= (unicode "NATURAL_") "NATURAL_"))
    (is (= (unicode "natural_") "natural_")))

(deftest NOTUnderscore
    (is (= (unicode "NOT_") "NOT_"))
    (is (= (unicode "not_") "not_")))

(deftest ORUnderscore
    (is (= (unicode "OR_") "OR_"))
    (is (= (unicode "or_") "or_")))

(deftest POWUnderscore
    (is (= (unicode "POW_") "POW_"))
    (is (= (unicode "pow_") "pow_")))

(deftest POW1Underscore
    (is (= (unicode "POW1_") "POW1_"))
    (is (= (unicode "pow1_") "pow1_")))

(deftest TRUEUnderscore
    (is (= (unicode "TRUE_") "TRUE_"))
    (is (= (unicode "true_") "true_")))

(deftest UNIONUnderscore
    (is (= (unicode "UNION_") "UNION_"))
    (is (= (unicode "union_") "union_")))

(deftest ANYLetterDigit
    (is (= (unicode "ANYabc123") "ANYabc123"))
    (is (= (unicode "anyabc123") "anyabc123")))

(deftest FALSELetterDigit
    (is (= (unicode "FALSEabc123") "FALSEabc123"))
    (is (= (unicode "falseabc123") "falseabc123")))

(deftest INTEGERLetterDigit
    (is (= (unicode "INTEGERabc123") "INTEGERabc123"))
    (is (= (unicode "integerabc123") "integerabc123")))

(deftest INTERLetterDigit
    (is (= (unicode "INTERabc123") "INTERabc123"))
    (is (= (unicode "interabc123") "interabc123")))

(deftest NATLetterDigit
    (is (= (unicode "NATabc123") "NATabc123"))
    (is (= (unicode "natabc123") "natabc123")))

(deftest NAT1LetterDigit
    (is (= (unicode "NAT1abc123") "NAT1abc123"))
    (is (= (unicode "nat1abc123") "nat1abc123")))

(deftest NATURALLetterDigit
    (is (= (unicode "NATURALabc123") "NATURALabc123"))
    (is (= (unicode "naturalabc123") "naturalabc123")))

(deftest NOTLetterDigit
    (is (= (unicode "NOTabc123") "NOTabc123"))
    (is (= (unicode "notabc123") "notabc123")))

(deftest ORLetterDigit
    (is (= (unicode "ORabc123") "ORabc123"))
    (is (= (unicode "orabc123") "orabc123")))

(deftest POWLetterDigit
    (is (= (unicode "POWabc123") "POWabc123"))
    (is (= (unicode "powabc123") "powabc123")))

(deftest POW1LetterDigit
    (is (= (unicode "POW1abc123") "POW1abc123"))
    (is (= (unicode "pow1abc123") "pow1abc123")))

(deftest TRUELetterDigit
    (is (= (unicode "TRUEabc123") "TRUEabc123"))
    (is (= (unicode "trueabc123") "trueabc123")))

(deftest UNIONLetterDigit
    (is (= (unicode "UNIONabc123") "UNIONabc123"))
    (is (= (unicode "unionabc123") "unionabc123")))

(deftest ANYLetterUnderscore
    (is (= (unicode "ANYabc_") "ANYabc_"))
    (is (= (unicode "anyabc_") "anyabc_")))

(deftest FALSELetterUnderscore
    (is (= (unicode "FALSEabc_") "FALSEabc_"))
    (is (= (unicode "falseabc_") "falseabc_")))

(deftest INTEGERLetterUnderscore
    (is (= (unicode "INTEGERabc_") "INTEGERabc_"))
    (is (= (unicode "integerabc_") "integerabc_")))

(deftest INTERLetterUnderscore
    (is (= (unicode "INTERabc_") "INTERabc_"))
    (is (= (unicode "interabc_") "interabc_")))

(deftest NATLetterUnderscore
    (is (= (unicode "NATabc_") "NATabc_"))
    (is (= (unicode "natabc_") "natabc_")))

(deftest NAT1LetterUnderscore
    (is (= (unicode "NAT1abc_") "NAT1abc_"))
    (is (= (unicode "nat1abc_") "nat1abc_")))

(deftest NATURALLetterUnderscore
    (is (= (unicode "NATURALabc_") "NATURALabc_"))
    (is (= (unicode "naturalabc_") "naturalabc_")))

(deftest NOTLetterUnderscore
    (is (= (unicode "NOTabc_") "NOTabc_"))
    (is (= (unicode "notabc_") "notabc_")))

(deftest ORLetterUnderscore
    (is (= (unicode "ORabc_") "ORabc_"))
    (is (= (unicode "orabc_") "orabc_")))

(deftest POWLetterUnderscore
    (is (= (unicode "POWabc_") "POWabc_"))
    (is (= (unicode "powabc_") "powabc_")))

(deftest POW1LetterUnderscore
    (is (= (unicode "POW1abc_") "POW1abc_"))
    (is (= (unicode "pow1abc_") "pow1abc_")))

(deftest TRUELetterUnderscore
    (is (= (unicode "TRUEabc_") "TRUEabc_"))
    (is (= (unicode "trueabc_") "trueabc_")))

(deftest UNIONLetterUnderscore
    (is (= (unicode "UNIONabc_") "UNIONabc_"))
    (is (= (unicode "unionabc_") "unionabc_")))

(deftest ANYDigitLetter
    (is (= (unicode "ANY123abc") "ANY123abc"))
    (is (= (unicode "any123abc") "any123abc")))

(deftest FALSEDigitLetter
    (is (= (unicode "FALSE123abc") "FALSE123abc"))
    (is (= (unicode "false123abc") "false123abc")))

(deftest INTEGERDigitLetter
    (is (= (unicode "INTEGER123abc") "INTEGER123abc"))
    (is (= (unicode "integer123abc") "integer123abc")))

(deftest INTERDigitLetter
    (is (= (unicode "INTER123abc") "INTER123abc"))
    (is (= (unicode "inter123abc") "inter123abc")))

(deftest NATDigitLetter
    (is (= (unicode "NAT123abc") "NAT123abc"))
    (is (= (unicode "nat123abc") "nat123abc")))

(deftest NAT1DigitLetter
    (is (= (unicode "NAT1123abc") "NAT1123abc"))
    (is (= (unicode "nat1123abc") "nat1123abc")))

(deftest NATURALDigitLetter
    (is (= (unicode "NATURAL123abc") "NATURAL123abc"))
    (is (= (unicode "natural123abc") "natural123abc")))

(deftest NOTDigitLetter
    (is (= (unicode "not123abc") "not123abc"))
    (is (= (unicode "NOT123abc") "NOT123abc")))

(deftest ORDigitLetter
    (is (= (unicode "or123abc") "or123abc"))
    (is (= (unicode "OR123abc") "OR123abc")))

(deftest POWDigitLetter
    (is (= (unicode "POW123abc") "POW123abc"))
    (is (= (unicode "pow123abc") "pow123abc")))

(deftest POW1DigitLetter
    (is (= (unicode "POW1123abc") "POW1123abc"))
    (is (= (unicode "pow1123abc") "pow1123abc")))

(deftest TRUEDigitLetter
    (is (= (unicode "TRUE123abc") "TRUE123abc"))
    (is (= (unicode "true123abc") "true123abc")))

(deftest UNIONDigitLetter
    (is (= (unicode "UNION123abc") "UNION123abc"))
    (is (= (unicode "union123abc") "union123abc")))

(deftest ANYDigitUnderscore
    (is (= (unicode "ANY123_") "ANY123_"))
    (is (= (unicode "any123_") "any123_")))

(deftest FALSEDigitUnderscore
    (is (= (unicode "FALSE123_") "FALSE123_"))
    (is (= (unicode "false123_") "false123_")))

(deftest INTEGERDigitUnderscore
    (is (= (unicode "INTEGER123_") "INTEGER123_"))
    (is (= (unicode "integer123_") "integer123_")))

(deftest INTERDigitUnderscore
    (is (= (unicode "INTER123_") "INTER123_"))
    (is (= (unicode "inter123_") "inter123_")))

(deftest NATDigitUnderscore
    (is (= (unicode "NAT123_") "NAT123_"))
    (is (= (unicode "nat123_") "nat123_")))

(deftest NAT1DigitUnderscore
    (is (= (unicode "NAT1123_") "NAT1123_"))
    (is (= (unicode "nat1123_") "nat1123_")))

(deftest NATURALDigitUnderscore
    (is (= (unicode "NATURAL123_") "NATURAL123_"))
    (is (= (unicode "natural123_") "natural123_")))

(deftest NOTDigitUnderscore
    (is (= (unicode "NOT123_") "NOT123_"))
    (is (= (unicode "not123_") "not123_")))

(deftest ORDigitUnderscore
    (is (= (unicode "OR123_") "OR123_"))
    (is (= (unicode "or123_") "or123_")))

(deftest POWDigitUnderscore
    (is (= (unicode "POW123_") "POW123_"))
    (is (= (unicode "pow123_") "pow123_")))

(deftest POW1DigitUnderscore
    (is (= (unicode "POW1123_") "POW1123_"))
    (is (= (unicode "pow1123_") "pow1123_")))

(deftest TRUEDigitUnderscore
    (is (= (unicode "TRUE123_") "TRUE123_"))
    (is (= (unicode "true123_") "true123_")))

(deftest UNIONDigitUnderscore
    (is (= (unicode "UNION123_") "UNION123_"))
    (is (= (unicode "union123_") "union123_")))

(deftest ANYUnderscoreLetter
    (is (= (unicode "ANY_abc") "ANY_abc"))
    (is (= (unicode "any_abc") "any_abc")))

(deftest FALSEUnderscoreLetter
    (is (= (unicode "FALSE_abc") "FALSE_abc"))
    (is (= (unicode "false_abc") "false_abc")))

(deftest INTEGERUnderscoreLetter
    (is (= (unicode "INTEGER_abc") "INTEGER_abc"))
    (is (= (unicode "integer_abc") "integer_abc")))

(deftest INTERUnderscoreLetter
    (is (= (unicode "INTER_abc") "INTER_abc"))
    (is (= (unicode "inter_abc") "inter_abc")))

(deftest NATUnderscoreLetter
    (is (= (unicode "NAT_abc") "NAT_abc"))
    (is (= (unicode "nat_abc") "nat_abc")))

(deftest NAT1UnderscoreLetter
    (is (= (unicode "NAT1_abc") "NAT1_abc"))
    (is (= (unicode "nat1_abc") "nat1_abc")))

(deftest NATURALUnderscoreLetter
    (is (= (unicode "NATURAL_abc") "NATURAL_abc"))
    (is (= (unicode "natural_abc") "natural_abc")))

(deftest NOTUnderscoreLetter
    (is (= (unicode "NOT_abc") "NOT_abc"))
    (is (= (unicode "not_abc") "not_abc")))

(deftest ORUnderscoreLetter
    (is (= (unicode "OR_abc") "OR_abc"))
    (is (= (unicode "or_abc") "or_abc")))

(deftest POWUnderscoreLetter
    (is (= (unicode "POW_abc") "POW_abc"))
    (is (= (unicode "pow_abc") "pow_abc")))

(deftest POW1UnderscoreLetter
    (is (= (unicode "POW1_abc") "POW1_abc"))
    (is (= (unicode "pow1_abc") "pow1_abc")))

(deftest TRUEUnderscoreLetter
    (is (= (unicode "TRUE_abc") "TRUE_abc"))
    (is (= (unicode "true_abc") "true_abc")))

(deftest UNIONUnderscoreLetter
    (is (= (unicode "UNION_abc") "UNION_abc"))
    (is (= (unicode "union_abc") "union_abc")))

(deftest ANYUnderscoreDigit
    (is (= (unicode "ANY_123") "ANY_123"))
    (is (= (unicode "any_123") "any_123")))

(deftest FALSEUnderscoreDigit
    (is (= (unicode "FALSE_123") "FALSE_123"))
    (is (= (unicode "false_123") "false_123")))

(deftest INTEGERUnderscoreDigit
    (is (= (unicode "INTEGER_123") "INTEGER_123"))
    (is (= (unicode "integer_123") "integer_123")))

(deftest INTERUnderscoreDigit
    (is (= (unicode "INTER_123") "INTER_123"))
    (is (= (unicode "inter_123") "inter_123")))

(deftest NATUnderscoreDigit
    (is (= (unicode "NAT_123") "NAT_123"))
    (is (= (unicode "nat_123") "nat_123")))

(deftest NAT1UnderscoreDigit
    (is (= (unicode "NAT1_123") "NAT1_123"))
    (is (= (unicode "nat1_123") "nat1_123")))

(deftest NATURALUnderscoreDigit
    (is (= (unicode "NATURAL_123") "NATURAL_123"))
    (is (= (unicode "natural_123") "natural_123")))

(deftest NOTUnderscoreDigit
    (is (= (unicode "NOT_123") "NOT_123"))
    (is (= (unicode "not_123") "not_123")))

(deftest ORUnderscoreDigit
    (is (= (unicode "OR_123") "OR_123"))
    (is (= (unicode "or_123") "or_123")))

(deftest POWUnderscoreDigit
    (is (= (unicode "POW_123") "POW_123"))
    (is (= (unicode "pow_123") "pow_123")))

(deftest POW1UnderscoreDigit
    (is (= (unicode "POW1_123") "POW1_123"))
    (is (= (unicode "pow1_123") "pow1_123")))

(deftest TRUEUnderscoreDigit
    (is (= (unicode "TRUE_123") "TRUE_123"))
    (is (= (unicode "true_123") "true_123")))

(deftest UNIONUnderscoreDigit
    (is (= (unicode "UNION_123") "UNION_123"))
    (is (= (unicode "union_123") "union_123")))

(deftest ANYLetterDigitUnderscore
    (is (= (unicode "ANYabc123_") "ANYabc123_"))
    (is (= (unicode "anyabc123_") "anyabc123_")))

(deftest FALSELetterDigitUnderscore
    (is (= (unicode "FALSEabc123_") "FALSEabc123_"))
    (is (= (unicode "falseabc123_") "falseabc123_")))

(deftest INTEGERLetterDigitUnderscore
    (is (= (unicode "INTEGERabc123_") "INTEGERabc123_"))
    (is (= (unicode "integerabc123_") "integerabc123_")))

(deftest INTERLetterDigitUnderscore
    (is (= (unicode "INTERabc123_") "INTERabc123_"))
    (is (= (unicode "interabc123_") "interabc123_")))

(deftest NATLetterDigitUnderscore
    (is (= (unicode "NATabc123_") "NATabc123_"))
    (is (= (unicode "natabc123_") "natabc123_")))

(deftest NAT1LetterDigitUnderscore
    (is (= (unicode "NAT1abc123_") "NAT1abc123_"))
    (is (= (unicode "nat1abc123_") "nat1abc123_")))

(deftest NATURALLetterDigitUnderscore
    (is (= (unicode "NATURALabc123_") "NATURALabc123_"))
    (is (= (unicode "naturalabc123_") "naturalabc123_")))

(deftest NOTLetterDigitUnderscore
    (is (= (unicode "NOTabc123_") "NOTabc123_"))
    (is (= (unicode "notabc123_") "notabc123_")))

(deftest ORLetterDigitUnderscore
    (is (= (unicode "ORabc123_") "ORabc123_"))
    (is (= (unicode "orabc123_") "orabc123_")))

(deftest POWLetterDigitUnderscore
    (is (= (unicode "POWabc123_") "POWabc123_"))
    (is (= (unicode "powabc123_") "powabc123_")))

(deftest POW1LetterDigitUnderscore
    (is (= (unicode "POW1abc123_") "POW1abc123_"))
    (is (= (unicode "pow1abc123_") "pow1abc123_")))

(deftest TRUELetterDigitUnderscore
    (is (= (unicode "TRUEabc123_") "TRUEabc123_"))
    (is (= (unicode "trueabc123_") "trueabc123_")))

(deftest UNIONLetterDigitUnderscore
    (is (= (unicode "UNIONabc123_") "UNIONabc123_"))
    (is (= (unicode "unionabc123_") "unionabc123_")))

(deftest ANYLetterUnderscoreDigit
    (is (= (unicode "ANYabc_123") "ANYabc_123"))
    (is (= (unicode "anyabc_123") "anyabc_123")))

(deftest FALSELetterUnderscoreDigit
    (is (= (unicode "FALSEabc_123") "FALSEabc_123"))
    (is (= (unicode "falseabc_123") "falseabc_123")))

(deftest INTEGERLetterUnderscoreDigit
    (is (= (unicode "INTEGERabc_123") "INTEGERabc_123"))
    (is (= (unicode "integerabc_123") "integerabc_123")))

(deftest INTERLetterUnderscoreDigit
    (is (= (unicode "INTERabc_123") "INTERabc_123"))
    (is (= (unicode "interabc_123") "interabc_123")))

(deftest NATLetterUnderscoreDigit
    (is (= (unicode "NATabc_123") "NATabc_123"))
    (is (= (unicode "natabc_123") "natabc_123")))

(deftest NAT1LetterUnderscoreDigit
    (is (= (unicode "NAT1abc_123") "NAT1abc_123"))
    (is (= (unicode "nat1abc_123") "nat1abc_123")))

(deftest NATURALLetterUnderscoreDigit
    (is (= (unicode "NATURALabc_123") "NATURALabc_123"))
    (is (= (unicode "naturalabc_123") "naturalabc_123")))

(deftest NOTLetterUnderscoreDigit
    (is (= (unicode "NOTabc_123") "NOTabc_123"))
    (is (= (unicode "notabc_123") "notabc_123")))

(deftest ORLetterUnderscoreDigit
    (is (= (unicode "ORabc_123") "ORabc_123"))
    (is (= (unicode "orabc_123") "orabc_123")))

(deftest POWLetterUnderscoreDigit
    (is (= (unicode "POWabc_123") "POWabc_123"))
    (is (= (unicode "powabc_123") "powabc_123")))

(deftest POW1LetterUnderscoreDigit
    (is (= (unicode "POW1abc_123") "POW1abc_123"))
    (is (= (unicode "pow1abc_123") "pow1abc_123")))

(deftest TRUELetterUnderscoreDigit
    (is (= (unicode "TRUEabc_123") "TRUEabc_123"))
    (is (= (unicode "trueabc_123") "trueabc_123")))

(deftest UNIONLetterUnderscoreDigit
    (is (= (unicode "UNIONabc_123") "UNIONabc_123"))
    (is (= (unicode "unionabc_123") "unionabc_123")))

(deftest ANYDigitLetterUnderscore
    (is (= (unicode "ANY123abc_") "ANY123abc_"))
    (is (= (unicode "any123abc_") "any123abc_")))

(deftest FALSEDigitLetterUnderscore
    (is (= (unicode "FALSE123abc_") "FALSE123abc_"))
    (is (= (unicode "false123abc_") "false123abc_")))

(deftest INTEGERDigitLetterUnderscore
    (is (= (unicode "INTEGER123abc_") "INTEGER123abc_"))
    (is (= (unicode "integer123abc_") "integer123abc_")))

(deftest INTERDigitLetterUnderscore
    (is (= (unicode "INTER123abc_") "INTER123abc_"))
    (is (= (unicode "inter123abc_") "inter123abc_")))

(deftest NATDigitLetterUnderscore
    (is (= (unicode "NAT123abc_") "NAT123abc_"))
    (is (= (unicode "nat123abc_") "nat123abc_")))

(deftest NAT1DigitLetterUnderscore
    (is (= (unicode "NAT1123abc_") "NAT1123abc_"))
    (is (= (unicode "nat1123abc_") "nat1123abc_")))

(deftest NATURALDigitLetterUnderscore
    (is (= (unicode "NATURAL123abc_") "NATURAL123abc_"))
    (is (= (unicode "natural123abc_") "natural123abc_")))

(deftest NOTDigitLetterUnderscore
    (is (= (unicode "NOT123abc_") "NOT123abc_"))
    (is (= (unicode "not123abc_") "not123abc_")))

(deftest ORDigitLetterUnderscore
    (is (= (unicode "OR123abc_") "OR123abc_"))
    (is (= (unicode "or123abc_") "or123abc_")))

(deftest POWDigitLetterUnderscore
    (is (= (unicode "POW123abc_") "POW123abc_"))
    (is (= (unicode "pow123abc_") "pow123abc_")))

(deftest POW1DigitLetterUnderscore
    (is (= (unicode "POW1123abc_") "POW1123abc_"))
    (is (= (unicode "pow1123abc_") "pow1123abc_")))

(deftest TRUEDigitLetterUnderscore
    (is (= (unicode "TRUE123abc_") "TRUE123abc_"))
    (is (= (unicode "true123abc_") "true123abc_")))

(deftest UNIONDigitLetterUnderscore
    (is (= (unicode "UNION123abc_") "UNION123abc_"))
    (is (= (unicode "union123abc_") "union123abc_")))

(deftest ANYDigitUnderscoreLetter
    (is (= (unicode "ANY123_abc") "ANY123_abc"))
    (is (= (unicode "any123_abc") "any123_abc")))

(deftest FALSEDigitUnderscoreLetter
    (is (= (unicode "FALSE123_abc") "FALSE123_abc"))
    (is (= (unicode "false123_abc") "false123_abc")))

(deftest INTEGERDigitUnderscoreLetter
    (is (= (unicode "INTEGER123_abc") "INTEGER123_abc"))
    (is (= (unicode "integer123_abc") "integer123_abc")))

(deftest INTERDigitUnderscoreLetter
    (is (= (unicode "INTER123_abc") "INTER123_abc"))
    (is (= (unicode "inter123_abc") "inter123_abc")))

(deftest NATDigitUnderscoreLetter
    (is (= (unicode "NAT123_abc") "NAT123_abc"))
    (is (= (unicode "nat123_abc") "nat123_abc")))

(deftest NAT1DigitUnderscoreLetter
    (is (= (unicode "NAT1123_abc") "NAT1123_abc"))
    (is (= (unicode "nat1123_abc") "nat1123_abc")))

(deftest NATURALDigitUnderscoreLetter
    (is (= (unicode "NATURAL123_abc") "NATURAL123_abc"))
    (is (= (unicode "natural123_abc") "natural123_abc")))

(deftest NOTDigitUnderscoreLetter
    (is (= (unicode "NOT123_abc") "NOT123_abc"))
    (is (= (unicode "not123_abc") "not123_abc")))

(deftest ORDigitUnderscoreLetter
    (is (= (unicode "OR123_abc") "OR123_abc"))
    (is (= (unicode "or123_abc") "or123_abc")))

(deftest POWDigitUnderscoreLetter
    (is (= (unicode "POW123_abc") "POW123_abc"))
    (is (= (unicode "pow123_abc") "pow123_abc")))

(deftest POW1DigitUnderscoreLetter
    (is (= (unicode "POW1123_abc") "POW1123_abc"))
    (is (= (unicode "pow1123_abc") "pow1123_abc")))

(deftest TRUEDigitUnderscoreLetter
    (is (= (unicode "TRUE123_abc") "TRUE123_abc"))
    (is (= (unicode "true123_abc") "true123_abc")))

(deftest UNIONDigitUnderscoreLetter
    (is (= (unicode "UNION123_abc") "UNION123_abc"))
    (is (= (unicode "union123_abc") "union123_abc")))

(deftest ANYUnderscoreLetterDigit
    (is (= (unicode "ANY_abc123") "ANY_abc123"))
    (is (= (unicode "any_abc123") "any_abc123")))

(deftest FALSEUnderscoreLetterDigit
    (is (= (unicode "FALSE_abc123") "FALSE_abc123"))
    (is (= (unicode "false_abc123") "false_abc123")))

(deftest INTEGERUnderscoreLetterDigit
    (is (= (unicode "INTEGER_abc123") "INTEGER_abc123"))
    (is (= (unicode "integer_abc123") "integer_abc123")))

(deftest INTERUnderscoreLetterDigit
    (is (= (unicode "INTER_abc123") "INTER_abc123"))
    (is (= (unicode "inter_abc123") "inter_abc123")))

(deftest NATUnderscoreLetterDigit
    (is (= (unicode "NAT_abc123") "NAT_abc123"))
    (is (= (unicode "nat_abc123") "nat_abc123")))

(deftest NAT1UnderscoreLetterDigit
    (is (= (unicode "NAT1_abc123") "NAT1_abc123"))
    (is (= (unicode "nat1_abc123") "nat1_abc123")))

(deftest NATURALUnderscoreLetterDigit
    (is (= (unicode "NATURAL_abc123") "NATURAL_abc123"))
    (is (= (unicode "natural_abc123") "natural_abc123")))

(deftest NOTUnderscoreLetterDigit
    (is (= (unicode "NOT_abc123") "NOT_abc123"))
    (is (= (unicode "not_abc123") "not_abc123")))

(deftest ORUnderscoreLetterDigit
    (is (= (unicode "OR_abc123") "OR_abc123"))
    (is (= (unicode "or_abc123") "or_abc123")))

(deftest POWUnderscoreLetterDigit
    (is (= (unicode "POW_abc123") "POW_abc123"))
    (is (= (unicode "pow_abc123") "pow_abc123")))

(deftest POW1UnderscoreLetterDigit
    (is (= (unicode "POW1_abc123") "POW1_abc123"))
    (is (= (unicode "pow1_abc123") "pow1_abc123")))

(deftest TRUEUnderscoreLetterDigit
    (is (= (unicode "TRUE_abc123") "TRUE_abc123"))
    (is (= (unicode "true_abc123") "true_abc123")))

(deftest UNIONUnderscoreLetterDigit
    (is (= (unicode "UNION_abc123") "UNION_abc123"))
    (is (= (unicode "union_abc123") "union_abc123")))

(deftest ANYUnderscoreDigitLetter
    (is (= (unicode "ANY_123abc") "ANY_123abc"))
    (is (= (unicode "any_123abc") "any_123abc")))

(deftest FALSEUnderscoreDigitLetter
    (is (= (unicode "FALSE_123abc") "FALSE_123abc"))
    (is (= (unicode "false_123abc") "false_123abc")))

(deftest INTEGERUnderscoreDigitLetter
    (is (= (unicode "INTEGER_123abc") "INTEGER_123abc"))
    (is (= (unicode "integer_123abc") "integer_123abc")))

(deftest INTERUnderscoreDigitLetter
    (is (= (unicode "INTER_123abc") "INTER_123abc"))
    (is (= (unicode "inter_123abc") "inter_123abc")))

(deftest NATUnderscoreDigitLetter
    (is (= (unicode "NAT_123abc") "NAT_123abc"))
    (is (= (unicode "nat_123abc") "nat_123abc")))

(deftest NAT1UnderscoreDigitLetter
    (is (= (unicode "NAT1_123abc") "NAT1_123abc"))
    (is (= (unicode "nat1_123abc") "nat1_123abc")))

(deftest NATURALUnderscoreDigitLetter
    (is (= (unicode "NATURAL_123abc") "NATURAL_123abc"))
    (is (= (unicode "natural_123abc") "natural_123abc")))

(deftest NOTUnderscoreDigitLetter
    (is (= (unicode "NOT_123abc") "NOT_123abc"))
    (is (= (unicode "not_123abc") "not_123abc")))

(deftest ORUnderscoreDigitLetter
    (is (= (unicode "OR_123abc") "OR_123abc"))
    (is (= (unicode "or_123abc") "or_123abc")))

(deftest POWUnderscoreDigitLetter
    (is (= (unicode "POW_123abc") "POW_123abc"))
    (is (= (unicode "pow_123abc") "pow_123abc")))

(deftest POW1UnderscoreDigitLetter
    (is (= (unicode "POW1_123abc") "POW1_123abc"))
    (is (= (unicode "pow1_123abc") "pow1_123abc")))

(deftest TRUEUnderscoreDigitLetter
    (is (= (unicode "TRUE_123abc") "TRUE_123abc"))
    (is (= (unicode "true_123abc") "true_123abc")))

(deftest UNIONUnderscoreDigitLetter
    (is (= (unicode "UNION_123abc") "UNION_123abc"))
    (is (= (unicode "union_123abc") "union_123abc")))

(deftest UnderscoreDigitUnderscore
    (is (= (unicode "_123_") "_123_")))

(deftest UnderscoreLetterUnderscore
    (is (= (unicode "_abc_") "_abc_")))

(deftest UnderscoreANYUnderscore
    (is (= (unicode "_ANY_") "_ANY_"))
    (is (= (unicode "_any_") "_any_")))

(deftest UnderscoreFALSEUnderscore
    (is (= (unicode "_FALSE_") "_FALSE_"))
    (is (= (unicode "_false_") "_false_")))

(deftest UnderscoreINTEGERUnderscore
    (is (= (unicode "_INTEGER_") "_INTEGER_"))
    (is (= (unicode "_integer_") "_integer_")))

(deftest UnderscoreINTERUnderscore
    (is (= (unicode "_INTER_") "_INTER_"))
    (is (= (unicode "_inter_") "_inter_")))

(deftest UnderscoreNATUnderscore
    (is (= (unicode "_NAT_") "_NAT_"))
    (is (= (unicode "_nat_") "_nat_")))

(deftest UnderscoreNAT1Underscore
    (is (= (unicode "_NAT1_") "_NAT1_"))
    (is (= (unicode "_nat1_") "_nat1_")))

(deftest UnderscoreNATURALUnderscore
    (is (= (unicode "_NATURAL_") "_NATURAL_"))
    (is (= (unicode "_natural_") "_natural_")))

(deftest UnderscoreNOTUnderscore
    (is (= (unicode "_NOT_") "_NOT_"))
    (is (= (unicode "_not_") "_not_")))

(deftest UnderscoreORUnderscore
    (is (= (unicode "_OR_") "_OR_"))
    (is (= (unicode "_or_") "_or_")))

(deftest UnderscorePOWUnderscore
    (is (= (unicode "_POW_") "_POW_"))
    (is (= (unicode "_pow_") "_pow_")))

(deftest UnderscorePOW1Underscore
    (is (= (unicode "_POW1_") "_POW1_"))
    (is (= (unicode "_pow1_") "_pow1_")))

(deftest UnderscoreTRUEUnderscore
    (is (= (unicode "_TRUE_") "_TRUE_"))
    (is (= (unicode "_true_") "_true_")))

(deftest UnderscoreUNIONUnderscore
    (is (= (unicode "_UNION_") "_UNION_"))
    (is (= (unicode "_union_") "_union_")))

(deftest LetterUnderscoreDigitUnderscoreLetter
    (is (= (unicode "abc_123_abc") "abc_123_abc")))

(deftest LetterUnderscoreLetterUnderscoreLetter
    (is (= (unicode "abc_abc_abc") "abc_abc_abc")))

(deftest LetterUnderscoreANYUnderscoreLetter
    (is (= (unicode "abc_ANY_abc") "abc_ANY_abc"))
    (is (= (unicode "abc_any_abc") "abc_any_abc")))

(deftest LetterUnderscoreFALSEUnderscoreLetter
    (is (= (unicode "abc_FALSE_abc") "abc_FALSE_abc"))
    (is (= (unicode "abc_false_abc") "abc_false_abc")))

(deftest LetterUnderscoreINTEGERUnderscoreLetter
    (is (= (unicode "abc_INTEGER_abc") "abc_INTEGER_abc"))
    (is (= (unicode "abc_integer_abc") "abc_integer_abc")))

(deftest LetterUnderscoreINTERUnderscoreLetter
    (is (= (unicode "abc_INTER_abc") "abc_INTER_abc"))
    (is (= (unicode "abc_inter_abc") "abc_inter_abc")))

(deftest LetterUnderscoreNATUnderscoreLetter
    (is (= (unicode "abc_NAT_abc") "abc_NAT_abc"))
    (is (= (unicode "abc_nat_abc") "abc_nat_abc")))

(deftest LetterUnderscoreNAT1UnderscoreLetter
    (is (= (unicode "abc_NAT1_abc") "abc_NAT1_abc"))
    (is (= (unicode "abc_nat1_abc") "abc_nat1_abc")))

(deftest LetterUnderscoreNATURALUnderscoreLetter
    (is (= (unicode "abc_NATURAL_abc") "abc_NATURAL_abc"))
    (is (= (unicode "abc_natural_abc") "abc_natural_abc")))

(deftest LetterUnderscoreNOTUnderscoreLetter
    (is (= (unicode "abc_NOT_abc") "abc_NOT_abc"))
    (is (= (unicode "abc_not_abc") "abc_not_abc")))

(deftest LetterUnderscoreORUnderscoreLetter
    (is (= (unicode "abc_OR_abc") "abc_OR_abc"))
    (is (= (unicode "abc_or_abc") "abc_or_abc")))

(deftest LetterUnderscorePOWUnderscoreLetter
    (is (= (unicode "abc_POW_abc") "abc_POW_abc"))
    (is (= (unicode "abc_pow_abc") "abc_pow_abc")))

(deftest LetterUnderscorePOW1UnderscoreLetter
    (is (= (unicode "abc_POW1_abc") "abc_POW1_abc"))
    (is (= (unicode "abc_pow1_abc") "abc_pow1_abc")))

(deftest LetterUnderscoreTRUEUnderscoreLetter
    (is (= (unicode "abc_TRUE_abc") "abc_TRUE_abc"))
    (is (= (unicode "abc_true_abc") "abc_true_abc")))

(deftest LetterUnderscoreUNIONUnderscoreLetter
    (is (= (unicode "abc_UNION_abc") "abc_UNION_abc"))
    (is (= (unicode "abc_union_abc") "abc_union_abc")))

(deftest DigitUnderscoreDigitUnderscoreDigit
    (is (= (unicode "123_123_123") "123_123_123")))

(deftest DigitUnderscoreLetterUnderscoreDigit
    (is (= (unicode "123_abc_123") "123_abc_123")))

(deftest DigitUnderscoreANYUnderscoreDigit
    (is (= (unicode "123_ANY_123") "123_ANY_123"))
    (is (= (unicode "123_any_123") "123_any_123")))

(deftest DigitUnderscoreFALSEUnderscoreDigit
    (is (= (unicode "123_FALSE_123") "123_FALSE_123"))
    (is (= (unicode "123_false_123") "123_false_123")))

(deftest DigitUnderscoreINTEGERUnderscoreDigit
    (is (= (unicode "123_INTEGER_123") "123_INTEGER_123"))
    (is (= (unicode "123_integer_123") "123_integer_123")))

(deftest DigitUnderscoreINTERUnderscoreDigit
    (is (= (unicode "123_INTER_123") "123_INTER_123"))
    (is (= (unicode "123_inter_123") "123_inter_123")))

(deftest DigitUnderscoreNATUnderscoreDigit
    (is (= (unicode "123_NAT_123") "123_NAT_123"))
    (is (= (unicode "123_nat_123") "123_nat_123")))

(deftest DigitUnderscoreNAT1UnderscoreDigit
    (is (= (unicode "123_NAT1_123") "123_NAT1_123"))
    (is (= (unicode "123_nat1_123") "123_nat1_123")))

(deftest DigitUnderscoreNATURALUnderscoreDigit
    (is (= (unicode "123_NATURAL_123") "123_NATURAL_123"))
    (is (= (unicode "123_natural_123") "123_natural_123")))

(deftest DigitUnderscoreNOTUnderscoreDigit
    (is (= (unicode "123_NOT_123") "123_NOT_123"))
    (is (= (unicode "123_not_123") "123_not_123")))

(deftest DigitUnderscoreORUnderscoreDigit
    (is (= (unicode "123_OR_123") "123_OR_123"))
    (is (= (unicode "123_or_123") "123_or_123")))

(deftest DigitUnderscorePOWUnderscoreDigit
    (is (= (unicode "123_POW_123") "123_POW_123"))
    (is (= (unicode "123_pow_123") "123_pow_123")))

(deftest DigitUnderscorePOW1UnderscoreDigit
    (is (= (unicode "123_POW1_123") "123_POW1_123"))
    (is (= (unicode "123_pow1_123") "123_pow1_123")))

(deftest DigitUnderscoreTRUEUnderscoreDigit
    (is (= (unicode "123_TRUE_123") "123_TRUE_123"))
    (is (= (unicode "123_true_123") "123_true_123")))

(deftest DigitUnderscoreUNIONUnderscoreDigit
    (is (= (unicode "123_UNION_123") "123_UNION_123"))
    (is (= (unicode "123_union_123") "123_union_123")))

(deftest Var_123
    (is (= (unicode "var_123") "var_123"))
    (is (= (unicode "123_var") "123_var"))
    (is (= (unicode "var_123_var") "var_123_var"))
	(is (= (unicode "var_") "var_"))
    (is (= (unicode "_var") "_var"))
    (is (= (unicode "_var_") "_var_"))
	(is (= (unicode "123_") "123_"))
    (is (= (unicode "_123") "_123"))
    (is (= (unicode "_123_") "_123_")))

(deftest Var123
    (is (= (unicode "var123") "var123"))
    (is (= (unicode "123var") "123var"))
    (is (= (unicode "var123var") "var123var"))
    (is (= (unicode "123var123") "123var123")))

(deftest VarANY
    (is (= (unicode "varANY") "varANY"))
    (is (= (unicode "varany") "varany"))
    (is (= (unicode "varANYvar") "varANYvar"))
    (is (= (unicode "varanyvar") "varanyvar"))
    (is (= (unicode "ANYvar") "ANYvar"))
    (is (= (unicode "anyvar") "anyvar"))

	(is (= (unicode "123ANY") "123ANY"))
    (is (= (unicode "123any") "123any"))
    (is (= (unicode "123ANY123") "123ANY123"))
    (is (= (unicode "123any123") "123any123"))
    (is (= (unicode "ANY123") "ANY123"))
    (is (= (unicode "any123") "any123"))

	(is (= (unicode "_ANY") "_ANY"))
    (is (= (unicode "_any") "_any"))
    (is (= (unicode "_ANY_") "_ANY_"))
    (is (= (unicode "_any_") "_any_"))
    (is (= (unicode "ANY_") "ANY_"))
    (is (= (unicode "any_") "any_")))

(deftest VarFALSE
    (is (= (unicode "varFALSE") "varFALSE"))
    (is (= (unicode "varfalse") "varfalse"))
    (is (= (unicode "varFALSEvar") "varFALSEvar"))
    (is (= (unicode "varfalsevar") "varfalsevar"))
    (is (= (unicode "FALSEvar") "FALSEvar"))
    (is (= (unicode "falsevar") "falsevar"))

	(is (= (unicode "123FALSE") "123FALSE"))
    (is (= (unicode "123false") "123false"))
    (is (= (unicode "123FALSE123") "123FALSE123"))
    (is (= (unicode "123false123") "123false123"))
    (is (= (unicode "FALSE123") "FALSE123"))
    (is (= (unicode "false123") "false123"))

	(is (= (unicode "_FALSE") "_FALSE"))
    (is (= (unicode "_false") "_false"))
    (is (= (unicode "_FALSE_") "_FALSE_"))
    (is (= (unicode "_false_") "_false_"))
    (is (= (unicode "FALSE_") "FALSE_"))
    (is (= (unicode "false_") "false_")))

(deftest VarINTEGER
    (is (= (unicode "varINTEGER") "varINTEGER"))
    (is (= (unicode "varinteger") "varinteger"))
    (is (= (unicode "varINTEGERvar") "varINTEGERvar"))
    (is (= (unicode "varintegervar") "varintegervar"))
    (is (= (unicode "INTEGERvar") "INTEGERvar"))
    (is (= (unicode "integervar") "integervar"))

	(is (= (unicode "123INTEGER") "123INTEGER"))
    (is (= (unicode "123integer") "123integer"))
    (is (= (unicode "123INTEGER123") "123INTEGER123"))
    (is (= (unicode "123integer123") "123integer123"))
    (is (= (unicode "INTEGER123") "INTEGER123"))
    (is (= (unicode "integer123") "integer123"))

	(is (= (unicode "_INTEGER") "_INTEGER"))
    (is (= (unicode "_integer") "_integer"))
    (is (= (unicode "_INTEGER_") "_INTEGER_"))
    (is (= (unicode "_integer_") "_integer_"))
    (is (= (unicode "INTEGER_") "INTEGER_"))
    (is (= (unicode "integer_") "integer_")))

(deftest VarINTER
    (is (= (unicode "varINTER") "varINTER"))
    (is (= (unicode "varinter") "varinter"))
    (is (= (unicode "varINTERvar") "varINTERvar"))
    (is (= (unicode "varintervar") "varintervar"))
    (is (= (unicode "INTERvar") "INTERvar"))
    (is (= (unicode "intervar") "intervar"))

	(is (= (unicode "123INTER") "123INTER"))
    (is (= (unicode "123inter") "123inter"))
    (is (= (unicode "123INTER123") "123INTER123"))
    (is (= (unicode "123inter123") "123inter123"))
    (is (= (unicode "INTER123") "INTER123"))
    (is (= (unicode "inter123") "inter123"))

	(is (= (unicode "_INTER") "_INTER"))
    (is (= (unicode "_inter") "_inter"))
    (is (= (unicode "_INTER_") "_INTER_"))
    (is (= (unicode "_inter_") "_inter_"))
    (is (= (unicode "INTER_") "INTER_"))
    (is (= (unicode "inter_") "inter_")))

(deftest VarNAT
    (is (= (unicode "varNAT") "varNAT"))
    (is (= (unicode "varnat") "varnat"))
    (is (= (unicode "varNATvar") "varNATvar"))
    (is (= (unicode "varnatvar") "varnatvar"))
    (is (= (unicode "NATvar") "NATvar"))
    (is (= (unicode "natvar") "natvar"))

	(is (= (unicode "123NAT") "123NAT"))
    (is (= (unicode "123nat") "123nat"))
    (is (= (unicode "123NAT123") "123NAT123"))
    (is (= (unicode "123nat123") "123nat123"))
    (is (= (unicode "NAT123") "NAT123"))
    (is (= (unicode "nat123") "nat123"))

	(is (= (unicode "_NAT") "_NAT"))
    (is (= (unicode "_nat") "_nat"))
    (is (= (unicode "_NAT_") "_NAT_"))
    (is (= (unicode "_nat_") "_nat_"))
    (is (= (unicode "NAT_") "NAT_"))
    (is (= (unicode "nat_") "nat_")))

(deftest VarNAT1
    (is (= (unicode "varNAT1") "varNAT1"))
    (is (= (unicode "varnat1") "varnat1"))
    (is (= (unicode "varNAT1var") "varNAT1var"))
    (is (= (unicode "varnat1var") "varnat1var"))
    (is (= (unicode "NAT1var") "NAT1var"))
    (is (= (unicode "nat1var") "nat1var"))

	(is (= (unicode "123NAT1") "123NAT1"))
    (is (= (unicode "123nat1") "123nat1"))
    (is (= (unicode "123NAT1123") "123NAT1123"))
    (is (= (unicode "123nat1123") "123nat1123"))
    (is (= (unicode "NAT1123") "NAT1123"))
    (is (= (unicode "nat1123") "nat1123"))

	(is (= (unicode "_NAT1") "_NAT1"))
    (is (= (unicode "_nat1") "_nat1"))
    (is (= (unicode "_NAT1_") "_NAT1_"))
    (is (= (unicode "_nat1_") "_nat1_"))
    (is (= (unicode "NAT1_") "NAT1_"))
    (is (= (unicode "nat1_") "nat1_")))

(deftest VarNATURAL
    (is (= (unicode "varNATURAL") "varNATURAL"))
    (is (= (unicode "varnatural") "varnatural"))
    (is (= (unicode "varNATURALvar") "varNATURALvar"))
    (is (= (unicode "varnaturalvar") "varnaturalvar"))
    (is (= (unicode "NATURALvar") "NATURALvar"))
    (is (= (unicode "naturalvar") "naturalvar"))

	(is (= (unicode "123NATURAL") "123NATURAL"))
    (is (= (unicode "123natural") "123natural"))
    (is (= (unicode "123NATURAL123") "123NATURAL123"))
    (is (= (unicode "123natural123") "123natural123"))
    (is (= (unicode "NATURAL123") "NATURAL123"))
    (is (= (unicode "natural123") "natural123"))

	(is (= (unicode "_NATURAL") "_NATURAL"))
    (is (= (unicode "_natural") "_natural"))
    (is (= (unicode "_NATURAL_") "_NATURAL_"))
    (is (= (unicode "_natural_") "_natural_"))
    (is (= (unicode "NATURAL_") "NATURAL_"))
    (is (= (unicode "natural_") "natural_")))

(deftest VarNOT
    (is (= (unicode "varNOT") "varNOT"))
    (is (= (unicode "varnot") "varnot"))
    (is (= (unicode "varNOTvar") "varNOTvar"))
    (is (= (unicode "varnotvar") "varnotvar"))
    (is (= (unicode "NOTvar") "NOTvar"))
    (is (= (unicode "notvar") "notvar"))

	(is (= (unicode "123NOT") "123NOT"))
    (is (= (unicode "123not") "123not"))
    (is (= (unicode "123NOT123") "123NOT123"))
    (is (= (unicode "123not123") "123not123"))
    (is (= (unicode "NOT123") "NOT123"))
    (is (= (unicode "not123") "not123"))

	(is (= (unicode "_NOT") "_NOT"))
    (is (= (unicode "_not") "_not"))
    (is (= (unicode "_NOT_") "_NOT_"))
    (is (= (unicode "_not_") "_not_"))
    (is (= (unicode "NOT_") "NOT_"))
    (is (= (unicode "not_") "not_")))

(deftest VarOr
    (is (= (unicode "varOR") "varOR"))
    (is (= (unicode "varor") "varor"))
    (is (= (unicode "varORvar") "varORvar"))
    (is (= (unicode "varorvar") "varorvar"))
    (is (= (unicode "ORvar") "ORvar"))
    (is (= (unicode "orvar") "orvar"))

	(is (= (unicode "123OR") "123OR"))
    (is (= (unicode "123or") "123or"))
    (is (= (unicode "123OR123") "123OR123"))
    (is (= (unicode "123or123") "123or123"))
    (is (= (unicode "OR123") "OR123"))
    (is (= (unicode "or123") "or123"))

	(is (= (unicode "_OR") "_OR"))
    (is (= (unicode "_or") "_or"))
    (is (= (unicode "_OR_") "_OR_"))
    (is (= (unicode "_or_") "_or_"))
    (is (= (unicode "OR_") "OR_"))
    (is (= (unicode "or_") "or_")))

(deftest VarPOW
    (is (= (unicode "varPOW") "varPOW"))
    (is (= (unicode "varpow") "varpow"))
    (is (= (unicode "varPOWvar") "varPOWvar"))
    (is (= (unicode "varpowvar") "varpowvar"))
    (is (= (unicode "POWvar") "POWvar"))
    (is (= (unicode "powvar") "powvar"))

	(is (= (unicode "123pow") "123pow"))
    (is (= (unicode "123POW") "123POW"))
    (is (= (unicode "123POW123") "123POW123"))
    (is (= (unicode "123pow123") "123pow123"))
    (is (= (unicode "POW123") "POW123"))
    (is (= (unicode "pow123") "pow123"))

	(is (= (unicode "_POW") "_POW"))
    (is (= (unicode "_pow") "_pow"))
    (is (= (unicode "_POW_") "_POW_"))
    (is (= (unicode "_pow_") "_pow_"))
    (is (= (unicode "POW_") "POW_"))
    (is (= (unicode "pow_") "pow_")))

(deftest VarPOW1
    (is (= (unicode "varPOW1") "varPOW1"))
    (is (= (unicode "varpow1") "varpow1"))
    (is (= (unicode "varPOW1var") "varPOW1var"))
    (is (= (unicode "varpow1var") "varpow1var"))
    (is (= (unicode "POW1var") "POW1var"))
    (is (= (unicode "pow1var") "pow1var"))

	(is (= (unicode "123POW1") "123POW1"))
    (is (= (unicode "123pow1") "123pow1"))
    (is (= (unicode "123POW1123") "123POW1123"))
    (is (= (unicode "123pow1123") "123pow1123"))
    (is (= (unicode "POW1123") "POW1123"))
    (is (= (unicode "pow1123") "pow1123"))

	(is (= (unicode "_POW1") "_POW1"))
    (is (= (unicode "_pow1") "_pow1"))
    (is (= (unicode "_POW1_") "_POW1_"))
    (is (= (unicode "_pow1_") "_pow1_"))
    (is (= (unicode "POW1_") "POW1_"))
    (is (= (unicode "pow1_") "pow1_")))

(deftest VarTRUE
    (is (= (unicode "varTRUE") "varTRUE"))
    (is (= (unicode "vartrue") "vartrue"))
    (is (= (unicode "varTRUEvar") "varTRUEvar"))
    (is (= (unicode "vartruevar") "vartruevar"))
    (is (= (unicode "TRUEvar") "TRUEvar"))
    (is (= (unicode "truevar") "truevar"))

	(is (= (unicode "123TRUE") "123TRUE"))
    (is (= (unicode "123true") "123true"))
    (is (= (unicode "123TRUE123") "123TRUE123"))
    (is (= (unicode "123true123") "123true123"))
    (is (= (unicode "TRUE123") "TRUE123"))
    (is (= (unicode "true123") "true123"))

	(is (= (unicode "_TRUE") "_TRUE"))
    (is (= (unicode "_true") "_true"))
    (is (= (unicode "_TRUE_") "_TRUE_"))
    (is (= (unicode "_true_") "_true_"))
    (is (= (unicode "TRUE_") "TRUE_"))
    (is (= (unicode "true_") "true_")))

(deftest VarUNION
    (is (= (unicode "varUNION") "varUNION"))
    (is (= (unicode "varunion") "varunion"))
    (is (= (unicode "varUNIONvar") "varUNIONvar"))
    (is (= (unicode "varunionvar") "varunionvar"))
    (is (= (unicode "UNIONvar") "UNIONvar"))
    (is (= (unicode "unionvar") "unionvar"))

	(is (= (unicode "123UNION") "123UNION"))
    (is (= (unicode "123union") "123union"))
    (is (= (unicode "123UNION123") "123UNION123"))
    (is (= (unicode "123union123") "123union123"))
    (is (= (unicode "UNION123") "UNION123"))
    (is (= (unicode "union123") "union123"))

	(is (= (unicode "_UNION") "_UNION"))
    (is (= (unicode "_union") "_union"))
    (is (= (unicode "_UNION_") "_UNION_"))
    (is (= (unicode "_union_") "_union_"))
    (is (= (unicode "UNION_") "UNION_"))
    (is (= (unicode "union_") "union_")))