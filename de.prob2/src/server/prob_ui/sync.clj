(ns prob-ui.sync
  (:use [prob-ui.schemas])
  (:require [clojure.set :as s]
            [schema.core :as schema])
  (:import groovy.lang.Closure
           com.google.common.cache.CacheBuilder
           java.util.concurrent.TimeUnit))


(defrecord Delta [action path value])

(defn- arg-count [f]
  (let [m (first (.getDeclaredMethods (class f)))
        p (.getParameterTypes m)]

    (alength p)))

(defn- primitive-array? [o]
  (.isArray (class o)))


(defn- immutable [b]
  (cond
   (or (instance? java.util.Map b)
       (map? b)) (into {} b)
       (or  (instance? java.util.List b)
            (primitive-array? b)
            (coll? b))
       (into [] b)
       :otherwise b))

(defn- mutable [b]
  (cond
   (or (instance? java.util.Map b)
       (map? b)) (doto (java.util.HashMap.) (.putAll b))
       (or  (instance? java.util.List b)
            (primitive-array? b)
            (coll? b)) (doto (java.util.ArrayList.) (.addAll b))
            :otherwise b))

(defn- groovy [s x cls]
  (let [mx (mutable x)]
    (case (.getMaximumNumberOfParameters cls)
      0 (.call cls)
      1 (.call cls mx)
      2 (.call cls [mx s]))))

(defn- merge-leafs [old new]
  (if (and (map? old) (map? new))
    (merge-with merge-leafs old new)
    new))

(defn- update-path
  ([s p v] (update-path s [] p v))
  ([s a [f & r] v]
     #_(println s a f r v (seq r) (map? (get-in s a)))
     (let [a' (conj a f)]
       (if  (seq r)
         (if (map? (get-in a s))
           (recur s a' r v)
           (assoc-in s a' (merge-leafs (get-in s a') (assoc-in {} r v))))
         (assoc-in s a' v)))))

(defn- transform [[p e]]
  (let [t (type e)]
    (fn [s]
      (cond
       (contains? (supers t) groovy.lang.Closure) (update-in s p (fn [c] (immutable (groovy s c e))))
       (and (fn? e) (= 1 (arg-count e))) (update-in s p e)
       (and (fn? e) (= 2 (arg-count e))) (update-in s p (partial e s))
       :else (update-path s p (immutable e))))))

(declare ddiff)

(defn map-diff-proc [path a b diffs]
  (reduce (fn [d ky]
            (ddiff (conj path ky) (get a ky) (get b ky) d)) diffs (keys b)))

(defn map-diff [path a b diffs]
  (let [a-keys (into #{} (keys a))
        b-keys (into #{} (keys b))
        del-keys (s/difference a-keys b-keys)
        new-in-b (select-keys b (s/difference b-keys a-keys))
        pa (select-keys a (s/intersection a-keys b-keys))
        pb (select-keys b (s/intersection a-keys b-keys))
        d1 (map-diff-proc path pa pb diffs)
        d2 (if (seq del-keys) (conj d1 (->Delta :del-keys path (into [] del-keys))) d1)
        d3 (if (seq new-in-b) (conj d2 (->Delta :merge path new-in-b)) d2)]
    d3))

(defn ddiff [path a b diffs]
  (cond
   (= a b) diffs
   (every? map? [a b]) (map-diff path a b diffs)
   :otherwise (conj diffs (->Delta :set path b))))

;; ==============  API  ====================

(defn compute-delta [os id cs]
  (let [d (if os [] [(->Delta :clear nil nil)])]
    [id (map-diff [] os cs d)]))

(defn compute-new-state [state txs]
  (schema/validate State state)
  (schema/validate Transactions txs)
  (let [transfunc (->> txs (map transform) reverse (apply comp))]
    (transfunc state)))

