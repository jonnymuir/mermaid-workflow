(ns mermaid-processor.lhasa-test
  (:require [clojure.test :refer :all]
            [mermaid-processor.behavior :as behavior]
            [mermaid-processor.behaviors.regex_based :as regex-behaviors]
            [mermaid-processor.parse :as parse]
            [mermaid-processor.behaviors.libraries.core :as core]
            [mermaid-processor.behaviors.libraries.svg :as svg]
            [mermaid-processor.behaviors.utils :as utils]
            [mermaid-processor.process :as process]
            [clojure.java.io :as io]
            [clojure.data.xml :as xml]
            [clojure.string :as str]))

;; These tests show are from https://github.com/jmaes12345/lhasa-kata

(def mermaid 
  (slurp 
   (io/resource "resources/lhasa/flowchart.mermaid")))

(def lhasa-chart 
  (parse/parse-mermaid mermaid))

(def action-map
  [{:regex #"^(Require SVG|Set SVG|Proceed\?)\s*$"
    :action [:core :do-nothing]}
   {:regex #"(?i)^\s*Any ellipse with height \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*([^?]+)[\s\?]*\s*$",
    :action [:svg :any-ellipse-with-height "svg" :%1 :%2]}
   {:regex #"(?i)^\s*Score (\d+)\s*$",
    :action [:core :set-field "score" :number :%1]}
   {:regex #"(?i)^\s*Any ellipses?[\?]?\s*$",
    :action [:svg :any-ellipses? "svg"]}
   {:regex #"(?i)^\s*Any rectangle with area \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*([^?]+)[\s\?]*\s*$",
    :action [:svg :any-rectangle-with-area "svg" :%1 :%2]}
   {:regex #"(?i)^\s*Any straight lines[\?]?\s*$",
    :action [:svg :any-straight-lines? "svg"]}
   {:regex #"(?i)^\s*Radius \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*([^?]+)[\s\?]*\s*$",
    :action [:svg :radius "svg" :%1 :%2]}
   {:regex #"(?i)^\s*Only blue circles[\?]?\s*$",
    :action [:svg :only-blue-circles? "svg"]}
   {:regex #"(?i)^\s*Any text[\?]?\s*$",
    :action [:svg :any-text? "svg"]}
   {:regex #"(?i)^\s*Any Red Circles[\?]?\s*$",
    :action [:svg :any-red-circles? "svg"]}
   {:regex #"(?i)^\s*Total element count \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*([^?]+)[\s\?]*\s*$",
    :action [:svg :total-element-count "svg" :%1 :%2]}
   {:regex #"(?i)^\s*Any rectangles or squares[\?]?\s*$",
    :action [:svg :any-rectangles? "svg"]}
   {:regex #"(?i)^\s*Text containing the sequence 'Lhasa'[\?]?\s*$",
    :action [:svg :text-contains-lhasa? "svg"]}
   {:regex #"(?i)^\s*Any elements with opacity \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*([^?]+)[\s\?]*\s*$",
    :action [:svg :any-elements-with-opacity "svg" :%1 :%2]}
   {:regex #"(?i)^\s*More than one element in the file[\?]?\s*$",
    :action [:svg :total-element-count "svg" ">" "1"]}
   {:regex #"(?i)^\s*Every line \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*([^?]+)[\s\?]*\s*$",
    :action [:svg :every-line "svg" :%1 :%2]}
   {:regex #"(?i)^\s*Rectangle green[\?]?\s*$",
    :action [:svg :any-green-rectangles? "svg"]}
   {:regex #"(?i)^\s*(Yes|True|Is True)[\?]?\s*$",
    :action [:core :last-result-is-true]}
   {:regex #"(?i)^\s*(No|False|Is False|Is Not True)[\?]?\s*$",
    :action [:core :last-result-is-not-true]}])


;; Example of how to process the Lhasa example
(deftest process-lhasa-example-test
  (testing "Process the lhasa kata from https://github.com/jmaes12345/lhasa-kata - single file test"
    (let [behaviors (behavior/build (regex-behaviors/build 
                     {:core core/actions
                      :svg svg/actions}
                     lhasa-chart
                     action-map))
           result-context (process/process-chart
                           (utils/set-field-value {}
                                                  "svg"
                                                  (xml/parse-str (slurp (io/resource "resources/lhasa/inputs/3_blue_circles-I.svg"))))
                           behaviors
                           lhasa-chart)]
      #_(prn (result-context :audit)) ; comment this back in to see the audit trail 
      (is (== 1 (utils/get-field-value result-context "score"))))))

(deftest process-lhasa-all-examples-test
  (testing "Process the lhasa kata from https://github.com/jmaes12345/lhasa-kata - all files test"
    (let [behaviors (behavior/build (regex-behaviors/build
                                     {:core core/actions
                                      :svg svg/actions}
                                     lhasa-chart
                                     action-map))
          files (file-seq (io/file (io/resource "resources/lhasa/inputs/")))
          svg-files (filter #(str/ends-with? (.getName %) ".svg") files)]
      (doseq [file svg-files]
        (let [file-name (-> file .getName)
              expected-score (cond
                               (str/ends-with? file-name "-I.svg") 1
                               (str/ends-with? file-name "-II.svg") 2
                               (str/ends-with? file-name "-III.svg") 3
                               :else (throw (Exception. (str "Unexpected file format: " file-name))))
              result-context (process/process-chart
                              (utils/set-field-value {}
                                                     "svg"
                                                     (xml/parse-str (slurp file)))
                              behaviors
                              lhasa-chart)
              actual-score (utils/get-field-value result-context "score")]
          (is (== expected-score actual-score) (str "Expected score for " file-name " to be " expected-score " but was " actual-score))))
      (is (= 90 (count svg-files)))
      )))
