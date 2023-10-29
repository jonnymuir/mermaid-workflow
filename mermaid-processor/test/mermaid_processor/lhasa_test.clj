(ns mermaid-processor.lhasa-test
  (:require [clojure.test :refer :all]
            [mermaid-processor.behaviors.regex_based :as behavior]
            [mermaid-processor.parse :as parse]
            [mermaid-processor.behaviors.libraries.core :as core]))

;; These tests show are from https://github.com/jmaes12345/lhasa-kata

(def lhasa-chart (parse/parse-mermaid "flowchart TD
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
Q9{Text containing the sequence 'Lhasa'?}
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
O3l[Score 3]"))

;; Example of how to get the missing functions
(deftest complex-chart-test
  (testing "valdate lhasa chart"
    (let [ex (try
               (behavior/build {} lhasa-chart {})
               (catch clojure.lang.ExceptionInfo e e))]
      (is (= (set ["Any ellipse with height >=50?" "Score 1" "Score 2" "Any ellipses?" "Any rectangle with area >= 300?" "Score 3" "Any straight lines?" "Radius larger than 100?" "Radius larger than 50?" "Only blue circles?" "Any text?" "Any Red Circles" "Total element count > 5?" "Any rectangles or squares?" "Text containing the sequence 'Lhasa'?" "Any elements with opacity less than 1?" "More than one element in the file?" "Every line longer than 100?" "Rectangle green?" "Yes" "No"])
             (set ((ex-data ex) :missing))))
      (is (= 21 (count ((ex-data ex) :missing)))))))


;; Example of how to convert the missing functions to an action map
(deftest generates-missing-example-map-test
  (testing "Generate an example mapping from the missing actions"
    (let [ex (try
               (behavior/build {} lhasa-chart {})
               (catch clojure.lang.ExceptionInfo e e))
          expected-value
          [{:regex #"(?i)Any ellipse with height \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*(.*)[\?]?",
            :action [:your-library-here :any-ellipse-with-height :%1 :%2]}
           {:regex #"(?i)Score 1[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Score 2[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Any ellipses?[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Any rectangle with area \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*(.*)[\?]?",
            :action [:your-library-here :any-rectangle-with-area :%1 :%2]}
           {:regex #"(?i)Score 3[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Any straight lines?[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Radius \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*(.*)[\?]?",
            :action [:your-library-here :radius :%1 :%2]}
           {:regex #"(?i)Radius \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*(.*)[\?]?",
            :action [:your-library-here :radius :%1 :%2]}
           {:regex #"(?i)Only blue circles?[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Any text?[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Any Red Circles[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Total element count \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*(.*)[\?]?",
            :action [:your-library-here :total-element-count :%1 :%2]}
           {:regex #"(?i)Any rectangles or squares?[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Text containing the sequence 'Lhasa'?[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Any elements with opacity \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*(.*)[\?]?",
            :action [:your-library-here :any-elements-with-opacity :%1 :%2]}
           {:regex #"(?i)More than one element in the file?[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Every line \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*(.*)[\?]?",
            :action [:your-library-here :every-line :%1 :%2]}
           {:regex #"(?i)Rectangle green?[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Yes[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)No[\?]?",
            :action [:your-library-here :your-function-name-here]}]
          actual-value ((ex-data ex) :example-map)]
      (doseq [[expected actual] (map vector expected-value actual-value)]
        (is (= (.pattern (expected :regex)) (.pattern (actual :regex))))
        (is (= (expected :action) (actual :action)))))))