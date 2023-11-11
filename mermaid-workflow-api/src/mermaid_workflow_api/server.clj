(ns mermaid-workflow-api.server
  (:require [reitit.ring :as ring]
            [reitit.coercion.malli]
            [reitit.openapi :as openapi]
            [reitit.ring.malli]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
    ;       [reitit.ring.middleware.dev :as dev]
            [reitit.ring.spec :as spec]
    ;       [spec-tools.spell :as spell]
            [ring.adapter.jetty :as jetty]
            [muuntaja.core :as m]
            [malli.util :as mu]
            [mermaid-workflow-api.chart :as chart]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.exception :as exception]))

(def ^:private info {:info {:title "mermaid-workflow-api"
             :description "swagger docs with [malli](https://github.com/metosin/malli) and reitit-ring"
             :version "0.0.1"}
                              ;; used in /secure APIs below
      :securityDefinitions {"auth" {:type :apiKey
                                    :in :header
                                    :name "Example-Api-Key"}}
      :tags [{:name "chart", :description "chart processing api"}]}
)

(def app
    "A Ring-compliant web application for the Mermaid Workflow API.
    
      This application is composed of a Reitit router with defined routes for processing
      Mermaid charts. It includes middleware for coercion, exception handling, and Swagger
      UI support for interactive API documentation."
  (ring/ring-handler
    (ring/router
      [["/swagger.json"
        {:get {:no-doc true
               :swagger info
               :handler (swagger/create-swagger-handler)}}]
       ["/openapi.json"
        {:get {:no-doc true
               :openapi info
               :handler (openapi/create-openapi-handler)}}]
       ["/chart"
        {:tags #{"chart"}}
        ["/process" chart/process 
         ]]]

      {;;:reitit.middleware/transform dev/print-request-diffs ;; pretty diffs
       :validate spec/validate ;; enable spec validation for route data
       ;;:reitit.spec/wrap spell/closed ;; strict top-level validation
       :exception pretty/exception
       :data {:coercion (reitit.coercion.malli/create
                          {;; set of keys to include in error messages
                           :error-keys #{#_:type :coercion :in :schema :value :errors :humanized #_:transformed}
                           ;; schema identity function (default: close all map schemas)
                           :compile mu/closed-schema
                           ;; strip-extra-keys (affects only predefined transformers)
                           :strip-extra-keys true
                           ;; add/set default values
                           :default-values true
                           ;; malli options
                           :options nil})
              :muuntaja m/instance
              :middleware [;; swagger & openapi
                           swagger/swagger-feature
                           openapi/openapi-feature
                           ;; query-params & form-params
                           parameters/parameters-middleware
                           ;; content-negotiation
                           muuntaja/format-negotiate-middleware
                           ;; encoding response body
                           muuntaja/format-response-middleware
                           ;; exception handling
                           exception/exception-middleware
                           ;; decoding request body
                           muuntaja/format-request-middleware
                           ;; coercing response bodys
                           coercion/coerce-response-middleware
                           ;; coercing request parameters
                           coercion/coerce-request-middleware
                          ;; multipart
                           multipart/multipart-middleware]}})
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path "/"
         :config {:validatorUrl nil
                  :urls [{:name "swagger", :url "swagger.json"}
                         {:name "openapi", :url "openapi.json"}]
                  :urls.primaryName "openapi"
                  :operationsSorter "alpha"}})
      (ring/create-default-handler))))

(defn start 
  "Run a server"
  [port]
  (jetty/run-jetty #'app {:port  (or port 3000), :join? false})
  (println (str "server running on port "  (or port 3000))))
