/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.task.sync.impl;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.server.common.model.scheduler.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.LifeCycle;
import com.antgroup.openspg.server.common.model.scheduler.MergeMode;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.common.SchedulerValue;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.task.JobTaskContext;
import com.antgroup.openspg.server.core.scheduler.service.task.sync.JobSyncTaskTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * pre check Task
 *
 * @author yangjin
 * @version : PreCheckSyncTask.java, v 0.1 2023年12月05日 11:11 yangjin Exp $
 */
@Component("preCheckTask")
public class PreCheckSyncTask extends JobSyncTaskTemplate {

    /**
     * scheduler max days
     */
    private static final long SCHEDULER_MAX_DAYS = 5;

    @Autowired
    SchedulerValue           schedulerValue;
    @Autowired
    SchedulerInstanceService schedulerInstanceService;

    @Override
    public TaskStatus submit(JobTaskContext context) {
        SchedulerInstance instance = context.getInstance();

        long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - instance.getGmtCreate().getTime());
        Integer lastDays = schedulerValue.getExecuteMaxDay();
        if (days > SCHEDULER_MAX_DAYS) {
            context.addTraceLog("前置校验已超过%s天未通过。超过%s天后任务将不会调度", days, lastDays);
        }
        Date schedulerDate = instance.getSchedulerDate();
        Date now = new Date();
        if (now.before(schedulerDate)) {
            context.addTraceLog("实例未到执行时间！开始调度日期：%s", DateTimeUtils.getDate2LongStr(schedulerDate));
            return TaskStatus.RUNNING;
        }

        if (LifeCycle.REAL_TIME.name().equals(instance.getLifeCycle())) {
            return processBySkip(context);
        }

        if (MergeMode.SNAPSHOT.name().equals(instance.getMergeMode())) {
            return processBySnapshot(context);
        } else {
            return processByMerge(context);
        }
    }

    private TaskStatus processBySkip(JobTaskContext context) {
        SchedulerInstance instance = context.getInstance();
        context.addTraceLog("当前任务无需前置数据检查。直接进行下一节点");
        SchedulerInstance updateInstance = new SchedulerInstance();
        updateInstance.setId(instance.getId());
        updateInstance.setStatus(InstanceStatus.RUNNING.name());
        schedulerInstanceService.update(updateInstance);
        return TaskStatus.FINISH;
    }

    private TaskStatus processBySnapshot(JobTaskContext context) {
        context.addTraceLog("当前任务不依赖上次实例完成，直接检查前置实例依赖");
        return checkPreInstance(context);
    }

    public TaskStatus processByMerge(JobTaskContext context) {
        context.addTraceLog("当前任务依赖上次实例完成，需检查上次实例是否执行完成");
        SchedulerInstance instance = context.getInstance();
        SchedulerJob job = context.getJob();
        String preSchedulerDate = DateTimeUtils.getDate2LongStr(
                CommonUtils.getPreviousValidTime(job.getSchedulerCron(), instance.getSchedulerDate()));
        String preUniqueId = instance.getJobId() + preSchedulerDate;
        SchedulerInstance preInstance = schedulerInstanceService.getByUniqueId(preUniqueId);

        if (null == preInstance) {
            return checkPreInstance(context);
        }
        if (InstanceStatus.isFinish(preInstance.getStatus())) {
            return checkPreInstance(context);
        }

        context.addTraceLog("当前任务依赖上次实例未执行完成，请等待实例(%s)调度完成", preInstance.getUniqueId());
        return TaskStatus.RUNNING;
    }

    private TaskStatus checkPreInstance(JobTaskContext context) {
        return TaskStatus.FINISH;
    }

}
