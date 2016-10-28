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

public class WorkflowTimer {

  private Long id;

  private String registrationId;
  private String username;
  private String realms;


  /**
   * fields for timer trigger
   */

  private String lockOwner;
  private Date lockExpireTime;
  private Date nextFireTime;
  private String cronExpression;

  /**
   * 是否串行，如果串行，则后一次触发要等待前一个实例完成
   */
  private boolean serial;

  /**
   * optional fields
   */
  private Date createdDate;
  private Date modifiedDate;

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

  public String getCronExpression() {
    return cronExpression;
  }

  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  public boolean isSerial() {
    return serial;
  }

  public void setSerial(boolean serial) {
    this.serial = serial;
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
}
