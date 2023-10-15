(ns mermaid-processor.parse-test
  (:require [clojure.test :refer :all]
            [mermaid-processor.parse :refer :all]))

(deftest parse-invalid-chart-test
  (testing "test a nonsense chart type throws an error"
    (is (thrown? IllegalArgumentException (parse-mermaid "nonsensechart TB"))))
)
(deftest parse-single-node
  (testing "simplest single node test"
    ; "A" should return a map which simply has one node id = A and text = A
    (is (= [[:node [:nodeId "A"]]]
         (parse-mermaid "flowchart TD
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
         (parse-mermaid "flowchart TD")))))

(deftest parse-single-node-with-description
  (testing "single node with a description test"
    (is (= [[:node [:nodeId "A"] [:nodeText "My Desc"]]]
           (parse-mermaid "flowchart TD
                                  A[My Desc]")))))
(deftest parse-multiple_nodes
  (testing "multiple nodes test"
    (is (= [[:node [:nodeId "A"] [:nodeText "My Desc"]]
            [:node [:nodeId "B"] [:nodeText "My other node"]]]
           (parse-mermaid "flowchart TD
                                  A[My Desc]
                                  B[My other node]")))))

(deftest parse-multiple_nodes2
  (testing "multiple nodes with newlines at the beginning"
    (is (= [[:node [:nodeId "A"] [:nodeText "My Desc"]]
            [:node [:nodeId "B"] [:nodeText "My other node"]]]
           (parse-mermaid "flowchart TD
                           

                                  A[My Desc]
                                  B[My other node]")))))

(deftest parse-multiple_nodes3
  (testing "multiple nodes with newlines at the end"
    (is (= [[:node [:nodeId "A"] [:nodeText "My Desc"]]
            [:node [:nodeId "B"] [:nodeText "My other node"]]]
           (parse-mermaid "flowchart TD
                                  A[My Desc]
                                  B[My other node]
                           
                           
                           ")))))

(deftest parse-multiple_nodes4
  (testing "multiple nodes with newlines in the middle"
    (is (= [[:node [:nodeId "A"] [:nodeText "My Desc"]]
            [:node [:nodeId "B"] [:nodeText "My other node"]]]
           (parse-mermaid "flowchart TD
                                  A[My Desc]
                           

                                  B[My other node]
                           ")))))

(deftest parse-multiple_nodes_and_routes
  (testing "multiple nodes test"
    (is (= [[:route [:node [:nodeId "A"] [:nodeText "My Desc"]]
            [:node [:nodeId "B"] [:nodeText "My other node"]]]]
           (parse-mermaid "flowchart TD
                                  A[My Desc]-->B[My other node]")))))

(deftest parse-multiple_nodes_and_routes_with_conditions
  (testing "multiple nodes test"
    (is (= [[:route [:node [:nodeId "A"] [:nodeText "My Desc"]] [:routeText "yes"]
             [:node [:nodeId "B"] [:nodeText "My other node"]]]]
           (parse-mermaid "flowchart TD
                                  A[My Desc]--> | yes | B[My other node]")))))
