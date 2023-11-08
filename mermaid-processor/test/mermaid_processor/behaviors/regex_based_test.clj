(ns mermaid-processor.behaviors.regex-based-test
  (:require [clojure.test :refer :all]
            [mermaid-processor.behaviors.regex_based :as behavior]
            [mermaid-processor.parse :as parse]
            [mermaid-processor.behaviors.libraries.core :as core]))

(deftest simple-action-test
  (testing "setting a field"
    (let [behaviors (behavior/build 
                   {:core core/actions}  
                   {:nodes {"A" {:node-text "Score -10.2" :routes '()}}}
                   [{:regex #"(?i)score\s(-?\d+(\.\d+)?)" :action [:core :set-field "score" :number :%1]}])
          result ((behaviors "Score -10.2") {})]
      (is (= -10.2 (((result :context) :fields) "score"))))))

(deftest simple-condition-test
  (testing "Test a simple condition"
    (let [behaviors
          (behavior/build
           {:core core/actions}
           {:nodes {"A" {:node-text "score > 5" :routes '({:route-destination "B"
                                                           :route-text "score > 5"})}}}
           [{:regex #"(?i)score\s*(>|<|>=|<=|==|!=)\s*([\d-]+(?:\.\d+)?)"
             :action [:core :compare :score :%1 :%2]}])
          result ((behaviors "score > 5") {:fields {:score 10}})]
      (is (result :result)))))

(deftest action-missing-test
  (testing "valdate an action isnt present"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A[Score 10]")
          ex (try
               (behavior/build {} chart {})
               (catch clojure.lang.ExceptionInfo e e))]
      (is (= ["Score 10"]
             ((ex-data ex) :missing))))))

(deftest condition-missing-test
  (testing "valdate a condition isnt present"
    (let [chart (parse/parse-mermaid "flowchart TD
                                      A-->|MyTest|B")
          ex (try
               (behavior/build {} chart {})
               (catch clojure.lang.ExceptionInfo e e))]
      (is (= ((set ["A" "B" "MyTest"])
             (set ((ex-data ex) :missing))))))))