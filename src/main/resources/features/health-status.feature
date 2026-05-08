Feature: Synchronization health status

  Scenario: Return successful dependency status
    Given the external API is up
    When the client requests GET /actuator/health
    Then the response status should be 200
    And the response should contain:
      | CoinCap API | status | UP |

  Scenario: Return unsuccessful dependency status
    Given the external API is unreachable
    When the client requests GET /actuator/health
    Then the response status should be 200
    And the response should contain:
      | CoinCap API | status | DOWN |