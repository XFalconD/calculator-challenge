# Calculator Challenge

Este projeto implementa uma API RESTful de calculadora com comunicação assíncrona via Apache Kafka, utilizando Spring Boot e suporte para precisão arbitrária em números decimais.

## Funcionalidades

- Operações básicas: soma, subtração, multiplicação e divisão
- Suporte para 2 operandos (a e b)
- Precisão arbitrária para números decimais usando BigDecimal
- Comunicação assíncrona entre módulos via Kafka
- Identificadores únicos de requisição com propagação MDC
- Logging estruturado com SLF4J e Logback

## Arquitetura

O projeto é dividido em 2 módulos Maven:
- **rest**: API REST que recebe requisições HTTP e as envia para processamento via Kafka
- **calculator**: Serviço que processa as operações matemáticas e retorna resultados via Kafka

## Pré-requisitos

- Java 21
- Maven 3.6+
- Docker e Docker Compose

## Como Executar

1. **Clone o repositório** (se aplicável)
   ```bash
   git clone <repository-url>
   cd calculator-challenge
   ```

2. **Construa os módulos**
   ```bash
   # Construir calculator
   cd calculator
   mvn clean package -DskipTests
   cd ..

   # Construir rest
   cd rest
   mvn clean package -DskipTests
   cd ..
   ```

3. **Execute com Docker Compose**
   ```bash
   docker-compose up --build
   ```

   Isso iniciará:
   - Zookeeper
   - Kafka
   - Serviço calculator
   - Serviço rest (porta 8080)

4. **Teste a API**

   Exemplos de requisições:

   ```bash
   # Soma
   curl "http://localhost:8080/sum?a=10&b=5"

   # Subtração
   curl "http://localhost:8080/subtract?a=10&b=5"

   # Multiplicação
   curl "http://localhost:8080/multiply?a=10&b=5"

   # Divisão com precisão arbitrária
   curl "http://localhost:8080/divide?a=1&b=3"
   ```

## Configuração

As configurações estão em `application.properties`:
- `calculator.precision`: Precisão para operações (padrão: 50)
- `calculator.request.timeout`: Timeout para requisições Kafka (padrão: 30 segundos)
- Tópicos Kafka configuráveis

## Testes

Execute os testes unitários:

```bash
# No módulo rest
cd rest
mvn test

# No módulo calculator
cd calculator
mvn test
```

## Logs

- Logs são gravados em arquivo (`spring.log`) e console
- Cada linha de log inclui o ID da requisição via MDC
- Nível de log configurável via `application.properties`

## Docker

- Imagens Docker são geradas automaticamente via Spring Boot Maven Plugin
- Docker Compose orquestra todos os serviços necessários