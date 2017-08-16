package com.pricecheck.app

import com.slackpricecheck.slack.Bot

object App {

  def main(args: Array[String]): Unit = {
    val slack_client: Client = new SlackClient()
    val itad_token: String = sys.env("ITAD_TOKEN")
    Bot bot = new Bot(slack_client, itad_token)

  }
}
