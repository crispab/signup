package util

import java.util.Date

import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.{ClientProtocolException, ResponseHandler}
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.joda.time.DateTime
import play.api.Logger

import scala.util.Random

object TestHelper {

  private val POSTGRESSION_URL = "http://api.postgression.com"
  private val POSTGRESSION_SSL_CONF = "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"

  lazy val postgressionDb = getNewPostgressionDb

  /**
   * Requests a temporary (30 minutes) integration test database to be created at http://www.postgression.com
   */
  def getNewPostgressionDb = {
    /*
     * Can't use Play's own WS API since it requires an implicit Application object to be present
     * and this method is called during test setup while the FakeApplication object is being created.
     * It's a chicken and egg problem, so I'm using a different species instead: Apache HttpComponents
     */
    val httpClient = HttpClients.createDefault()
    try {
      val httpGet = new HttpGet(POSTGRESSION_URL)

      val responseHandler = new ResponseHandler[String]() {
        override def handleResponse(response: HttpResponse): String = {
          val status = response.getStatusLine.getStatusCode
          if (status >= 200 && status < 300) {
            Option(response.getEntity) match {
              case Some(entity) => EntityUtils.toString(entity)
              case None => "NO DB URL RECEIVED FROM POSTGRESSION API!"
            }
          } else {
            throw new ClientProtocolException("Unexpected response status: " + status)
          }
        }
      }
      val dbUrl = httpClient.execute(httpGet, responseHandler)
      Logger.debug("dbUrl = " + dbUrl)

      dbUrl + POSTGRESSION_SSL_CONF
    } finally {
      httpClient.close()
    }
  }

  lazy val testId = Random.alphanumeric.take(8).mkString

  def withTestId(string: String): String = {testId + string}

  def morningStart: Date = {
    DateTime.now.plusWeeks(2).withHourOfDay(9).withMinuteOfHour(0).toDate
  }

  def morningEnd: Date = {
    DateTime.now.plusWeeks(2).withHourOfDay(11).withMinuteOfHour(0).toDate
  }
}
