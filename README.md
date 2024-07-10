# hello-zookeeper

# Problem Statement:

Imagine an environment that consists of multiple nodes. Each node is a separate JVM process and could 
potentially be running on a distinct physical machine. Your task is to write an application that will 
run on all nodes. The application should coordinate between the nodes so that as they are started 
System.out.println ("We are started!") is only called exactly once across the whole cluster, 
whether 1 node or 10 are running.

# Problem Analysis

The problem states that the java application will be running on multiple node (separate physical machine 
or VM) and only one node (JVM process) will be printing a message on startup even multiple nodes are started at a time. 

Therefore - each nodes need to know if the message is already printed by another node before its printing.

# Solution
This above problem can be solved using Apache Zookeeper which is an opensource co-ordination service and
widely used highly scalable configuration management, co-ordinating services, handling race-condition, distributed 
locking, deadlock prevention, leader selection in a distributed environment. 

We will use leader selection approach of apache zookeeper to solve the problem.
Steps are:

- Start Zookeeper Cluster
- create a persistent parent node (/parent) in the cluster. The parent node ensures that the starting point which is always
  available even all the ephemeral nodes got deleted
- create a non-persistent child node(/parent/n_<sequence_number>) also known as ephemeral sequential in apache zookeeper.
- find the node of lowest sequence number from the all active children of the parent node (/parent/*).
- The node with the lowest sequence number will print ("We are started!").

# Tools and library used
- maven (3.9.8) for dependency management
- java 8
- Apache Zookeeper client library
- docker and docker-compose to startup zookeeper cluster and run the multiple instance of application.

# Build
- ./build.sh

# Run Simulation - 3 node apache zookeeper and 3 process of the java application

- ./start-zookeeper.sh
- ./start-app.sh
- ./stop-app.sh # for stopping the java application
- ./stop-zookeeper.sh # for stopping the cluster

## Notes:
- we have used Thread.sleep(<Milisec>) and logged current node names to simulate the work - which was not mentioned in the requirement. 

# Output snapshot:

## Nodes running
![alt text](docs/nodes-running.png)

## Logs of the application
![alt text](docs/output-app-logs.png)

From the logs we can see only 1 node printed the expected message ("We are started!")