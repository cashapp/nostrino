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
 */

package app.cash.nostrino.model

import app.cash.nostrino.crypto.ArbKeys.arbSecKey
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
    private val testData = Arb.pair(arbReaction, arbSecKey)
  }
}
