(ns mermaid-processor.behaviors.libraries.core-test
  (:require [clojure.test :refer :all]
            [mermaid-processor.behaviors.libraries.core :as core]
            [mermaid-processor.behaviors.utils :as utils]))

(deftest set-number-test
  (testing "set a number test"
    (let [action ((core/actions :set-number) "my-field" "10")
          {context :context} (action {})]
      (prn context)
      (is (= 10.0 (utils/get-field-value context "my-field"))))))

