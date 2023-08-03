# FROM maven:3.6.0-jdk-11-slim AS build
# COPY src /home/app/src
# COPY pom.xml /home/app
# RUN mvn -f /home/app/pom.xml package
#
# #
# # Package stage
# #
# FROM adoptopenjdk:11-jre-hotspot
# COPY --from=build /home/app/target/demo-security-project-java-1.0-SNAPSHOT.jar /usr/local/lib/demo.jar
# EXPOSE 8080
# CMD ["java","-jar","/usr/local/lib/demo.jar"]

# Use a base image with Java 19.0.2 installed
FROM openjdk:21-jdk
ARG JAR_FILE=target/*.jar
COPY target/demo-security-project-java-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
