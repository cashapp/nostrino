package app.cash.nostrino.model

import app.cash.nostrino.ArbPrimitive.arbByteString32
import app.cash.nostrino.crypto.PubKeyTest.Companion.arbPubKey
import app.cash.nostrino.crypto.SecKeyTest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.choose
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.triple
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
    private val emojis: List<Int> by lazy {
      Companion::class.java.getResource("/emojis.txt").readText()
        .lines().filterNot { it.isEmpty() }.map { it.codePointAt(0) }
    }
    private val arbEmoji = Arb.element(emojis)
    val arbReaction = Arb.triple(arbByteString32, arbPubKey, arbEmoji)
      .flatMap { (e, p, c) ->
        Arb.choose(
          3 to Upvote(e, p),
          3 to Downvote(e, p),
          1 to EmojiReact(String(Character.toChars(c)), e, p)
        )
      }
    private val testData = Arb.pair(arbReaction, SecKeyTest.arbSecKey)
  }
}
