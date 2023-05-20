package app.cash.nostrino.model

import app.cash.nostrino.ArbPrimitive
import app.cash.nostrino.crypto.ArbKeys.arbPubKey
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choose
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.triple

object ArbEventContent {

  private val emojis: List<Int> by lazy {
    ArbEventContent::class.java.getResource("/emojis.txt").readText()
      .lines().filterNot { it.isEmpty() }.map { it.codePointAt(0) }
  }
  private val arbEmoji = Arb.element(emojis)
  val arbReaction = Arb.triple(ArbPrimitive.arbByteString32, arbPubKey, arbEmoji)
    .flatMap { (e, p, c) ->
      Arb.choose(
        3 to Upvote(e, p),
        3 to Downvote(e, p),
        1 to EmojiReact(String(Character.toChars(c)), e, p)
      )
    }

  val arbTextNote: Arb<TextNote> = Arb.pair(
    Arb.string(minSize = 1),
    Arb.list(ArbTags.arbTag, 0..5)
  ).map { (content, tags) -> TextNote(content, tags) }

  val arbUserMetaData = Arb.bind(
    ArbPrimitive.arbVanillaString.orNull(),
    ArbPrimitive.arbVanillaString.orNull(),
    ArbPrimitive.arbVanillaString.orNull(),
    ArbPrimitive.arbVanillaString.orNull(),
    ArbPrimitive.arbVanillaString.orNull(),
    ArbPrimitive.arbVanillaString.orNull(),
    ArbPrimitive.arbVanillaString.orNull()
  ) { name, about, picture, nip05, banner, displayName, website ->
    UserMetaData(name, about, picture, nip05, banner, displayName, website)
  }

  val arbZapRequest = Arb.bind(
    Arb.string(minSize = 1),
    ArbTags.arbRelaysTag,
    ArbTags.arbAmountTag,
    ArbTags.arbLnurlTag,
    ArbTags.arbPubKeyTag,
    ArbTags.arbEventTag
  ) { content, relays, amount, lnurl, pubKey, event ->
    ZapRequest(content, relays.relays, amount.amount, lnurl.lnurl, pubKey.pubKey, event.eventId)
  }

}
