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

import app.cash.nostrino.model.EncryptedDmTest.Companion.arbEncryptedDm
import app.cash.nostrino.model.Primitives.arbByteString32
import app.cash.nostrino.model.Primitives.arbByteString64
import app.cash.nostrino.model.Primitives.arbInstantSeconds
import app.cash.nostrino.model.ReactionTest.Companion.arbReaction
import app.cash.nostrino.model.TextNoteTest.Companion.arbTextNote
import app.cash.nostrino.model.UserMetaDataTest.Companion.arbUserMetaData
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll

class EventTest : StringSpec({

  "can rehydrated event content" {
    checkAll(arbEventWithContent) { (event, content) ->
      event.content() shouldBe content
    }
  }
}) {
  companion object {
    val arbEventContent = Arb.choice(
      arbTextNote,
      arbEncryptedDm,
      arbUserMetaData,
      arbReaction
    )

    val arbEventWithContent: Arb<Pair<Event, EventContent>> by lazy {
      Arb.bind(
        arbByteString32,
        arbByteString32,
        arbInstantSeconds,
        arbEventContent,
        arbByteString64
      ) { id, pubKey, createdAt, content, sig ->
        Event(id, pubKey, createdAt, content.kind, content.tags(), content.toJsonString(), sig) to content
      }
    }
    val arbEvent: Arb<Event> by lazy { arbEventWithContent.map { it.first } }
  }
}
