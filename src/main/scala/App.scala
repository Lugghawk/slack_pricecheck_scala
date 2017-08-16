package com.pricecheck.app

import com.slackpricecheck.slack.Bot
import com.pricecheck.client.{SlackClient, Client}

object App {

  def main(args: Array[String]): Unit = {
    val slack_client: Client = new SlackClient()
    val itad_token: String = sys.env("ITAD_TOKEN")
    val bot:Bot = new Bot(slack_client, itad_token)

  }
}
