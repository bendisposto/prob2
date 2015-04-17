(ns de.prob2.nw
  (:require [cljs.reader :as r]))

(def gui (js/require "nw.gui"))
(def process (js/require "process"))
(def fs (js/require "fs"))

(defn os-name []
  (.-platform process))

(defn slurp [filename]
  (.readFileSync fs filename "utf8"))

(defn read-string [s]
  (r/read-string s))
