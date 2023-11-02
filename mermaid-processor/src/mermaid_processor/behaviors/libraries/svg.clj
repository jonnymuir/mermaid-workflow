(ns mermaid-processor.behaviors.libraries.svg
  "SVG library for behaviors, providing some basic functionality for the Lhasa test for how to queston an SVG input"

  (:require [mermaid-processor.behaviors.utils :as utils]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zx]
            [clojure.string :as str]))

(defn- find-ellipses [svg]
  (-> svg
      zip/xml-zip
      (zx/xml-> :xmlns.http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg/ellipse)))

(defn- height-matches? [ellipse comparator height]
  (let [ellipse-height (* 2 (Double/parseDouble (or (zx/attr ellipse :ry) "0")))]
    (utils/apply-comparator ellipse-height comparator height)))

(defn- find-text-elements [svg]
  (-> svg
      zip/xml-zip
      (zx/xml-> :xmlns.http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg/text)))

(defn- text-contains? [text-element substring]
  (str/includes? (str/lower-case (zx/text text-element)) (str/lower-case substring)))


(defn- opacity-matches? [svg-element comparator opacity]
  (let [element-opacity (Double/parseDouble (or (get-in svg-element [:attrs :opacity]) "0"))]
    (utils/apply-comparator element-opacity comparator opacity)))

(defn- find-green-rectangles [svg]
  (-> svg
      zip/xml-zip
      (zx/xml-> [:xmlns.http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg/rect (zx/attr= :fill "green")])))

(defn- find-lines [svg]
  (-> svg
      zip/xml-zip
      (zx/xml-> :xmlns.http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg/line)))

(defn- find-circles [svg]
  (-> svg
      zip/xml-zip
      (zx/xml-> :xmlns.http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg/circle)))

(defn- find-rectangles [svg]
  (-> svg
      zip/xml-zip
      (zx/xml-> :xmlns.http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg/rect)))

(defn- area-matches? [rectangle comparator area]
  (let [rect-width (Double/parseDouble (or (zx/attr rectangle :width) "0"))
        rect-height (Double/parseDouble (or (zx/attr rectangle :height) "0"))
        rect-area (* rect-width rect-height)]
    (utils/apply-comparator rect-area comparator area)))

(defn- radius-matches? [circle comparator radius]
  (let [circle-radius (Double/parseDouble (or (zx/attr circle :r) "0"))]
    (utils/apply-comparator circle-radius comparator radius)))

(defn- all-elements [svg]
  (let [svg-node (-> svg
                     zip/xml-zip
                     (zx/xml-> :xmlns.http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg/svg)
                     first)]
    (filter :tag (zip/children svg-node))))

(defn- only-blue-circles? [svg]
  (let [circles (find-circles svg)
        all-elements-count (count (all-elements svg))
        blue-circles-count (count (filter #(= "blue" (zx/attr % :fill)) circles))]
    (= all-elements-count blue-circles-count)))


(def actions
  "A map of example SVG action functions. To see them in use see the Lhasa test in the github project"
  {:any-ellipse-with-height
   (fn [svg-field comparator height]
     (fn [context]
       (let [svg (utils/get-field-value context svg-field)
             ellipses (find-ellipses svg)
             result (some #(height-matches? % comparator height) ellipses)]
         {:context context
          :result result})))

   :any-ellipses?
   (fn [svg-field]
     (fn [context]
       (let [result (seq (find-ellipses (utils/get-field-value context svg-field)))]
         {:context context
          :result result})))

   :any-red-circles?
   (fn [svg-field]
     (fn [context]
       (let [svg (utils/get-field-value context svg-field)
             circles (find-circles svg)
             result (some #(= "red" (zx/attr % :fill)) circles)]
         {:context context
          :result result})))

   :total-element-count
   (fn [svg-field comparator elem-count]
     (fn [context]
       (let [svg (utils/get-field-value context svg-field)
             result (utils/apply-comparator
                     (count (all-elements svg))
                     comparator
                     elem-count)]
         {:context context
          :result result})))

   :any-rectangles?
   (fn [svg-field]
     (fn [context]
       (let [result (seq (find-rectangles (utils/get-field-value context svg-field)))]
         {:context context
          :result result})))

   :text-contains-lhasa?
   (fn [svg-field]
     (fn [context]
       (let [svg (utils/get-field-value context svg-field)
             texts (find-text-elements svg)
             result (some #(text-contains? % "lhasa") texts)]
         {:context context
          :result result})))

   :any-elements-with-opacity
   (fn [svg-field comparator opacity]
     (fn [context]
       (let [svg (utils/get-field-value context svg-field)
             result (some #(opacity-matches? % comparator opacity) (all-elements svg))]
         {:context context
          :result result})))

   :every-line
   (fn [svg-field comparator length]
     (fn [context]
       (let [svg (utils/get-field-value context svg-field)
             lines (find-lines svg)
             result (every? #(let [x1 (Double/parseDouble (or (zx/attr % :x1) "0"))
                                   x2 (Double/parseDouble (or (zx/attr % :x2) "0"))
                                   y1 (Double/parseDouble (or (zx/attr % :y1) "0"))
                                   y2 (Double/parseDouble (or (zx/attr % :y2) "0"))
                                   line-length (Math/sqrt (+ (Math/pow (- x2 x1) 2) (Math/pow (- y2 y1) 2)))]
                               (utils/apply-comparator line-length comparator length))
                            lines)]
         {:context context
          :result result})))

   :any-green-rectangles?
   (fn [svg-field]
     (fn [context]
       (let [result (seq (find-green-rectangles (utils/get-field-value context svg-field)))]
         {:context context
          :result result})))

   :any-rectangle-with-area
   (fn [svg-field comparator area]
     (fn [context]
       (let [svg (utils/get-field-value context svg-field)
             rectangles (find-rectangles svg)
             result (some #(area-matches? % comparator area) rectangles)]
         {:context context
          :result result})))
   :any-straight-lines?
   (fn [svg-field]
     (fn [context]
       (let [result (seq (find-lines (utils/get-field-value context svg-field)))]
         {:context context
          :result result})))

   :radius
   (fn [svg-field comparator radius]
     (fn [context]
       (let [svg (utils/get-field-value context svg-field)
             circles (find-circles svg)
             result (some #(radius-matches? % comparator radius) circles)]
         {:context context
          :result result})))

   :only-blue-circles?
   (fn [svg-field]
     (fn [context]
       (let [svg (utils/get-field-value context svg-field)
             result (only-blue-circles? svg)]
         {:context context
          :result result})))

   :any-text?
   (fn [svg-field]
     (fn [context]
       (let [svg (utils/get-field-value context svg-field)
             text-elements (find-text-elements svg)
             result (seq text-elements)]
         {:context context
          :result result})))})
