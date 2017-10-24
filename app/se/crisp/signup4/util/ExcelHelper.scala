package se.crisp.signup4.util

import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFWorkbook}
import play.api.i18n.Messages
import se.crisp.signup4.models.Participation
import se.crisp.signup4.models.Status._
import se.crisp.signup4.util.StatusHelper._

object ExcelHelper {

  def createWorkbook(guests: Seq[Participation], members: Seq[Participation])(implicit messages: Messages): XSSFWorkbook = {
    val workbook = new XSSFWorkbook()
    val sheet = workbook.createSheet(Messages("excel.signups"))
    createHeading(workbook, sheet)
    populateWithInvitedStatus(sheet, guests, areGuests = true)
    populateWithInvitedStatus(sheet, members, startRow = guests.size + 1)
    autosizeAllColumns(sheet)
    workbook
  }

  private def createHeading(workbook: XSSFWorkbook, sheet: XSSFSheet)(implicit messages: Messages) {
    val heading = sheet.createRow(0)
    heading.createCell(0).setCellValue(Messages("excel.firsname"))
    heading.createCell(1).setCellValue(Messages("excel.lastname"))
    heading.createCell(2).setCellValue(Messages("excel.description"))
    heading.createCell(3).setCellValue(Messages("excel.email"))
    heading.createCell(4).setCellValue(Messages("excel.status"))
    heading.createCell(5).setCellValue(Messages("excel.people"))
    heading.createCell(6).setCellValue(Messages("excel.date"))
    heading.createCell(7).setCellValue(Messages("excel.guest.heading"))
    heading.createCell(8).setCellValue(Messages("excel.late.heading"))
    heading.createCell(9).setCellValue(Messages("excel.comment"))

    val headingFont = workbook.createFont()
    headingFont.setBold(true)
    val headingStyle = workbook.createCellStyle()
    headingStyle.setFont(headingFont)

    for (i <- 0 to 9) {
      heading.getCell(i).setCellStyle(headingStyle)
    }
  }

  private def populateWithInvitedStatus(sheet: XSSFSheet, invited: Seq[Participation], startRow: Int = 1, areGuests: Boolean = false)(implicit messages: Messages) {
    var rowNumber = startRow

    val workbook = sheet.getWorkbook
    val createHelper = workbook.getCreationHelper
    val dateStyle = workbook.createCellStyle()
    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm;@"))

    for (participation <- invited) {
      val row = sheet.createRow(rowNumber)
      row.createCell(0).setCellValue(participation.user.firstName)
      row.createCell(1).setCellValue(participation.user.lastName)
      row.createCell(2).setCellValue(participation.user.comment)
      row.createCell(3).setCellValue(participation.user.email)
      row.createCell(4).setCellValue(asMessage(participation.status))
      row.createCell(5).setCellValue(participation.participantsComing)
      if (participation.status != Unregistered && participation.signUpTime.isDefined) {
        val cell = row.createCell(6)
        cell.setCellStyle(dateStyle)
        cell.setCellValue(participation.signUpTime.get)
      }
      if (areGuests) {
        row.createCell(7).setCellValue(Messages("excel.guest"))
      }
      if (participation.isLateSignUp) {
        row.createCell(8).setCellValue(Messages("excel.late"))
      }
      row.createCell(9).setCellValue(participation.comment)
      rowNumber += 1
    }
  }

  private def autosizeAllColumns(sheet: XSSFSheet)(implicit messages: Messages) {
    for (i <- 0 to 9) {
      sheet.autoSizeColumn(i)
    }
  }
}
