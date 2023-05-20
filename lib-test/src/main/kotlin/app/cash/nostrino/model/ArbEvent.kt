package app.cash.nostrino.model

import app.cash.nostrino.ArbPrimitive.arbByteString32
import io.kotest.property.arbitrary.map

object ArbEvent {
  val arbEventId = arbByteString32.map { it.hex() }
}
