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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.log4j.Logger;
import org.nebula.service.auth.UserCredentialsPool;
import org.nebula.service.dao.entity.Registration;
import org.nebula.service.dao.mapper.RegistrationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class UserProcessCache {

  private final static Logger logger = Logger
      .getLogger(UserProcessCache.class);

  private final static int MAX_CACHE_SIZE = 100000;

  @Autowired
  private RegistrationMapper registrationMapper;

  @Autowired
  private UserCredentialsPool userCredentialsPool;

  private LoadingCache<String, String> cache;

  public UserProcessCache() {
    cache = CacheBuilder.newBuilder()
        .maximumSize(MAX_CACHE_SIZE)
        .build(
            new CacheLoader<String, String>() {
              public String load(String key)
                  throws IllegalArgumentException { // no checked exception
                return loadRegistration(key);
              }
            });
  }

  public boolean hasProcess(String accessId, String registrationId) {

    if (userCredentialsPool.isAdmin(accessId)) {
      return true;
    }

    try {
      return accessId.equals(cache.get(registrationId));
    } catch (ExecutionException e) {
      logger.error("Failed to get user from cache for registrationId " + registrationId, e);
    }
    return false;
  }

  private String loadRegistration(String registrationId) {
    Registration registration = registrationMapper.findById(registrationId);

    if (registration != null) {
      return registration.getUser();
    }

    throw new IllegalArgumentException("The registration " + registrationId + " doesn't exist");
  }

}
