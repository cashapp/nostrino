package app.cash.nostrino.model

import app.cash.nostrino.crypto.PubKeyTest.Companion.arbPubKey
import app.cash.nostrino.model.Primitives.arbByteString32
import app.cash.nostrino.model.UserMetaDataTest.Companion.arbVanillaString
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.pair
import io.kotest.property.checkAll

class TagTest : StringSpec({
  "event tag json list should be e :: event_id hex" {
    checkAll(arbEventTag) { tag ->
      tag.toJsonList() shouldBe listOf("e", tag.eventId.hex())
    }
  }

  "pubkey tag json list should be p :: pubkey hex" {
    checkAll(arbPubKeyTag) { tag ->
      tag.toJsonList() shouldBe listOf("p", tag.pubKey.hex())
    }
  }

  "hashtag json list should be t :: label" {
    checkAll(arbHashTag) { tag ->
      tag.toJsonList() shouldBe listOf("t", tag.label)
    }
  }

  "any tag can be ser/de" {
    checkAll(arbTag) { tag ->
      Tag.parseRaw(tag.toJsonList()) shouldBe tag
    }
  }

  "empty string list fails to parse as a tag" {
    shouldThrow<IllegalArgumentException> { Tag.parseRaw(emptyList()) }
  }

  "tag without value should fail to parse" {
    shouldThrow<IllegalArgumentException> { Tag.parseRaw(listOf("e")) }
    shouldThrow<IllegalArgumentException> { Tag.parseRaw(listOf("p")) }
    shouldThrow<IllegalArgumentException> { Tag.parseRaw(listOf("t")) }
  }

  "string list with incorrect tag should fail to parse" {
    checkAll(Arb.pair(
      arbTag,
      Arb.char().filterNot { setOf('e', 'p', 't').contains(it) }.map { it.toString() }
    )) { (tag, c) ->
      shouldThrow<IllegalArgumentException> { Tag.parseRaw(listOf(c).plus(tag.toJsonList().drop(1))) }
    }
  }

}) {

  companion object {
    val arbEventTag: Arb<EventTag> = arbByteString32.map { EventTag(it) }
    val arbPubKeyTag: Arb<PubKeyTag> = arbPubKey.map { PubKeyTag(it) }
    val arbHashTag: Arb<HashTag> = arbVanillaString.map { HashTag(it.replace(" ", "")) }
    val arbTag: Arb<Tag> = Arb.choice(arbEventTag, arbPubKeyTag, arbHashTag)
  }
}