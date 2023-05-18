package app.cash.nostrino.model

/**
 * Represents the kind of an event as defined
 * by the protocol: https://github.com/nostr-protocol/nips#event-kinds
 */
@JvmInline
value class Kind(val value: Int)

val USER_META_DATA = Kind(0)
val TEXT_NOTE = Kind(1)
val ENCRYPTED_DM = Kind(4)
val REACTION = Kind(7)
val ZAP_REQUEST = Kind(9734)
val ZAP_RECEIPT = Kind(9735)
