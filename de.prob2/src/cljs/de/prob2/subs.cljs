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


(defn ^:extern js-subs-handler
  "Forwards changes to a subscription to a handler. First argument
  is the handler id that should be triggered. The second argument
  is a subscription pattern. All following arguments are added to
  the handler dispatch call. All arguments are preprocessed by the
  Clojurescript reader. The handler is called with the changed
  subscription. The arguments are also passed into this funtion.
  The registration of the handler must be done separately.

  For instance: We have a handler registered that uses the key :foo
  and we want to trigger :foo each time the trace with id
  09b2fdfa-f49b-4c5f-be64-ea5e63f0d628 changes.

  de.prob2.subs.subs_js_handler(':foo', '[:trace #uuid \"09b2fdfa-f49b-4c5f-be64-ea5e63f0d628\"])

  A matching handler could be registered with
  de.prob2.subs.js_register_handler(
  ':foo',
  function(x,db) { console.log('changed',x,
  'fulltree',db,
  'args',arguments);})

  Important: A handler should not take longer than 16ms to run. Long
  running handlers are supposed to split up the work into chunks and
  call themselfs after 16 ms. Information for splitting up the work
  can be encoded in the optional arguments.
  "
  [handler hook & args]
  (let [real-handler (cljs.reader/read-string handler)
        real-hook (cljs.reader/read-string hook)
        x (rf/subscribe real-hook)]
    (ra/run! (rf/dispatch
              (into [real-handler
                     (clj->js @x)]
                    args)))))


(defn ^:extern js-register-handler
  "Registers a handler function. The first argument is the handler key,
  the second argument is a function of at least two arguments.
  The first argument is the changed part of the state. The second argument
  is the full state. Additional arguments are those that were passed from
  the subscription.

  Important: A handler should not take longer than 16ms to run. Long
  running handlers are supposed to split up the work into chunks and
  call themselfs after 16 ms. Information for splitting up the work
  can be encoded in the optional arguments.
  "
  [handler f]
  (let [real-handler (cljs.reader/read-string handler)]
    (rf/register-handler
     real-handler
     (fn [db [_ x & args]]
       (let [jsdb (clj->js db)
             jsx (clj->js x)
             res (apply f jsx jsdb args)]
         db)))))


