$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("createEvent.feature");
formatter.feature({
  "line": 1,
  "name": "Create event",
  "description": "",
  "id": "create-event",
  "keyword": "Feature"
});
formatter.before({
  "duration": 4591792958,
  "status": "passed"
});
formatter.before({
  "duration": 3901065180,
  "status": "passed"
});
formatter.scenario({
  "line": 3,
  "name": "The administrator creates an event",
  "description": "",
  "id": "create-event;the-administrator-creates-an-event",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 4,
  "name": "the group Friends exist with Tom, Dick, Harry",
  "keyword": "Given "
});
formatter.step({
  "line": 5,
  "name": "the Friends plan a Dinner tomorrow at 20:00",
  "keyword": "And "
});
formatter.step({
  "line": 6,
  "name": "The administrator creates the event",
  "keyword": "When "
});
formatter.step({
  "line": 7,
  "name": "the event Dinner exists in the Friends group",
  "keyword": "Then "
});
formatter.step({
  "line": 8,
  "name": "nobody has signed up yet",
  "keyword": "And "
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Tom, Dick, Harry",
      "offset": 29
    }
  ],
  "location": "SigningUpSteps.createGroup(String,String\u003e)"
});
formatter.result({
  "duration": 2098129690,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 4
    },
    {
      "val": "Dinner",
      "offset": 19
    },
    {
      "val": "tomorrow at 20:00",
      "offset": 26
    }
  ],
  "location": "CreateEventSteps.planEventAtTime(String,String,String)"
});
formatter.result({
  "duration": 1131185262,
  "status": "passed"
});
formatter.match({
  "location": "CreateEventSteps.createEvent()"
});
formatter.result({
  "duration": 8610780105,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Dinner",
      "offset": 10
    },
    {
      "val": "Friends",
      "offset": 31
    }
  ],
  "location": "CreateEventSteps.verifyEventExists(String,String)"
});
formatter.result({
  "duration": 462579557,
  "status": "passed"
});
formatter.match({
  "location": "CreateEventSteps.verifyNobodySignedUp()"
});
formatter.result({
  "duration": 1590252719,
  "status": "passed"
});
formatter.after({
  "duration": 2137348853,
  "status": "passed"
});
formatter.after({
  "duration": 146227785,
  "status": "passed"
});
formatter.after({
  "duration": 126157,
  "status": "passed"
});
formatter.uri("loginLogout.feature");
formatter.feature({
  "line": 1,
  "name": "Login and logout",
  "description": "",
  "id": "login-and-logout",
  "keyword": "Feature"
});
formatter.before({
  "duration": 46254,
  "status": "passed"
});
formatter.before({
  "duration": 9368216,
  "status": "passed"
});
formatter.scenario({
  "line": 3,
  "name": "The admin logs in",
  "description": "",
  "id": "login-and-logout;the-admin-logs-in",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 4,
  "name": "the Admin user exist",
  "keyword": "Given "
});
formatter.step({
  "line": 5,
  "name": "is not logged in",
  "keyword": "And "
});
formatter.step({
  "line": 6,
  "name": "Admin logs in",
  "keyword": "When "
});
formatter.step({
  "line": 7,
  "name": "the user should be logged in",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "Admin",
      "offset": 4
    }
  ],
  "location": "LoginLogoutSteps.verifyUserExists(String)"
});
formatter.result({
  "duration": 144711960,
  "status": "passed"
});
formatter.match({
  "location": "LoginLogoutSteps.verifyNotLoggedIn()"
});
formatter.result({
  "duration": 592404414,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Admin",
      "offset": 0
    }
  ],
  "location": "LoginLogoutSteps.login(String)"
});
formatter.result({
  "duration": 1437566363,
  "status": "passed"
});
formatter.match({
  "location": "LoginLogoutSteps.verifyLoggedIn()"
});
formatter.result({
  "duration": 165068377,
  "status": "passed"
});
formatter.after({
  "duration": 323722,
  "status": "passed"
});
formatter.after({
  "duration": 170541,
  "status": "passed"
});
formatter.after({
  "duration": 36370,
  "status": "passed"
});
formatter.uri("signingUp.feature");
formatter.feature({
  "line": 1,
  "name": "Sign up for an event",
  "description": "",
  "id": "sign-up-for-an-event",
  "keyword": "Feature"
});
formatter.scenarioOutline({
  "line": 3,
  "name": "Tom responds to an event",
  "description": "",
  "id": "sign-up-for-an-event;tom-responds-to-an-event",
  "type": "scenario_outline",
  "keyword": "Scenario Outline"
});
formatter.step({
  "line": 4,
  "name": "the group Friends exist with Tom, Dick, Harry",
  "keyword": "Given "
});
formatter.step({
  "line": 5,
  "name": "the group Friends has an event Dinner",
  "keyword": "And "
});
formatter.step({
  "line": 6,
  "name": "Tom navigates to his sign up page",
  "keyword": "When "
});
formatter.step({
  "line": 7,
  "name": "submits he is \u003ccoming\u003e",
  "keyword": "And "
});
formatter.step({
  "line": 8,
  "name": "the number of participants is \u003cparticipants\u003e",
  "keyword": "Then "
});
formatter.step({
  "line": 9,
  "name": "the participation of Tom is \u003cstatus\u003e",
  "keyword": "And "
});
formatter.examples({
  "line": 11,
  "name": "",
  "description": "",
  "id": "sign-up-for-an-event;tom-responds-to-an-event;",
  "rows": [
    {
      "cells": [
        "coming",
        "participants",
        "status"
      ],
      "line": 12,
      "id": "sign-up-for-an-event;tom-responds-to-an-event;;1"
    },
    {
      "cells": [
        "On",
        "1",
        "On"
      ],
      "line": 13,
      "id": "sign-up-for-an-event;tom-responds-to-an-event;;2"
    },
    {
      "cells": [
        "Off",
        "0",
        "Off"
      ],
      "line": 14,
      "id": "sign-up-for-an-event;tom-responds-to-an-event;;3"
    },
    {
      "cells": [
        "Maybe",
        "0",
        "Maybe"
      ],
      "line": 15,
      "id": "sign-up-for-an-event;tom-responds-to-an-event;;4"
    }
  ],
  "keyword": "Examples"
});
formatter.before({
  "duration": 93634,
  "status": "passed"
});
formatter.before({
  "duration": 46578222,
  "status": "passed"
});
formatter.scenario({
  "line": 13,
  "name": "Tom responds to an event",
  "description": "",
  "id": "sign-up-for-an-event;tom-responds-to-an-event;;2",
  "type": "scenario",
  "keyword": "Scenario Outline"
});
formatter.step({
  "line": 4,
  "name": "the group Friends exist with Tom, Dick, Harry",
  "keyword": "Given "
});
formatter.step({
  "line": 5,
  "name": "the group Friends has an event Dinner",
  "keyword": "And "
});
formatter.step({
  "line": 6,
  "name": "Tom navigates to his sign up page",
  "keyword": "When "
});
formatter.step({
  "line": 7,
  "name": "submits he is On",
  "matchedColumns": [
    0
  ],
  "keyword": "And "
});
formatter.step({
  "line": 8,
  "name": "the number of participants is 1",
  "matchedColumns": [
    1
  ],
  "keyword": "Then "
});
formatter.step({
  "line": 9,
  "name": "the participation of Tom is On",
  "matchedColumns": [
    2
  ],
  "keyword": "And "
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Tom, Dick, Harry",
      "offset": 29
    }
  ],
  "location": "SigningUpSteps.createGroup(String,String\u003e)"
});
formatter.result({
  "duration": 1613725557,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Dinner",
      "offset": 31
    }
  ],
  "location": "SigningUpSteps.createEvent(String,String)"
});
formatter.result({
  "duration": 436699349,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Tom",
      "offset": 0
    }
  ],
  "location": "SigningUpSteps.navigateToSignUpPage(String)"
});
formatter.result({
  "duration": 1337353810,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "On",
      "offset": 14
    }
  ],
  "location": "SigningUpSteps.setParticipationAndSubmit(String)"
});
formatter.result({
  "duration": 2010169928,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "1",
      "offset": 30
    }
  ],
  "location": "SigningUpSteps.verifyParticipants(int)"
});
formatter.result({
  "duration": 1544433957,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Tom",
      "offset": 21
    },
    {
      "val": "On",
      "offset": 28
    }
  ],
  "location": "SigningUpSteps.verifyStatus(String,String)"
});
formatter.result({
  "duration": 173163069,
  "status": "passed"
});
formatter.after({
  "duration": 675880,
  "status": "passed"
});
formatter.after({
  "duration": 2027807672,
  "status": "passed"
});
formatter.after({
  "duration": 34174,
  "status": "passed"
});
formatter.before({
  "duration": 65202,
  "status": "passed"
});
formatter.before({
  "duration": 11052993,
  "status": "passed"
});
formatter.scenario({
  "line": 14,
  "name": "Tom responds to an event",
  "description": "",
  "id": "sign-up-for-an-event;tom-responds-to-an-event;;3",
  "type": "scenario",
  "keyword": "Scenario Outline"
});
formatter.step({
  "line": 4,
  "name": "the group Friends exist with Tom, Dick, Harry",
  "keyword": "Given "
});
formatter.step({
  "line": 5,
  "name": "the group Friends has an event Dinner",
  "keyword": "And "
});
formatter.step({
  "line": 6,
  "name": "Tom navigates to his sign up page",
  "keyword": "When "
});
formatter.step({
  "line": 7,
  "name": "submits he is Off",
  "matchedColumns": [
    0
  ],
  "keyword": "And "
});
formatter.step({
  "line": 8,
  "name": "the number of participants is 0",
  "matchedColumns": [
    1
  ],
  "keyword": "Then "
});
formatter.step({
  "line": 9,
  "name": "the participation of Tom is Off",
  "matchedColumns": [
    2
  ],
  "keyword": "And "
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Tom, Dick, Harry",
      "offset": 29
    }
  ],
  "location": "SigningUpSteps.createGroup(String,String\u003e)"
});
formatter.result({
  "duration": 1576902113,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Dinner",
      "offset": 31
    }
  ],
  "location": "SigningUpSteps.createEvent(String,String)"
});
formatter.result({
  "duration": 445162389,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Tom",
      "offset": 0
    }
  ],
  "location": "SigningUpSteps.navigateToSignUpPage(String)"
});
formatter.result({
  "duration": 800630402,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Off",
      "offset": 14
    }
  ],
  "location": "SigningUpSteps.setParticipationAndSubmit(String)"
});
formatter.result({
  "duration": 1540218223,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "0",
      "offset": 30
    }
  ],
  "location": "SigningUpSteps.verifyParticipants(int)"
});
formatter.result({
  "duration": 1588936179,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Tom",
      "offset": 21
    },
    {
      "val": "Off",
      "offset": 28
    }
  ],
  "location": "SigningUpSteps.verifyStatus(String,String)"
});
formatter.result({
  "duration": 137805224,
  "status": "passed"
});
formatter.after({
  "duration": 359596,
  "status": "passed"
});
formatter.after({
  "duration": 2053059727,
  "status": "passed"
});
formatter.after({
  "duration": 62138,
  "status": "passed"
});
formatter.before({
  "duration": 73646,
  "status": "passed"
});
formatter.before({
  "duration": 12275511,
  "status": "passed"
});
formatter.scenario({
  "line": 15,
  "name": "Tom responds to an event",
  "description": "",
  "id": "sign-up-for-an-event;tom-responds-to-an-event;;4",
  "type": "scenario",
  "keyword": "Scenario Outline"
});
formatter.step({
  "line": 4,
  "name": "the group Friends exist with Tom, Dick, Harry",
  "keyword": "Given "
});
formatter.step({
  "line": 5,
  "name": "the group Friends has an event Dinner",
  "keyword": "And "
});
formatter.step({
  "line": 6,
  "name": "Tom navigates to his sign up page",
  "keyword": "When "
});
formatter.step({
  "line": 7,
  "name": "submits he is Maybe",
  "matchedColumns": [
    0
  ],
  "keyword": "And "
});
formatter.step({
  "line": 8,
  "name": "the number of participants is 0",
  "matchedColumns": [
    1
  ],
  "keyword": "Then "
});
formatter.step({
  "line": 9,
  "name": "the participation of Tom is Maybe",
  "matchedColumns": [
    2
  ],
  "keyword": "And "
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Tom, Dick, Harry",
      "offset": 29
    }
  ],
  "location": "SigningUpSteps.createGroup(String,String\u003e)"
});
formatter.result({
  "duration": 1616911754,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Dinner",
      "offset": 31
    }
  ],
  "location": "SigningUpSteps.createEvent(String,String)"
});
formatter.result({
  "duration": 426713754,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Tom",
      "offset": 0
    }
  ],
  "location": "SigningUpSteps.navigateToSignUpPage(String)"
});
formatter.result({
  "duration": 594922652,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Maybe",
      "offset": 14
    }
  ],
  "location": "SigningUpSteps.setParticipationAndSubmit(String)"
});
formatter.result({
  "duration": 1608291590,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "0",
      "offset": 30
    }
  ],
  "location": "SigningUpSteps.verifyParticipants(int)"
});
formatter.result({
  "duration": 1584019933,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Tom",
      "offset": 21
    },
    {
      "val": "Maybe",
      "offset": 28
    }
  ],
  "location": "SigningUpSteps.verifyStatus(String,String)"
});
formatter.result({
  "duration": 138767439,
  "status": "passed"
});
formatter.after({
  "duration": 639566,
  "status": "passed"
});
formatter.after({
  "duration": 2014477191,
  "status": "passed"
});
formatter.after({
  "duration": 64057,
  "status": "passed"
});
formatter.scenarioOutline({
  "line": 18,
  "name": "All respond to an event with comments",
  "description": "",
  "id": "sign-up-for-an-event;all-respond-to-an-event-with-comments",
  "type": "scenario_outline",
  "keyword": "Scenario Outline"
});
formatter.step({
  "line": 19,
  "name": "the group Friends exist with Tom, Dick, Harry",
  "keyword": "Given "
});
formatter.step({
  "line": 20,
  "name": "the group Friends has an event Dinner",
  "keyword": "And "
});
formatter.step({
  "line": 21,
  "name": "\u003cuser\u003e navigates to his sign up page",
  "keyword": "When "
});
formatter.step({
  "line": 22,
  "name": "enters a comment \"\u003ccomment\u003e\"",
  "keyword": "And "
});
formatter.step({
  "line": 23,
  "name": "submits he is \u003ccoming\u003e",
  "keyword": "And "
});
formatter.step({
  "line": 24,
  "name": "the participation of \u003cuser\u003e is \u003ccoming\u003e",
  "keyword": "Then "
});
formatter.step({
  "line": 25,
  "name": "the comment by \u003cuser\u003e is \"\u003ccomment\u003e\"",
  "keyword": "And "
});
formatter.examples({
  "line": 27,
  "name": "",
  "description": "",
  "id": "sign-up-for-an-event;all-respond-to-an-event-with-comments;",
  "rows": [
    {
      "cells": [
        "user",
        "comment",
        "coming"
      ],
      "line": 28,
      "id": "sign-up-for-an-event;all-respond-to-an-event-with-comments;;1"
    },
    {
      "cells": [
        "Tom",
        "Yay!!",
        "On"
      ],
      "line": 29,
      "id": "sign-up-for-an-event;all-respond-to-an-event-with-comments;;2"
    },
    {
      "cells": [
        "Dick",
        "Naah, don\u0027t think so.",
        "Off"
      ],
      "line": 30,
      "id": "sign-up-for-an-event;all-respond-to-an-event-with-comments;;3"
    },
    {
      "cells": [
        "Harry",
        "Hmm, I don\u0027t know...",
        "Maybe"
      ],
      "line": 31,
      "id": "sign-up-for-an-event;all-respond-to-an-event-with-comments;;4"
    }
  ],
  "keyword": "Examples"
});
formatter.before({
  "duration": 66118,
  "status": "passed"
});
formatter.before({
  "duration": 10657131,
  "status": "passed"
});
formatter.scenario({
  "line": 29,
  "name": "All respond to an event with comments",
  "description": "",
  "id": "sign-up-for-an-event;all-respond-to-an-event-with-comments;;2",
  "type": "scenario",
  "keyword": "Scenario Outline"
});
formatter.step({
  "line": 19,
  "name": "the group Friends exist with Tom, Dick, Harry",
  "keyword": "Given "
});
formatter.step({
  "line": 20,
  "name": "the group Friends has an event Dinner",
  "keyword": "And "
});
formatter.step({
  "line": 21,
  "name": "Tom navigates to his sign up page",
  "matchedColumns": [
    0
  ],
  "keyword": "When "
});
formatter.step({
  "line": 22,
  "name": "enters a comment \"Yay!!\"",
  "matchedColumns": [
    1
  ],
  "keyword": "And "
});
formatter.step({
  "line": 23,
  "name": "submits he is On",
  "matchedColumns": [
    2
  ],
  "keyword": "And "
});
formatter.step({
  "line": 24,
  "name": "the participation of Tom is On",
  "matchedColumns": [
    0,
    2
  ],
  "keyword": "Then "
});
formatter.step({
  "line": 25,
  "name": "the comment by Tom is \"Yay!!\"",
  "matchedColumns": [
    0,
    1
  ],
  "keyword": "And "
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Tom, Dick, Harry",
      "offset": 29
    }
  ],
  "location": "SigningUpSteps.createGroup(String,String\u003e)"
});
formatter.result({
  "duration": 1574607655,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Dinner",
      "offset": 31
    }
  ],
  "location": "SigningUpSteps.createEvent(String,String)"
});
formatter.result({
  "duration": 415542722,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Tom",
      "offset": 0
    }
  ],
  "location": "SigningUpSteps.navigateToSignUpPage(String)"
});
formatter.result({
  "duration": 661787506,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Yay!!",
      "offset": 18
    }
  ],
  "location": "SigningUpSteps.enterComment(String)"
});
formatter.result({
  "duration": 151513534,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "On",
      "offset": 14
    }
  ],
  "location": "SigningUpSteps.setParticipationAndSubmit(String)"
});
formatter.result({
  "duration": 1441784417,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Tom",
      "offset": 21
    },
    {
      "val": "On",
      "offset": 28
    }
  ],
  "location": "SigningUpSteps.verifyStatus(String,String)"
});
formatter.result({
  "duration": 134132781,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Tom",
      "offset": 15
    },
    {
      "val": "Yay!!",
      "offset": 23
    }
  ],
  "location": "SigningUpSteps.verifyComment(String,String)"
});
formatter.result({
  "duration": 557147144,
  "status": "passed"
});
formatter.after({
  "duration": 253211,
  "status": "passed"
});
formatter.after({
  "duration": 2025964626,
  "status": "passed"
});
formatter.after({
  "duration": 34852,
  "status": "passed"
});
formatter.before({
  "duration": 59123,
  "status": "passed"
});
formatter.before({
  "duration": 11189448,
  "status": "passed"
});
formatter.scenario({
  "line": 30,
  "name": "All respond to an event with comments",
  "description": "",
  "id": "sign-up-for-an-event;all-respond-to-an-event-with-comments;;3",
  "type": "scenario",
  "keyword": "Scenario Outline"
});
formatter.step({
  "line": 19,
  "name": "the group Friends exist with Tom, Dick, Harry",
  "keyword": "Given "
});
formatter.step({
  "line": 20,
  "name": "the group Friends has an event Dinner",
  "keyword": "And "
});
formatter.step({
  "line": 21,
  "name": "Dick navigates to his sign up page",
  "matchedColumns": [
    0
  ],
  "keyword": "When "
});
formatter.step({
  "line": 22,
  "name": "enters a comment \"Naah, don\u0027t think so.\"",
  "matchedColumns": [
    1
  ],
  "keyword": "And "
});
formatter.step({
  "line": 23,
  "name": "submits he is Off",
  "matchedColumns": [
    2
  ],
  "keyword": "And "
});
formatter.step({
  "line": 24,
  "name": "the participation of Dick is Off",
  "matchedColumns": [
    0,
    2
  ],
  "keyword": "Then "
});
formatter.step({
  "line": 25,
  "name": "the comment by Dick is \"Naah, don\u0027t think so.\"",
  "matchedColumns": [
    0,
    1
  ],
  "keyword": "And "
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Tom, Dick, Harry",
      "offset": 29
    }
  ],
  "location": "SigningUpSteps.createGroup(String,String\u003e)"
});
formatter.result({
  "duration": 1546661443,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Dinner",
      "offset": 31
    }
  ],
  "location": "SigningUpSteps.createEvent(String,String)"
});
formatter.result({
  "duration": 443777672,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Dick",
      "offset": 0
    }
  ],
  "location": "SigningUpSteps.navigateToSignUpPage(String)"
});
formatter.result({
  "duration": 749084005,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Naah, don\u0027t think so.",
      "offset": 18
    }
  ],
  "location": "SigningUpSteps.enterComment(String)"
});
formatter.result({
  "duration": 173598835,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Off",
      "offset": 14
    }
  ],
  "location": "SigningUpSteps.setParticipationAndSubmit(String)"
});
formatter.result({
  "duration": 1271405672,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Dick",
      "offset": 21
    },
    {
      "val": "Off",
      "offset": 29
    }
  ],
  "location": "SigningUpSteps.verifyStatus(String,String)"
});
formatter.result({
  "duration": 134857412,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Dick",
      "offset": 15
    },
    {
      "val": "Naah, don\u0027t think so.",
      "offset": 24
    }
  ],
  "location": "SigningUpSteps.verifyComment(String,String)"
});
formatter.result({
  "duration": 604501060,
  "status": "passed"
});
formatter.after({
  "duration": 602501,
  "status": "passed"
});
formatter.after({
  "duration": 1988539285,
  "status": "passed"
});
formatter.after({
  "duration": 59304,
  "status": "passed"
});
formatter.before({
  "duration": 48588,
  "status": "passed"
});
formatter.before({
  "duration": 10586887,
  "status": "passed"
});
formatter.scenario({
  "line": 31,
  "name": "All respond to an event with comments",
  "description": "",
  "id": "sign-up-for-an-event;all-respond-to-an-event-with-comments;;4",
  "type": "scenario",
  "keyword": "Scenario Outline"
});
formatter.step({
  "line": 19,
  "name": "the group Friends exist with Tom, Dick, Harry",
  "keyword": "Given "
});
formatter.step({
  "line": 20,
  "name": "the group Friends has an event Dinner",
  "keyword": "And "
});
formatter.step({
  "line": 21,
  "name": "Harry navigates to his sign up page",
  "matchedColumns": [
    0
  ],
  "keyword": "When "
});
formatter.step({
  "line": 22,
  "name": "enters a comment \"Hmm, I don\u0027t know...\"",
  "matchedColumns": [
    1
  ],
  "keyword": "And "
});
formatter.step({
  "line": 23,
  "name": "submits he is Maybe",
  "matchedColumns": [
    2
  ],
  "keyword": "And "
});
formatter.step({
  "line": 24,
  "name": "the participation of Harry is Maybe",
  "matchedColumns": [
    0,
    2
  ],
  "keyword": "Then "
});
formatter.step({
  "line": 25,
  "name": "the comment by Harry is \"Hmm, I don\u0027t know...\"",
  "matchedColumns": [
    0,
    1
  ],
  "keyword": "And "
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Tom, Dick, Harry",
      "offset": 29
    }
  ],
  "location": "SigningUpSteps.createGroup(String,String\u003e)"
});
formatter.result({
  "duration": 1634970872,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Dinner",
      "offset": 31
    }
  ],
  "location": "SigningUpSteps.createEvent(String,String)"
});
formatter.result({
  "duration": 421314560,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Harry",
      "offset": 0
    }
  ],
  "location": "SigningUpSteps.navigateToSignUpPage(String)"
});
formatter.result({
  "duration": 784081817,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Hmm, I don\u0027t know...",
      "offset": 18
    }
  ],
  "location": "SigningUpSteps.enterComment(String)"
});
formatter.result({
  "duration": 183254073,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Maybe",
      "offset": 14
    }
  ],
  "location": "SigningUpSteps.setParticipationAndSubmit(String)"
});
formatter.result({
  "duration": 2073103390,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Harry",
      "offset": 21
    },
    {
      "val": "Maybe",
      "offset": 30
    }
  ],
  "location": "SigningUpSteps.verifyStatus(String,String)"
});
formatter.result({
  "duration": 133450629,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Harry",
      "offset": 15
    },
    {
      "val": "Hmm, I don\u0027t know...",
      "offset": 25
    }
  ],
  "location": "SigningUpSteps.verifyComment(String,String)"
});
formatter.result({
  "duration": 558490436,
  "status": "passed"
});
formatter.after({
  "duration": 444914,
  "status": "passed"
});
formatter.after({
  "duration": 1985521348,
  "status": "passed"
});
formatter.after({
  "duration": 39111,
  "status": "passed"
});
formatter.uri("simpleNavigation.feature");
formatter.feature({
  "line": 1,
  "name": "Simple navigation",
  "description": "",
  "id": "simple-navigation",
  "keyword": "Feature"
});
formatter.before({
  "duration": 45222,
  "status": "passed"
});
formatter.before({
  "duration": 8312311,
  "status": "passed"
});
formatter.scenario({
  "line": 3,
  "name": "The user requests the start page",
  "description": "",
  "id": "simple-navigation;the-user-requests-the-start-page",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 4,
  "name": "the user has a blank web browser",
  "keyword": "Given "
});
formatter.step({
  "line": 5,
  "name": "pointing the browser to the start page",
  "keyword": "When "
});
formatter.step({
  "line": 6,
  "name": "the start page should display",
  "keyword": "Then "
});
formatter.match({
  "location": "SimpleNavigationSteps.browseToBlank()"
});
formatter.result({
  "duration": 75570161,
  "status": "passed"
});
formatter.match({
  "location": "SimpleNavigationSteps.navigateToStartPage()"
});
formatter.result({
  "duration": 497198298,
  "status": "passed"
});
formatter.match({
  "location": "SimpleNavigationSteps.verifyOnStartPage()"
});
formatter.result({
  "duration": 106728091,
  "status": "passed"
});
formatter.after({
  "duration": 392095,
  "status": "passed"
});
formatter.after({
  "duration": 98046,
  "status": "passed"
});
formatter.after({
  "duration": 19448,
  "status": "passed"
});
formatter.before({
  "duration": 80036,
  "status": "passed"
});
formatter.before({
  "duration": 49963209,
  "status": "passed"
});
formatter.scenario({
  "line": 8,
  "name": "The home link should work",
  "description": "",
  "id": "simple-navigation;the-home-link-should-work",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 9,
  "name": "the user is on the start page",
  "keyword": "Given "
});
formatter.step({
  "line": 10,
  "name": "selecting the home link",
  "keyword": "When "
});
formatter.step({
  "line": 11,
  "name": "the start page should display",
  "keyword": "Then "
});
formatter.match({
  "location": "SimpleNavigationSteps.navigateToStartPage()"
});
formatter.result({
  "duration": 468251940,
  "status": "passed"
});
formatter.match({
  "location": "SimpleNavigationSteps.selectHomeLink()"
});
formatter.result({
  "duration": 349609090,
  "status": "passed"
});
formatter.match({
  "location": "SimpleNavigationSteps.verifyOnStartPage()"
});
formatter.result({
  "duration": 222475538,
  "status": "passed"
});
formatter.after({
  "duration": 409108,
  "status": "passed"
});
formatter.after({
  "duration": 130897,
  "status": "passed"
});
formatter.after({
  "duration": 25986,
  "status": "passed"
});
formatter.before({
  "duration": 82675,
  "status": "passed"
});
formatter.before({
  "duration": 49044525,
  "status": "passed"
});
formatter.scenario({
  "line": 13,
  "name": "The user selects Groups in the menu",
  "description": "",
  "id": "simple-navigation;the-user-selects-groups-in-the-menu",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 14,
  "name": "the group Friends exist with Tom, Dick, Harry",
  "keyword": "Given "
});
formatter.step({
  "line": 15,
  "name": "the user is on the start page",
  "keyword": "And "
});
formatter.step({
  "line": 16,
  "name": "selecting the Groups menu item",
  "keyword": "When "
});
formatter.step({
  "line": 17,
  "name": "the groups page should display",
  "keyword": "Then "
});
formatter.step({
  "line": 18,
  "name": "the group Friends is listed on the page",
  "keyword": "And "
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Tom, Dick, Harry",
      "offset": 29
    }
  ],
  "location": "SigningUpSteps.createGroup(String,String\u003e)"
});
formatter.result({
  "duration": 1570024668,
  "status": "passed"
});
formatter.match({
  "location": "SimpleNavigationSteps.navigateToStartPage()"
});
formatter.result({
  "duration": 440143873,
  "status": "passed"
});
formatter.match({
  "location": "SimpleNavigationSteps.selectGroupsMenuItem()"
});
formatter.result({
  "duration": 420581399,
  "status": "passed"
});
formatter.match({
  "location": "SimpleNavigationSteps.verifyOnGroupsPage()"
});
formatter.result({
  "duration": 231458044,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    }
  ],
  "location": "SimpleNavigationSteps.verifyGroupListed(String)"
});
formatter.result({
  "duration": 94098570,
  "status": "passed"
});
formatter.after({
  "duration": 270561,
  "status": "passed"
});
formatter.after({
  "duration": 2033237936,
  "status": "passed"
});
formatter.after({
  "duration": 58283,
  "status": "passed"
});
formatter.before({
  "duration": 55066,
  "status": "passed"
});
formatter.before({
  "duration": 8316309,
  "status": "passed"
});
formatter.scenario({
  "line": 20,
  "name": "The user clicks on a group on the groups page",
  "description": "",
  "id": "simple-navigation;the-user-clicks-on-a-group-on-the-groups-page",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 21,
  "name": "the group Friends exist with Tom, Dick, Harry",
  "keyword": "Given "
});
formatter.step({
  "line": 22,
  "name": "the user is on the groups page",
  "keyword": "And "
});
formatter.step({
  "line": 23,
  "name": "selecting the Friends group",
  "keyword": "When "
});
formatter.step({
  "line": 24,
  "name": "the group Friends page should display",
  "keyword": "Then "
});
formatter.step({
  "line": 25,
  "name": "members Tom, Dick, Harry is listed on the page",
  "keyword": "And "
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Tom, Dick, Harry",
      "offset": 29
    }
  ],
  "location": "SigningUpSteps.createGroup(String,String\u003e)"
});
formatter.result({
  "duration": 1554313698,
  "status": "passed"
});
formatter.match({
  "location": "SimpleNavigationSteps.navigateToGroupsPage()"
});
formatter.result({
  "duration": 454797086,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 14
    }
  ],
  "location": "SimpleNavigationSteps.selectGroup(String)"
});
formatter.result({
  "duration": 549980882,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    }
  ],
  "location": "SimpleNavigationSteps.verifyViewingGroup(String)"
});
formatter.result({
  "duration": 226298826,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Tom, Dick, Harry",
      "offset": 8
    }
  ],
  "location": "SimpleNavigationSteps.membersListed(String\u003e)"
});
formatter.result({
  "duration": 167374677,
  "status": "passed"
});
formatter.after({
  "duration": 358620,
  "status": "passed"
});
formatter.after({
  "duration": 2004814706,
  "status": "passed"
});
formatter.after({
  "duration": 46318,
  "status": "passed"
});
formatter.before({
  "duration": 69739,
  "status": "passed"
});
formatter.before({
  "duration": 9486157,
  "status": "passed"
});
formatter.scenario({
  "line": 27,
  "name": "The user clicks on a user on the groups page",
  "description": "",
  "id": "simple-navigation;the-user-clicks-on-a-user-on-the-groups-page",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 28,
  "name": "the group Friends exist with Tom, Dick, Harry",
  "keyword": "Given "
});
formatter.step({
  "line": 29,
  "name": "the user is on the group Friends page",
  "keyword": "And "
});
formatter.step({
  "line": 30,
  "name": "selecting the member Dick",
  "keyword": "When "
});
formatter.step({
  "line": 31,
  "name": "the user page for Dick should display",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 10
    },
    {
      "val": "Tom, Dick, Harry",
      "offset": 29
    }
  ],
  "location": "SigningUpSteps.createGroup(String,String\u003e)"
});
formatter.result({
  "duration": 1602790088,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Friends",
      "offset": 25
    }
  ],
  "location": "SimpleNavigationSteps.navigateToGroup(String)"
});
formatter.result({
  "duration": 809700510,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Dick",
      "offset": 21
    }
  ],
  "location": "SimpleNavigationSteps.selectMember(String)"
});
formatter.result({
  "duration": 425480310,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Dick",
      "offset": 18
    }
  ],
  "location": "SimpleNavigationSteps.verifyViewingUser(String)"
});
formatter.result({
  "duration": 576215757,
  "status": "passed"
});
formatter.after({
  "duration": 348174,
  "status": "passed"
});
formatter.after({
  "duration": 2025354216,
  "status": "passed"
});
formatter.after({
  "duration": 34251,
  "status": "passed"
});
});