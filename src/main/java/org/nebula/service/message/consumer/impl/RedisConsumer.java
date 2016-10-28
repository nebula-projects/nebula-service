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

package org.nebula.service.message.consumer.impl;

import org.apache.log4j.Logger;
import org.nebula.service.core.Realm;
import org.nebula.service.message.CommonMessage;
import org.nebula.service.message.Message;
import org.nebula.service.message.MessageConverter;
import org.nebula.service.message.consumer.Consumer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

public class RedisConsumer implements Consumer {

  private final static Logger logger = Logger.getLogger(RedisConsumer.class);

  private static final int LEASE_SECS = 60;

  private JedisPool jedisPool;

  private String queueKey;

  private String itemKey;

  private String ackKey;

  private String backupKey;

  private String backupLockKey;

  private int leaseSecs = LEASE_SECS;

  private MessageConverter messageConverter;
  ;

  public RedisConsumer(JedisPool jedisPool, Realm realm, int leaseSecs) {
    this.jedisPool = jedisPool;

    this.queueKey = realm.getQueueKey();
    this.itemKey = realm.getItemKey();
    this.ackKey = realm.getAckKey();
    this.backupKey = realm.getBackupKey();
    this.backupLockKey = realm.getBackupLockKey();
    this.leaseSecs = leaseSecs;
  }

  public void setMessageConverter(MessageConverter messageConverter) {
    this.messageConverter = messageConverter;
  }

  public void ack(final String messageId) {

    Jedis jedis = null;

    try {
      jedis = jedisPool.getResource();

      Pipeline p = jedis.pipelined();

      p.lrem(ackKey, 0, messageId);
      p.lrem(backupKey, 0, messageId);
      p.hdel(itemKey, messageId);
      p.sync();
    } catch (Exception e) {
      logger.error("Failed to ack the message " + messageId, e);
      jedisPool.returnBrokenResource(jedis);
    } finally {
      jedisPool.returnResource(jedis);
    }
  }

  public void release(final String messageId) throws Exception {
    Jedis jedis = null;

    try {
      jedis = jedisPool.getResource();
      Pipeline p = jedis.pipelined();

      p.lrem(ackKey, 0, messageId);
      p.lrem(backupKey, 0, messageId);
      p.lpush(queueKey, messageId);
      p.sync();
    } catch (Exception e) {
      logger.error("Failed to release the message " + messageId, e);
      jedisPool.returnBrokenResource(jedis);
    } finally {
      jedisPool.returnResource(jedis);
    }

  }

  public long lengthOfQueue() {

    Jedis jedis = null;

    try {
      jedis = jedisPool.getResource();

      return jedis.llen(queueKey);
    } catch (Exception e) {
      logger.error("Failed to get the length of the queue " + queueKey, e);
      jedisPool.returnBrokenResource(jedis);
    } finally {
      jedisPool.returnResource(jedis);
    }

    return -1;
  }

  public long lengthOfAckQueue() {

    Jedis jedis = null;

    try {
      jedis = jedisPool.getResource();

      return jedis.llen(ackKey);
    } catch (Exception e) {
      logger.error("Failed to get the length of the ack queue " + ackKey, e);
      jedisPool.returnBrokenResource(jedis);
    } finally {
      jedisPool.returnResource(jedis);
    }

    return -1;
  }

  public long lengthOfBackupQueue() {

    Jedis jedis = null;

    try {
      jedis = jedisPool.getResource();

      return jedis.llen(backupKey);
    } catch (Exception e) {
      logger.error("Failed to get the length of the backup queue "
                   + backupKey, e);
      jedisPool.returnBrokenResource(jedis);
    } finally {
      jedisPool.returnResource(jedis);
    }

    return -1;
  }

  public long numberOfMessage() {

    Jedis jedis = null;

    try {
      jedis = jedisPool.getResource();

      return jedis.hlen(itemKey);
    } catch (Exception e) {
      logger.error("Failed to get the number of message " + itemKey, e);
      jedisPool.returnBrokenResource(jedis);
    } finally {
      jedisPool.returnResource(jedis);
    }

    return -1;
  }

  private void autoClean() {
    if (leaseSecs <= 0) {
      return;
    }

    Jedis jedis = null;

    try {
      jedis = jedisPool.getResource();

      if (jedis.exists(backupLockKey)) {
        return;
      }

      if (jedis.exists(backupKey)) {
        if (jedis.setnx(backupLockKey, "1") == 1) {
          String result = null;
          while ((result = jedis.rpoplpush(backupKey, queueKey)) != null) {
            logger.info("MOVE: " + result + " from " + backupKey
                        + " to " + queueKey);
          }
          Pipeline p = jedis.pipelined();

          if (jedis.exists(ackKey)) {
            jedis.rename(ackKey, backupKey);
          }

          p.expire(backupLockKey, leaseSecs);
          p.sync();
        }
      } else {

        jedis.watch(backupLockKey);
        jedis.watch(ackKey);

        boolean ackKeyExists = jedis.exists(ackKey);

        if (!jedis.exists(backupLockKey)) {
          Transaction t = null;
          try {
            t = jedis.multi();
            if (ackKeyExists) {
              t.renamenx(ackKey, backupKey);
            }
            t.setex(backupLockKey, leaseSecs, "1");
          } finally {
            if (t != null) {
              t.exec();
            }
          }

        } else {
          jedis.unwatch();
        }
      }
    } catch (Exception e) {
      logger.error("Failed to execute autoclean " + itemKey, e);
      jedisPool.returnBrokenResource(jedis);
    } finally {
      jedisPool.returnResource(jedis);
    }
  }

  @Override
  public Message get(final int timeout) throws Exception {
    logger.info(queueKey + ": queueLength=" + this.lengthOfQueue()
                + ", ackQueueLength=" + this.lengthOfAckQueue()
                + ", backupQueueLength=" + this.lengthOfBackupQueue());
    autoClean();

    final CommonMessage msg = new CommonMessage();

    Jedis jedis = null;

    try {
      jedis = jedisPool.getResource();
      String messageId;
      if (timeout < 0) {
        messageId = jedis.rpoplpush(queueKey, ackKey);
      } else {
        messageId = jedis.brpoplpush(queueKey, ackKey, timeout);
      }

      if (messageId == null) {
        return null;
      }

      msg.setId(messageId);
      msg.setPayload(jedis.hget(itemKey, messageId));

      if (messageConverter != null && msg.getPayload() != null) {
        Message message = (Message) messageConverter.convert(msg);
        return message;
      }
    } catch (Exception e) {
      logger.error("Failed toget message from queue " + queueKey, e);
      jedisPool.returnBrokenResource(jedis);
    } finally {
      jedisPool.returnResource(jedis);
    }

    return msg;

  }
}
