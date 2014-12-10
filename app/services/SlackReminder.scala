package services

import models._
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WS
import play.api.Play.current


object SlackReminder {

  private def sendMessage(event: Event, message: JsValue) {
    val slackChannelURL = play.api.Play.configuration.getString("slack.channel.url")
    if(slackChannelURL.isDefined) {
      try {
        WS.url(slackChannelURL.get).post(message)
      } catch {
        case ex: Exception => {
          Logger.error("FAILED sending Slack message: " + message, ex)
          LogEntry.create(event, "Misslyckades att skicka chattmeddelande på Slack. " + ex.getClass.getSimpleName + ": " + ex.getMessage)
        }
      }
      LogEntry.create(event, "Skickat chattmeddelande på Slack")
    }
  }

  private def createReminderMessage(event: Event) = {
    val baseUrl = play.api.Play.configuration.getString("application.base.url").getOrElse("")
    Json.parse(views.txt.events.slacknotificationmessage(event, baseUrl).toString())
  }

  def sendReminderMessage(event: Event) {
    sendMessage(event, createReminderMessage(event))
  }

  private def createCancellationMessage(event: Event) = {
    val baseUrl = play.api.Play.configuration.getString("application.base.url").getOrElse("")
    Json.parse(views.txt.events.slackcancellationmessage(event, baseUrl).toString())
  }

  def sendCancellationMessage(event: Event)= {
    sendMessage(event, createCancellationMessage(event))
  }

}
