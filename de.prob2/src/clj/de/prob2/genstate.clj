(ns de.prob2.genstate
    (:require [clojure.test.check.generators :as gen]
              [de.prob2.generated.schema :as schema]
              [schema-gen.core :as s]))

(defn gen-comp [] (s/schema->gen schema/Component-Spec))

(defn gen-map-for-keys [keys generator]
    (let [gens (repeatedly (count keys) (constantly generator))]
        (apply gen/hash-map (interleave keys gens))))

(defn components [comp-names]
    (gen-map-for-keys comp-names (gen-comp)))

(defn create-edge [c comp-names]
    (gen/hash-map :from (gen/return c) :to (gen/elements comp-names)
                          :type (gen/elements [:sees :uses :refines :imports :extends])))

(defn dep-graph [comp-names]
    (apply gen/tuple (map (fn [e] (create-edge e comp-names)) comp-names)))

(defn model-spec [comp-names]
    (gen/hash-map :dir gen/string
                  :main-component-name (gen/elements comp-names)
                  :filename gen/string
                  :type (gen/elements [:eventb :b :csp])
                  :dependency-graph (dep-graph comp-names)
                  :components (components comp-names)))

(defn gen-string-names []
    (gen/such-that not-empty (gen/vector (gen/not-empty gen/string))))

(defn model-gen []
    (gen/bind (gen-string-names)
              model-spec))

(defn extract-formulas [model]
    (let [comp ((:components model) (:main-component-name model))
          vars (get comp :variables [])
          constants (get comp :constants [])
          sets (get comp :sets [])]
          (map :formula-id (concat vars constants sets))))

(defn gen-results [result-ids]
    (let [gens (repeatedly (count result-ids) (constantly gen/string))]
        (apply gen/hash-map (interleave result-ids gens))))

(defn state-spec [formula-ids result-ids model-id state-id]
    (gen/hash-map :values (gen-map-for-keys formula-ids 
                                            (gen/elements result-ids))
                  :initialized? gen/boolean
                  :inv-ok? gen/boolean
                  :timeout? gen/boolean
                  :max-transitions-reached? gen/boolean
                  :id (gen/return {:model model-id :state state-id})
                  :state-errors (gen/vector (gen/hash-map :event gen/string
                                                :short-desc gen/string
                                                :long-desc gen/string))
                  :events-with-timeout (gen/vector gen/string)))

(defn gen-states [model-id state-ids formula-ids result-ids]
    (let [ids (map (fn [sid] {model-id sid}) state-ids)
          gens (map (fn [sid] (state-spec formula-ids result-ids model-id sid))
                              state-ids)]
          (apply gen/hash-map (interleave ids gens))))

(defn animator-spec [model-id state-ids]
    (gen/hash-map model-id (gen/elements state-ids)))

(defn transition-spec [model-id state-ids]
    (gen/hash-map :id gen/string
                  :name gen/string
                  :parameters (gen/vector gen/string)
                  :return-values (gen/vector gen/string)
                  :src (animator-spec model-id state-ids)
                  :dest (animator-spec model-id state-ids)))

(defn gen-uuid []
    (gen/return (java.util.UUID/randomUUID)))

(defn trace-spec [[model-id transitions out-transitions index]]
    (let [current-trans (get transitions index nil)
          current-state (if current-trans (:dest current-trans) 
                                          (:src (first out-transitions)))]
          (gen/hash-map :trace-id (gen-uuid)
                  :transitions (gen/return transitions)
                  :out-transitions (gen/return out-transitions)
                  :current-index (gen/return index)
                  :current-state (gen/return current-state)
                  :current-transition (gen/return current-trans)
                  :back? (gen/return (some? current-trans))
                  :forward? (gen/return (< index (- (count transitions) 1)))
                  :model (gen/return model-id))))

(defn extract-index [[model-id transitions out-transitions]]
    (gen/tuple (gen/return model-id)
               (gen/return transitions)
               (gen/return out-transitions)
               (gen/choose -1 (- (count transitions) 1))))

(defn extract-transitions [model-id state-ids]
    (gen/tuple (gen/return model-id)
               (gen/vector (transition-spec model-id state-ids))
               (gen/not-empty (gen/vector (transition-spec model-id state-ids)))))

(defn trace-gen [model-id state-ids]
    (gen/bind (gen/bind (extract-transitions model-id state-ids) extract-index) trace-spec))

(defn traces-to-map [traces]
    (let [tuples (map (fn [t] [(:trace-id t) t]) traces)]
        (gen/return (into {} tuples))))

(defn gen-traces [model-id state-ids]
    (gen/bind (gen/vector (trace-gen model-id state-ids)) traces-to-map))

(defn create-ui-state [[model model-id state-ids result-ids]]
    (let [formulas (extract-formulas model)]
        (gen/hash-map 
            :traces  (gen-traces model-id state-ids)
            :models  (gen/return {model-id model})
            :states  (gen-states model-id state-ids formulas result-ids)
            :results (gen-results result-ids))))

(defn gen-ui-state []
    (gen/bind (gen/tuple (model-gen) gen/string (gen-string-names) (gen/not-empty (gen/vector gen/int)))
        create-ui-state))