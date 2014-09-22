(ns prob-ui.state-store
  (:use [prob-ui.schemas])
  (:require [prob-ui.sync :as sync]
            [schema.core :as schema]
            [cognitect.transit :as transit])
  (:import com.google.common.cache.CacheBuilder
           java.util.concurrent.TimeUnit
           java.io.ByteArrayOutputStream
           ))

(def cache! (.. (CacheBuilder/newBuilder)
                (expireAfterAccess 500 TimeUnit/SECONDS)
                (build)))

(def current-state
  (atom {:state {}
         :current 0}))

(defn- get-from-cache [old-state]
  (when old-state (.getIfPresent cache! old-state)))

(defn- store! [state]
  (swap! current-state
         (fn [cs]
           (let [nk (inc (:current cs))]
             (.put cache! (str nk) {:state state :current nk})
             (assoc cs :state state :current nk)))))

(defn- mkStr [delta]
  (let [s (ByteArrayOutputStream. 4096)
        w (transit/writer s :json)]
    (transit/write w delta)
    (.toString s)))

(defn transact! [txs]
  (store! (sync/compute-new-state @current-state txs)))

(defn get-cached-state [state-id]
  (let [cs @current-state]
    (if (= (:current cs) state-id)
      cs
      (get-from-cache state-id))))


(defn delta
  ([os-id] (delta (get-cached-state os-id) @current-state))
  ([os cs] (mkStr (sync/compute-delta os cs))))
