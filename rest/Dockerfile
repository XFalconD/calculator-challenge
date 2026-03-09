FROM maven:3.9.5-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy the rest module pom and source
COPY rest/pom.xml ./pom.xml
COPY rest/src ./src

# Build the rest module
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built JAR from builder
COPY --from=builder /app/target/rest-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
