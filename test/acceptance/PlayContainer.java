package acceptance;

import cucumber.api.java.Before;
import play.Play;
import play.test.TestServer;

import static play.test.Helpers.testServer;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.start;

public class PlayContainer {
  public static int LOCAL_PORT = 3333;
  private static TestServer testServerForDbAccess;
  private static boolean initialised = false;

  @Before
  public void before() {
    if (!initialised) {
      testServerForDbAccess = testServer(LOCAL_PORT, fakeApplication());
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

  public static String getBaseUrl() {
    return  Play.application().configuration().getString("application.base.url");
  }
}
