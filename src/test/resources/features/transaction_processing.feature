Feature: Transaction Processing
  As a system operator
  I want to process day transactions
  So that I can track processed transactions with additional fees

  Scenario: Processing a pending transaction
    Given there is a day transaction with following details
      | id | description    | amount | status  |
      | 1  | Test Payment  | 300.00 | PENDING |
    When the transaction is processed
    Then the processed transaction should have following details
      | processedStatus | processedAmount |
      | IN_PROCESS     | 330.000         |