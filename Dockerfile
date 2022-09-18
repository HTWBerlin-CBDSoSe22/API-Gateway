#
# Build stage
#
FROM maven:3.8.5-openjdk-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

#
# Package stage
#
FROM openjdk:17-oracle
COPY --from=build /home/app/target/*.jar demo.jar
EXPOSE 8088
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","demo.jar"]