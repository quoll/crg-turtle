(ns ^{:doc "A graph parser"
      :author "Paul Gearon"}
  crg.turtle.parser
  (:require [crg.turtle.nodes :as nodes]
            [crg.turtle.qname :as qname])
  (:import [crg.turtle TtlLexer Parser]
           [java.io InputStream]
           [java.util UUID Map]
           [java.net URI]))

(def known-prefixes
  {"http://www.w3.org/2003/g/data-view#" "grddl"
   "http://www.w3.org/ns/ma-ont#" "ma"
   "http://www.w3.org/2002/07/owl#" "owl"
   "http://www.w3.org/ns/prov#" "prov"
   "http://www.w3.org/1999/02/22-rdf-syntax-ns#" "rdf"
   "http://www.w3.org/ns/rdfa#" "rdfa"
   "http://www.w3.org/2000/01/rdf-schema#" "rdfs"
   "http://www.w3.org/2007/rif#" "rif"
   "http://www.w3.org/ns/r2rml#" "rr"
   "http://www.w3.org/ns/sparql-service-description#" "sd"
   "http://www.w3.org/2004/02/skos/core#" "skos"
   "http://www.w3.org/2008/05/skos-xl#" "skosxl"
   "http://www.w3.org/2007/05/powder#" "wdr"
   "http://rdfs.org/ns/void#" "void"
   "http://www.w3.org/2007/05/powder-s#" "wdrs"
   "http://www.w3.org/1999/xhtml/vocab#" "xhv"
   "http://www.w3.org/XML/1998/namespace" "xml"
   "http://www.w3.org/2001/XMLSchema#" "xsd"})

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

(defn rget "Reverse get function" [m v] (some (fn [[k vv]] (when (= v vv) k)) m))

(def bcntr (atom 0))

(defn blank-node
  ([] (BlankNode. (swap! bcntr inc) #_(UUID/randomUUID)))
  ([^String id] (BlankNode. (subs id 2))))

(def pre-cntr (atom 0))

(defn generate-prefix
  [m p]
  (let [last-pos (dec (count p))]
    (if (= \: (.charAt p last-pos))
      (subs p 0 last-pos)
      (loop []
        (let [pre (str "ns" (swap! pre-cntr inc))]
          (if-not (m pre)
            pre
            (recur)))))))

;; each of the IRI functions returns an IRI representation and a new prefix map
;; prefix-generate accepts the current prefix map and the namespace that needs a prefix
(defn iri [prefix-map i ^clojure.lang.IFn prefix-generate]
  (let [[prefix-ns ln] (qname/split-iri i)]
    (if-let [pn (rget prefix-map prefix-ns)]
      [(keyword pn ln) prefix-map]
      (let [prefix-name (or (known-prefixes prefix-ns) (prefix-generate prefix-map prefix-ns))]
        [(keyword prefix-name ln) (assoc prefix-map prefix-name prefix-ns)]))))

(defn based-iri [^Map pm ^String i ^String b ^clojure.lang.IFn pfx-gen]
  (let [^URI u (URI. i)]
    (if (.isAbsolute u)
      (iri pm i pfx-gen)
      (let [kw (keyword i)]
        (if-let [n (namespace kw)] ;; keyword had a / character and was therefore namespaced
          [kw (assoc pm n (str n "/"))]
          [kw pm])))))

(defn full-iri [^Map pm ^String b ^String p ^String l ^clojure.lang.IFn pfx-gen]
  (if p [(if (empty? p) (keyword l) (keyword p l)) pm] (based-iri pm l b pfx-gen)))

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
  ([^java.io.InputStream in] (create-parser in {} generate-prefix))
  ([^java.io.InputStream in ^java.util.Map known-ns ^clojure.lang.IFn pfx-gen]
   (let [lexer (TtlLexer/newLexer in)
         parser (Parser.
                  (reify nodes/NodeBuilder
                    (new-blank [_] (blank-node))
                    (new-blank [_ id] (blank-node id))
                    (new-iri [_ pm i] (iri pm i pfx-gen))
                    (new-iri [_ pm i b] (based-iri pm i b pfx-gen))
                    (new-iri [_ pm b p l] (full-iri pm b p l pfx-gen))
                    (new-literal [_ lex t lang] (clojure-literal lex t lang)))
                  (into {} known-ns))]
     (->TtlParser parser lexer))))

