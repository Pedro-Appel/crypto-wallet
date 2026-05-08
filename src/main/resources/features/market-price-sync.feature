Feature: Market price synchronization

  Scenario: Store snapshot of assets every 30s
    Given the wallet contains the following assets:
      | symbol | quantity |
      | BTC    | 1.5      |
      | ETH    | 10       |
    And CoinCap returns the following prices:
      | symbol | priceUsd |
      | BTC    | 60000    |
      | ETH    | 3000     |
    When the scheduled price sync runs
    Then a snapshot should be stored for BTC
    And a snapshot should be stored for ETH

  Scenario: Fetch prices only for existing wallet assets
    Given the wallet contains the following assets:
      | symbol |
      | BTC    |
      | ETH    |
    When the scheduled price sync runs
    Then CoinCap should be called for BTC,ETH,ADA

  Scenario: CoinCap unavailability should not stop the application
    Given CoinCap API is unavailable
    When the scheduled price sync runs
    Then the synchronization status should be FAILED
    And the wallet APIs should still be available

  Scenario: Use custom executor with maximum 3 threads
    Given the wallet contains more than 3 assets
    When the scheduled price sync runs
    Then no more than 3 synchronization threads should run simultaneously