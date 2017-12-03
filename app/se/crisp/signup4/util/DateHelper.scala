package se.crisp.signup4.util

import java.text.SimpleDateFormat
import java.util.Date


object DateHelper {

  def DATE = "yyyy-MM-dd"
  def DAY = "EEEE"
  def TIME = "HH:mm"
  def DATE_TIME: String = DAY + " " + DATE + ", " + TIME

  def US_DATE = "MM/dd/yyyy"


  private def formatted(date: Date, format: String): String = {
    new SimpleDateFormat(format).format(date)
  }

  def asDate(date: Date): String = formatted(date, DATE)

  def asDayAndDate(date: Date): String = formatted(date, DAY + " " + DATE)

  def asUsDate(date: Date): String = formatted(date, US_DATE)

  def asTime(date: Date): String = formatted(date, TIME)

  def asDateTime(date: Date): String = formatted(date, DATE_TIME)

  def sameDay(date1: Date, date2: Date): Boolean = {
    asDate(date1).equals(asDate(date2))
  }
}