# Walmart Movies Demo

Spring Boot + Kafka + Azure Cosmos DB emulator with a React UI. Everything is containerized so you can run it locally with Docker Compose.

## Stack
- **API**: Spring Boot 3, Azure Cosmos DB SDK, Spring Kafka
- **DB**: Azure Cosmos DB Linux emulator (SQL API)
- **Messaging**: Kafka + Kafka UI
- **Frontend**: React (Vite)

## Prerequisites
- Docker + Docker Compose
- 3GB+ memory available for the Cosmos DB emulator

## Running
```bash
docker compose up --build
```
Services:
- API: http://localhost:8080/api/movies
- Frontend: http://localhost:5173
- Kafka UI: http://localhost:8085
- Cosmos DB emulator explorer: https://localhost:8081/_explorer/index.html

### Cosmos DB certificate (when running locally)
The emulator uses a self-signed certificate. The API container downloads the cert at startup and imports it into the JVM trust store. If you call the API directly from your host, download the cert and trust it locally:
```bash
curl -k https://localhost:8081/_explorer/emulator.pem -o emulator.pem
sudo security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain emulator.pem  # macOS example
```

## API quick look
- `GET /api/movies` list movies
- `POST /api/movies` create `{title, genre, year, rating?}`
- `PUT /api/movies/{id}` update
- `DELETE /api/movies/{id}` delete

Each create/update/delete also publishes a Kafka event to the `movies.events` topic; Kafka UI is included for inspection.

## Configuration
Environment variables (all have defaults for local dev):
- `AZURE_COSMOS_URI` (default `https://cosmosdb:8081/`)
- `AZURE_COSMOS_KEY` (emulator default)
- `AZURE_COSMOS_DB` (default `moviesdb`)
- `SPRING_KAFKA_BOOTSTRAP_SERVERS` (default `kafka:9092`)
- `MOVIE_EVENTS_TOPIC` (default `movies.events`)

## Stopping
```bash
docker compose down
```
## Switching from in memory to cosmos and vice-versa
- `in the .env file comment/uncomment the desired storage whether you want in memory or from Azure cosmosDB`
- `then run the following docker command to update the settings/configuration details:`
```bash
docker compose --env-file .env up
```
