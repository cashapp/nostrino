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

import okio.ByteString

/**
 * An event deletion request message. Event kind 5, as defined in
 * [nip-09](https://github.com/nostr-protocol/nips/blob/master/09.md).
 */
data class EventDeletion(
  val message: String = "",
  val eventIds: Set<ByteString>,
  override val tags: List<Tag> = eventIds.map { EventTag(it) }
) : EventContent {

  override val kind: Int = EventDeletion.kind

  override fun toJsonString(): String = message

  companion object {
    const val kind = 5
  }
}
