/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.metadata.impl.local;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerJobQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerJobService;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author yangjin
 * @version : SchedulerJobServiceImpl.java, v 0.1 2023年11月30日 14:09 yangjin Exp $
 */
@Service
public class LocalSchedulerJobServiceImpl implements SchedulerJobService {

    private static ConcurrentHashMap<Long, SchedulerJob> jobs = new ConcurrentHashMap<>();

    @Override
    public Long insert(SchedulerJob record) {
        Long max = jobs.keySet().stream().max(Comparator.comparing(x -> x)).orElse(null);
        Long id = ++max;
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
            boolean flag = false;
            SchedulerJob job = jobs.get(key);
            if (record.getId() != null && record.getId().equals(job.getId())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getCreateUserName()) && record.getCreateUserName().equals(job.getCreateUserName())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getType()) && record.getType().equals(job.getType())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getLifeCycle()) && record.getLifeCycle().equals(job.getLifeCycle())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getStatus()) && record.getStatus().equals(job.getStatus())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getMergeMode()) && record.getMergeMode().equals(job.getMergeMode())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getName()) && StringUtils.isNotBlank(job.getName()) && job.getName().contains(
                    record.getName())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getConfig()) && StringUtils.isNotBlank(job.getConfig()) && job.getConfig().contains(
                    record.getConfig())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getExtension()) && StringUtils.isNotBlank(job.getExtension()) && job.getExtension().contains(
                    record.getExtension())) {
                flag = true;
            }

            if (flag) {
                SchedulerJob target = new SchedulerJob();
                BeanUtils.copyProperties(job, target);
                jobList.add(target);
            }
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
