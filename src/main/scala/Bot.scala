package com.slackpricecheck.slack

import slack.models.{Hello, Message}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import akka.actor.ActorSystem
import com.pricecheck.client.{Client, SlackClient}
import com.slackpricecheck.itad._

class Bot {
  implicit val system = ActorSystem("slack")

  var client: Client = _
  var itad: ITAD = _

  var selfId: String = _
  def connect(connect_client: Client, itad_token: String): Unit = {
    client = new SlackClient()
    selfId = client.self()
    itad = ITAD(itad_token)
  }

  def this(client: Client, itad_token: String) = {
    this()
    connect(client, itad_token)
    client.onMessage { message =>
      if (shouldRespond(message.text)){
        val game = message.text.replaceAll(s"<@$selfId>","").trim()
        val lowestPriceFuture = itad.getLowestPrice(game)

        lowestPriceFuture onSuccess {
          case price =>
            val lowestPrice = price
            val lowest_price_fmt = "%1.2f" format lowestPrice.price_new
            client.sendMessage(message.origin, s"Found lowest price of $$${lowest_price_fmt} at ${lowestPrice.shop.name}  (${lowestPrice.url})")
        }

        lowestPriceFuture onFailure {
          case exception =>
            client.sendMessage(message.origin, s"Could not find lowest price for $game.")
        }
      }
    }
  }

  def shouldRespond(message: String): Boolean = message match {
    case msg if msg.startsWith(s"<@$selfId>") => true
    case _ => false
  }
}
