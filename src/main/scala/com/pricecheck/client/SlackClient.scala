package com.pricecheck.client

import slack.api.SlackApiClient
import slack.rtm.SlackRtmClient
import akka.actor.ActorSystem
import scala.concurrent.Future

class SlackClientBuilder(token: String)(implicit system: ActorSystem) extends ClientBuilder {
  def connect(): Client = {
    val client = SlackApiClient(token)
    val rtmClient = SlackRtmClient(token)
    new SlackClient(client, rtmClient)
  }
}

class SlackClient(api_client: SlackApiClient, rtm_client: SlackRtmClient) extends Client {

  var selfId : String = rtm_client.state.self.id

  def sendMessage(target: String, message: String): Future[Long] = {
    rtm_client.sendMessage(target, message)
  }

  def onMessage(f: (Message) => Unit): Unit = {
    rtm_client.onMessage( message => {
      f(SlackMessage(message))
    })
  }

  def self(): String = selfId
}


object SlackMessage {
  def apply(message: slack.models.Message): SlackMessage = {
    SlackMessage(message.text, message.channel)
  }
}

final case class SlackMessage(text: String, origin: String) extends Message
