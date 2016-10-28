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

import org.apache.ibatis.session.RowBounds;
import org.apache.log4j.Logger;
import org.nebula.service.core.ServiceContext;
import org.nebula.service.dao.entity.ActivityTimer;
import org.nebula.service.dao.entity.WorkflowTimer;
import org.nebula.service.dao.mapper.ActivityTimerMapper;
import org.nebula.service.dao.mapper.WorkflowTimerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class TimerDaemon {

  private final static Logger logger = Logger.getLogger(TimerDaemon.class);

  @Value("${timer.scan.interval}")
  private int scanInterval;
  @Value("${timer.lock.expire.delay}")
  private int lockExpireDelay;
  @Value("${timer.get.batch.size}")
  private int getBatchSize;

  @Autowired
  private WorkflowTimerMapper workflowTimerMapper;

  @Autowired
  private ActivityTimerMapper activityTimerMapper;

  @Autowired
  private WorkflowTimerExecutor workflowTimerExecutor;

  @Autowired
  private ActivityTimerExecutor activityTimerExecutor;

  @Autowired
  private ServiceContext serviceContext;

  private String lockOwner = UUID.randomUUID().toString();

  private boolean shouldRun;

  @PostConstruct
  public void start() {
    shouldRun = true;

    new Thread(new WorkflowTimerJob()).start();
    new Thread(new ActivityTimerJob()).start();
  }

  @PreDestroy
  public void stop() {
    shouldRun = false;
  }

  abstract class TimerJob<T> implements Runnable {

    public void run() {
      logger.info("Scanning timers starting ...");
      while (shouldRun) {
        try {

          while(!serviceContext.isReady()) {
            sleep(2);
          }

          int locked = lockTimers();
          int start = 0;

          while (start < locked) {
            RowBounds rowBounds = new RowBounds(start, getBatchSize);
            List<T> runnableTimers = acquireRunnableTimers(rowBounds);

            for (T runnableTimer : runnableTimers) {
              process(runnableTimer);
            }

            start += getBatchSize;
          }
        } catch (Exception e) {
          logger.error("Caught unexpected exception while triggering timers.", e);
        }

        try {
          Thread.sleep(scanInterval * 1000);
        } catch (InterruptedException e) {
          logger.warn("Catch exception in timers scanning thread.", e);
        }
      }
    }

    private void sleep(int secs) throws Exception{
      Thread.sleep(secs);
    }

    protected abstract void process(T t);

    protected abstract List<T> acquireRunnableTimers(RowBounds rowBounds);

    protected abstract int lockTimers();
  }

  class WorkflowTimerJob extends TimerJob<WorkflowTimer> {

    protected void process(WorkflowTimer workflowTimer) {
      try {
        workflowTimerExecutor.execute(workflowTimer);
      } catch (Exception e) {
        logger.error("Caught unexpected exception while triggering timer: " + workflowTimer.getId(),
                     e);
      }
    }

    protected List<WorkflowTimer> acquireRunnableTimers(RowBounds rowBounds) {
      return workflowTimerMapper.findRunnableTimersByLockOwner(rowBounds, lockOwner);
    }

    protected int lockTimers() {
      return workflowTimerMapper.lockTimers(lockOwner, lockExpireDelay);
    }
  }

  class ActivityTimerJob extends TimerJob<ActivityTimer> {

    protected void process(ActivityTimer activityTimer) {
      try {
        activityTimerExecutor.execute(activityTimer);
      } catch (Exception e) {
        logger.error("Caught unexpected exception while triggering timer: " + activityTimer.getId(),
                     e);
      }
    }

    protected List<ActivityTimer> acquireRunnableTimers(RowBounds rowBounds) {
      return activityTimerMapper.findRunnableTimersByLockOwner(rowBounds, lockOwner);
    }

    protected int lockTimers() {
      return activityTimerMapper.lockTimers(lockOwner, lockExpireDelay);
    }
  }

}
