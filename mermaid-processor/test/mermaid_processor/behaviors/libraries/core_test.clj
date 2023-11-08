(ns mermaid-processor.behaviors.libraries.core-test
  (:require [clojure.test :refer :all]
            [mermaid-processor.behaviors.libraries.core :as core]
            [mermaid-processor.behaviors.utils :as utils]
            [clojure.data.xml :as xml]))

(deftest set-number-test
  (testing "set a number test"
    (let [action ((core/actions :set-field) "my-field" :number "10")
          {context :context} (action {})]
      (is (= 10.0 (utils/get-field-value context "my-field"))))))

(deftest set-string-test
  (testing "set a string test"
    (let [action ((core/actions :set-field) "my-field" :string "moo")
          {context :context} (action {})]
      (is (= "moo" (utils/get-field-value context "my-field"))))))

(deftest set-xml-test
  (testing "set xml test"
    (let [xml-str "<moo>baa</moo>"
          action ((core/actions :set-field) "my-field" :xml xml-str)
          {context :context} (action {})
          expected (xml/parse-str xml-str)
          actual (utils/get-field-value context "my-field")]
      (is (= expected actual)))))

(deftest set-required-string-test
  (testing "set required string test"
    (let [action ((core/actions :set-field) "my-field" :string :required)
          {context :context} (action {})]
      (is (= :required (utils/get-field-value context "my-field"))))))

(deftest set-required-number-test
  (testing "set required number test"
    (let [action ((core/actions :set-field) "my-field" :number :required)
          {context :context} (action {})]
      (is (= :required (utils/get-field-value context "my-field"))))))


(deftest compare-test
  (testing "comparing a field positive test"
    (let [action ((core/actions :compare) "my-field" ">" "5")
          {result :result} (action (utils/set-field-value {} "my-field" 6))]
      (is result))))

(deftest compare-test-negative
  (testing "comparing a field negative test"
    (let [action ((core/actions :compare) "my-field" ">" "5")
          {result :result} (action (utils/set-field-value {} "my-field" 4))]
      (is (not result)))))

(deftest compare-test-negative2
  (testing "comparing a field negative test2"
    (let [action ((core/actions :compare) "my-field" ">" "5")
          {result :result} 
          (action 
           (utils/set-field-value {} "my-field" 5))]
      (is (not result)))))

(deftest compare-all-test
  (testing "comparing all field positive test"
    (let [action ((core/actions :compare) :all-fields ">" "5")
          {result :result} 
          (action 
           (utils/set-field-value 
            (utils/set-field-value {} "my-field1" 6) "my-field2" 7))]
      (is result))))

(deftest compare-all-test-negative
  (testing "comparing all field negative test"
    (let [action ((core/actions :compare) :all-fields ">" "5")
          {result :result} 
          (action 
           (utils/set-field-value 
            (utils/set-field-value {} "my-field1" 6) "my-field2" 4))]
      (is (not result)))))

(deftest compare-all-with-keyword-negative
  (testing "comparing all with keyword negative test"
    (let [action ((core/actions :compare) :all-fields "<>" :required)
          {result :result}
          (action
           (utils/set-field-value
            (utils/set-field-value {} "my-field1" 6) "my-field2" 7))]
      (is result))))

(deftest test-last-result-true-positive
  (testing "test last result true positive test"
    (let [action ((core/actions :last-result-is-true))
          {result :result} (action (utils/set-last-result {} true))]
      (is result))))

(deftest test-last-result-true-negative
  (testing "test last result true negative test"
    (let [action ((core/actions :last-result-is-true))
          {result :result} (action (utils/set-last-result {} false))]
      (is (not result)))))

(deftest test-last-result-not-true-positive
  (testing "test last result true positive test"
    (let [action ((core/actions :last-result-is-not-true))
          {result :result} (action (utils/set-last-result {} false))]
      (is result))))

(deftest test-last-result-not-true-negative
  (testing "test last result true negative test"
    (let [action ((core/actions :last-result-is-not-true))
          {result :result} (action (utils/set-last-result {} true))]
      (is (not result)))))

(deftest test-do-nothing
  (testing "test do nothing"
    (let [action ((core/actions :do-nothing))
          {context :context} (action {})]
      (is (= {} context)))))
