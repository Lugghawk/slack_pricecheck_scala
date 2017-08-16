import com.slackpricecheck.slack.Bot
import org.scalatest._
import com.slackpricecheck.itad._

class BotTest extends FlatSpec with Matchers{


  "A bot" should "only speak when spoken to" in {
    val bot : Bot = new Bot()
    bot.selfId = "bot"
    assert(bot.shouldRespond("<@bot> do stuff"))
    assert(!bot.shouldRespond("Don't respond bot!"))
  }


  it should "know which game it's being asked about" in {
    val bot : Bot = new Bot()
    bot.selfId = "bot"
    bot.gameName("<@bot> game1") should be ("game1")
    bot.gameName("<@bot> my_long_game_name") should be ("my_long_game_name")
    bot.gameName("<@bot>") should be ("")
  }

  it should "format the price so it can be read by the recipient" in {
    val bot: Bot = new Bot()
    val price: Price = new Price(1.00, 0.00, 0.00, "http://foo.com", Shop("steam", "Steam"))
    val priceMessage = "Found lowest price of $1.00 at Steam (http://foo.com)"

    bot.formatPriceMessage(price) should be (priceMessage)

  }
}
