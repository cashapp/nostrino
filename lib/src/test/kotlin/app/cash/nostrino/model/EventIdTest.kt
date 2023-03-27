package app.cash.nostrino.model

import app.cash.nostrino.crypto.Bech32Serde
import app.cash.nostrino.crypto.PubKey
import app.cash.nostrino.crypto.PubKeyTest
import app.cash.nostrino.model.Primitives.arbByteString32
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.Arb
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.byteArray
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll
import okio.ByteString.Companion.toByteString

class EventIdTest : StringSpec({
  "shows value as hex in toString" {
    checkAll(arbEventId) { eventId ->
      eventId.toString() shouldMatch "[a-f0-9]{64}"
      eventId.toString() shouldBe eventId.id.hex()
    }
  }

  "can always be converted to nip-19 bech32 encoded note" {
    checkAll(arbEventId) { id ->
      id.note shouldMatch "note1[qpzry9x8gf2tvdw0s3jn54khce6mua7l]{58}"
    }
  }

  "can always be restored from nip-19 bech32 encoded note" {
    checkAll(arbEventId) { id ->
      EventId.parse(id.note) shouldBe id
    }
  }

  "will refuse to convert any other kind of bech32 encoded string" {
    checkAll(Arb.stringPattern("[A-Za-z0-9]{4,12}")) { text ->
      val encoding = text.take(3).lowercase()
      val encoded = Bech32Serde.encodeBytes(encoding, text.toByteArray().toByteString(), Bech32Serde.Encoding.Bech32)
      shouldThrow<IllegalArgumentException> { EventId.parse(encoded) }
        .message shouldBe "Unsupported encoding hrp=$encoding"
    }
  }

  "will refuse to create an event id with the wrong number of bytes" {
    checkAll(Arb.byteArray(Arb.int(0, 256).filterNot { it == 32 }, Arb.byte())) {
      shouldThrow<IllegalArgumentException> { EventId(it.toByteString()) }
    }
  }
}) {
  companion object {
    val arbEventId: Arb<EventId> = arbByteString32.map { EventId(it) }
  }
}
