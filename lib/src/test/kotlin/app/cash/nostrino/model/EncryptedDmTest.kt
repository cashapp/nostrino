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

import app.cash.nostrino.crypto.SecKey
import app.cash.nostrino.crypto.SecKeyTest.Companion.arbSecKey
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.triple
import io.kotest.property.checkAll

class EncryptedDmTest : StringSpec({

  "can encrypt and decrypt" {
    checkAll(arbTestData) { (from, to, message) ->
      val dm = EncryptedDm(from, to.pubKey, message)
      val decrypted = dm.decipher(from.pubKey, to)

      decrypted shouldBe message
    }
  }
}) {
  companion object {
    private val arbTestData: Arb<Triple<SecKey, SecKey, String>> = Arb.triple(arbSecKey, arbSecKey, Arb.string())

    val arbEncryptedDm: Arb<EncryptedDm> = arbTestData.map { (from, to, message) ->
      EncryptedDm(from, to.pubKey, message)
    }
  }
}
