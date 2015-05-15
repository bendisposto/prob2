(ns de.prob2.subs
  (:require-macros [reagent.ratom :as ra :refer [reaction]])
  (:require [re-frame.core :as rf :refer [register-sub subscribe]]
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

(defn lookup-value [db [_ k]]
  (get-in db [:results k]))

(register-sub
 :current-state
 (fn [db [_ trace-id]]
   (let [trace (reaction (get-in @db [:traces trace-id]))
         trans (reaction (get @trace :current-transition))
         dst-id (reaction (get @trace :current-state))
         dst (reaction (get-in @db [:states @dst-id]))
         dvals (reaction (map (partial lookup-value @db) (:values @dst)))]
     (if @trans
       (let [src (reaction (get-in @db [:states (:src @trans)]))
             svals (reaction (map (partial lookup-value @db) (:values @src)))]
         (reaction {:previous @svals :current @dvals :info @dst}))
       (reaction :previous [] :current @dvals :info @dst)))))

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
     (reaction  [(:dependency-graph @model) (:components @model)]))))

(register-sub
 :animator-count
 (fn [db _]
   (let [traces (subscribe [:traces])
         grouped (reaction (group-by :model (vals @traces)))]
     (reaction (count @grouped)))))

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


(register-sub
 :constantly
 (fn [_ [_ v]]
   (reaction v)))

(register-sub
 :pages (fn [db] (let [pane (reaction (get-in @db [:ui :pane]))
                      pages (reaction (get-in @db [:ui :pages]))
                      active (reaction (first @pane))
                      active-page (reaction (assoc (get @pages @active) :class " active "))]
                  (reaction (assoc @pages (:id @active-page) @active-page)))))

(register-sub
 :height (fn [db] (reaction (get-in @db [:ui :screen :height]))))

(register-sub
 :width (fn [db] (reaction (get-in @db [:ui :screen :width]))))

(register-sub
 :minibuffer (fn [db] (reaction (get-in @db [:ui :show-minibuffer]))))

(register-sub
 :active (fn [db] (reaction (get-in @db [:ui :active]))))

(register-sub
 :active-content
 (fn [db] (let [active-page (reaction (get-in @db [:ui :active]))]
           (logp :active @active-page)
           (reaction (get-in @db [:ui :pages @active-page])))))

(register-sub
 :the-editor (fn [db] (reaction (get-in @db [:ui :the-editor]))))

