(ns mermaid-workflow-api.utils 
  (:require [clj-http.client :as http]
            [mermaid-processor.parse :as parse]))

(defn get-chart 
  [content]
    (if (re-matches #"^http[s]?://.*" content)
      (let [response (http/get content {:as :string})] ;; Ensure response is treated as a string
        (if (= 200 (:status response))
          (parse/parse-mermaid (:body response))
          (throw (ex-info "Failed to fetch from URL."
                          {:status (:status response)
                           :reason (:reason-phrase response)}))))
      content)) ;; If not a URL, return the string as is
 