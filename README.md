# Turtle

A Turtle loading module for CRG (Clojure RDF Graphs). Meets the standard for the W3C Candidate Recomendation (19 Feb, 2013).

Specifically aimed at parsing for Clojure. To this end, XSD strings (which includes previously
"untyped" literals) are returned as strings, doubles and decimals are returned as doubles and
integers are returned as longs. All other datatypes get returned as a Literal record, which
contains the lexical form, the datatype, and the language code. As for normal RDF literals,
the language code is only available if the datatype is a string (no datatype implies a string).

Where possible, the parser with return keywords rather than URIs (IRIs are not explicitly handled).
If a URI is represented as a QName, then it will be converted to a keyword instead of a URI. This
is more convenient for most Clojure applications. Keywords can be converted back into URIs using
the prefix-map from the parser.


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

### Bugs

Probably lots.

### TODO

  * Provide an option to parse to URI instead of always keywords.
  * Provide option to convert URIs to QNames automatically (auto updating the prefixes)
  * Possibly extend Triple to look like a 3 element list. (Is this necessary?)

## License

Copyright © 2013 Paul Gearon

Distributed under the Eclipse Public License, the same as Clojure.
