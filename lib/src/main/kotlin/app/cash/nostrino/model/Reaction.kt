/*
 * Copyright (c) 2023 Block, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package app.cash.nostrino.model

import app.cash.nostrino.crypto.PubKey
import okio.ByteString

/** A reaction to an event. Event kind 7 as defined in [nip-25](https://github.com/nostr-protocol/nips/blob/master/25.md#nip-25). */
sealed class Reaction(
  codePoint: Int,
  open val eventId: ByteString,
  open val authorPubKey: PubKey,
  override val tags: List<Tag> = listOf(EventTag(eventId), PubKeyTag(authorPubKey)),
) : EventContent {
  private val jsonString = String(Character.toChars(codePoint))

  override val kind: Int = Reaction.kind

  override fun toJsonString(): String = jsonString

  override fun toString() = "${javaClass.simpleName}(${toJsonString()}, ${eventId.hex()}, ${authorPubKey.npub})"

  companion object {
    const val kind = 7

    /**
     * Construct a `Reaction` from the given string, event id and public key being reacted to.
     * Only the first codepoint of `content` will be interpreted, with special meaning given to `+` (upvote)
     * and `-` (downvote). Any other codepoint is accepted, but the spec assumption is that it will be an emoji.
     */
    fun from(content: String, eventId: ByteString, key: PubKey, tags: List<Tag>) = when (content) {
      "+" -> Upvote(eventId, key, tags)
      "-" -> Downvote(eventId, key, tags)
      else -> EmojiReact(content, eventId, key, tags)
    }
  }
}

/** Specialised reaction indicating an upvote. */
data class Upvote(
  override val eventId: ByteString,
  override val authorPubKey: PubKey,
  override val tags: List<Tag> = listOf(EventTag(eventId), PubKeyTag(authorPubKey)),
) : Reaction('+'.code, eventId, authorPubKey)

/** Specialised reaction indicating a downvote. */
data class Downvote(
  override val eventId: ByteString,
  override val authorPubKey: PubKey,
  override val tags: List<Tag> = listOf(EventTag(eventId), PubKeyTag(authorPubKey)),
) : Reaction('-'.code, eventId, authorPubKey)

/** Any reaction that isn't an upvote or downvote. */
data class EmojiReact(
  val emoji: String,
  override val eventId: ByteString,
  override val authorPubKey: PubKey,
  override val tags: List<Tag> = listOf(EventTag(eventId), PubKeyTag(authorPubKey)),
) : Reaction(emoji.codePointAt(0), eventId, authorPubKey)
