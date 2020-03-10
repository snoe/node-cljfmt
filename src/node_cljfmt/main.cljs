(ns node-cljfmt.main
  (:require
   [cljs.nodejs :as nodejs]
   [cljs.reader :as reader]
   [cljfmt.core :as cljfmt]
   [clojure.tools.cli :as cli]
   [clojure.string :as string]))

(nodejs/enable-util-print!)

(def fs (nodejs/require "fs"))

(def known-option? #{:edn})

(def default-options
  {:project-root "."
   :file-pattern #"\.clj[csx]?$"
   :ansi?        true
   :indentation? true
   :insert-missing-whitespace?      true
   :remove-surrounding-whitespace?  true
   :remove-trailing-whitespace?     true
   :remove-consecutive-blank-lines? true
   :indents   cljfmt/default-indents
   :alias-map {}})

(defn merge-default-options [options]
  (-> (merge default-options options)
      (assoc :indents (merge (:indents default-options)
                             (:indents options {})))))

(defn edn-reader [filename]
  (reader/read-string ((aget fs "readFileSync") filename "utf8")))

(defn stdin-read [opts]
  (let [stdin (aget js/process "stdin")
        content (atom "")]
    (.call (aget stdin "setEncoding") stdin "utf8")
    (.call (aget stdin "on") stdin "readable" (fn [] (if-let [s (.call (aget stdin "read") stdin)] (swap! content str s))))
    (.call (aget stdin "on") stdin "end" (fn []
                                           (try
                                             (let [formatted (cljfmt/reformat-string @content opts)]
                                               (.log js/console formatted)) s
                                             (catch :default e
                                               (.log js/console @content)
                                               (.error js/console e)))))))

(def ^:private cli-options
  [[nil "--edn EDN_PATH"
    :parse-fn edn-reader]])

(defn -main [& args]
  (let [parsed-opts (cli/parse-opts args cli-options)
        [cmd & paths] (:arguments parsed-opts)
        options (merge-default-options (get-in parsed-opts [:options :edn]))]
    (if-let [filename (first paths)]
      (let [file ((aget fs "readFileSync") filename "utf8")
            formatted (cljfmt/reformat-string file options)]
        ((aget fs "writeFileSync") filename formatted "utf8"))
      (stdin-read options))))

(set! *main-cli-fn* -main)
