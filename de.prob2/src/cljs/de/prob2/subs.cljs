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


(defn ^extern subscribe-js-callback
  "Subscribes a Javascript callback function to a normal subscription
  hook. First argument is a JS function of one argument that should
  be called each time the subscription changes. The second argument is
  a Clojurescript subscription encoded as a String. i.e. it is read
  using the Clojurescript reader.

  Example:
  de.prob2.subs.register_js_callback(function(x) { console.log(\"changed\" x},'[:trace #uuid \"09b2fdfa-f49b-4c5f-be64-ea5e63f0d628\"]')"

  [callback hook]
  (let [x (rf/subscribe (cljs.reader/read-string hook))]
    (ra/run! (callback (clj->js @x)))))
