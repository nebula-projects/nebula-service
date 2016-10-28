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

import org.apache.commons.lang.Validate;

public class Realm {

  private String name;

  private String prefix;

  private String user;

  public Realm(String prefix, String user, String name) {
    Validate.notNull(prefix, "The prefix can't be null.");
    Validate.notEmpty(user, "The user can't be empty.");
    Validate.notEmpty(name, "The name can't be empty.");

    this.prefix = prefix;
    this.user = user;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String getQueueKey() {
    return prefix + ":" + user + ":" + name;
  }

  public String getItemKey() {
    return prefix + ":" + user + ":" + name + ":ITEMS";
  }

  public String getAckKey() {
    return prefix + ":" + user + ":" + name + ":ACKBUF";
  }

  public String getBackupKey() {
    return prefix + ":" + user + ":" + name + ":BACKUP";
  }

  public String getBackupLockKey() {
    return prefix + ":" + user + ":" + name + ":BACKUPLOCK";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
    result = prime * result + ((user == null) ? 0 : user.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Realm other = (Realm) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (prefix == null) {
      if (other.prefix != null) {
        return false;
      }
    } else if (!prefix.equals(other.prefix)) {
      return false;
    }
    if (user == null) {
      if (other.user != null) {
        return false;
      }
    } else if (!user.equals(other.user)) {
      return false;
    }
    return true;
  }

}
