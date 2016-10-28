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

package org.nebula.service.dao.cache;

import org.apache.log4j.Logger;
import org.nebula.service.dao.entity.Heartbeat;
import org.nebula.service.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class HeartbeatCache {

  private final static Logger logger = Logger.getLogger(HeartbeatCache.class);

  @Autowired
  private JedisPool jedisPool;

  private int expireSeconds;

  public HeartbeatCache(int expireSeconds) {
    this.expireSeconds = expireSeconds;
  }

  public void add(final Heartbeat heartbeat) {

    String nodeId = heartbeat.getRegistrationId() + "|" + heartbeat.getIp()
                    + "|" + heartbeat.getWorkingDir();

    logger.info("HeartbeatCache ADD: " + nodeId);

    Jedis jedis = null;

    try {
      jedis = jedisPool.getResource();
      heartbeat.setHeartbeatTime(new Date());

      jedis.sadd(heartbeat.getRegistrationId(), nodeId);

      String content = JsonUtils.toJson(heartbeat);

      jedis.set(nodeId, content);

      jedis.expire(nodeId, expireSeconds);

      logger.info("HeartbeatCache ADDED");
    } catch (Exception e) {
      logger.error("Failed to add heartbeat for nodeId " + nodeId, e);
      jedisPool.returnBrokenResource(jedis);
    } finally {
      jedisPool.returnResource(jedis);
    }
  }

  public List<Heartbeat> findByRegistrationId(final String registrationId) {

    List<Heartbeat> heartbeats = new ArrayList<Heartbeat>();

    Jedis jedis = null;

    try {
      jedis = jedisPool.getResource();

      Set<String> nodeIds = jedis.smembers(registrationId);
      for (String nodeId : nodeIds) {
        String value = jedis.get(nodeId);
        if (value == null) {
          continue;
        }
        heartbeats.add(JsonUtils.toObject(value,
                                          Heartbeat.class));
      }
      logger.info("HeartbeatCache findByRegistrationId heartbeats: "
                  + heartbeats.size());
      return heartbeats;
    } catch (Exception e) {
      logger.error("Failed to find heartbeat for registration "
                   + registrationId, e);
      jedisPool.returnBrokenResource(jedis);
    } finally {
      jedisPool.returnResource(jedis);
    }

    return heartbeats;
  }

}
