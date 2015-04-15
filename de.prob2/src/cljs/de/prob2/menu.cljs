(ns de.prob2.menu
  (:require [taoensso.encore :as enc  :refer (logf log logp)]
            [de.prob2.nw :as nw]
            [re-frame.core :as rf]
            [de.prob2.i18n :refer [i18n]]))


(def order-store (atom 0))
(defn next-order []
  (swap! order-store inc))


(defn menu
  ([name entries] (menu name entries {}))
  ([name entries options]
   (merge {:label (i18n name) :submenu entries} options)))

(def separator {:type "separator"})

(defn item
  ([name] (item name {}))
  ([name options]
   (assoc options :label (i18n name) :action name)))

(defn listen [path callback]
  (let [state (rf/subscribe [:state-path path])]
    (run! (callback @state))))

(defn menu-data []
  [
   (menu
    :prob
    [(item :hide-prob {:key "h" :selector "hide:"})
     (item :hide-others {:key "h" :modifiers "cmd-alt" :selector "hideOtherApplications"})]
    {:mac :only})
   (menu
    :file-menu
    [(item :open-file)
     (item :reload {:context-fn (listen [:context] (fn [ctx] (= ctx :animation)))})
     (item :close-animation {:context #{:animation}})
     separator
     (item :quit)])
   (menu
    :edit-menu
    [(item :undo)
     (item :redo)
     separator
     (item :cut)
     (item :copy)
     (item :paste)
     (item :select-all)
     ])
   (menu
    :view-menu
    [(item :modeline)])
   (menu
    :window-menu
    [(item :preferences)])
   (menu
    :help-menu
    [(item :about-prob)
     (item :bugreport)])])




(defn create-menu
  ([] (create-menu nil))
  ([type]
   (let [m (.-Menu nw/gui)]
     (if type
       (m. (js-obj "type" type))
       (m.)))))

(defn mac-specialcase [opts]
  (let [this-os (nw/os-name)
        mac? (= "darwin" this-os)
        mac-options (:mac opts)]
    (when (or
           (and mac? (= mac-options :only))
           (and (not mac?) (= mac-options :exclude))
           (not mac-options))
      opts)))

(defn expand-action [opts]
  (when (and opts (not (= "separator" (get opts :type))))
    (let [xx (get opts :action ::missing-action)
          action (if (seq? xx) (first xx) xx)
          args (if (seq? xx) (rest xx) [])
          opts (if (:label opts)
                 opts
                 (assoc opts :label (i18n action)))
          opts (assoc opts :click (fn [] (rf/dispatch (into [action] args))))]
      opts)))

(declare submenu)

(defn menu-item [opts]
  #_(logp :entry opts)
  (let [mi (.-MenuItem nw/gui)
        opts (if-not (:submenu opts)
               opts
               (assoc opts :submenu (submenu (:submenu opts))))]
    #_(logp :mk opts)
    (when opts
      (-> opts (assoc :element mi) clj->js (mi.)))))


(defn submenu [items]
  (let [menu (create-menu)]
    (doseq [i (map (fn [e] (some-> e mac-specialcase expand-action)) items)
            :when i]
      (.append menu (menu-item i)))
    menu))


(defn set-menubar [menubar items]
  #_(logp items)
  (doseq [i items
          :when i]
    (when-let [el (menu-item i)]
      (.append menubar el)))
  (set! (-> nw/gui .-Window .get .-menu) menubar))

(rf/register-handler
 :populate-menus
 (fn [db _] (set-menubar (create-menu "menubar") (menu-data)) db))
