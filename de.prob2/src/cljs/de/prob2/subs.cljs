(ns de.prob2.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf :refer [dispatch register-sub register-handler]]))


(register-sub
 :initialised?
 (fn  [db]
   (reaction (not (empty? @db)))))

(register-sub
 :encoding-set?
 (fn  [db]
   (reaction (:encoding @db))))

(register-sub
 :traces
 (fn [db]
   (reaction (:traces @db))))
