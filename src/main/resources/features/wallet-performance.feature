Feature: Wallet performance

  Scenario: Calculate asset percentage growth
    Given the wallet contains the following assets:
      | symbol | purchasePrice |
      | BTC    | 50000         |
    And the current BTC market price is 60000 USD
    When the client requests GET /wallet/performance
    Then BTC performance percentage should be 20%

  Scenario: Calculate asset USD gain
    Given the wallet contains the following assets:
      | symbol | quantity | purchasePrice |
      | BTC    | 2        | 50000         |
    And the current BTC market price is 60000 USD
    When the client requests GET /wallet/performance
    Then BTC USD gain should be $20000

  Scenario: Return best performing asset
    Given the wallet contains the following assets:
      | symbol | purchasePrice |
      | BTC    | 50000         |
      | ETH    | 4000          |
    And the current BTC market price is 70000 USD
    And the current ETH market price is 3000 USD
    When the client requests GET /wallet/performance
    Then BTC should be the best performing asset

  Scenario: Return worst performing asset
    Given the wallet contains the following assets:
      | symbol | purchasePrice |
      | BTC    | 50000         |
      | ETH    | 4000          |
    And the current BTC market price is 70000 USD
    And the current ETH market price is 3000 USD
    When the client requests GET /wallet/performance
    Then ETH should be the worst performing asset