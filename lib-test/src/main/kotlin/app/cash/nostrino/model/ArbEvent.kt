package app.cash.nostrino.model

import app.cash.nostrino.ArbPrimitive
import app.cash.nostrino.ArbPrimitive.arbByteString32
import app.cash.nostrino.message.NostrMessageAdapter
import app.cash.nostrino.message.relay.CommandResult
import app.cash.nostrino.message.relay.EndOfStoredEvents
import app.cash.nostrino.message.relay.EventMessage
import app.cash.nostrino.message.relay.Notice
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string

object ArbEvent {
  val moshi = Moshi.Builder()
    .add(NostrMessageAdapter())
    .addLast(KotlinJsonAdapterFactory())
    .build()

  val arbEventId = arbByteString32.map { it.hex() }
  private val arbEventContent = Arb.choice(
    ArbEventContent.arbTextNote,
    ArbEventContent.arbEncryptedDm,
    ArbEventContent.arbUserMetaData,
    ArbEventContent.arbReaction,
    ArbEventContent.arbZapRequest
  )

  val arbEventWithContent: Arb<Pair<Event, EventContent>> by lazy {
    Arb.bind(
      arbByteString32,
      arbByteString32,
      ArbPrimitive.arbInstantSeconds,
      arbEventContent,
      ArbPrimitive.arbByteString64
    ) { id, pubKey, createdAt, content, sig ->
      val event = Event(
        id = id,
        pubKey = pubKey,
        createdAt = createdAt,
        kind = content.kind,
        tags = content.tags.map { it.toJsonList() },
        content = content.toJsonString(),
        sig = sig
      )
      event to content
    }
  }
  val arbEvent: Arb<Event> by lazy { arbEventWithContent.map { it.first } }

  val arbSubscriptionId = ArbPrimitive.arbUUID.map { it.toString() }
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
