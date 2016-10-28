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

package org.nebula.service.dao.entity;

import java.util.Date;

public class ActivityTimer {

  private Long id;

  private String registrationId;
  private String instanceId;
  private int eventId;

  private String username;
  private String realms;


  /**
   * fields for timer trigger
   */

  private String lockOwner;
  private Date lockExpireTime;
  private Date nextFireTime;
  private Integer interval;

  /**
   * optional fields
   */
  private Date createdDate;
  private Date modifiedDate;

  /**
   * 下一次触发延时，用于计算nextFireTime，不需要存库
   */
  private int nextFireDelay = 0;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(String registrationId) {
    this.registrationId = registrationId;
  }


  public String getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  public int getEventId() {
    return eventId;
  }

  public void setEventId(int eventId) {
    this.eventId = eventId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getRealms() {
    return realms;
  }

  public void setRealms(String realms) {
    this.realms = realms;
  }

  public String getLockOwner() {
    return lockOwner;
  }

  public void setLockOwner(String lockOwner) {
    this.lockOwner = lockOwner;
  }

  public Date getLockExpireTime() {
    return lockExpireTime;
  }

  public void setLockExpireTime(Date lockExpireTime) {
    this.lockExpireTime = lockExpireTime;
  }

  public Date getNextFireTime() {
    return nextFireTime;
  }

  public void setNextFireTime(Date nextFireTime) {
    this.nextFireTime = nextFireTime;
  }

  public Integer getInterval() {
    return interval;
  }

  public void setInterval(Integer interval) {
    this.interval = interval;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Date getModifiedDate() {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public int getNextFireDelay() {
    return nextFireDelay;
  }

  public void setNextFireDelay(int nextFireDelay) {
    this.nextFireDelay = nextFireDelay;
  }
}
