# Undercontroll Core API

API principal do sistema Undercontroll, construída com Spring Boot 3, Java 21 e Maven.

## Requisitos

- Java 21
- Docker e Docker Compose, se você quiser subir dependências locais
- PowerShell no Windows, ou outro terminal compatível com o Maven Wrapper

> Use sempre os comandos a partir da raiz do repositório.

## Estrutura de execução

A aplicação pode ser executada em dois cenários principais:

- **`dev`**: usa banco em memória **H2** e RabbitMQ local.
- **`prod`**: usa banco externo e RabbitMQ configurados por variáveis de ambiente.

## 1) Rodando localmente no perfil `dev`

Esse é o jeito mais simples para subir o projeto durante o desenvolvimento.

### 1. Suba o RabbitMQ local

Se você já tiver RabbitMQ instalado, pode usar o serviço local. Caso prefira Docker, suba apenas o RabbitMQ:

```powershell
docker compose up -d rabbitmq
```

### 2. Inicie a aplicação

No PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"; .\mvnw.cmd spring-boot:run
```

Esse perfil usa:

- banco H2 em memória
- console H2 em `http://localhost:8080/h2-console`
- RabbitMQ em `localhost:5672`

## 2) Gerando o artefato e executando o `.jar`

```powershell
.\mvnw.cmd clean package -DskipTests
java -jar target\core-0.0.1-SNAPSHOT.jar
```

Se quiser rodar com um perfil específico:

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"; java -jar target\core-0.0.1-SNAPSHOT.jar
```

## 3) Rodando no perfil `prod`

O perfil `prod` exige variáveis de ambiente para banco e RabbitMQ.

Exemplo básico no PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
$env:DB_URL="jdbc:postgresql://localhost:5432/undercontroll"
$env:DB_DRIVER="org.postgresql.Driver"
$env:DB_USERNAME="undercontroll"
$env:DB_PASSWORD="sua_senha"
$env:RABBITMQ_HOST="localhost"
$env:RABBITMQ_PORT="5672"
$env:RABBITMQ_USER="rabbitmq"
$env:RABBITMQ_PASSWORD="rabbitmq"
$env:JWT_SECRET="uma_chave_forte_aqui"
.\mvnw.cmd spring-boot:run
```

> O arquivo `.env.example` contém a lista completa de variáveis opcionais e recomendadas.

## 4) Usando Docker

### Build da imagem

```powershell
docker build -t undercontroll-core .
```

### Execução do container

Você pode usar um arquivo `.env` para injetar as variáveis:

```powershell
docker run --rm -p 8080:8080 --env-file .env undercontroll-core
```

## 5) Docker Compose

O `docker-compose.yml` atual sobe apenas dependências auxiliares:

- MySQL
- RabbitMQ

Para subir tudo em segundo plano:

```powershell
docker compose up -d
```

Se você estiver usando o perfil `dev`, o banco H2 já vem embutido na aplicação. Nesse caso, o Compose é útil principalmente para o RabbitMQ.

## Variáveis de ambiente mais importantes

### Obrigatórias no `prod`

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `RABBITMQ_HOST`
- `RABBITMQ_USER`
- `RABBITMQ_PASSWORD`
- `JWT_SECRET`

### Opcionais / ajustáveis

- `DB_DRIVER`
- `RABBITMQ_PORT`
- `MAIL_HOST`
- `MAIL_PORT`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `RATE_LIMIT_AUTH_RPM`
- `RATE_LIMIT_GENERAL_RPM`
- `SPRING_PROFILES_ACTIVE`

## URLs úteis

Com a aplicação rodando em `localhost:8080`:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health check: `http://localhost:8080/actuator/health`
- Métricas Prometheus: `http://localhost:8080/actuator/prometheus`
- Console H2 no perfil `dev`: `http://localhost:8080/h2-console`

## Comandos rápidos

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
.\mvnw.cmd clean package -DskipTests
```

## Observações

- O projeto usa Java 21.
- O Maven Wrapper (`mvnw` / `mvnw.cmd`) já vem incluso, então não é necessário instalar Maven globalmente.
- Se você mudar de banco no perfil `prod`, ajuste `DB_URL` e `DB_DRIVER` conforme o driver escolhido.

