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

package app.cash.nostrino.client

import app.cash.nostrino.crypto.SecKeyGenerator
import app.cash.nostrino.model.EncryptedDm
import app.cash.nostrino.model.Filter
import app.cash.nostrino.model.HashTag
import app.cash.nostrino.model.ReactionTest.Companion.arbReaction
import app.cash.nostrino.model.TextNote
import app.cash.nostrino.model.TextNoteTest.Companion.arbTextNote
import app.cash.nostrino.model.UserMetaDataTest.Companion.arbUserMetaData
import app.cash.turbine.test
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RelayClientTest : StringSpec({

  "can connect to local relay, publish and consume notes" {
    val sec = SecKeyGenerator().generate()
    val note = arbTextNote.next()
    val event = note.sign(sec)

    with(RelayClient("ws://localhost:7707")) {
      start()
      subscribe(Filter.userNotes(sec.pubKey))
      send(event)

      val actualEvent = notes.first()
      actualEvent shouldBe event
      actualEvent.content shouldBe note.text
      actualEvent.content() shouldBe note

      stop()
    }
  }

  "can publish and consume DMs" {
    val alice = SecKeyGenerator().generate()
    val bob = SecKeyGenerator().generate()

    val dm = EncryptedDm(bob, alice.pubKey, "Hello world")
    val event = dm.sign(bob)

    with(RelayClient("ws://localhost:7707")) {
      start()
      subscribe(Filter.directMessages(alice.pubKey))
      send(event)

      val actualEvent = directMessages.first()
      actualEvent shouldBe event
      actualEvent.content shouldBe dm.cipherText.toString()
      actualEvent.content() shouldBe dm

      stop()
    }
  }

  "can publish and consume reactions the publishing user" {
    val alice = SecKeyGenerator().generate()

    val reaction = arbReaction.next()
    val event = reaction.sign(alice)

    with(RelayClient("ws://localhost:7707")) {
      start()
      subscribe(Filter.reactions(author = alice.pubKey))
      send(event)

      val actualEvent = reactions.first()
      actualEvent shouldBe event
      actualEvent.content shouldBe reaction.toJsonString()
      actualEvent.content() shouldBe reaction

      stop()
    }
  }

  "can consume reactions for an event" {
    val alice = SecKeyGenerator().generate()

    val reaction = arbReaction.next()
    val event = reaction.sign(alice)

    with(RelayClient("ws://localhost:7707")) {
      start()
      subscribe(Filter.reactions(eventId = reaction.eventId))
      send(event)

      val actualEvent = reactions.first()
      actualEvent shouldBe event
      actualEvent.content shouldBe reaction.toJsonString()
      actualEvent.content() shouldBe reaction

      stop()
    }
  }

  "can consume reactions to an event author" {
    val alice = SecKeyGenerator().generate()

    val reaction = arbReaction.next()
    val event = reaction.sign(alice)

    with(RelayClient("ws://localhost:7707")) {
      start()
      subscribe(Filter.reactions(eventAuthor = reaction.authorPubKey))
      send(event)

      val actualEvent = reactions.first()
      actualEvent shouldBe event
      actualEvent.content shouldBe reaction.toJsonString()
      actualEvent.content() shouldBe reaction

      stop()
    }
  }

  "can publish and consume user metadata" {
    val sec = SecKeyGenerator().generate()
    val md = arbUserMetaData.next()
    val event = md.sign(sec)

    with(RelayClient("ws://localhost:7707")) {
      start()
      subscribe(Filter.userMetaData(sec.pubKey))
      send(event)

      val actualEvent = userMetaData.first()
      actualEvent shouldBe event
      actualEvent.content shouldBe md.toJsonString()
      actualEvent.content() shouldBe md

      stop()
    }
  }

  "can consume events by hashtag" {
    val sec = SecKeyGenerator().generate()
    val noteWithoutHashTags = arbTextNote.next()
    val noteWithHashTags = TextNote(
      text = "never",
      tags = listOf(
        HashTag("gonna"),
        HashTag("give"),
        HashTag("you"),
        HashTag("up")
      )
    )

    with(RelayClient("ws://localhost:7707")) {
      start()
      subscribe(
        Filter.hashTagNotes(
          hashtags = setOf(
            HashTag("give"),
            HashTag("up"),
          )
        )
      )

      send(noteWithHashTags.sign(sec))
      send(noteWithoutHashTags.sign(sec))

      notes.test {
        val actualEvent = awaitItem()
        actualEvent.content shouldBe "never"
        actualEvent.content() shouldBe noteWithHashTags

        expectNoEvents()
      }

      stop()
    }
  }

  "can unsubscribe via the returned subscription" {
    val alice = SecKeyGenerator().generate()
    val bob = SecKeyGenerator().generate()

    with(RelayClient("ws://localhost:7707")) {
      start()

      // create two separate subscriptions, one for each key
      subscribe(Filter.userNotes(alice.pubKey))
      val bobSub = subscribe(Filter.userNotes(bob.pubKey))

      // start counting how many events we receive from each key
      var (aliceCount, bobCount) = 0 to 0
      val readingJob = launch {
        notes.collect {
          when (it.pubKey) {
            alice.pubKey.key -> aliceCount++
            bob.pubKey.key -> bobCount++
            else -> fail("Unrecognised key in event: $it")
          }
        }
      }

      // start sending events from both alice and bob
      val sendingJob = launch {
        while (true) {
          send(arbTextNote.next().sign(alice))
          send(arbTextNote.next().sign(bob))
          delay(30L)
        }
      }

      // allow some time for events to be collected
      delay(200L)

      // unsubscribe from bob
      unsubscribe(bobSub)

      // allow some time for more events
      delay(300L)

      // the count of alice's messages must exceed bob's by > 2X
      bobCount * 2 shouldBeLessThan aliceCount

      sendingJob.cancel()
      readingJob.cancel()
      stop()
    }
  }
})
