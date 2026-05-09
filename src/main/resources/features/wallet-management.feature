Feature: Wallet management

  Scenario: Create a wallet
    Given a client send a POST request to /wallet
    When  it has the following body:
      """
      {
        "assets": [
          {"symbol": "BTC", "quantity": 2, "purchasePrice": 50000,"purchaseDate": "2024-01-01T00:00:00Z"},
          {"symbol": "ETH", "quantity": 5, "purchasePrice": 3000, "purchaseDate": "2024-01-01T00:00:00Z"}
        ]
      }
      """
    Then the response status should be 201
    And the response should contain:
      """
      {
        "walletId": "some-uuid"
      }
      """

  Scenario: Add asset to wallet
    Given There is a wallet with id "some-id"
    When the client sends a POST request to /wallet/some-id/assets
    And it has the following body:
      """
      {
        "symbol": "ADA",
        "quantity": 100,
        "purchasePrice": 1.5
        "purchaseDate": "2024-01-01T00:00:00Z"
      }
      """
    Then the response status should be 200
    And the asset should be added to the wallet in the repository