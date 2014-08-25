(ns prob-ui.synctest
  (:use midje.sweet prob-ui.schemas)
  (:require [prob-ui.sync :as sync]
            [prob-ui.syncclient :as sc]
            [prob-ui.state-store :as store]
            [cognitect.transit :as transit]

            [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer (defspec)]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [schema.core :as schema]))

(def base (gen/one-of [gen/int gen/boolean gen/string]))
(def skalar (gen/one-of [base (gen/vector base) (gen/return {})]))



(defn- keys-in
  "Returns all paths leading to leafs"
  [m]
  (if (map? m)
    (vec
     (mapcat (fn [[k v]]
               (let [sub (keys-in v)
                     nested (map #(into [k] %) (filter (comp not empty?) sub))]
                 (if (seq nested)
                   nested
                   [[k]])))
             m))
    []))

(defn- prefix [path]
  (for [c (range (count path))] (take c path)))

(defn- state-path [tree f]
  (gen/fmap
   rest
   (gen/such-that
    (fn [p] (= :state (first p)))
    (let [x (mapcat f (keys-in tree))]
      (if (not-empty x) (gen/elements x)
          (gen/return []))))))

(defn- full-path [tree] (state-path tree vector))

(defn- fresh-path [tree]
  (gen/fmap flatten
            (gen/tuple (state-path tree prefix)
                       (gen/such-that not-empty
                                      (gen/vector gen/keyword)))))

(defn gen-path [tree]  (gen/one-of [(fresh-path tree) (full-path tree)]))

(defn gen-state-helper [size]
  (if (= size 0)
    skalar
    (let [size' (quot size 8)]
      (gen/map
       gen/keyword
       (gen/frequency [[8 base]
                       [2 (gen/resize size' (gen/sized gen-state-helper))]])))))


(defn gen-state [size]
  (gen/bind
   (gen/such-that
    #(and (map? %) (not-empty %))
    (gen/resize size (gen/sized gen-state-helper)))
   (fn [s] (gen/return {:current 0 :state  s}))))

(defn gen-single-transaction [tree]
  (gen/tuple
   (if (empty? tree)
     (gen/return [])
     (gen/fmap vec (gen-path tree)))
   skalar))


(defn gen-transactions [tree vector-size]
  (gen/such-that
   not-empty
   (gen/resize
    vector-size
    (gen/sized
     (fn [size] (if (= size 0)
                 (gen-single-transaction tree)
                 (gen/vector (gen-single-transaction tree))))))))

;; Nicht stark genug! einer der pfade darf kein prefix des anderen sein

(defn- mk-unique [ts]
  (let [mps (map (fn [[p e]] (assoc-in {} p e)) ts)
        mp (reduce merge mps)
        ks (keys-in mp)]
    (mapv  (fn [p] [p (get-in mp p)]) ks)))


(defn gen-unique-transactions [t s]
  (gen/bind (gen-transactions t s)
            (fn [v] (gen/such-that not-empty (gen/return (mk-unique v))))))


(defn gen-state-and-transactions
  ([ss vs] (gen-state-and-transactions gen-transactions ss vs))
  ([gen-vec-fn state-size vector-size]
     (gen/bind
      (gen-state state-size)
      (fn [v] (gen/tuple
              (gen/return v)
              (gen-vec-fn v vector-size))))))

(def gen-state-and-unique-transactions
  (partial gen-state-and-transactions gen-unique-transactions))

(defn compute-counterexample-state [ce]
  (let [[s ts] (-> ce :shrunk :smallest first)]
    (prn s)
    (prn ts)
    (sync/compute-new-state s ts)))

(defn roundtrip-check [s s']
  (let [[id changes] (sync/compute-delta s s')]
    (sc/compute-new-state s id changes)))

(defn roundtrip-delta [s s']
  (let [as ()])
  (let [delta (store/delta s s')
        [id changes] (sc/read-transit delta)]
    (sc/compute-new-state s id changes)))


;;  ================  PROPERTIES ===================


(def roundtrip
  (prop/for-all [[s ts] (gen-state-and-transactions 10 10)]
                (let [s' {:current 1 :state (sync/compute-new-state s ts)}
                      [id changes] (sync/compute-delta s s')
                      s'' (sc/compute-new-state s id changes)]
                  (and (is (= s' s'') (str "input s" (pr-str s) "ts" (pr-str ts)))
                       (is (not (some (partial schema/check State) [s s' s''])))))))

(def roundtrip-delta
  (prop/for-all [[s ts] (gen-state-and-transactions 10 10)]
                (let [s' {:current 1 :state (sync/compute-new-state s ts)}
                      [id changes] (sc/read-transit (store/delta s s'))
                      s'' (sc/compute-new-state s id changes)]
                  (and (is (= s' s'') (str "input s" (pr-str s) "ts" (pr-str ts)))
                       (is (not (some (partial schema/check State) [s s' s''])))))))


(def all-txs-succeeded
  (prop/for-all
   [[s ts] (gen-state-and-unique-transactions 10 10)]
   (let [s' {:current 1 :state (sync/compute-new-state s ts)}]
     (and
      (is (not (some (partial schema/check State) [s s'])))
      (is (not (schema/check Transactions ts)))
      (some (fn [[p v]] (not= v (get-in s' (concat [:state] p)))) ts)))))


;;   ===============   TESTS    ====================

(fact "simple-del-with-delta"
  (let [s' {:current 12 :state {:a [1 2]}}]
    (roundtrip-delta {:current 1 :state {:a [1 2 3]}} s') => s'))

(fact "simple-del"
  (let [s' {:current 12 :state {:a [1 2]}}]
    (roundtrip-check {:current 1 :state {:a [1 2 3]}} s') => s'))

(fact "merge-nil1"
  (sync/compute-delta {:current 1 :state {:a [1 2 3]}}
                      {:current 12 :state {:b nil}}) => truthy)

(fact "merge-nil2"
  (sc/compute-new-state
   {:current 0 :state {:a [1 2 3]}}
   1 [(sync/->Delta :del-keys [] [:a])
      (sync/->Delta :merge [] {:b nil})]) => {:current 1 :state {:b nil}})


(fact "merge-nested1"
  (sync/compute-new-state
   {:current 0 :state {:blub 0}}  [[[:blub] 0]]) =>
   {:blub 0})

(fact "simple adding"
  (sync/compute-new-state
   {:current 0 :state {}} [[[:foo] 1]]) => {:foo 1})

(fact "simple delta"
  (sync/compute-delta
   {:current 1 :state {:foo 12}} {:current 2 :state {:foo 13}}) => [2 [(sync/->Delta :set [:foo] 13)]])


(fact "multiple transactions"
  (sync/compute-new-state
   {:current 0 :state {:a 1}} [[[:a] 2] [[:b :c] 22] [[:b :d] 12]]) => {:a 2 :b {:c 22 :d 12}})

;; Midje does not qork with the keyword
(defn qc? [r] (:result r))

(fact (tc/quick-check 1000 roundtrip) => qc?)
(fact (tc/quick-check 1000 roundtrip-delta) => qc?)
(fact (tc/quick-check 3000 all-txs-succeeded) => qc?)


(comment
  (defspec qs-roundtrip 1000 roundtrip)
  (defspec qs-roundtrip-delta 1000 roundtrip-delta)
  (defspec qs-multiple-transactions 1000 all-txs-succeeded))
