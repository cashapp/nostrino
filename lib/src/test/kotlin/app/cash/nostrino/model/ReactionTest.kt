package app.cash.nostrino.model

import app.cash.nostrino.crypto.SecKeyTest
import app.cash.nostrino.model.ArbEventContent.arbReaction
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.pair
import io.kotest.property.checkAll

class ReactionTest : StringSpec({

  "can construct signed reaction events" {
    checkAll(testData) { (reaction, sec) ->
      val event = reaction.sign(sec)
      event.content shouldBe reaction.toJsonString()
      event.pubKey shouldBe sec.pubKey.key
      event.tags.findLast { it.first() == "e" }?.drop(1).shouldContainExactly(reaction.eventId.hex())
      event.tags.findLast { it.first() == "p" }?.drop(1).shouldContainExactly(reaction.authorPubKey.key.hex())
    }
  }
}) {
  companion object {
    private val testData = Arb.pair(arbReaction, SecKeyTest.arbSecKey)
  }
}
