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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nebula.framework.core.CronExpression;

import java.text.ParseException;
import java.util.Date;

public class CronUtils {

  private final static Log log = LogFactory.getLog(CronUtils.class);

  private final static Date DIED_DATE = new Date(2199, 0, 0);

  public static Date getNextFireTime(String cronExpression, Date specifiedReferenceDate) {

    CronExpression cron = null;
    try {
      cron = new CronExpression(cronExpression);
    } catch (ParseException e) {
      log.error("The cronExpression " + cronExpression + " is illegal. The worker won't run.");
      return DIED_DATE;
    }
    return cron.getNextValidTimeAfter(specifiedReferenceDate);
  }

}
