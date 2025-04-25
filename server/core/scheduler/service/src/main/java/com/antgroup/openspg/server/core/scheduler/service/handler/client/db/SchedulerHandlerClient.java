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
package com.antgroup.openspg.server.core.scheduler.service.handler.client.db;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.common.util.NetworkAddressUtils;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.SchedulerInfoStatus;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum.TaskStatus;
import com.antgroup.openspg.server.common.service.spring.SpringContextHolder;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInfoQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerHandlerResult;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInfo;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInfoLog;
import com.antgroup.openspg.server.core.scheduler.service.config.SchedulerConfig;
import com.antgroup.openspg.server.core.scheduler.service.handler.SchedulerHandler;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInfoService;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/** Scheduler Handler DB implementation class. To generate and execute Instances */
@Service
@Slf4j
@ConditionalOnProperty(name = "scheduler.handler.type", havingValue = "db")
public class SchedulerHandlerClient {

  private static final int corePoolSize = 1;
  private static final long initialDelay = 0;
  public static final Integer LOCK_TIME_MINUTES = 10;

  private static final int LOGGER_MAX_COUNT = 3;

  @Autowired SchedulerConfig schedulerConfig;
  @Autowired SchedulerInfoService schedulerInfoService;

  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    insertSchedulerToDB();
    log.info("Init DB Scheduler Handler");
    List<SchedulerInfo> schedulerInfos =
        schedulerInfoService.query(new SchedulerInfoQuery()).getResults();
    log.info("Number of DB schedulerHandler:{}", schedulerInfos.size());
    if (CollectionUtils.isEmpty(schedulerInfos)) {
      log.info("The schedulerHandler has not in DB, no registration is required!");
      return;
    }
    for (SchedulerInfo info : schedulerInfos) {
      try {
        SchedulerHandler handler =
            SpringContextHolder.getBean(info.getName(), SchedulerHandler.class);
        if (handler == null) {
          log.error("schedulerHandler bean not found name:{}", info.getName());
          continue;
        }
        ScheduledThreadPoolExecutor executor =
            new ScheduledThreadPoolExecutor(
                corePoolSize,
                (runnable) -> {
                  Thread thread = new Thread(runnable);
                  thread.setDaemon(true);
                  thread.setName("dbSchedule-" + thread.getId());
                  return thread;
                });
        executor.scheduleAtFixedRate(
            new SchedulerHandlerRunnable(handler, info.getId(), info.getName()),
            initialDelay,
            info.getPeriod(),
            TimeUnit.SECONDS);
      } catch (Exception e) {
        log.error("Executor DB SchedulerHandler Exception:" + info.getName(), e);
      }
    }
  }

  public void insertSchedulerToDB() {
    log.info("get Scheduler Handler Beans");
    Map<String, SchedulerHandler> schedulerHandlers =
        SpringContextHolder.getBeanMap(SchedulerHandler.class);
    log.info("Number of registered schedulerHandler:{}", schedulerHandlers.size());
    if (MapUtils.isEmpty(schedulerHandlers)) {
      return;
    }
    for (String name : schedulerHandlers.keySet()) {
      try {
        SchedulerHandler schedulerHandler = schedulerHandlers.get(name);
        Long period =
            (schedulerHandler.getPeriod() == null || schedulerHandler.getPeriod() <= 0)
                ? schedulerConfig.getHandlerProcessPeriod()
                : schedulerHandler.getPeriod();
        SchedulerInfo schedulerInfo = schedulerInfoService.getByName(name);
        if (schedulerInfo != null) {
          continue;
        }
        schedulerInfo = new SchedulerInfo();
        schedulerInfo.setGmtCreate(new Date());
        schedulerInfo.setGmtModified(new Date());
        schedulerInfo.setName(name);
        schedulerInfo.setStatus(SchedulerEnum.SchedulerInfoStatus.RUNNING);
        schedulerInfo.setPeriod(period);
        schedulerInfo.setCount(0);
        JSONObject config = new JSONObject();
        config.put(SchedulerInfo.HOST_EXCEPTION_TIMEOUT_KEY, 300);
        schedulerInfo.setConfig(config);
        schedulerInfoService.insert(schedulerInfo);
      } catch (Exception e) {
        log.error("insert Scheduler Handler Exception:" + name, e);
      }
    }
  }

  class SchedulerHandlerRunnable implements Runnable {
    SchedulerHandler handler;
    String handlerName;
    Long handlerId;

    public SchedulerHandlerRunnable(SchedulerHandler handler, Long handlerId, String handlerName) {
      this.handler = handler;
      this.handlerId = handlerId;
      this.handlerName = handlerName;
    }

    @Override
    public void run() {
      try {
        log.info("Start DB SchedulerHandler:{}", handlerName);
        triggerJob();
      } catch (Exception e) {
        log.error("DB SchedulerHandler process Exception", e);
      }
    }

    private Boolean triggerJob() {
      Date rt = new Date();
      Boolean lockStatus = true;
      try {
        SchedulerInfo schedulerInfo = schedulerInfoService.getById(handlerId);
        String hostAddress = NetworkAddressUtils.LOCAL_IP;
        List<String> whiteIps = schedulerInfo.getWhiteIps();
        if (CollectionUtils.isNotEmpty(whiteIps) && !whiteIps.contains(hostAddress)) {
          log.info("The native ip is not in the whitelist, stop triggering");
          return false;
        }

        lockStatus = lockTask(handlerId);
        if (!lockStatus) {
          log.info("get scheduler lock failed {} {}", handlerName, hostAddress);
          return false;
        }
        log.info("get scheduler lock success {} {}", handlerName, hostAddress);
        boolean result = canExecuteJob(handlerName, schedulerInfo);
        log.info("can execute result {} {}", result, handlerName);
        if (!result) {
          return false;
        }
        SchedulerHandlerResult process = handler.process(new JSONObject());
        SchedulerInfoLog infoLog =
            new SchedulerInfoLog(process.getStatus().name(), process.getMsg(), rt, new Date());
        updateSchedulerInfoLog(handlerId, process.getStatus(), infoLog);
        return true;
      } catch (Exception e) {
        log.error(handlerName + " process failed", e);
        SchedulerInfoLog infoLog =
            new SchedulerInfoLog(
                TaskStatus.ERROR.name(), ExceptionUtils.getStackTrace(e), rt, new Date());
        boolean success = updateSchedulerInfoLog(handlerId, TaskStatus.ERROR, infoLog);
        log.info("update error info {} {}", success, handlerName);
        return false;
      } finally {
        unlockTask(handlerId, lockStatus);
      }
    }

    private boolean canExecuteJob(String name, SchedulerInfo schedulerInfo) {
      List<SchedulerInfoLog> infoLogs = schedulerInfo.getLog();
      if (CollectionUtils.isEmpty(infoLogs)) {
        return true;
      }
      SchedulerInfoLog schedulerLog = infoLogs.get(infoLogs.size() - 1);
      Date nowDate = new Date();
      if (SchedulerInfoStatus.RUNNING.equals(schedulerInfo.getStatus())) {
        Long hostExceptionTimeout = schedulerInfo.getHostExceptionTimeout();
        if (hostExceptionTimeout != null
            && nowDate.getTime() - schedulerLog.getRt().getTime() >= hostExceptionTimeout * 1000) {
          log.info("running and timeout to pull again {} {}", name, hostExceptionTimeout);
          return true;
        }
      }
      if (SchedulerInfoStatus.WAIT.equals(schedulerInfo.getStatus())
          && nowDate.getTime() - schedulerLog.getRt().getTime()
              >= schedulerInfo.getPeriod() * 1000) {
        SchedulerInfo infoDTO = new SchedulerInfo();
        infoDTO.setId(schedulerInfo.getId());
        infoDTO.setStatus(SchedulerInfoStatus.RUNNING);
        schedulerInfoService.update(infoDTO);
        return true;
      }
      return false;
    }

    public boolean updateSchedulerInfoLog(Long id, TaskStatus status, SchedulerInfoLog infoLog) {
      SchedulerInfo schedulerInfo = schedulerInfoService.getById(id);
      if (null == schedulerInfo) {
        log.warn("no scheduler info {} ", id);
        return false;
      }
      if (TaskStatus.FINISH.equals(status)) {
        schedulerInfo.setCount(0);
      } else {
        schedulerInfo.setCount(schedulerInfo.getCount() + 1);
      }
      schedulerInfo.setStatus(SchedulerInfoStatus.WAIT);
      schedulerInfo.setLog(buildNewLogList(schedulerInfo.getLog(), infoLog));
      Long count = schedulerInfoService.update(schedulerInfo);
      return count > 0;
    }

    private List<SchedulerInfoLog> buildNewLogList(
        List<SchedulerInfoLog> logList, SchedulerInfoLog infoLog) {
      if (null == logList) {
        logList = Lists.newArrayList();
      }
      if (logList.size() >= LOGGER_MAX_COUNT) {
        logList.remove(0);
      }
      logList.add(infoLog);
      return logList;
    }

    private boolean lockTask(Long id) {
      SchedulerInfo info = schedulerInfoService.getById(id);
      if (info.getLockTime() == null) {
        if (schedulerInfoService.updateLock(id) < 1) {
          log.warn("Failed to preempt scheduler lock, the lock is already occupied!");
          return false;
        }
        return true;
      }

      Date now = new Date();
      Date unLockTime = DateUtils.addMinutes(info.getLockTime(), LOCK_TIME_MINUTES);
      if (now.before(unLockTime)) {
        log.info(
            "Last scheduler lock preempt time:{},The threshold was not exceeded. Wait for the execution to complete",
            DateTimeUtils.getDate2LongStr(info.getLockTime()));
        return false;
      }

      // Timeout release lock
      log.info(
          "Last scheduler lock preempt time:{}, The threshold was exceeded. The current process is executed directly",
          DateTimeUtils.getDate2LongStr(info.getLockTime()));
      unlockTask(id, true);
      if (schedulerInfoService.updateLock(id) < 1) {
        log.warn("Failed to re-preempt scheduler lock!");
        return false;
      }
      log.info("Re-preempt scheduler lock successfully!");
      return true;
    }

    /** Release lock after scheduling is completed */
    private void unlockTask(Long id, boolean lock) {
      if (!lock) {
        return;
      }
      schedulerInfoService.updateUnlock(id);
      log.info("Scheduler lock released successfully!");
    }
  }
}
