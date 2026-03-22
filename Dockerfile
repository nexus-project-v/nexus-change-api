FROM maven:3.9.8-eclipse-temurin-21 AS build
RUN mkdir -p /app
WORKDIR /app
COPY pom.xml /app
COPY src /app/src
RUN mvn -B -f pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9933

# Define a default value for the Spring profile, but allow overriding via environment variable
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}","app.jar"]
