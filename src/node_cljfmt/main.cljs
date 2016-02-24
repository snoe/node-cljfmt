(ns node-cljfmt.main
  (:require
   [cljs.nodejs :as nodejs]
   [cljfmt.core :as cljfmt]))

(nodejs/enable-util-print!)

(def fs (nodejs/require "fs"))

(defn -main []
  (if-let [filename (nth (aget js/process "argv") 2)]
    (let [file ((aget fs "readFileSync") filename "utf8")
          formatted (cljfmt/reformat-string file)]
      ((aget fs "writeFileSync") filename formatted "utf8"))
    (js/console.error "Provide a filename")))

(set! *main-cli-fn* -main)
