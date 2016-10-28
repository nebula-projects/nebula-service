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

public class WorkflowScheduledMessage extends AbstractMessage {

  private boolean signal = false;

  public WorkflowScheduledMessage() {
  }

  public WorkflowScheduledMessage(String registrationId, String instanceId, int eventId,
                                  String realm) {
    super(registrationId, instanceId, eventId, realm);
  }

  public boolean isSignal() {
    return signal;
  }

  public void setSignal(boolean signal) {
    this.signal = signal;
  }

}
