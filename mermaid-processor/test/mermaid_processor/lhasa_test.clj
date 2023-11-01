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
            [clojure.data.xml :as xml]))

;; These tests show are from https://github.com/jmaes12345/lhasa-kata

(def mermaid 
  (slurp 
   (io/resource "resources/lhasa/flowchart.mermaid")))

(def lhasa-chart 
  (parse/parse-mermaid mermaid))


(def action-map
  [{:regex #"(?i)Any ellipse with height \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*(.*)[\?]?",
    :action [:svg :any-ellipse-with-height :svg :%1 :%2]}
   {:regex #"(?i)Score (\d+)",
    :action [:core :set-number :score :%1]}
   {:regex #"(?i)Any ellipses?[\?]?",
    :action [:svg :any-ellipses? :svg]}
   {:regex #"(?i)Any rectangle with area \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*(.*)[\?]?",
    :action [:svg :any-rectangle-with-area :svg :%1 :%2]}
   {:regex #"(?i)Any straight lines[\?]?",
    :action [:svg :any-straight-lines? :svg]}
   {:regex #"(?i)Radius \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*(.*)[\?]?",
    :action [:svg :radius :svg :%1 :%2]}
   {:regex #"(?i)Only blue circles[\?]?",
    :action [:svg :only-blue-circles? :svg]}
   {:regex #"(?i)Any text[\?]?",
    :action [:svg :any-text? :svg]}
   {:regex #"(?i)Any Red Circles[\?]?",
    :action [:svg :any-red-circles? :svg]}
   {:regex #"(?i)Total element count \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*(.*)[\?]?",
    :action [:svg :total-element-count :svg :%1 :%2]}
   {:regex #"(?i)Any rectangles or squares[\?]?",
    :action [:svg :any-rectangles? :svg]}
   {:regex #"(?i)Text containing the sequence 'Lhasa'[\?]?",
    :action [:svg :text-contains-lhasa? :svg]}
   {:regex #"(?i)Any elements with opacity \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*(.*)[\?]?",
    :action [:svg :any-elements-with-opacity :svg :%1 :%2]}
   {:regex #"(?i)More than one element in the file[\?]?",
    :action [:svg :total-element-count :svg ">" "1"]}
   {:regex #"(?i)Every line \s*(>=|<=|>|<|==|!=|larger than|less than|longer than|shorter than)\s*(.*)[\?]?",
    :action [:svg :every-line :svg :%1 :%2]}
   {:regex #"(?i)Rectangle green[\?]?",
    :action [:svg :any-green-rectangles? :svg]}
   {:regex #"(?i)(Yes|True|Is True)[\?]?",
    :action [:core :last-result-is-true]}
   {:regex #"(?i)(No|False|Is False|Is Not True)[\?]?",
    :action [:core :last-result-is-not-true]}])


;; Example of how to process the Lhasa example
(deftest process-lhasa-example-test
  (testing "Process the lhasa kata from https://github.com/jmaes12345/lhasa-kata"
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
      (is (== 3 (utils/get-field-value result-context "score"))))))
