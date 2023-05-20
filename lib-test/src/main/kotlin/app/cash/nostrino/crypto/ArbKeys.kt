package app.cash.nostrino.crypto

import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.map

object ArbKeys {

  val arbSecKey = arbitrary { SecKeyGenerator().generate() }
  val arbPubKey = arbSecKey.map { it.pubKey }

}
