(ns mermaid-processor.behaviors.libraries.svg-test
  (:require [clojure.test :refer :all]
            [mermaid-processor.behaviors.libraries.svg :as svg]
            [mermaid-processor.behaviors.utils :as utils]
            [clojure.data.xml :as xml]))

;; Test for :any-ellipse-with-height
(deftest any-ellipse-with-height-test-positive
  (testing "any ellipse with height positive test"
    (let [action ((svg/actions :any-ellipse-with-height) :svg "=" "80")
          {result :result} (action 
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str 
"<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"600\" height=\"600\">
  <ellipse cx=\"400\" cy=\"400\" rx=\"80\" ry=\"40\" />
</svg>")))]
      (is result))))

(deftest any-ellipse-with-height-test-negative
  (testing "any ellipse with height positive test"
    (let [action ((svg/actions :any-ellipse-with-height) :svg "!=" "80")
          {result :result} (action
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str
"<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"600\" height=\"600\">
  <ellipse cx=\"400\" cy=\"400\" rx=\"80\" ry=\"40\" />
</svg>")))]
      (is (not result)))))


;; Test for :total-element-count
(deftest total-element-count-test-positive
  (testing "total element count positive test"
    (let [action ((svg/actions :total-element-count) :svg "=" 3)
          {result :result} (action 
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str 
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <circle />
  <rect />
  <ellipse />
</svg>")))]
      (is result))))

(deftest total-element-count-test-negative
  (testing "total element count negative test"
    (let [action ((svg/actions :total-element-count) :svg "!=" 3)
          {result :result} (action
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <circle />
  <rect />
</svg>")))]
      (is result))))

;; Test for :any-rectangles?
(deftest any-rectangles-test-positive
  (testing "any rectangles positive test"
    (let [action ((svg/actions :any-rectangles?) :svg)
          {result :result} (action 
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str 
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <rect />
</svg>")))]
      (is result))))

(deftest any-rectangles-test-negative
  (testing "any rectangles negative test"
    (let [action ((svg/actions :any-rectangles?) :svg)
          {result :result} (action
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <circle />
</svg>")))]
      (is (not result)))))

;; Test for :text-contains-lhasa?
(deftest text-contains-lhasa-test-positive
  (testing "text contains Lhasa positive test"
    (let [action ((svg/actions :text-contains-lhasa?) :svg)
          {result :result} (action 
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str 
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <text>Lhasa is ace</text>
</svg>")))]
      (is result))))

(deftest text-contains-lhasa-test-negative
  (testing "text contains Lhasa negative test"
    (let [action ((svg/actions :text-contains-lhasa?) :svg)
          {result :result} (action
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str 
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <text>Jonny is ace</text>
</svg>")))]
      (is (not result)))))

;; Test for :any-elements-with-opacity
(deftest any-elements-with-opacity-test-positive
  (testing "any elements with opacity positive test"
    (let [action ((svg/actions :any-elements-with-opacity) :svg "=" 0.5)
          {result :result} (action 
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str 
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <circle opacity=\"0.5\" />
</svg>")))]
      (is result))))

(deftest any-elements-with-opacity-test-negative
  (testing "any elements with opacity negative test"
    (let [action ((svg/actions :any-elements-with-opacity) :svg "!=" 0.5)
          {result :result} (action
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <circle opacity=\"0.7\" />
</svg>")))]
      (is result))))

;; Test for :every-line
(deftest every-line-test-positive
  (testing "every line positive test"
    (let [action ((svg/actions :every-line) :svg "=" 100)
          {result :result} (action 
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str 
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <line x1=\"0\" y1=\"0\" x2=\"100\" y2=\"0\" />
</svg>")))]
      (is result))))

(deftest every-line-test-negative
  (testing "every line negative test"
    (let [action ((svg/actions :every-line) :svg "!=" 100)
          {result :result} (action
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <line x1=\"0\" y1=\"0\" x2=\"50\" y2=\"0\" />
</svg>")))]
      (is result))))

;; Test for :any-green-rectangles?
(deftest any-green-rectangles-test-positive
  (testing "any green rectangles positive test"
    (let [action ((svg/actions :any-green-rectangles?) :svg)
          {result :result} (action 
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str 
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <rect fill=\"green\" />
</svg>")))]
      (is result))))

(deftest any-green-rectangles-test-negative
  (testing "any green rectangles negative test"
    (let [action ((svg/actions :any-green-rectangles?) :svg)
          {result :result} (action
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <rect fill=\"blue\" />
</svg>")))]
      (is (not result)))))

;; Test for :any-rectangle-with-area
(deftest any-rectangle-with-area-test-positive
  (testing "any rectangle with area positive test"
    (let [action ((svg/actions :any-rectangle-with-area) :svg "=" 100)
          {result :result} (action 
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str 
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <rect width=\"10\" height=\"10\" />
</svg>")))]
      (is result))))

(deftest any-rectangle-with-area-test-negative
  (testing "any rectangle with area negative test"
    (let [action ((svg/actions :any-rectangle-with-area) :svg "!=" 100)
          {result :result} (action
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str
                                "<svg xmlns=\"http://www.w3.org/2000/svg\">
  <rect width=\"5\" height=\"5\" />
</svg>")))]
      (is result))))

;; Test for :any-straight-lines?
(deftest any-straight-lines-test-positive
  (testing "any straight lines positive test"
    (let [action ((svg/actions :any-straight-lines?) :svg)
          {result :result} (action 
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str 
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <line />
</svg>")))]
      (is result))))

(deftest any-straight-lines-test-negative
  (testing "any straight lines negative test"
    (let [action ((svg/actions :any-straight-lines?) :svg)
          {result :result} (action
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <circle />
</svg>")))]
      (is (not result)))))

;; Test for :radius
(deftest radius-test-positive
  (testing "radius positive test"
    (let [action ((svg/actions :radius) :svg "=" 50)
          {result :result} (action 
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str 
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <circle r=\"50\" />
</svg>")))]
      (is result))))

(deftest radius-test-negative
  (testing "radius negative test"
    (let [action ((svg/actions :radius) :svg "!=" 50)
          {result :result} (action
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <circle r=\"40\" />
</svg>")))]
      (is result))))

;; Test for :only-blue-circles?
(deftest only-blue-circles-test-positive
  (testing "only blue circles positive test"
    (let [action ((svg/actions :only-blue-circles?) :svg)
          {result :result} (action 
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str 
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <circle fill=\"blue\" />
</svg>")))]
      (is result))))

(deftest only-blue-circles-test-negative
  (testing "only blue circles negative test"
    (let [action ((svg/actions :only-blue-circles?) :svg)
          {result :result} (action
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str
                                "<svg xmlns=\"http://www.w3.org/2000/svg\">
  <circle fill=\"red\" />
</svg>")))]
      (is (not result)))))

;; Test for :any-text?
(deftest any-text-test-positive
  (testing "any text positive test"
    (let [action ((svg/actions :any-text?) :svg)
          {result :result} (action 
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str 
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <text>Hello</text>
</svg>")))]
      (is result))))

(deftest any-text-test-negative
  (testing "any text negative test"
    (let [action ((svg/actions :any-text?) :svg)
          {result :result} (action
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str
                                "<svg xmlns=\"http://www.w3.org/2000/svg\">
  <circle />
</svg>")))]
      (is (not result)))))

;; Test for :any-ellipses?
(deftest any-ellipses-test-positive
  (testing "any ellipses positive test"
    (let [action ((svg/actions :any-ellipses?) :svg)
          {result :result} (action 
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str 
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <ellipse cx=\"200\" cy=\"200\" rx=\"50\" ry=\"25\" />
</svg>")))]
      (is result))))

(deftest any-ellipses-test-negative
  (testing "any ellipses negative test"
    (let [action ((svg/actions :any-ellipses?) :svg)
          {result :result} (action
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <circle cx=\"200\" cy=\"200\" r=\"50\" />
</svg>")))]
      (is (not result)))))

;; Test for :any-red-circles?
(deftest any-red-circles-test-positive
  (testing "any red circles positive test"
    (let [action ((svg/actions :any-red-circles?) :svg)
          {result :result} (action 
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str 
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <circle cx=\"200\" cy=\"200\" r=\"50\" fill=\"red\" />
</svg>")))]
      (is result))))

(deftest any-red-circles-test-negative
  (testing "any red circles negative test"
    (let [action ((svg/actions :any-red-circles?) :svg)
          {result :result} (action
                              (utils/set-field-value
                               {}
                               :svg
                               (xml/parse-str
"<svg xmlns=\"http://www.w3.org/2000/svg\">
  <circle cx=\"200\" cy=\"200\" r=\"50\" fill=\"blue\" />
</svg>")))]
      (is (not result)))))
