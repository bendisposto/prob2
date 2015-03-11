(ns de.prob2.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf :refer [register-sub]]
            [taoensso.encore :as enc  :refer (logf log logp)]))


(register-sub
 :initialised?
 (fn  [db]
   (reaction (not (empty? @db)))))

(register-sub
 :encoding-set?
 (fn  [db]
   (reaction (:encoding @db))))

(register-sub
 :connected?
 (fn [db]
  (reaction (:connected? @db))))

(register-sub
 :traces
 (fn [db]
   (reaction (:traces @db))))
