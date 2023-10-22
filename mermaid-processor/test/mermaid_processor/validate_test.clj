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

(deftest validate-complex
  (testing "valdate a more complex chart"
    (let [chart (parse/parse-mermaid "flowchart TD
Q1{Any Red Circles}
    Q1 --> |Yes| Q2
    Q1 --> |No| O3a
    Q2{Only blue circles?}
    Q2 --> |Yes| Q3
    Q2 --> |No| Q4
    Q3{Radius larger than 50?}
    Q3 --> |Yes| Q5
    Q3 --> |No | O3b
    Q4{Any rectangles or squares?}
    Q4 --> |Yes| Q6
    Q4 --> |No| Q7
    Q5{Radius larger than 100?}
    Q5 --> |Yes| O1c
    Q5 --> |No| O2c
    Q6{Any text?}
    Q6 --> |Yes| Q8
    Q6 --> |No | Q10
    Q7{Any text?}
    Q7 --> |Yes| Q9
    Q7 --> |No | O2d
    Q8{Rectangle green?}
    Q8 --> |Yes| O1e
    Q8 --> |No| Q10
    Q9{Text containing the sequence 'Ihasa'?}
    Q9 --> |Yes| O1f
    Q9 --> |No| O3f
    Q10{More than one element in the file?}
    Q10 --> |No| O1e
    Q10 --> |Yes| Q11
    Q11{Any straight lines?}
    Q11 --> |Yes| Q12
    Q11 --> |No| Q13
    Q12{Every line longer than 100?}
    Q12 --> |Yes| O2g
    Q12 --> |No| O3g
    Q13{Any ellipses?}
    Q13 --> |Yes| Q14
    Q13 --> |Yes| Q15
    Q14{Any ellipse with height >=50?}
    Q14 --> |Yes| O3h
    Q14 --> |No| Q16
    Q15{Any elements with opacity less than 1?}
    Q15 --> |Yes| Q17
    Q15 --> |No| O3i
    Q16{Any rectangle with area >= 300?}
    Q16 --> |Yes| O1j
    Q16 --> |No| Q18
    Q17{Total element count > 5?}
    Q17 --> |Yes| O1k
    Q17 --> |No| O2k
    Q18{Total element count > 5?}
    Q18 --> |Yes| O2l
    Q18 --> |No| O3l
    O3a[Score 3]
    O3b[Score 3]
    O1c[Score 1]
    O2c[Score 2]
    O2d[Score 2]
    O1e[Score 1]
    O1f[Score 1]
    O3f[Score 3]
    O2g[Score 2]
    O3g[Score 3]
    O3h[Score 3]
    O3i[Score 3]
    O1j[Score 1]
    O1k[Score 1]
    O2k[Score 2]
    O2l[Score 2]
    O3l[Score 3]")
          behavior (behavior/build {} {})
          result (validate/validate-chart-behaviors behavior chart)]
      (is (= {:missing-actions '({:action "Any ellipse with height >=50?"} {:action "Score 1"} {:action "Score 2"} {:action "Any ellipses?"} {:action "Any rectangle with area >= 300?"} {:action "Score 3"} {:action "Any straight lines?"} {:action "Radius larger than 100?"} {:action "Radius larger than 50?"} {:action "Only blue circles?"} {:action "Any text?"} {:action "Any Red Circles"} {:action "Total element count > 5?"} {:action "Any rectangles or squares?"} {:action "Text containing the sequence 'Ihasa'?"} {:action "Any elements with opacity less than 1?"} {:action "More than one element in the file?"} {:action "Every line longer than 100?"} {:action "Rectangle green?"}), 
              :missing-conditions '({:condition "Yes"} {:condition "No"})}  result)))))