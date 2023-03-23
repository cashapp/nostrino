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

import app.cash.nostrino.message.NostrMessageAdapter
import app.cash.nostrino.message.relay.CommandResult
import app.cash.nostrino.message.relay.EndOfStoredEvents
import app.cash.nostrino.message.relay.EventMessage
import app.cash.nostrino.message.relay.Notice
import app.cash.nostrino.message.relay.RelayMessage
import app.cash.nostrino.model.EventTest.Companion.arbEvent
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.instant
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class NostrMessageAdapterTest : StringSpec({

  "can serde end_of_stored_events" {
    val adapter = moshi.adapter(EndOfStoredEvents::class.java)
    checkAll(arbEndOfStoredEvents) {
      val json = adapter.toJson(it)
      json shouldStartWith """["EOSE","""
      adapter.fromJson(json) shouldBe it
    }
  }

  "can serde command_result" {
    val adapter = moshi.adapter(CommandResult::class.java)
    checkAll(arbCommandResult) {
      val json = adapter.toJson(it)
      json shouldStartWith """["OK","""
      adapter.fromJson(json) shouldBe it
    }
  }

  "can serde notice" {
    val adapter = moshi.adapter(Notice::class.java)
    checkAll(arbNotice) {
      val json = adapter.toJson(it)
      json shouldStartWith """["NOTICE","""
      adapter.fromJson(json) shouldBe it
    }
  }

  "can serde events" {
    val adapter = moshi.adapter(Event::class.java)
    checkAll(arbEvent) {
      val json = adapter.toJson(it)
      adapter.fromJson(json) shouldBe it
    }
  }

  "can serde event messages" {
    val adapter = moshi.adapter(EventMessage::class.java)
    checkAll(arbEventMessage) {
      val json = adapter.toJson(it)
      json shouldStartWith """["EVENT","${it.subscriptionId}","""
      adapter.fromJson(json) shouldBe it
    }
  }

  "can serde any relay message" {
    val adapter = moshi.adapter(RelayMessage::class.java)
    checkAll(arbRelayMessage) {
      adapter.fromJson(adapter.toJson(it)) shouldBe it
    }
  }
}) {
  companion object {
    val moshi = Moshi.Builder()
      .add(NostrMessageAdapter())
      .addLast(KotlinJsonAdapterFactory())
      .build()

    val arbByteString32: Arb<ByteString> = Arb.list(Arb.byte(), 32..32)
      .map { it.toByteArray().toByteString() }
    val arbByteString64: Arb<ByteString> = Arb.list(Arb.byte(), 64..64)
      .map { it.toByteArray().toByteString() }
    val arbInstantSeconds: Arb<Instant> =
      Arb.instant(Instant.EPOCH, Instant.now().plus(5000, ChronoUnit.DAYS))
        .map { it.truncatedTo(ChronoUnit.SECONDS) }
    val arbUUID = arbitrary { UUID.randomUUID() }

    val arbSubscriptionId = arbUUID.map { it.toString() }
    val arbEndOfStoredEvents = arbSubscriptionId.map { EndOfStoredEvents(it) }
    val arbNotice = Arb.string().map { Notice(it) }
    val arbCommandResult = Arb.bind(
      arbByteString32,
      Arb.boolean(),
      Arb.string().orNull()
    ) { id, success, message ->
      CommandResult(id, success, message)
    }

    val arbEventMessage: Arb<EventMessage> =
      Arb.bind(arbSubscriptionId, arbEvent) { subscriptionId, event ->
        EventMessage(subscriptionId, event)
      }

    val arbRelayMessage = Arb.choice(
      arbEndOfStoredEvents,
      arbCommandResult,
      arbEventMessage,
      arbNotice
    )
  }
}
