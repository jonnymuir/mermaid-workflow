(ns mermaid-processor.behaviors.regex_based_test
  (:require [clojure.test :refer :all]
            [mermaid-processor.behaviors.regex_based :as behavior]))

(deftest simple-action-test
  (testing "setting a field"
    (let [actions (behavior/build-actions 
                   {:nodes {"A" {:node-text "Score -10.2" :routes '()}}}
                   [{:regex #"(?i)score\s(-?\d+(\.\d+)?)" :action [:set-number :score :%1]}])
          result-context ((actions "Score -10.2") {})]
      (is (= -10.2 ((result-context :fields) :score))))))

#_(deftest simple-condition-test
  (testing "Test a simple condition"
    (let [conditions (behavior/build
                      [[#"(?i)score\s*(>|<|>=|<=|==|!=)\s*([\d-]+(?:\.\d+)?)"
                        [["test" "score" "%1" "%2"]]]])
          result ((conditions "score > 5") {:fields {:score 10}})]
      (is result))))