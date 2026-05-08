Feature: Historical wallet value

  Scenario: Retrieve wallet value for a given timestamp
    Given BTC historical price on timestamp 1767225600000 is 60000 USD
    And the wallet contains 2 BTC
    When the client requests:
      """
      GET /wallet/history?fromDate=2026-01-01T00:00:00Z
      """
    Then the response status should be 200
    And the response should contain:
      | totalWalletValueUsd | 120000 |

  Scenario: Return empty value when no historical data exists
    Given no historical prices exist
    When the client requests:
      """
      GET /wallet/history?fromDate=2000-01-01T00:00:00Z
      """
    Then the response status should be 404