Feature: Login and logout

  Scenario: The admin logs in
    Given the Admin user exist
    And is not logged in
    When Admin logs in
    Then the user name should be visible on the screen

#  Scenario: The admin logs out
#    Given I am logged in
#    When I click My Identity Dropdown
#    And I click logout
#    Then I should be logged out
