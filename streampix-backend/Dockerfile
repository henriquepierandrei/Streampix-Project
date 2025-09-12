# Imagem base do Java
FROM eclipse-temurin:21-jdk AS builder

# Define o diretório de trabalho
WORKDIR /app

# Copia o projeto
COPY . .

RUN chmod +x mvnw
# Compila o projeto (se usar Maven)
RUN ./mvnw clean package -DskipTests

# =========================
# Segunda etapa: rodar a app
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia o JAR gerado da etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Porta que o Spring Boot vai expor
EXPOSE 8080

# Comando de execução
ENTRYPOINT ["java", "-jar", "app.jar"]
