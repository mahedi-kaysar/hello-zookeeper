FROM openjdk:8-jre-alpine
WORKDIR /
ADD target/hello-zookeeper-client-1.0-jar-with-dependencies.jar hello-zookeeper-client.jar
CMD ["java", "-jar", "hello-zookeeper-client.jar"]
