package app.cash.nostrino.model

import app.cash.nostrino.ArbPrimitive.arbByteString32
import app.cash.nostrino.ArbPrimitive.arbVanillaString
import app.cash.nostrino.crypto.ArbKeys.arbPubKey
import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map

object ArbTags {

  val arbEventTag: Arb<EventTag> = arbByteString32.map { EventTag(it) }
  val arbPubKeyTag: Arb<PubKeyTag> = arbPubKey.map { PubKeyTag(it) }
  val arbHashTag: Arb<HashTag> = arbVanillaString.map { HashTag(it.replace(" ", "")) }
  val arbRelaysTag: Arb<RelaysTag> = Arb.list(arbVanillaString, range = 1..10).map(::RelaysTag)
  val arbAmountTag: Arb<AmountTag> = Arb.long(min = 1L).map { AmountTag(it) }
  val arbLnurlTag: Arb<LnurlTag> = arbVanillaString.map(::LnurlTag)
  val arbTag: Arb<Tag> = Arb.choice(arbEventTag, arbPubKeyTag, arbHashTag, arbRelaysTag, arbAmountTag, arbLnurlTag)

}
