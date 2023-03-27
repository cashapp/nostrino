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

/**
 * Sent by [nip-15])(https://github.com/nostr-protocol/nips/blob/master/15.md#nip-15) compliant relays when the
 * subscription has finished fetching stored events and will subsequently only send real-time events.
 */
data class EndOfStoredEvents(
  val subscriptionId: String
) : RelayMessage
