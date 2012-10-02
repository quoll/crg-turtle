(ns crg.turtle.core
  (:require [crg.turtle.parser :as p])
  (:gen-class))

(defn -main
  "Load a file"
  [& args]
  (when-not (seq args)
    (println "Usage: turtle <ttl.file>")
    (System/exit 1))
  (println "Getting: " (first args))
  (doseq [t (p/load-turtle (first args))]
    (println t))
  (println "Done"))
