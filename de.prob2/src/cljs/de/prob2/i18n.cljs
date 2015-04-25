(ns de.prob2.i18n
  (:require
   [de.prob2.nw :as nw]
   [goog.string :as gstring]
   [taoensso.encore :as enc  :refer (logf log logp)]))

(def language (atom :english))

(def messages (atom nil))

(defn read-language [file]
  (logp :language file)
  (let [c (nw/slurp file)
        dss (str "{ " c " }")
        data (nw/read-string dss)]
    data))

(defn init-messages []
  (let [fs (js/require "fs")
        files (array-seq (.readdirSync fs "./i18n"))
        languages (filter #(.endsWith % ".lang") files)
        ]
    (into {}
          (for [lang languages]
            {(keyword (second (re-find #"(.*)\.lang" lang)))
             (read-language (str "./i18n/" lang))}))))


(defn set-language [l]
  (reset! language l))

(defn i18n [key]
  (when-not @messages
    (logp :loading-languages)
    (reset! messages (init-messages))
    (logp :installed (keys @messages))
    #_(logp @messages))
  (let [msg (get-in @messages [@language key] "missing message")]
    (if (= "missing message" msg) (logp :missing-message key))
    msg))

(defn format [key & params]
  (apply gstring/format (i18n key) params))



