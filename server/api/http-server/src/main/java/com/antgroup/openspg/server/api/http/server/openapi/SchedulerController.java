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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInfoQuery;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerJobQuery;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInfo;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.api.SchedulerService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInfoService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/public/v1/scheduler")
@Slf4j
public class SchedulerController extends BaseController {

  @Autowired private SchedulerService schedulerService;
  @Autowired private SchedulerInfoService schedulerInfoService;

  @RequestMapping(value = "/job/submit", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<SchedulerJob> submitJob(@RequestBody SchedulerJob request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<SchedulerJob>() {
          @Override
          public void check() {
            log.info("/scheduler/insert request: {}", JSON.toJSONString(request));
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
            AssertUtils.assertParamObjectIsNotNull("name", request.getName());
          }

          @Override
          public SchedulerJob action() {
            return schedulerService.submitJob(request);
          }
        });
  }

  @RequestMapping(value = "/job/execute", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<Boolean> executeJob(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            log.info("/scheduler/executeJob id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public Boolean action() {
            return schedulerService.executeJob(id);
          }
        });
  }

  @RequestMapping(value = "/job/enable", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<Boolean> enableJob(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            log.info("/scheduler/enableJob id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public Boolean action() {
            return schedulerService.enableJob(id);
          }
        });
  }

  @RequestMapping(value = "/job/disable", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<Boolean> disableJob(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            log.info("/scheduler/disableJob id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public Boolean action() {
            return schedulerService.disableJob(id);
          }
        });
  }

  @RequestMapping(value = "/job/delete", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<Boolean> deleteJob(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            log.info("/scheduler/deleteJob id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public Boolean action() {
            return schedulerService.deleteJob(id);
          }
        });
  }

  @RequestMapping(value = "/job/update", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<Boolean> updateJob(@RequestBody SchedulerJob request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            log.info("/scheduler/updateJob request: {}", JSON.toJSONString(request));
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("id", request.getId());
            AssertUtils.assertParamObjectIsNotNull("name", request.getName());
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
          }

          @Override
          public Boolean action() {
            return schedulerService.updateJob(request);
          }
        });
  }

  @RequestMapping(value = "/job/getById", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<SchedulerJob> getJobById(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<SchedulerJob>() {
          @Override
          public void check() {
            log.info("/scheduler/getJobById id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public SchedulerJob action() {
            return schedulerService.getJobById(id);
          }
        });
  }

  @RequestMapping(value = "/job/search", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<Paged<SchedulerJob>> searchJobs(@RequestBody SchedulerJobQuery request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Paged<SchedulerJob>>() {
          @Override
          public void check() {
            log.info("/scheduler/searchJobs request: {}", JSON.toJSONString(request));
          }

          @Override
          public Paged<SchedulerJob> action() {
            return schedulerService.searchJobs(request);
          }
        });
  }

  @RequestMapping(value = "/instance/getById", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<SchedulerInstance> getInstanceById(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<SchedulerInstance>() {
          @Override
          public void check() {
            log.info("/scheduler/getInstanceById id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public SchedulerInstance action() {
            return schedulerService.getInstanceById(id);
          }
        });
  }

  @RequestMapping(value = "/instance/stop", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<Boolean> stopInstance(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            log.info("/scheduler/stopInstance id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public Boolean action() {
            return schedulerService.stopInstance(id);
          }
        });
  }

  @RequestMapping(value = "/instance/setFinish", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<Boolean> setFinishInstance(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            log.info("/scheduler/setFinishInstance id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public Boolean action() {
            return schedulerService.setFinishInstance(id);
          }
        });
  }

  @RequestMapping(value = "/instance/restart", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<Boolean> restartInstance(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            log.info("/scheduler/restartInstance id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public Boolean action() {
            return schedulerService.restartInstance(id);
          }
        });
  }

  @RequestMapping(value = "/instance/trigger", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<Boolean> triggerInstance(Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            log.info("/scheduler/triggerInstance id: {}", id);
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          @Override
          public Boolean action() {
            return schedulerService.triggerInstance(id);
          }
        });
  }

  @RequestMapping(value = "/instance/search", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<Paged<SchedulerInstance>> searchInstances(
      @RequestBody SchedulerInstanceQuery request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Paged<SchedulerInstance>>() {
          @Override
          public void check() {
            log.info("/scheduler/searchInstances request: {}", JSON.toJSONString(request));
          }

          @Override
          public Paged<SchedulerInstance> action() {
            return schedulerService.searchInstances(request);
          }
        });
  }

  @RequestMapping(value = "/task/search", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<Paged<SchedulerTask>> searchTasks(@RequestBody SchedulerTaskQuery request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Paged<SchedulerTask>>() {
          @Override
          public void check() {
            log.info("/scheduler/searchTasks request: {}", JSON.toJSONString(request));
          }

          @Override
          public Paged<SchedulerTask> action() {
            return schedulerService.searchTasks(request);
          }
        });
  }

  @RequestMapping(value = "/setIp", method = RequestMethod.GET)
  @ResponseBody
  public HttpResult<Boolean> deleteJob(String ip) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            log.info("/scheduler/setIp ip: {}", ip);
          }

          @Override
          public Boolean action() {
            List<SchedulerInfo> infos =
                schedulerInfoService.query(new SchedulerInfoQuery()).getResults();
            infos.forEach(
                info -> {
                  JSONObject config = info.getConfig();
                  if (StringUtils.isBlank(ip)) {
                    config.remove(SchedulerInfo.WHITE_IP_KEY);
                  } else {
                    JSONArray array = new JSONArray();
                    array.add(ip);
                    config.put(SchedulerInfo.WHITE_IP_KEY, array);
                  }
                  SchedulerInfo updateInfo = new SchedulerInfo();
                  updateInfo.setId(info.getId());
                  updateInfo.setConfig(config);
                  schedulerInfoService.update(updateInfo);
                });
            return true;
          }
        });
  }
}
