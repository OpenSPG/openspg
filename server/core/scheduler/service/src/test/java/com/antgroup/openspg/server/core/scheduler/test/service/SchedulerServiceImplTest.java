package com.antgroup.openspg.server.core.scheduler.test.service;

import java.util.List;

import com.antgroup.openspg.server.api.http.client.util.ConnectionInfo;
import com.antgroup.openspg.server.api.http.client.util.HttpClientBootstrap;
import com.antgroup.openspg.server.common.model.scheduler.LifeCycle;
import com.antgroup.openspg.server.common.model.scheduler.MergeMode;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerJobQuery;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.api.SchedulerService;
import com.antgroup.openspg.server.core.scheduler.test.sofaboot.SofaBootTestApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
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

    @Autowired
    SchedulerService schedulerService;
    private Long jobId;

    @Before
    public void setUp() {
        HttpClientBootstrap.init(new ConnectionInfo("http://127.0.0.1:8887").setConnectTimeout(60000).setReadTimeout(60000));
        this.submitJob();
    }

    @After
    public void tearDown() {
        this.deleteJob();
    }

    @Test
    void submitJob() {
        SchedulerJob job = new SchedulerJob();
        job.setProjectId(0L);
        job.setName("Test Job");
        job.setCreateUser("test");
        job.setLifeCycle(LifeCycle.ONCE.name());
        job.setType("localDryRun");
        job.setMergeMode(MergeMode.MERGE.name());
        job = schedulerService.submitJob(job);
        jobId = job.getId();
    }

    void deleteJob() {
        assertTrue(schedulerService.deleteJob(jobId));
    }

    @Test
    void executeJob() {
        assertTrue(schedulerService.executeJob(jobId));
    }

    @Test
    void onlineJob() {
        assertTrue(schedulerService.onlineJob(jobId));
    }

    @Test
    void offlineJob() {
        assertTrue(schedulerService.offlineJob(jobId));
    }

    @Test
    void updateJob() {
        SchedulerJob job = schedulerService.getJobById(jobId);
        job.setName("Update Test Job");
        assertTrue(schedulerService.updateJob(job));
    }

    @Test
    void getJobById() {
        SchedulerJob job = schedulerService.getJobById(jobId);
        assertEquals(jobId, job.getId());
    }

    @Test
    void searchJobs() {
        SchedulerJobQuery query = new SchedulerJobQuery();
        query.setId(jobId);
        List<SchedulerJob> jobs = schedulerService.searchJobs(query).getData();
        assertEquals(1, jobs.size());
    }

    @Test
    void getInstanceById() {
        SchedulerInstanceQuery query = new SchedulerInstanceQuery();
        query.setJobId(jobId);
        List<SchedulerInstance> instances = schedulerService.searchInstances(query).getData();
        assertTrue(instances.size() > 0);
        SchedulerInstance instance = instances.get(0);
        SchedulerInstance ins = schedulerService.getInstanceById(instance.getId());
        assertEquals(ins.getId(), instance.getId());
    }

    @Test
    void setFinishInstance() {
        SchedulerInstanceQuery query = new SchedulerInstanceQuery();
        query.setJobId(jobId);
        List<SchedulerInstance> instances = schedulerService.searchInstances(query).getData();
        assertTrue(instances.size() > 0);
        SchedulerInstance instance = instances.get(0);
        assertTrue(schedulerService.reRunInstance(instance.getId()));
        assertTrue(schedulerService.setFinishInstance(instance.getId()));
    }

    @Test
    void reRunInstance() {
        SchedulerInstanceQuery query = new SchedulerInstanceQuery();
        query.setJobId(jobId);
        List<SchedulerInstance> instances = schedulerService.searchInstances(query).getData();
        assertTrue(instances.size() > 0);
        SchedulerInstance instance = instances.get(0);
        assertTrue(schedulerService.reRunInstance(instance.getId()));
        assertTrue(schedulerService.stopInstance(instance.getId()));
    }

    @Test
    void triggerInstance() {
        SchedulerInstanceQuery query = new SchedulerInstanceQuery();
        query.setJobId(jobId);
        List<SchedulerInstance> instances = schedulerService.searchInstances(query).getData();
        assertTrue(instances.size() > 0);
        SchedulerInstance instance = instances.get(0);
        assertTrue(schedulerService.triggerInstance(instance.getId()));
    }

    @Test
    void searchInstances() {
        SchedulerInstanceQuery query = new SchedulerInstanceQuery();
        query.setJobId(jobId);
        List<SchedulerInstance> instances = schedulerService.searchInstances(query).getData();
        assertTrue(instances.size() > 0);
    }

    @Test
    void searchTasks() {
        SchedulerTaskQuery query = new SchedulerTaskQuery();
        query.setJobId(jobId);
        List<SchedulerTask> tasks = schedulerService.searchTasks(query).getData();
        assertTrue(tasks.size() > 0);
    }
}