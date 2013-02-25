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

(deftest simple-test
  (testing "Simple instantiation of parser for basic parse operations."
    (let [s (ByteArrayInputStream. (.getBytes t "UTF-8"))
          parser (create-parser s)
          triple-stream (get-triples parser)
          base (get-base parser)
          prefix-map (get-prefix-map parser)]
      (is (count prefix-map) 0)
      (let [triples (into [] triple-stream)]
        (is (= (count triples) 6))
        (is (= (count prefix-map) 1))
        (is (empty? base))
        (is (= (.getSubject (first triples)) :a))
        (is (= (.getPredicate (first triples)) :b))
        (is (= (.getObject (first triples)) 1.0))
        (is (= (.getSubject (last triples)) :h))
        (is (= (.getPredicate (last triples)) :i))
        (is (= (.getObject (last triples)) "j"))
        (let [f (nth triples 4)]
          (is (= (.getSubject f) :e))
          (is (= (.getPredicate f) :g))
          (is (= (.getObject f) 3.0)))
        ))))
