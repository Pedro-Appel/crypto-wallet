Feature: Timestamp conversion

  Scenario: Convert timestamp to ISO-8601
    Given the timestamp 1767225600000
    When the timestamp is converted
    Then the ISO-8601 value should be:
      """
      2026-01-01T00:00:00Z"""


  Scenario: Store historical snapshots using millisecond timestamps
    Given the scheduled price sync runs on 2026-01-01T00:00:00.000Z
    When the daily snapshot is stored
    Then the stored timestamp should be 1767225600000