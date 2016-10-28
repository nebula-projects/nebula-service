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

import java.util.ArrayList;
import java.util.List;

public abstract class Realms {

  private List<Realm> realms = new ArrayList<Realm>();

  protected void AddRealm(Realm realm) {
    if (realm == null) {
      throw new IllegalArgumentException("The realm can't be null.");
    }
    this.realms.add(realm);
  }

  public List<Realm> getRealms() {
    return realms;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;

    for (Realm realm : realms) {
      result = prime * result + ((realm == null) ? 0 : realm.hashCode());
    }

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
    Realms other = (Realms) obj;

    int realmSize = realms.size();
    int otherRealmSize = other.realms.size();

    if (realmSize != otherRealmSize) {
      return false;
    } else {
      for (int i = 0; i < realmSize; i++) {
        if (!realms.get(i).equals(other.realms.get(i))) {
          return false;
        }
      }
    }

    return true;
  }
}
