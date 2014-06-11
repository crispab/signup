package util

import java.util.{TimeZone, Date}
import java.text.SimpleDateFormat
import org.joda.time.Days
import org.joda.time.DateMidnight


object DateHelper {

  def DATE = "yyyy-MM-dd"

  def TIME = "HH:mm"

  def DATE_TIME = DATE + ", " + TIME

  private def formatted(date: Date, format: String): String = {
    new SimpleDateFormat(format).format(date)
  }

  def asDate(date: Date): String = formatted(date, DATE)

  def asTime(date: Date): String = formatted(date, TIME)

  def asDateTime(date: Date): String = formatted(date, DATE_TIME)

  def asUtcDateTime(date: Date): String = {
    val utcFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
    utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
    utcFormat.format(date)
  }

  def sameDay(date1: Date, date2: Date) = {
    asDate(date1).equals(asDate(date2))
  }

  def daysBetween(date1: Date, date2: Date) = {
    Days.daysBetween(new DateMidnight(date1), new DateMidnight(date2)).getDays
  }

}