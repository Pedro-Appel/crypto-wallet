# Crypto Wallet Asset Manager

## Project Overview
Build a Java (Spring Boot) backend service that tracks the value and performance of cryptocurrency wallets.

The system must:
- Periodically retrieve market prices from the CoinCap API (https://rest.coincap.io/v3/)
- Store historical price data for the currency and user
- Calculate wallet value and asset performance
- Expose REST APIs for querying the wallet state

## Entities
* Wallet
* CoinAsset

### Wallet
- id
- List<CoinAsset>

### CoinAsset
- symbol (e.g., BTC, ETH)
- name (e.g., Bitcoin, Ethereum)
- quantity (amount owned)
- purchase_price (USD price at acquisition)
- purchase_date

## Functionalities
* Periodic fetch market prices for each coin and store value by date so it can be used later to see the state of the coin in a given time period
  * https://rest.coincap.io/v3/assets
* Add assets to wallet (provide symbol, quantity, purchase price, and purchase date)
* Expose wallet assets
* Expose wallet summary
* Expose historical value (Value on a given date)
* Wallet performance - Based on the historical value if each coin purchase calculate the growth of each asset providing:
    * best performing asset,
    * worst performing asset,
    * all asset’s performances

## Technical definition
1. SpringBoot 4 + Java 25
2. PostgreSQL
3. Third party : CoinCap API (https://rest.coincap.io/v3/)
4. OTEL for metrics, tracing and logging
5. Docker for local development and testing
6. Gradle as build tool
7. JUnit + Mockito for testing
8. Swagger / OpenAPI for API documentation (External API Health Exposure with Actuator)
9. FeignClient for external API integration
10. Spring Scheduler for periodic tasks
11. ThreadPoolTaskExecutor for async execution of scheduled tasks (Max 3 threads)
12. Resilience4j for handling external API failures gracefully

### Specific requirements
#### Third Party
1. Async request (Scheduled every 30s)
2. Use a custom ThreadExecutor with a max of 3 threads
3. Unavailability of the external API **must** not make the our API unavailable
4. Price must be in USD

## Project assumptions
- No user management will be implemented for the MVP, so no user entity or authentication will be required
- No requirement on performance or reactivity was mentioned so will assume the easier and make it synchronous

## Main APIs
POST /wallet - Create a new wallet
GET /wallet/{id} - Get wallet current value
GET /wallet/{id}/assets - List all assets purchases in the wallet
POST /wallet/{id}/assets - Add a new asset to the wallet
GET /wallet/{id}/history?fromDate={iso-8601-instant} - Get historical value of the wallet from a given date
GET /wallet/{id}/performance - Get performance of each asset in the wallet
GET /actuator/health - Health check endpoint to monitor the status of the application and its dependencies (including external API)

## Architecture decisions
- Wallets are managed by id
- No authentication
- Market price snapshots are scheduled every 30 seconds
- CoinCap integration with async scheduler
- External API failures do not affect local APIs

## MVP Implementation Plan
- Timestamp conversion
- Wallet state API
- Historical snapshots persistence
- Historical wallet queries
- Performance calculations
- Scheduler + async execution
- Health endpoint
- Failure handling

## After MVP
- Docker or even a Kubernetes Setup (Check)
- Rate limiting or retry logic for API calls (Check)
- Caching of latest prices (Check - But not for external API calls, only for internal queries) 
- Pagination for price history (TBD)
- Metrics or logging (Check)
- OpenAPI / Swagger documentation (Check)

## Running the project
### Requirements
- Java 25
- PostgreSQL
- Docker (optional, for running PostgreSQL + LGTM Stack)

## Configure API KEY:

COIN_CAP_API_KEY=your_api_key_here

## Run DockerCompose
```bash
  COIN_CAP_API_KEY=your_api_key_here \
  COIN_CAP_BASE_URL=https://rest.coincap.io \
  COIN_CAP_ASSET_SNAPSHOT_FIXED_RATE=30 \
  POSTGRES_DB=crypto_wallet \
  POSTGRES_USER=crypto \
  POSTGRES_PASSWORD=crypto \
  POSTGRES_PORT=5432 \
  APP_PORT=8080 \
  SPRING_PROFILES_ACTIVE=local \
  JAVA_OPTS="-Xmx256m -Xms128m" \
  docker compose up -d
```
