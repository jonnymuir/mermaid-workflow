(ns mermaid-processor.parse-test
  (:require [clojure.test :refer :all] 
            [mermaid-processor.parse :as parse])) 

(deftest parse-invalid-chart-test
  (testing "test a nonsense chart type throws an error"
    (is (thrown? IllegalArgumentException (parse/parse-mermaid "nonsensechart TB"))))
)
(deftest parse-single-node-test
  (testing "simplest single node test"
    ; "A" should return a map which simply has one node id = A and text = A
    (is (= {:start-at "A" :nodes {"A" {:node-text "A" :routes '()}}}
         (parse/parse-mermaid "flowchart TD
                                  A")) 
        )
    )
  )
(deftest parse-fail-test
  (testing "Parsing failure throws an exception"
    (is (thrown-with-msg?
         clojure.lang.ExceptionInfo
         #"Parsing failed"
         ; Pass in an empty chart - the parser will fail
         (parse/parse-mermaid "flowchart TD")))))

(deftest parse-single-node-with-description
  (testing "single node with a description test"
    (is (= {:start-at "A" :nodes {"A" {:node-text "My Desc" :routes '()}}}
           (parse/parse-mermaid "flowchart TD
                                  A[My Desc]")))))
(deftest parse-multiple_nodes
  (testing "multiple nodes test"
    (is (= {:start-at "A" :nodes {"A" {:node-text "My Desc" :routes '()}
                                  "B" {:node-text "My other node" :routes '()}}}
           (parse/parse-mermaid "flowchart TD
                                  A[My Desc]
                                  B[My other node]")))))

(deftest parse-multiple_nodes2
  (testing "multiple nodes with newlines at the beginning"
    (is (= {:start-at "A" :nodes {"A" {:node-text "My Desc" :routes '()}
                                  "B" {:node-text "My other node" :routes '()}}}
           (parse/parse-mermaid "flowchart TD
                           

                                  A[My Desc]
                                  B[My other node]")))))

(deftest parse-multiple_nodes3
  (testing "multiple nodes with newlines at the end"
    (is (= {:start-at "A" :nodes {"A" {:node-text "My Desc" :routes '()}
                                  "B" {:node-text "My other node" :routes '()}}}
           (parse/parse-mermaid "flowchart TD
                                  A[My Desc]
                                  B[My other node]
                           
                           
                           ")))))

(deftest parse-multiple_nodes4
  (testing "multiple nodes with newlines in the middle"
    (is (= {:start-at "A" :nodes {"A" {:node-text "My Desc" :routes '()}
                                  "B" {:node-text "My other node" :routes '()}}}
           (parse/parse-mermaid "flowchart TD
                                  A[My Desc]
                           

                                  B[My other node]
                           ")))))

(deftest parse-multiple_nodes_and_routes
  (testing "multiple nodes and routes test"
    (is (= {:start-at "A" :nodes {"A" {:node-text "My Desc" :routes '({:route-destination "B" :route-text nil})}
                                  "B" {:node-text "My other node" :routes '()}}}
           (parse/parse-mermaid "flowchart TD
                                  A[My Desc]-->B[My other node]")))))

(deftest parse-multiple_nodes_and_routes_with_conditions
  (testing "multiple nodes and routs with conditions test"
    (is (= {:start-at "A" :nodes {"A" {:node-text "My Desc" :routes '({:route-destination "B" :route-text "yes"})}
                                  "B" {:node-text "My other node" :routes '()}}}
           (parse/parse-mermaid "flowchart TD
                                  A[My Desc]--> | yes | B[My other node]")))))
