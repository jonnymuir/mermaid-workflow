(ns mermaid-processor.parse-test
  (:require [clojure.test :refer :all]
            [mermaid-processor.parse :refer :all]))

(deftest parse-test
  (testing "first word describes the chart type - test it creates an empty map"
    (is (= ( count (parse-mermaid "flowchart TB" )) 0))
  )
)
(deftest parse-test2
  (testing "test a nonsense chart type throws an error"
    (is (thrown? IllegalArgumentException (parse-mermaid "nonsensechart TB"))))
)