package app.cash.nostrino.model

import app.cash.nostrino.crypto.PubKey
import okio.ByteString

data class ZapRequest(
  val content: String,
  val relays: List<String>,
  val amount: Long,
  val lnurl: String?,
  val to: PubKey,
  val eventId: ByteString?,
  override val tags: List<Tag> = listOfNotNull(
    RelaysTag(relays),
    AmountTag(amount),
    lnurl?.let(::LnurlTag),
    PubKeyTag(to),
    eventId?.let(::EventTag)
  )
) : EventContent {
  override val kind = Companion.kind

  override fun toJsonString() = content

  companion object {
    const val kind = 9734
  }
}
