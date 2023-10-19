(ns mermaid-processor.process-test
  (:require [clojure.test :refer :all]
            [mermaid-processor.parse :as parse]
            [mermaid-processor.process :as process]))

(deftest process-single-node-test
  (testing "process a single node test"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A[Score 10]")
          behaviors {:actions {"Score 10" (fn [context]
                                  (assoc context :score 10))}
                    :conditions {}}
          result (process/process-chart chart behaviors {})]
    (is (= 10 (result :score))))))

(deftest process-single-node-test-unknown-function
  (testing "process a single node where test command doesn't exist"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Action not found: .*"
                          (let [chart (parse/parse-mermaid "flowchart TD
                                      A[Blah 10]")
                                behaviors {:actions {"Score 10" (fn [context]
                                                                 (assoc context :score 10))}
                                          :conditions {}}
                                result (process/process-chart chart behaviors {})])))))

(deftest process-two-node-test
  (testing "process a two node test"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A[Score 10]-->B[Score 20]")
          behaviors {:actions 
                    {"Score 10" (fn [context]
                                 (update context :score #(-> % (or 0) (+ 10))))
                    "Score 20" (fn [context]
                                 (update context :score #(-> % (or 0) (+ 20))))}
                     :conditions {}}
          result (process/process-chart chart behaviors {})]
      (is (= 30 (result :score))))))

(deftest process-multiple-routes-goes-down-first
  (testing "If multiple available routes the first one in the chart should be chosen"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A-->B
                                      A-->C")
          behaviors {:actions (fn [action-type] (fn [context] context))
                     :conditions {}} 
          result (process/process-chart chart behaviors {})]
      (is (= "B" (result :current-node-id))))))

(deftest process-two-node-test-with-conditions-first-route
  (testing "process a single node test with conditions goes down first route"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A[Score 10]-->|Test Score is 10|B
                                      A-->C")
          behaviors {:actions (fn [action-type] 
                      (case action-type
                        "Score 10" (fn [context]
                             (update context :score #(-> % (or 0) (+ 10))))
                        (fn [context] context)))
                    :conditions 
                    {"Test Score is 10" (fn [context] (= 10 (context :score)))}}
          result (process/process-chart chart behaviors {})]
      (is (= "B" (result :current-node-id))))))

(deftest process-two-node-test-with-conditions-second-route
  (testing "process a single node test with conditions goes down second route if first condition false"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A[Score 10]-->|Test Score is 20|B
                                      A-->|Test Score is 10|C")
          behaviors {:actions (fn [action-type]
                                 (case action-type
                                   "Score 10" (fn [context]
                                                (update context :score #(-> % (or 0) (+ 10))))
                                   (fn [context] context)))
                      :conditions
                      {"Test Score is 10" (fn [context] (= 10 (context :score)))
                       "Test Score is 20" (fn [context] (= 20 (context :score)))}}
           result (process/process-chart chart behaviors {})]
      (is (= "C" (result :current-node-id))))))

#_(deftest process-two-node-test-with-conditions-no-route
  (testing "process a single node test with conditions goes down no routes if all conditions false"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A[Score 10]-->|Test Score is 20|B
                                      A-->|Test Score is 20|C")
          behaviors {:actions (fn [action-type]
                                (case action-type
                                  "Score 10" (fn [context]
                                               (update context :score #(-> % (or 0) (+ 10))))
                                  (fn [context] context)))
                     :conditions
                     {"Test Score is 10" (fn [context] (= 10 (context :score)))
                      "Test Score is 20" (fn [context] (= 20 (context :score)))}}
          result (process/process-chart chart behaviors {})]
      (is (= "A" (result :current-node-id))))))