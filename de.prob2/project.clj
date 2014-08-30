(defproject de.prob2 "0.1.0-SNAPSHOT"
  :description "ProB 2.0"
  :url "https://github.com/bendisposto/prob2"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-alpha1"]
                 [com.taoensso/sente "0.15.1"]]

  :repositories [["cobra" "http://cobra.cs.uni-duesseldorf.de/artifactory/repo"]]

  :plugins [[lein-haml-sass "0.2.7-SNAPSHOT"]
            [com.keminglabs/cljx "0.4.0"]]

  :cljx {:builds [{:source-paths ["src/both"]
                   :output-path "src/server"
                   :rules :clj}

                  {:source-paths ["src/both"]
                   :output-path "src/client"
                   :rules :cljs}]}

  :hooks [cljx.hooks]

  :jvm-opts ["-Dapple.awt.UIElement=true"
             "-XX:+TieredCompilation"
             "-XX:TieredStopAtLevel=1"
             "-Xverify:none"]

  :scss {:src "resources/private/scss"
         :output-directory "resources/public/gui/css_generated"
         :output-extension "css"
         }

  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]

  :profiles
  {:dev
   {:plugins [[lein-expand-resource-paths "0.0.1"]]

    :dependencies [[org.clojure/test.check "0.5.7"]
                   [midje "1.6.3"]]
    :resource-paths ["dev" "kernel/build/libs/*.jar"]}

   :production
   {:dependencies [[de.prob2/de.prob2.kernel "2.0.0-milestone-16-SNAPSHOT"]]
    :main de.prob2}

   :clj
   {:source-paths ["src/server"]
    :test-paths ["test/server"]
    :dependencies [[liberator "0.10.0"]
                   [com.stuartsierra/component "0.2.1"]
                   [compojure "1.1.8"]
                   [ring/ring-core "1.3.0"]
                   [http-kit "2.1.18"]
                   [prismatic/schema "0.2.4"]
                   [com.cognitect/transit-clj "0.8.247"]]}

   :cljs
   {:source-paths ["src/client"]
    :test-paths ["test/client"]
    :dependencies [[org.clojure/clojurescript "0.0-2311"]
                   [org.clojure/core.async "0.1.278.0-76b25b-alpha"]
                   [com.cognitect/transit-cljs "0.8.184"]
                   [om "0.6.4"]]
    :plugins [[lein-cljsbuild "1.0.3"]]
    :cljsbuild
    {:builds [{:id "release"
               :source-paths ["src/client"]
               :compiler {
                          :output-to "resources/public/gui/js_generated/prob_ui.js"
                          :optimizations :simple
                          :pretty-print true
                          :preamble ["react/react.min.js"]
                          :externs ["react/externs/react.js"]}}]}}}


  :aliases {  "server" ["with-profile","+clj,+dev","repl" ":start" ":port" "6000"]
              "client" ["with-profile","+cljs,-dev", "cljsbuild", "auto"]
              "autotest" ["with-profile","+clj", "midje", ":autotest"]
              "crosscompile" ["cljx", "auto"]})
