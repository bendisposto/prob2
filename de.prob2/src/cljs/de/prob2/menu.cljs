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

(defn menu-data []
  [
   (menu
    :prob
    [(item :about-prob)
     separator
     (item :preferences {:key ","})
     separator
     (item :hide-prob {:key "h" :selector "hide:"})
     (item :hide-others {:key "h" :modifiers "cmd-alt" :selector "hideOtherApplications"})
     separator
     (item :quit-prob {:key "q" :selector "quit:"})]
    {:mac :only})
   (menu
    :file-menu
    [(item :open-file {:key "o"})
     (item :reload {:context :animation :key "r"})])
   (menu
    :edit-menu
    [(item :undo {:selector "undo:" :key "z"})
     (item :redo {:selector "redo:" :key "z" :modifiers "cmd-shift"})
     separator
     (item :cut  {:selector "cut:" :key "x"})
     (item :copy {:selector "copy:" :key "c"})
     (item :paste {:selector "paste:" :key "v"})
     (item :select-all {:selector "selectAll:" :key "a"})
     ]
    )
   (menu
    :view-menu
    [(item :modeline {:key " " :modifiers "ctrl"})])
   (menu
    :window-menu
    [])
   (menu
    :help-menu
    [(item :about-prob {:mac :exclude})
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
    (let [action (get opts :action ::missing-action)
          opts (if (:label opts)
                 opts
                 (assoc opts :label (i18n action)))
          opts (assoc opts :click (fn [] (rf/dispatch [action])))]
      opts))

(declare submenu)

(defn menu-item [opts]
    #_(logp :entry opts)
    (let [mi (.-MenuItem nw/gui)
          opts (mac-specialcase opts)
          opts (expand-action opts)
          opts (if-not (:submenu opts)
                 opts
                 (assoc opts :submenu (submenu (:submenu opts))))]
      (when opts
        (-> opts (assoc :element mi) clj->js (mi.)))))


(defn submenu [items]
  (let [menu (create-menu)]
      (doseq [i items
              :when i]
        (.append menu (menu-item i)))
      menu))

(defn mk-menu [menu-instance items]
  (doseq [i items
          :when i]
    (.append menu-instance (menu-item i)))
  menu-instance)

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


(comment

  (defn create-menu-abstraction [data]
    (for [e data]
      (let [label (:label e)
            element (:element e)
            menu? (:submenu e)]
        [label (merge {:label label :element element} (when menu? {:submenu (into {} (map create-menu-abstaction (:submenu e)))}))])
      ))


  )
