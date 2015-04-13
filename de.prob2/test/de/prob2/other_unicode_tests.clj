(ns de.prob2.other-unicode-tests
	(:require [de.prob2.unicodetranslator :refer (ascii,unicode)])
	(:use clojure.test))

(deftest empty-from-ascii (is (= "" (ascii ""))))

(deftest empty-from-unicode (is (= "" (unicode ""))))

(deftest tests-for-Ascii
	(is (= "nafor" (ascii "nafor")))
	(is (= "x:   NAT" (ascii "x\u2208   \u2115")))
	(is (= "x:NAT" (ascii "x\u2208\u2115")))
	(is (= "x  :NAT" (ascii "x  \u2208\u2115")))
	(is (= "INTERNAT" (ascii "INTERNAT")))
	(is (= "cur_floor := groundf" (ascii "cur_floor \u2254 groundf")))
	(is (= "cur_floor < groundf" (ascii "cur_floor < groundf")))
	(is (= "direction_up = FALSE" (ascii "direction_up = FALSE")))
	(is (= "b : (groundf .. topf)" (ascii "b \u2208 (groundf \u2025 topf)")))
	(is (= "call_buttons := call_buttons \\/ {b}" (ascii "call_buttons \u2254 call_buttons \u222a {b}")))
	(is (= "b /: call_buttons" (ascii "b \u2209 call_buttons"))))

(deftest tests-to-unicode
	(is (= "INTERNAT" (unicode "INTERNAT"))))

(deftest horrible-names
	(is (= "POW12" (unicode "POW12"))))

(deftest testQuotes
	(is (= "\"" (unicode "\""))))

(deftest NoSpaceAndAmpersand
	(is (= "active \u2227 waiting" (unicode "active & waiting")))
	(is (= "active \u2227waiting" (unicode "active &waiting")))
	(is (= "active\u2227 waiting" (unicode "active& waiting")))
	(is (= "active\u2227waiting" (unicode "active&waiting"))))

(deftest PROBCORE413
	(let [unicodeNoSpaces "\u2200r\u2982ROUTES\u00B7r\u2208ROUTES\u21D2(\u2200S\u2982\u2119(BLOCKS)\u00B7S\u2286BLOCKS\u2227S\u2286(nxt(r))[S]\u21D2S=(\u2205 \u2982 \u2119(BLOCKS)))"
		  unicodeWithSpaces "\u2200r \u2982 ROUTES\u00B7r\u2208ROUTES\u21D2(\u2200S \u2982 \u2119(BLOCKS)\u00B7S\u2286BLOCKS\u2227S\u2286(nxt(r))[S]\u21D2S=(\u2205 \u2982 \u2119(BLOCKS)))"
		  asciii "!r oftype ROUTES.r:ROUTES=>(!S oftype POW(BLOCKS).S<:BLOCKS&S<:(nxt(r))[S]=>S=({} oftype POW(BLOCKS)))"]
		(is (= asciii (ascii unicodeNoSpaces)))
		(is (= unicodeWithSpaces (unicode asciii)))))

(deftest PARSERLIB22
	(let [unicodee "\u22a4\u2228\u00acmss_button=ato\u2228ato_availability=TRUE";
		  unicodeWithSpaces "\u22a4 \u2228 \u00ac mss_button=ato \u2228 ato_availability=TRUE";
		  asciii (ascii unicodee)]
		  (is (= "true or not mss_button=ato or ato_availability=TRUE" asciii))
		  (is (= unicodeWithSpaces (unicode asciii)))))

;The problem is when identifier is a prime
(deftest PARSERLIB23
	(let [unicodee "current_mode=mss_button'\u2228stationary=TRUE";
		  unicodeWithSpaces "current_mode=mss_button' \u2228 stationary=TRUE";
		  asciii (ascii unicodee)]
		(is (= unicodeWithSpaces (unicode asciii)))))

;Problem with number before or
(deftest testLovelyOrAndNumberProblem
	(let [unicodee "x=1\u2228y=2";
		  unicodeWithSpaces "x=1 \u2228 y=2";
		  asciii (ascii unicodee)]
		(is (= unicodeWithSpaces (unicode asciii)))))

(deftest testNAT1NoSpace
	(let [asciii "NAT1";
		  unicodee "\u21151"]
		(is (= unicodee (unicode asciii)))
		(is (= asciii (ascii unicodee)))))

(deftest testPOW1NoSpace
	(let [asciii "POW1"
		  unicodee "\u21191"]
		(is (= unicodee (unicode asciii)))
		(is (= asciii (ascii unicodee)))))

(deftest testUnicodeInVsSetMinus
	(let [unicodee "\u2200i,s·(s\u2208open \u2227 i\u2208INVARIANTS\u2216invs_to_verify[{s}] \u21d2 s \u21a6 i \u2208 truth)";
		  asciii "!i,s.(s:open & i:INVARIANTS\\invs_to_verify[{s}] => s |-> i : truth)"]
		(is (= asciii (ascii unicodee)))
		(is (= unicodee (unicode asciii)))))