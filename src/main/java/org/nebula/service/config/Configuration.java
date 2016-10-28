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

public class Configuration {

  private int heatbeatIntervalSecs = 10;

  private int workflowPollThreads = 1;

  private int maxWorkflowExecutionThreads = 5;

  private int activityPollThreads = 1;

  private int maxActivityExecutionThreads = 5;

  public int getHeatbeatIntervalSecs() {
    return heatbeatIntervalSecs;
  }

  public void setHeatbeatIntervalSecs(int heatbeatIntervalSecs) {
    if (heatbeatIntervalSecs < 1) {
      throw new IllegalArgumentException("The heatbeat interval should be greater than zero.");
    }
    this.heatbeatIntervalSecs = heatbeatIntervalSecs;
  }

  public int getWorkflowPollThreads() {
    return workflowPollThreads;
  }

  public void setWorkflowPollThreads(int workflowPollThreads) {

    this.workflowPollThreads = workflowPollThreads;
  }

  public int getMaxWorkflowExecutionThreads() {
    return maxWorkflowExecutionThreads;
  }

  public void setMaxWorkflowExecutionThreads(int maxWorkflowExecutionThreads) {
    this.maxWorkflowExecutionThreads = maxWorkflowExecutionThreads;
  }

  public int getActivityPollThreads() {
    return activityPollThreads;
  }

  public void setActivityPollThreads(int activityPollThreads) {
    this.activityPollThreads = activityPollThreads;
  }

  public int getMaxActivityExecutionThreads() {
    return maxActivityExecutionThreads;
  }

  public void setMaxActivityExecutionThreads(int maxActivityExecutionThreads) {
    this.maxActivityExecutionThreads = maxActivityExecutionThreads;
  }
}
