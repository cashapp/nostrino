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

import app.cash.nostrino.crypto.SecKeyTest.Companion.arbSecKey
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll

class UserMetaDataTest : StringSpec({

  "can be signed into an event" {
    checkAll(testData) { (userMetaData, sec) ->
      val event = userMetaData.sign(sec)
      event.content shouldBe userMetaData.toJsonString()
      event.content() shouldBe userMetaData
      event.pubKey shouldBe sec.pubKey.key
    }
  }
}) {
  companion object {
    val arbVanillaString = Arb.stringPattern("[a-zA-Z0-9 ]+")
    val arbUserMetaData = Arb.bind(
      arbVanillaString.orNull(),
      arbVanillaString.orNull(),
      arbVanillaString.orNull(),
      arbVanillaString.orNull(),
      arbVanillaString.orNull(),
      arbVanillaString.orNull(),
      arbVanillaString.orNull()
    ) { name, about, picture, nip05, banner, displayName, website ->
      UserMetaData(name, about, picture, nip05, banner, displayName, website)
    }
    private val testData = Arb.pair(arbUserMetaData, arbSecKey)
  }
}
