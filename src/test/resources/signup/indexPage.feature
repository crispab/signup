Feature: Sign Up index page

  Scenario: The user requests the index page
    Given I have a web browser running
    When I enter the site index page url
    Then it should display

  Scenario: The user clicks "Groups" in the menu
    Given I am on the start page
    When I click on the Groups menu item
    Then the Groups page should display

  Scenario: The user clicks on a group on the groups page
    Given I am on the groups page
    When I click on a group
    Then the group page should display

  Scenario: The user clicks on a user on the groups page
    Given I am on a group page
    When I click on a user
    Then the user page should display
