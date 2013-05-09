# Turtle

A Turtle loading module for CRG (Clojure RDF Graphs). Meets the grammar for the W3C Candidate Recomendation (19 Feb, 2013).

Specifically aimed at parsing for Clojure. To this end, XSD strings (which includes previously
"untyped" literals) are returned as strings, doubles and decimals are returned as doubles and
integers are returned as longs. All other datatypes get returned as a Literal record, which
contains the lexical form, the datatype, and the language code. As for normal RDF literals,
the language code is only available if the datatype is a string (no datatype implies a string).

Where the parser always returns keywords rather than IRIs.
If an IRI is represented as a QName, then it will be converted to a keyword instead of a IRI. If
present as a raw IRI, then it will be scanned for an appropriate prefix, and converted to a QName.
QNames are required for predicates anyway, so this protects against the use of IRIs in that position.
Clojure applications also work much more conveniently and efficiently with keywords. Keywords can be
converted back into IRIs using the prefix-map from the parser.

Note that it is possible for the parser to generate Keywords with a namespace and and empty name.
This is legal both as a QName and as a Keyword, though Clojure does not have a literal representation
for nameless Keywords.

## Java and Clojure

While this project uses Beaver as the parser generator, the only Java code written for the project
is the bare minimum to glue Beaver to the Clojure code that does the work.

## Installation

Add the following dependency to a leiningen project.clj:
```clj
  [org.clojars.quoll/turtle "0.1.0"]
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

  * Provide option to convert URIs to QNames automatically (done, but not tested)
  * Provide an option to parse to URI instead of always keywords.

## Acknowledgements

Special thanks to [Daniel Spiewak](https://github.com/djspiewak "@djspiewak") for advice on parsing
(especially the trick of terminating JFlex early to convince Beaver to stream a result).

## License

Copyright © 2013 Paul Gearon

Distributed under the Eclipse Public License, the same as Clojure.
