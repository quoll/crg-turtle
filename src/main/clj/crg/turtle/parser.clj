(ns ^{:doc "A graph parser"
      :author "Paul Gearon"}
  crg.turtle.parser
  (:require [crg.turtle.nodes :as nodes])
  (:import [crg.turtle TtlLexer Parser]
           [java.io InputStream]
           [java.util UUID Map]
           [java.net URI]))

(defn as-uri [u]
  (if (keyword? u)
    (str (namespace u) \: (name u))
    (str \< u \>)))

(defrecord BlankNode [id]
  Object
  (toString [_] (str "_:" id)))

(defrecord Literal [^String lex t ^String lang]
  Object
  (toString [_] (if (contains? #{nil :xsd/string} t)
                  (if lang (str \' lex "'@" lang) (str \' lex \'))
                  (str \' lex "'^^" (as-uri t)))))

(defn typed-literal [^String lex t _] (Literal. lex t nil))
(defn string-literal [^String lex _ lang] (if lang (Literal. lex :xsd/string lang) lex))
(defn long-literal [^String lex t _] (try
                               (Long/parseLong lex)
                               (catch NumberFormatException _ (Literal. lex t nil))))
(defn double-literal [^String lex t _] (try
                                 (Double/parseDouble lex)
                                 (catch NumberFormatException _ (Literal. lex t nil))))
(defn boolean-literal [^String lex _ _] (= lex "TRUE"))

(def literal-fns
  {nil string-literal
   :xsd/string string-literal
   :xsd/boolean boolean-literal
   :xsd/integer long-literal
   :xsd/double double-literal
   :xsd/decimal double-literal})

(defn clojure-literal [^String lex t ^String lang] ((literal-fns t typed-literal) lex t lang))

(def cntr (atom 0))

(defn blank-node
  ([] (BlankNode. (swap! cntr inc) #_(UUID/randomUUID)))
  ([^String id] (BlankNode. (subs id 2))))

(defn iri [i] (URI. i))
(defn based-iri [^String i ^String b]
  (let [^URI u (URI. i)]
    (if (.isAbsolute u) u (keyword i))))
(defn full-iri [^Map pm ^String b ^String p ^String l] (if p (if (empty? p) (keyword l) (keyword p l)) (based-iri l b)))

(defn triples-seq
  [^crg.turtle.Parser parser ^crg.turtle.TtlLexer lexer]
  (when-let [triples (.parse parser lexer)]
    (concat triples (lazy-seq (triples-seq parser lexer)))))

(defprotocol TParser
  (get-triples [p])
  (get-prefix-map [p])
  (get-base [p]))

(defrecord TtlParser [^crg.turtle.Parser parser ^crg.turtle.TtlLexer lexer]
  TParser
  (get-triples [_] (triples-seq parser lexer))
  (get-prefix-map [_] (into {} (.getPrefixMap parser)))
  (get-base [_] (.getBase parser)))

(defn create-parser
  "Create a parser for a stream"
  [^java.io.InputStream in]
  (let [lexer (TtlLexer/newLexer in)
        parser (Parser.
                 (reify nodes/NodeBuilder
                   (new-blank [_] (blank-node))
                   (new-blank [_ id] (blank-node id))
                   (new-iri [_ i] (iri i))
                   (new-iri [_ i b] (based-iri i b))
                   (new-iri [_ pm b p l] (full-iri pm b p l))
                   (new-literal [_ lex t lang] (clojure-literal lex t lang))))]
    (->TtlParser parser lexer)))

