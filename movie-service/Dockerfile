# ─────────────────────────────────────────────────────────────────────────────
# Multi-stage build — reduce el tamaño de la imagen final
#
# Stage 1 (builder): compila el proyecto con JDK completo + Maven
# Stage 2 (runtime): solo el JAR + JRE (sin Maven, sin código fuente)
#
# Resultado: imagen ~150MB en lugar de ~500MB con JDK completo
# ─────────────────────────────────────────────────────────────────────────────

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:25-jdk AS runner

# curl es necesario para el health check de Docker Compose y Kubernetes.
# eclipse-temurin (Ubuntu base) no lo incluye por defecto.
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \

WORKDIR /app

# Copia solo el JAR del stage anterior
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 51100

# Virtual Threads habilitados por defecto en Spring Boot 4 + Java 25
# -XX:+UseZGC: Garbage Collector de baja latencia (recomendado con Virtual Threads)
ENTRYPOINT ["java", "-XX:+UseZGC", "-jar", "app.jar"]