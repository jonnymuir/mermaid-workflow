(ns mermaid-processor.validate-test
  (:require [clojure.test :refer :all]
            [mermaid-processor.parse :as parse]
            [mermaid-processor.validate :as validate]
            [mermaid-processor.behavior :as behavior]))

(deftest validate-action-missing
  (testing "valdate an action isnt present"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A[Score 10]")
          behavior (behavior/build {}
                                    {}) 
          result (validate/validate-chart-behaviors behavior chart)]
    (is (= [{:action "Score 10"}]  (result :missing-actions))))))

(deftest validate-action-present
  (testing "valdate an action is present"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A[Score 10]")
          behavior (behavior/build {"Score 10" (fn [context]
                                                 (assoc context :score 10))}
                                   {})
          result (validate/validate-chart-behaviors behavior chart)]
      (is (= []  (result :missing-actions))))))

(deftest validate-condition-missing
  (testing "valdate a condition isnt present"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A-->|MyTest|B")
          behavior (behavior/build (fn [action-type] (fn [context] context))
                                   {})
          result (validate/validate-chart-behaviors behavior chart)]
      (is (= [{:condition "MyTest"}]  (result :missing-conditions))))))

(deftest validate-condition-present
  (testing "valdate a condition is present"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A-->|MyTest|B")
          behavior (behavior/build (fn [action-type] (fn [context] context))
                                   {"MyTest" true})
          result (validate/validate-chart-behaviors behavior chart)]
      (is (= []  (result :missing-conditions))))))
