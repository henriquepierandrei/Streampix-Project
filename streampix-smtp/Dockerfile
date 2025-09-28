# Build do SMTP ou Backend
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Instala Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copia pom.xml e src
COPY pom.xml ./
COPY src ./src

# Baixa dependências e builda o jar
RUN mvn dependency:go-offline -B
RUN mvn clean package -DskipTests

# Segunda etapa: runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
