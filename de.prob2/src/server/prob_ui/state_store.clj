(ns prob-ui.state-store
  (:use [prob-ui.schemas])
  (:require [prob-ui.sync :as sync]
            [schema.core :as schema]
            [com.stuartsierra.component :as component]
            [clojure.core.cache :as cache]
            [cognitect.transit :as transit])
  (:import java.io.ByteArrayOutputStream))


(defrecord SyncStore []
  component/Lifecycle
  (start [this]
    (print "Initializing SyncStore")
    (assoc this :id 0 :state {} :cache (cache/ttl-cache-factory {} :ttl 10000)))
  (stop [this]
    (print "Stopping SyncStore")
    (dissoc this :id :state :cache)))

(defn new-syncstore [] (SyncStore.))

(defn store [syncstore state]
  (let [{:keys [id cache]} syncstore
        id' (inc id)]
    (assoc syncstore :id id' :state state :cache (cache/miss cache id' state))))

(defn fetch [syncstore id]
  (if (= id (:id syncstore))
    (:state syncstore)
    (get (:cache syncstore))))

(defn- mkStr [delta]
  (let [s (ByteArrayOutputStream. 4096)
        w (transit/writer s :json)]
    (transit/write w delta)
    (.toString s)))

#_(defn transact! [txs]
  (store! (sync/compute-new-state @current-state txs)))


#_(defn delta
  ([os-id] (delta (get-cached-state os-id) @current-state))
  ([os cs] (mkStr (sync/compute-delta os cs))))
