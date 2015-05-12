Feature: Simple navigation

  Scenario: The user requests the start page
    Given the user has a blank web browser
    When pointing the browser to the start page
    Then the start page should display

  Scenario: The home link should work
    Given the user is on the start page
    When selecting the home link
    Then the start page should display

  Scenario: The user selects Groups in the menu
    Given the group Friends exist with Tom, Dick, Harry
      And the user is on the start page
    When selecting the Groups menu item
    Then the groups page should display
      And the group Friends is listed on the page

#  Scenario: The user clicks on a group on the groups page
#    Given the group Friends exist with Tom, Dick, Harry
#      And the user is on the groups page
#    When selecting the Friends group
#    Then the group Friends page should display
#      And members Tom, Dick, Harry is listed on the page
#
#  Scenario: The user clicks on a user on the groups page
#    Given the group Friends exist with Tom, Dick, Harry
#      And the user is on the group Friends page
#    When selecting the member Dick
#    Then the user page for Dick should display
