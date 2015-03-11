(ns de.prob2.helpers
  (:require [cognitect.transit :as transit]))

(def id-store (clojure.core/atom 0))
(defn fresh-id []
  (let [x @id-store]
    (swap! id-store inc) x))


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


(defn pp-transition [{:keys [name parameters return-values]}]
  (let [ppp (if (seq parameters) (str "(" (clojure.string/join "," parameters) ")") "")
        pprv (if (seq return-values) (str (clojure.string/join "," return-values) \u21DC " ")  "")
        fname (fix-names name)] (str pprv fname ppp)))


(defn read-transit [db msg]
  (if (:encoding db)
    (let [r (transit/reader (:encoding db))]
      (transit/read r msg))
    (keyword msg)))


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
