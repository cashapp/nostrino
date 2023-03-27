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

package app.cash.nostrino.message.relay

import app.cash.nostrino.model.Event

/**
 * An event sent from relay to client for a specific subscription, as defined in
 * [nip-01](https://github.com/nostr-protocol/nips/blob/master/01.md#from-relay-to-client-sending-events-and-notices)
 */
data class EventMessage(
  val subscriptionId: String,
  val event: Event
) : RelayMessage
