package app.cash.nostrino

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.instant
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.stringPattern
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

object ArbPrimitive {

  val arbByteString32: Arb<ByteString> = Arb.list(Arb.byte(), 32..32)
    .map { it.toByteArray().toByteString() }
  val arbByteString64: Arb<ByteString> = Arb.list(Arb.byte(), 64..64)
    .map { it.toByteArray().toByteString() }
  val arbInstantSeconds: Arb<Instant> =
    Arb.instant(Instant.EPOCH, Instant.now().plus(5000, ChronoUnit.DAYS))
      .map { it.truncatedTo(ChronoUnit.SECONDS) }
  val arbUUID = arbitrary { UUID.randomUUID() }
  val arbVanillaString = Arb.stringPattern("[a-zA-Z0-9 ]+")

}
