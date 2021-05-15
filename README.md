# WIP: SMA Processing - System Multi Agents Processing

## Requirements
* Requires Scala 2.12.2 or higher
* Building should be done by using SBT (Scala Built Tools) with version 0.13 or higher.

## Creating a SMA build

First `sbt compile` should be executed. 

Then a new jar-package can be created with `sbt assembly`.

## Launch program 

Deploy folder for queries with static data `dataset\csparql_web_server` on server (tomcat, jboss ..) , port 9000

Run Program
### arguments Examples 
--queries=csparql_query/Q1.txt
--queries=csparql_query/Q1.txt,csparql_query/Q2.txt