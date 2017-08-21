package com.pricecheck.itad

import dispatch._
import scala.concurrent.{Future, ExecutionContext}
import play.api.libs.json._
import play.api.libs.json.Reads._ // Custom validation helpers
import com.netaporter.uri.dsl._
import play.api.libs.functional.syntax._ // Combinator syntax


case class ITAD(token: String)(implicit ec: ExecutionContext){


  def getLowestPrice(gameName: String): Future[Price] = {
    val gamePlain = getPlain(gameName);
    gamePlain map {
        case Some(plain) => prices(plain).min
        case None => throw new Exception (s"No price found for $gameName")
    }
  }

  def prices(gamePlain: String): List[Price] = {
    val pricesUrl = "https://api.isthereanydeal.com/v01/game/prices" ? ("key" -> token) & ("plains" -> gamePlain) & ("country" -> "CA")
    val svc = url(pricesUrl)
    val pricesHtml = Http(svc OK as.String)
    val pricesJson = Json.parse(pricesHtml())
    val pricesList = (pricesJson \ "data" \ gamePlain \ "list" ).as[List[Price]]
    return pricesList
  }

  def getPlain(gameTitle: String): Future[Option[String]] = {
    val plainUrl = "https://api.isthereanydeal.com/v02/game/plain/" ? ("key" -> token) & ("title" -> gameTitle)
    val svc = url(plainUrl)
    val gamePlain = Http(svc OK as.String)
    val plain = for (p <- gamePlain) yield Json.parse(p)
    for (p <- plain) yield (p \ "data" \ "plain").asOpt[String]
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
