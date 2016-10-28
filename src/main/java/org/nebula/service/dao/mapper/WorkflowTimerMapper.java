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

package org.nebula.service.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.nebula.service.dao.entity.WorkflowTimer;

import java.util.Date;
import java.util.List;

public interface WorkflowTimerMapper {

  long insertTimer(WorkflowTimer workflowTimer);

  /**
   * 更新串行timer的真实nextFireTime.为避免多线程竞争，只有符合id的记录才会更新。
   *
   * @param id
   * @param delay
   */
//    void updateNextFireTime(@Param("id") Long id, @Param("delay") int delay);

  /**
   * 更新串行timer的真实nextFireTime.为避免多线程竞争，只有符合id的记录才会更新。
   */
  void updateNextFireTime(@Param("id") Long id, @Param("nextFireTime") Date nextFireTime);

  int deleteTimersByRegistrationId(String registrationId);

  int deleteTimerById(Long id);

  int resetTimers(String registrationId);

  WorkflowTimer findByRegistrationId(String registrationId);

  void update(WorkflowTimer workflowTimer);

  List<WorkflowTimer> findRunnableTimersByLockOwner(RowBounds rowBounds,
                                                    @Param("lockOwner") String lockOwner);

  int lockTimers(@Param("lockOwner") String lockOwner,
                 @Param("lockExpireDelay") int lockExpireDelay);

}
