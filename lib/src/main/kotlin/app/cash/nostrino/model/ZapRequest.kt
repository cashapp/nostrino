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

data class ZapRequest(
  val content: String,
  val relays: List<String>,
  val amount: Long?,
  val lnurl: String?,
  val to: PubKey,
  val eventId: ByteString?,
  override val tags: List<Tag> = listOfNotNull(
    RelaysTag(relays),
    amount?.let(::AmountTag),
    lnurl?.let(::LnurlTag),
    PubKeyTag(to),
    eventId?.let(::EventTag)
  )
) : EventContent {
  override val kind = Companion.kind

  override fun toJsonString() = content

  companion object {
    const val kind = 9734
  }
}
