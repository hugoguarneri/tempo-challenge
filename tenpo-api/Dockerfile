# Stage 1: Build
FROM gradle:jdk17 AS build
WORKDIR /app

# Copiar los archivos de configuración y el código fuente
COPY build.gradle .
COPY src ./src

# Compilar la aplicación
RUN gradle build --no-daemon --exclude-task test

# Stage 2: Run
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copiar el archivo JAR generado en la etapa anterior
COPY --from=build /app/build/libs/*.jar app.jar

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]


#docker build --tag tenpo-api:latest .
#docker run --publish 8080:8080 --rm -d tenpo-api:latest