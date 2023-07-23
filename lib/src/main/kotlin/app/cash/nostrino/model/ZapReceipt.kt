package app.cash.nostrino.model

import app.cash.nostrino.crypto.PubKey
import okio.ByteString

/**
 * Zap receipt. Event kind 9735, as defined in
 * [nip-57](https://github.com/nostr-protocol/nips/blob/master/57.md).
 */
data class ZapReceipt(
  val to: PubKey,
  val eventId: ByteString?,
  val bolt11: String,
  val zapRequest: Event,
  val preimage: ByteString? = null,
  override val tags: List<Tag> = listOfNotNull(
    PubKeyTag(to),
    eventId?.let(::EventTag),
    Bolt11Tag(bolt11),
    ZapReceiptDescriptionTag(zapRequest),
    preimage?.let(::PreimageTag),
  )
) : EventContent {

  init {
      require(zapRequest.kind == ZapRequest.kind) {
          "ZapReceipt description must be a ZapRequest Event"
      }
  }

  override val kind = Companion.kind

  override fun toJsonString() = ""

  companion object {
    const val kind = 9735
  }
}
