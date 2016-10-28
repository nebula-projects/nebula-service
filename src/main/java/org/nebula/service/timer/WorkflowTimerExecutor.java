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

package org.nebula.service.timer;


import org.apache.log4j.Logger;
import org.nebula.framework.client.request.RegisterRequest;
import org.nebula.framework.client.request.StartWorkflowRequest;
import org.nebula.framework.model.Input;
import org.nebula.service.dao.entity.Registration;
import org.nebula.service.dao.entity.WorkflowTimer;
import org.nebula.service.dao.mapper.RegistrationMapper;
import org.nebula.service.dao.mapper.WorkflowTimerMapper;
import org.nebula.service.processor.StartWorkflowProcessor;
import org.nebula.service.util.CronUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.nebula.service.util.JsonUtils.toJson;
import static org.nebula.service.util.JsonUtils.toObject;


@Component
public class WorkflowTimerExecutor {

  private final static int TEN_YEARS = 10 * 365 * 24 * 60 * 60;

  private final static Date YEAR_2100 = new GregorianCalendar(2100, 0, 0, 0, 0, 0).getTime();

  private final static Logger logger = Logger
      .getLogger(WorkflowTimerExecutor.class);


  @Autowired
  private RegistrationMapper registrationMapper;
  @Autowired
  private StartWorkflowProcessor startWorkflowProcessor;
  @Autowired
  private WorkflowTimerMapper workflowTimerMapper;

  public void execute(WorkflowTimer workflowTimer) {

    startWorkflow(workflowTimer);

    if (isOneShotTimer(workflowTimer.getCronExpression())) {
      deleteTimer(workflowTimer.getId());
      logger.info("delete Timer: " + workflowTimer.getId());
    } else {

      Date
          nextFireTime =
          workflowTimer.isSerial() ? YEAR_2100 : CronUtils
              .getNextFireTime(workflowTimer.getCronExpression(), new Date());
      updateTimer(workflowTimer.getId(), nextFireTime);

      logger.info("update Timer: " + workflowTimer.getId() + " with nextFireTime " + nextFireTime);
    }
  }

  private void startWorkflow(WorkflowTimer workflowTimer) {
    Registration registration = registrationMapper.findById(workflowTimer.getRegistrationId());

    if (!registration.isEnabled()) {
      throw new IllegalStateException("The registration " + registration.getId() + " is disabled.");
    }

    RegisterRequest.RegistrationInfo
        info =
        (RegisterRequest.RegistrationInfo) toObject(registration.getData(),
                                                    RegisterRequest.RegistrationInfo.class
                                                        .getName());

    StartWorkflowRequest request = new StartWorkflowRequest();
    request.setAccessId(registration.getUser());
    request.setUser(registration.getUser());
    request.setRegistrationId(workflowTimer.getRegistrationId());
    request.setRealms(info.getRealms());
    request.setWorkflowProfile(info.getWorkflowProfile());
    request.setStartProfile(info.getStartProfile());
    Input input = new Input();
    input.setInputs(new String[0]);
    request.setInput(input);
    request.setStartMode(
        workflowTimer.isSerial() ? StartWorkflowRequest.StartMode.TIMER_START_SERIAL
                                 : StartWorkflowRequest.StartMode.TIMER_START_PARALLEL);

    startWorkflowProcessor.process(request);

    logger.info("startWorkflow: " + toJson(request));
  }

  private void updateTimer(Long timerId, Date nextFireTime) {
    workflowTimerMapper.updateNextFireTime(timerId, nextFireTime);
  }

  private boolean isOneShotTimer(Integer interval) {
    return interval == null || interval <= 0;
  }

  private boolean isOneShotTimer(String cronExpression) {
    return cronExpression == null;
  }

  private void deleteTimer(Long timerId) {
    workflowTimerMapper.deleteTimerById(timerId);
  }
}
