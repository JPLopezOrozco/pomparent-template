FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY services/monolith-app/pom.xml services/monolith-app/pom.xml
RUN mvn -q -DskipTests -pl services/monolith-app -am dependency:go-offline

COPY . .
RUN mvn -q -DskipTests -pl services/monolith-app -am package

# 👇 DIAGNÓSTICO: mostrame qué generó Maven
RUN ls -la /app/services/monolith-app/target

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/services/monolith-app/target/monolith-app.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]