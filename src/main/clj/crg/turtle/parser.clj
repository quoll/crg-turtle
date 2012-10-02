(ns ^{:doc "A graph parser"
      :author "Paul Gearon"}
  crg.turtle.parser
  (:use [clojure.java.io :only [input-stream]])
  (:import [crg.turtle TtlLexer Parser PrefixedTripleSink NodeBuilder]
           [java.io InputStream]
           [java.util UUID]
           [java.net URI]))


(def ^:dynamic *prefixes*)

(defrecord BlankNode [id])
(defrecord Literal [lex t lang])

(defn blank-node
  ([] (BlankNode. (str "_:" (UUID/randomUUID))))
  ([id] (BlankNode. id)))

(defn iri [i] (URI. i))
(defn based-iri [i b] (let [u (URI. i)]
                        (if (.isRelative u) (keyword i) u)))
(defn full-iri [pm b p l] (if p (if (empty? p) (keyword l) (keyword p l)) (based-iri l b)))

(defn parse-into
  "Parse a file into a graph"
  [^java.io.InputStream in g]
  (binding [*prefixes* {}]
    (let [graph (atom g)
          lexer (TtlLexer/newLexer in)
          parser (Parser.
                   (proxy [PrefixedTripleSink] []
                     (setPrefixMap [m] (set! *prefixes* m))
                     (triple [s p o] (swap! graph conj [s p o])))
                   (proxy [NodeBuilder] []
                     (newBlank ([] (blank-node)) ([id] (blank-node id)))
                     (newIri ([i] (iri i))
                             ([i b] (based-iri i b))
                             ([pm b p l] (full-iri pm b p l)))
                     (newLiteral [lex t lang] (Literal. lex t lang))))]
      (.parse parser lexer)
      @graph)))

(defn parse
  "Parses a stream into a new graph"
  [in]
  (parse-into in []))

(defn load-turtle-into
  [file graph]
  (with-open [f (input-stream file)]
    (parse-into f graph)))

(defn load-turtle
  [file]
  (with-open [f (input-stream file)]
    (parse f)))

