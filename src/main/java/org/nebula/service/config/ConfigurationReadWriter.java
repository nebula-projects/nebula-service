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

package org.nebula.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;

import java.io.File;

public class ConfigurationReadWriter {

  private final static Logger logger = Logger
      .getLogger(ConfigurationReadWriter.class);

  private final static ObjectMapper mapper = new ObjectMapper();

  private Configuration configuration = new Configuration();

  private String filePath;

  public ConfigurationReadWriter(String filePath) {
    this.filePath = filePath;
    makeDefaultConfigurationFileIfNotExist(new File(filePath));
  }

  public Configuration readConfiguration() {

    try {
      configuration = mapper.readValue(new File(filePath),
                                       Configuration.class);
    } catch (Exception e) {
      logger.error("Failed to read value from the path:" + filePath);
    }

    log(configuration);

    return configuration;
  }

  public synchronized void writeConfiguration(Configuration configuration) {

    log(configuration);

    try {
      mapper.writeValue(new File(filePath), configuration);
    } catch (Exception e) {
      logger.error("Failed to read value from the path:" + filePath);
    }
  }

  private void makeDefaultConfigurationFileIfNotExist(File file) {

    if (!file.exists()) {
      file.getParentFile().mkdirs();
      writeConfiguration(new Configuration());
    }
  }

  private void log(Configuration configuration) {
    try {
      logger.info("The configuration: "
                  + mapper.writeValueAsString(configuration));
    } catch (Throwable e) {
      // Ignore it.
    }
  }
}
