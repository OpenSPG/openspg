/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.common.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.server.common.model.scheduler.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.common.service.spring.SpringContextHolder;
import com.antgroup.openspg.server.core.scheduler.model.common.WorkflowDag;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerCommonService;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerConstant;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerValue;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerJobService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTask;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTaskContext;
import com.antgroup.openspg.server.core.scheduler.service.task.async.JobAsyncTask;
import com.antgroup.openspg.server.core.scheduler.service.translate.TranslatorFactory;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yangjin
 * @version : SchedulerCommonServiceImpl.java, v 0.1 2023年12月04日 16:44 yangjin Exp $
 */
@Service
public class SchedulerCommonServiceImpl implements SchedulerCommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerCommonServiceImpl.class);

    @Autowired
    SchedulerJobService      schedulerJobService;
    @Autowired
    SchedulerInstanceService schedulerInstanceService;
    @Autowired
    SchedulerTaskService     schedulerTaskService;
    @Autowired
    SchedulerValue           schedulerValue;

    @Override
    public void setInstanceFinish(SchedulerInstance instance, InstanceStatus instanceStatus, TaskStatus taskStatus) {
        SchedulerInstance updateInstance = new SchedulerInstance();
        updateInstance.setId(instance.getId());
        updateInstance.setStatus(instanceStatus.name());
        updateInstance.setProgress(100L);
        Date finishTime = instance.getFinishTime() == null ? new Date() : instance.getFinishTime();
        updateInstance.setFinishTime(finishTime);
        Long updateNum = schedulerInstanceService.update(updateInstance);
        if (updateNum <= 0) {
            throw new RuntimeException(String.format("update instance failed %s", updateInstance));
        }
        stopRunningProcess(instance);

        schedulerTaskService.setStatusByInstanceId(instance.getId(), taskStatus);
    }

    /**
     * stop Running Process
     *
     * @param instance
     */
    private void stopRunningProcess(SchedulerInstance instance) {
        List<SchedulerTask> taskList = schedulerTaskService.queryByInstanceId(instance.getId());
        List<SchedulerTask> processList = taskList.stream()
                .filter(s -> TaskStatus.isRunning(s.getStatus()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(processList)) {
            return;
        }
        SchedulerJob job = schedulerJobService.getById(instance.getJobId());
        processList.forEach(task -> {
            try {
                JobTaskContext context = new JobTaskContext(job, instance, task);
                String type = task.getType();
                if (StringUtils.isBlank(type)) {
                    LOGGER.warn(String.format("stop task type is null id:%s", task.getId()));
                    return;
                }
                type = type.split(SchedulerConstant.UNDERLINE_SEPARATOR)[0];
                JobTask jobTask = SpringContextHolder.getBean(type, JobTask.class);
                if (jobTask == null) {
                    LOGGER.error(String.format("stop task is null id:%s", task.getId()));
                    return;
                }
                if (jobTask instanceof JobAsyncTask) {
                    JobAsyncTask jobAsyncTask = (JobAsyncTask) jobTask;
                    jobAsyncTask.stop(context, task.getResource());
                }
            } catch (Exception e) {
                LOGGER.error(String.format("stop task error id:%s", task.getId()), e);
            }
        });
    }

    private void checkInstanceRunning(SchedulerJob job) {
        SchedulerInstanceQuery query = new SchedulerInstanceQuery();
        query.setJobId(job.getId());
        query.setStartCreateTime(DateUtils.addDays(new Date(), -1));
        query.setEndCreateTime(new Date());
        List<SchedulerInstance> instances = schedulerInstanceService.query(query).getData();
        instances.stream().forEach(instance -> {
            if (!InstanceStatus.isFinish(instance.getStatus())) {
                throw new RuntimeException(String.format("Running instances exist within 24H uniqueId:%", instance.getUniqueId()));
            }
        });
    }

    @Override
    public SchedulerInstance generateOnceInstance(SchedulerJob job) {
        checkInstanceRunning(job);
        Date schedulerDate = new Date();
        String uniqueId = job.getId().toString() + System.currentTimeMillis();
        return generateInstance(job, uniqueId, schedulerDate);
    }

    @Override
    public List<SchedulerInstance> generatePeriodInstance(SchedulerJob job) {
        List<SchedulerInstance> instances = Lists.newArrayList();
        List<Date> executionDates = CommonUtils.getCronExecutionDatesByToday(job.getSchedulerCron());
        for (Date schedulerDate : executionDates) {
            String uniqueId = job.getId().toString() + DateTimeUtils.getDate2Str(DateTimeUtils.YYYY_MM_DD_HH_MM_SS2, schedulerDate);
            SchedulerInstance instance = generateInstance(job, uniqueId, schedulerDate);
            if (instance == null) {
                continue;
            }
            instances.add(instance);
        }
        return instances;
    }

    @Override
    public SchedulerInstance generateRealTimeInstance(SchedulerJob job) {
        checkInstanceRunning(job);
        Date schedulerDate = new Date();
        String uniqueId = job.getId().toString() + System.currentTimeMillis();
        return generateInstance(job, uniqueId, schedulerDate);
    }

    @Override
    public SchedulerInstance generateInstance(SchedulerJob job, String uniqueId, Date schedulerDate) {
        SchedulerInstance existInstance = schedulerInstanceService.getByUniqueId(uniqueId);
        if (existInstance != null) {
            LOGGER.info(String.format("generateInstance uniqueId exist jobId:%s uniqueId:%s", job.getId(), uniqueId));
            return null;
        }

        LOGGER.info(String.format("generateInstance start jobId:%s uniqueId:%s", job.getId(), uniqueId));
        SchedulerInstance instance = new SchedulerInstance();
        instance.setUniqueId(uniqueId);
        instance.setProjectId(job.getProjectId());
        instance.setJobId(job.getId());
        instance.setType(job.getTranslate());
        instance.setStatus(InstanceStatus.WAITING.name());
        instance.setProgress(0L);
        instance.setCreateUser(job.getCreateUser());
        instance.setModifyUser(job.getCreateUser());
        instance.setGmtCreate(new Date());
        instance.setGmtModified(new Date());
        instance.setBeginRunningTime(new Date());
        instance.setLifeCycle(job.getLifeCycle());
        instance.setSchedulerDate(schedulerDate);
        instance.setMergeMode(job.getMergeMode());
        instance.setEnv(schedulerValue.getExecuteEnv());
        instance.setVersion(SchedulerConstant.INSTANCE_DEFAULT_VERSION);
        instance.setConfig(job.getConfig());
        WorkflowDag workflowDag = TranslatorFactory.getTranslator(job.getTranslate()).translate(job);
        instance.setWorkflowConfig(JSON.toJSONString(workflowDag));

        schedulerInstanceService.insert(instance);
        LOGGER.info(String.format("generateInstance successful jobId:%s instances:%s", job.getId(), uniqueId));

        List<WorkflowDag.Node> nodes = workflowDag.getNodes();
        nodes.forEach(node -> {
            TaskStatus status = CollectionUtils.isEmpty(workflowDag.getPreNodes(node.getId())) ? TaskStatus.RUNNING : TaskStatus.WAIT;
            schedulerTaskService.insert(new SchedulerTask(instance.getCreateUser(), job.getId(), instance.getId(), status, node));
        });

        SchedulerJob updateJob = new SchedulerJob();
        updateJob.setId(job.getId());
        updateJob.setLastExecuteTime(schedulerDate);
        schedulerJobService.update(updateJob);

        return instance;
    }
}
