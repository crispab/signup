Feature: Be able to authenticate/login and also, logout

  Scenario: The user logs in
    Given I am on the start page
    When I click on the Login menu item
    And submit credentials
    Then I should be logged in

  Scenario: The user logs out
    Given I am logged in
    When I click My Identity Dropdown
    And I click logout
    Then I should be logged out
