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
        files (.readdirSync fs "./i18n")]
    (into {}
          (for [c (array-seq files)]
            {(keyword (second (re-find #"(.*)\.lang" c)))
             (read-language fs (str "./i18n/" c))}))))


(defn set-language [l]
  (reset! language l))

(defn i18n [key]
  (logp :resolbe key)
  (when-not @messages
    (logp :loading-languages)
    (reset! messages (init-messages)))
  (let [msg (get-in @messages [@language key] "missing message")]
(logp :resolved key :in @messages :yielding msg)
    msg))
