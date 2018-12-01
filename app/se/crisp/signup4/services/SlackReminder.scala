package se.crisp.signup4.services

import javax.inject.{Inject, Singleton}

import play.api.i18n.Messages
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.{Configuration, Logger}
import se.crisp.signup4.models.dao.LogEntryDAO
import se.crisp.signup4.models.{User, _}
import se.crisp.signup4.util.HtmlHelper


@Singleton
class SlackReminder @Inject() (val logEntryDAO: LogEntryDAO,
                               val slackremindermessageView: se.crisp.signup4.views.txt.events.slackremindermessage,
                               val slackcancellationmessageView: se.crisp.signup4.views.txt.events.slackcancellationmessage,
                               val slackupdatedparticipationmessageView: se.crisp.signup4.views.txt.events.slackupdatedparticipationmessage,
                               val configuration: Configuration,
                               val wSClient: WSClient) (implicit val htmlHelper: HtmlHelper) {

  private val ANONYMOUS = User(firstName = "John", lastName = "Doe", email = "")

  private def sendMessage(event: Event, message: JsValue)(implicit loggedIn: User, messages: Messages)  {
    val slackChannelURL = configuration.getOptional[String]("slack.channel.url")
    if(slackChannelURL.isDefined) {
      try {
        wSClient.url(slackChannelURL.get).post(message)
      } catch {
        case ex: Exception =>
          Logger.error("FAILED sending Slack message: " + message, ex)
          logEntryDAO.create(event, Messages("slack.failedreminder", ex.getClass.getSimpleName, ex.getMessage))

      }
      if(loggedIn != ANONYMOUS) {
        logEntryDAO.create(event, Messages("slack.sentreminders", loggedIn.name))
      }
    }
  }

  private def createReminderMessage(event: Event)(implicit  messages: Messages) = {
    val baseUrl = htmlHelper.baseUrl
    Json.parse(slackremindermessageView(event, baseUrl).toString())
  }

  def sendReminderMessage(event: Event)(implicit loggedIn: User, messages: Messages)  {
    sendMessage(event, createReminderMessage(event))
  }

  private def createCancellationMessage(event: Event)(implicit  messages: Messages) = {
    val baseUrl = htmlHelper.baseUrl
    Json.parse(slackcancellationmessageView(event, baseUrl).toString())
  }

  def sendCancellationMessage(event: Event)(implicit loggedIn: User, messages: Messages) {
    sendMessage(event, createCancellationMessage(event))
  }

  private def createUpdatedParticipationMessage(participation: Participation)(implicit  messages: Messages) = {
    val baseUrl = htmlHelper.baseUrl
    Json.parse(slackupdatedparticipationmessageView(participation.event, participation, baseUrl).toString())
  }

  def sendUpdatedParticipationMessage(participation: Participation)(implicit  messages: Messages) {
    sendMessage(participation.event, createUpdatedParticipationMessage(participation))(loggedIn = ANONYMOUS, messages)
  }

}
