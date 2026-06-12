FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

FROM tomcat:11.0-jdk21-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /app/target/backend-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/backend.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
