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
* User

### Wallet
- id
- List<CoinAsset>

### CoinAsset
- symbol (e.g., BTC, ETH)
- quantity (amount owned)
- purchase_price (USD price at acquisition)
- purchase_date

## Functionalities
* Periodic fetch market prices for each coin and store value by date so it can be used later to see the state of the coin in a given time period
  * https://rest.coincap.io/v3/assets
* Add assets to wallet (provide symbol, quantity, purchase price, and purchase date)
* Expose wallet state (List wallet assets and wallet value)
* Expose historical value (Value on a given date)
* Wallet performance - Based on the historical value if each coin purchase calculate the growth of each asset providing:
    * a list with best performing asset,
    * worst performing asset,
    * all asset’s performances

## Technical definition

1. SpringBoot 4 + Java 25
2. PostgreSQL
3. Third party : CoinCap API (https://rest.coincap.io/v3/)

### Specific requirements
#### Third Party
1. Async request (Scheduled every 30s)
2. use a custom ThreadExecutor with a max of 3 threads
3. Unavailability of the external API **must** not make the our API unavailable
4. Price must be in USD

## Project assumptions
- No user management only a single wallet will be implemented for the MVP so no user entity or authentication will be required
- No requirement on performance or reactivity was mentioned so will assume the easier and make it synchronous

## Main APIs
GET /wallet
GET /wallet/assets
GET /wallet/history
GET /wallet/performance
GET /health/prices

## Architecture decisions
- Single wallet MVP
- No authentication
- Daily historical snapshots
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
- Docker or even a Kubernetes Setup
- Rate limiting or retry logic for API calls
- Caching of latest prices
- Pagination for price history
- Metrics or logging
- OpenAPI / Swagger documentation

## Running the project
### Requirements
- Java 25
- PostgreSQL
- Docker (optional, for running PostgreSQL + LGTM Stack)

## Configure API KEY:

COIN_CAP_API_KEY=your_api_key_here

## Run DockerCompose
```bash
docker compose up -d
```
