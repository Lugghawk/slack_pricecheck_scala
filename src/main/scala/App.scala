package com.pricecheck.app

import com.slackpricecheck.slack.Bot
import com.pricecheck.client.{SlackClient, Client}
import com.slackpricecheck.itad._

object App {

  def main(args: Array[String]): Unit = {
    val slack_client: Client = new SlackClient()
    val itad_token: String = sys.env("ITAD_TOKEN")
    val itad_client: ITAD = ITAD(itad_token)
    val bot:Bot = new Bot(slack_client, itad_client)

  }
}
