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

package org.nebula.service.util;

import org.nebula.service.core.Realm;

import java.util.List;
import java.util.Random;

public class RealmUtils {

  private final static Random random = new Random();

  public static final String getProperRealm(List<String> realms) {

    return realms.get(random.nextInt(realms.size()));
  }

  public static final Realm randomSelectRealm(List<Realm> realms) {

    return realms.get(random.nextInt(realms.size()));
  }
}
