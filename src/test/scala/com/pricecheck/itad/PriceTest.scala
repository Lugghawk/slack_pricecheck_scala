package com.pricecheck.itad

import org.scalatest._


class PriceTest extends FlatSpec with Matchers {

  "Price" should "order itself by price_new" in {
    val prices = Seq(
      Price(1,1,1,"",null),
      Price(2,1,1,"",null),
      Price(3,1,1,"",null),
      Price(4,1,1,"",null))


    prices.min should be (Price(1,1,1,"",null))
    prices.max should be (Price(4,1,1,"",null))
  }
}                 
