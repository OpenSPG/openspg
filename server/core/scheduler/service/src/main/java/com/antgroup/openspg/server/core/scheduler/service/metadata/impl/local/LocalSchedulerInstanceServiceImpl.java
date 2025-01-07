/*
 * Copyright 2023 OpenSPG Authors
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

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.exception.SchedulerException;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.InstanceStatus;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.utils.SchedulerUtils;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** Scheduler Instance Service implementation class: Add, delete, update, and query instances */
@Service
@ConditionalOnProperty(name = "scheduler.metadata.store.type", havingValue = "local")
public class LocalSchedulerInstanceServiceImpl implements SchedulerInstanceService {

  private static ConcurrentHashMap<Long, SchedulerInstance> instances = new ConcurrentHashMap<>();
  private static AtomicLong maxId = new AtomicLong(0L);

  @Override
  public synchronized Long insert(SchedulerInstance record) {
    String uniqueId = record.getUniqueId();
    for (Long id : instances.keySet()) {
      SchedulerInstance instance = instances.get(id);
      if (uniqueId.equals(instance.getUniqueId())) {
        throw new SchedulerException("uniqueId {} already existed", uniqueId);
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
    SchedulerInstance old = getById(id);
    if (record.getGmtModified() != null && !old.getGmtModified().equals(record.getGmtModified())) {
      return 0L;
    }
    record = SchedulerUtils.merge(old, record);
    record.setGmtModified(new Date());
    instances.put(id, record);
    return id;
  }

  @Override
  public SchedulerInstance getById(Long id) {
    SchedulerInstance oldInstance = instances.get(id);
    if (oldInstance == null) {
      throw new SchedulerException("not find id {}", id);
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
  public Paged<SchedulerInstance> query(SchedulerInstanceQuery record) {
    List<SchedulerInstance> instanceList = Lists.newArrayList();
    for (Long key : instances.keySet()) {
      SchedulerInstance instance = instances.get(key);
      // Filter instance by fields
      if (!SchedulerUtils.compare(instance.getId(), record.getId(), SchedulerUtils.EQ)
          || !SchedulerUtils.compare(
              instance.getProjectId(), record.getProjectId(), SchedulerUtils.EQ)
          || !SchedulerUtils.compare(instance.getJobId(), record.getJobId(), SchedulerUtils.EQ)
          || !SchedulerUtils.compare(
              instance.getUniqueId(), record.getUniqueId(), SchedulerUtils.EQ)
          || !SchedulerUtils.compare(instance.getType(), record.getType(), SchedulerUtils.EQ)
          || !SchedulerUtils.compare(instance.getStatus(), record.getStatus(), SchedulerUtils.EQ)
          || !SchedulerUtils.compare(
              instance.getLifeCycle(), record.getLifeCycle(), SchedulerUtils.EQ)
          || !SchedulerUtils.compare(
              instance.getDependence(), record.getDependence(), SchedulerUtils.EQ)
          || !SchedulerUtils.compare(
              instance.getVersion(), record.getVersion(), SchedulerUtils.EQ)) {
        continue;
      }

      Date create = instance.getGmtCreate();
      if (!SchedulerUtils.compare(create, record.getStartCreateTime(), SchedulerUtils.LT)) {
        continue;
      }

      SchedulerInstance target = new SchedulerInstance();
      BeanUtils.copyProperties(instance, target);
      instanceList.add(target);
    }
    Paged<SchedulerInstance> paged = new Paged<>(record.getPageSize(), record.getPageNo());
    paged.setResults(instanceList);
    return paged;
  }

  @Override
  public List<SchedulerInstance> getNotFinishInstance(SchedulerInstanceQuery record) {
    List<SchedulerInstance> instanceList = query(record).getResults();
    instanceList =
        instanceList.stream()
            .filter(s -> !InstanceStatus.isFinished(s.getStatus()))
            .collect(Collectors.toList());
    return instanceList;
  }
}
