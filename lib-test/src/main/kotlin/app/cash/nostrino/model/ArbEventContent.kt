package app.cash.nostrino.model

import app.cash.nostrino.ArbPrimitive
import app.cash.nostrino.ArbPrimitive.arbByteString32
import app.cash.nostrino.ArbPrimitive.arbEmoji
import app.cash.nostrino.crypto.ArbKeys.arbPubKey
import app.cash.nostrino.crypto.ArbKeys.arbSecKey
import app.cash.nostrino.model.ArbEvent.arbEventId
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choose
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.triple

object ArbEventContent {

  val arbEncryptedDm: Arb<EncryptedDm> = Arb.triple(arbSecKey, arbSecKey, Arb.string()).map { (from, to, message) ->
    EncryptedDm(from, to.pubKey, message)
  }

  val arbFilter: Arb<Filter> = Arb.bind(
    Arb.set(arbEventId).orNull(),
    ArbPrimitive.arbInstantSeconds.orNull(),
    Arb.set(arbPubKey.map { it.key.hex() }).orNull(),
  ) { ids, since, authors ->
    Filter(
      ids = ids,
      since = since,
      authors = authors,
    )
  }

  val arbReaction = Arb.triple(arbByteString32, arbPubKey, arbEmoji)
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
