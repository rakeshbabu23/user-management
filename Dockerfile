FROM openjdk:17-jdk
ADD target/spring-mysql-docker.jar spring-mysql-docker.jar
ENTRYPOINT ["java","-jar","/spring-mysql-docker.jar"]
