package acceptance.steps;

import cucumber.api.java.Before;
import play.test.TestServer;

import java.util.HashMap;
import java.util.Map;

import static play.test.Helpers.testServer;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.start;

public class GlobalHooks {
  public static int LOCAL_PORT = 3333;
  private static TestServer testServerForDbAccess;
  private static boolean initialised = false;

  @Before
  public void before() {
    if (!initialised) {

      // TODO: make database url configurable (point to CI-test install)
      Map<String, String> additionalConfiguration = new HashMap<>();
      //additionalConfiguration.put("db.default.url", TestHelper.postgressionDb());

      testServerForDbAccess = testServer(LOCAL_PORT, fakeApplication(additionalConfiguration));
      start(testServerForDbAccess);
      initialised = true;
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          testServerForDbAccess.stop();
        }
      });
    }
  }
}
