FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY . .
RUN mvn -q -DskipTests dependency:go-offline && \
    mvn -q -DskipTests -pl services/transaction-service -am package

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/services/transaction-service/target/*-SNAPSHOT.jar /app/app.jar

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SERVER_PORT=8082

EXPOSE 8082
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

