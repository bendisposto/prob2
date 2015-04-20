(ns de.prob2.unicode-toascii
	(:require [de.prob2.generated.unicodetranslator :refer (ascii)])
	(:use clojure.test))

(deftest TIn
	(is (= (ascii "\u2208") ":")))

(deftest TNotsubseteq
	(is (= (ascii "\u2288") "/<:")))

(deftest TNotsubset
	(is (= (ascii "\u2284") "/<<:")))

(deftest TSubseteq
	(is (= (ascii "\u2286") "<:")))

(deftest TSetminus
	(is (= (ascii "\u2216") "\\")))

(deftest TDotdot
	(is (= (ascii "\u2025") "..")))

(deftest TNat
	(is (= (ascii "\u2115") "NAT")))

(deftest TEmptyset
	(is (= (ascii "\u2205") "{}")))

(deftest TBcmsuch
	(is (= (ascii ":\u2223") ":|")))

(deftest TBfalse
	(is (= (ascii "\u22a5") "false")))

(deftest TForall
	(is (= (ascii "\u2200") "!")))

(deftest TExists
	(is (= (ascii "\u2203") "#")))

(deftest TMapsto
	(is (= (ascii "\u21a6") "|->")))

(deftest TBtrue
	(is (= (ascii "\u22a4") "true")))

(deftest TSubset
	(is (= (ascii "\u2282") "<<:")))

(deftest TBunion
	(is (= (ascii "\u222a") "\\/")))

(deftest TBinter
	(is (= (ascii "\u2229") "/\\")))

(deftest TDomres
	(is (= (ascii "\u25c1") "<|")))

(deftest TRanres
	(is (= (ascii "\u25b7") "|>")))

(deftest TDomsub
	(is (= (ascii "\u2a64") "<<|")))

(deftest TRansub
	(is (= (ascii "\u2a65") "|>>")))

(deftest TLambda
	(is (= (ascii "\u03bb") "%")))

(deftest TOftype
	(is (= (ascii "\u2982") "oftype")))

(deftest TNotin
	(is (= (ascii "\u2209") "/:")))

(deftest TCprod
	(is (= (ascii "\u00d7") "**")))

(deftest TUnion
	(is (= (ascii "\u22c3") "UNION")))

(deftest TInter
	(is (= (ascii "\u22c2") "INTER")))

(deftest TFcomp
	(is (= (ascii "\u003b") ";")))

(deftest TBcomp
	(is (= (ascii "\u2218") "circ")))

(deftest TTotalSurjectiveRel
	(is (= (ascii "\ue102") "<<->>")))

(deftest TDprod
	(is (= (ascii "\u2297") "><")))

(deftest TPprod
	(is (= (ascii "\u2225") "||")))

(deftest TBcmeq
	(is (= (ascii "\u2254") ":=")))

(deftest TBcmin
	(is (= (ascii ":\u2208") "::")))

(deftest TIntg
	(is (= (ascii "\u2124") "INT")))

(deftest TLand
	(is (= (ascii "\u2227") "&")))

(deftest TLimp
	(is (= (ascii "\u21d2") "=>")))

(deftest TLeqv
	(is (= (ascii "\u21d4") "<=>")))

(deftest TLnot
	(is (= (ascii "\u00ac") "not")))

(deftest TQdot
	(is (= (ascii "\u00b7") ".")))

(deftest TConv
	(is (= (ascii "\u223c") "~")))

(deftest TTotalRel
	(is (= (ascii "\ue100") "<<->")))

(deftest TSurjectiveRel
	(is (= (ascii "\ue101") "<->>")))

(deftest TPfun
	(is (= (ascii "\u21f8") "+->")))

(deftest TTfun
	(is (= (ascii "\u2192") "-->")))

(deftest TPinj
	(is (= (ascii "\u2914") ">+>")))

(deftest TTinj
	(is (= (ascii "\u21a3") ">->")))

(deftest TPsur
	(is (= (ascii "\u2900") "+>>")))

(deftest TTsur
	(is (= (ascii "\u21a0") "->>")))

(deftest TTbij
	(is (= (ascii "\u2916") ">->>")))

(deftest TExpn
	(is (= (ascii "\u005e") "^")))

(deftest TLor
	(is (= (ascii "\u2228") "or")))

(deftest TPow
	(is (= (ascii "\u2119") "POW")))

(deftest TMid
	(is (= (ascii "\u2223") "|")))

(deftest TNeq
	(is (= (ascii "\u2260") "/=")))

(deftest TRel
	(is (= (ascii "\u2194") "<->")))

(deftest TOvl
	(is (= (ascii "\ue103") "<+")))

(deftest TLeq
	(is (= (ascii "\u2264") "<=")))

(deftest TGeq
	(is (= (ascii "\u2265") ">=")))

(deftest TDiv
	(is (= (ascii "\u00f7") "/")))

(deftest TMult
	(is (= (ascii "\u2217") "*")))

(deftest TMinus
	(is (= (ascii "\u2212") "-")))

(deftest TComma
	(is (= (ascii ",") ",")))

(deftest Conjunction
	(is (= (ascii "P \u2227 Q") "P & Q")))

(deftest Disjunction
	(is (= (ascii "P \u2228 Q") "P or Q")))

(deftest Implication
	(is (= (ascii "P \u21d2 Q") "P => Q")))

(deftest Equivalence
	(is (= (ascii "P \u21d4 Q") "P <=> Q")))

(deftest Negation
	(is (= (ascii "\u00ac P") "not P")))

(deftest UniversalQuantification
	(is (= (ascii "!(z).(P => Q)") 
				"!(z).(P => Q)"))
	(is (= (ascii "\u2200(z)\u00b7(P \u21d2 Q)") "!(z).(P => Q)")))

(deftest UniversalQuantification2
	(is (= (ascii "(\u2200z\u00b7P \u21d2 Q)") "(!z.P => Q)")))

(deftest ExistentialQuantification
	(is (= (ascii "\u2203(z)\u00b7(P \u2227 Q)") "#(z).(P & Q)")))

(deftest ExistentialQuantification2
	(is (= (ascii "(\u2203z\u00b7P \u2227 Q)") "(#z.P & Q)")))

(deftest Substitution
	(is (= (ascii "[G] P") "[G] P")))

(deftest Equality
	(is (= (ascii "E = F") "E = F")))

(deftest Inequality
	(is (= (ascii "E \u2260 F") "E /= F")))

(deftest SingletonSet
	(is (= (ascii "{E}") "{E}")))

(deftest SetEnumeration
	(is (= (ascii "{E, F}") "{E, F}")))

(deftest EmptySet
	(is (= (ascii "\u2205") "{}")))

(deftest SetComprehension
	(is (= (ascii "{z \u2223 P}") "{z | P}")))

(deftest SetComprehension2
	(is (= (ascii "{z \u00b7 P \u2223 F}") 
				"{z . P | F}")))

(deftest SetComprehension3
	(is (= (ascii "{F \u2223 P}") "{F | P}")))

(deftest SetComprehension4
	(is (= (ascii "{x \u2223 P}") "{x | P}")))

(deftest Union
	(is (= (ascii "S \u222a T") "S \\/ T")))

(deftest Intersection
	(is (= (ascii "S \u2229 T") "S /\\ T")))

(deftest Difference
	(is (= (ascii "S\u2212T") "S-T")))

(deftest Difference2
	(is (= (ascii "S\\T") "S\\T")))

(deftest OrderedPair
	(is (= (ascii "E \u21a6 F") "E |-> F")))

(deftest CartesianProduct
	(is (= (ascii "S \u2217 T") "S * T")))

(deftest CartesianProduct2
	(is (= (ascii "S \u00d7 T") "S ** T")))

(deftest Powerset
	(is (= (ascii "\u2119(S)") "POW(S)")))

(deftest NonEmptySubsets
	(is (= (ascii "POW1(S)") "POW1(S)")))

(deftest FiniteSets
	(is (= (ascii "finite S") "finite S")))

(deftest FiniteSubsets
	(is (= (ascii "FIN(S)") "FIN(S)")))

(deftest FiniteNonEmptySubsets
	(is (= (ascii "FIN1(S)") "FIN1(S)")))

(deftest Cardinality
	(is (= (ascii "card(S)") "card(S)")))

(deftest Partition
	(is (= (ascii "partition(S,x,y)") 
				"partition(S,x,y)")))

(deftest GeneralizedUnion
	(is (= (ascii "\u22c3(U)") "UNION(U)")))

(deftest GeneralizedUnion2
	(is (= (ascii "\u22c3 (z)\u00b7(P \u2223 E)") "UNION (z).(P | E)")))

(deftest GeneralizedUnion3
	(is (= (ascii "union(U)") "union(U)")))

(deftest QuantifiedUnion
	(is (= (ascii "\u22c3 z\u00b7P \u2223 S") "UNION z.P | S")))

(deftest GeneralizedIntersection
	(is (= (ascii "\u22c2(U)") "INTER(U)")))

(deftest GeneralizedIntersection2
	(is (= (ascii "\u22c2 (z)\u00b7(P \u2223 E)") "INTER (z).(P | E)")))

(deftest GeneralizedIntersection3
	(is (= (ascii "inter(U)") "inter(U)")))

(deftest QuantifiedIntersection
	(is (= (ascii "\u22c2 z\u00b7P \u2223 S") "INTER z.P | S")))

(deftest SetMembership
	(is (= (ascii "E \u2208 S") "E : S")))

(deftest SetNonMembership
	(is (= (ascii "E \u2209 S") "E /: S")))

(deftest Subset
	(is (= (ascii "S \u2286 T") "S <: T")))

(deftest NotASubset
	(is (= (ascii "S \u2288 T") "S /<: T")))

(deftest ProperSubset
	(is (= (ascii "S \u2282 T") "S <<: T")))

(deftest NotAProperSubset
	(is (= (ascii "S \u2284 T") "S /<<: T")))

(deftest NaturalNumbers
	(is (= (ascii "\u2115") "NAT")))

(deftest PositiveNaturalNumbers
	(is (= (ascii "NAT1") "NAT1")))

(deftest Minimum
	(is (= (ascii "min(S)") "min(S)")))

(deftest Maximum
	(is (= (ascii "max(S)") "max(S)")))

(deftest Sum
	(is (= (ascii "m + n") "m + n")))

(deftest DifferenceAlt
	(is (= (ascii "m \u2212 n") "m - n")))

(deftest Product
	(is (= (ascii "m \u2217 n") "m * n")))

(deftest Quotient
	(is (= (ascii "m \u00f7 n") "m / n")))

(deftest Remainder
	(is (= (ascii "m mod n") "m mod n")))

(deftest Interval
	(is (= (ascii "m \u2025 n") "m .. n")))

(deftest SetSummation
	(is (= (ascii "SIGMA(z)\u00b7(P \u2223 E)") "SIGMA(z).(P | E)")))

(deftest SetProduct
	(is (= (ascii "PI(z)\u00b7(P \u2223 E)") 
				"PI(z).(P | E)")))

(deftest Greater
	(is (= (ascii "m > n") "m > n")))

(deftest Less
	(is (= (ascii "m < n") "m < n")))

(deftest GreaterOrEqual
	(is (= (ascii "m \u2265 n") "m >= n")))

(deftest LessOrEqual
	(is (= (ascii "m \u2264 n") "m <= n")))

(deftest Relations
	(is (= (ascii "S \u2194 T") "S <-> T")))

(deftest Domain
	(is (= (ascii "dom(r)") "dom(r)")))

(deftest Range
	(is (= (ascii "ran(r)") "ran(r)")))

(deftest ForwardComposition
	(is (= "p ; q" (ascii "p ; q"))))

(deftest BackwardComposition
	(is (= (ascii "p \u2218 q") "p circ q")))

(deftest Identity
	(is (= (ascii "id(S)") "id(S)")))

(deftest DomainRestriction
	(is (= (ascii "S \u25c1 r") "S <| r")))

(deftest DomainSubtraction
	(is (= (ascii "S \u2a64 r") "S <<| r")))

(deftest RangeRestriction
	(is (= (ascii "r \u25b7 T") "r |> T")))

(deftest RangeSubtraction
	(is (= (ascii "r \u2a65 T") "r |>> T")))

(deftest Inverse
	(is (= (ascii "r\u223c") "r~")))

(deftest relationalImage
	(is (= (ascii "r[S]") "r[S]")))

(deftest RightOverriding
	(is (= (ascii "r1 \ue103 r2") "r1 <+ r2")))

(deftest LeftOverriding
	(is (= (ascii "r1 +> r2") "r1 +> r2")))

(deftest DirectProduct
	(is (= (ascii "p \u2297 q") "p >< q")))

(deftest ParallelProduct
	(is (= (ascii "p \u2225 q") "p || q")))

(deftest Iteration
	(is (= (ascii "iterate(r,n)") 
				"iterate(r,n)")))

(deftest Closure
	(is (= (ascii "closure(r)") "closure(r)")))

(deftest rClosure
	(is (= (ascii "rclosure(r)") 
				"rclosure(r)")))

(deftest iClosure
	(is (= (ascii "iclosure(r)") 
				"iclosure(r)")))

(deftest Projection1
	(is (= (ascii "prj1(S,T)") "prj1(S,T)")))


(deftest Projection1_1
	(is (= (ascii "prj1") "prj1")))

(deftest Projection2
	(is (= (ascii "prj2(S,T)") "prj2(S,T)")))

(deftest Projection2_1
	(is (= (ascii "prj2") "prj2")))

(deftest PartialFunctions
	(is (= (ascii "S \u21f8 T") "S +-> T")))

(deftest TotalFunctions
	(is (= (ascii "S \u2192 T") "S --> T")))

(deftest PartialInjections
	(is (= (ascii "S \u2914 T") "S >+> T")))

(deftest TotalInjections
	(is (= (ascii "S \u21a3 T") "S >-> T")))

(deftest PartialSurjections
	(is (= (ascii "S +->> T") "S +->> T")))

(deftest TotalSurjections
	(is (= (ascii "S -->> T") "S -->> T")))

(deftest Bijections
	(is (= (ascii "S \u2916 T") "S >->> T")))

(deftest LambdaAbstraction
	(is (= (ascii "\u03bbz\u00b7(P\u2223E)") 
				"%z.(P|E)")))

(deftest FunctionApplication
	(is (= (ascii "f(E)") "f(E)")))

(deftest FunctionApplication2
	(is (= (ascii "f(E \u21a6 F)") 
				"f(E |-> F)")))

(deftest FiniteSequences
	(is (= (ascii "seq S") "seq S")))

(deftest FiniteNonEmptySequences
	(is (= (ascii "seq1(S)") "seq1(S)")))

(deftest InjectiveSequences
	(is (= (ascii "iseq(S)") "iseq(S)")))

(deftest Permutations
	(is (= (ascii "perm(S)") "perm(S)")))

(deftest SequenceConcatenations
	(is (= (ascii "s\u005et") "s^t")))

(deftest PrependElement
	(is (= (ascii "E -> s") "E -> s")))

(deftest AppendElement
		(is (= (ascii "s <- E") "s <- E")))

(deftest SingletonSequence
	(is (= (ascii "[E]") "[E]")))

(deftest SequenceConstruction
	(is (= (ascii "[E,F]") "[E,F]")))

(deftest Size
	(is (= (ascii "size(s)") "size(s)")))

(deftest Reverse
	(is (= (ascii "rev(s)") "rev(s)")))

(deftest Take
	(is (= (ascii "s /|\\ n") "s /|\\ n")))

(deftest Drop
	(is (= (ascii "s \\|/ n") "s \\|/ n")))

(deftest FirstElement
	(is (= (ascii "first(s)") "first(s)")))

(deftest LastElement
	(is (= (ascii "last(s)") "last(s)")))

(deftest Tail
	(is (= (ascii "tail(s)") "tail(s)")))

(deftest Front
	(is (= (ascii "front(s)") "front(s)")))

(deftest GeneralizedConcatenation
	(is (= (ascii "conc(ss)") "conc(ss)")))

(deftest Skip
	(is (= (ascii "skip") "skip")))

(deftest SimpleSubstitution
	(is (= (ascii "x := E") "x := E")))

(deftest BooleanSubstitution
	(is (= (ascii "x := bool(P)") 
				"x := bool(P)")))

(deftest ChoiceFromSet
	(is (= (ascii "x :\u2208 S") "x :: S")))

(deftest ChoiceByPredicate
	(is (= (ascii "x : P") "x : P")))

(deftest ChoiceByPredicate2
	(is (= (ascii "x :| P") "x :| P")))

(deftest FunctionalOverride
	(is (= (ascii "f(x) := E") "f(x) := E")))

(deftest MultipleSubstitution
	(is (= (ascii "x,y := E,F") "x,y := E,F")))

(deftest ParallelSubstitution
	(is (= (ascii "G \u2225 H") "G || H")))

(deftest SequentialSubstitution
	(is (= (ascii "G ; H") "G ; H")))

(deftest Precondition
	(is (= (ascii "P \u2223 G") "P | G")))

(deftest Guarding
	(is (= (ascii "P ==> G") "P ==> G")))

(deftest Alternatives
	(is (= (ascii "P [] G") "P [] G")))

(deftest UnboundedChoice
	(is (= (ascii "@z \u00b7 G") "@z . G")))

(deftest Context
	(is (= (ascii "CONTEXT") "CONTEXT")))

(deftest Extends
	(is (= (ascii "EXTENDS") "EXTENDS")))

(deftest Sets
	(is (= (ascii "SETS") "SETS")))

(deftest Constants
	(is (= (ascii "CONSTANTS") "CONSTANTS")))

(deftest Axioms
	(is (= (ascii "AXIOMS") "AXIOMS")))

(deftest Theorems
	(is (= (ascii "THEOREMS") "THEOREMS")))

(deftest End
	(is (= (ascii "END") "END")))

(deftest Machine
	(is (= (ascii "MACHINE") "MACHINE")))

(deftest Refines
	(is (= (ascii "REFINES") "REFINES")))

(deftest Sees
	(is (= (ascii "SEES") "SEES")))

(deftest Variables
	(is (= (ascii "VARIABLES") "VARIABLES")))

(deftest Invariant
	(is (= (ascii "INVARIANT") "INVARIANT")))

(deftest Variant
	(is (= (ascii "VARIANT") "VARIANT")))

(deftest Events
	(is (= (ascii "EVENTS") "EVENTS")))

(deftest Any
	(is (= (ascii "ANY") "ANY")))

(deftest Where
	(is (= (ascii "WHERE") "WHERE")))

(deftest With
	(is (= (ascii "WITH") "WITH")))

(deftest Then
	(is (= (ascii "THEN") "THEN")))

(deftest Letter
	(is (= (ascii "abc") "abc")))

(deftest LetterDigit
	(is (= (ascii "abc123") "abc123")))

(deftest LetterUnderscore
	(is (= (ascii "abc_") "abc_")))

(deftest LetterANY
	(is (= (ascii "abcANY") "abcANY"))
	(is (= (ascii "abcany") "abcany")))

(deftest LetterFALSE
	(is (= (ascii "abcFALSE") "abcFALSE"))
	(is (= (ascii "abcfalse") "abcfalse")))

(deftest LetterINTEGER
	(is (= (ascii "abcINTEGER") "abcINTEGER"))
	(is (= (ascii "abcinteger") "abcinteger")))

(deftest LetterINTER
	(is (= (ascii "abcINTER") "abcINTER"))
	(is (= (ascii "abcinter") "abcinter")))

(deftest LetterNAT
	(is (= (ascii "abcNAT") "abcNAT"))
	(is (= (ascii "abcnat") "abcnat")))

(deftest LetterNAT1
	(is (= (ascii "abcNAT1") "abcNAT1"))
	(is (= (ascii "abcnat1") "abcnat1")))

(deftest LetterNATURAL
	(is (= (ascii "abcNATURAL") "abcNATURAL"))
	(is (= (ascii "abcnatural") "abcnatural")))

(deftest LetterNOT
	(is (= (ascii "abcNOT") "abcNOT"))
	(is (= (ascii "abcnot") "abcnot")))

(deftest LetterOR
	(is (= (ascii "abcOR") "abcOR"))
	(is (= (ascii "abcor") "abcor")))

(deftest LetterPOW
	(is (= (ascii "abcPOW") "abcPOW"))
	(is (= (ascii "abcpow") "abcpow")))

(deftest LetterPOW1
	(is (= (ascii "abcPOW1") "abcPOW1"))
	(is (= (ascii "abcpow1") "abcpow1")))

(deftest LetterTRUE
	(is (= (ascii "abcTRUE") "abcTRUE"))
	(is (= (ascii "abctrue") "abctrue")))

(deftest LetterUNION
	(is (= (ascii "abcUNION") "abcUNION"))
	(is (= (ascii "abcunion") "abcunion")))

(deftest LetterDigitUnderscore
	(is (= (ascii "abc123_") "abc123_")))

(deftest LetterDigitANY
	(is (= (ascii "abc123ANY") "abc123ANY"))
	(is (= (ascii "abc123any") "abc123any")))

(deftest LetterDigitFALSE
	(is (= (ascii "abc123FALSE") 
				"abc123FALSE"))
	(is (= (ascii "abc123false") 
				"abc123false")))

(deftest LetterDigitINTEGER
	(is (= (ascii "abc123INTEGER") 
				"abc123INTEGER"))
	(is (= (ascii "abc123integer") 
				"abc123integer")))

(deftest LetterDigitINTER
	(is (= (ascii "abc123INTER") 
				"abc123INTER"))
	(is (= (ascii "abc123inter") 
				"abc123inter")))

(deftest LetterDigitNAT
	(is (= (ascii "abc123NAT") "abc123NAT"))
	(is (= (ascii "abc123nat") "abc123nat")))

(deftest LetterDigitNAT1
	(is (= (ascii "abc123NAT1") "abc123NAT1"))
	(is (= (ascii "abc123nat1") "abc123nat1")))

(deftest LetterDigitNATURAL
	(is (= (ascii "abc123NATURAL") 
				"abc123NATURAL"))
	(is (= (ascii "abc123natural") 
				"abc123natural")))

(deftest LetterDigitNOT
	(is (= (ascii "abc123NOT") "abc123NOT"))
	(is (= (ascii "abc123not") "abc123not")))

(deftest LetterDigitOR
	(is (= (ascii "abc123OR") "abc123OR"))
	(is (= (ascii "abc123or") "abc123or")))

(deftest LetterDigitPOW
	(is (= (ascii "abc123POW") "abc123POW"))
	(is (= (ascii "abc123pow") "abc123pow")))

(deftest LetterDigitPOW1
	(is (= (ascii "abc123POW1") "abc123POW1"))
	(is (= (ascii "abc123pow1") "abc123pow1")))

(deftest LetterDigitTRUE
	(is (= (ascii "abc123TRUE") "abc123TRUE"))
	(is (= (ascii "abc123true") "abc123true")))

(deftest LetterDigitUNION
	(is (= (ascii "abc123UNION") 
				"abc123UNION"))
	(is (= (ascii "abc123union") 
				"abc123union")))

(deftest LetterUnderscoreDigit
	(is (= (ascii "abc_123") "abc_123")))

(deftest LetterUnderscoreANY
	(is (= (ascii "abc_ANY") "abc_ANY"))
	(is (= (ascii "abc_any") "abc_any")))

(deftest LetterUnderscoreFALSE
	(is (= (ascii "abc_FALSE") "abc_FALSE"))
	(is (= (ascii "abc_false") "abc_false")))

(deftest LetterUnderscoreINTEGER
	(is (= (ascii "abc_INTEGER") 
				"abc_INTEGER"))
	(is (= (ascii "abc_integer") 
				"abc_integer")))

(deftest LetterUnderscoreINTER
	(is (= (ascii "abc_INTER") "abc_INTER"))
	(is (= (ascii "abc_inter") "abc_inter")))

(deftest LetterUnderscoreNAT
	(is (= (ascii "abc_NAT") "abc_NAT"))
	(is (= (ascii "abc_nat") "abc_nat")))

(deftest LetterUnderscoreNAT1
	(is (= (ascii "abc_NAT1") "abc_NAT1"))
	(is (= (ascii "abc_nat1") "abc_nat1")))

(deftest LetterUnderscoreNATURAL
	(is (= (ascii "abc_NATURAL") 
				"abc_NATURAL"))
	(is (= (ascii "abc_natural") 
				"abc_natural")))

(deftest LetterUnderscoreNOT
	(is (= (ascii "abc_NOT") "abc_NOT"))
	(is (= (ascii "abc_not") "abc_not")))

(deftest LetterUnderscoreOR
	(is (= (ascii "abc_OR") "abc_OR"))
	(is (= (ascii "abc_or") "abc_or")))

(deftest LetterUnderscorePOW
	(is (= (ascii "abc_pow") "abc_pow"))
	(is (= (ascii "abc_POW") "abc_POW")))

(deftest LetterUnderscorePOW1
	(is (= (ascii "abc_POW1") "abc_POW1"))
	(is (= (ascii "abc_pow1") "abc_pow1")))

(deftest LetterUnderscoreTRUE
	(is (= (ascii "abc_TRUE") "abc_TRUE"))
	(is (= (ascii "abc_true") "abc_true")))

(deftest LetterUnderscoreUNION
	(is (= (ascii "abc_UNION") "abc_UNION"))
	(is (= (ascii "abc_union") "abc_union")))

(deftest LetterANYDigit
	(is (= (ascii "abcANY123") "abcANY123"))
	(is (= (ascii "abcany123") "abcany123")))

(deftest LetterFALSEDigit
	(is (= (ascii "abcFALSE123") 
				"abcFALSE123"))
	(is (= (ascii "abcfalse123") 
				"abcfalse123")))

(deftest LetterINTEGERDigit
	(is (= (ascii "abcINTEGER123") 
				"abcINTEGER123"))
	(is (= (ascii "abcinteger123") 
				"abcinteger123")))

(deftest LetterINTERDigit
	(is (= (ascii "abcINTER123") 
				"abcINTER123"))
	(is (= (ascii "abcinter123") 
				"abcinter123")))

(deftest LetterNATDigit
	(is (= (ascii "abcNAT123") "abcNAT123"))
	(is (= (ascii "abcnat123") "abcnat123")))

(deftest LetterNAT1Digit
	(is (= (ascii "abcNAT1123") "abcNAT1123"))
	(is (= (ascii "abcnat1123") "abcnat1123")))

(deftest LetterNATURALDigit
	(is (= (ascii "abcNATURAL123") 
				"abcNATURAL123"))
	(is (= (ascii "abcnatural123") 
				"abcnatural123")))

(deftest LetterNOTDigit
	(is (= (ascii "abcNOT123") "abcNOT123"))
	(is (= (ascii "abcnot123") "abcnot123")))

(deftest LetterORDigit
	(is (= (ascii "abcOR123") "abcOR123"))
	(is (= (ascii "abcor123") "abcor123")))

(deftest LetterPOWDigit
	(is (= (ascii "abcPOW123") "abcPOW123"))
	(is (= (ascii "abcpow123") "abcpow123")))

(deftest LetterPOW1Digit
	(is (= (ascii "abcPOW1123") "abcPOW1123"))
	(is (= (ascii "abcpow1123") "abcpow1123")))

(deftest LetterTRUEDigit
	(is (= (ascii "abcTRUE123") "abcTRUE123"))
	(is (= (ascii "abctrue123") "abctrue123")))

(deftest LetterUNIONDigit
	(is (= (ascii "abcUNION123") 
				"abcUNION123"))
	(is (= (ascii "abcunion123") 
				"abcunion123")))

(deftest LetterANYUnderscore
	(is (= (ascii "abcANY_") "abcANY_"))
	(is (= (ascii "abcany_") "abcany_")))

(deftest LetterFALSEUnderscore
	(is (= (ascii "abcFALSE_") "abcFALSE_"))
	(is (= (ascii "abcfalse_") "abcfalse_")))

(deftest LetterINTEGERUnderscore
	(is (= (ascii "abcINTEGER_") 
				"abcINTEGER_"))
	(is (= (ascii "abcinteger_") 
				"abcinteger_")))

(deftest LetterINTERUnderscore
	(is (= (ascii "abcINTER_") "abcINTER_"))
	(is (= (ascii "abcinter_") "abcinter_")))

(deftest LetterNATUnderscore
	(is (= (ascii "abcNAT_") "abcNAT_"))
	(is (= (ascii "abcnat_") "abcnat_")))

(deftest LetterNAT1Underscore
	(is (= (ascii "abcNAT1_") "abcNAT1_"))
	(is (= (ascii "abcnat1_") "abcnat1_")))

(deftest LetterNATURALUnderscore
	(is (= (ascii "abcNATURAL_") 
				"abcNATURAL_"))
	(is (= (ascii "abcnatural_") 
				"abcnatural_")))

(deftest LetterNOTUnderscore
	(is (= (ascii "abcNOT_") "abcNOT_"))
	(is (= (ascii "abcnot_") "abcnot_")))

(deftest LetterORUnderscore
	(is (= (ascii "abcOR_") "abcOR_"))
	(is (= (ascii "abcor_") "abcor_")))

(deftest LetterPOWUnderscore
	(is (= (ascii "abcPOW_") "abcPOW_"))
	(is (= (ascii "abcpow_") "abcpow_")))

(deftest LetterPOW1Underscore
	(is (= (ascii "abcPOW1_") "abcPOW1_"))
	(is (= (ascii "abcpow1_") "abcpow1_")))

(deftest LetterTRUEUnderscore
	(is (= (ascii "abcTRUE_") "abcTRUE_"))
	(is (= (ascii "abctrue_") "abctrue_")))

(deftest LetterUNIONUnderscore
	(is (= (ascii "abcUNION_") "abcUNION_"))
	(is (= (ascii "abcunion_") "abcunion_")))

(deftest LetterDigitUnderscoreANY
	(is (= (ascii "abc123_ANY") "abc123_ANY"))
	(is (= (ascii "abc123_any") "abc123_any")))

(deftest LetterDigitUnderscoreFALSE
	(is (= (ascii "abc123_FALSE") 
				"abc123_FALSE"))
	(is (= (ascii "abc123_false") 
				"abc123_false")))

(deftest LetterDigitUnderscoreINTEGER
	(is (= (ascii "abc123_INTEGER") 
				"abc123_INTEGER"))
	(is (= (ascii "abc123_integer") 
				"abc123_integer")))

(deftest LetterDigitUnderscoreINTER
	(is (= (ascii "abc123_INTER") 
				"abc123_INTER"))
	(is (= (ascii "abc123_inter") 
				"abc123_inter")))

(deftest LetterDigitUnderscoreNAT
	(is (= (ascii "abc123_NAT") "abc123_NAT"))
	(is (= (ascii "abc123_nat") "abc123_nat")))

(deftest LetterDigitUnderscoreNAT1
	(is (= (ascii "abc123_NAT1") 
				"abc123_NAT1"))
	(is (= (ascii "abc123_nat1") 
				"abc123_nat1")))

(deftest LetterDigitUnderscoreNATURAL
	(is (= (ascii "abc123_NATURAL") 
				"abc123_NATURAL"))
	(is (= (ascii "abc123_natural") 
				"abc123_natural")))

(deftest LetterDigitUnderscoreNOT
	(is (= (ascii "abc123_NOT") "abc123_NOT"))
	(is (= (ascii "abc123_not") "abc123_not")))

(deftest LetterDigitUnderscoreOR
	(is (= (ascii "abc123_OR") "abc123_OR"))
	(is (= (ascii "abc123_or") "abc123_or")))

(deftest LetterDigitUnderscorePOW
	(is (= (ascii "abc123_POW") "abc123_POW"))
	(is (= (ascii "abc123_pow") "abc123_pow")))

(deftest LetterDigitUnderscorePOW1
	(is (= (ascii "abc123_POW1") 
				"abc123_POW1"))
	(is (= (ascii "abc123_pow1") 
				"abc123_pow1")))

(deftest LetterDigitUnderscoreTRUE
	(is (= (ascii "abc123_TRUE") 
				"abc123_TRUE"))
	(is (= (ascii "abc123_true") 
				"abc123_true")))

(deftest LetterDigitUnderscoreUNION
	(is (= (ascii "abc123_UNION") 
				"abc123_UNION"))
	(is (= (ascii "abc123_union") 
				"abc123_union")))

(deftest LetterDigitANYUnderscore
	(is (= (ascii "abc123ANY_") "abc123ANY_"))
	(is (= (ascii "abc123any_") "abc123any_")))

(deftest LetterDigitFALSEUnderscore
	(is (= (ascii "abc123FALSE_") 
				"abc123FALSE_"))
	(is (= (ascii "abc123false_") 
				"abc123false_")))

(deftest LetterDigitINTEGERUnderscore
	(is (= (ascii "abc123INTEGER_") 
				"abc123INTEGER_"))
	(is (= (ascii "abc123integer_") 
				"abc123integer_")))

(deftest LetterDigitINTERUnderscore
	(is (= (ascii "abc123INTER_") 
				"abc123INTER_"))
	(is (= (ascii "abc123inter_") 
				"abc123inter_")))

(deftest LetterDigitNATUnderscore
	(is (= (ascii "abc123NAT_") "abc123NAT_"))
	(is (= (ascii "abc123nat_") "abc123nat_")))

(deftest LetterDigitNAT1Underscore
	(is (= (ascii "abc123NAT1_") 
				"abc123NAT1_"))
	(is (= (ascii "abc123nat1_") 
				"abc123nat1_")))

(deftest LetterDigitNATURALUnderscore
	(is (= (ascii "abc123NATURAL_") 
				"abc123NATURAL_"))
	(is (= (ascii "abc123natural_") 
				"abc123natural_")))

(deftest LetterDigitNOTUnderscore
	(is (= (ascii "abc123NOT_") "abc123NOT_"))
	(is (= (ascii "abc123not_") "abc123not_")))

(deftest LetterDigitORUnderscore
	(is (= (ascii "abc123OR_") "abc123OR_"))
	(is (= (ascii "abc123or_") "abc123or_")))

(deftest LetterDigitPOWUnderscore
	(is (= (ascii "abc123POW_") "abc123POW_"))
	(is (= (ascii "abc123pow_") "abc123pow_")))

(deftest LetterDigitPOW1Underscore
	(is (= (ascii "abc123POW1_") 
				"abc123POW1_"))
	(is (= (ascii "abc123pow1_") 
				"abc123pow1_")))

(deftest LetterDigitTRUEUnderscore
	(is (= (ascii "abc123TRUE_") 
				"abc123TRUE_"))
	(is (= (ascii "abc123true_") 
				"abc123true_")))

(deftest LetterDigitUNIONUnderscore
	(is (= (ascii "abc123UNION_") 
				"abc123UNION_"))
	(is (= (ascii "abc123union_") 
				"abc123union_")))

(deftest LetterUnderscoreDigitANY
	(is (= (ascii "abc_123ANY") "abc_123ANY"))
	(is (= (ascii "abc_123any") "abc_123any")))

(deftest LetterUnderscoreDigitFALSE
	(is (= (ascii "abc_123FALSE") 
				"abc_123FALSE"))
	(is (= (ascii "abc_123false") 
				"abc_123false")))

(deftest LetterUnderscoreDigitINTEGER
	(is (= (ascii "abc_123INTEGER") 
				"abc_123INTEGER"))
	(is (= (ascii "abc_123integer") 
				"abc_123integer")))

(deftest LetterUnderscoreDigitINTER
	(is (= (ascii "abc_123INTER") 
				"abc_123INTER"))
	(is (= (ascii "abc_123inter") 
				"abc_123inter")))

(deftest LetterUnderscoreDigitANT
	(is (= (ascii "abc_123NAT") "abc_123NAT"))
	(is (= (ascii "abc_123nat") "abc_123nat")))

(deftest LetterUnderscoreDigitNAT1
	(is (= (ascii "abc_123NAT1") 
				"abc_123NAT1"))
	(is (= (ascii "abc_123nat1") 
				"abc_123nat1")))

(deftest LetterUnderscoreDigitNATURAL
	(is (= (ascii "abc_123NATURAL") 
				"abc_123NATURAL"))
	(is (= (ascii "abc_123natural") 
				"abc_123natural")))

(deftest LetterUnderscoreDigitNOT
	(is (= (ascii "abc_123NOT") "abc_123NOT"))
	(is (= (ascii "abc_123not") "abc_123not")))

(deftest LetterUnderscoreDigitOR
	(is (= (ascii "abc_123OR") "abc_123OR"))
	(is (= (ascii "abc_123or") "abc_123or")))

(deftest LetterUnderscoreDigitPOW
	(is (= (ascii "abc_123POW") "abc_123POW"))
	(is (= (ascii "abc_123pow") "abc_123pow")))

(deftest LetterUnderscoreDigitPOW1
	(is (= (ascii "abc_123POW1") 
				"abc_123POW1"))
	(is (= (ascii "abc_123pow1") 
				"abc_123pow1")))

(deftest LetterUnderscoreDigitTRUE
	(is (= (ascii "abc_123TRUE") 
				"abc_123TRUE"))
	(is (= (ascii "abc_123true") 
				"abc_123true")))

(deftest LetterUnderscoreDigitUNION
	(is (= (ascii "abc_123UNION") 
				"abc_123UNION"))
	(is (= (ascii "abc_123union") 
				"abc_123union")))

(deftest LetterUnderscoreANYDigit
	(is (= (ascii "abc_ANY123") "abc_ANY123"))
	(is (= (ascii "abc_any123") "abc_any123")))

(deftest LetterUnderscoreFALSEDigit
	(is (= (ascii "abc_FALSE123") 
				"abc_FALSE123"))
	(is (= (ascii "abc_false123") 
				"abc_false123")))

(deftest LetterUnderscoreINTEGERDigit
	(is (= (ascii "abc_INTEGER123") 
				"abc_INTEGER123"))
	(is (= (ascii "abc_integer123") 
				"abc_integer123")))

(deftest LetterUnderscoreINTERDigit
	(is (= (ascii "abc_INTER123") 
				"abc_INTER123"))
	(is (= (ascii "abc_inter123") 
				"abc_inter123")))

(deftest LetterUnderscoreNATDigit
	(is (= (ascii "abc_NAT123") "abc_NAT123"))
	(is (= (ascii "abc_nat123") "abc_nat123")))

(deftest LetterUnderscoreNAT1Digit
	(is (= (ascii "abc_NAT1123") 
				"abc_NAT1123"))
	(is (= (ascii "abc_nat1123") 
				"abc_nat1123")))

(deftest LetterUnderscoreNATURALDigit
	(is (= (ascii "abc_NATURAL123") 
				"abc_NATURAL123"))
	(is (= (ascii "abc_natural123") 
				"abc_natural123")))

(deftest LetterUnderscoreNOTDigit
	(is (= (ascii "abc_NOT123") "abc_NOT123"))
	(is (= (ascii "abc_not123") "abc_not123")))

(deftest LetterUnderscoreORDigit
	(is (= (ascii "abc_OR123") "abc_OR123"))
	(is (= (ascii "abc_or123") "abc_or123")))

(deftest LetterUnderscorePOWDigit
	(is (= (ascii "abc_POW123") "abc_POW123"))
	(is (= (ascii "abc_pow123") "abc_pow123")))

(deftest LetterUnderscorePOW1Digit
	(is (= (ascii "abc_POW1123") 
				"abc_POW1123"))
	(is (= (ascii "abc_pow1123") 
				"abc_pow1123")))

(deftest LetterUnderscoreTRUEDigit
	(is (= (ascii "abc_TRUE123") 
				"abc_TRUE123"))
	(is (= (ascii "abc_true123") 
				"abc_true123")))

(deftest LetterUnderscoreUNIONDigit
	(is (= (ascii "abc_UNION123") 
				"abc_UNION123"))
	(is (= (ascii "abc_union123") 
				"abc_union123")))

(deftest LetterANYDigitUnderscore
	(is (= (ascii "abcANY123_") "abcANY123_"))
	(is (= (ascii "abcany123_") "abcany123_")))

(deftest LetterFALSEDigitUnderscore
	(is (= (ascii "abcFALSE123_") 
				"abcFALSE123_"))
	(is (= (ascii "abcfalse123_") 
				"abcfalse123_")))

(deftest LetterINTEGERDigitUnderscore
	(is (= (ascii "abcINTEGER123_") 
				"abcINTEGER123_"))
	(is (= (ascii "abcinteger123_") 
				"abcinteger123_")))

(deftest LetterINTERDigitUnderscore
	(is (= (ascii "abcINTER123_") 
				"abcINTER123_"))
	(is (= (ascii "abcinter123_") 
				"abcinter123_")))

(deftest LetterNATDigitUnderscore
	(is (= (ascii "abcNAT123_") "abcNAT123_"))
	(is (= (ascii "abcnat123_") "abcnat123_")))

(deftest LetterNAT1DigitUnderscore
	(is (= (ascii "abcNAT1123_") 
				"abcNAT1123_"))
	(is (= (ascii "abcnat1123_") 
				"abcnat1123_")))

(deftest LetterNATURALDigitUnderscore
	(is (= (ascii "abcNATURAL123_") 
				"abcNATURAL123_"))
	(is (= (ascii "abcnatural123_") 
				"abcnatural123_")))

(deftest LetterNOTDigitUnderscore
	(is (= (ascii "abcNOT123_") "abcNOT123_"))
	(is (= (ascii "abcnot123_") "abcnot123_")))

(deftest LetterORDigitUnderscore
	(is (= (ascii "abcOR123_") "abcOR123_"))
	(is (= (ascii "abcor123_") "abcor123_")))

(deftest LetterPOWDigitUnderscore
	(is (= (ascii "abcPOW123_") "abcPOW123_"))
	(is (= (ascii "abcpow123_") "abcpow123_")))

(deftest LetterPOW1DigitUnderscore
	(is (= (ascii "abcPOW1123_") 
				"abcPOW1123_"))
	(is (= (ascii "abcpow1123_") 
				"abcpow1123_")))

(deftest LetterTRUEDigitUnderscore
	(is (= (ascii "abcTRUE123_") 
				"abcTRUE123_"))
	(is (= (ascii "abctrue123_") 
				"abctrue123_")))

(deftest LetterUNIONDigitUnderscore
	(is (= (ascii "abcUNION123_") 
				"abcUNION123_"))
	(is (= (ascii "abcunion123_") 
				"abcunion123_")))

(deftest LetterANYUnderscoreDigit
	(is (= (ascii "abcANY_123") "abcANY_123"))
	(is (= (ascii "abcany_123") "abcany_123")))

(deftest LetterFALSEUnderscoreDigit
	(is (= (ascii "abcFALSE_123") 
				"abcFALSE_123"))
	(is (= (ascii "abcfalse_123") 
				"abcfalse_123")))

(deftest LetterINTEGERUnderscoreDigit
	(is (= (ascii "abcINTEGER_123") 
				"abcINTEGER_123"))
	(is (= (ascii "abcinteger_123") 
				"abcinteger_123")))

(deftest LetterINTERUnderscoreDigit
	(is (= (ascii "abcINTER_123") 
				"abcINTER_123"))
	(is (= (ascii "abcinter_123") 
				"abcinter_123")))

(deftest LetterNATUnderscoreDigit
	(is (= (ascii "abcNAT_123") "abcNAT_123"))
	(is (= (ascii "abcnat_123") "abcnat_123")))

(deftest LetterNAT1UnderscoreDigit
	(is (= (ascii "abcNAT1_123") 
				"abcNAT1_123"))
	(is (= (ascii "abcnat1_123") 
				"abcnat1_123")))

(deftest LetterNATURALUnderscoreDigit
	(is (= (ascii "abcNATURAL_123") 
				"abcNATURAL_123"))
	(is (= (ascii "abcnatural_123") 
				"abcnatural_123")))

(deftest LetterNOTUnderscoreDigit
	(is (= (ascii "abcNOT_123") "abcNOT_123"))
	(is (= (ascii "abcnot_123") "abcnot_123")))

(deftest LetterORUnderscoreDigit
	(is (= (ascii "abcOR_123") "abcOR_123"))
	(is (= (ascii "abcor_123") "abcor_123")))

(deftest LetterPOWUnderscoreDigit
	(is (= (ascii "abcPOW_123") "abcPOW_123"))
	(is (= (ascii "abcpow_123") "abcpow_123")))

(deftest LetterPOW1UnderscoreDigit
	(is (= (ascii "abcPOW1_123") 
				"abcPOW1_123"))
	(is (= (ascii "abcpow1_123") 
				"abcpow1_123")))

(deftest LetterTRUEUnderscoreDigit
	(is (= (ascii "abcTRUE_123") 
				"abcTRUE_123"))
	(is (= (ascii "abctrue_123") 
				"abctrue_123")))

(deftest LetterUNIONUnderscoreDigit
	(is (= (ascii "abcUNION_123") 
				"abcUNION_123"))
	(is (= (ascii "abcunion_123") 
				"abcunion_123")))

(deftest Digit
	(is (= (ascii "123") "123")))

(deftest DigitLetter
	(is (= (ascii "123abc") "123abc")))

(deftest DigitUnderscore
	(is (= (ascii "123_") "123_")))

(deftest DigitANY
	(is (= (ascii "123ANY") "123ANY"))
	(is (= (ascii "123any") "123any")))

(deftest DigitFALSE
	(is (= (ascii "123FALSE") "123FALSE"))
	(is (= (ascii "123false") "123false")))

(deftest DigitINTEGER
	(is (= (ascii "123INTEGER") "123INTEGER"))
	(is (= (ascii "123integer") "123integer")))

(deftest DigitINTER
	(is (= (ascii "123INTER") "123INTER"))
	(is (= (ascii "123inter") "123inter")))

(deftest DigitNAT
	(is (= (ascii "123NAT") "123NAT"))
	(is (= (ascii "123nat") "123nat")))

(deftest DigitNAT1
	(is (= (ascii "123NAT1") "123NAT1"))
	(is (= (ascii "123nat1") "123nat1")))

(deftest DigitNATURAL
	(is (= (ascii "123NATURAL") "123NATURAL"))
	(is (= (ascii "123natural") "123natural")))

(deftest DigitNOT
	(is (= (ascii "123NOT") "123NOT"))
	(is (= (ascii "123not") "123not")))

(deftest DigitOR
	(is (= (ascii "123OR") "123OR"))
	(is (= (ascii "123or") "123or")))

(deftest DigitPOW
	(is (= (ascii "123POW") "123POW"))
	(is (= (ascii "123pow") "123pow")))

(deftest DigitPOW1
	(is (= (ascii "123POW1") "123POW1"))
	(is (= (ascii "123pow1") "123pow1")))

(deftest DigitTRUE
	(is (= (ascii "123TRUE") "123TRUE"))
	(is (= (ascii "123true") "123true")))

(deftest DigitUNION
	(is (= (ascii "123UNION") "123UNION"))
	(is (= (ascii "123union") "123union")))

(deftest DigitLetterUnderscore
	(is (= (ascii "123abc_") "123abc_")))

(deftest DigitLetterANY
	(is (= (ascii "123abcANY") "123abcANY"))
	(is (= (ascii "123abcany") "123abcany")))

(deftest DigitLetterFALSE
	(is (= (ascii "123abcFALSE") 
				"123abcFALSE"))
	(is (= (ascii "123abcfalse") 
				"123abcfalse")))

(deftest DigitLetterINTEGER
	(is (= (ascii "123abcINTEGER") 
				"123abcINTEGER"))
	(is (= (ascii "123abcinteger") 
				"123abcinteger")))

(deftest DigitLetterINTER
	(is (= (ascii "123abcINTER") 
				"123abcINTER"))
	(is (= (ascii "123abcinter") 
				"123abcinter")))

(deftest DigitLetterNAT
	(is (= (ascii "123abcNAT") "123abcNAT"))
	(is (= (ascii "123abcnat") "123abcnat")))

(deftest DigitLetterNAT1
	(is (= (ascii "123abcNAT1") "123abcNAT1"))
	(is (= (ascii "123abcnat1") "123abcnat1")))

(deftest DigitLetterNATURAL
	(is (= (ascii "123abcNATURAL") 
				"123abcNATURAL"))
	(is (= (ascii "123abcnatural") 
				"123abcnatural")))

(deftest DigitLetterNOT
	(is (= (ascii "123abcNOT") "123abcNOT"))
	(is (= (ascii "123abcnot") "123abcnot")))

(deftest DigitLetterOR
	(is (= (ascii "123abcOR") "123abcOR"))
	(is (= (ascii "123abcor") "123abcor")))

(deftest DigitLetterPOW
	(is (= (ascii "123abcPOW") "123abcPOW"))
	(is (= (ascii "123abcpow") "123abcpow")))

(deftest DigitLetterPOW1
	(is (= (ascii "123abcPOW1") "123abcPOW1"))
	(is (= (ascii "123abcpow1") "123abcpow1")))

(deftest DigitLetterTRUE
	(is (= (ascii "123abcTRUE") "123abcTRUE"))
	(is (= (ascii "123abctrue") "123abctrue")))

(deftest DigitLetterUNION
	(is (= (ascii "123abcUNION") 
				"123abcUNION"))
	(is (= (ascii "123abcunion") 
				"123abcunion")))

(deftest DigitUnderscoreLetter
	(is (= (ascii "123_abc") "123_abc")))

(deftest DigitUnderscoreANY
	(is (= (ascii "123_ANY") "123_ANY"))
	(is (= (ascii "123_any") "123_any")))

(deftest DigitUnderscoreFALSE
	(is (= (ascii "123_FALSE") "123_FALSE"))
	(is (= (ascii "123_false") "123_false")))

(deftest DigitUnderscoreINTEGER
	(is (= (ascii "123_INTEGER") 
				"123_INTEGER"))
	(is (= (ascii "123_integer") 
				"123_integer")))

(deftest DigitUnderscoreINTER
	(is (= (ascii "123_INTER") "123_INTER"))
	(is (= (ascii "123_inter") "123_inter")))

(deftest DigitUnderscoreNAT
	(is (= (ascii "123_NAT") "123_NAT"))
	(is (= (ascii "123_nat") "123_nat")))

(deftest DigitUnderscoreNAT1
	(is (= (ascii "123_NAT1") "123_NAT1"))
	(is (= (ascii "123_nat1") "123_nat1")))

(deftest DigitUnderscoreNATURAL
	(is (= (ascii "123_NATURAL") 
				"123_NATURAL"))
	(is (= (ascii "123_natural") 
				"123_natural")))

(deftest DigitUnderscoreNOT
	(is (= (ascii "123_NOT") "123_NOT"))
	(is (= (ascii "123_not") "123_not")))

(deftest DigitUnderscoreOR
	(is (= (ascii "123_OR") "123_OR"))
	(is (= (ascii "123_or") "123_or")))

(deftest DigitUnderscorePOW
	(is (= (ascii "123_POW") "123_POW"))
	(is (= (ascii "123_pow") "123_pow")))

(deftest DigitUnderscorePOW1
	(is (= (ascii "123_POW1") "123_POW1"))
	(is (= (ascii "123_pow1") "123_pow1")))

(deftest DigitUnderscoreTRUE
	(is (= (ascii "123_TRUE") "123_TRUE"))
	(is (= (ascii "123_true") "123_true")))

(deftest DigitUnderscoreUNION
	(is (= (ascii "123_UNION") "123_UNION"))
	(is (= (ascii "123_union") "123_union")))

(deftest DigitANYLetter
	(is (= (ascii "123ANYabc") "123ANYabc"))
	(is (= (ascii "123anyabc") "123anyabc")))

(deftest DigitFALSELetter
	(is (= (ascii "123FALSEabc") 
				"123FALSEabc"))
	(is (= (ascii "123falseabc") 
				"123falseabc")))

(deftest DigitINTEGERLetter
	(is (= (ascii "123INTEGERabc") 
				"123INTEGERabc"))
	(is (= (ascii "123integerabc") 
				"123integerabc")))

(deftest DigitINTERLetter
	(is (= (ascii "123INTERabc") 
				"123INTERabc"))
	(is (= (ascii "123interabc") 
				"123interabc")))

(deftest DigitNATLetter
	(is (= (ascii "123NATabc") "123NATabc"))
	(is (= (ascii "123natabc") "123natabc")))

(deftest DigitNAT1Letter
	(is (= (ascii "123NAT1abc") "123NAT1abc"))
	(is (= (ascii "123nat1abc") "123nat1abc")))

(deftest DigitNATURALLetter
	(is (= (ascii "123NATURALabc") 
				"123NATURALabc"))
	(is (= (ascii "123naturalabc") 
				"123naturalabc")))

(deftest DigitNOTLetter
	(is (= (ascii "123NOTabc") "123NOTabc"))
	(is (= (ascii "123notabc") "123notabc")))

(deftest DigitORLetter
	(is (= (ascii "123ORabc") "123ORabc"))
	(is (= (ascii "123orabc") "123orabc")))

(deftest DigitPOWLetter
	(is (= (ascii "123POWabc") "123POWabc"))
	(is (= (ascii "123powabc") "123powabc")))

(deftest DigitPOW1Letter
	(is (= (ascii "123POW1abc") "123POW1abc"))
	(is (= (ascii "123pow1abc") "123pow1abc")))

(deftest DigitTRUELetter
	(is (= (ascii "123TRUEabc") "123TRUEabc"))
	(is (= (ascii "123trueabc") "123trueabc")))

(deftest DigitUNIONLetter
	(is (= (ascii "123UNIONabc") 
				"123UNIONabc"))
	(is (= (ascii "123unionabc") 
				"123unionabc")))

(deftest DigitANYUnderscore
	(is (= (ascii "123ANY_") "123ANY_"))
	(is (= (ascii "123any_") "123any_")))

(deftest DigitFALSEUnderscore
	(is (= (ascii "123FALSE_") "123FALSE_"))
	(is (= (ascii "123false_") "123false_")))

(deftest DigitINTEGERUnderscore
	(is (= (ascii "123INTEGER_") 
				"123INTEGER_"))
	(is (= (ascii "123integer_") 
				"123integer_")))

(deftest DigitINTERUnderscore
	(is (= (ascii "123INTER_") "123INTER_"))
	(is (= (ascii "123inter_") "123inter_")))

(deftest DigitNATUnderscore
	(is (= (ascii "123NAT_") "123NAT_"))
	(is (= (ascii "123nat_") "123nat_")))

(deftest DigitNAT1Underscore
	(is (= (ascii "123NAT1_") "123NAT1_"))
	(is (= (ascii "123nat1_") "123nat1_")))

(deftest DigitNATURALUnderscore
	(is (= (ascii "123NATURAL_") 
				"123NATURAL_"))
	(is (= (ascii "123natural_") 
				"123natural_")))

(deftest DigitNOTUnderscore
	(is (= (ascii "123NOT_") "123NOT_"))
	(is (= (ascii "123not_") "123not_")))

(deftest DigitORUnderscore
	(is (= (ascii "123OR_") "123OR_"))
	(is (= (ascii "123or_") "123or_")))

(deftest DigitPOWUnderscore
	(is (= (ascii "123POW_") "123POW_"))
	(is (= (ascii "123pow_") "123pow_")))

(deftest DigitPOW1Underscore
	(is (= (ascii "123POW1_") "123POW1_"))
	(is (= (ascii "123pow1_") "123pow1_")))

(deftest DigitTRUEUnderscore
	(is (= (ascii "123TRUE_") "123TRUE_"))
	(is (= (ascii "123true_") "123true_")))

(deftest DigitUNIONUnderscore
	(is (= (ascii "123UNION_") "123UNION_"))
	(is (= (ascii "123union_") "123union_")))

(deftest DigitLetterUnderscoreANY
	(is (= (ascii "123abc_ANY") "123abc_ANY"))
	(is (= (ascii "123abc_any") "123abc_any")))

(deftest DigitLetterUnderscoreFALSE
	(is (= (ascii "123abc_FALSE") 
				"123abc_FALSE"))
	(is (= (ascii "123abc_false") 
				"123abc_false")))

(deftest DigitLetterUnderscoreINTEGER
	(is (= (ascii "123abc_INTEGER") 
				"123abc_INTEGER"))
	(is (= (ascii "123abc_integer") 
				"123abc_integer")))

(deftest DigitLetterUnderscoreINTER
	(is (= (ascii "123abc_INTER") 
				"123abc_INTER"))
	(is (= (ascii "123abc_inter") 
				"123abc_inter")))

(deftest DigitLetterUnderscoreNAT
	(is (= (ascii "123abc_NAT") "123abc_NAT"))
	(is (= (ascii "123abc_nat") "123abc_nat")))

(deftest DigitLetterUnderscoreNAT1
	(is (= (ascii "123abc_NAT1") 
				"123abc_NAT1"))
	(is (= (ascii "123abc_nat1") 
				"123abc_nat1")))

(deftest DigitLetterUnderscoreNATURAL
	(is (= (ascii "123abc_NATURAL") 
				"123abc_NATURAL"))
	(is (= (ascii "123abc_natural") 
				"123abc_natural")))

(deftest DigitLetterUnderscoreNOT
	(is (= (ascii "123abc_NOT") "123abc_NOT"))
	(is (= (ascii "123abc_not") "123abc_not")))

(deftest DigitLetterUnderscoreOR
	(is (= (ascii "123abc_OR") "123abc_OR"))
	(is (= (ascii "123abc_or") "123abc_or")))

(deftest DigitLetterUnderscorePOW
	(is (= (ascii "123abc_POW") "123abc_POW"))
	(is (= (ascii "123abc_pow") "123abc_pow")))

(deftest DigitLetterUnderscorePOW1
	(is (= (ascii "123abc_POW1") 
				"123abc_POW1"))
	(is (= (ascii "123abc_pow1") 
				"123abc_pow1")))

(deftest DigitLetterUnderscoreTRUE
	(is (= (ascii "123abc_TRUE") 
				"123abc_TRUE"))
	(is (= (ascii "123abc_true") 
				"123abc_true")))

(deftest DigitLetterUnderscoreUNION
	(is (= (ascii "123abc_UNION") 
				"123abc_UNION"))
	(is (= (ascii "123abc_union") 
				"123abc_union")))

(deftest DigitLetterANYUnderscore
	(is (= (ascii "123abcANY_") "123abcANY_"))
	(is (= (ascii "123abcany_") "123abcany_")))

(deftest DigitLetterFALSEUnderscore
	(is (= (ascii "123abcFALSE_") 
				"123abcFALSE_"))
	(is (= (ascii "123abcfalse_") 
				"123abcfalse_")))

(deftest DigitLetterINTEGERUnderscore
	(is (= (ascii "123abcINTEGER_") 
				"123abcINTEGER_"))
	(is (= (ascii "123abcinteger_") 
				"123abcinteger_")))

(deftest DigitLetterINTERUnderscore
	(is (= (ascii "123abcINTER_") 
				"123abcINTER_"))
	(is (= (ascii "123abcinter_") 
				"123abcinter_")))

(deftest DigitLetterNATUnderscore
	(is (= (ascii "123abcNAT_") "123abcNAT_"))
	(is (= (ascii "123abcnat_") "123abcnat_")))

(deftest DigitLetterNAT1Underscore
	(is (= (ascii "123abcNAT1_") 
				"123abcNAT1_"))
	(is (= (ascii "123abcnat1_") 
				"123abcnat1_")))

(deftest DigitLetterNATURALUnderscore
	(is (= (ascii "123abcNATURAL_") 
				"123abcNATURAL_"))
	(is (= (ascii "123abcnatural_") 
				"123abcnatural_")))

(deftest DigitLetterNOTUnderscore
	(is (= (ascii "123abcNOT_") "123abcNOT_"))
	(is (= (ascii "123abcnot_") "123abcnot_")))

(deftest DigitLetterORUnderscore
	(is (= (ascii "123abcOR_") "123abcOR_"))
	(is (= (ascii "123abcor_") "123abcor_")))

(deftest DigitLetterPOWUnderscore
	(is (= (ascii "123abcPOW_") "123abcPOW_"))
	(is (= (ascii "123abcpow_") "123abcpow_")))

(deftest DigitLetterPOW1Underscore
	(is (= (ascii "123abcPOW1_") 
				"123abcPOW1_"))
	(is (= (ascii "123abcpow1_") 
				"123abcpow1_")))

(deftest DigitLetterTRUEUnderscore
	(is (= (ascii "123abcTRUE_") 
				"123abcTRUE_"))
	(is (= (ascii "123abctrue_") 
				"123abctrue_")))

(deftest DigitLetterUNIONUnderscore
	(is (= (ascii "123abcUNION_") 
				"123abcUNION_"))
	(is (= (ascii "123abcunion_") 
				"123abcunion_")))

(deftest DigitUnderscoreLetterANY
	(is (= (ascii "123_abcANY") "123_abcANY"))
	(is (= (ascii "123_abcany") "123_abcany")))

(deftest DigitUnderscoreLetterFALSE
	(is (= (ascii "123_abcFALSE") 
				"123_abcFALSE"))
	(is (= (ascii "123_abcfalse") 
				"123_abcfalse")))

(deftest DigitUnderscoreLetterINTEGER
	(is (= (ascii "123_abcINTEGER") 
				"123_abcINTEGER"))
	(is (= (ascii "123_abcinteger") 
				"123_abcinteger")))

(deftest DigitUnderscoreLetterINTER
	(is (= (ascii "123_abcINTER") 
				"123_abcINTER"))
	(is (= (ascii "123_abcinter") 
				"123_abcinter")))

(deftest DigitUnderscoreLetterNAT
	(is (= (ascii "123_abcNAT") "123_abcNAT"))
	(is (= (ascii "123_abcnat") "123_abcnat")))

(deftest DigitUnderscoreLetterNAT1
	(is (= (ascii "123_abcNAT1") 
				"123_abcNAT1"))
	(is (= (ascii "123_abcnat1") 
				"123_abcnat1")))

(deftest DigitUnderscoreLetterNATURAL
	(is (= (ascii "123_abcNATURAL") 
				"123_abcNATURAL"))
	(is (= (ascii "123_abcnatural") 
				"123_abcnatural")))

(deftest DigitUnderscoreLetterNOT
	(is (= (ascii "123_abcNOT") "123_abcNOT"))
	(is (= (ascii "123_abcnot") "123_abcnot")))

(deftest DigitUnderscoreLetterOR
	(is (= (ascii "123_abcOR") "123_abcOR"))
	(is (= (ascii "123_abcor") "123_abcor")))

(deftest DigitUnderscoreLetterPOW
	(is (= (ascii "123_abcPOW") "123_abcPOW"))
	(is (= (ascii "123_abcpow") "123_abcpow")))

(deftest DigitUnderscoreLetterPOW1
	(is (= (ascii "123_abcPOW1") 
				"123_abcPOW1"))
	(is (= (ascii "123_abcpow1") 
				"123_abcpow1")))

(deftest DigitUnderscoreLetterTRUE
	(is (= (ascii "123_abcTRUE") 
				"123_abcTRUE"))
	(is (= (ascii "123_abctrue") 
				"123_abctrue")))

(deftest DigitUnderscoreLetterUNION
	(is (= (ascii "123_abcUNION") 
				"123_abcUNION"))
	(is (= (ascii "123_abcunion") 
				"123_abcunion")))

(deftest DigitUnderscoreANYLetter
	(is (= (ascii "123_ANYabc") "123_ANYabc"))
	(is (= (ascii "123_anyabc") "123_anyabc")))

(deftest DigitUnderscoreFALSELetter
	(is (= (ascii "123_FALSEabc") 
				"123_FALSEabc"))
	(is (= (ascii "123_falseabc") 
				"123_falseabc")))

(deftest DigitUnderscoreINTEGERLetter
	(is (= (ascii "123_INTEGERabc") 
				"123_INTEGERabc"))
	(is (= (ascii "123_integerabc") 
				"123_integerabc")))

(deftest DigitUnderscoreINTERLetter
	(is (= (ascii "123_INTERabc") 
				"123_INTERabc"))
	(is (= (ascii "123_interabc") 
				"123_interabc")))

(deftest DigitUnderscoreNATLetter
	(is (= (ascii "123_NATabc") "123_NATabc"))
	(is (= (ascii "123_natabc") "123_natabc")))

(deftest DigitUnderscoreNAT1Letter
	(is (= (ascii "123_NAT1abc") 
				"123_NAT1abc"))
	(is (= (ascii "123_nat1abc") 
				"123_nat1abc")))

(deftest DigitUnderscoreNATURALLetter
	(is (= (ascii "123_NATURALabc") 
				"123_NATURALabc"))
	(is (= (ascii "123_naturalabc") 
				"123_naturalabc")))

(deftest DigitUnderscoreNOTLetter
	(is (= (ascii "123_NOTabc") "123_NOTabc"))
	(is (= (ascii "123_notabc") "123_notabc")))

(deftest DigitUnderscoreORLetter
	(is (= (ascii "123_ORabc") "123_ORabc"))
	(is (= (ascii "123_orabc") "123_orabc")))

(deftest DigitUnderscorePOWLetter
	(is (= (ascii "123_POWabc") "123_POWabc"))
	(is (= (ascii "123_powabc") "123_powabc")))

(deftest DigitUnderscorePOW1Letter
	(is (= (ascii "123_POW1abc") 
				"123_POW1abc"))
	(is (= (ascii "123_pow1abc") 
				"123_pow1abc")))

(deftest DigitUnderscoreTRUELetter
	(is (= (ascii "123_TRUEabc") 
				"123_TRUEabc"))
	(is (= (ascii "123_trueabc") 
				"123_trueabc")))

(deftest DigitUnderscoreUNIONLetter
	(is (= (ascii "123_UNIONabc") 
				"123_UNIONabc"))
	(is (= (ascii "123_unionabc") 
				"123_unionabc")))

(deftest DigitANYLetterUnderscore
	(is (= (ascii "123ANYabc_") "123ANYabc_"))
	(is (= (ascii "123anyabc_") "123anyabc_")))

(deftest DigitFALSELetterUnderscore
	(is (= (ascii "123FALSEabc_") 
				"123FALSEabc_"))
	(is (= (ascii "123falseabc_") 
				"123falseabc_")))

(deftest DigitINTEGERLetterUnderscore
	(is (= (ascii "123INTEGERabc_") 
				"123INTEGERabc_"))
	(is (= (ascii "123integerabc_") 
				"123integerabc_")))

(deftest DigitINTERLetterUnderscore
	(is (= (ascii "123INTERabc_") 
				"123INTERabc_"))
	(is (= (ascii "123interabc_") 
				"123interabc_")))

(deftest DigitNATLetterUnderscore
	(is (= (ascii "123NATabc_") "123NATabc_"))
	(is (= (ascii "123natabc_") "123natabc_")))

(deftest DigitNAT1LetterUnderscore
	(is (= (ascii "123NAT1abc_") 
				"123NAT1abc_"))
	(is (= (ascii "123nat1abc_") 
				"123nat1abc_")))

(deftest DigitNATURALLetterUnderscore
	(is (= (ascii "123NATURALabc_") 
				"123NATURALabc_"))
	(is (= (ascii "123naturalabc_") 
				"123naturalabc_")))

(deftest DigitNOTLetterUnderscore
	(is (= (ascii "123NOTabc_") "123NOTabc_"))
	(is (= (ascii "123notabc_") "123notabc_")))

(deftest DigitORLetterUnderscore
	(is (= (ascii "123ORabc_") "123ORabc_"))
	(is (= (ascii "123orabc_") "123orabc_")))

(deftest DigitPOWLetterUnderscore
	(is (= (ascii "123POWabc_") "123POWabc_"))
	(is (= (ascii "123powabc_") "123powabc_")))

(deftest DigitPOW1LetterUnderscore
	(is (= (ascii "123POW1abc_") 
				"123POW1abc_"))
	(is (= (ascii "123pow1abc_") 
				"123pow1abc_")))

(deftest DigitTRUELetterUnderscore
	(is (= (ascii "123TRUEabc_") 
				"123TRUEabc_"))
	(is (= (ascii "123trueabc_") 
				"123trueabc_")))

(deftest DigitUNIONLetterUnderscore
	(is (= (ascii "123UNIONabc_") 
				"123UNIONabc_"))
	(is (= (ascii "123unionabc_") 
				"123unionabc_")))

(deftest DigitANYUnderscoreLetter
	(is (= (ascii "123ANY_abc") "123ANY_abc"))
	(is (= (ascii "123any_abc") "123any_abc")))

(deftest DigitFALSEUnderscoreLetter
	(is (= (ascii "123FALSE_abc") 
				"123FALSE_abc"))
	(is (= (ascii "123false_abc") 
				"123false_abc")))

(deftest DigitINTEGERUnderscoreLetter
	(is (= (ascii "123INTEGER_abc") 
				"123INTEGER_abc"))
	(is (= (ascii "123integer_abc") 
				"123integer_abc")))

(deftest DigitINTERUnderscoreLetter
	(is (= (ascii "123INTER_abc") 
				"123INTER_abc"))
	(is (= (ascii "123inter_abc") 
				"123inter_abc")))

(deftest DigitNATUnderscoreLetter
	(is (= (ascii "123NAT_abc") "123NAT_abc"))
	(is (= (ascii "123nat_abc") "123nat_abc")))

(deftest DigitNAT1UnderscoreLetter
	(is (= (ascii "123NAT1_abc") 
				"123NAT1_abc"))
	(is (= (ascii "123nat1_abc") 
				"123nat1_abc")))

(deftest DigitNATURALUnderscoreLetter
	(is (= (ascii "123NATURAL_abc") 
				"123NATURAL_abc"))
	(is (= (ascii "123natural_abc") 
				"123natural_abc")))

(deftest DigitNOTUnderscoreLetter
	(is (= (ascii "123NOT_abc") "123NOT_abc"))
	(is (= (ascii "123not_abc") "123not_abc")))

(deftest DigitORUnderscoreLetter
	(is (= (ascii "123OR_abc") "123OR_abc"))
	(is (= (ascii "123or_abc") "123or_abc")))

(deftest DigitPOWUnderscoreLetter
	(is (= (ascii "123POW_abc") "123POW_abc"))
	(is (= (ascii "123pow_abc") "123pow_abc")))

(deftest DigitPOW1UnderscoreLetter
	(is (= (ascii "123POW1_abc") 
				"123POW1_abc"))
	(is (= (ascii "123pow1_abc") 
				"123pow1_abc")))

(deftest DigitTRUEUnderscoreLetter
	(is (= (ascii "123TRUE_abc") 
				"123TRUE_abc"))
	(is (= (ascii "123true_abc") 
				"123true_abc")))

(deftest DigitUNIONUnderscoreLetter
	(is (= (ascii "123UNION_abc") 
				"123UNION_abc"))
	(is (= (ascii "123union_abc") 
				"123union_abc")))

(deftest Underscore
	(is (= (ascii "_") "_")))

(deftest UnderscoreLetter
	(is (= (ascii "_abc") "_abc")))

(deftest UnderscoreDigit
	(is (= (ascii "_123") "_123")))

(deftest UnderscoreANY
	(is (= (ascii "_ANY") "_ANY"))
	(is (= (ascii "_any") "_any")))

(deftest UnderscoreFALSE
	(is (= (ascii "_FALSE") "_FALSE"))
	(is (= (ascii "_false") "_false")))

(deftest UnderscoreINTEGER
	(is (= (ascii "_INTEGER") "_INTEGER"))
	(is (= (ascii "_integer") "_integer")))

(deftest UnderscoreINTER
	(is (= (ascii "_INTER") "_INTER"))
	(is (= (ascii "_inter") "_inter")))

(deftest UnderscoreNAT
	(is (= (ascii "_NAT") "_NAT"))
	(is (= (ascii "_nat") "_nat")))

(deftest UnderscoreNAT1
	(is (= (ascii "_NAT1") "_NAT1"))
	(is (= (ascii "_nat1") "_nat1")))

(deftest UnderscoreNATURAL
	(is (= (ascii "_NATURAL") "_NATURAL"))
	(is (= (ascii "_natural") "_natural")))

(deftest UnderscoreNOT
	(is (= (ascii "_NOT") "_NOT"))
	(is (= (ascii "_not") "_not")))

(deftest UnderscoreOR
	(is (= (ascii "_OR") "_OR"))
	(is (= (ascii "_or") "_or")))

(deftest UnderscorePOW
	(is (= (ascii "_POW") "_POW"))
	(is (= (ascii "_pow") "_pow")))

(deftest UnderscorePOW1
	(is (= (ascii "_POW1") "_POW1"))
	(is (= (ascii "_pow1") "_pow1")))

(deftest UnderscoreTRUE
	(is (= (ascii "_TRUE") "_TRUE"))
	(is (= (ascii "_true") "_true")))

(deftest UnderscoreUNION
	(is (= (ascii "_UNION") "_UNION"))
	(is (= (ascii "_union") "_union")))

(deftest UnderscoreLetterDigit
	(is (= (ascii "_abc123") "_abc123")))

(deftest UnderscoreLetterANY
	(is (= (ascii "_123ANY") "_123ANY"))
	(is (= (ascii "_123any") "_123any")))

(deftest UnderscoreLetterFALSE
	(is (= (ascii "_123FALSE") "_123FALSE"))
	(is (= (ascii "_123false") "_123false")))

(deftest UnderscoreLetterINTEGER
	(is (= (ascii "_123INTEGER") 
				"_123INTEGER"))
	(is (= (ascii "_123integer") 
				"_123integer")))

(deftest UnderscoreLetterINTER
	(is (= (ascii "_123INTER") "_123INTER"))
	(is (= (ascii "_123inter") "_123inter")))

(deftest UnderscoreLetterNAT
	(is (= (ascii "_123NAT") "_123NAT"))
	(is (= (ascii "_123nat") "_123nat")))

(deftest UnderscoreLetterNAT1
	(is (= (ascii "_123NAT1") "_123NAT1"))
	(is (= (ascii "_123nat1") "_123nat1")))

(deftest UnderscoreLetterNATURAL
	(is (= (ascii "_123NATURAL") 
				"_123NATURAL"))
	(is (= (ascii "_123natural") 
				"_123natural")))

(deftest UnderscoreLetterNOT
	(is (= (ascii "_123NOT") "_123NOT"))
	(is (= (ascii "_123not") "_123not")))

(deftest UnderscoreLetterOR
	(is (= (ascii "_123OR") "_123OR"))
	(is (= (ascii "_123or") "_123or")))

(deftest UnderscoreLetterPOW
	(is (= (ascii "_123POW") "_123POW"))
	(is (= (ascii "_123pow") "_123pow")))

(deftest UnderscoreLetterPOW1
	(is (= (ascii "_123POW1") "_123POW1"))
	(is (= (ascii "_123pow1") "_123pow1")))

(deftest UnderscoreLetterTRUE
	(is (= (ascii "_123TRUE") "_123TRUE"))
	(is (= (ascii "_123true") "_123true")))

(deftest UnderscoreLetterUNION
	(is (= (ascii "_123UNION") "_123UNION"))
	(is (= (ascii "_123union") "_123union")))

(deftest UnderscoreDigitLetter
	(is (= (ascii "_123abc") "_123abc")))

(deftest UnderscoreDigitANY
	(is (= (ascii "_123ANY") "_123ANY"))
	(is (= (ascii "_123any") "_123any")))

(deftest UnderscoreDigitFALSE
	(is (= (ascii "_123FALSE") "_123FALSE"))
	(is (= (ascii "_123false") "_123false")))

(deftest UnderscoreDigitINTEGER
	(is (= (ascii "_123INTEGER") 
				"_123INTEGER"))
	(is (= (ascii "_123integer") 
				"_123integer")))

(deftest UnderscoreDigitINTER
	(is (= (ascii "_123INTER") "_123INTER"))
	(is (= (ascii "_123inter") "_123inter")))

(deftest UnderscoreDigitNAT
	(is (= (ascii "_123NAT") "_123NAT"))
	(is (= (ascii "_123nat") "_123nat")))

(deftest UnderscoreDigitNAT1
	(is (= (ascii "_123NAT1") "_123NAT1"))
	(is (= (ascii "_123nat1") "_123nat1")))

(deftest UnderscoreDigitNATURAL
	(is (= (ascii "_123NATURAL") 
				"_123NATURAL"))
	(is (= (ascii "_123natural") 
				"_123natural")))

(deftest UnderscoreDigitNOT
	(is (= (ascii "_123NOT") "_123NOT"))
	(is (= (ascii "_123not") "_123not")))

(deftest UnderscoreDigitOR
	(is (= (ascii "_123OR") "_123OR"))
	(is (= (ascii "_123or") "_123or")))

(deftest UnderscoreDigitPOW
	(is (= (ascii "_123POW") "_123POW"))
	(is (= (ascii "_123pow") "_123pow")))

(deftest UnderscoreDigitPOW1
	(is (= (ascii "_123POW1") "_123POW1"))
	(is (= (ascii "_123pow1") "_123pow1")))

(deftest UnderscoreDigitTRUE
	(is (= (ascii "_123TRUE") "_123TRUE"))
	(is (= (ascii "_123true") "_123true")))

(deftest UnderscoreDigitUNION
	(is (= (ascii "_123UNION") "_123UNION"))
	(is (= (ascii "_123union") "_123union")))

(deftest UnderscoreANYLetter
	(is (= (ascii "_ANYabc") "_ANYabc"))
	(is (= (ascii "_anyabc") "_anyabc")))

(deftest UnderscoreFALSELetter
	(is (= (ascii "_FALSEabc") "_FALSEabc"))
	(is (= (ascii "_falseabc") "_falseabc")))

(deftest UnderscoreINTEGERLetter
	(is (= (ascii "_INTEGERabc") 
				"_INTEGERabc"))
	(is (= (ascii "_integerabc") 
				"_integerabc")))

(deftest UnderscoreINTERLetter
	(is (= (ascii "_INTERabc") "_INTERabc"))
	(is (= (ascii "_interabc") "_interabc")))

(deftest UnderscoreNATLetter
	(is (= (ascii "_NATabc") "_NATabc"))
	(is (= (ascii "_natabc") "_natabc")))

(deftest UnderscoreNAT1Letter
	(is (= (ascii "_NAT1abc") "_NAT1abc"))
	(is (= (ascii "_nat1abc") "_nat1abc")))

(deftest UnderscoreNATURALLetter
	(is (= (ascii "_NATURALabc") 
				"_NATURALabc"))
	(is (= (ascii "_naturalabc") 
				"_naturalabc")))

(deftest UnderscoreNOTLetter
	(is (= (ascii "_NOTabc") "_NOTabc"))
	(is (= (ascii "_notabc") "_notabc")))

(deftest UnderscoreORLetter
	(is (= (ascii "_ORabc") "_ORabc"))
	(is (= (ascii "_orabc") "_orabc")))

(deftest UnderscorePOWLetter
	(is (= (ascii "_POWabc") "_POWabc"))
	(is (= (ascii "_powabc") "_powabc")))

(deftest UnderscorePOW1Letter
	(is (= (ascii "_POW1abc") "_POW1abc"))
	(is (= (ascii "_pow1abc") "_pow1abc")))

(deftest UnderscoreTRUELetter
	(is (= (ascii "_TRUEabc") "_TRUEabc"))
	(is (= (ascii "_trueabc") "_trueabc")))

(deftest UnderscoreUNIONLetter
	(is (= (ascii "_UNIONabc") "_UNIONabc"))
	(is (= (ascii "_unionabc") "_unionabc")))

(deftest UnderscoreANYDigit
	(is (= (ascii "_ANY123") "_ANY123"))
	(is (= (ascii "_any123") "_any123")))

(deftest UnderscoreFALSEDigit
	(is (= (ascii "_FALSE123") "_FALSE123"))
	(is (= (ascii "_false123") "_false123")))

(deftest UnderscoreINTEGERDigit
	(is (= (ascii "_INTEGER123") 
				"_INTEGER123"))
	(is (= (ascii "_integer123") 
				"_integer123")))

(deftest UnderscoreINTERDigit
	(is (= (ascii "_INTER123") "_INTER123"))
	(is (= (ascii "_inter123") "_inter123")))

(deftest UnderscoreNATDigit
	(is (= (ascii "_NAT123") "_NAT123"))
	(is (= (ascii "_nat123") "_nat123")))

(deftest UnderscoreNAT1Digit
	(is (= (ascii "_NAT1123") "_NAT1123"))
	(is (= (ascii "_nat1123") "_nat1123")))

(deftest UnderscoreNATURALDigit
	(is (= (ascii "_NATURAL123") 
				"_NATURAL123"))
	(is (= (ascii "_natural123") 
				"_natural123")))

(deftest UnderscoreNOTDigit
	(is (= (ascii "_NOT123") "_NOT123"))
	(is (= (ascii "_not123") "_not123")))

(deftest UnderscoreORDigit
	(is (= (ascii "_OR123") "_OR123"))
	(is (= (ascii "_or123") "_or123")))

(deftest UnderscorePOWDigit
	(is (= (ascii "_POW123") "_POW123"))
	(is (= (ascii "_pow123") "_pow123")))

(deftest UnderscorePOW1Digit
	(is (= (ascii "_POW1123") "_POW1123"))
	(is (= (ascii "_pow1123") "_pow1123")))

(deftest UnderscoreTRUEDigit
	(is (= (ascii "_TRUE123") "_TRUE123"))
	(is (= (ascii "_true123") "_true123")))

(deftest UnderscoreUNIONDigit
	(is (= (ascii "_UNION123") "_UNION123"))
	(is (= (ascii "_union123") "_union123")))

(deftest UnderscoreLetterDigitANY
	(is (= (ascii "_abc123ANY") "_abc123ANY"))
	(is (= (ascii "_abc123any") "_abc123any")))

(deftest UnderscoreLetterDigitFALSE
	(is (= (ascii "_abc123FALSE") 
				"_abc123FALSE"))
	(is (= (ascii "_abc123false") 
				"_abc123false")))

(deftest UnderscoreLetterDigitINTEGER
	(is (= (ascii "_abc123INTEGER") 
				"_abc123INTEGER"))
	(is (= (ascii "_abc123integer") 
				"_abc123integer")))

(deftest UnderscoreLetterDigitINTER
	(is (= (ascii "_abc123INTER") 
				"_abc123INTER"))
	(is (= (ascii "_abc123inter") 
				"_abc123inter")))

(deftest UnderscoreLetterDigitNAT
	(is (= (ascii "_abc123NAT") "_abc123NAT"))
	(is (= (ascii "_abc123nat") "_abc123nat")))

(deftest UnderscoreLetterDigitNAT1
	(is (= (ascii "_abc123NAT1") 
				"_abc123NAT1"))
	(is (= (ascii "_abc123nat1") 
				"_abc123nat1")))

(deftest UnderscoreLetterDigitNATURAL
	(is (= (ascii "_abc123NATURAL") 
				"_abc123NATURAL"))
	(is (= (ascii "_abc123natural") 
				"_abc123natural")))

(deftest UnderscoreLetterDigitNOT
	(is (= (ascii "_abc123NOT") "_abc123NOT"))
	(is (= (ascii "_abc123not") "_abc123not")))

(deftest UnderscoreLetterDigitOR
	(is (= (ascii "_abc123OR") "_abc123OR"))
	(is (= (ascii "_abc123or") "_abc123or")))

(deftest UnderscoreLetterDigitPOW
	(is (= (ascii "_abc123POW") "_abc123POW"))
	(is (= (ascii "_abc123pow") "_abc123pow")))

(deftest UnderscoreLetterDigitPOW1
	(is (= (ascii "_abc123POW1") 
				"_abc123POW1"))
	(is (= (ascii "_abc123pow1") 
				"_abc123pow1")))

(deftest UnderscoreLetterDigitTRUE
	(is (= (ascii "_abc123TRUE") 
				"_abc123TRUE"))
	(is (= (ascii "_abc123true") 
				"_abc123true")))

(deftest UnderscoreLetterDigitUNION
	(is (= (ascii "_abc123UNION") 
				"_abc123UNION"))
	(is (= (ascii "_abc123union") 
				"_abc123union")))

(deftest UnderscoreLetterANYDigit
	(is (= (ascii "_abcANY123") "_abcANY123"))
	(is (= (ascii "_abcany123") "_abcany123")))

(deftest UnderscoreLetterFALSEDigit
	(is (= (ascii "_abcFALSE123") 
				"_abcFALSE123"))
	(is (= (ascii "_abcfalse123") 
				"_abcfalse123")))

(deftest UnderscoreLetterINTEGERDigit
	(is (= (ascii "_abcINTEGER123") 
				"_abcINTEGER123"))
	(is (= (ascii "_abcinteger123") 
				"_abcinteger123")))

(deftest UnderscoreLetterINTERDigit
	(is (= (ascii "_abcINTER123") 
				"_abcINTER123"))
	(is (= (ascii "_abcinter123") 
				"_abcinter123")))

(deftest UnderscoreLetterNATDigit
	(is (= (ascii "_abcNAT123") "_abcNAT123"))
	(is (= (ascii "_abcnat123") "_abcnat123")))

(deftest UnderscoreLetterNAT1Digit
	(is (= (ascii "_abcNAT1123") 
				"_abcNAT1123"))
	(is (= (ascii "_abcnat1123") 
				"_abcnat1123")))

(deftest UnderscoreLetterNATURALDigit
	(is (= (ascii "_abcNATURAL123") 
				"_abcNATURAL123"))
	(is (= (ascii "_abcnatural123") 
				"_abcnatural123")))

(deftest UnderscoreLetterNOTDigit
	(is (= (ascii "_abcNOT123") "_abcNOT123"))
	(is (= (ascii "_abcnot123") "_abcnot123")))

(deftest UnderscoreLetterORDigit
	(is (= (ascii "_abcOR123") "_abcOR123"))
	(is (= (ascii "_abcor123") "_abcor123")))

(deftest UnderscoreLetterPOWDigit
	(is (= (ascii "_abcPOW123") "_abcPOW123"))
	(is (= (ascii "_abcpow123") "_abcpow123")))

(deftest UnderscoreLetterPOW1Digit
	(is (= (ascii "_abcPOW1123") 
				"_abcPOW1123"))
	(is (= (ascii "_abcpow1123") 
				"_abcpow1123")))

(deftest UnderscoreLetterTRUEDigit
	(is (= (ascii "_abcTRUE123") 
				"_abcTRUE123"))
	(is (= (ascii "_abctrue123") 
				"_abctrue123")))

(deftest UnderscoreLetterUNIONDigit
	(is (= (ascii "_abcUNION123") 
				"_abcUNION123"))
	(is (= (ascii "_abcunion123") 
				"_abcunion123")))

(deftest UnderscoreDigitLetterANY
	(is (= (ascii "_123abcANY") "_123abcANY"))
	(is (= (ascii "_123abcany") "_123abcany")))

(deftest UnderscoreDigitLetterFALSE
	(is (= (ascii "_123abcFALSE") 
				"_123abcFALSE"))
	(is (= (ascii "_123abcfalse") 
				"_123abcfalse")))

(deftest UnderscoreDigitLetterINTEGER
	(is (= (ascii "_123abcINTEGER") 
				"_123abcINTEGER"))
	(is (= (ascii "_123abcinteger") 
				"_123abcinteger")))

(deftest UnderscoreDigitLetterINTER
	(is (= (ascii "_123abcINTER") 
				"_123abcINTER"))
	(is (= (ascii "_123abcinter") 
				"_123abcinter")))

(deftest UnderscoreDigitLetterNAT
	(is (= (ascii "_123abcNAT") "_123abcNAT"))
	(is (= (ascii "_123abcnat") "_123abcnat")))

(deftest UnderscoreDigitLetterNAT1
	(is (= (ascii "_123abcNAT1") 
				"_123abcNAT1"))
	(is (= (ascii "_123abcnat1") 
				"_123abcnat1")))

(deftest UnderscoreDigitLetterNATURAL
	(is (= (ascii "_123abcNATURAL") 
				"_123abcNATURAL"))
	(is (= (ascii "_123abcnatural") 
				"_123abcnatural")))

(deftest UnderscoreDigitLetterNOT
	(is (= (ascii "_123abcNOT") "_123abcNOT"))
	(is (= (ascii "_123abcnot") "_123abcnot")))

(deftest UnderscoreDigitLetterOR
	(is (= (ascii "_123abcOR") "_123abcOR"))
	(is (= (ascii "_123abcor") "_123abcor")))

(deftest UnderscoreDigitLetterPOW
	(is (= (ascii "_123abcPOW") "_123abcPOW"))
	(is (= (ascii "_123abcpow") "_123abcpow")))

(deftest UnderscoreDigitLetterPOW1
	(is (= (ascii "_123abcPOW1") 
				"_123abcPOW1"))
	(is (= (ascii "_123abcpow1") 
				"_123abcpow1")))

(deftest UnderscoreDigitLetterTRUE
	(is (= (ascii "_123abcTRUE") 
				"_123abcTRUE"))
	(is (= (ascii "_123abctrue") 
				"_123abctrue")))

(deftest UnderscoreDigitLetterUNION
	(is (= (ascii "_123abcUNION") 
				"_123abcUNION"))
	(is (= (ascii "_123abcunion") 
				"_123abcunion")))

(deftest UnderscoreDigitANYLetter
	(is (= (ascii "_123ANYabc") "_123ANYabc"))
	(is (= (ascii "_123anyabc") "_123anyabc")))

(deftest UnderscoreDigitFALSELetter
	(is (= (ascii "_123FALSEabc") 
				"_123FALSEabc"))
	(is (= (ascii "_123falseabc") 
				"_123falseabc")))

(deftest UnderscoreDigitINTEGERLetter
	(is (= (ascii "_123INTEGERabc") 
				"_123INTEGERabc"))
	(is (= (ascii "_123integerabc") 
				"_123integerabc")))

(deftest UnderscoreDigitINTERLetter
	(is (= (ascii "_123INTERabc") 
				"_123INTERabc"))
	(is (= (ascii "_123interabc") 
				"_123interabc")))

(deftest UnderscoreDigitNATLetter
	(is (= (ascii "_123NATabc") "_123NATabc"))
	(is (= (ascii "_123natabc") "_123natabc")))

(deftest UnderscoreDigitNAT1Letter
	(is (= (ascii "_123NAT1abc") 
				"_123NAT1abc"))
	(is (= (ascii "_123nat1abc") 
				"_123nat1abc")))

(deftest UnderscoreDigitNATURALLetter
	(is (= (ascii "_123NATURALabc") 
				"_123NATURALabc"))
	(is (= (ascii "_123naturalabc") 
				"_123naturalabc")))

(deftest UnderscoreDigitNOTLetter
	(is (= (ascii "_123NOTabc") "_123NOTabc"))
	(is (= (ascii "_123notabc") "_123notabc")))

(deftest UnderscoreDigitORLetter
	(is (= (ascii "_123orabc") "_123orabc"))
	(is (= (ascii "_123ORabc") "_123ORabc")))

(deftest UnderscoreDigitPOWLetter
	(is (= (ascii "_123POWabc") "_123POWabc"))
	(is (= (ascii "_123powabc") "_123powabc")))

(deftest UnderscoreDigitPOW1Letter
	(is (= (ascii "_123POW1abc") 
				"_123POW1abc"))
	(is (= (ascii "_123pow1abc") 
				"_123pow1abc")))

(deftest UnderscoreDigitTRUELetter
	(is (= (ascii "_123TRUEabc") 
				"_123TRUEabc"))
	(is (= (ascii "_123trueabc") 
				"_123trueabc")))

(deftest UnderscoreDigitUNIONLetter
	(is (= (ascii "_123UNIONabc") 
				"_123UNIONabc"))
	(is (= (ascii "_123unionabc") 
				"_123unionabc")))

(deftest UnderscoreANYLetterDigit
	(is (= (ascii "_ANYabc123") "_ANYabc123"))
	(is (= (ascii "_anyabc123") "_anyabc123")))

(deftest UnderscoreFALSELetterDigit
	(is (= (ascii "_FALSEabc123") 
				"_FALSEabc123"))
	(is (= (ascii "_falseabc123") 
				"_falseabc123")))

(deftest UnderscoreINTEGERLetterDigit
	(is (= (ascii "_INTEGERabc123") 
				"_INTEGERabc123"))
	(is (= (ascii "_integerabc123") 
				"_integerabc123")))

(deftest UnderscoreINTERLetterDigit
	(is (= (ascii "_INTERabc123") 
				"_INTERabc123"))
	(is (= (ascii "_interabc123") 
				"_interabc123")))

(deftest UnderscoreNATLetterDigit
	(is (= (ascii "_NATabc123") "_NATabc123"))
	(is (= (ascii "_natabc123") "_natabc123")))

(deftest UnderscoreNAT1LetterDigit
	(is (= (ascii "_NAT1abc123") 
				"_NAT1abc123"))
	(is (= (ascii "_nat1abc123") 
				"_nat1abc123")))

(deftest UnderscoreNATURALLetterDigit
	(is (= (ascii "_NATURALabc123") 
				"_NATURALabc123"))
	(is (= (ascii "_naturalabc123") 
				"_naturalabc123")))

(deftest UnderscoreNOTLetterDigit
	(is (= (ascii "_NOTabc123") "_NOTabc123"))
	(is (= (ascii "_notabc123") "_notabc123")))

(deftest UnderscoreORLetterDigit
	(is (= (ascii "_ORabc123") "_ORabc123"))
	(is (= (ascii "_orabc123") "_orabc123")))

(deftest UnderscorePOWLetterDigit
	(is (= (ascii "_POWabc123") "_POWabc123"))
	(is (= (ascii "_powabc123") "_powabc123")))

(deftest UnderscorePOW1LetterDigit
	(is (= (ascii "_POW1abc123") 
				"_POW1abc123"))
	(is (= (ascii "_pow1abc123") 
				"_pow1abc123")))

(deftest UnderscoreTRUELetterDigit
	(is (= (ascii "_TRUEabc123") 
				"_TRUEabc123"))
	(is (= (ascii "_trueabc123") 
				"_trueabc123")))

(deftest UnderscoreUNIONLetterDigit
	(is (= (ascii "_UNIONabc123") 
				"_UNIONabc123"))
	(is (= (ascii "_unionabc123") 
				"_unionabc123")))

(deftest UnderscoreANYDigitLetter
	(is (= (ascii "_ANY123abc") "_ANY123abc"))
	(is (= (ascii "_any123abc") "_any123abc")))

(deftest UnderscoreFALSEDigitLetter
	(is (= (ascii "_FALSE123abc") 
				"_FALSE123abc"))
	(is (= (ascii "_false123abc") 
				"_false123abc")))

(deftest UnderscoreINTEGERDigitLetter
	(is (= (ascii "_INTEGER123abc") 
				"_INTEGER123abc"))
	(is (= (ascii "_integer123abc") 
				"_integer123abc")))

(deftest UnderscoreINTERDigitLetter
	(is (= (ascii "_INTER123abc") 
				"_INTER123abc"))
	(is (= (ascii "_inter123abc") 
				"_inter123abc")))

(deftest UnderscoreNATDigitLetter
	(is (= (ascii "_NAT123abc") "_NAT123abc"))
	(is (= (ascii "_nat123abc") "_nat123abc")))

(deftest UnderscoreNAT1DigitLetter
	(is (= (ascii "_NAT1123abc") 
				"_NAT1123abc"))
	(is (= (ascii "_nat1123abc") 
				"_nat1123abc")))

(deftest UnderscoreNATURALDigitLetter
	(is (= (ascii "_NATURAL123abc") 
				"_NATURAL123abc"))
	(is (= (ascii "_natural123abc") 
				"_natural123abc")))

(deftest UnderscoreNOTDigitLetter
	(is (= (ascii "_NOT123abc") "_NOT123abc"))
	(is (= (ascii "_not123abc") "_not123abc")))

(deftest UnderscoreORDigitLetter
	(is (= (ascii "_OR123abc") "_OR123abc"))
	(is (= (ascii "_or123abc") "_or123abc")))

(deftest UnderscorePOWDigitLetter
	(is (= (ascii "_POW123abc") "_POW123abc"))
	(is (= (ascii "_pow123abc") "_pow123abc")))

(deftest UnderscorePOW1DigitLetter
	(is (= (ascii "_POW1123abc") 
				"_POW1123abc"))
	(is (= (ascii "_pow1123abc") 
				"_pow1123abc")))

(deftest UnderscoreTRUEDigitLetter
	(is (= (ascii "_TRUE123abc") 
				"_TRUE123abc"))
	(is (= (ascii "_true123abc") 
				"_true123abc")))

(deftest UnderscoreUNIONDigitLetter
	(is (= (ascii "_UNION123abc") 
				"_UNION123abc"))
	(is (= (ascii "_union123abc") 
				"_union123abc")))

(deftest ANYLetter
	(is (= (ascii "ANYabc") "ANYabc"))
	(is (= (ascii "anyabc") "anyabc")))

(deftest FALSELetter
	(is (= (ascii "FALSEabc") "FALSEabc"))
	(is (= (ascii "falseabc") "falseabc")))

(deftest INTEGERLetter
	(is (= (ascii "INTEGERabc") "INTEGERabc"))
	(is (= (ascii "integerabc") "integerabc")))

(deftest INTERLetter
	(is (= (ascii "INTERabc") "INTERabc"))
	(is (= (ascii "interabc") "interabc")))

(deftest NATLetter
	(is (= (ascii "NATabc") "NATabc"))
	(is (= (ascii "natabc") "natabc")))

(deftest NAT1Letter
	(is (= (ascii "NAT1abc") "NAT1abc"))
	(is (= (ascii "nat1abc") "nat1abc")))

(deftest NATURALLetter
	(is (= (ascii "NATURALabc") "NATURALabc"))
	(is (= (ascii "naturalabc") "naturalabc")))

(deftest NOTLetter
	(is (= (ascii "NOTabc") "NOTabc"))
	(is (= (ascii "notabc") "notabc")))

(deftest ORLetter
	(is (= (ascii "ORabc") "ORabc"))
	(is (= (ascii "orabc") "orabc")))

(deftest POWLetter
	(is (= (ascii "POWabc") "POWabc"))
	(is (= (ascii "powabc") "powabc")))

(deftest POW1Letter
	(is (= (ascii "POW1abc") "POW1abc"))
	(is (= (ascii "pow1abc") "pow1abc")))

(deftest TRUELetter
	(is (= (ascii "TRUEabc") "TRUEabc"))
	(is (= (ascii "trueabc") "trueabc")))

(deftest UNIONLetter
	(is (= (ascii "UNIONabc") "UNIONabc"))
	(is (= (ascii "unionabc") "unionabc")))

(deftest ANYDigit
	(is (= (ascii "ANY123") "ANY123"))
	(is (= (ascii "any123") "any123")))

(deftest FALSEDigit
	(is (= (ascii "FALSE123") "FALSE123"))
	(is (= (ascii "false123") "false123")))

(deftest INTEGERDigit
	(is (= (ascii "INTEGER123") "INTEGER123"))
	(is (= (ascii "integer123") "integer123")))

(deftest INTERDigit
	(is (= (ascii "INTER123") "INTER123"))
	(is (= (ascii "inter123") "inter123")))

(deftest NATDigit
	(is (= (ascii "NAT123") "NAT123"))
	(is (= (ascii "nat123") "nat123")))

(deftest NAT1Digit
	(is (= (ascii "NAT1123") "NAT1123"))
	(is (= (ascii "nat1123") "nat1123")))

(deftest NATURALDigit
	(is (= (ascii "NATURAL123") "NATURAL123"))
	(is (= (ascii "natural123") "natural123")))

(deftest NOTDigit
	(is (= (ascii "not123") "not123"))
	(is (= (ascii "NOT123") "NOT123")))

(deftest ORDigit
	(is (= (ascii "or123") "or123"))
	(is (= (ascii "OR123") "OR123")))

(deftest POWDigit
	(is (= (ascii "POW123") "POW123"))
	(is (= (ascii "pow123") "pow123")))

(deftest POW1Digit
	(is (= (ascii "POW1123") "POW1123"))
	(is (= (ascii "pow1123") "pow1123")))

(deftest TRUEDigit
	(is (= (ascii "TRUE123") "TRUE123"))
	(is (= (ascii "true123") "true123")))

(deftest UNIONDigit
	(is (= (ascii "UNION123") "UNION123"))
	(is (= (ascii "union123") "union123")))

(deftest ANYUnderscore
	(is (= (ascii "ANY_") "ANY_"))
	(is (= (ascii "any_") "any_")))

(deftest FALSEUnderscore
	(is (= (ascii "FALSE_") "FALSE_"))
	(is (= (ascii "false_") "false_")))

(deftest INTEGERUnderscore
	(is (= (ascii "INTEGER_") "INTEGER_"))
	(is (= (ascii "integer_") "integer_")))

(deftest INTERUnderscore
	(is (= (ascii "INTER_") "INTER_"))
	(is (= (ascii "inter_") "inter_")))

(deftest NATUnderscore
	(is (= (ascii "NAT_") "NAT_"))
	(is (= (ascii "nat_") "nat_")))

(deftest NAT1Underscore
	(is (= (ascii "NAT1_") "NAT1_"))
	(is (= (ascii "nat1_") "nat1_")))

(deftest NATURALUnderscore
	(is (= (ascii "NATURAL_") "NATURAL_"))
	(is (= (ascii "natural_") "natural_")))

(deftest NOTUnderscore
	(is (= (ascii "NOT_") "NOT_"))
	(is (= (ascii "not_") "not_")))

(deftest ORUnderscore
	(is (= (ascii "OR_") "OR_"))
	(is (= (ascii "or_") "or_")))

(deftest POWUnderscore
	(is (= (ascii "POW_") "POW_"))
	(is (= (ascii "pow_") "pow_")))

(deftest POW1Underscore
	(is (= (ascii "POW1_") "POW1_"))
	(is (= (ascii "pow1_") "pow1_")))

(deftest TRUEUnderscore
	(is (= (ascii "TRUE_") "TRUE_"))
	(is (= (ascii "true_") "true_")))

(deftest UNIONUnderscore
	(is (= (ascii "UNION_") "UNION_"))
	(is (= (ascii "union_") "union_")))

(deftest ANYLetterDigit
	(is (= (ascii "ANYabc123") "ANYabc123"))
	(is (= (ascii "anyabc123") "anyabc123")))

(deftest FALSELetterDigit
	(is (= (ascii "FALSEabc123") 
				"FALSEabc123"))
	(is (= (ascii "falseabc123") 
				"falseabc123")))

(deftest INTEGERLetterDigit
	(is (= (ascii "INTEGERabc123") 
				"INTEGERabc123"))
	(is (= (ascii "integerabc123") 
				"integerabc123")))

(deftest INTERLetterDigit
	(is (= (ascii "INTERabc123") 
				"INTERabc123"))
	(is (= (ascii "interabc123") 
				"interabc123")))

(deftest NATLetterDigit
	(is (= (ascii "NATabc123") "NATabc123"))
	(is (= (ascii "natabc123") "natabc123")))

(deftest NAT1LetterDigit
	(is (= (ascii "NAT1abc123") "NAT1abc123"))
	(is (= (ascii "nat1abc123") "nat1abc123")))

(deftest NATURALLetterDigit
	(is (= (ascii "NATURALabc123") 
				"NATURALabc123"))
	(is (= (ascii "naturalabc123") 
				"naturalabc123")))

(deftest NOTLetterDigit
	(is (= (ascii "NOTabc123") "NOTabc123"))
	(is (= (ascii "notabc123") "notabc123")))

(deftest ORLetterDigit
	(is (= (ascii "ORabc123") "ORabc123"))
	(is (= (ascii "orabc123") "orabc123")))

(deftest POWLetterDigit
	(is (= (ascii "POWabc123") "POWabc123"))
	(is (= (ascii "powabc123") "powabc123")))

(deftest POW1LetterDigit
	(is (= (ascii "POW1abc123") "POW1abc123"))
	(is (= (ascii "pow1abc123") "pow1abc123")))

(deftest TRUELetterDigit
	(is (= (ascii "TRUEabc123") "TRUEabc123"))
	(is (= (ascii "trueabc123") "trueabc123")))

(deftest UNIONLetterDigit
	(is (= (ascii "UNIONabc123") 
				"UNIONabc123"))
	(is (= (ascii "unionabc123") 
				"unionabc123")))

(deftest ANYLetterUnderscore
	(is (= (ascii "ANYabc_") "ANYabc_"))
	(is (= (ascii "anyabc_") "anyabc_")))

(deftest FALSELetterUnderscore
	(is (= (ascii "FALSEabc_") "FALSEabc_"))
	(is (= (ascii "falseabc_") "falseabc_")))

(deftest INTEGERLetterUnderscore
	(is (= (ascii "INTEGERabc_") 
				"INTEGERabc_"))
	(is (= (ascii "integerabc_") 
				"integerabc_")))

(deftest INTERLetterUnderscore
	(is (= (ascii "INTERabc_") "INTERabc_"))
	(is (= (ascii "interabc_") "interabc_")))

(deftest NATLetterUnderscore
	(is (= (ascii "NATabc_") "NATabc_"))
	(is (= (ascii "natabc_") "natabc_")))

(deftest NAT1LetterUnderscore
	(is (= (ascii "NAT1abc_") "NAT1abc_"))
	(is (= (ascii "nat1abc_") "nat1abc_")))

(deftest NATURALLetterUnderscore
	(is (= (ascii "NATURALabc_") 
				"NATURALabc_"))
	(is (= (ascii "naturalabc_") 
				"naturalabc_")))

(deftest NOTLetterUnderscore
	(is (= (ascii "NOTabc_") "NOTabc_"))
	(is (= (ascii "notabc_") "notabc_")))

(deftest ORLetterUnderscore
	(is (= (ascii "ORabc_") "ORabc_"))
	(is (= (ascii "orabc_") "orabc_")))

(deftest POWLetterUnderscore
	(is (= (ascii "POWabc_") "POWabc_"))
	(is (= (ascii "powabc_") "powabc_")))

(deftest POW1LetterUnderscore
	(is (= (ascii "POW1abc_") "POW1abc_"))
	(is (= (ascii "pow1abc_") "pow1abc_")))

(deftest TRUELetterUnderscore
	(is (= (ascii "TRUEabc_") "TRUEabc_"))
	(is (= (ascii "trueabc_") "trueabc_")))

(deftest UNIONLetterUnderscore
	(is (= (ascii "UNIONabc_") "UNIONabc_"))
	(is (= (ascii "unionabc_") "unionabc_")))

(deftest ANYDigitLetter
	(is (= (ascii "ANY123abc") "ANY123abc"))
	(is (= (ascii "any123abc") "any123abc")))

(deftest FALSEDigitLetter
	(is (= (ascii "FALSE123abc") 
				"FALSE123abc"))
	(is (= (ascii "false123abc") 
				"false123abc")))

(deftest INTEGERDigitLetter
	(is (= (ascii "INTEGER123abc") 
				"INTEGER123abc"))
	(is (= (ascii "integer123abc") 
				"integer123abc")))

(deftest INTERDigitLetter
	(is (= (ascii "INTER123abc") 
				"INTER123abc"))
	(is (= (ascii "inter123abc") 
				"inter123abc")))

(deftest NATDigitLetter
	(is (= (ascii "NAT123abc") "NAT123abc"))
	(is (= (ascii "nat123abc") "nat123abc")))

(deftest NAT1DigitLetter
	(is (= (ascii "NAT1123abc") "NAT1123abc"))
	(is (= (ascii "nat1123abc") "nat1123abc")))

(deftest NATURALDigitLetter
	(is (= (ascii "NATURAL123abc") 
				"NATURAL123abc"))
	(is (= (ascii "natural123abc") 
				"natural123abc")))

(deftest NOTDigitLetter
	(is (= (ascii "NOT123abc") "NOT123abc"))
	(is (= (ascii "not123abc") "not123abc")))

(deftest ORDigitLetter
	(is (= (ascii "OR123abc") "OR123abc"))
	(is (= (ascii "or123abc") "or123abc")))

(deftest POWDigitLetter
	(is (= (ascii "POW123abc") "POW123abc"))
	(is (= (ascii "pow123abc") "pow123abc")))

(deftest POW1DigitLetter
	(is (= (ascii "POW1123abc") "POW1123abc"))
	(is (= (ascii "pow1123abc") "pow1123abc")))

(deftest TRUEDigitLetter
	(is (= (ascii "TRUE123abc") "TRUE123abc"))
	(is (= (ascii "true123abc") "true123abc")))

(deftest UNIONDigitLetter
	(is (= (ascii "UNION123abc") 
				"UNION123abc"))
	(is (= (ascii "union123abc") 
				"union123abc")))

(deftest ANYDigitUnderscore
	(is (= (ascii "ANY123_") "ANY123_"))
	(is (= (ascii "any123_") "any123_")))

(deftest FALSEDigitUnderscore
	(is (= (ascii "FALSE123_") "FALSE123_"))
	(is (= (ascii "false123_") "false123_")))

(deftest INTEGERDigitUnderscore
	(is (= (ascii "INTEGER123_") 
				"INTEGER123_"))
	(is (= (ascii "integer123_") 
				"integer123_")))

(deftest INTERDigitUnderscore
	(is (= (ascii "INTER123_") "INTER123_"))
	(is (= (ascii "inter123_") "inter123_")))

(deftest NATDigitUnderscore
	(is (= (ascii "NAT123_") "NAT123_"))
	(is (= (ascii "nat123_") "nat123_")))

(deftest NAT1DigitUnderscore
	(is (= (ascii "NAT1123_") "NAT1123_"))
	(is (= (ascii "nat1123_") "nat1123_")))

(deftest NATURALDigitUnderscore
	(is (= (ascii "NATURAL123_") 
				"NATURAL123_"))
	(is (= (ascii "natural123_") 
				"natural123_")))

(deftest NOTDigitUnderscore
	(is (= (ascii "NOT123_") "NOT123_"))
	(is (= (ascii "not123_") "not123_")))

(deftest ORDigitUnderscore
	(is (= (ascii "OR123_") "OR123_"))
	(is (= (ascii "or123_") "or123_")))

(deftest POWDigitUnderscore
	(is (= (ascii "POW123_") "POW123_"))
	(is (= (ascii "pow123_") "pow123_")))

(deftest POW1DigitUnderscore
	(is (= (ascii "POW1123_") "POW1123_"))
	(is (= (ascii "pow1123_") "pow1123_")))

(deftest TRUEDigitUnderscore
	(is (= (ascii "TRUE123_") "TRUE123_"))
	(is (= (ascii "true123_") "true123_")))

(deftest UNIONDigitUnderscore
	(is (= (ascii "UNION123_") "UNION123_"))
	(is (= (ascii "union123_") "union123_")))

(deftest ANYUnderscoreLetter
	(is (= (ascii "ANY_abc") "ANY_abc"))
	(is (= (ascii "any_abc") "any_abc")))

(deftest FALSEUnderscoreLetter
	(is (= (ascii "FALSE_abc") "FALSE_abc"))
	(is (= (ascii "false_abc") "false_abc")))

(deftest INTEGERUnderscoreLetter
	(is (= (ascii "INTEGER_abc") 
				"INTEGER_abc"))
	(is (= (ascii "integer_abc") 
				"integer_abc")))

(deftest INTERUnderscoreLetter
	(is (= (ascii "INTER_abc") "INTER_abc"))
	(is (= (ascii "inter_abc") "inter_abc")))

(deftest NATUnderscoreLetter
	(is (= (ascii "NAT_abc") "NAT_abc"))
	(is (= (ascii "nat_abc") "nat_abc")))

(deftest NAT1UnderscoreLetter
	(is (= (ascii "NAT1_abc") "NAT1_abc"))
	(is (= (ascii "nat1_abc") "nat1_abc")))

(deftest NATURALUnderscoreLetter
	(is (= (ascii "NATURAL_abc") 
				"NATURAL_abc"))
	(is (= (ascii "natural_abc") 
				"natural_abc")))

(deftest NOTUnderscoreLetter
	(is (= (ascii "NOT_abc") "NOT_abc"))
	(is (= (ascii "not_abc") "not_abc")))

(deftest ORUnderscoreLetter
	(is (= (ascii "OR_abc") "OR_abc"))
	(is (= (ascii "or_abc") "or_abc")))

(deftest POWUnderscoreLetter
	(is (= (ascii "POW_abc") "POW_abc"))
	(is (= (ascii "pow_abc") "pow_abc")))

(deftest POW1UnderscoreLetter
	(is (= (ascii "POW1_abc") "POW1_abc"))
	(is (= (ascii "pow1_abc") "pow1_abc")))

(deftest TRUEUnderscoreLetter
	(is (= (ascii "TRUE_abc") "TRUE_abc"))
	(is (= (ascii "true_abc") "true_abc")))

(deftest UNIONUnderscoreLetter
	(is (= (ascii "UNION_abc") "UNION_abc"))
	(is (= (ascii "union_abc") "union_abc")))

(deftest ANYUnderscoreDigit
	(is (= (ascii "ANY_123") "ANY_123"))
	(is (= (ascii "any_123") "any_123")))

(deftest FALSEUnderscoreDigit
	(is (= (ascii "FALSE_123") "FALSE_123"))
	(is (= (ascii "false_123") "false_123")))

(deftest INTEGERUnderscoreDigit
	(is (= (ascii "INTEGER_123") 
				"INTEGER_123"))
	(is (= (ascii "integer_123") 
				"integer_123")))

(deftest INTERUnderscoreDigit
	(is (= (ascii "INTER_123") "INTER_123"))
	(is (= (ascii "inter_123") "inter_123")))

(deftest NATUnderscoreDigit
	(is (= (ascii "NAT_123") "NAT_123"))
	(is (= (ascii "nat_123") "nat_123")))

(deftest NAT1UnderscoreDigit
	(is (= (ascii "NAT1_123") "NAT1_123"))
	(is (= (ascii "nat1_123") "nat1_123")))

(deftest NATURALUnderscoreDigit
	(is (= (ascii "NATURAL_123") 
				"NATURAL_123"))
	(is (= (ascii "natural_123") 
				"natural_123")))

(deftest NOTUnderscoreDigit
	(is (= (ascii "not_123") "not_123"))
	(is (= (ascii "NOT_123") "NOT_123")))

(deftest ORUnderscoreDigit
	(is (= (ascii "or_123") "or_123"))
	(is (= (ascii "OR_123") "OR_123")))

(deftest POWUnderscoreDigit
	(is (= (ascii "POW_123") "POW_123"))
	(is (= (ascii "pow_123") "pow_123")))

(deftest POW1UnderscoreDigit
	(is (= (ascii "POW1_123") "POW1_123"))
	(is (= (ascii "pow1_123") "pow1_123")))

(deftest TRUEUnderscoreDigit
	(is (= (ascii "TRUE_123") "TRUE_123"))
	(is (= (ascii "true_123") "true_123")))

(deftest UNIONUnderscoreDigit
	(is (= (ascii "UNION_123") "UNION_123"))
	(is (= (ascii "union_123") "union_123")))

(deftest ANYLetterDigitUnderscore
	(is (= (ascii "ANYabc123_") "ANYabc123_"))
	(is (= (ascii "anyabc123_") "anyabc123_")))

(deftest FALSELetterDigitUnderscore
	(is (= (ascii "FALSEabc123_") 
				"FALSEabc123_"))
	(is (= (ascii "falseabc123_") 
				"falseabc123_")))

(deftest INTEGERLetterDigitUnderscore
	(is (= (ascii "INTEGERabc123_") 
				"INTEGERabc123_"))
	(is (= (ascii "integerabc123_") 
				"integerabc123_")))

(deftest INTERLetterDigitUnderscore
	(is (= (ascii "INTERabc123_") 
				"INTERabc123_"))
	(is (= (ascii "interabc123_") 
				"interabc123_")))

(deftest NATLetterDigitUnderscore
	(is (= (ascii "NATabc123_") "NATabc123_"))
	(is (= (ascii "natabc123_") "natabc123_")))

(deftest NAT1LetterDigitUnderscore
	(is (= (ascii "NAT1abc123_") 
				"NAT1abc123_"))
	(is (= (ascii "nat1abc123_") 
				"nat1abc123_")))

(deftest NATURALLetterDigitUnderscore
	(is (= (ascii "NATURALabc123_") 
				"NATURALabc123_"))
	(is (= (ascii "naturalabc123_") 
				"naturalabc123_")))

(deftest NOTLetterDigitUnderscore
	(is (= (ascii "NOTabc123_") "NOTabc123_"))
	(is (= (ascii "notabc123_") "notabc123_")))

(deftest ORLetterDigitUnderscore
	(is (= (ascii "ORabc123_") "ORabc123_"))
	(is (= (ascii "orabc123_") "orabc123_")))

(deftest POWLetterDigitUnderscore
	(is (= (ascii "POWabc123_") "POWabc123_"))
	(is (= (ascii "powabc123_") "powabc123_")))

(deftest POW1LetterDigitUnderscore
	(is (= (ascii "POW1abc123_") 
				"POW1abc123_"))
	(is (= (ascii "pow1abc123_") 
				"pow1abc123_")))

(deftest TRUELetterDigitUnderscore
	(is (= (ascii "TRUEabc123_") 
				"TRUEabc123_"))
	(is (= (ascii "trueabc123_") 
				"trueabc123_")))

(deftest UNIONLetterDigitUnderscore
	(is (= (ascii "UNIONabc123_") 
				"UNIONabc123_"))
	(is (= (ascii "unionabc123_") 
				"unionabc123_")))

(deftest ANYLetterUnderscoreDigit
	(is (= (ascii "ANYabc_123") "ANYabc_123"))
	(is (= (ascii "anyabc_123") "anyabc_123")))

(deftest FALSELetterUnderscoreDigit
	(is (= (ascii "FALSEabc_123") 
				"FALSEabc_123"))
	(is (= (ascii "falseabc_123") 
				"falseabc_123")))

(deftest INTEGERLetterUnderscoreDigit
	(is (= (ascii "INTEGERabc_123") 
				"INTEGERabc_123"))
	(is (= (ascii "integerabc_123") 
				"integerabc_123")))

(deftest INTERLetterUnderscoreDigit
	(is (= (ascii "INTERabc_123") 
				"INTERabc_123"))
	(is (= (ascii "interabc_123") 
				"interabc_123")))

(deftest NATLetterUnderscoreDigit
	(is (= (ascii "NATabc_123") "NATabc_123"))
	(is (= (ascii "natabc_123") "natabc_123")))

(deftest NAT1LetterUnderscoreDigit
	(is (= (ascii "NAT1abc_123") 
				"NAT1abc_123"))
	(is (= (ascii "nat1abc_123") 
				"nat1abc_123")))

(deftest NATURALLetterUnderscoreDigit
	(is (= (ascii "NATURALabc_123") 
				"NATURALabc_123"))
	(is (= (ascii "naturalabc_123") 
				"naturalabc_123")))

(deftest NOTLetterUnderscoreDigit
	(is (= (ascii "NOTabc_123") "NOTabc_123"))
	(is (= (ascii "notabc_123") "notabc_123")))

(deftest ORLetterUnderscoreDigit
	(is (= (ascii "ORabc_123") "ORabc_123"))
	(is (= (ascii "orabc_123") "orabc_123")))

(deftest POWLetterUnderscoreDigit
	(is (= (ascii "POWabc_123") "POWabc_123"))
	(is (= (ascii "powabc_123") "powabc_123")))

(deftest POW1LetterUnderscoreDigit
	(is (= (ascii "POW1abc_123") 
				"POW1abc_123"))
	(is (= (ascii "pow1abc_123") 
				"pow1abc_123")))

(deftest TRUELetterUnderscoreDigit
	(is (= (ascii "TRUEabc_123") 
				"TRUEabc_123"))
	(is (= (ascii "trueabc_123") 
				"trueabc_123")))

(deftest UNIONLetterUnderscoreDigit
	(is (= (ascii "UNIONabc_123") 
				"UNIONabc_123"))
	(is (= (ascii "unionabc_123") 
				"unionabc_123")))

(deftest ANYDigitLetterUnderscore
	(is (= (ascii "ANY123abc_") "ANY123abc_"))
	(is (= (ascii "any123abc_") "any123abc_")))

(deftest FALSEDigitLetterUnderscore
	(is (= (ascii "FALSE123abc_") 
				"FALSE123abc_"))
	(is (= (ascii "false123abc_") 
				"false123abc_")))

(deftest INTEGERDigitLetterUnderscore
	(is (= (ascii "INTEGER123abc_") 
				"INTEGER123abc_"))
	(is (= (ascii "integer123abc_") 
				"integer123abc_")))

(deftest INTERDigitLetterUnderscore
	(is (= (ascii "INTER123abc_") 
				"INTER123abc_"))
	(is (= (ascii "inter123abc_") 
				"inter123abc_")))

(deftest NATDigitLetterUnderscore
	(is (= (ascii "NAT123abc_") "NAT123abc_"))
	(is (= (ascii "nat123abc_") "nat123abc_")))

(deftest NAT1DigitLetterUnderscore
	(is (= (ascii "NAT1123abc_") 
				"NAT1123abc_"))
	(is (= (ascii "nat1123abc_") 
				"nat1123abc_")))

(deftest NATURALDigitLetterUnderscore
	(is (= (ascii "NATURAL123abc_") 
				"NATURAL123abc_"))
	(is (= (ascii "natural123abc_") 
				"natural123abc_")))

(deftest NOTDigitLetterUnderscore
	(is (= (ascii "NOT123abc_") "NOT123abc_"))
	(is (= (ascii "not123abc_") "not123abc_")))

(deftest ORDigitLetterUnderscore
	(is (= (ascii "OR123abc_") "OR123abc_"))
	(is (= (ascii "or123abc_") "or123abc_")))

(deftest POWDigitLetterUnderscore
	(is (= (ascii "POW123abc_") "POW123abc_"))
	(is (= (ascii "pow123abc_") "pow123abc_")))

(deftest POW1DigitLetterUnderscore
	(is (= (ascii "POW1123abc_") 
				"POW1123abc_"))
	(is (= (ascii "pow1123abc_") 
				"pow1123abc_")))

(deftest TRUEDigitLetterUnderscore
	(is (= (ascii "TRUE123abc_") 
				"TRUE123abc_"))
	(is (= (ascii "true123abc_") 
				"true123abc_")))

(deftest UNIONDigitLetterUnderscore
	(is (= (ascii "UNION123abc_") 
				"UNION123abc_"))
	(is (= (ascii "union123abc_") 
				"union123abc_")))

(deftest ANYDigitUnderscoreLetter
	(is (= (ascii "ANY123_abc") "ANY123_abc"))
	(is (= (ascii "any123_abc") "any123_abc")))

(deftest FALSEDigitUnderscoreLetter
	(is (= (ascii "FALSE123_abc") 
				"FALSE123_abc"))
	(is (= (ascii "false123_abc") 
				"false123_abc")))

(deftest INTEGERDigitUnderscoreLetter
	(is (= (ascii "INTEGER123_abc") 
				"INTEGER123_abc"))
	(is (= (ascii "integer123_abc") 
				"integer123_abc")))

(deftest INTERDigitUnderscoreLetter
	(is (= (ascii "INTER123_abc") 
				"INTER123_abc"))
	(is (= (ascii "inter123_abc") 
				"inter123_abc")))

(deftest NATDigitUnderscoreLetter
	(is (= (ascii "NAT123_abc") "NAT123_abc"))
	(is (= (ascii "nat123_abc") "nat123_abc")))

(deftest NAT1DigitUnderscoreLetter
	(is (= (ascii "NAT1123_abc") 
				"NAT1123_abc"))
	(is (= (ascii "nat1123_abc") 
				"nat1123_abc")))

(deftest NATURALDigitUnderscoreLetter
	(is (= (ascii "NATURAL123_abc") 
				"NATURAL123_abc"))
	(is (= (ascii "natural123_abc") 
				"natural123_abc")))

(deftest NOTDigitUnderscoreLetter
	(is (= (ascii "NOT123_abc") "NOT123_abc"))
	(is (= (ascii "not123_abc") "not123_abc")))

(deftest ORDigitUnderscoreLetter
	(is (= (ascii "OR123_abc") "OR123_abc"))
	(is (= (ascii "or123_abc") "or123_abc")))

(deftest POWDigitUnderscoreLetter
	(is (= (ascii "POW123_abc") "POW123_abc"))
	(is (= (ascii "pow123_abc") "pow123_abc")))

(deftest POW1DigitUnderscoreLetter
	(is (= (ascii "POW1123_abc") 
				"POW1123_abc"))
	(is (= (ascii "pow1123_abc") 
				"pow1123_abc")))

(deftest TRUEDigitUnderscoreLetter
	(is (= (ascii "TRUE123_abc") 
				"TRUE123_abc"))
	(is (= (ascii "true123_abc") 
				"true123_abc")))

(deftest UNIONDigitUnderscoreLetter
	(is (= (ascii "UNION123_abc") 
				"UNION123_abc"))
	(is (= (ascii "union123_abc") 
				"union123_abc")))

(deftest ANYUnderscoreLetterDigit
	(is (= (ascii "ANY_abc123") "ANY_abc123"))
	(is (= (ascii "any_abc123") "any_abc123")))

(deftest FALSEUnderscoreLetterDigit
	(is (= (ascii "FALSE_abc123") 
				"FALSE_abc123"))
	(is (= (ascii "false_abc123") 
				"false_abc123")))

(deftest INTEGERUnderscoreLetterDigit
	(is (= (ascii "INTEGER_abc123") 
				"INTEGER_abc123"))
	(is (= (ascii "integer_abc123") 
				"integer_abc123")))

(deftest INTERUnderscoreLetterDigit
	(is (= (ascii "INTER_abc123") 
				"INTER_abc123"))
	(is (= (ascii "inter_abc123") 
				"inter_abc123")))

(deftest NATUnderscoreLetterDigit
	(is (= (ascii "NAT_abc123") "NAT_abc123"))
	(is (= (ascii "nat_abc123") "nat_abc123")))

(deftest NAT1UnderscoreLetterDigit
	(is (= (ascii "NAT1_abc123") 
				"NAT1_abc123"))
	(is (= (ascii "nat1_abc123") 
				"nat1_abc123")))

(deftest NATURALUnderscoreLetterDigit
	(is (= (ascii "NATURAL_abc123") 
				"NATURAL_abc123"))
	(is (= (ascii "natural_abc123") 
				"natural_abc123")))

(deftest NOTUnderscoreLetterDigit
	(is (= (ascii "NOT_abc123") "NOT_abc123"))
	(is (= (ascii "not_abc123") "not_abc123")))

(deftest ORUnderscoreLetterDigit
	(is (= (ascii "OR_abc123") "OR_abc123"))
	(is (= (ascii "or_abc123") "or_abc123")))

(deftest POWUnderscoreLetterDigit
	(is (= (ascii "POW_abc123") "POW_abc123"))
	(is (= (ascii "pow_abc123") "pow_abc123")))

(deftest POW1UnderscoreLetterDigit
	(is (= (ascii "POW1_abc123") 
				"POW1_abc123"))
	(is (= (ascii "pow1_abc123") 
				"pow1_abc123")))

(deftest TRUEUnderscoreLetterDigit
	(is (= (ascii "TRUE_abc123") 
				"TRUE_abc123"))
	(is (= (ascii "true_abc123") 
				"true_abc123")))

(deftest UNIONUnderscoreLetterDigit
	(is (= (ascii "UNION_abc123") 
				"UNION_abc123"))
	(is (= (ascii "union_abc123") 
				"union_abc123")))

(deftest ANYUnderscoreDigitLetter
	(is (= (ascii "ANY_123abc") "ANY_123abc"))
	(is (= (ascii "any_123abc") "any_123abc")))

(deftest FALSEUnderscoreDigitLetter
	(is (= (ascii "FALSE_123abc") 
				"FALSE_123abc"))
	(is (= (ascii "false_123abc") 
				"false_123abc")))

(deftest INTEGERUnderscoreDigitLetter
	(is (= (ascii "INTEGER_123abc") 
				"INTEGER_123abc"))
	(is (= (ascii "integer_123abc") 
				"integer_123abc")))

(deftest INTERUnderscoreDigitLetter
	(is (= (ascii "INTER_123abc") 
				"INTER_123abc"))
	(is (= (ascii "inter_123abc") 
				"inter_123abc")))

(deftest NATUnderscoreDigitLetter
	(is (= (ascii "NAT_123abc") "NAT_123abc"))
	(is (= (ascii "nat_123abc") "nat_123abc")))

(deftest NAT1UnderscoreDigitLetter
	(is (= (ascii "NAT1_123abc") 
				"NAT1_123abc"))
	(is (= (ascii "nat1_123abc") 
				"nat1_123abc")))

(deftest NATURALUnderscoreDigitLetter
	(is (= (ascii "NATURAL_123abc") 
				"NATURAL_123abc"))
	(is (= (ascii "natural_123abc") 
				"natural_123abc")))

(deftest NOTUnderscoreDigitLetter
	(is (= (ascii "NOT_123abc") "NOT_123abc"))
	(is (= (ascii "not_123abc") "not_123abc")))

(deftest ORUnderscoreDigitLetter
	(is (= (ascii "OR_123abc") "OR_123abc"))
	(is (= (ascii "or_123abc") "or_123abc")))

(deftest POWUnderscoreDigitLetter
	(is (= (ascii "POW_123abc") "POW_123abc"))
	(is (= (ascii "pow_123abc") "pow_123abc")))

(deftest POW1UnderscoreDigitLetter
	(is (= (ascii "POW1_123abc") 
				"POW1_123abc"))
	(is (= (ascii "pow1_123abc") 
				"pow1_123abc")))

(deftest TRUEUnderscoreDigitLetter
	(is (= (ascii "TRUE_123abc") 
				"TRUE_123abc"))
	(is (= (ascii "true_123abc") 
				"true_123abc")))

(deftest UNIONUnderscoreDigitLetter
	(is (= (ascii "UNION_123abc") 
				"UNION_123abc"))
	(is (= (ascii "union_123abc") 
				"union_123abc")))

(deftest UnderscoreDigitUnderscore
	(is (= (ascii "_123_") "_123_")))

(deftest UnderscoreLetterUnderscore
	(is (= (ascii "_abc_") "_abc_")))

(deftest UnderscoreANYUnderscore
	(is (= (ascii "_ANY_") "_ANY_"))
	(is (= (ascii "_any_") "_any_")))

(deftest UnderscoreFALSEUnderscore
	(is (= (ascii "_FALSE_") "_FALSE_"))
	(is (= (ascii "_false_") "_false_")))

(deftest UnderscoreINTEGERUnderscore
	(is (= (ascii "_INTEGER_") "_INTEGER_"))
	(is (= (ascii "_integer_") "_integer_")))

(deftest UnderscoreINTERUnderscore
	(is (= (ascii "_INTER_") "_INTER_"))
	(is (= (ascii "_inter_") "_inter_")))

(deftest UnderscoreNATUnderscore
	(is (= (ascii "_NAT_") "_NAT_"))
	(is (= (ascii "_nat_") "_nat_")))

(deftest UnderscoreNAT1Underscore
	(is (= (ascii "_NAT1_") "_NAT1_"))
	(is (= (ascii "_nat1_") "_nat1_")))

(deftest UnderscoreNATURALUnderscore
	(is (= (ascii "_NATURAL_") "_NATURAL_"))
	(is (= (ascii "_natural_") "_natural_")))

(deftest UnderscoreNOTUnderscore
	(is (= (ascii "_NOT_") "_NOT_"))
	(is (= (ascii "_not_") "_not_")))

(deftest UnderscoreORUnderscore
	(is (= (ascii "_OR_") "_OR_"))
	(is (= (ascii "_or_") "_or_")))

(deftest UnderscorePOWUnderscore
	(is (= (ascii "_POW_") "_POW_"))
	(is (= (ascii "_pow_") "_pow_")))

(deftest UnderscorePOW1Underscore
	(is (= (ascii "_POW1_") "_POW1_"))
	(is (= (ascii "_pow1_") "_pow1_")))

(deftest UnderscoreTRUEUnderscore
	(is (= (ascii "_TRUE_") "_TRUE_"))
	(is (= (ascii "_true_") "_true_")))

(deftest UnderscoreUNIONUnderscore
	(is (= (ascii "_UNION_") "_UNION_"))
	(is (= (ascii "_union_") "_union_")))

(deftest LetterUnderscoreDigitUnderscoreLetter
	(is (= (ascii "abc_123_abc") 
				"abc_123_abc")))

(deftest LetterUnderscoreLetterUnderscoreLetter
	(is (= (ascii "abc_abc_abc") 
				"abc_abc_abc")))

(deftest LetterUnderscoreANYUnderscoreLetter
	(is (= (ascii "abc_ANY_abc") 
				"abc_ANY_abc"))
	(is (= (ascii "abc_any_abc") 
				"abc_any_abc")))

(deftest LetterUnderscoreFALSEUnderscoreLetter
	(is (= (ascii "abc_FALSE_abc") 
				"abc_FALSE_abc"))
	(is (= (ascii "abc_false_abc") 
				"abc_false_abc")))

(deftest LetterUnderscoreINTEGERUnderscoreLetter
	(is (= (ascii "abc_INTEGER_abc") 
				"abc_INTEGER_abc"))
	(is (= (ascii "abc_integer_abc") 
				"abc_integer_abc")))

(deftest LetterUnderscoreINTERUnderscoreLetter
	(is (= (ascii "abc_INTER_abc") 
				"abc_INTER_abc"))
	(is (= (ascii "abc_inter_abc") 
				"abc_inter_abc")))

(deftest LetterUnderscoreNATUnderscoreLetter
	(is (= (ascii "abc_NAT_abc") 
				"abc_NAT_abc"))
	(is (= (ascii "abc_nat_abc") 
				"abc_nat_abc")))

(deftest LetterUnderscoreNAT1UnderscoreLetter
	(is (= (ascii "abc_NAT1_abc") 
				"abc_NAT1_abc"))
	(is (= (ascii "abc_nat1_abc") 
				"abc_nat1_abc")))

(deftest LetterUnderscoreNATURALUnderscoreLetter
	(is (= (ascii "abc_NATURAL_abc") 
				"abc_NATURAL_abc"))
	(is (= (ascii "abc_natural_abc") 
				"abc_natural_abc")))

(deftest LetterUnderscoreNOTUnderscoreLetter
	(is (= (ascii "abc_NOT_abc") 
				"abc_NOT_abc"))
	(is (= (ascii "abc_not_abc") 
				"abc_not_abc")))

(deftest LetterUnderscoreORUnderscoreLetter
	(is (= (ascii "abc_OR_abc") "abc_OR_abc"))
	(is (= (ascii "abc_or_abc") "abc_or_abc")))

(deftest LetterUnderscorePOWUnderscoreLetter
	(is (= (ascii "abc_POW_abc") 
				"abc_POW_abc"))
	(is (= (ascii "abc_pow_abc") 
				"abc_pow_abc")))

(deftest LetterUnderscorePOW1UnderscoreLetter
	(is (= (ascii "abc_POW1_abc") 
				"abc_POW1_abc"))
	(is (= (ascii "abc_pow1_abc") 
				"abc_pow1_abc")))

(deftest LetterUnderscoreTRUEUnderscoreLetter
	(is (= (ascii "abc_TRUE_abc") 
				"abc_TRUE_abc"))
	(is (= (ascii "abc_true_abc") 
				"abc_true_abc")))

(deftest LetterUnderscoreUNIONUnderscoreLetter
	(is (= (ascii "abc_UNION_abc") 
				"abc_UNION_abc"))
	(is (= (ascii "abc_union_abc") 
				"abc_union_abc")))

(deftest DigitUnderscoreDigitUnderscoreDigit
	(is (= (ascii "123_123_123") 
				"123_123_123")))

(deftest DigitUnderscoreLetterUnderscoreDigit
	(is (= (ascii "123_abc_123") 
				"123_abc_123")))

(deftest DigitUnderscoreANYUnderscoreDigit
	(is (= (ascii "123_ANY_123") 
				"123_ANY_123"))
	(is (= (ascii "123_any_123") 
				"123_any_123")))

(deftest DigitUnderscoreFALSEUnderscoreDigit
	(is (= (ascii "123_FALSE_123") 
				"123_FALSE_123"))
	(is (= (ascii "123_false_123") 
				"123_false_123")))

(deftest DigitUnderscoreINTEGERUnderscoreDigit
	(is (= (ascii "123_INTEGER_123") 
				"123_INTEGER_123"))
	(is (= (ascii "123_integer_123") 
				"123_integer_123")))

(deftest DigitUnderscoreINTERUnderscoreDigit
	(is (= (ascii "123_INTER_123") 
				"123_INTER_123"))
	(is (= (ascii "123_inter_123") 
				"123_inter_123")))

(deftest DigitUnderscoreNATUnderscoreDigit
	(is (= (ascii "123_NAT_123") 
				"123_NAT_123"))
	(is (= (ascii "123_nat_123") 
				"123_nat_123")))

(deftest DigitUnderscoreNAT1UnderscoreDigit
	(is (= (ascii "123_NAT1_123") 
				"123_NAT1_123"))
	(is (= (ascii "123_nat1_123") 
				"123_nat1_123")))

(deftest DigitUnderscoreNATURALUnderscoreDigit
	(is (= (ascii "123_NATURAL_123") 
				"123_NATURAL_123"))
	(is (= (ascii "123_natural_123") 
				"123_natural_123")))

(deftest DigitUnderscoreNOTUnderscoreDigit
	(is (= (ascii "123_NOT_123") 
				"123_NOT_123"))
	(is (= (ascii "123_not_123") 
				"123_not_123")))

(deftest DigitUnderscoreORUnderscoreDigit
	(is (= (ascii "123_OR_123") "123_OR_123"))
	(is (= (ascii "123_or_123") "123_or_123")))

(deftest DigitUnderscorePOWUnderscoreDigit
	(is (= (ascii "123_POW_123") 
				"123_POW_123"))
	(is (= (ascii "123_pow_123") 
				"123_pow_123")))

(deftest DigitUnderscorePOW1UnderscoreDigit
	(is (= (ascii "123_POW1_123") 
				"123_POW1_123"))
	(is (= (ascii "123_pow1_123") 
				"123_pow1_123")))

(deftest DigitUnderscoreTRUEUnderscoreDigit
	(is (= (ascii "123_TRUE_123") 
				"123_TRUE_123"))
	(is (= (ascii "123_true_123") 
				"123_true_123")))

(deftest DigitUnderscoreUNIONUnderscoreDigit
	(is (= (ascii "123_UNION_123") 
				"123_UNION_123"))
	(is (= (ascii "123_union_123") 
				"123_union_123")))

(deftest Var_123
	(is (= (ascii "var_123") "var_123"))
	(is (= (ascii "123_var") "123_var"))
	(is (= (ascii "var_123_var") 
				"var_123_var"))

	(is (= (ascii "var_") "var_"))
	(is (= (ascii "_var") "_var"))
	(is (= (ascii "_var_") "_var_"))

	(is (= (ascii "123_") "123_"))
	(is (= (ascii "_123") "_123"))
	(is (= (ascii "_123_") "_123_")))

(deftest Var123
	(is (= (ascii "var123") "var123"))
	(is (= (ascii "123var") "123var"))
	(is (= (ascii "var123var") "var123var"))
	(is (= (ascii "123var123") "123var123")))

(deftest VarANY
	(is (= (ascii "varANY") "varANY"))
	(is (= (ascii "varany") "varany"))
	(is (= (ascii "varANYvar") "varANYvar"))
	(is (= (ascii "varanyvar") "varanyvar"))
	(is (= (ascii "ANYvar") "ANYvar"))
	(is (= (ascii "anyvar") "anyvar"))

	(is (= (ascii "123any") "123any"))
	(is (= (ascii "123ANY") "123ANY"))
	(is (= (ascii "123ANY123") "123ANY123"))
	(is (= (ascii "123any123") "123any123"))
	(is (= (ascii "ANY123") "ANY123"))
	(is (= (ascii "any123") "any123"))

	(is (= (ascii "_any") "_any"))
	(is (= (ascii "_ANY") "_ANY"))
	(is (= (ascii "_ANY_") "_ANY_"))
	(is (= (ascii "_any_") "_any_"))
	(is (= (ascii "ANY_") "ANY_"))
	(is (= (ascii "any_") "any_")))

(deftest VarFALSE
	(is (= (ascii "varFALSE") "varFALSE"))
	(is (= (ascii "varfalse") "varfalse"))
	(is (= (ascii "varFALSEvar") 
				"varFALSEvar"))
	(is (= (ascii "varfalsevar") 
				"varfalsevar"))
	(is (= (ascii "FALSEvar") "FALSEvar"))
	(is (= (ascii "falsevar") "falsevar"))

	(is (= (ascii "123FALSE") "123FALSE"))
	(is (= (ascii "123false") "123false"))
	(is (= (ascii "123FALSE123") 
				"123FALSE123"))
	(is (= (ascii "123false123") 
				"123false123"))
	(is (= (ascii "FALSE123") "FALSE123"))
	(is (= (ascii "false123") "false123"))

	(is (= (ascii "_FALSE") "_FALSE"))
	(is (= (ascii "_false") "_false"))
	(is (= (ascii "_FALSE_") "_FALSE_"))
	(is (= (ascii "_false_") "_false_"))
	(is (= (ascii "FALSE_") "FALSE_"))
	(is (= (ascii "false_") "false_")))

(deftest VarINTEGER
	(is (= (ascii "varINTEGER") "varINTEGER"))
	(is (= (ascii "varinteger") "varinteger"))
	(is (= (ascii "varINTEGERvar") 
				"varINTEGERvar"))
	(is (= (ascii "varintegervar") 
				"varintegervar"))
	(is (= (ascii "INTEGERvar") "INTEGERvar"))
	(is (= (ascii "integervar") "integervar"))

	(is (= (ascii "123INTEGER") "123INTEGER"))
	(is (= (ascii "123integer") "123integer"))
	(is (= (ascii "INTEGER123") "INTEGER123"))
	(is (= (ascii "integer123") "integer123"))
	(is (= (ascii "123INTEGER123") 
				"123INTEGER123"))
	(is (= (ascii "123integer123") 
				"123integer123"))

	(is (= (ascii "_INTEGER") "_INTEGER"))
	(is (= (ascii "_integer") "_integer"))
	(is (= (ascii "_INTEGER_") "_INTEGER_"))
	(is (= (ascii "_integer_") "_integer_"))
	(is (= (ascii "INTEGER_") "INTEGER_"))
	(is (= (ascii "integer_") "integer_")))

(deftest VarINTER
	(is (= (ascii "varINTER") "varINTER"))
	(is (= (ascii "varinter") "varinter"))
	(is (= (ascii "varINTERvar") 
				"varINTERvar"))
	(is (= (ascii "varintervar") 
				"varintervar"))
	(is (= (ascii "INTERvar") "INTERvar"))
	(is (= (ascii "intervar") "intervar"))

	(is (= (ascii "123inter") "123inter"))
	(is (= (ascii "123INTER") "123INTER"))
	(is (= (ascii "123INTER123") 
				"123INTER123"))
	(is (= (ascii "123inter123") 
				"123inter123"))
	(is (= (ascii "INTER123") "INTER123"))
	(is (= (ascii "inter123") "inter123"))

	(is (= (ascii "_INTER") "_INTER"))
	(is (= (ascii "_inter") "_inter"))
	(is (= (ascii "_INTER_") "_INTER_"))
	(is (= (ascii "_inter_") "_inter_"))
	(is (= (ascii "INTER_") "INTER_"))
	(is (= (ascii "inter_") "inter_")))

(deftest VarNAT
	(is (= (ascii "varNAT") "varNAT"))
	(is (= (ascii "varnat") "varnat"))
	(is (= (ascii "varNATvar") "varNATvar"))
	(is (= (ascii "varnatvar") "varnatvar"))
	(is (= (ascii "NATvar") "NATvar"))
	(is (= (ascii "natvar") "natvar"))

	(is (= (ascii "123NAT") "123NAT"))
	(is (= (ascii "123nat") "123nat"))
	(is (= (ascii "123NAT123") "123NAT123"))
	(is (= (ascii "123nat123") "123nat123"))
	(is (= (ascii "NAT123") "NAT123"))
	(is (= (ascii "nat123") "nat123"))

	(is (= (ascii "_NAT") "_NAT"))
	(is (= (ascii "_nat") "_nat"))
	(is (= (ascii "_NAT_") "_NAT_"))
	(is (= (ascii "_nat_") "_nat_"))
	(is (= (ascii "NAT_") "NAT_"))
	(is (= (ascii "nat_") "nat_")))

(deftest VarNAT1
	(is (= (ascii "varNAT1") "varNAT1"))
	(is (= (ascii "varnat1") "varnat1"))
	(is (= (ascii "varNAT1var") "varNAT1var"))
	(is (= (ascii "varnat1var") "varnat1var"))
	(is (= (ascii "NAT1var") "NAT1var"))
	(is (= (ascii "nat1var") "nat1var"))

	(is (= (ascii "123NAT1") "123NAT1"))
	(is (= (ascii "123nat1") "123nat1"))
	(is (= (ascii "123NAT1123") "123NAT1123"))
	(is (= (ascii "123nat1123") "123nat1123"))
	(is (= (ascii "NAT1123") "NAT1123"))
	(is (= (ascii "nat1123") "nat1123"))

	(is (= (ascii "_NAT1") "_NAT1"))
	(is (= (ascii "_nat1") "_nat1"))
	(is (= (ascii "_NAT1_") "_NAT1_"))
	(is (= (ascii "_nat1_") "_nat1_"))
	(is (= (ascii "NAT1_") "NAT1_"))
	(is (= (ascii "nat1_") "nat1_")))

(deftest VarNATURAL
	(is (= (ascii "varNATURAL") "varNATURAL"))
	(is (= (ascii "varnatural") "varnatural"))
	(is (= (ascii "varNATURALvar") 
				"varNATURALvar"))
	(is (= (ascii "varnaturalvar") 
				"varnaturalvar"))
	(is (= (ascii "NATURALvar") "NATURALvar"))
	(is (= (ascii "naturalvar") "naturalvar"))

	(is (= (ascii "123NATURAL") "123NATURAL"))
	(is (= (ascii "123natural") "123natural"))
	(is (= (ascii "123NATURAL123") 
				"123NATURAL123"))
	(is (= (ascii "123natural123") 
				"123natural123"))
	(is (= (ascii "NATURAL123") "NATURAL123"))
	(is (= (ascii "natural123") "natural123"))

	(is (= (ascii "_NATURAL") "_NATURAL"))
	(is (= (ascii "_natural") "_natural"))
	(is (= (ascii "_NATURAL_") "_NATURAL_"))
	(is (= (ascii "_natural_") "_natural_"))
	(is (= (ascii "NATURAL_") "NATURAL_"))
	(is (= (ascii "natural_") "natural_")))

(deftest VarNOT
	(is (= (ascii "varNOT") "varNOT"))
	(is (= (ascii "varnot") "varnot"))
	(is (= (ascii "varNOTvar") "varNOTvar"))
	(is (= (ascii "varnotvar") "varnotvar"))
	(is (= (ascii "NOTvar") "NOTvar"))
	(is (= (ascii "notvar") "notvar"))

	(is (= (ascii "123NOT") "123NOT"))
	(is (= (ascii "123not") "123not"))
	(is (= (ascii "123NOT123") "123NOT123"))
	(is (= (ascii "123not123") "123not123"))
	(is (= (ascii "NOT123") "NOT123"))
	(is (= (ascii "not123") "not123"))

	(is (= (ascii "_NOT") "_NOT"))
	(is (= (ascii "_not") "_not"))
	(is (= (ascii "_NOT_") "_NOT_"))
	(is (= (ascii "_not_") "_not_"))
	(is (= (ascii "NOT_") "NOT_"))
	(is (= (ascii "not_") "not_")))

(deftest VarOr
	(is (= (ascii "varOR") "varOR"))
	(is (= (ascii "varor") "varor"))
	(is (= (ascii "varORvar") "varORvar"))
	(is (= (ascii "varorvar") "varorvar"))
	(is (= (ascii "ORvar") "ORvar"))
	(is (= (ascii "orvar") "orvar"))

	(is (= (ascii "123OR") "123OR"))
	(is (= (ascii "123or") "123or"))
	(is (= (ascii "123OR123") "123OR123"))
	(is (= (ascii "123or123") "123or123"))
	(is (= (ascii "OR123") "OR123"))
	(is (= (ascii "or123") "or123"))

	(is (= (ascii "_OR") "_OR"))
	(is (= (ascii "_or") "_or"))
	(is (= (ascii "_OR_") "_OR_"))
	(is (= (ascii "_or_") "_or_"))
	(is (= (ascii "OR_") "OR_"))
	(is (= (ascii "or_") "or_")))

(deftest VarPOW
	(is (= (ascii "varPOW") "varPOW"))
	(is (= (ascii "varpow") "varpow"))
	(is (= (ascii "varPOWvar") "varPOWvar"))
	(is (= (ascii "varpowvar") "varpowvar"))
	(is (= (ascii "POWvar") "POWvar"))
	(is (= (ascii "powvar") "powvar"))

	(is (= (ascii "123POW") "123POW"))
	(is (= (ascii "123pow") "123pow"))
	(is (= (ascii "123POW123") "123POW123"))
	(is (= (ascii "123pow123") "123pow123"))
	(is (= (ascii "POW123") "POW123"))
	(is (= (ascii "pow123") "pow123"))

	(is (= (ascii "_POW") "_POW"))
	(is (= (ascii "_pow") "_pow"))
	(is (= (ascii "_POW_") "_POW_"))
	(is (= (ascii "_pow_") "_pow_"))
	(is (= (ascii "POW_") "POW_"))
	(is (= (ascii "pow_") "pow_")))

(deftest VarPOW1
	(is (= (ascii "varPOW1") "varPOW1"))
	(is (= (ascii "varpow1") "varpow1"))
	(is (= (ascii "varPOW1var") "varPOW1var"))
	(is (= (ascii "varpow1var") "varpow1var"))
	(is (= (ascii "POW1var") "POW1var"))
	(is (= (ascii "pow1var") "pow1var"))

	(is (= (ascii "123POW1") "123POW1"))
	(is (= (ascii "123pow1") "123pow1"))
	(is (= (ascii "123POW1123") "123POW1123"))
	(is (= (ascii "123pow1123") "123pow1123"))
	(is (= (ascii "POW1123") "POW1123"))
	(is (= (ascii "pow1123") "pow1123"))

	(is (= (ascii "_POW1") "_POW1"))
	(is (= (ascii "_pow1") "_pow1"))
	(is (= (ascii "_POW1_") "_POW1_"))
	(is (= (ascii "_pow1_") "_pow1_"))
	(is (= (ascii "POW1_") "POW1_"))
	(is (= (ascii "pow1_") "pow1_")))

(deftest VarTRUE
	(is (= (ascii "varTRUE") "varTRUE"))
	(is (= (ascii "vartrue") "vartrue"))
	(is (= (ascii "varTRUEvar") "varTRUEvar"))
	(is (= (ascii "vartruevar") "vartruevar"))
	(is (= (ascii "TRUEvar") "TRUEvar"))
	(is (= (ascii "truevar") "truevar"))

	(is (= (ascii "123TRUE") "123TRUE"))
	(is (= (ascii "123true") "123true"))
	(is (= (ascii "123TRUE123") "123TRUE123"))
	(is (= (ascii "123true123") "123true123"))
	(is (= (ascii "TRUE123") "TRUE123"))
	(is (= (ascii "true123") "true123"))

	(is (= (ascii "_TRUE") "_TRUE"))
	(is (= (ascii "_true") "_true"))
	(is (= (ascii "_TRUE_") "_TRUE_"))
	(is (= (ascii "_true_") "_true_"))
	(is (= (ascii "TRUE_") "TRUE_"))
	(is (= (ascii "true_") "true_")))

(deftest VarUNION
	(is (= (ascii "varUNION") "varUNION"))
	(is (= (ascii "varunion") "varunion"))
	(is (= (ascii "varUNIONvar") 
				"varUNIONvar"))
	(is (= (ascii "varunionvar") 
				"varunionvar"))
	(is (= (ascii "UNIONvar") "UNIONvar"))
	(is (= (ascii "unionvar") "unionvar"))

	(is (= (ascii "123UNION") "123UNION"))
	(is (= (ascii "123union") "123union"))
	(is (= (ascii "123UNION123") 
				"123UNION123"))
	(is (= (ascii "123union123") 
				"123union123"))
	(is (= (ascii "UNION123") "UNION123"))
	(is (= (ascii "union123") "union123"))

	(is (= (ascii "_UNION") "_UNION"))
	(is (= (ascii "_union") "_union"))
	(is (= (ascii "_UNION_") "_UNION_"))
	(is (= (ascii "_union_") "_union_"))
	(is (= (ascii "UNION_") "UNION_"))
	(is (= (ascii "union_") "union_")))