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

package app.cash.nostrino

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.instant
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.stringPattern
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

object ArbPrimitive {

  val arbByteString32: Arb<ByteString> = Arb.list(Arb.byte(), 32..32)
    .map { it.toByteArray().toByteString() }
  val arbByteString64: Arb<ByteString> = Arb.list(Arb.byte(), 64..64)
    .map { it.toByteArray().toByteString() }
  private val emojis: List<Int> by lazy {
    ArbPrimitive::class.java.getResource("/emojis.txt").readText()
      .lines().filterNot { it.isEmpty() }.map { it.codePointAt(0) }
  }
  val arbEmoji = Arb.element(emojis)
  val arbInstantSeconds: Arb<Instant> =
    Arb.instant(Instant.EPOCH, Instant.now().plus(5000, ChronoUnit.DAYS))
      .map { it.truncatedTo(ChronoUnit.SECONDS) }
  val arbUUID = arbitrary { UUID.randomUUID() }
  val arbVanillaString = Arb.stringPattern("[a-zA-Z0-9 ]+")

}
