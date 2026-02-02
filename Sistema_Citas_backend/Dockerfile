# Paso 1: Construcción
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY src .
RUN mvn clean package -DskipTests

# Paso 2: Ejecución
FROM eclipse-temurin:21-jre
COPY --from=build /target/Sistema_Citas-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]