(ns mermaid-processor.lhasa-validate-test
  (:require [clojure.test :refer :all]
            [mermaid-processor.behaviors.regex_based :as behavior]
            [mermaid-processor.parse :as parse]
            [mermaid-processor.behaviors.libraries.core :as core]
            [mermaid-processor.behaviors.utils :as utils]
            [mermaid-processor.process :as process]
            [clojure.java.io :as io]))

;; These tests show are from https://github.com/jmaes12345/lhasa-kata

(def mermaid 
  (slurp 
   (io/resource "resources/lhasa/flowchart.mermaid")))

(def lhasa-chart 
  (parse/parse-mermaid mermaid))

;; Example of how to get the missing functions
(deftest complex-chart-test
  (testing "valdate lhasa chart"
    (let [ex (try
               (behavior/build {} lhasa-chart {})
               (catch clojure.lang.ExceptionInfo e e))]
      (is (= (set ["Require SVG" "Proceed?" "Any ellipse with height >=50?" "Score 1" "Score 2" "Any ellipses?" "Any rectangle with area >= 300?" "Score 3" "Any straight lines?" "Radius larger than 100?" "Radius larger than 50?" "Only blue circles?" "Any text?" "Any Red Circles" "Total element count > 5?" "Any rectangles or squares?" "Text containing the sequence 'Lhasa'?" "Any elements with opacity less than 1?" "More than one element in the file?" "Every line longer than 100?" "Rectangle green?" "Yes" "No"])
             (set ((ex-data ex) :missing))))
      (is (= 23 (count ((ex-data ex) :missing)))))))


;; Example of how to convert the missing functions to an action map
(deftest generates-missing-example-map-test
  (testing "Generate an example mapping from the missing actions"
    (let [ex (try
               (behavior/build {} lhasa-chart {})
               (catch clojure.lang.ExceptionInfo e e))
          expected-value
          [{:regex (re-pattern (str "(?i)Any ellipse with height \\s*(" utils/all-comparators ")\\s*([^?]+)[\\s\\?]*")),
            :action [:your-library-here :any-ellipse-with-height :%1 :%2]}
           {:regex #"(?i)Score 1[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Score 2[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Any ellipses[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex (re-pattern (str "(?i)Any rectangle with area \\s*(" utils/all-comparators ")\\s*([^?]+)[\\s\\?]*")),
            :action [:your-library-here :any-rectangle-with-area :%1 :%2]}
           {:regex #"(?i)Score 3[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Any straight lines[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex (re-pattern (str "(?i)Radius \\s*(" utils/all-comparators ")\\s*([^?]+)[\\s\\?]*")),
            :action [:your-library-here :radius :%1 :%2]}
           {:regex #"(?i)Only blue circles[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Any text[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Any Red Circles[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex (re-pattern (str "(?i)Total element count \\s*(" utils/all-comparators ")\\s*([^?]+)[\\s\\?]*")),
            :action [:your-library-here :total-element-count :%1 :%2]}
           {:regex #"(?i)Any rectangles or squares[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Require SVG[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)Text containing the sequence 'Lhasa'[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex (re-pattern (str "(?i)Any elements with opacity \\s*(" utils/all-comparators ")\\s*([^?]+)[\\s\\?]*")),
            :action [:your-library-here :any-elements-with-opacity :%1 :%2]}
           {:regex #"(?i)More than one element in the file[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex (re-pattern (str "(?i)Every line \\s*(" utils/all-comparators ")\\s*([^?]+)[\\s\\?]*")),
            :action [:your-library-here :every-line :%1 :%2]}
           {:regex #"(?i)Rectangle green[\?]?",
            :action [:your-library-here :your-function-name-here]}
           {:regex #"(?i)(Yes|True|Is True)[\?]?",
            :action [:core :last-result-is-true]}
           {:regex #"(?i)(No|False|Is False|Is Not True)[\?]?",
            :action [:core :last-result-is-not-true]}]
          actual-value ((ex-data ex) :example-map)]
      (doseq [[expected actual] (map vector expected-value actual-value)]
        (is (= (.pattern (expected :regex)) (.pattern (actual :regex))))
        (is (= (expected :action) (actual :action)))))))