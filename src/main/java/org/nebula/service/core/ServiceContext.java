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

package org.nebula.service.core;

import org.springframework.stereotype.Component;

@Component
public class ServiceContext {

  public static enum ServiceStatus {READY, NOT_READY};

  private volatile ServiceStatus status = ServiceStatus.NOT_READY;

  public ServiceStatus getStatus() {
    return status;
  }

  public void setStatus(ServiceStatus status) {
    this.status = status;
  }

  public boolean isReady() {
    return this.status == ServiceStatus.READY;
  }

  public void changeToReadyStatus() {
    if(status== ServiceStatus.NOT_READY) {
      status = ServiceStatus.READY;
    }
  }

  public void changeToNotReadyStatus() {
    if(status== ServiceStatus.READY) {
      status = ServiceStatus.NOT_READY;
    }
  }
}