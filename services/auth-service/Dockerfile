FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /build

COPY . .

RUN mvn -q -DskipTests -f services/auth-service/pom.xml package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /build/services/auth-service/target/*.jar /app/app.jar

ENV JAVA_TOOL_OPTIONS="-Xms256m -Xmx512m"
ENV SERVER_PORT=9090

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]