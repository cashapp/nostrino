package app.cash.nostrino.model

import app.cash.nostrino.crypto.PubKey
import okio.ByteString

/** A reaction to an event. Event kind 7 as defined in [nip-25](https://github.com/nostr-protocol/nips/blob/master/25.md#nip-25). */
sealed class Reaction(
  codePoint: Int,
  open val eventId: ByteString,
  open val authorPubKey: PubKey,
) : EventContent {
  private val jsonString = String(Character.toChars(codePoint))

  override val kind: Int = Reaction.kind

  override fun tags(): List<List<String>> = listOf(
    listOf("e", eventId.hex()),
    listOf("p", authorPubKey.key.hex()),
  )

  override fun toJsonString(): String = jsonString

  override fun toString() = "${javaClass.simpleName}(${toJsonString()}, ${eventId.hex()}, ${authorPubKey.npub})"

  companion object {
    const val kind = 7

    /**
     * Construct a `Reaction` from the given string, event id and public key being reacted to.
     * Only the first codepoint of `content` will be interpreted, with special meaning given to `+` (upvote)
     * and `-` (downvote). Any other codepoint is accepted, but the spec assumption is that it will be an emoji.
     */
    fun from(content: String, eventId: ByteString, key: PubKey) = when (content) {
      "+" -> Upvote(eventId, key)
      "-" -> Downvote(eventId, key)
      else -> EmojiReact(content, eventId, key)
    }
  }
}

/** Specialised reaction indicating an upvote. */
data class Upvote(
  override val eventId: ByteString,
  override val authorPubKey: PubKey
) : Reaction('+'.code, eventId, authorPubKey)

/** Specialised reaction indicating a downvote. */
data class Downvote(
  override val eventId: ByteString,
  override val authorPubKey: PubKey
) : Reaction('-'.code, eventId, authorPubKey)

/** Any reaction that isn't an upvote or downvote. */
data class EmojiReact(
  val emoji: String,
  override val eventId: ByteString,
  override val authorPubKey: PubKey
) : Reaction(emoji.codePointAt(0), eventId, authorPubKey)
