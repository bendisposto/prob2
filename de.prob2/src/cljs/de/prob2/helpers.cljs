(ns de.prob2.helpers
  (:require-macros [reagent.ratom :as ra])
  (:require [cognitect.transit :as transit]
            [clojure.string]
            [re-frame.core :as rf]
            [taoensso.encore :as enc  :refer (logf log logp)]))


(def host "localhost")
(def port 3000)

(defn mk-url [n] (str "http://" host ":" port "/" n))

(def id-store (clojure.core/atom 0))
(defn fresh-id []
  (swap! id-store inc))
 
(defn kebap-case
  [text]
  (clojure.string/join
   "-"
   (map (fn [s] (.toLowerCase s))
        (map second (re-seq #"([A-Z][a-z]*|[0-9])" text)))))

(defn dissoc-in
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (assoc m k newmap))
      m)
    (dissoc m k)))

(defn fix-names [name]
  (get {"$initialise_machine" "INITIALISATION"
        "$setup_constants" "SETUP CONSTANTS"} name name))

(defn title-case [kw]
  (let [s (name kw)
        [[f] l] (split-at 1 s)]
    (apply str (.toUpperCase (str f)) l)))

(defn pp-transition [{:keys [name parameters return-values]}]
  (let [ppp (if (seq parameters) (str "(" (clojure.string/join "," parameters) ")") "")
        pprv (if (seq return-values) (str (clojure.string/join "," return-values) \u21DC " ")  "")
        fname (fix-names name)] (str pprv fname ppp)))


(defn read-transit [db msg]
  (if (:encoding db)
    (let [r (transit/reader (:encoding db))]
      (transit/read r msg))
    (keyword msg)))


(defn odeep-merge
  "Like merge, but merges maps recursively."
  {:added "1.7"}
  [& maps]
  (if (every? map? maps)
    (apply merge-with odeep-merge maps)
    (last maps)))

(defn deep-merge [left right]
  (let [k (keys right)
        knew (remove #(contains? left %) k)
        kmerge (filter #(contains? left %) k)
        left' (reduce (fn [a e] (assoc a e (get right e))) left knew)]
    (reduce (fn [a e]
              (let [v (get left e)
                    v' (get right e)]
                (if (and (map? v) (map? v'))
                  (assoc a e (deep-merge v v'))
                  (if (= v v') a (assoc a e v'))))) left' kmerge)))

(defn subs->handler [handler hook & args]
  (let [x (rf/subscribe hook)]
    (ra/run! (rf/dispatch (into [handler @x] args)))))

(defn decode
  "Takes a handler of 2 arguments, where the second argument is a vector of a message type and a message. The message is read through transit if the encoding has been set. Otherwise it create a ketword from the message (only used when fetching the encoding from the server). This middleware should be applied before the with-send middleware."
  [handler]
  (fn [db v]
    (handler db [(first v) (read-transit db (second v))])))

(defn with-send
  "Takes a handler of 2 arguments, where the second argument is a vector of a message type, a message and a function that sends a message to the server. Should be applied last!"
  [handler]
  (fn [{{send! :send!} :websocket :as db} [type msg]]
    (handler db [type msg send!])))

(def relay
  (fn [{{send! :send!} :websocket :as db} [t m]]
    (send! [t m])
    db))


(defn fixedpoint [F guess eps?]
  ((fn [x]
     (let [x' (F x)]
       (if (eps? x x') x (recur x')))) guess))


(defn tc-step [x]
  (set (clojure.set/union x (for [[a b] x [c d] x :when (= b c)] [a d]))))

(defn tc [g] (let [e (:edges g)
                   ne (fixedpoint tc-step e =)]
               {:nodes (:nodes g) :edges ne}))


(defn remote-call [callback command & args]
  (rf/dispatch (into [:prob2/call callback :groovy identity command] args)))

(defn remote-clojure-call [callback command & args]
  (rf/dispatch (into [:prob2/call callback :clojure identity command] args)))
