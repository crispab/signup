package services

import models._
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.Play.current


object SlackReminder {

  def createReminderMessage(event: Event) = {
    val baseUrl = play.api.Play.configuration.getString("application.base.url").getOrElse("")
    Json.parse(views.txt.events.message(event, baseUrl).toString())
  }

  def remindListeners(event: Event) {
    val slackChannelURL = play.api.Play.configuration.getString("slack.channel.url")
    if(slackChannelURL.isDefined) {
      Logger.debug("Sending chat reminder for: " + event.name)
      try {
        WS.url(slackChannelURL.get).post(createReminderMessage(event))
      } catch {
        case ex: Exception => {
          Logger.error("FAILED sending Slack message for " + event.name, ex)
          LogEntry.create(event, "Misslyckades att skicka påminnelse (chattmeddelande) på Slack. " + ex.getClass.getSimpleName + ": " + ex.getMessage)
        }
      }
      LogEntry.create(event, "Skickat påminnelse (chattmeddelande) på Slack")
    }
  }

  def sendCancellationMessage(event: Event)= {
    // TODO: implement this
    throw new NotImplementedError()
  }

}
