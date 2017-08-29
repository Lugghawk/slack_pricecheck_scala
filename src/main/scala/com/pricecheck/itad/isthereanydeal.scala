package com.pricecheck.itad

import scala.concurrent.{Future, ExecutionContext}
import play.api.libs.json._
import play.api.libs.json.Reads._ // Custom validation helpers
import com.netaporter.uri.dsl._
import play.api.libs.functional.syntax._ // Combinator syntax
import com.pricecheck.http.HttpClient


case class ITAD(token: String, httpClient: HttpClient)(implicit ec: ExecutionContext){


  def getLowestPrice(gameName: String): Future[Price] = {
    val gamePlain = getPlain(gameName);
    val priceList :Future[List[Price]] = gamePlain flatMap {
      case Some(plain) => prices(plain)
      case None => throw new Exception (s"No price found for $gameName")
    }

    priceList map {
      _.min
    }
  }

  def prices(gamePlain: String): Future[List[Price]] = {
    val pricesHtmlFuture = pricesHtml(gamePlain)
    val pricesJson = for (p <- pricesHtmlFuture) yield Json.parse(p)
    for (p <- pricesJson) yield (p \ "data" \ gamePlain \ "list" ).as[List[Price]]
  }

  def pricesHtml(gamePlain: String): Future[String] = {
    val pricesUrl = "https://api.isthereanydeal.com/v01/game/prices" ? ("key" -> token) & ("plains" -> gamePlain) & ("country" -> "CA")
    getUrlAsString(pricesUrl)
  }

  def getPlain(gameTitle: String): Future[Option[String]] = {
    val gamePlainHtmlFuture = gamePlainHtml(gameTitle)
    val plain = for (p <- gamePlainHtmlFuture) yield Json.parse(p)
    for (p <- plain) yield (p \ "data" \ "plain").asOpt[String]
  }

  def gamePlainHtml(title: String): Future[String] = {
    val plainUrl = "https://api.isthereanydeal.com/v02/game/plain/" ? ("key" -> token) & ("title" -> title)
    getUrlAsString(plainUrl)
  }

  def getUrlAsString(urlToFetch: String): Future[String] = {
    httpClient.getUrlAsString(urlToFetch)
  }

}

object Shop {
  implicit val shopReader: Reads[Shop] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "name").read[String]
  )(Shop.apply _)
}
case class Shop(id: String, name: String)

object Price {
  implicit val ord: Ordering[Price] = Ordering.by(_.price_new)
  implicit val priceReader: Reads[Price] = (
    (JsPath \ "price_new").read[Double] and
    (JsPath \ "price_old").read[Double] and
    (JsPath \ "price_cut").read[Double] and
    (JsPath \ "url").read[String] and
    (JsPath \ "shop").read[Shop]
  )(Price.apply _)
}

case class Price(
  price_new: Double,
  price_old: Double,
  price_cut: Double,
  url: String,
  shop: Shop
)
