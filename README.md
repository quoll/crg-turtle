# Turtle

A Turtle loading module for CRG (Clojure RDF Graphs). Meets the grammar for the W3C Proposed Recomendation (09 Jan, 2014).

Specifically aimed at parsing for Clojure. To this end, XSD strings (which includes previously
"untyped" literals) are returned as strings, doubles and decimals are returned as doubles and
integers are returned as longs. All other datatypes get returned as a Literal record, which
contains the lexical form, the datatype, and the language code. As for normal RDF literals,
the language code is only available if the datatype is a string (no datatype implies a string).

The parser now always returns keywords rather than IRIs.
If an IRI is represented as a QName, then it will be converted to a keyword instead of a IRI. If
present as a raw IRI, then it will be scanned for an appropriate prefix, and converted to a QName.
QNames are required for predicates anyway, so this protects against the use of IRIs in that position.
Clojure applications also work much more conveniently and efficiently with keywords. Keywords can be
converted back into IRIs using the prefix-map from the parser.

Note that it is possible for the parser to generate Keywords with a namespace and and empty name.
This is legal both as a QName and as a Keyword, though Clojure does not have a literal representation
for nameless Keywords.

It is not difficult to have the parser return IRIs when QNames were not specified (this was the original
mode of operation for the parser), so if this need arises then IRIs (or URIs) may  be included easily.
Keywords are used as these have a more efficient representation, are more compatible with Clojure, and
because there is no standard implementation of IRI available. Jena includes one, but this involves a lot
of validity checking that would slow down parsing. Also, this project is explicitly trying to avoid
third-party non-Clojure libraries (Beaver notwithstanding - it's here because it generates the fastest
parsers).

## Java and Clojure

While this project uses Beaver as the parser generator, the only Java code written for the project
is the bare minimum to glue Beaver to the Clojure code that does the work.

## Requirements

  * JVM 1.6 or greater
  * Clojure 1.5.1 or greater

These requirements are due to precompilation of the library. It can be compiled and run with earlier
versions of Java and Clojure, but the user will need to download the sources and build it themselves.

## Installation

Add the following dependency to a leiningen project.clj:
```clj
  [org.clojars.quoll/turtle "0.2.2"]
```

## Usage

The parser is in crg.turtle.parser.

Calling crg.turtle.parser/create-parser will return a parser object. An object is necessary here
because parsing is done lazily, and the prefixes are accumulated out of band but can be updated
at any point during the parsing operation.

To get a seq of triples from the parser, call crg.turtle.parser/get-triples. After the seq is
exhausted, the parser can be queried for the prefix map and the URI base with
crg.turtle.parser/get-prefix-map and crg.turtle.parser/get-base resepectively.

## Examples

```clj
(use '[clojure.java.io :only [input-stream]])
(use '[crg.turtle.parser])

(with-open [f (input-stream "simple.ttl")]
  (let [parser (create-parser f)]
   (doseq [triple (get-triples parser)]
    (println triple))))
```

### Building
This project interleaves Clojure and Java compilation steps. To make this happen correctly, lein has to to compile using two different profiles. Once that has been done, the jar can be built:

```bash
  lein with-profile precomp compile
  lein uberjar
```

### Bugs

Probably lots.

### TODO

  * Provide an option to parse to URI instead of always keywords.
  * Rework to use core.async. This will invert to be more like the original Java code, which is still much faster.

## Acknowledgements

Special thanks to [Daniel Spiewak](https://github.com/djspiewak "@djspiewak") for advice on parsing
(especially the trick of terminating JFlex early to convince Beaver to stream a result).

## License

Copyright © 2013 Paul Gearon

Distributed under the Eclipse Public License, the same as Clojure.
