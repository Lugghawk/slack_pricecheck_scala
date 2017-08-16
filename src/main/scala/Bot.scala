package com.slackpricecheck.slack

import slack.models.{Hello, Message}
import scala.concurrent.Future
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
  def connect(connect_client: Client, itad_client: ITAD): Unit = {
    client = new SlackClient()
    selfId = client.self()
    itad = itad_client
  }

  def this(chat_client: Client, itad_client: ITAD) = {
    this()
    connect(chat_client, itad_client)
    client.onMessage { message =>
      if (shouldRespond(message.text)){
        val game = gameName(message.text)
        val lowestPriceFuture = fetchBestPriceForGame(game)

        lowestPriceFuture onSuccess {
          case price =>
            respondWithPrice(message.origin, client, price)
        }

        lowestPriceFuture onFailure {
          case exception =>
            client.sendMessage(message.origin, s"Could not find lowest price for $game.")
        }
      }
    }
  }

  def respondWithPrice(target: String, client: Client, price: Price):Unit = {
    val lowestPriceMessage = formatPriceMessage(price)
    client.sendMessage(target, lowestPriceMessage)
  }

  /**
   * Move to Price class
   */
  def formatPriceMessage(price: Price): String = {
    val formatted_price = "%1.2f" format price.price_new
    s"Found lowest price of $$${formatted_price} at ${price.shop.name} (${price.url})"
  }

  def fetchBestPriceForGame(game: String): Future[Price] = {
    itad.getLowestPrice(game)
  }

  /**
   * An incoming message to the bot has the format "<@bot> myGame"
   * This strips the <@bot> and returns 'myGame'
   */
  def gameName(message: String): String = {
    message.replaceAll(s"<@$selfId>", "").trim()
  }

  /**
   * Determines if a message is directed at the bot. If so, we should respond
   */
  def shouldRespond(message: String): Boolean = message match {
    case msg if msg.startsWith(s"<@$selfId>") => true
    case _ => false
  }
}
