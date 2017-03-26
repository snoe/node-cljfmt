(defproject node-cljfmt "0.4.0"
  :description "A node script that reformats a file to proper clojure formatting"
  :url "http://github.com/snoe/node-cljfmt"

  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [org.clojure/clojurescript "1.9.495"]
                 [cljfmt "0.5.6"]]

  :plugins [[lein-cljsbuild "1.1.5"]]

  :clean-targets ["target" "bin/cljfmt"]

  :cljsbuild {:builds [{:id "release"
                        :source-paths ["src"]
                        :compiler {:main node-cljfmt.main
                                   :asset-path "target"
                                   :verbose true
                                   :output-to "bin/cljfmt"
                                   :output-dir "target"
                                   :optimizations :advanced
                                   :target :nodejs}}]})
