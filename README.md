# Calculator Challenge

This project implements a RESTful calculator API with asynchronous communication via Apache Kafka, using Spring Boot and support for arbitrary precision in decimal numbers.

## Features

- Basic operations: addition, subtraction, multiplication, and division
- Support for 2 operands (a and b)
- Arbitrary precision for decimal numbers using BigDecimal
- Asynchronous communication between modules via Kafka
- Unique request identifiers with MDC propagation
- Structured logging with SLF4J and Logback

## Architecture

The project is divided into 2 Maven modules:
- **rest**: REST API that receives HTTP requests and sends them for processing via Kafka
- **calculator**: Service that processes mathematical operations and returns results via Kafka

## Prerequisites

- Java 21
- Maven 3.6+
- Docker and Docker Compose

## How to Run

1. **Clone the repository** (if applicable)
   ```bash
   git clone https://github.com/XFalconD/calculator-challenge/
   cd calculator-challenge
   ```

2. **Build the modules**
   ```bash
   # Build calculator
   cd calculator
   mvn clean package -DskipTests
   cd ..

   # Build rest
   cd rest
   mvn clean package -DskipTests
   cd ..
   ```

3. **Run with Docker Compose**
   ```bash
   docker-compose up --build
   ```

   This will start:
   - Zookeeper
   - Kafka
   - calculator service
   - rest service (port 8080)

4. **Test the API**

   Request examples:

   ```bash
   # Sum
   curl "http://localhost:8080/sum?a=10&b=5"

   # Subtraction
   curl "http://localhost:8080/subtract?a=10&b=5"

   # Multiplication
   curl "http://localhost:8080/multiply?a=10&b=5"

   # Division with arbitrary precision
   curl "http://localhost:8080/divide?a=1&b=3"
   ```

## Configuration

Settings are in `application.properties`:
- `calculator.precision`: Precision for operations (default: 10)
- `calculator.request.timeout`: Timeout for Kafka requests (default: 30 seconds)
- Configurable Kafka topics

## Tests

Execute unit tests:

```bash
# In the rest module
cd rest
mvn test

# In the calculator module
cd calculator
mvn test
```

## Logs

- Logs are written to file (`spring.log`) and console
- Each log line includes the request ID via MDC
- Log level configurable via `application.properties`

## Docker

- Docker images are generated automatically via Spring Boot Maven Plugin
- Docker Compose orchestrates all required services