package app.cash.nostrino.model

/**
 * Represents the kind of an event.
 */
enum class Kind(val value: Int) {
  USER_META_DATA(0),
  TEXT_NOTE(1),
  ENCRYPTED_DM(4),
  REACTION(7),
  ZAP_REQUEST(9734),
  ZAP_RECEIPT(9735);

  companion object {
    fun fromValue(value: Int): Kind? = Kind.values().firstOrNull { it.value == value }
  }
}
