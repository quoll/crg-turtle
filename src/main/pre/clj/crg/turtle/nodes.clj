(ns crg.turtle.nodes
  (:import [java.util List Map]
           [beaver Symbol]))

(defprotocol NodeBuilder
  (new-blank [builder]
             [builder ^String id]
    "Creates a blank node")

  (new-iri [builder ^Map prefix-map ^String iri]
           [builder ^Map prefix-map ^String iri ^String base]
           [builder ^Map prefix-map ^String base ^String prefix ^String local]
    "Creates an IRI given a full IRI, or parts of one and a context.
     Returns the IRI/prefixMap as a tuple, since the prefixMap can be updated.")

  (new-literal [builder ^String lexical type ^String lang]
    "Creates a literal using a given lexical form, an IRI for the type (may be nil),
     and a language code (only valid if the type indicates xsd:string or nil)"))

(defrecord Literal [lexical type lang])

(defrecord Blank [id])

(defprotocol AstNode
  (^ISeq get-triples [n ^NodeBuilder builder]
    "Retrieves the triples associated with a node. This naturally recurses into the various
    structures associated with different node types.")

  (get-node [n ^NodeBuilder builder]
    "If this node represents more, then get the simple node that this structure is built around.")

  (add-as-object [n ^NodeBuilder builder s p]
    "Tells a node to add itself as the object of a triple, along with any
    associated triples described by the node.
    builder - the node builder to use, when blank nodes are needed.
    s - the subject of the triple to make this an object of.
    p - the predicate of the triple to make this an object of."))

(extend-protocol AstNode
  Object
  (get-triples [n _] [])
  (get-node [n _] n)
  (add-as-object [n _ s p] [[s p n]]))

(defrecord PredicateObjects [predicate ^List objects])

(defrecord PropertyListNode [node ^List pred-objs]
  AstNode

  (get-node [_ _] node)

  (get-triples [_ builder]
    (apply concat
           (for [^PredicateObjects po pred-objs, o (:objects po)]
             (let [p (:predicate po)]
               (add-as-object o builder node p)))))

  (add-as-object [n builder s p]
    (conj (get-triples n builder) [s p node])))


(defrecord CollectionNode [^List c head]
  AstNode
  (get-node [_ _] head)

  (add-as-object [cn builder s p]
    (conj (get-triples cn builder) [s p head]))

  (get-triples [_ builder]
    (when (seq c)
      (loop [cs c, lastn nil, triples []]
        (if-not (seq cs)
          (concat triples [[lastn :rdf/rest :rdf/nil]])
          (let [n (if lastn (new-blank builder) head)
                joiner (if lastn [[lastn :rdf/next n]] [])
                ^Symbol first-symbol (first cs)
                cn (.value first-symbol)]
            (recur
              (rest cs)
              n
              (concat triples (add-as-object cn builder n :rdf/first) joiner))))))))

(defn add-triples [builder t s pol]
  (apply concat
         (cons t
               (for [po pol, o (:objects po)]
                 (let [p (:predicate po)]
                   (add-as-object o builder s p))))))

(defprotocol Nodes
  (^CollectionNode new-collection-node [n ^NodeBuilder builder c] "creates a new CollectionNode")

  (^PropertyListNode new-property-list-node [n node ^List po-list] "Creates a new PropertyListNode")

  (^PredicateObjects new-predicate-objects [n pred ^List objs] "Creates a new PropertyObject record")
  
  (new-triple [n s p o] "Shortcut for building a triple")
  
  (subject-triples [n b sa pol] "Gets triples from s/pol")
  
  (blank-subject-triples [n b bl pol] "Gets triples from bl/pol"))
  
(defrecord NodeImpl []
  Nodes
  (new-collection-node [_ builder c]
    (if (seq c)
      (->CollectionNode c (new-blank builder))
      (->CollectionNode c :rdf/nil)))

  (new-property-list-node [_ node po-list] (->PropertyListNode node po-list))

  (new-predicate-objects [_ pred objs] (->PredicateObjects pred objs))
  
  (new-triple [_ s p o] [s p o])

  (subject-triples [_ bldr sa pol]
    (add-triples bldr (get-triples sa bldr) (get-node sa bldr) pol))

  (blank-subject-triples [_ bldr bl pol]
    (let [t (get-triples bl bldr)]
      (if (seq pol) (add-triples bldr t (get-node bl bldr) pol) t))))

