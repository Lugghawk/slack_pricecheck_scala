package com.pricecheck.http

import dispatch._
import scala.concurrent.{Future, ExecutionContext}


trait HttpClient {
  def getUrlAsString(urlToFetch: String):Future[String]
}

case class DispatchHttpClient(implicit ec: ExecutionContext) extends HttpClient {
  def getUrlAsString(urlToFetch: String):Future[String] = {
    Http(url(urlToFetch) OK as.String)
  }
}
