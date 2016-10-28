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

import org.apache.log4j.Logger;
import org.nebula.service.config.RedisChangeListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class DynamicJedisPool extends JedisPool implements RedisChangeListener {

  private final static Logger logger = Logger
      .getLogger(DynamicJedisPool.class);

  @Value("${redis.maxTotal}")
  int redisMaxTotal;

  @Value("${redis.maxIdle}")
  int redisMaxIdle;

  @Value("${redis.maxWaitSecs}")
  int redisMaxWaitSecs;


  public DynamicJedisPool(JedisPoolConfig jedisPoolConfig, String host, int port) {
    super(jedisPoolConfig, host, port);
  }

  public DynamicJedisPool() {
  }

  public void onChange(String redisHost, int redisPort) {
    initJedisPool(redisHost, redisPort);
  }

  void initJedisPool(String redisHost, int redisPort) {
    JedisPoolConfig config = new JedisPoolConfig();
    config.setMaxTotal(redisMaxTotal);
    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
    config.setMaxIdle(redisMaxIdle);
    //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
    config.setMaxWaitMillis(1000 * redisMaxWaitSecs);
    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    config.setTestOnBorrow(true);

    this.close();

    this.internalPool = new DynamicJedisPool(config, redisHost, redisPort).internalPool;

    logger.info(
        "The redisHost/Port is changed to " + redisHost + ":" + redisPort + ", redisMaxtotal is "
        + redisMaxTotal + ", redisMaxIdle is " + redisMaxIdle + ", redisMaxWaitSecs is "
        + redisMaxWaitSecs);
  }
}