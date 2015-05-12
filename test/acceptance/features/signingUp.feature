Feature: Sign up for an event

  Scenario Outline: Tom responds to an event
    Given the group Friends exist with Tom, Dick, Harry
      And the group Friends has an event Dinner
    When Tom navigates to his sign up page
      And submits he is <coming>
    Then the number of participants is <participants>
      And the participation of Tom is <status>

    Examples:
    |coming   |participants|status|
    |On       |1           |On    |
    |Off      |0           |Off   |
    |Maybe    |0           |Maybe |


  Scenario Outline: All respond to an event with comments
    Given the group Friends exist with Tom, Dick, Harry
    And the group Friends has an event Dinner
    When <user> navigates to his sign up page
    And enters a comment "<comment>"
    And submits he is <coming>
    Then the participation of <user> is <coming>
    And the comment by <user> is "<comment>"

    Examples:
      |user  |comment               |coming|
      |Tom   |Yay!!                 |On    |
      |Dick  |Naah, don't think so. |Off   |
      |Harry |Hmm, I don't know...  |Maybe |

