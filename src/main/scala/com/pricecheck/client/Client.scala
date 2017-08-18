package com.pricecheck.client

import scala.concurrent.Future

trait Client {
  def connect(): Unit
  def sendMessage(target: String, message: String): Future[Any]
  def onMessage(f: (Message) => Unit): Unit
  def self():String
}

trait Message {
  val text: String
  val origin: String
}
