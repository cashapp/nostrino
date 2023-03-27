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

import app.cash.nostrino.crypto.SecKey
import app.cash.nostrino.message.NostrMessageAdapter.Companion.moshi
import com.squareup.moshi.JsonAdapter
import okio.ByteString.Companion.encodeUtf8
import java.time.Instant
import java.time.temporal.ChronoUnit

/** A type that can be signed and converted into an Event */
interface EventContent {

  val kind: Int

  fun tags(): List<List<String>>

  fun toJsonString(): String

  /**
   * Signing with a `SecKey` will result in a valid signed `Event` where the author is the `PubKey` associated
   * with the `SecKey`.
   */
  fun sign(
    sec: SecKey,
    createdAt: Instant = Instant.now()
  ): Event {
    val createdAtSecondsOnly = createdAt.truncatedTo(ChronoUnit.SECONDS).truncatedTo(ChronoUnit.SECONDS)
    val contentJson = toJsonString()
    val elements = listOf(0, sec.pubKey.key.hex(), createdAtSecondsOnly.epochSecond, kind, tags(), contentJson)
    val toJson = jsonListAdapter.toJson(elements)
    val id = toJson.encodeUtf8().sha256()
    val sig = sec.sign(id)
    return Event(id, sec.pubKey.key, createdAtSecondsOnly, kind, tags(), contentJson, sig)
  }

  companion object {
    val jsonListAdapter: JsonAdapter<List<*>> = moshi.adapter(List::class.java)
  }
}
