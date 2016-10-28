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

package org.nebula.service.message.provider;

import org.nebula.service.core.Realm;
import org.nebula.service.message.provider.impl.RedisProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import redis.clients.jedis.JedisPool;

@Component
public class ProviderFactory {

  public final static Realm WORKFLOW_COMPLETED_REALM = new Realm("WF_COMPLETED", "NEBULA", "QUEUE");

  private final static Map<Realm, Provider> providers = new ConcurrentHashMap<Realm, Provider>();

  @Autowired
  private JedisPool jedisPool;

  public Provider getWorkflowCompletedProvider() {
    return getProvider(WORKFLOW_COMPLETED_REALM);
  }

  public Provider getProvider(Realm realm) {
    Provider provider = providers.get(realm);

    if (provider == null) {

      synchronized (providers) {
        provider = providers.get(realm);

        if (provider == null) {
          provider = new RedisProvider(jedisPool, realm);
          providers.put(realm, provider);
        }
      }
    }
    return provider;
  }

}
