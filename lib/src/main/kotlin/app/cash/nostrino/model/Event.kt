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

import app.cash.nostrino.crypto.CipherText
import app.cash.nostrino.message.NostrMessageAdapter.Companion.moshi
import com.squareup.moshi.Json
import okio.ByteString
import java.time.Instant

/** The primitive type understood by relays, as per nip-01 */
data class Event(
  val id: ByteString,
  @Json(name = "pubkey")
  val pubKey: ByteString,
  @Json(name = "created_at")
  val createdAt: Instant,
  val kind: Int,
  val tags: List<List<String>>,
  val content: String,
  val sig: ByteString
) {

  /**
   * Deserialise the `content` string into an instance of `EventContent` that corresponds with the event `kind`.
   */
  fun content(): EventContent = when (this.kind) {
    TextNote.kind -> TextNote(content)
    EncryptedDm.kind -> EncryptedDm(this.tags.firstPubKey()!!, CipherText.parse(content))
    Reaction.kind -> Reaction.from(content, tags.lastEventId()!!, tags.lastPubKey()!!)
    else -> adapters[this.kind]?.fromJson(content)!!
  }

  companion object {
    private val adapters = mapOf(
      UserMetaData.kind to moshi.adapter(UserMetaData::class.java),
    )
  }
}
