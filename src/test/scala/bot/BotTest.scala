import com.slackpricecheck.slack.Bot
import org.scalatest._
import org.scalamock.scalatest.MockFactory
import com.pricecheck.client.{Client}
import com.slackpricecheck.itad._

class BotTest extends FlatSpec with Matchers with MockFactory{

  def fixture = new {
    val client = mock[Client]
    val itad = mock[ITAD]

    (client.self _).expects().returning("bot")
  }

  "A bot" should "only speak when spoken to" in {
    val f = fixture
    val bot : Bot = new Bot(f.client, stub[ITAD])
    bot.selfId = "bot"
    assert(bot.shouldRespond("<@bot> do stuff"))
    assert(!bot.shouldRespond("Don't respond bot!"))
  }


  it should "know which game it's being asked about" in {
    val f = fixture
    val bot : Bot = new Bot(f.client, mock[ITAD])
    bot.selfId = "bot"
    bot.gameName("<@bot> game1") should be ("game1")
    bot.gameName("<@bot> my_long_game_name") should be ("my_long_game_name")
    bot.gameName("<@bot>") should be ("")
  }

  it should "format the price so it can be read by the recipient" in {
    val f = fixture
    val bot: Bot = new Bot(f.client, mock[ITAD])
    val price: Price = new Price(1.00, 0.00, 0.00, "http://foo.com", Shop("steam", "Steam"))
    val priceMessage = "Found lowest price of $1.00 at Steam (http://foo.com)"

    bot.formatPriceMessage(price) should be (priceMessage)

  }
}
