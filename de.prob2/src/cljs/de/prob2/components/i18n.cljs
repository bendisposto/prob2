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
  (let [fs (js/require "fs")]
    (.readdir
     fs "./i18n"
     (fn [err content]
       (logp :reset)
       (reset! messages
               (into {}
                     (for [c (array-seq content)]
                       (do  (logp :loadlanguage c)
                            {(keyword (second (re-find #"(.*)\.lang" c)))
                             (read-language fs (str "./i18n/" c))}))))
       (logp :lang @messages)))))

(init-messages)

(defn set-language [l]
  (reset! language l))

(defn i18n [key]
  (get-in @messages [@language key] "missing message"))
