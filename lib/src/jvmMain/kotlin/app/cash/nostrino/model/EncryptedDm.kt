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
import app.cash.nostrino.crypto.PubKey
import app.cash.nostrino.crypto.SecKey
import okio.ByteString.Companion.toByteString
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * An encrypted direct message. Event kind 4, as defined in
 * [nip-04](https://github.com/nostr-protocol/nips/blob/master/04.md).
 */
data class EncryptedDm(
  val to: PubKey,
  val cipherText: CipherText,
  override val tags: List<Tag> = listOf(PubKeyTag(to)),
) : EventContent {

  constructor(from: SecKey, to: PubKey, message: String) : this(to, from.encryptMessage(to, message))

  override val kind: Int = EncryptedDm.kind

  override fun toJsonString() = cipherText.toString()

  /** Providing the public key of the sender and the secret key of the recipient, decode this message */
  fun decipher(from: PubKey, to: SecKey): String = cipherText.decipher(from, to)

  companion object {
    const val kind = 4
  }
}

fun SecKey.encryptMessage(to: PubKey, plainText: String): CipherText {
  val random = SecureRandom()
  val iv = ByteArray(16)
  random.nextBytes(iv)
  val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
  cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(sharedSecretWith(to), "AES"), IvParameterSpec(iv))
  val encrypted = cipher.doFinal(plainText.toByteArray())
  return CipherText(encrypted.toByteString(), iv.toByteString())
}
