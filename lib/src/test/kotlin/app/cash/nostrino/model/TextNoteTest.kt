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
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class TextNoteTest : StringSpec({

  "can be signed into an event" {
    checkAll(testData) { (note, sec) ->
      val event = note.sign(sec)
      event.content shouldBe note.toJsonString()
      event.pubKey shouldBe sec.pubKey.key
    }
  }
}) {
  companion object {
    val arbTextNote = Arb.string(minSize = 1).map { TextNote(it) }
    private val testData = Arb.pair(arbTextNote, arbSecKey)
  }
}
