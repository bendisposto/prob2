(ns prob-ui.state-store
  (:use [prob-ui.schemas])
  (:require [prob-ui.sync :as sync]
            [schema.core :as schema]
            [com.stuartsierra.component :as component]
            [clojure.core.cache :as cache]
            [cognitect.transit :as transit])
  (:import java.io.ByteArrayOutputStream))

(def store! (atom {:id 0 :state {} :cache (cache/ttl-cache-factory {} :ttl 10000)}))

(defn fetch [sid]
  (let [{:keys [id state cache]} @store!]
  (if (= sid id)
    [state id state]
    [(get cache sid) id state])))

(defn- mkStr [delta]
  (let [s (ByteArrayOutputStream. 4096)
        w (transit/writer s :json)]
    (transit/write w delta)
    (.toString s)))

(defn transact! [txs]
  (let [{:keys [id state cache]} @store!
    state' (sync/compute-new-state state txs)
    id' (inc id)]
    (swap! store! assoc :id id' :state state' :cache (cache/miss cache id' state'))))

(defn delta
  ([os-id] (apply delta (fetch os-id)))
  ([os id cs] (mkStr (sync/compute-delta os id cs))))
