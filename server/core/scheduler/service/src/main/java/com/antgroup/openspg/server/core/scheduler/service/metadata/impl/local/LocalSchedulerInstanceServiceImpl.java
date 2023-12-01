/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.metadata.impl.local;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author yangjin
 * @version : SchedulerInstanceServiceImpl.java, v 0.1 2023年11月30日 14:10 yangjin Exp $
 */
@Service
public class LocalSchedulerInstanceServiceImpl implements SchedulerInstanceService {

    private static ConcurrentHashMap<Long, SchedulerInstance> instances = new ConcurrentHashMap<>();

    @Override
    public Long insert(SchedulerInstance record) {
        Long max = instances.keySet().stream().max(Comparator.comparing(x -> x)).orElse(null);
        Long id = ++max;
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
    public SchedulerInstance getByInstanceId(String instanceId) {
        for (Long key : instances.keySet()) {
            SchedulerInstance instance = instances.get(key);
            if (instanceId.equals(instance.getInstanceId())) {
                SchedulerInstance target = new SchedulerInstance();
                BeanUtils.copyProperties(instance, target);
                return target;
            }
        }
        return null;
    }

    @Override
    public Page<List<SchedulerInstance>> query(SchedulerInstance record) {
        return null;
    }

    @Override
    public Long getCount(SchedulerInstance record) {
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
    public List<SchedulerInstance> getNotFinishInstance(SchedulerInstance record) {
        return null;
    }

    @Override
    public List<SchedulerInstance> getInstanceByTask(String taskType, TaskStatus status, Date startFinishTime, Date endFinishTime) {
        return null;
    }
}
