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
import okio.ByteString
import okio.ByteString.Companion.decodeHex

/** If the tag list contains at least one event id `["e", hex-encoded-id]]` then extract the first. */
fun List<List<String>>.firstEventId(): ByteString? =
  firstOrNull { it.firstOrNull() == "e" }
    ?.let { it.getOrNull(1) }
    ?.let { it.decodeHex() }

/** If the tag list contains at least one event id `["e", hex-encoded-id]]` then extract the last. */
fun List<List<String>>.lastEventId(): ByteString? =
  lastOrNull { it.firstOrNull() == "e" }
    ?.let { it.getOrNull(1) }
    ?.let { it.decodeHex() }

/** If the tag list contains at least one pubkey `["p", hex-encoded-key]]` then extract the first. */
fun List<List<String>>.firstPubKey(): PubKey? =
  firstOrNull { it.firstOrNull() == "p" }
    ?.let { it.getOrNull(1) }
    ?.let { PubKey(it.decodeHex()) }

/** If the tag list contains at least one pubkey `["p", hex-encoded-key]]` then extract the last. */
fun List<List<String>>.lastPubKey(): PubKey? =
  lastOrNull { it.firstOrNull() == "p" }
    ?.let { it.getOrNull(1) }
    ?.let { PubKey(it.decodeHex()) }
