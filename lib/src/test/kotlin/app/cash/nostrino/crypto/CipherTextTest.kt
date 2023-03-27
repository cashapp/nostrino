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

package app.cash.nostrino.crypto

import app.cash.nostrino.model.Primitives.arbByteString32
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.pair
import io.kotest.property.checkAll

class CipherTextTest : StringSpec({

  "Meaningful error on bad cipher text parsing" {
    shouldThrow<IllegalArgumentException> { CipherText.parse("no") }
      .message shouldStartWith "Invalid cipherText"

    shouldThrow<IllegalArgumentException> { CipherText.parse("abc?iv=789§") }
      .message shouldStartWith "Invalid cipherText"

    shouldThrow<IllegalArgumentException> { CipherText.parse("abc?iv=123?iv=789") }
      .message shouldStartWith "Invalid cipherText"
  }

  "Parse valid format and serde" {
    checkAll(Arb.pair(arbByteString32, arbByteString32)) { (cipher, iv) ->
      val input = "${cipher.base64()}?iv=${iv.base64()}"
      val expected = CipherText(cipher, iv)
      CipherText.parse(input) shouldBe expected
      expected.toString() shouldBe input
    }
  }
})
