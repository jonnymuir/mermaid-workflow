(ns mermaid-processor.process-test
  (:require [clojure.test :refer :all]
            [mermaid-processor.parse :as parse]))

(deftest simple-process-test
  (testing "simplest single node test"
    (let [parsed (parse/parse-mermaid "flowchart TD
                                    A")]
      (println parsed)
      (is (= [[:node [:nodeId "A"]]]
           parsed)))))
