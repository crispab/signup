package se.crisp.signup4.util

import se.crisp.signup4.models.Participation
import se.crisp.signup4.models.Status._
import StatusHelper._
import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFWorkbook}
import play.api.i18n.Messages

object ExcelHelper {

  def createWorkbook(guests: Seq[Participation], members: Seq[Participation]): XSSFWorkbook = {
    val workbook = new XSSFWorkbook()
    val sheet = workbook.createSheet(Messages("excel.signups"))
    createHeading(workbook, sheet)
    populateWithInvitedStatus(sheet, guests, areGuests = true)
    populateWithInvitedStatus(sheet, members, startRow = guests.size + 1)
    autosizeAllColumns(sheet)
    workbook
  }

  private def createHeading(workbook: XSSFWorkbook, sheet: XSSFSheet) {
    val heading = sheet.createRow(0)
    heading.createCell(0).setCellValue(Messages("excel.firsname"))
    heading.createCell(1).setCellValue(Messages("excel.lastname"))
    heading.createCell(2).setCellValue(Messages("excel.email"))
    heading.createCell(3).setCellValue(Messages("excel.status"))
    heading.createCell(4).setCellValue(Messages("excel.people"))
    heading.createCell(5).setCellValue(Messages("excel.date"))
    heading.createCell(6).setCellValue(Messages("excel.guest.heading"))
    heading.createCell(7).setCellValue(Messages("excel.late.heading"))
    heading.createCell(8).setCellValue(Messages("excel.comment"))

    val headingFont = workbook.createFont()
    headingFont.setBold(true)
    val headingStyle = workbook.createCellStyle()
    headingStyle.setFont(headingFont)

    heading.getCell(0).setCellStyle(headingStyle)
    heading.getCell(1).setCellStyle(headingStyle)
    heading.getCell(2).setCellStyle(headingStyle)
    heading.getCell(3).setCellStyle(headingStyle)
    heading.getCell(4).setCellStyle(headingStyle)
    heading.getCell(5).setCellStyle(headingStyle)
    heading.getCell(6).setCellStyle(headingStyle)
    heading.getCell(7).setCellStyle(headingStyle)
    heading.getCell(8).setCellStyle(headingStyle)
  }

  private def populateWithInvitedStatus(sheet: XSSFSheet, invited: Seq[Participation], startRow: Int = 1, areGuests: Boolean = false) {
    var rowNumber = startRow

    val workbook = sheet.getWorkbook
    val createHelper = workbook.getCreationHelper
    val dateStyle = workbook.createCellStyle()
    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm;@"))

    for (participation <- invited) {
      val row = sheet.createRow(rowNumber)
      row.createCell(0).setCellValue(participation.user.firstName)
      row.createCell(1).setCellValue(participation.user.lastName)
      row.createCell(2).setCellValue(participation.user.email)
      row.createCell(3).setCellValue(asMessage(participation.status))
      row.createCell(4).setCellValue(participation.participantsComing)
      if (participation.status != Unregistered && participation.signUpTime.isDefined) {
        val cell = row.createCell(5)
        cell.setCellStyle(dateStyle)
        cell.setCellValue(participation.signUpTime.get)
      }
      if (areGuests) {
        row.createCell(6).setCellValue(Messages("excel.guest"))
      }
      if (participation.isLateSignUp) {
        row.createCell(7).setCellValue(Messages("excel.late"))
      }
      row.createCell(8).setCellValue(participation.comment)
      rowNumber += 1
    }
  }

  private def autosizeAllColumns(sheet: XSSFSheet) {
    sheet.autoSizeColumn(0)
    sheet.autoSizeColumn(1)
    sheet.autoSizeColumn(2)
    sheet.autoSizeColumn(3)
    sheet.autoSizeColumn(4)
    sheet.autoSizeColumn(5)
    sheet.autoSizeColumn(6)
    sheet.autoSizeColumn(7)
    sheet.autoSizeColumn(8)
  }
}
