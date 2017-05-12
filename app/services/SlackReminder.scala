package services

import models._
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WS
import play.api.Play.current
import play.api.i18n.Messages


object SlackReminder {

  private val ANONYMOUS = new User(firstName = "John", lastName = "Doe", email = "")

  private def sendMessage(event: Event, message: JsValue)(implicit loggedIn: User)  {
    val slackChannelURL = play.api.Play.configuration.getString("slack.channel.url")
    if(slackChannelURL.isDefined) {
      try {
        WS.url(slackChannelURL.get).post(message)
      } catch {
        case ex: Exception =>
          Logger.error("FAILED sending Slack message: " + message, ex)
          LogEntry.create(event, Messages("slack.failedreminder", ex.getClass.getSimpleName, ex.getMessage))

      }
      if(loggedIn != ANONYMOUS) {
        LogEntry.create(event, Messages("slack.sentreminders", loggedIn.name))
      }
    }
  }

  private def createReminderMessage(event: Event) = {
    val baseUrl = play.api.Play.configuration.getString("application.base.url").getOrElse("")
    Json.parse(views.txt.events.slackremindermessage(event, baseUrl).toString())
  }

  def sendReminderMessage(event: Event)(implicit loggedIn: User)  {
    sendMessage(event, createReminderMessage(event))
  }

  private def createCancellationMessage(event: Event) = {
    val baseUrl = play.api.Play.configuration.getString("application.base.url").getOrElse("")
    Json.parse(views.txt.events.slackcancellationmessage(event, baseUrl).toString())
  }

  def sendCancellationMessage(event: Event)(implicit loggedIn: User) {
    sendMessage(event, createCancellationMessage(event))
  }

  private def createUpdatedParticipationMessage(participation: Participation) = {
    val baseUrl = play.api.Play.configuration.getString("application.base.url").getOrElse("")
    Json.parse(views.txt.events.slackupdatedparticipationmessage(participation.event, participation, baseUrl).toString())
  }

  def sendUpdatedParticipationMessage(participation: Participation) {
    sendMessage(participation.event, createUpdatedParticipationMessage(participation))(loggedIn = ANONYMOUS)
  }

}
