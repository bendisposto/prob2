(ns de.prob2.dom-translations
	(:require [de.prob2.kernel :as k]
   		      [clojure.test.check.clojure-test :refer (defspec)]
		      [clojure.test.check :as tc]
              [clojure.test.check.generators :as gen]
              [clojure.test.check.properties :as prop]))


(def true-prop (prop/for-all [x gen/nat] (>= x 0)))


(defspec testing 100 true-prop)