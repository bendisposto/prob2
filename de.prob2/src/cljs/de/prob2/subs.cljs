(ns de.prob2.subs
  (:require-macros [reagent.ratom :as ra :refer [reaction]])
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

(register-sub
 :models
 (fn [db]
   (reaction (:models @db))))

(register-sub
 :model
 (fn [db [_ trace-id]]
   (let [model-id (reaction (get-in @db [:traces trace-id :model]))]
     (reaction (get-in @db [:models @model-id])))))

(register-sub
 :trace
 (fn [db [_ uuid]]
   (reaction (get-in @db [:traces uuid]))))

(register-sub
 :state
 (fn [db [_ state-spec]]
   (reaction (get-in @db [:states state-spec]))))

(register-sub
 :hierarchy
 (fn [db [_ trace-id]]
   (let [model-id (reaction (get-in @db [:traces trace-id :model]))
         model (reaction (get-in @db [:models @model-id]))]
     (reaction (:dependency-graph @model)))))


;; Watch out! If you use this subscription you tie your implementation
;; to a specific layout of the ui state tree. If possible, use a
;; specific subscription instead
(register-sub
 :state-path
 (fn [db [_ path-spec]]
   (reaction (get-in @db path-spec))))

;; Watch out! If you use this subscription you tie your implementation
;; to a specific layout of the ui state tree. If possible, use a
;; specific subscription instead
(register-sub
 :state-paths
 (fn [db [_ & path-specs]]
   (reaction (mapv (partial get-in @db) path-specs))))


