package com.pricecheck.app

import akka.actor.ActorSystem
import com.pricecheck.bot.Bot
import com.pricecheck.client._
import com.pricecheck.itad._
import com.pricecheck.http._

object Main extends App{
  implicit val system = ActorSystem("bot")
  implicit val execution_context = system.dispatchers.lookup("blocking-io-dispatcher")
  val slack_client: Client = new SlackClientBuilder(sys.env("SLACK_TOKEN")).connect()
  val itad_token: String = sys.env("ITAD_TOKEN")
  val http_client: HttpClient = DispatchHttpClient()
  val itad_client: ITAD = ITAD(itad_token, http_client)
  val bot:Bot = new Bot(slack_client, itad_client)
  bot.run()
}
