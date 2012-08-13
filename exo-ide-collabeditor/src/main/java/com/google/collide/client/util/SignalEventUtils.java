// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.collide.client.util;

import org.waveprotocol.wave.client.common.util.SignalEvent;
import org.waveprotocol.wave.client.common.util.SignalEventImpl;

import elemental.events.Event;

/**
 * Utility methods for dealing with {@link SignalEvent}.
 *
 */
public class SignalEventUtils {

  public static SignalEvent create(Event rawEvent) {
    return SignalEventImpl.create((com.google.gwt.user.client.Event) rawEvent, true);
  }
  
  public static SignalEvent create(Event rawEvent, boolean cancelBubbleIfNullified) {
    return SignalEventImpl.create(
        (com.google.gwt.user.client.Event) rawEvent, cancelBubbleIfNullified);
  }

  /**
   * Returns the paste contents from a "paste" event, or null if it is not a
   * paste event or cannot be retrieved.
   */
  public static native String getPasteContents(Event event) /*-{
    if (!event.clipboardData || !event.clipboardData.getData) {
      return null;
    }

    return event.clipboardData.getData('text/plain');
  }-*/;
}