(ns crg.turtle.core
  (:use [clojure.java.io :only [input-stream]])
  (:require [crg.turtle.parser :as p])
  (:gen-class))

(defn -main
  "Load a file"
  [& args]
  (when-not (seq args)
    (println "Usage: turtle <ttl.file>")
    (System/exit 1))
  (println "Getting: " (first args))
  (with-open [f (input-stream (first args))]
    (let [t (p/create-parser f)]
      (println (p/get-triples t))))
  (println "Done"))
