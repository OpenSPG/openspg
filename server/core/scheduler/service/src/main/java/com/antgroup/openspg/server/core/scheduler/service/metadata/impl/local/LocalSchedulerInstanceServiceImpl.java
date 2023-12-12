/*
 * Copyright 2023 Ant Group CO., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.server.core.scheduler.service.metadata.impl.local;

import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.common.model.scheduler.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Scheduler Instance Service implementation class: Add, delete, update, and query instances */
@Service
public class LocalSchedulerInstanceServiceImpl implements SchedulerInstanceService {

  private static ConcurrentHashMap<Long, SchedulerInstance> instances = new ConcurrentHashMap<>();
  private static AtomicLong maxId = new AtomicLong(0L);

  @Autowired SchedulerTaskService schedulerTaskService;

  @Override
  public Long insert(SchedulerInstance record) {
    String uniqueId = record.getUniqueId();
    for (Long id : instances.keySet()) {
      SchedulerInstance instance = instances.get(id);
      if (uniqueId.equals(instance.getUniqueId())) {
        throw new RuntimeException(String.format("uniqueId:%s already existed", uniqueId));
      }
    }
    Long id = maxId.incrementAndGet();
    record.setId(id);
    record.setGmtModified(new Date());
    instances.put(id, record);
    return id;
  }

  @Override
  public int deleteById(Long id) {
    SchedulerInstance record = instances.remove(id);
    return record == null ? 0 : 1;
  }

  @Override
  public int deleteByJobId(Long jobId) {
    List<Long> instanceList = Lists.newArrayList();
    for (Long key : instances.keySet()) {
      SchedulerInstance instance = instances.get(key);
      if (jobId.equals(instance.getJobId())) {
        instanceList.add(instance.getId());
      }
    }
    for (Long id : instanceList) {
      instances.remove(id);
    }
    return instanceList.size();
  }

  @Override
  public int deleteByIds(List<Long> ids) {
    int flag = 0;
    for (Long id : ids) {
      SchedulerInstance record = instances.remove(id);
      if (record != null) {
        flag++;
      }
    }
    return flag;
  }

  @Override
  public Long update(SchedulerInstance record) {
    Long id = record.getId();
    SchedulerInstance oldInstance = instances.get(id);
    if (oldInstance == null) {
      throw new RuntimeException("not find id:" + id);
    }
    if (record.getGmtModified() != null
        && !oldInstance.getGmtModified().equals(record.getGmtModified())) {
      return 0L;
    }
    record = CommonUtils.merge(oldInstance, record);
    record.setGmtModified(new Date());
    instances.put(id, record);
    return id;
  }

  @Override
  public SchedulerInstance getById(Long id) {
    SchedulerInstance oldInstance = instances.get(id);
    SchedulerInstance instance = new SchedulerInstance();
    BeanUtils.copyProperties(oldInstance, instance);
    return instance;
  }

  @Override
  public SchedulerInstance getByUniqueId(String instanceId) {
    for (Long key : instances.keySet()) {
      SchedulerInstance instance = instances.get(key);
      if (instanceId.equals(instance.getUniqueId())) {
        SchedulerInstance target = new SchedulerInstance();
        BeanUtils.copyProperties(instance, target);
        return target;
      }
    }
    return null;
  }

  @Override
  public Page<List<SchedulerInstance>> query(SchedulerInstanceQuery record) {
    Page<List<SchedulerInstance>> page = new Page<>();
    List<SchedulerInstance> instanceList = Lists.newArrayList();
    page.setData(instanceList);
    for (Long key : instances.keySet()) {
      SchedulerInstance instance = instances.get(key);
      if (!CommonUtils.equals(instance.getId(), record.getId())
          || !CommonUtils.equals(instance.getProjectId(), record.getProjectId())
          || !CommonUtils.equals(instance.getJobId(), record.getJobId())
          || !CommonUtils.equals(instance.getUniqueId(), record.getUniqueId())
          || !CommonUtils.equals(instance.getCreateUser(), record.getCreateUser())
          || !CommonUtils.equals(instance.getType(), record.getType())
          || !CommonUtils.equals(instance.getStatus(), record.getStatus())
          || !CommonUtils.equals(instance.getLifeCycle(), record.getLifeCycle())
          || !CommonUtils.equals(instance.getMergeMode(), record.getMergeMode())
          || !CommonUtils.equals(instance.getEnv(), record.getEnv())
          || !CommonUtils.equals(instance.getVersion(), record.getVersion())
          || !CommonUtils.contains(instance.getConfig(), record.getConfig())
          || !CommonUtils.contains(instance.getWorkflowConfig(), record.getWorkflowConfig())) {
        continue;
      }

      String keyword = record.getKeyword();
      if (!CommonUtils.contains(instance.getUniqueId(), keyword)
          || !CommonUtils.contains(instance.getCreateUser(), keyword)) {
        continue;
      }

      if (!CommonUtils.after(instance.getSchedulerDate(), record.getStartSchedulerDate())
          || !CommonUtils.before(instance.getSchedulerDate(), record.getEndSchedulerDate())
          || !CommonUtils.after(instance.getGmtCreate(), record.getStartCreateTime())
          || !CommonUtils.before(instance.getGmtCreate(), record.getEndCreateTime())
          || !CommonUtils.after(instance.getFinishTime(), record.getStartFinishTime())
          || !CommonUtils.before(instance.getFinishTime(), record.getEndFinishTime())) {
        continue;
      }

      if (CollectionUtils.isNotEmpty(record.getTypes())
          && !record.getTypes().contains(instance.getType())) {
        continue;
      }

      SchedulerInstance target = new SchedulerInstance();
      BeanUtils.copyProperties(instance, target);
      instanceList.add(target);
    }
    page.setPageNo(1);
    page.setPageSize(instanceList.size());
    page.setTotal(Long.valueOf(instanceList.size()));
    return page;
  }

  @Override
  public Long getCount(SchedulerInstanceQuery record) {
    return query(record).getTotal();
  }

  @Override
  public List<SchedulerInstance> getByIds(List<Long> ids) {
    List<SchedulerInstance> instanceList = Lists.newArrayList();
    for (Long id : ids) {
      SchedulerInstance instance = instances.get(id);
      SchedulerInstance target = new SchedulerInstance();
      BeanUtils.copyProperties(instance, target);
      instanceList.add(target);
    }
    return instanceList;
  }

  @Override
  public List<SchedulerInstance> getNotFinishInstance(SchedulerInstanceQuery record) {
    List<SchedulerInstance> instanceList = query(record).getData();
    instanceList =
        instanceList.stream()
            .filter(s -> !InstanceStatus.isFinished(s.getStatus()))
            .collect(Collectors.toList());
    return instanceList;
  }

  @Override
  public List<SchedulerInstance> getInstanceByTask(
      String taskType, TaskStatus status, Date startFinishTime, Date endFinishTime) {
    SchedulerTaskQuery schedulerTask = new SchedulerTaskQuery();
    schedulerTask.setType(taskType);
    schedulerTask.setStatus(status.name());
    List<SchedulerTask> tasks = schedulerTaskService.query(schedulerTask).getData();
    List<Long> ids =
        tasks.stream()
            .filter(
                task ->
                    task.getFinishTime().after(startFinishTime)
                        && task.getFinishTime().before(endFinishTime))
            .map(SchedulerTask::getInstanceId)
            .collect(Collectors.toList());
    List<SchedulerInstance> instanceList = getByIds(ids);
    return instanceList;
  }
}
