# Etapa 1: compilar con Gradle
FROM gradle:8.9-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# Etapa 2: imagen final con el JAR
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=dev"]
