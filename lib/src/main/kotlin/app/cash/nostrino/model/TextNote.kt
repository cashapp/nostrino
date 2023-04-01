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

/**
 * A text note that can be published via relays. Event kind 1, as defined in
 * [nip-01](https://github.com/nostr-protocol/nips/blob/master/01.md#basic-event-kinds).
 */
data class TextNote(
  val text: String
) : EventContent {

  override val kind: Int = Companion.kind

  override fun tags(): List<List<String>> = emptyList() // TODO

  override fun toJsonString(): String = text

  companion object {
    const val kind = 1
  }
}
