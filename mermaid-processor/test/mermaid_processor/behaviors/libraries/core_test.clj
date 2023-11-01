(ns mermaid-processor.behaviors.libraries.core-test
  (:require [clojure.test :refer :all]
            [mermaid-processor.behaviors.libraries.core :as core]
            [mermaid-processor.behaviors.utils :as utils]))

(deftest set-number-test
  (testing "set a number test"
    (let [action ((core/actions :set-number) "my-field" "10")
          {context :context} (action {})]
      (is (= 10.0 (utils/get-field-value context "my-field"))))))

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
          {result :result} (action (utils/set-field-value {} "my-field" 5))]
      (is (not result)))))

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
