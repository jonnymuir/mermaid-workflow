(ns mermaid-workflow-api.chart
  (:require [clj-http.client :as http]
            [mermaid-processor.behavior :as behavior]
            [mermaid-processor.behaviors.regex_based :as regex-behaviors]
            [mermaid-processor.parse :as parse]
            [mermaid-processor.behaviors.libraries.core :as core]
            [mermaid-processor.behaviors.libraries.svg :as svg]
            [mermaid-processor.behaviors.utils :as utils]
            [mermaid-processor.process :as process]
            [clojure.data.xml :as xml]
            [cheshire.core :as json]))

(defn- fetch-url [url]
  (try
    (let [response (http/get url {:as :string})]
      ;; If the status is OK, return the body, otherwise throw an ex-info
      (if (= 200 (:status response))
        (:body response)
        (throw (ex-info "Non-200 response received" 
                        {:status (:status response)
                         :reason (:reason-phrase response)}))))
    ;; Catch only exceptions that are not of type ExceptionInfo
    (catch clojure.lang.ExceptionInfo e
      (throw e)) ;; rethrow the ex-info exception
    (catch Exception e
      ;; Wrap and re-throw as ex-info with additional context
      (throw (ex-info "Error fetching URL"
                      {:url url
                       :cause (.getMessage e)
                       :exception e})))))


(defn- get-chart
  [content]
  (if (re-matches #"^http[s]?://.*" content)
    (parse/parse-mermaid (fetch-url content))
    (parse/parse-mermaid content))) ;; If not a URL, return the string as is

(def ^:private cached-get-chart (memoize get-chart))

(defn- string-to-keyword [s]
  (when (and s (re-matches #":.*" s))
    (keyword (subs s 1))))

(defn- parse-action [action]
  (mapv #(or (string-to-keyword %) %) action))

(defn- json-to-action-map [json-str]
  (let [parsed-json (json/parse-string json-str true) ;; 'true' for keywordizing keys
        action-map (map (fn [rule]
                     {:regex (re-pattern (rule :regex))
                      :action (parse-action (rule :action))})
                   parsed-json)]
    (vec action-map)))

(defn- get-mappings
  [mappings]
  (if (re-matches #"^http[s]?://.*" mappings)
    (let [mappings-string (fetch-url mappings)]
      (json-to-action-map mappings-string))
    (json-to-action-map mappings))) ;; If not a URL, return the string as is

(def ^:private cached-get-mappings (memoize get-mappings))

(def process
 "Processes a Mermaid chart by applying a set of regex-based mapping rules to generate a context map.
  
  This function takes a Mermaid chart and a set of mappings as input, either directly as strings or via URLs. It fetches the content if URLs are provided, parses the Mermaid chart, and converts the TOML mappings to a Clojure map. It then builds behavior functions based on the parsed mappings and applies them to the Mermaid chart to process it according to the defined rules. The result is a context map that reflects the outcome of the processing, which is returned in the response body upon a successful operation.

  If the process encounters an error, it returns an ExceptionInfo with details about the failure.

  The `process` function is designed to be used as a handler in a web service endpoint, responding to POST requests with a JSON body containing the 'context', 'chart', 'mappings', and 'additional-data' fields.

  - `context`: An optional initial context map.
  - `chart`: A string representing the Mermaid chart or a URL pointing to the chart content.
  - `mappings`: A string representing the mappings in TOML format or a URL pointing to the mappings content.
  - `additional-data`: An optional vector of maps containing additional data fields.

  Responses:
  - 200: A map with the key 'context' containing the processed context.
  - 500: An error map with the key 'reason' containing error details if the processing fails."
  {:post
   {:summary "Process a mermaid chart example"
    :parameters
    {:body
     {:context string?
      :chart string?,
      :mappings string?,
      :additional-data [:vector [:map {:field-name string?, :field-value string?}]]}}
    :responses {200 {:body [:map [:context string?]]}
                500 {:body any?}}
    :handler
    (fn
      [{{{:keys [context chart mappings _]} :body} :parameters}]
        (try 
          (let [parsed-chart (cached-get-chart chart)
                parsed-mappings (cached-get-mappings mappings)
                context (if (context) (json/decode context)  {})
                behaviors (behavior/build (regex-behaviors/build
                                           {:core core/actions
                                            :svg svg/actions}
                                           parsed-chart
                                           parsed-mappings))
                result-context (process/process-chart
                                (utils/set-field-value
                                 context
                                 "svg"
                                 (xml/parse-str "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"600\" height=\"600\">
                                  <circle cx=\"150\" cy=\"150\" r=\"65\" fill=\"blue\" />
                                  <circle cx=\"350\" cy=\"100\" r=\"70\" fill=\"blue\" />
                                  <circle cx=\"250\" cy=\"450\" r=\"125\" fill=\"blue\" />
                                </svg>"))
                                behaviors
                                parsed-chart)]
            {:status 200
             :body {:context (json/generate-string result-context)}})
          (catch clojure.lang.ExceptionInfo e
            {:status 500
             :body {:reason (ex-data e)}})))}})
      