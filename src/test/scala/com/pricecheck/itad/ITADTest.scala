package com.pricecheck.itad

import org.scalatest._
import org.scalamock.scalatest.MockFactory
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import com.pricecheck.http.HttpClient

class ITADTest extends FlatSpec with Matchers with MockFactory {

  "IsThereAnyDeal" should "be able to interpret a gameplain from isthereanydeal.com" in {
    val httpClient = mock[HttpClient]
    val gameTitle:String = "Stellaris"
    (httpClient.getUrlAsString _)
      .expects(s"https://api.isthereanydeal.com/v02/game/plain/?key=_testtoken_&title=$gameTitle")
      .returning(Future {"{\".meta\":{\"match\":\"title\",\"active\":true},\"data\":{\"plain\":\"stellaris\"}}"})
    val itad = ITAD("_testtoken_", httpClient)

    val result = Await.result(itad.getPlain(gameTitle), 500.millis)
    result should be (Some("stellaris"))
  }

  it should "return a None when no plain exists on isthereanydeal.com" in {
    val httpClient = mock[HttpClient]
    val gameTitle:String = "Non-existant-game"
    (httpClient.getUrlAsString _)
      .expects(s"https://api.isthereanydeal.com/v02/game/plain/?key=_testtoken_&title=$gameTitle")
      .returning(Future {"{\".meta\":{\"match\":false,\"active\":false},\"data\":[]}"})

    val itad = ITAD("_testtoken_", httpClient)
    val result = Await.result(itad.getPlain(gameTitle), 500.millis)
    result should be (None)
  }
}
