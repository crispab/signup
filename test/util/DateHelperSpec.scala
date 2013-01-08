package util

import org.specs2.mutable._
import java.text.SimpleDateFormat
import java.util.TimeZone
import play.Logger

class DateHelperSpec extends Specification {

  def theDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-01-14 17:00")

  "DateHelper" should {

    "handle simple case" in {
      DateHelper.asUtcDateTime(theDate) must beEqualTo("20130114T160000Z")
    }

    "list time zones" in {
      val tzids = TimeZone.getAvailableIDs.sorted
      tzids.map {tzid => Logger.debug(tzid)}
      1 must beGreaterThan(0)
    }

  }

}
