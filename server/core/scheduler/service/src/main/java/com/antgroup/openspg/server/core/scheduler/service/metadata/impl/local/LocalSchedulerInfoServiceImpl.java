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
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInfoQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInfo;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInfoService;
import com.antgroup.openspg.server.core.scheduler.service.utils.SchedulerUtils;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** Scheduler Info Service implementation class: Add, delete, update, and query infos */
@Service
@ConditionalOnProperty(name = "scheduler.metadata.store.type", havingValue = "local")
public class LocalSchedulerInfoServiceImpl implements SchedulerInfoService {

  private static ConcurrentHashMap<Long, SchedulerInfo> infos = new ConcurrentHashMap<>();
  private static AtomicLong maxId = new AtomicLong(0L);

  @Override
  public synchronized Long insert(SchedulerInfo record) {
    Long id = maxId.incrementAndGet();
    record.setId(id);
    record.setGmtModified(new Date());
    infos.put(id, record);
    return id;
  }

  @Override
  public synchronized int deleteById(Long id) {
    SchedulerInfo record = infos.remove(id);
    return record == null ? 0 : 1;
  }

  @Override
  public synchronized Long update(SchedulerInfo record) {
    Long id = record.getId();
    SchedulerInfo old = getById(id);
    if (record.getGmtModified() != null && !old.getGmtModified().equals(record.getGmtModified())) {
      return 0L;
    }
    record = SchedulerUtils.merge(old, record);
    record.setGmtModified(new Date());
    infos.put(id, record);
    return id;
  }

  @Override
  public SchedulerInfo getById(Long id) {
    SchedulerInfo oldInfo = infos.get(id);
    if (oldInfo == null) {
      throw new SchedulerException("not find id {}", id);
    }
    SchedulerInfo info = new SchedulerInfo();
    BeanUtils.copyProperties(oldInfo, info);
    return info;
  }

  @Override
  public Paged<SchedulerInfo> query(SchedulerInfoQuery record) {
    List<SchedulerInfo> infoList = Lists.newArrayList();
    for (Long key : infos.keySet()) {
      SchedulerInfo info = infos.get(key);

      // Filter info by fields
      if (!SchedulerUtils.compare(info.getId(), record.getId(), SchedulerUtils.EQ)
          || !SchedulerUtils.compare(info.getStatus(), record.getStatus(), SchedulerUtils.EQ)
          || !SchedulerUtils.compare(info.getName(), record.getName(), SchedulerUtils.IN)
          || !SchedulerUtils.compare(info.getConfig(), record.getConfig(), SchedulerUtils.IN)) {
        continue;
      }

      SchedulerInfo target = new SchedulerInfo();
      BeanUtils.copyProperties(info, target);
      infoList.add(target);
    }
    Paged<SchedulerInfo> paged = new Paged<>(record.getPageSize(), record.getPageNo());
    paged.setResults(infoList);
    return paged;
  }

  @Override
  public SchedulerInfo getByName(String name) {
    for (Long key : infos.keySet()) {
      SchedulerInfo info = infos.get(key);
      if (name.equals(info.getName())) {
        SchedulerInfo record = new SchedulerInfo();
        BeanUtils.copyProperties(info, record);
        return record;
      }
    }
    throw new SchedulerException("not find name {}", name);
  }

  @Override
  public int updateLock(Long id) {
    SchedulerInfo oldRecord = getById(id);
    if (oldRecord.getLockTime() != null) {
      return 0;
    }
    oldRecord.setGmtModified(new Date());
    oldRecord.setLockTime(new Date());
    infos.put(oldRecord.getId(), oldRecord);
    return 1;
  }

  @Override
  public int updateUnlock(Long id) {
    SchedulerInfo oldRecord = getById(id);
    oldRecord.setGmtModified(new Date());
    oldRecord.setLockTime(null);
    infos.put(oldRecord.getId(), oldRecord);
    return 1;
  }
}
