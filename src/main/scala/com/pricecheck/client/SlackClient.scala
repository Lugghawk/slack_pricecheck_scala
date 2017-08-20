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
      f(new SlackMessage(message))
    })
  }

  def self(): String = selfId
}

class SlackMessage(message: slack.models.Message) extends Message {
  val text: String = message.text
  val origin: String = message.channel
}
