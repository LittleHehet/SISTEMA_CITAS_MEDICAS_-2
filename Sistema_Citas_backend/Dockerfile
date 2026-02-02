# Paso 1: Construcción (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# Paso 2: Ejecución (Runtime)
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/Sistema_Citas-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]