#!/bin/bash

docker-compose -f docker-compose-start-app.yml down -v
docker-compose -f docker-compose-start-zookeeper-cluster.yml down -v
docker rm $(docker ps -a -q)