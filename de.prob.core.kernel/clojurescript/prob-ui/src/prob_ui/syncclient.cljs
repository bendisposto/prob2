(ns prob-ui.syncclient
  (:require [goog.Uri :as uri]
            [goog.net.XhrIo :as xhr]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.reader :as r]))

(enable-console-print!)

(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
nested structure. keys is a sequence of keys. Any empty maps that result
will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

(defn prep-merge [path mergemap]
  (if (seq path) (assoc-in {} path mergemap) mergemap))

(def ^:export state (atom {:current -1 :state {}}))

(defmulti mk-fn first)

(defmethod mk-fn :set [[_ path val]] (fn [s] 
  (println :set s path val)
                                       (if (seq path)
                                         (update-in s path (constantly val))
                                         (merge s val))))

(defmethod mk-fn :del-keys [[_ path dkys]]
  (fn [s] 
     (println :delkeys s path dkys)
    (let [x (map #(into path %) dkys)]
           (reduce (fn [s e] (dissoc-in s e)) s dkys))))

(defmethod mk-fn :merge [[_ path add]]
  (fn [s] (println :merge s path add)
    (merge s (prep-merge path add))))

(defmethod mk-fn :concat [[_ path elems]]
  (fn [s] (println :concat s path elems)
    (update-in s path #(into % elems))))

(defmethod mk-fn :del [[_ path index]]
  (fn [s] (println :del s path index)(update-in s path
                    (fn [v]
                      (into [] (remove nil?
                                       (map-indexed (fn [i v] (if ((into #{} index) i) nil v)) v)))))))


(defn update-state [updates]
  (map mk-fn updates))

(defn receiver [event]
  (let [response (.getResponseText (.-target event))
    _ (println response)
        [id changes] (r/read-string response)
        _ (println "id" id)
        _ (println "content" changes)
        fns (doall (map mk-fn changes))
        _ (println "fns" fns)
        chg-fkt (apply comp fns)
        ]
    (swap! state (fn [cs] (let [os (:state cs)
      _ (println "oldstate" os)
                                ns (chg-fkt os)
                                _ (println "newstate" ns)]
                           (assoc os :current id :state ns)))))
  (println "receiver done"))

(defn get-state [url]
  (xhr/send url receiver "GET" "")
  (println "get-state done"))

(defn ^:export pp-state []
  (let [cs @state]
    (println "ID:" (cs :current))
    (println "State:" (cs :state))))

(defn ^:export get-updates []
  (let [url (str  "/data?state=" (:current @state))]
    (get-state url)))


