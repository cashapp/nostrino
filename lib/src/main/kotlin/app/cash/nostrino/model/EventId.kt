package app.cash.nostrino.model

import app.cash.nostrino.crypto.Bech32Serde
import okio.ByteString
import okio.ByteString.Companion.toByteString

data class EventId(val id: ByteString) {
  init {
    require(id.size == 32) { "Incorrect size for EventId: $id" }
  }

  val note by lazy {
    Bech32Serde.encodeBytes("note", id, Bech32Serde.Encoding.Bech32)
  }

  override fun toString() = id.hex()

  companion object {
    /** Create event id from nip-19 bech32 encoded string */
    fun parse(bech32: String): EventId {
      val (hrp, id) = Bech32Serde.decodeBytes(bech32, false)
      require(hrp == "note") { "Unsupported encoding hrp=$hrp" }
      return EventId(id.toByteString())
    }
  }
}
