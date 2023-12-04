/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.metadata.impl.local;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.common.util.StringUtils;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yangjin
 * @version : SchedulerInstanceServiceImpl.java, v 0.1 2023年11月30日 14:10 yangjin Exp $
 */
@Service
public class LocalSchedulerInstanceServiceImpl implements SchedulerInstanceService {

    private static ConcurrentHashMap<Long, SchedulerInstance> instances = new ConcurrentHashMap<>();

    @Autowired
    SchedulerTaskService schedulerTaskService;

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
            boolean flag = false;
            SchedulerInstance instance = instances.get(key);
            if (record.getId() != null && record.getId().equals(instance.getId())) {
                flag = true;
            }
            if (record.getProjectId() != null && record.getProjectId().equals(instance.getProjectId())) {
                flag = true;
            }
            if (record.getJobId() != null && record.getJobId().equals(instance.getJobId())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getUniqueId()) && record.getUniqueId().equals(instance.getUniqueId())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getCreateUser()) && record.getCreateUser().equals(instance.getCreateUser())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getType()) && record.getType().equals(instance.getType())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getStatus()) && record.getStatus().equals(instance.getStatus())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getLifeCycle()) && record.getLifeCycle().equals(instance.getLifeCycle())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getMergeMode()) && record.getMergeMode().equals(instance.getMergeMode())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getEnv()) && record.getEnv().equals(instance.getEnv())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getVersion()) && record.getVersion().equals(instance.getVersion())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getConfig()) && StringUtils.isNotBlank(instance.getConfig()) && instance.getConfig().contains(
                    record.getConfig())) {
                flag = true;
            }
            if (StringUtils.isNotBlank(record.getWorkflowConfig()) && StringUtils.isNotBlank(instance.getWorkflowConfig())
                    && instance.getWorkflowConfig().contains(record.getWorkflowConfig())) {
                flag = true;
            }

            if (flag) {
                SchedulerInstance target = new SchedulerInstance();
                BeanUtils.copyProperties(instance, target);
                instanceList.add(target);
            }
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
        instanceList = instanceList.stream().filter(s -> !InstanceStatus.isFinish(s.getStatus())).collect(Collectors.toList());
        return instanceList;
    }

    @Override
    public List<SchedulerInstance> getInstanceByTask(String taskType, TaskStatus status, Date startFinishTime, Date endFinishTime) {
        SchedulerTaskQuery schedulerTask = new SchedulerTaskQuery();
        schedulerTask.setType(taskType);
        schedulerTask.setStatus(status.name());
        List<SchedulerTask> tasks = schedulerTaskService.query(schedulerTask).getData();
        List<Long> ids = tasks.stream().filter(
                task -> task.getFinishTime().after(startFinishTime) && task.getFinishTime().before(endFinishTime)).map(
                SchedulerTask::getInstanceId).collect(Collectors.toList());
        List<SchedulerInstance> instanceList = getByIds(ids);
        return instanceList;
    }
}
