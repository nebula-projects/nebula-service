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

package org.nebula.service.message.provider.impl;

import org.apache.log4j.Logger;
import org.nebula.service.core.Realm;
import org.nebula.service.message.Message;
import org.nebula.service.message.provider.Provider;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

public class RedisProvider implements Provider {

  private final static Logger logger = Logger.getLogger(RedisProvider.class);

  private JedisPool jedisPool;

  private String queueKey;

  private String itemKey;

  public RedisProvider(JedisPool jedisPool, Realm realm) {
    this.jedisPool = jedisPool;
    this.queueKey = realm.getQueueKey();
    this.itemKey = realm.getItemKey();
  }

  public void send(final Message message) {

    Jedis jedis = null;

    try {
      jedis = jedisPool.getResource();
      Pipeline p = jedis.pipelined();

      p.hset(itemKey, message.getId(), message.toString());
      p.lpush(queueKey, message.getId());

      p.sync();

    } catch (Exception e) {
      logger.error("Failed to send message " + message.getId(), e);
      jedisPool.returnBrokenResource(jedis);
    } finally {
      jedisPool.returnResource(jedis);
    }

  }
}
