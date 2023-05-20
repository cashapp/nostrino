package app.cash.nostrino.crypto

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import okio.ByteString.Companion.toByteString

object ArbKeys {

  val arbSecKey = arbitrary { SecKeyGenerator().generate() }
  val arbPubKey = arbSecKey.map { it.pubKey }
  val arbHash = Arb.string().map {
    it.toByteArray(Charsets.UTF_8).toByteString().sha256()
  }

}
