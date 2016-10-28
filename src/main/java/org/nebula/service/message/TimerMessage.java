/*
 * Copyright 2002-2015 the original author or authors.
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

package org.nebula.service.message;

public class TimerMessage {

  private String id;
  private int priority;
  private int delay;

  private String instanceId;
  private int eventId;

  public TimerMessage() {
    this("", -1, 0, 0);
  }

  public TimerMessage(String instanceId, int eventId, int delay, int priority) {
    this.instanceId = instanceId;
    this.eventId = eventId;
    this.delay = delay;
    this.priority = priority;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getPriority() {
    return priority;
  }

  public int getDelay() {
    return delay;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public int getEventId() {
    return eventId;
  }
}
