(ns de.prob2.i18n
  (:require [taoensso.encore :as enc  :refer (logf log logp)]))

(def language (atom :english))

(def messages (atom nil))


(defn read-language [fs file]
  (let [c (.readFileSync fs file "utf8")
        dss (str "{ " c " }")
        data (cljs.reader/read-string dss)]
    data))

(defn init-messages []
  (let [fs (js/require "fs")
        files (array-seq (.readdirSync fs "./i18n"))
        languages (filter #(.endsWith % ".lang") files)
        ]
    (into {}
          (for [lang languages]
            {(keyword (second (re-find #"(.*)\.lang" lang)))
             (read-language fs (str "./i18n/" lang))}))))


(defn set-language [l]
  (reset! language l))

(defn i18n [key]
  (when-not @messages
    (logp :loading-languages)
    (reset! messages (init-messages))
    (logp :installed (keys @messages)))
  (let [msg (get-in @messages [@language key] "missing message")]
    msg))
