package com.pricecheck.client

import scala.concurrent.Future

trait ClientBuilder {
  def connect(): Client
}

trait Client {
  def sendMessage(target: String, message: String): Future[Any]
  def onMessage(f: (Message) => Unit): Unit
  def self():String
}

trait Message {
  def text: String
  def origin: String
}
