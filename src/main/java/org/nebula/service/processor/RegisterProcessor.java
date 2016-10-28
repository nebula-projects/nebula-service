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

package org.nebula.service.processor;

import org.nebula.framework.client.request.RegisterRequest;
import org.nebula.framework.client.response.RegisterResponse;
import org.nebula.service.config.DomainConfiguration;
import org.nebula.service.dao.entity.Registration;
import org.nebula.service.dao.entity.WorkflowTimer;
import org.nebula.service.dao.mapper.RegistrationMapper;
import org.nebula.service.dao.mapper.WorkflowTimerMapper;
import org.nebula.service.util.CronUtils;
import org.nebula.service.validator.RegisterRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.UUID;

import static org.nebula.service.util.JsonUtils.toJson;

@Component
public class RegisterProcessor
    extends
    AbstractProcessor<RegisterRequest, RegisterResponse, Registration> {

  @Autowired
  PlatformTransactionManager transactionManager;

  @Autowired
  private RegistrationMapper registrationMapper;
  @Autowired
  private WorkflowTimerMapper workflowTimerMapper;

  @Autowired
  private DomainConfiguration domainConfiguration;

  @Autowired
  public RegisterProcessor(RegisterRequestValidator validator) {
    super(validator);
  }

  protected PersistenceResult convertAndPersist(
      RegisterRequest request) {

    Registration registration = new Registration();
    registration.setUser(request.getUser());
    registration
        .setId(domainConfiguration.getName() + "-" + request.getAccessId() +"-" + UUID.randomUUID().toString());
    registration.setName(request.getName());
    registration.setVersion(request.getVersion());
    registration.setType(request.getNodeType().name());
    registration.setEnabled(true);
    registration.setData(toJson(request.getRegistrationInfo()));

    return persistInTransaction(request, registration);
  }

  protected RegisterResponse buildResponse(RegisterRequest request,
                                           Registration registration) {

    RegisterResponse response = new RegisterResponse();
    response.setRegistrationId(registration.getId());

    return response;
  }

  private PersistenceResult persistInTransaction(final RegisterRequest request,
                                                 final Registration registration) {
    TransactionTemplate template = new TransactionTemplate(transactionManager);
    Object o = template.execute(new TransactionCallback() {
      @Override
      public Object doInTransaction(TransactionStatus status) {

        PersistenceResult result = null;
        try {
          boolean inserted = false;
          try {
            registrationMapper.insertRegistration(registration);
            getLogger().info("inserted Registration id:" + registration.getId());
            inserted = true;

            result = new PersistenceResult(true, registration);
          } catch (DuplicateKeyException e) {
            getLogger().info("Duplicated registration: " + toJson(registration));

            Registration old = registrationMapper.find(registration);
            registration.setId(old.getId());
            registrationMapper.update(registration);

            result = new PersistenceResult(false, registration);
          }

          // 如果是workflow registration，则相应插入timer
          if (request.getNodeType() == RegisterRequest.NodeType.WORKFLOW) {
            persistTimer(request, registration);
          }
        } catch (Exception e) {
          status.setRollbackOnly();
          throw new RuntimeException("Persisted registration failure." + toJson(registration), e);
        }
        return result;
      }
    });

    return (PersistenceResult) o;
  }

  private void persistTimer(RegisterRequest request, Registration registration) {

    boolean isSerial = request.getRegistrationInfo().getWorkflowProfile().isSerial();
    String cronExpression = request.getRegistrationInfo().getWorkflowProfile().getCronExpression();

    boolean isTimerWorkerflow = cronExpression != null;
    if (!isTimerWorkerflow) {
      return;
    }

    Date nextFireTime = CronUtils.getNextFireTime(cronExpression, new Date());

    WorkflowTimer workflowTimer = new WorkflowTimer();
    workflowTimer.setRegistrationId(registration.getId());
    workflowTimer.setCronExpression(cronExpression);
    workflowTimer.setNextFireTime(nextFireTime);
    workflowTimer.setUsername(request.getUser());
    workflowTimer.setRealms(toJson(request.getRegistrationInfo().getRealms()));
    workflowTimer.setSerial(isSerial);

    try {
      workflowTimerMapper.insertTimer(workflowTimer);
      getLogger().info("inserted Workflow timer " + workflowTimer.getId());
    } catch (DuplicateKeyException e) {
      getLogger().info("Duplicated workflow timer: " + toJson(workflowTimer));

      WorkflowTimer existingTimer = workflowTimerMapper.findByRegistrationId(registration.getId());
      workflowTimer.setId(existingTimer.getId());
      workflowTimer.setNextFireTime(nextFireTime);
      workflowTimer.setCronExpression(cronExpression);
      workflowTimer.setSerial(isSerial);
      workflowTimerMapper.update(workflowTimer);
    }
  }

}
