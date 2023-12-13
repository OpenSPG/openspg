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
import com.antgroup.openspg.server.common.model.exception.SchedulerException;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/** Scheduler Task Service implementation class: Add, delete, update, and query tasks */
@Service
public class LocalSchedulerTaskServiceImpl implements SchedulerTaskService {

  private static ConcurrentHashMap<Long, SchedulerTask> tasks = new ConcurrentHashMap<>();
  private static AtomicLong maxId = new AtomicLong(0L);

  @Override
  public synchronized Long insert(SchedulerTask record) {
    Long id = maxId.incrementAndGet();
    record.setId(id);
    record.setGmtModified(new Date());
    tasks.put(id, record);
    return id;
  }

  @Override
  public synchronized int deleteByJobId(Long jobId) {
    List<Long> ids = Lists.newArrayList();
    for (Long key : tasks.keySet()) {
      SchedulerTask task = tasks.get(key);
      if (jobId.equals(task.getJobId())) {
        ids.add(task.getId());
      }
    }
    for (Long id : ids) {
      tasks.remove(id);
    }
    return ids.size();
  }

  @Override
  public synchronized Long update(SchedulerTask record) {
    Long id = record.getId();
    SchedulerTask oldRecord = getById(id);
    if (record.getGmtModified() != null
        && !oldRecord.getGmtModified().equals(record.getGmtModified())) {
      return 0L;
    }
    record = CommonUtils.merge(oldRecord, record);
    record.setGmtModified(new Date());
    tasks.put(id, record);
    return id;
  }

  @Override
  public synchronized Long replace(SchedulerTask record) {
    if (record.getId() == null) {
      return insert(record);
    } else {
      return update(record);
    }
  }

  @Override
  public SchedulerTask getById(Long id) {
    SchedulerTask oldTask = tasks.get(id);
    if (oldTask == null) {
      throw new SchedulerException("not find id {}", id);
    }
    SchedulerTask task = new SchedulerTask();
    BeanUtils.copyProperties(oldTask, task);
    return task;
  }

  @Override
  public Page<List<SchedulerTask>> query(SchedulerTaskQuery record) {
    Page<List<SchedulerTask>> page = new Page<>();
    List<SchedulerTask> taskList = Lists.newArrayList();
    page.setData(taskList);
    for (Long key : tasks.keySet()) {
      SchedulerTask task = tasks.get(key);

      if (!CommonUtils.compare(task.getId(), record.getId(), CommonUtils.EQ)
          || !CommonUtils.compare(task.getType(), record.getType(), CommonUtils.EQ)
          || !CommonUtils.compare(task.getTitle(), record.getTitle(), CommonUtils.IN)
          || !CommonUtils.compare(task.getJobId(), record.getJobId(), CommonUtils.EQ)
          || !CommonUtils.compare(task.getInstanceId(), record.getInstanceId(), CommonUtils.EQ)) {
        continue;
      }

      SchedulerTask target = new SchedulerTask();
      BeanUtils.copyProperties(task, target);
      taskList.add(target);
    }
    page.setPageNo(1);
    page.setPageSize(taskList.size());
    page.setTotal(Long.valueOf(taskList.size()));
    return page;
  }

  @Override
  public SchedulerTask queryByInstanceIdAndType(Long instanceId, String type) {
    for (Long key : tasks.keySet()) {
      SchedulerTask task = tasks.get(key);
      if (instanceId.equals(task.getInstanceId()) && type.equalsIgnoreCase(task.getType())) {
        SchedulerTask target = new SchedulerTask();
        BeanUtils.copyProperties(task, target);
        return target;
      }
    }
    return null;
  }

  @Override
  public List<SchedulerTask> queryByInstanceId(Long instanceId) {
    List<SchedulerTask> taskList = Lists.newArrayList();
    for (Long key : tasks.keySet()) {
      SchedulerTask task = tasks.get(key);
      if (instanceId.equals(task.getInstanceId())) {
        SchedulerTask target = new SchedulerTask();
        BeanUtils.copyProperties(task, target);
        taskList.add(target);
      }
    }
    return taskList;
  }

  @Override
  public List<SchedulerTask> queryBaseColumnByInstanceId(Long instanceId) {
    List<SchedulerTask> taskList = Lists.newArrayList();
    for (Long key : tasks.keySet()) {
      SchedulerTask task = tasks.get(key);
      if (instanceId.equals(task.getInstanceId())) {
        SchedulerTask target = new SchedulerTask();
        BeanUtils.copyProperties(task, target);
        target.setRemark(null);
        taskList.add(target);
      }
    }
    return taskList;
  }

  @Override
  public int setStatusByInstanceId(Long instanceId, TaskStatus status) {
    int flag = 0;
    for (Long key : tasks.keySet()) {
      SchedulerTask task = tasks.get(key);
      if (instanceId.equals(task.getInstanceId())) {
        task.setGmtModified(new Date());
        task.setStatus(status);
        flag++;
      }
    }
    return flag;
  }

  @Override
  public int updateLock(Long id) {
    SchedulerTask oldRecord = getById(id);
    if (oldRecord.getLockTime() != null) {
      return 0;
    }
    oldRecord.setGmtModified(new Date());
    oldRecord.setLockTime(new Date());
    tasks.put(id, oldRecord);
    return 1;
  }

  @Override
  public int updateUnlock(Long id) {
    SchedulerTask oldRecord = getById(id);
    oldRecord.setGmtModified(new Date());
    oldRecord.setLockTime(null);
    tasks.put(id, oldRecord);
    return 1;
  }
}
