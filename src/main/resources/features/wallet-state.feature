Feature: Wallet state

  Scenario: Retrieve wallet current value
    Given the wallet contains the following assets:
      | symbol | quantity | purchasePrice |
      | BTC    | 2        | 50000         |
    And the latest BTC market price is 60000 USD
    When the client requests GET /wallet
    Then the response status should be 200
    And the response should contain:
      | totalWalletValueUsd | 120000 |

  Scenario: Retrieve wallet assets with current values
    Given the wallet contains the following assets:
      | symbol | quantity |
      | BTC    | 1        |
      | ETH    | 5        |
    And the latest BTC market price is 60000 USD
    And the latest ETH market price is 3000 USD
    When the client requests GET /wallet/assets
    Then the response status should be 200
    And the response should contain 2 assets

  Scenario: Return unavailable status for assets without market price
    Given the wallet contains the following assets:
      | symbol |
      | BTC    |
      | XRP    |
    And only BTC has a latest market price
    When the client requests GET /wallet/assets
    Then BTC should have status AVAILABLE
    And XRP should have status UNAVAILABLE