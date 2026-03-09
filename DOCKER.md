# Calculator Challenge - Docker Setup

This project uses Docker and Docker Compose to orchestrate a multi-service application with:
- **Kafka** message broker
- **Calculator Service** (processes arithmetic operations)
- **REST API Service** (handles HTTP requests)

## Prerequisites

- Docker (version 20.10+)
- Docker Compose (version 1.29+)

## Quick Start

### 1. Build and Start All Services

```bash
docker-compose up --build
```

This will:
- Build the Calculator and REST services
- Start Zookeeper
- Start Kafka
- Start the Calculator service
- Start the REST API on `http://localhost:8080`

### 2. Stop All Services

```bash
docker-compose down
```

### 3. View Logs

View logs for all services:
```bash
docker-compose logs -f
```

View logs for a specific service:
```bash
docker-compose logs -f rest
docker-compose logs -f calculator
docker-compose logs -f kafka
```

## API Usage

Once the REST service is running, you can test the calculator:

```bash
# Sum operation
curl -X POST http://localhost:8080/api/calculator/sum \
  -H "Content-Type: application/json" \
  -d '{"a": 10, "b": 20}'

# Subtract operation
curl -X POST http://localhost:8080/api/calculator/subtract \
  -H "Content-Type: application/json" \
  -d '{"a": 10, "b": 5}'

# Multiply operation
curl -X POST http://localhost:8080/api/calculator/multiply \
  -H "Content-Type: application/json" \
  -d '{"a": 5, "b": 4}'

# Divide operation (with arbitrary precision)
curl -X POST http://localhost:8080/api/calculator/divide \
  -H "Content-Type: application/json" \
  -d '{"a": 100, "b": 3}'
```

## Service Communication

The services communicate asynchronously via Kafka:

1. **REST Service** receives HTTP request → generates requestId → sends `OperationRequest` to `calculator-requests` topic
2. **Calculator Service** consumes from `calculator-requests` → processes operation → publishes result to `calculator-responses` topic
3. **REST Service** consumes from `calculator-responses` → retrieves cached result → returns to client

## Architecture

```
┌─────────────┐
│  REST API   │ (Port 8080)
└──────┬──────┘
       │
       ├──→ Kafka (Port 9092)
       │
┌──────┴──────┐
│  Calculator │
│   Service   │
└─────────────┘
```

## Configuration

### Environment Variables

Both services accept these environment variables via `docker-compose.yml`:

- `SPRING_KAFKA_BOOTSTRAP_SERVERS` - Kafka bootstrap servers (default: `kafka:9092`)
- `KAFKA_TOPIC_REQUEST` - Request topic name (default: `calculator-requests`)
- `KAFKA_TOPIC_RESPONSE` - Response topic name (default: `calculator-responses`)

Modify `docker-compose.yml` to change these values.

## Troubleshooting

### Kafka Connection Issues

If services can't connect to Kafka:
1. Verify Kafka is healthy: `docker-compose ps`
2. Check Kafka logs: `docker-compose logs kafka`
3. Ensure `kafka:9092` is used (not `localhost:9092`) within Docker network

### Service Crashes

View detailed logs:
```bash
docker-compose logs calculator
docker-compose logs rest
```

### Complete Restart

Clean restart from scratch:
```bash
docker-compose down -v
docker-compose up --build
```

## Performance Tuning

For production use, consider:
- Increasing Kafka replication factor
- Configuring resource limits in `docker-compose.yml`
- Adding health checks with longer startup time for complex operations

## Testing

Unit tests can be run locally without Docker:

```bash
# Calculator module
cd calculator
mvn test

# REST module
cd ../rest
mvn test
```

Integration tests require a running Kafka instance (use Docker).
