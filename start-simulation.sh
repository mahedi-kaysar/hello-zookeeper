#!/bin/bash

# start zookeeper three nodes
docker-compose -f docker-compose-start-zookeeper-cluster.yml up -d

#Build docker image of the java application
docker build . -t hello-zookeeper:1.0.0

# sleep 10 seconds to wait zookeeper healthy
sleep 5

# run three container of hello-zookeeper image
docker-compose -f docker-compose-start-app.yml up -d