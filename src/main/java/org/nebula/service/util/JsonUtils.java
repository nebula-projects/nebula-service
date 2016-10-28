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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class JsonUtils {

  private final static Logger logger = Logger.getLogger(JsonUtils.class);

  private final static ObjectMapper mapper = new ObjectMapper();

  public final static String toJson(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      logger.error("Failed to convert the object " + obj + " to json.");
    }
    return "";
  }

  public final static Object toObject(String json, String className) {
    try {
      return mapper.readValue(json, Class.forName(className));
    } catch (Exception e) {
      logger.error("Failed to convert the json " + json + " to object " + className);
    }
    return "";
  }

  public final static <T> T toObject(String json, Class<T> clazz) {
    try {
      return mapper.readValue(json, clazz);
    } catch (Exception e) {
      logger.error("Failed to convert the json " + json + " to object " + clazz, e);
    }
    return null;
  }

  public final static List toList(String json, Class clazz) {
    try {
      final CollectionType javaType =
          mapper.getTypeFactory().constructCollectionType(List.class, clazz);
      return mapper.readValue(json, javaType);
    } catch (Exception e) {
      logger.error("Failed to convert the json " + json + " to " + clazz.getName() + " list");
    }
    return null;
  }

  public final static Map toMap(String json) {
    try {
      return mapper.readValue(json, new TypeReference<Map<String, Object>>() {
      });
    } catch (Exception e) {
      logger.error("Failed to convert the json " + json + " to map");
    }
    return null;
  }
}
