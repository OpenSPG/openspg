package com.antgroup.openspg.test.scheduler;

import java.util.List;

import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.common.util.thread.ThreadUtils;
import com.antgroup.openspg.server.api.http.client.util.ConnectionInfo;
import com.antgroup.openspg.server.api.http.client.util.HttpClientBootstrap;
import com.antgroup.openspg.server.common.model.scheduler.InstanceStatus;
import com.antgroup.openspg.server.common.model.scheduler.LifeCycle;
import com.antgroup.openspg.server.common.model.scheduler.MergeMode;
import com.antgroup.openspg.server.common.model.scheduler.Status;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerJobQuery;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.api.SchedulerService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.translate.TranslateEnum;
import com.antgroup.openspg.test.sofaboot.SofaBootTestApplication;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author yangjin
 * @Title:
 * @Package com.antgroup.openspg.server.core.scheduler.service.api.impl
 * @Description:
 * @date 2023/12/617:29
 */
@SpringBootTest(classes = SofaBootTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SchedulerServiceImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerServiceImplTest.class);

    @Autowired
    SchedulerService         schedulerService;
    @Autowired
    SchedulerInstanceService schedulerInstanceService;

    @Before
    public void setUp() {
        HttpClientBootstrap.init(new ConnectionInfo("http://127.0.0.1:8887").setConnectTimeout(60000).setReadTimeout(60000));
    }

    /**
     * step 1: create new Job to submit and execute Job
     * step 2: query all Jobs and to offline,online,update Job
     * step 3: get Job to execute
     * step 4: get Instance to set Finish
     * step 5: reRun Instance and to stop
     * step 6: reRun Instance and to trigger
     * step 7: trigger Instance until it ends
     * step 8: get tasks
     * step 9: delete Job;
     */
    @Test
    void submitOnceJob() {
        // step 1: create Job to submit
        SchedulerJob job = new SchedulerJob();
        job.setProjectId(0L);
        job.setName("Test Job");
        job.setCreateUser("test");
        job.setLifeCycle(LifeCycle.ONCE.name());
        job.setTranslate(TranslateEnum.LOCAL_DRY_RUN.name());
        job.setMergeMode(MergeMode.MERGE.name());
        job = schedulerService.submitJob(job);
        Long jobId = job.getId();
        assertTrue(jobId > 0);

        // step 2: query Jobs
        SchedulerJobQuery jobQuery = new SchedulerJobQuery();
        jobQuery.setId(jobId);
        List<SchedulerJob> jobs = schedulerService.searchJobs(jobQuery).getData();
        assertEquals(1, jobs.size());
        // offline job
        assertTrue(schedulerService.offlineJob(jobId));
        job = schedulerService.getJobById(jobId);
        assertEquals(Status.OFFLINE.name(), job.getStatus());
        SchedulerInstanceQuery instanceQuery = new SchedulerInstanceQuery();
        instanceQuery.setJobId(jobId);
        List<SchedulerInstance> notFinishInstances = schedulerInstanceService.getNotFinishInstance(instanceQuery);
        assertTrue(CollectionUtils.isEmpty(notFinishInstances));
        // online Job
        assertTrue(schedulerService.onlineJob(jobId));
        job = schedulerService.getJobById(jobId);
        assertEquals(Status.ONLINE.name(), job.getStatus());
        // update Job
        String updateName = "Update Test Job";
        job.setName(updateName);
        assertTrue(schedulerService.updateJob(job));
        job = schedulerService.getJobById(jobId);
        assertEquals(updateName, job.getName());

        // step 3: execute Job
        assertTrue(schedulerService.executeJob(jobId));
        notFinishInstances = schedulerInstanceService.getNotFinishInstance(instanceQuery);
        assertEquals(1, notFinishInstances.size());

        // step 4: get Instance to set Finish
        List<SchedulerInstance> instances = schedulerService.searchInstances(instanceQuery).getData();
        assertTrue(instances.size() > 0);
        SchedulerInstance instance = notFinishInstances.get(0);
        SchedulerInstance ins = schedulerService.getInstanceById(instance.getId());
        assertEquals(ins.getId(), instance.getId());
        assertTrue(schedulerService.setFinishInstance(instance.getId()));
        notFinishInstances = schedulerInstanceService.getNotFinishInstance(instanceQuery);
        assertTrue(CollectionUtils.isEmpty(notFinishInstances));

        // step 5: reRun Instance and to stop
        assertTrue(schedulerService.reRunInstance(instance.getId()));
        notFinishInstances = schedulerInstanceService.getNotFinishInstance(instanceQuery);
        assertEquals(1, notFinishInstances.size());
        instance = notFinishInstances.get(0);
        assertTrue(schedulerService.stopInstance(instance.getId()));
        notFinishInstances = schedulerInstanceService.getNotFinishInstance(instanceQuery);
        assertTrue(CollectionUtils.isEmpty(notFinishInstances));

        // step 6: reRun Instance
        assertTrue(schedulerService.reRunInstance(instance.getId()));
        notFinishInstances = schedulerInstanceService.getNotFinishInstance(instanceQuery);
        assertEquals(1, notFinishInstances.size());
        instance = notFinishInstances.get(0);

        ThreadUtils.sleep(5000);
        // step 7: trigger Instance until it ends
        while (!InstanceStatus.isFinish(getInstance(instance.getId()))) {
            assertTrue(schedulerService.triggerInstance(instance.getId()));
            ThreadUtils.sleep(5000);
        }
        instance = schedulerService.getInstanceById(instance.getId());
        assertEquals(InstanceStatus.FINISH.name(), instance.getStatus());

        // step 8: get tasks
        SchedulerTaskQuery taskQuery = new SchedulerTaskQuery();
        taskQuery.setInstanceId(instance.getId());
        List<SchedulerTask> tasks = schedulerService.searchTasks(taskQuery).getData();
        assertTrue(tasks.size() > 0);
        for (SchedulerTask task : tasks) {
            LOGGER.info(String.format("|task|%s|%s|%s|%s|%s|%s|%s|%s|%s",
                    task.getTitle(), task.getType(), task.getStatus(), task.getJobId(), task.getInstanceId(), task.getExecuteNum(),
                    DateTimeUtils.getDate2LongStr(task.getBeginTime()), DateTimeUtils.getDate2LongStr(task.getFinishTime()),
                    task.getRemark()
            ));
        }

        // step 9: delete Job
        assertTrue(schedulerService.deleteJob(jobId));
        jobs = schedulerService.searchJobs(jobQuery).getData();
        assertEquals(0, jobs.size());
        instances = schedulerService.searchInstances(instanceQuery).getData();
        assertEquals(0, instances.size());
        tasks = schedulerService.searchTasks(taskQuery).getData();
        assertEquals(0, tasks.size());
    }

    private String getInstance(Long id) {
        SchedulerInstance ins = schedulerService.getInstanceById(id);
        return ins.getStatus();
    }

}