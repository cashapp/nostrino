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

package app.cash.nostrino.client

import app.cash.nostrino.model.EncryptedDm
import app.cash.nostrino.model.Event
import app.cash.nostrino.model.Filter
import app.cash.nostrino.model.Kind
import app.cash.nostrino.model.Kind.ENCRYPTED_DM
import app.cash.nostrino.model.Kind.REACTION
import app.cash.nostrino.model.Kind.TEXT_NOTE
import app.cash.nostrino.model.Kind.USER_META_DATA
import app.cash.nostrino.model.Reaction
import app.cash.nostrino.model.TextNote
import app.cash.nostrino.model.UserMetaData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import java.util.UUID

abstract class Relay {

  /** Begin sending and receiving events with this Relay */
  abstract fun start()

  /** Stop sending and receiving events with this Relay */
  abstract fun stop()

  /** Queue an event to be sent (potentially immediately) */
  abstract fun send(event: Event)

  /** Create a new subscription with exactly one filter */
  fun subscribe(
    filter: Filter,
    subscription: Subscription = Subscription(UUID.randomUUID().toString())
  ) = subscribe(setOf(filter), subscription)

  /** Create a new subscription with zero to many filters */
  abstract fun subscribe(
    filters: Set<Filter>,
    subscription: Subscription = Subscription(UUID.randomUUID().toString())
  ): Subscription

  /** Unsubscribe from a subscription */
  abstract fun unsubscribe(subscription: Subscription)

  /** All events transmitted by this relay for our active subscriptions */
  abstract val allEvents: Flow<Event>

  /** The subset of allEvents that are of type TextNote */
  val notes: Flow<Event> by lazy { allEvents.filter { it.kind == TEXT_NOTE } }

  /** The subset of allEvents that are of type EncryptedDm */
  val directMessages: Flow<Event> by lazy { allEvents.filter { it.kind == ENCRYPTED_DM } }

  /** The subset of allEvents that are of type UserMetaData */
  val userMetaData: Flow<Event> by lazy { allEvents.filter { it.kind == USER_META_DATA } }

  /** The subset of allEvents that are of type Reaction */
  val reactions: Flow<Event> by lazy { allEvents.filter { it.kind == REACTION } }
}
