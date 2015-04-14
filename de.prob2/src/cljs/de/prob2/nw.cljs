(ns de.prob2.nw)

(def gui (js/require "nw.gui"))
(def process (js/require "process"))
(def fs (js/require "fs"))

(defn os-name []
  (.-platform process))

(defn slurp [filename]
  (.readFileSync fs filename "utf8"))

(defn read-string [s]
  (cljs.reader/read-string s))
