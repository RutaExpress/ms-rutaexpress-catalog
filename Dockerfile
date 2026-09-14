# ===== Etapa 1: build =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar el pom
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código y compilar
COPY src ./src
RUN mvn clean package -DskipTests -B

# ===== Etapa 2: runtime =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario no root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar solo el jar
COPY --from=build /app/target/*.jar app.jar

# Puerto
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]