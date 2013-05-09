(ns crg.turtle.core-test
  (:use clojure.test
        crg.turtle.parser)
  (:import [java.io ByteArrayInputStream]))

(def t "@prefix : <http://example.org#> .
       @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
       :a :b 1.0 .
       :c :d 1 , 2.
       :e :f 1.0e0 ; :g 3.0e0.
       :h :i \"j\".")

(defn get-subject [[s p o]] s)
(defn get-predicate [[s p o]] p)
(defn get-object [[s p o]] o)

(deftest simple-test
  (testing "Simple instantiation of parser for basic parse operations."
    (let [s (ByteArrayInputStream. (.getBytes t "UTF-8"))
          parser (create-parser s)
          triple-stream (get-triples parser)
          base (get-base parser)]
      (is (count (get-prefix-map parser)) 0)
      (let [triples (into [] triple-stream)]
        (is (= (count triples) 6))
        (is (= (count (get-prefix-map parser)) 2))
        (is (empty? base))
        (is (= (get-subject (first triples)) :a))
        (is (= (get-predicate (first triples)) :b))
        (is (= (get-object (first triples)) 1.0))
        (is (= (get-subject (last triples)) :h))
        (is (= (get-predicate (last triples)) :i))
        (is (= (get-object (last triples)) "j"))
        (let [f (nth triples 4)]
          (is (= (get-subject f) :e))
          (is (= (get-predicate f) :g))
          (is (= (get-object f) 3.0)))
        ))))

(def cpx "@base <http://ex.com/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix dc: <http://purl.org/dc/elements/1.1/> .
@prefix ex: <http://example.org/stuff/1.0/> .

<http://www.w3.org/TR/rdf-syntax-grammar>
  dc:title \"RDF/XML Syntax Specification (Revised)\" ;
  ex:editor [
    ex:fullname \"Dave Beckett\";
    ex:homePage <http://purl.org/net/dajobe/>;
    ex: dc:
  ] .

ex:collection :hasCollection ( :a :b :c ) .
[ a ex:Foo ; ex:some ex:data ] ex:friend [ a ex:Bar ; ex:contains ( \"some\" \"data\"@en ) ] .
( :a :b \"foo\" [ a ex:Type , ex:Other ] ( :aa ) ) ex:to ( [ :val 1 ] [ :val 2] [ :val 3] ) .

[ a ex:Foo ;;; ex:some ex:data ] ex:friend [ a ex:Bar ;; ex:contains ( \"some\" \"data\" ) ] .
ex:collection2 :hasCollection ( :a :b :c ) ; .
ex:collection3 :hasCollection ( :a :b :c ) ;;; .

@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

ex:numbers :hasCollection ( 1 2.0 3.0E0 ) .
")

(deftest structure-test
  (testing "Tests most triple forms available in Turtle."
    (let [s (ByteArrayInputStream. (.getBytes cpx "UTF-8"))
          parser (create-parser s)
          triple-stream (get-triples parser)
          base (get-base parser)]
      (is (count (get-prefix-map parser)) 0)
      (let [triples (into [] triple-stream)]
        (is (= (count triples) 75))
        (is (= (count (get-prefix-map parser)) 5)))))) ; the empty namespace defaults to base

