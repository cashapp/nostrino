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
import app.cash.nostrino.model.ArbEventContent.arbZapReceipt
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.pair
import io.kotest.property.checkAll

class ZapReceiptTest : StringSpec({
  "can be serialised" {
    checkAll(testData) { (zapReceipt, secKey) ->
      val event = zapReceipt.sign(secKey)
      event.content shouldBe zapReceipt.toJsonString()
      event.pubKey shouldBe secKey.pubKey.key
      event.tags shouldBe zapReceipt.tags.map { it.toJsonList() }
    }
  }

  "can be deserialised" {
    val rawEvent = """
       {
          "id": "67b48a14fb66c60c8f9070bdeb37afdfcc3d08ad01989460448e4081eddda446",
          "pubkey": "9630f464cca6a5147aa8a35f0bcdd3ce485324e732fd39e09233b1d848238f31",
          "created_at": 1674164545,
          "kind": 9735,
          "tags": [
            ["p", "32e1827635450ebb3c5a7d12c1f8e7b2b514439ac10a67eef3d9fd9c5c68e245"],
            ["e", "3624762a1274dd9636e0c552b53086d70bc88c165bc4dc0f9e836a1eaf86c3b8"],
            ["bolt11", "lnbc10u1p3unwfusp5t9r3yymhpfqculx78u027lxspgxcr2n2987mx2j55nnfs95nxnzqpp5jmrh92pfld78spqs78v9euf2385t83uvpwk9ldrlvf6ch7tpascqhp5zvkrmemgth3tufcvflmzjzfvjt023nazlhljz2n9hattj4f8jq8qxqyjw5qcqpjrzjqtc4fc44feggv7065fqe5m4ytjarg3repr5j9el35xhmtfexc42yczarjuqqfzqqqqqqqqlgqqqqqqgq9q9qxpqysgq079nkq507a5tw7xgttmj4u990j7wfggtrasah5gd4ywfr2pjcn29383tphp4t48gquelz9z78p4cq7ml3nrrphw5w6eckhjwmhezhnqpy6gyf0"],
            ["description", "{\"pubkey\":\"32e1827635450ebb3c5a7d12c1f8e7b2b514439ac10a67eef3d9fd9c5c68e245\",\"content\":\"\",\"id\":\"d9cc14d50fcb8c27539aacf776882942c1a11ea4472f8cdec1dea82fab66279d\",\"created_at\":1674164539,\"sig\":\"77127f636577e9029276be060332ea565deaf89ff215a494ccff16ae3f757065e2bc59b2e8c113dd407917a010b3abd36c8d7ad84c0e3ab7dab3a0b0caa9835d\",\"kind\":9734,\"tags\":[[\"e\",\"3624762a1274dd9636e0c552b53086d70bc88c165bc4dc0f9e836a1eaf86c3b8\"],[\"p\",\"32e1827635450ebb3c5a7d12c1f8e7b2b514439ac10a67eef3d9fd9c5c68e245\"],[\"relays\",\"wss://relay.damus.io\",\"wss://nostr-relay.wlvs.space\",\"wss://nostr.fmt.wiz.biz\",\"wss://relay.nostr.bg\",\"wss://nostr.oxtr.dev\",\"wss://nostr.v0l.io\",\"wss://brb.io\",\"wss://nostr.bitcoiner.social\",\"ws://monad.jb55.com:8080\",\"wss://relay.snort.social\"]]}"],
            ["preimage", "5d006d2cf1e73c7148e7519a4c68adc81642ce0e25a432b2434c99f97344c15f"]
          ],
          "content": "",
          "sig": "b0a3c5c984ceb777ac455b2f659505df51585d5fd97a0ec1fdb5f3347d392080d4b420240434a3afd909207195dac1e2f7e3df26ba862a45afd8bfe101c2b1cc"
      }
    """.trimIndent()
    val event = Event.fromJson(rawEvent)

    event?.content().shouldBeInstanceOf<ZapReceipt>()
  }

}) {
  companion object {
    private val testData = Arb.pair(arbZapReceipt, arbSecKey)
  }
}
