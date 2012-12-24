package util

import java.util.{TimeZone, Date}
import java.text.SimpleDateFormat

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
}