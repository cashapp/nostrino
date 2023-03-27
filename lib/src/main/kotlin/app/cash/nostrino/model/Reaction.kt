package app.cash.nostrino.model

import app.cash.nostrino.crypto.PubKey
import okio.ByteString

/** A reaction to an event. Event kind 7 as defined in nip-25. */
sealed class Reaction(
  val codePoint: Int,
  val eventId: ByteString,
  val authorPubKey: PubKey,
) : EventContent {

  override val kind: Int = Reaction.kind

  override fun tags(): List<List<String>> = listOf(
    listOf("e", eventId.hex()),
    listOf("p", authorPubKey.key.hex()),
  )

  override fun toJsonString(): String = Character.toChars(codePoint).joinToString()

  override fun toString() = "${javaClass.simpleName}(${toJsonString()}, ${eventId.hex()}, ${authorPubKey.npub})"

  companion object {
    const val kind = 7
  }
}

class Upvote(eventId: ByteString, authorPubKey: PubKey) : Reaction('+'.code, eventId, authorPubKey)
class Downvote(eventId: ByteString, authorPubKey: PubKey) : Reaction('-'.code, eventId, authorPubKey)
class EmojiReact(emoji: String, eventId: ByteString, authorPubKey: PubKey) : Reaction(
  emoji.codePointAt(0), eventId, authorPubKey
)
