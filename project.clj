;; build with:  lein with-profile precomp compile
(defproject org.clojars.quoll/turtle "0.1.3"
  :description "Turtle Parser for Clojure"
  :url "https://github.com/quoll/crg-turtle"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [net.sf.beaver/beaver-ant "0.9.9"]]

  :plugins [[lein-beaver "0.1.2-SNAPSHOT"]]

  :prep-tasks ["javac" "compile"]

  :source-paths ["src/main/clj"]
  :test-paths ["src/test/clj"]
  :java-source-paths ["src/main/java" "target/src"]

  :grammar-src-dir "src/main/grammar"
  :grammar-dest-dir "target/src/"

  :profiles { :precomp { :prep-tasks ^:replace ["beaver" "compile"]
                         :source-paths ["src/main/pre/clj"]
                         :aot [crg.turtle.nodes] } }

  :main crg.turtle.core)
