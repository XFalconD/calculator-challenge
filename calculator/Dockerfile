FROM maven:3.9.5-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy the calculator module pom and source
COPY calculator/pom.xml ./pom.xml
COPY calculator/src ./src

# Build the calculator module
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built JAR from builder
COPY --from=builder /app/target/calculator-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
