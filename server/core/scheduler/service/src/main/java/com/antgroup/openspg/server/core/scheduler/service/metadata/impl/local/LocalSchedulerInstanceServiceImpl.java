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
package com.antgroup.openspg.server.core.scheduler.service.metadata.impl.local;

import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.common.model.scheduler.InstanceStatus;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
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
  public synchronized Long insert(SchedulerInstance record) {
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
  public synchronized int deleteByJobId(Long jobId) {
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
  public synchronized Long update(SchedulerInstance record) {
    Long id = record.getId();
    SchedulerInstance oldInstance = getById(id);
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
    if (oldInstance == null) {
      throw new RuntimeException("not find id:" + id);
    }
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
          || !CommonUtils.equals(instance.getType(), record.getType())
          || !CommonUtils.equals(instance.getStatus(), record.getStatus())
          || !CommonUtils.equals(instance.getLifeCycle(), record.getLifeCycle())
          || !CommonUtils.equals(instance.getMergeMode(), record.getMergeMode())
          || !CommonUtils.equals(instance.getVersion(), record.getVersion())) {
        continue;
      }

      if (!CommonUtils.after(instance.getGmtCreate(), record.getStartCreateTime())
          || !CommonUtils.before(instance.getGmtCreate(), record.getEndCreateTime())) {
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
  public List<SchedulerInstance> getNotFinishInstance(SchedulerInstanceQuery record) {
    List<SchedulerInstance> instanceList = query(record).getData();
    instanceList =
        instanceList.stream()
            .filter(s -> !InstanceStatus.isFinished(s.getStatus()))
            .collect(Collectors.toList());
    return instanceList;
  }
  
}
