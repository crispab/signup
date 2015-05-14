package util

import java.text.SimpleDateFormat
import java.util.Date


object DateHelper {

  def DATE = "yyyy-MM-dd"
  def US_DATE = "MM/dd/yyyy"

  def TIME = "HH:mm"

  def DATE_TIME = DATE + ", " + TIME

  private def formatted(date: Date, format: String): String = {
    new SimpleDateFormat(format).format(date)
  }

  def asDate(date: Date): String = formatted(date, DATE)

  def asUsDate(date: Date): String = formatted(date, US_DATE)

  def asTime(date: Date): String = formatted(date, TIME)

  def asDateTime(date: Date): String = formatted(date, DATE_TIME)

  def sameDay(date1: Date, date2: Date) = {
    asDate(date1).equals(asDate(date2))
  }
}