(ns ^{:doc "A graph parser"
      :author "Paul Gearon"}
  crg.turtle.parser
  (:import [crg.turtle TtlLexer Parser NodeBuilder]
           [crg.turtle.ast Types]
           [java.io InputStream]
           [java.util UUID]
           [java.net URI]))

(def xsd-string (URI. (str Types/XSD_NS "string")))
(def xsd-bool (URI. (str Types/XSD_NS Types/BOOLEAN)))
(def xsd-dec (URI. (str Types/XSD_NS Types/DECIMAL)))
(def xsd-dbl (URI. (str Types/XSD_NS Types/DOUBLE)))
(def xsd-int (URI. (str Types/XSD_NS Types/INTEGER)))

(defrecord BlankNode [id])
(defrecord Literal [lex t lang])

(defn blank-node
  ([] (BlankNode. (str "_:" (UUID/randomUUID))))
  ([id] (BlankNode. id)))

(defn iri [i] (URI. i))
(defn based-iri [i b] (let [u (URI. i)]
                        (if (.isRelative u) (keyword i) u)))
(defn full-iri [pm b p l] (if p (if (empty? p) (keyword l) (keyword p l)) (based-iri l b)))
(defn clojure-literal [lex t lang]
  (case t
    nil (if lang (with-meta lex lang) lex)
    (xsd-bool :xsd/boolean) (= lex Types/TRUE)
    (xsd-string :xsd/string) lex
    (xsd-int :xsd/integer)
      (try
        (Long/parseLong lex)
        (catch NumberFormatException _ (Literal. lex t lang)))
    (xsd-dbl :xsd/double xsd-dec :xsd/decimal)
      (try
        (Double/parseDouble lex)
        (catch NumberFormatException _ (Literal. lex t lang)))
    (Literal. lex t lang)))

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
                 (proxy [NodeBuilder] []
                   (newBlank ([] (blank-node)) ([id] (blank-node id)))
                   (newIri ([i] (iri i))
                           ([i b]  (based-iri i b))
                           ([pm b p l] (full-iri pm b p l)))
                   (newLiteral [lex t lang] (clojure-literal lex t lang))))]
    (->TtlParser parser lexer)))

