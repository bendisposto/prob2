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


(defn ^extern subs-js-handler
  "Forwards changes to a subscription to a handler. First argument
  is the handler id that should be triggered. The second argument
  is a subscription pattern. All following arguments are added to
  the handler dispatch call. All arguments are preprocessed by the
  Clojurescript reader. The handler is called with the complete
  subscription specification and the changes. Registration of the
  handler must be done separately.

  For instance: We have a handler registered that uses the key :foo
  and we want to trigger :foo each time the trace with id
  09b2fdfa-f49b-4c5f-be64-ea5e63f0d628 changes. 

  de.prob2.subs.subs_js_handler(':foo', '[:trace #uuid \"09b2fdfa-f49b-4c5f-be64-ea5e63f0d628\"])

  Important: A handler should not take longer than 16ms to run. Long
  running handlers are supposed to split up the work into chunks and
  call themselfs after 16 ms. Information for splitting up the work
  can be encoded in the optional arguments.
  "
  [handler hook & args]
  (let [real-handler (cljs.reader/read-string handler)
        real-hook (cljs.reader/read-string hook)
        real-args (map cljs.reader/read-string args)
        x (rf/subscribe real-hook)]
    (ra/run! (rf/dispatch
              (into [real-handler
                     (clj->js real-hook)
                     (clj->js @x)]
                    (clj->js real-args))))))


(comment  (defn ^extern subscribe-js-callback
  "Subscribes a Javascript callback function to a normal subscription
  hook. First argument is a JS function of one argument that should
  be called each time the subscription changes. The second argument is
  a Clojurescript subscription encoded as a String. i.e. it is read
  using the Clojurescript reader.

  Example:
  de.prob2.subs.register_js_callback(function(x) { console.log(\"changed\" x},'[:trace #uuid \"09b2fdfa-f49b-4c5f-be64-ea5e63f0d628\"]')

  Warning:
  Be careful what you put into the callback. The UI can become unresponsive if the callback is taking too long."

  [callback hook]
  (let [x (rf/subscribe (cljs.reader/read-string hook))]
    (ra/run! (callback (clj->js @x))))))
