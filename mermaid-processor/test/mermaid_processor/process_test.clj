(ns mermaid-processor.process-test
  (:require [clojure.test :refer :all]
            [mermaid-processor.parse :as parse]
            [mermaid-processor.process :as process]
            [mermaid-processor.behavior :as behavior]))

(deftest process-single-node-test
  (testing "process a single node test"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A[Score 10]")
          behaviors (behavior/build {"Score 10" (fn [context]
                                                  (assoc context :score 10))}
                                    {}) 
          result-context (process/process-chart {} behaviors chart)]
    (is (= 10 (result-context :score))))))

(deftest process-single-node-test-unknown-function
  (testing "process a single node where test command doesn't exist"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Action not found: .*"
         (process/process-chart {} 
                                (behavior/build {"Score 10" (fn [context] (assoc context :score 10))}
                                                       {}) 
                                (parse/parse-mermaid "flowchart TD
                                                      A[Blah 10]"))))))
(deftest process-two-node-test
  (testing "process a two node test"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A[Score 10]-->B[Score 20]")
          behaviors (behavior/build 
                    {"Score 10" (fn [context]
                                 (update context :score #(-> % (or 0) (+ 10))))
                    "Score 20" (fn [context]
                                 (update context :score #(-> % (or 0) (+ 20))))}
                     {})
          result-context (process/process-chart {} behaviors chart)]
      (is (= 30 (result-context :score))))))

(deftest process-multiple-routes-goes-down-first
  (testing "If multiple available routes the first one in the chart should be chosen"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A-->B
                                      A-->C")
          behaviors (behavior/build (fn [action-type] (fn [context] context)) {})
           result-context (process/process-chart {} behaviors chart)]
      (is (= "B" ((behaviors :get-current-node-id) result-context chart))))))

(deftest process-two-node-test-with-conditions-first-route
  (testing "process a single node test with conditions goes down first route"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A[Score 10]-->|Test Score is 10|B
                                      A-->C")
          behaviors (behavior/build (fn [action-type] 
                      (case action-type
                        "Score 10" (fn [context]
                             (update context :score #(-> % (or 0) (+ 10))))
                        (fn [context] context))) 
                    {"Test Score is 10" (fn [context] (= 10 (context :score)))})
          result-context (process/process-chart {} behaviors chart)]
      (is (= "B" ((behaviors :get-current-node-id) result-context chart))))))

(deftest process-two-node-test-with-conditions-second-route
  (testing "process a single node test with conditions goes down second route if first condition false"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A[Score 10]-->|Test Score is 20|B
                                      A-->|Test Score is 10|C")
          behaviors (behavior/build (fn [action-type]
                                 (case action-type
                                   "Score 10" (fn [context]
                                                (update context :score #(-> % (or 0) (+ 10))))
                                   (fn [context] context)))
                      {"Test Score is 10" (fn [context] (= 10 (context :score)))
                       "Test Score is 20" (fn [context] (= 20 (context :score)))})
           result-context (process/process-chart {} behaviors chart)]
      (is (= "C" ((behaviors :get-current-node-id) result-context chart))))))

(deftest process-two-node-test-with-conditions-no-route
  (testing "process a single node test with conditions goes down no routes if all conditions false"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A[Score 10]-->|Test Score is 20|B
                                      A-->|Test Score is 20|C")
          behaviors (behavior/build (fn [action-type]
                                (case action-type
                                  "Score 10" (fn [context]
                                               (update context :score #(-> % (or 0) (+ 10))))
                                  (fn [context] context)))
                     {"Test Score is 10" (fn [context] (= 10 (context :score)))
                      "Test Score is 20" (fn [context] (= 20 (context :score)))})
          result-context (process/process-chart {} behaviors chart)]
      (is (= "A" ((behaviors :get-current-node-id) result-context chart))))))