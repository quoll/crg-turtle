(ns crg.turtle.nodes)

(defprotocol NodeBuilder
  (new-blank [builder]
             [builder ^String id]
    "Creates a blank node")

  (new-iri [builder ^String iri]
           [builder ^String iri ^String base]
           [builder ^Map prefix-map ^String base ^String prefix ^String local]
    "Creates an IRI given a full IRI, or parts of one and a context")

  (new-literal [builder ^String lexical type ^String lang]
    "Creates a literal using a given lexical form, an IRI for the type (may be nil),
     and a language code (only valid if the type indicates xsd:string or nil)"))

