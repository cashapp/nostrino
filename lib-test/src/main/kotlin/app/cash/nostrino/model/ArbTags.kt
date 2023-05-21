/*
 * Copyright (c) 2023 Block, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
