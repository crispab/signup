Feature: Create event

  Scenario: The administrator creates an event
    Given the group Friends exist with Tom, Dick, Harry
      And the Friends plan a Dinner tomorrow at 20:00
    When The administrator creates the event
    Then the event Dinner exists in the Friends group
      And nobody has signed up yet
