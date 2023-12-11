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

/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.metadata.impl.local;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerJobQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerJobService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author yangjin
 * @version : SchedulerJobServiceImpl.java, v 0.1 2023年11月30日 14:09 yangjin Exp $
 */
@Service
public class LocalSchedulerJobServiceImpl implements SchedulerJobService {

    private static ConcurrentHashMap<Long, SchedulerJob> jobs  = new ConcurrentHashMap<>();
    private static AtomicLong                            maxId = new AtomicLong(0L);

    @Override
    public Long insert(SchedulerJob record) {
        Long id = maxId.incrementAndGet();
        record.setId(id);
        record.setGmtModified(new Date());
        jobs.put(id, record);
        return id;
    }

    @Override
    public int deleteById(Long id) {
        SchedulerJob record = jobs.remove(id);
        return record == null ? 0 : 1;
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        int flag = 0;
        for (Long id : ids) {
            SchedulerJob record = jobs.remove(id);
            if (record != null) {
                flag++;
            }
        }
        return flag;
    }

    @Override
    public Long update(SchedulerJob record) {
        Long id = record.getId();
        SchedulerJob oldRecord = jobs.get(id);
        if (oldRecord == null) {
            throw new RuntimeException("not find id:" + id);
        }
        if (record.getGmtModified() != null && !oldRecord.getGmtModified().equals(record.getGmtModified())) {
            return 0L;
        }
        record = CommonUtils.merge(oldRecord, record);
        record.setGmtModified(new Date());
        jobs.put(id, record);
        return id;
    }

    @Override
    public SchedulerJob getById(Long id) {
        SchedulerJob oldJob = jobs.get(id);
        SchedulerJob job = new SchedulerJob();
        BeanUtils.copyProperties(oldJob, job);
        return job;
    }

    @Override
    public Page<List<SchedulerJob>> query(SchedulerJobQuery record) {
        Page<List<SchedulerJob>> page = new Page<>();
        List<SchedulerJob> jobList = Lists.newArrayList();
        page.setData(jobList);
        for (Long key : jobs.keySet()) {
            SchedulerJob job = jobs.get(key);
            if (!CommonUtils.equals(job.getId(), record.getId())
                    || !CommonUtils.equals(job.getCreateUser(), record.getCreateUser())
                    || !CommonUtils.equals(job.getTranslate(), record.getTranslate())
                    || !CommonUtils.equals(job.getLifeCycle(), record.getLifeCycle())
                    || !CommonUtils.equals(job.getStatus(), record.getStatus())
                    || !CommonUtils.equals(job.getMergeMode(), record.getMergeMode())
                    || !CommonUtils.contains(job.getName(), record.getName())
                    || !CommonUtils.contains(job.getConfig(), record.getConfig())
                    || !CommonUtils.contains(job.getExtension(), record.getExtension())) {
                continue;
            }

            String keyword = record.getKeyword();
            if (!CommonUtils.contains(job.getName(), keyword) || !CommonUtils.contains(job.getCreateUser(), keyword)) {
                continue;
            }
            if (CollectionUtils.isNotEmpty(record.getTypes()) && !record.getTypes().contains(job.getTranslate())) {
                continue;
            }

            SchedulerJob target = new SchedulerJob();
            BeanUtils.copyProperties(job, target);
            jobList.add(target);

        }
        page.setPageNo(1);
        page.setPageSize(jobList.size());
        page.setTotal(Long.valueOf(jobList.size()));
        return page;
    }

    @Override
    public Long getCount(SchedulerJobQuery record) {
        return query(record).getTotal();
    }

    @Override
    public List<SchedulerJob> getByIds(List<Long> ids) {
        List<SchedulerJob> jobList = Lists.newArrayList();
        for (Long id : ids) {
            SchedulerJob job = jobs.get(id);
            SchedulerJob target = new SchedulerJob();
            BeanUtils.copyProperties(job, target);
            jobList.add(target);
        }
        return jobList;
    }
}
