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

import app.cash.nostrino.crypto.PubKeyTest.Companion.arbPubKey
import app.cash.nostrino.model.ArbEventContent.arbFilter
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll

class FilterTest : StringSpec({

  "can add authors to existing filters" {
    val testData = Arb.pair(arbFilter, Arb.set(arbPubKey))
    checkAll(testData) { (filter, moreAuthors) ->
      val filterAuthorKeys = filter.plusAuthors(*moreAuthors.toTypedArray()).authors
      filterAuthorKeys shouldBe when (filter.authors) {
        null -> moreAuthors.map { it.key.hex() }
        else -> filter.authors?.plus(moreAuthors.map { it.key.hex() })
      }?.toSet()
    }
  }

})
