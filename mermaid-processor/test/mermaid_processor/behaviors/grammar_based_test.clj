(ns mermaid-processor.behaviors.grammar_based_test
  (:require [clojure.test :refer :all]
            [mermaid-processor.behaviors.grammar_based :as behavior]))

(deftest simple-action-test
  (testing "setting a field"
    (let [actions (behavior/build-actions 
"S ::= score-statement
<score-statement> ::= set-number
set-number ::= score <whitespace> number
score ::= 'Score'
<whitespace> ::= #'\\s+'
<number> ::= #'[+-]?\\d+(\\.\\d+)?'")
          result-context ((actions "Score -10.2") {})]
      (is (= -10.2 ((result-context :fields) :score))))))

(deftest simple-condition-test
  (testing "Test a simple condition"
    (let [conditions (behavior/build-conditions
                   "S ::= comparison-condition
<comparison-condition> ::= condition
<condition> ::= [<whitespace>] comparison [<whitespace>] ('or' [<whitespace>] comparison [<whitespace>])*
comparison ::= identifier [<whitespace>] comparison-operator [<whitespace>] number
<comparison-operator> ::= '<' | '>' | '=' | '<=' | '>=' | '!='
<identifier> ::= 'score'
<whitespace> ::= #'\\s+'
<number> ::= #'[+-]?\\d+(\\.\\d+)?'
")
          result ((conditions "score > 5") {:fields {:score 10}})]
      (is result))))