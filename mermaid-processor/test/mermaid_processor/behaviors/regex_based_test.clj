(ns mermaid-processor.behaviors.regex_based_test
  (:require [clojure.test :refer :all]
            [mermaid-processor.behaviors.regex_based :as behavior]))

(deftest simple-action-test
  (testing "setting a field"
    (let [behaviors (behavior/build 
                   {:nodes {"A" {:node-text "Score -10.2" :routes '()}}}
                   [{:regex #"(?i)score\s(-?\d+(\.\d+)?)" :action [:set-number :score :%1]}])
          result ((behaviors "Score -10.2") {})]
      (is (= -10.2 (((result :context) :fields) :score))))))

(deftest simple-condition-test
  (testing "Test a simple condition"
    (let [behaviors
          (behavior/build
           {:nodes {"A" {:node-text "score > 5" :routes '({:route-destination "B"
                                :route-text "score > 5"})}}}
           [{:regex #"(?i)score\s*(>|<|>=|<=|==|!=)\s*([\d-]+(?:\.\d+)?)"
             :action [:compare :score :%1 :%2]}])
          result ((behaviors "score > 5") {:fields {:score 10}})]
      (is (result :result)))))