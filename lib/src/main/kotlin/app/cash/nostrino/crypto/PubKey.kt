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

package app.cash.nostrino.crypto

import okio.ByteString
import okio.ByteString.Companion.toByteString

/**
 * Nostr public key.
 */
data class PubKey(val key: ByteString) : Key {

  /** nip-19 bech32 encoded form of this key */
  val npub by lazy {
    Bech32Serde.encodeBytes("npub", key, Bech32Serde.Encoding.Bech32)
  }

  override fun encoded(): String = npub

  override fun toString() = "PubKey(key=${key.hex()})"

  companion object {
    /** Create pub key from nip-19 bech32 encoded string */
    fun parse(bech32: String): PubKey {
      val (hrp, key) = Bech32Serde.decodeBytes(bech32, false)
      require(hrp == "npub") { "Unsupported encoding hrp=$hrp" }
      return PubKey(key.toByteString())
    }
  }
}
