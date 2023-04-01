package app.cash.nostrino.model

import app.cash.nostrino.crypto.PubKeyTest.Companion.arbPubKey
import app.cash.nostrino.model.EventTest.Companion.arbEventId
import app.cash.nostrino.model.Primitives.arbInstantSeconds
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll

class FilterTest : StringSpec({

  "can add authors to existing filters" {
    val testData = Arb.pair(arbFilter, Arb.set(arbPubKey))
    checkAll(testData) { (filter, moreAuthors) ->
      val filterAuthorKeys = filter.plusAuthors(*moreAuthors.toTypedArray()).authors
      filterAuthorKeys shouldBe when (filter.authors) {
        null -> moreAuthors.map { it.key.hex() }
        else -> filter.authors?.plus(moreAuthors.map { it.key.hex() })
      }?.toSet()
    }
  }

}) {
  companion object {
    val arbFilter: Arb<Filter> = Arb.bind(
      Arb.set(arbEventId).orNull(),
      arbInstantSeconds.orNull(),
      Arb.set(arbPubKey.map { it.key.hex() }).orNull(),
    ) { ids, since, authors ->
      Filter(
        ids = ids,
        since = since,
        authors = authors,
      )
    }
  }
}
