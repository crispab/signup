Feature: Sign Up index page

  Scenario: The user requests the index page
    Given I have a web browser running
    When I enter the site index page url
    Then it should display

  Scenario: The user clicks "Groups"
    Given I am on the start page
    When I click on the Groups menu item
    Then The Groups page should display