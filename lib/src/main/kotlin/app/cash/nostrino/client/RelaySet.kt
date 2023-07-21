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

import app.cash.nostrino.model.Event
import app.cash.nostrino.model.Filter
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.map
import okio.ByteString

data class RelaySet(
  val relays: Set<Relay>
) : Relay() {

  override fun start() = relays.forEach { it.start() }

  override fun stop() = relays.forEach { it.stop() }

  override fun send(event: Event) = relays.forEach { it.send(event) }

  override fun subscribe(filters: Set<Filter>, subscription: Subscription): Subscription = subscription.also {
    relays.forEach { it.subscribe(filters, subscription) }
  }

  override fun unsubscribe(subscription: Subscription) = relays.forEach { it.unsubscribe(subscription) }

  @OptIn(ExperimentalCoroutinesApi::class)
  override val allEvents: Flow<Event> by lazy {
    val cache = CacheBuilder.newBuilder()
      .maximumSize(4096)
      .build<ByteString, Boolean>(CacheLoader.from { _ -> false })
    relays.map { it.allEvents }.asFlow()
      .flattenMerge()
      .filterNot { cache.get(it.id) }
      .map {
        cache.put(it.id, true)
        it
      }
  }
}
