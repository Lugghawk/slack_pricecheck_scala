package com.pricecheck.itad

import dispatch._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future}
import play.api.libs.json._
import com.netaporter.uri.dsl._


object ITAD {
  def apply(token: String): ITAD = {
    new ITAD(token)
  }
}

class ITAD(token: String) {

  implicit val shopReader = new Reads[Shop] {
    def reads(js: JsValue): JsResult[Shop] = {
      JsSuccess(Shop(
        (js \ "id").as[String],
        (js \ "name").as[String]
      ))
    }

  }

  implicit val priceReader = new Reads[Price] {
    def reads(js: JsValue): JsResult[Price] = {
      JsSuccess(Price(
        (js \ "price_new").as[Double],
        (js \ "price_old").as[Double],
        (js \ "price_cut").as[Double],
        (js \ "url").as[String],
        (js \ "shop").as[Shop]
      ))
    }
  }

  def getLowestPrice(gameName: String): Future[Price] = Future {
    val gamePlain = getPlain(gameName);
    if (gamePlain.isDefined) {
      lowestPrice(prices(gamePlain.get).get)
    }else{
      throw new Exception(s"No price found for $gameName")
    }
  }

  def prices(gamePlain: String): Option[List[Price]] = {
    val pricesUrl = "https://api.isthereanydeal.com/v01/game/prices" ? ("key" -> token) & ("plains" -> gamePlain) & ("country" -> "CA")
    val svc = url(pricesUrl)
    val pricesHtml = Http(svc OK as.String)
    val pricesJson = Json.parse(pricesHtml())
    val pricesList = (pricesJson \ "data" \ gamePlain \ "list" ).asOpt[List[Price]]
    return pricesList
  }

  def getPlain(gameTitle: String): Option[String] = {
    val plainUrl = "https://api.isthereanydeal.com/v02/game/plain/" ? ("key" -> token) & ("title" -> gameTitle)
    val svc = url(plainUrl)
    val gamePlain = Http(svc OK as.String)
    val plain = Json.parse(gamePlain())
    (plain \ "data" \ "plain").asOpt[String]
  }

  def lowestPrice(priceList: List[Price]): Price = {
    priceList.minBy( price => (price.price_new) )
  }

}


case class Shop(id: String, name: String)
case class Price(
  price_new: Double,
  price_old: Double,
  price_cut: Double,
  url: String,
  shop: Shop
)
