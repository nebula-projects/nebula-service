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

package org.nebula.service.auth;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserCredentialsPool {

  private Map<String, UserCredentials> pool = new HashMap<String, UserCredentials>();

  private int userCredentialsHashCode;

  public void setUserCredentialsPool(Map<String, UserCredentials> pool) {
    this.pool = pool;
  }

  public UserCredentials getUserCredentials(String accessId) {
    return pool.get(accessId);
  }

  public boolean authenticate(String accessId, String secretKey) {
    UserCredentials userCredentials = getUserCredentials(accessId);

    return userCredentials!=null ?  userCredentials.authenticate(accessId, secretKey) : false;
  }

  public boolean isAdmin(String accessId) {
    UserCredentials userCredentials = getUserCredentials(accessId);
    return userCredentials!=null && userCredentials.isAdmin();
  }

  public boolean exists(String secretKey) {
    return getUserCredentials(secretKey) != null;
  }

  public int getUserCredentialsHashCode() {
    return userCredentialsHashCode;
  }

  public void setUserCredentialsHashCode(int userCredentialsHashCode) {
    this.userCredentialsHashCode = userCredentialsHashCode;
  }
}