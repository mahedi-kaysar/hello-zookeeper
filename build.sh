#!/bin/bash

# Build the jar with dependencies
#mvn clean package

#Build docker image of the java application
docker build . -t hello-zookeeper:1.0.0
