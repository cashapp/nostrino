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
 */

package app.cash.nostrino.model

import app.cash.nostrino.crypto.PubKey
import com.squareup.moshi.Json
import okio.ByteString
import java.time.Instant
import kotlin.time.Duration.Companion.hours

/** A subscription filter, as defined in nip-01 */
data class Filter(
  val ids: Set<String>? = null,
  val since: Instant? = null,
  val authors: Set<String>? = null,
  val kinds: Set<Int>? = null,
  @Json(name = "#e")
  val eTags: Set<String>? = null,
  @Json(name = "#p")
  val pTags: Set<String>? = null,
  val limit: Int? = null
) {
  companion object {

    val globalFeedNotes = Filter(
      since = Instant.now().minusSeconds(12.hours.inWholeSeconds),
      kinds = setOf(TextNote.kind),
      limit = 500
    )

    fun userNotes(pubKey: PubKey, since: Instant = Instant.EPOCH) = userNotes(
      pubKeys = setOf(pubKey),
      since = since
    )

    fun userNotes(pubKeys: Set<PubKey>, since: Instant = Instant.EPOCH) = Filter(
      since = since,
      authors = pubKeys.map { it.key.hex() }.toSet(),
      kinds = setOf(TextNote.kind),
      limit = 500
    )

    fun directMessages(pubKey: PubKey, since: Instant = Instant.EPOCH) = Filter(
      since = since,
      kinds = setOf(EncryptedDm.kind),
      pTags = setOf(pubKey.key.hex())
    )

    fun userMetaData(pubKey: PubKey, since: Instant = Instant.EPOCH) = Filter(
      since = since,
      kinds = setOf(UserMetaData.kind),
      authors = setOf(pubKey.key.hex())
    )

    fun reactions(pubKey: PubKey, since: Instant = Instant.EPOCH) = Filter(
      since = since,
      kinds = setOf(Reaction.kind),
      authors = setOf(pubKey.key.hex())
    )

    fun reactions(eventId: ByteString, since: Instant = Instant.EPOCH) = Filter(
      since = since,
      kinds = setOf(Reaction.kind),
      eTags = setOf(eventId.hex())
    )
  }
}
