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

package com.antgroup.openspg.server.api.http.server.openapi;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.common.constants.BuilderConstant;
import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.facade.dto.common.request.KagBuilderRequest;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.bulider.BuilderJobQuery;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.api.SchedulerService;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/public/v1/builder")
@Slf4j
public class BuilderController extends BaseController {

  @Autowired private BuilderJobService builderJobService;

  @Autowired private ProjectManager projectManager;

  @Autowired private SchedulerService schedulerService;

  @RequestMapping(value = "/kag/submit", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<BuilderJob> submit(@RequestBody KagBuilderRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<BuilderJob>() {
          @Override
          public void check() {
            log.info("/builder/kag/submit request: {}", JSON.toJSONString(request));
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
            Project project = projectManager.queryById(request.getProjectId());
            AssertUtils.assertParamObjectIsNotNull("project", project);
            AssertUtils.assertParamObjectIsNotNull("workerNum", request.getWorkerNum());
            AssertUtils.assertParamObjectIsNotNull("command", request.getCommand());
          }

          @Override
          public BuilderJob action() {
            BuilderJob job = new BuilderJob();
            job.setProjectId(request.getProjectId());
            job.setGmtCreate(new Date());
            job.setGmtModified(new Date());
            String userId = request.getUserNumber() == null ? "164072" : request.getUserNumber();
            job.setCreateUser(userId);
            job.setModifyUser(userId);
            job.setJobName(
                "KAG_COMMAND_"
                    + userId
                    + "_"
                    + DateTimeUtils.getDate2Str(DateTimeUtils.YYYY_MM_DD_HH_MM_SS2, new Date()));
            job.setFileUrl("");
            job.setStatus("RUNNING");
            job.setDataSourceType("KAG");
            job.setType(BuilderConstant.KAG_COMMAND);
            job.setVersion(BuilderConstant.DEFAULT_VERSION);
            job.setComputingConf(JSON.toJSONString(request));
            job.setLifeCycle(SchedulerEnum.LifeCycle.ONCE.name());
            Long id = builderJobService.insert(job);
            job.setId(id);
            SchedulerJob schedulerJob = createSchedulerJob(job);
            BuilderJob taskJob = new BuilderJob();
            taskJob.setId(job.getId());
            taskJob.setTaskId(schedulerJob.getId());
            builderJobService.update(taskJob);
            return job;
          }
        });
  }

  private SchedulerJob createSchedulerJob(BuilderJob taskJob) {
    SchedulerJob job = new SchedulerJob();
    job.setProjectId(taskJob.getProjectId());
    job.setName(taskJob.getJobName());
    job.setCreateUser(taskJob.getCreateUser());
    job.setModifyUser(taskJob.getModifyUser());
    job.setLifeCycle(SchedulerEnum.LifeCycle.valueOf(taskJob.getLifeCycle()));
    job.setStatus(SchedulerEnum.Status.ENABLE);
    job.setTranslateType(SchedulerEnum.TranslateType.KAG_COMMAND_BUILDER);
    job.setDependence(SchedulerEnum.Dependence.INDEPENDENT);
    job.setInvokerId(taskJob.getId().toString());
    job = schedulerService.submitJob(job);
    return job;
  }

  @RequestMapping(value = "/delete", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<Boolean> delete(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            log.info("/builder/delete id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public Boolean action() {
            return builderJobService.deleteById(id) > 0;
          }
        });
  }

  @RequestMapping(value = "/getById", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<BuilderJob> getById(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<BuilderJob>() {
          @Override
          public void check() {
            log.info("/builder/getById id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public BuilderJob action() {
            return builderJobService.getById(id);
          }
        });
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<Paged<BuilderJob>> search(@RequestBody BuilderJobQuery request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Paged<BuilderJob>>() {
          @Override
          public void check() {
            log.info("/builder/search request: {}", JSON.toJSONString(request));
          }

          @Override
          public Paged<BuilderJob> action() {
            return builderJobService.query(request);
          }
        });
  }
}
