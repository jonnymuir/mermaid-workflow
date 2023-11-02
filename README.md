[![Clojure CI](https://github.com/jonnymoo/mermaid-workflow/actions/workflows/clojure.yml/badge.svg)](https://github.com/jonnymoo/mermaid-workflow/actions/workflows/clojure.yml)

# mermaid-workflow
A project which aims to allow runnable user centric workflow described by mermaid diagrams

# Context
[Mermaid diagrams](https://mermaid.js.org/) is tool to create complex diagrams in simple markdown. Its main purpose is to help documentation catch-up with development.

# Problem statement
I would like to describe how work flows in a simple diagram.

This could be for:
- My own simple to do list of work.
- A repeatable pattern / process of work for use in an organisation.
- To describe how services to end users work and how work flows between consumers of the service and the organisations which are required to fulfil the service e.g. [Service design](https://gds.blog.gov.uk/2016/04/18/what-we-mean-by-service-design/) 

# Constraints and Criteria
I would like to be able to describe my workflow as simply as possible in a mermaid diagram.
I want the diagram to be "test-able / run-able" with as little (ideally no) effort as possible.

# Quick Start Guide

## Prerequisites

### 1. Java
Ensure you have Java installed. You can check by running:
```
java -version
```
If not installed, download and install the OpenJDK from [AdoptOpenJDK's official website](https://adoptopenjdk.net/).

### 2. Clojure
Install Clojure using the instructions provided on the [official Clojure site](https://clojure.org/guides/getting_started).

### 3. Leiningen
This is a build tool for Clojure. Install it by following the instructions on the [official Leiningen website](https://leiningen.org/).

## Setting Up the Project

### 1. Clone the Repository
```
git clone https://github.com/jonnymoo/mermaid-workflow.git
cd mermaid-workflow
```

### 2. Fetch Dependencies
```
lein deps
```

### 3. Run the Tests
```
lein test
```
This will execute all the tests in the project. Ensure all tests pass to verify that everything is set up correctly.

## Exploring Further: Understanding the Lhasa Test

The Lhasa test, located in `/mermaid_processor/test/mermaid_processor/lhasa_test.clj`, is a test suite designed by Lhasa here https://github.com/jmaes12345/lhasa-kata

It has been used to show how to provide custom functionality and run an automatic from a mermaid flowchart.

### The Mermaid Flowchart

The test begins by reading a Mermaid flowchart from the file `resources/lhasa/flowchart.mermaid`. This flowchart describes the expected behavior and conditions that SVG files should meet.

```
(def mermaid 
  (slurp 
   (io/resource "resources/lhasa/flowchart.mermaid")))

(def lhasa-chart 
  (parse/parse-mermaid mermaid))
```

[View the flowchart.mermaid file](https://github.com/jonnymoo/mermaid-workflow/blob/main/mermaid-processor/test/resources/lhasa/flowchart.mermaid)

### Action Map

The `action-map` is a collection of regex patterns paired with corresponding actions. Each regex pattern is designed to match specific conditions or queries from the Mermaid flowchart. When a match is found, the associated action is executed.

### Single File Test

The `process-lhasa-example-test` function is an example of how to process a single SVG file against the Mermaid flowchart. It uses the behaviors derived from the action map and the parsed Mermaid chart to make decisions according to contents of the SVG file and generate a score.

### Processing All SVG Files

The `process-lhasa-all-examples-test` function is designed to process all SVG files located in the `resources/lhasa/inputs/` directory. For each SVG file, the test determines the expected score based on the file's naming convention (e.g., `-I.svg` indicates a score of 1). It then processes the SVG file using the behaviors and the Mermaid chart and compares the expected score with the actual score.

### Conclusion

For a deeper dive, explore the [Lhasa test file](https://github.com/jonnymoo/mermaid-workflow/blob/main/mermaid-processor/test/mermaid_processor/lhasa_test.clj) in the repository

# Solution concepts
```mermaid
journey
    title A simple workflow
    section Implement Simplest Workflow
      Create an API: 5: Me
      Create a Work Tray: 3: Me
    section Persistence
      Create backing stores: 5: Me
    section User Testing
      Observation: 5: Janey
```
### Bugs and Issues

...

## Design guidelines

Use TDD to implement a modular / composable design that promotes simplicity over complexity

Order function parameters by specificity (general to specific), e.g. my-function [context behavior item]

Aim towards a DDD like Ubiquitous Language. This means entities should use real life terms from the business / user domain. This language should be refleced in the code. And on language, a decision has been made to not use british english, but stick with american english, e.g. behavior, not behaviour. This is simply to make it consistent with other code bases.

```mermaid
C4Container
    title Container diagram for Workflow Automation System

    Person_Ext(user, "User", "Uses the WorkflowUI to interact with the system.")

    System_Boundary(workflowSys, "Workflow System") {
        Container(webUI, "WorkflowUI", "Web Application", "Provides a reference front-end for the workflow.")
        Container(webAPI, "WorkflowAPI", "Web API", "Handles the business logic and data processing for workflows.")
        ContainerDb(clojars, "Clojars", "Artifact Repository", "Stores the Clojure libraries used in the system.")
        Container(mermaidLib, "mermaid-processing", "Clojure Library", "Processes and renders diagrams.")
    }

    Rel(user, webUI, "Uses", "HTTP/HTTPS")
    Rel(webUI, webAPI, "Sends requests to", "HTTP/HTTPS")
    Rel(webAPI, clojars, "Fetches libraries from")
    Rel(webAPI, mermaidLib, "Uses for diagram processing")

    UpdateElementStyle(user, $fontColor="blue")
    UpdateRelStyle(webUI, webAPI, $textColor="green", $lineColor="green")
    UpdateRelStyle(webAPI, clojars, $textColor="purple", $lineColor="purple")
    UpdateRelStyle(webAPI, mermaidLib, $textColor="orange", $lineColor="orange")
```
```mermaid
C4Component
    title Component diagram for mermaid-processing library

    Boundary(mermaidLib, "mermaid-processing") {
        Component(context, "Context", "Stores the state of the running workflow, current node, and other state required by conditions or actions.")
        Component(behavior, "Behavior", "Specifies actions and conditions. Can modify the Context.")
        Component(chart, "Chart", "Represents the parsed Mermaid diagram.")
    }

    Rel(behavior, context, "Modifies")
    Rel(behavior, chart, "Reads state from")
    Rel(chart, context, "Updates state in")
```