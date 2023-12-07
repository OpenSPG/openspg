/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.metadata.impl.local;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author yangjin
 * @version : SchedulerTaskServiceImpl.java, v 0.1 2023年11月30日 14:11 yangjin Exp $
 */
@Service
public class LocalSchedulerTaskServiceImpl implements SchedulerTaskService {

    private static ConcurrentHashMap<Long, SchedulerTask> tasks = new ConcurrentHashMap<>();

    @Override
    public Long insert(SchedulerTask record) {
        Long id = CommonUtils.getMaxId(tasks.keySet());
        record.setId(id);
        record.setGmtModified(new Date());
        tasks.put(id, record);
        return id;
    }

    @Override
    public int deleteById(Long id) {
        SchedulerTask record = tasks.remove(id);
        return record == null ? 0 : 1;
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        int flag = 0;
        for (Long id : ids) {
            SchedulerTask record = tasks.remove(id);
            if (record != null) {
                flag++;
            }
        }
        return flag;
    }

    @Override
    public int deleteByJobId(Long jobId) {
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
    public Long update(SchedulerTask record) {
        Long id = record.getId();
        SchedulerTask oldRecord = tasks.get(id);
        if (oldRecord == null) {
            throw new RuntimeException("not find id:" + id);
        }
        if (record.getGmtModified() != null && !oldRecord.getGmtModified().equals(record.getGmtModified())) {
            return 0L;
        }
        record = CommonUtils.merge(oldRecord, record);
        record.setGmtModified(new Date());
        tasks.put(id, record);
        return id;
    }

    @Override
    public Long replace(SchedulerTask record) {
        if (record.getId() == null) {
            return insert(record);
        } else {
            return update(record);
        }
    }

    @Override
    public SchedulerTask getById(Long id) {
        SchedulerTask oldTask = tasks.get(id);
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

            if (!CommonUtils.equals(task.getId(), record.getId())
                    || !CommonUtils.equals(task.getCreateUser(), record.getCreateUser())
                    || !CommonUtils.equals(task.getType(), record.getType())
                    || !CommonUtils.contains(task.getTitle(), record.getTitle())
                    || !CommonUtils.equals(task.getJobId(), record.getJobId())
                    || !CommonUtils.equals(task.getInstanceId(), record.getInstanceId())
                    || !CommonUtils.contains(task.getExtension(), record.getExtension())) {
                continue;
            }

            if (!CommonUtils.after(task.getGmtCreate(), record.getStartCreateTime())
                    || !CommonUtils.before(task.getGmtCreate(), record.getEndCreateTime())) {
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
    public Long getCount(SchedulerTaskQuery record) {
        return query(record).getTotal();
    }

    @Override
    public List<SchedulerTask> getByIds(List<Long> ids) {
        List<SchedulerTask> taskList = Lists.newArrayList();
        for (Long id : ids) {
            SchedulerTask task = tasks.get(id);
            SchedulerTask target = new SchedulerTask();
            BeanUtils.copyProperties(task, target);
            taskList.add(target);
        }
        return taskList;
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
                task.setStatus(status.name());
                flag++;
            }
        }
        return flag;
    }

    @Override
    public int updateExtensionByLock(SchedulerTask record, String extension) {
        Long id = record.getId();
        SchedulerTask oldRecord = tasks.get(id);
        if (oldRecord == null) {
            return 0;
        }
        if (record.getGmtModified() != oldRecord.getGmtModified()) {
            throw new RuntimeException("modified time inconsistent,update extension failed");
        }
        oldRecord.setGmtModified(new Date());
        oldRecord.setExtension(extension);
        tasks.put(id, oldRecord);
        return 1;
    }

    @Override
    public int updateLock(Long id) {
        SchedulerTask oldRecord = tasks.get(id);
        if (oldRecord == null) {
            throw new RuntimeException(String.format("not find task id:%s", id));
        }
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
        SchedulerTask oldRecord = tasks.get(id);
        if (oldRecord == null) {
            throw new RuntimeException(String.format("not find task id:%s", id));
        }
        oldRecord.setGmtModified(new Date());
        oldRecord.setLockTime(null);
        tasks.put(id, oldRecord);
        return 1;
    }
}
