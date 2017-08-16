import org.scalatest.FlatSpec
import com.slackpricecheck.slack.Bot

class BotTest extends FlatSpec {


  "A bot" should "only speak when spoken to" in {
    val bot : Bot = new Bot()
    bot.selfId = "bot"
    assert(bot.shouldRespond("<@bot> do stuff"))
    assert(!bot.shouldRespond("Don't respond bot!"))
  }

}
