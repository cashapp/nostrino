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

import app.cash.nostrino.crypto.ArbKeys.arbSecKey
import app.cash.nostrino.message.NostrMessageAdapter
import app.cash.nostrino.model.ArbEventContent.arbZapRequest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.pair
import io.kotest.property.checkAll

class ZapRequestTest : StringSpec({
  "can be serialised" {
    checkAll(testData) { (zapRequest, secKey) ->
      val event = zapRequest.sign(secKey)
      event.content shouldBe zapRequest.toJsonString()
      event.pubKey shouldBe secKey.pubKey.key
      event.tags shouldBe zapRequest.tags.map { it.toJsonList() }
    }
  }

  "can be deserialised" {
    val rawEvent = """
       {
         "content": "Zap!",
         "kind": 9734,
         "tags": [
           ["relays", "wss://nostr-pub.wellorder.com"],
           ["amount", "21000"],
           ["lnurl", "lnurl1dp68gurn8ghj7um5v93kketj9ehx2amn9uh8wetvdskkkmn0wahz7mrww4excup0dajx2mrv92x9xp"],
           ["p", "04c915daefee38317fa734444acee390a8269fe5810b2241e5e6dd343dfbecc9"],
           ["e", "9ae37aa68f48645127299e9453eb5d908a0cbb6058ff340d528ed4d37c8994fb"]
         ],
         "pubkey": "97c70a44366a6535c145b333f973ea86dfdc2d7a99da618c40c64705ad98e322",
         "created_at": 1679673265,
         "id": "30efed56a035b2549fcaeec0bf2c1595f9a9b3bb4b1a38abaf8ee9041c4b7d93",
         "sig": "f2cb581a84ed10e4dc84937bd98e27acac71ab057255f6aa8dfa561808c981fe8870f4a03c1e3666784d82a9c802d3704e174371aa13d63e2aeaf24ff5374d9d"
       }
    """.trimIndent()
    val event = Event.fromJson(rawEvent)

    event?.content().shouldBeInstanceOf<ZapRequest>()
  }

}) {
  companion object {
    private val testData = Arb.pair(arbZapRequest, arbSecKey)
  }
}
