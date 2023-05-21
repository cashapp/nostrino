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

  val arbEventTag: Arb<EventTag> by lazy { arbByteString32.map { EventTag(it) } }
  val arbPubKeyTag: Arb<PubKeyTag> by lazy { arbPubKey.map { PubKeyTag(it) } }
  val arbHashTag: Arb<HashTag> by lazy { arbVanillaString.map { HashTag(it.replace(" ", "")) } }
  val arbRelaysTag: Arb<RelaysTag> by lazy { Arb.list(arbVanillaString, range = 1..10).map(::RelaysTag) }
  val arbAmountTag: Arb<AmountTag> by lazy { Arb.long(min = 1L).map { AmountTag(it) } }
  val arbLnUrlTag: Arb<LnUrlTag> by lazy { arbVanillaString.map(::LnUrlTag) }
  val arbTag: Arb<Tag> by lazy { Arb.choice(arbEventTag, arbPubKeyTag, arbHashTag, arbRelaysTag, arbAmountTag, arbLnUrlTag) }

}
