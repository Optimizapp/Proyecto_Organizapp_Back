# Stage 1: cache de dependencias (se re-usa mientras pom.xml no cambie)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Stage 2: compilar con código fuente
COPY src ./src
RUN mvn package -DskipTests

# Stage 3: imagen de ejecución mínima
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]
