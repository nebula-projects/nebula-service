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

import com.fasterxml.jackson.databind.ObjectMapper;


public class AbstractMessage implements Message {

  private final static ObjectMapper mapper = new ObjectMapper();

  private String id;
  private int priority;
  private int delay;
  private int ttr;
  private long timestamp;

  private String registrationId;
  private String instanceId;
  private int eventId;
  private String realm;

  public AbstractMessage() {
  }

  public AbstractMessage(String instanceId, int eventId, int delay, int priority) {
    this.instanceId = instanceId;
    this.eventId = eventId;
    this.delay = delay;
    this.priority = priority;
  }

  public AbstractMessage(String registrationId, String instanceId,
                         int eventId, String realm) {
    this.registrationId = registrationId;
    this.instanceId = instanceId;
    this.eventId = eventId;
    this.realm = realm;
    this.timestamp = System.currentTimeMillis();
  }

//	public AbstractMessage(long id, String registrationId, String instanceId, int eventId, String realm) {
//		this.id = id;
//		this.registrationId = registrationId;
//		this.instanceId = instanceId;
//		this.eventId = eventId;
//		this.realm = realm;
//		this.timestamp = System.currentTimeMillis();
//	}
//	
//	public AbstractMessage(long id, int priority, int delay, int ttr) {
//		this.id = id;
//		this.priority = priority;
//		this.delay = delay;
//		this.ttr = ttr; 
//		this.timestamp = System.currentTimeMillis();
//	}

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

  public int getTTR() {
    return ttr;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String toString() {
    try {
      return mapper.writeValueAsString(this);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  public String getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(String registrationId) {
    this.registrationId = registrationId;
  }

  public int getEventId() {
    return eventId;
  }

  public String getRealm() {
    return realm;
  }

}
