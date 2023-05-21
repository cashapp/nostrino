package app.cash.nostrino.model

import app.cash.nostrino.crypto.PubKey
import okio.ByteString
import okio.ByteString.Companion.decodeHex

sealed interface Tag {
  fun toJsonList(): List<String>

  companion object {
    fun parseRaw(strings: List<String>): Tag {
      require(strings.size >= 2) { "Invalid tag format: $strings" }
      val (tag, value) = strings
      val values = strings.drop(1)
      return when (tag) {
        "e" -> EventTag(value.decodeHex())
        "p" -> PubKeyTag(PubKey(value.decodeHex()))
        "t" -> HashTag(value)
        "amount" -> AmountTag(value.toLong())
        "lnurl" -> LnUrlTag(value)
        "relays" -> RelaysTag(values)
        else -> throw IllegalArgumentException("Invalid tag format: $strings")
      }
    }
  }
}

data class EventTag(val eventId: ByteString) : Tag {
  override fun toJsonList() = listOf("e", eventId.hex())
}

data class PubKeyTag(val pubKey: PubKey) : Tag {
  override fun toJsonList() = listOf("p", pubKey.hex())
}

data class HashTag(val label: String) : Tag {
  override fun toJsonList() = listOf("t", label)
}

data class RelaysTag(val relays: List<String>) : Tag {
  override fun toJsonList() = listOf("relays") + relays
}

data class AmountTag(val amount: Long) : Tag {
  override fun toJsonList() = listOf("amount", amount.toString())
}

data class LnUrlTag(val lnurl: String) : Tag {
  override fun toJsonList() = listOf("lnurl", lnurl)
}
