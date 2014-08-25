(defproject de.prob2 "0.1.0-SNAPSHOT"
  :description "ProB 2.0"
  :url "https://github.com/bendisposto/prob2"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]

  :repositories [["cobra" "http://cobra.cs.uni-duesseldorf.de/artifactory/repo"]]

  :plugins [[lein-haml-sass "0.2.7-SNAPSHOT"]
            [com.keminglabs/cljx "0.4.0"]]

  :cljx {:builds [{:source-paths ["src/cljx"]
                   :output-path "src/clojure"
                   :rules :clj}

                  {:source-paths ["src/cljx"]
                   :output-path "src/clojurescript"
                   :rules :cljs}]}

  ;:hooks [cljx.hooks]

  :jvm-opts ["-Dapple.awt.UIElement=true" 
             "-XX:+TieredCompilation" 
             "-XX:TieredStopAtLevel=1" 
             "-Xverify:none"]

  :scss {:src "resources/private/scss"
         :output-directory "resources/public/gui/css"
         :output-extension "css"
         }

  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]

  :profiles {:dev { :plugins [[lein-expand-resource-paths "0.0.1"]]

                    :dependencies [[org.clojure/clojure "1.6.0"]
                                  [org.clojure/test.check "0.5.7"]
                                  [midje "1.6.3"]
                                  ]
                    :resource-paths ["kernel/build/libs/*.jar"]
                 }
             :production {

                          :dependencies [[org.clojure/clojure "1.6.0"]
                                         [com.google.guava/guava "14.0.1"]
                                         [org.codehaus.groovy/groovy-all "2.3.0"]
                                         ]
                          }

             :clj {:source-paths ["src/clojure"]
                   :test-paths ["test/clojure"]
                   :dependencies [[liberator "0.10.0"]
                                  [compojure "1.1.8"]
                                  [ring/ring-core "1.3.0"]
                                        ;[ring/ring-jetty-adapter "1.3.0"]
                                  [org.signaut/ring-jetty8-adapter "1.1.6"]
                                  [prismatic/schema "0.2.4"]
                                  [com.cognitect/transit-clj "0.8.229"]
                                  [org.clojure/clojure "1.6.0"]

                                  ]

                   :main de.prob2

                   }

             :cljs {:source-paths ["src/clojurescript"]
                    :test-paths ["test/clojurescript"]
                    :dependencies [[org.clojure/clojurescript "0.0-2173"]
                                   [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                                   [com.cognitect/transit-cljs "0.8.137"]
                                   [om "0.6.4"]]
                    :plugins [[lein-cljsbuild "1.0.2"]]
                    :cljsbuild {
                                :builds [{:id "release"
                                          :source-paths ["src/clojurescript"]
                                          :compiler {
                                                     :output-to "resources/public/gui/js/prob_ui.js"
                                                     :optimizations :simple
                                                     :pretty-print true
                                                     :preamble ["react/react.min.js"]
                                                     :externs ["react/externs/react.js"]}}
                                         ]}}}


  :aliases {  "server" ["with-profile","+clj,+dev","repl" ":start" ":port" "6000"]
              "client" ["with-profile","+cljs", "cljsbuild", "auto"]
              "autotest" ["with-profile","+clj", "midje", ":autotest"]
              })
