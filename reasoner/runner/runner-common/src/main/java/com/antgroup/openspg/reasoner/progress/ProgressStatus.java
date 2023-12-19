/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.reasoner.progress;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProgressStatus implements Serializable {
  private static final Logger log = LoggerFactory.getLogger(ProgressStatus.class);

  @Getter @Setter private JobStatus status;
  @Getter @Setter private ProgressInfo progressInfo;
  @Getter private String errMsg;
  private final String instanceId;
  private final String taskKey;
  private final String persistenceWay;
  @Getter private final Map<TimeConsumeType, Long> timeConsumeMap = new HashMap<>();
  private TimeConsumeType nowType;
  private long nowTypeStartMs;

  public ProgressStatus(JSONObject context) {
    this.instanceId = context.getString("instanceId");
    this.taskKey = context.getString("taskKey");
    this.progressInfo = new ProgressInfo();
    this.errMsg = context.getString("errMsg");
    this.status = str2Status(context.getString("status"));
    this.persistenceWay = "oss";
  }

  public ProgressStatus(String instanceId, String taskKey, String persistenceWay) {
    this.status = JobStatus.pending;
    this.progressInfo = new ProgressInfo();
    this.instanceId = instanceId;
    this.taskKey = taskKey;
    this.persistenceWay = persistenceWay;
  }

  public void init(long totalSteps) {
    this.status = JobStatus.pending;
    this.progressInfo.setTotalSteps(totalSteps);
  }

  /** Persistent progress status files */
  public abstract void persistenceProgressStatus();

  /** Retrieve task status information from storage. */
  public abstract void refresh();

  /** Reset the exists progress status file */
  public abstract void reset();

  private JobStatus str2Status(String s) {
    if ("ERROR".equals(s)) {
      return JobStatus.error;
    } else if ("FINISH".equals(s)) {
      return JobStatus.finished;
    } else if ("RUNNING".equals(s)) {
      return JobStatus.running;
    } else {
      return JobStatus.pending;
    }
  }

  private String status2Str(JobStatus status) {
    if (status == JobStatus.error) {
      return "ERROR";
    } else if (status == JobStatus.finished) {
      return "FINISH";
    } else if (status == JobStatus.running) {
      return "RUNNING";
    } else {
      // pending
      return "WAIT";
    }
  }

  public String toJson() {
    Map<String, Object> context = new HashMap<>();
    context.put("instanceId", this.instanceId);
    context.put("taskKey", this.taskKey);
    context.put("errMsg", this.errMsg);
    context.put("progressInfo", this.progressInfo);
    context.put("status", status2Str(this.status));
    context.put("timeConsumeMap", this.timeConsumeMap);
    context.put("ts", System.currentTimeMillis() / 1000);
    return JSON.toJSONString(context, true);
  }

  public void updateStatus(JobStatus jobStatus, String errMsg) {
    this.status = jobStatus;
    this.errMsg = errMsg;
    persistenceProgressStatus();
  }

  public void finishedProgress() {
    this.status = JobStatus.finished;
    this.progressInfo.setProcessOffset(this.progressInfo.getRealTotal());
    this.progressInfo.setReadOffset(this.progressInfo.getRealTotal());
    persistenceProgressStatus();
  }

  public boolean isFinished() {
    return JobStatus.finished == this.status;
  }

  public boolean isError() {
    return JobStatus.error == this.status;
  }

  public void setStepTotal(long step, long total) {
    if (isFinished()) {
      throw new RuntimeException(
          "task is finished, can not change status, now status=" + this.status);
    }
    if (step == this.progressInfo.getCurStep() && total == this.progressInfo.getTotal()) {
      // The same steps do not need to be reset.
      return;
    }
    this.status = JobStatus.running;
    this.progressInfo.setTotal(total);
    this.progressInfo.setCurStep(step);
    if (this.progressInfo.getTotalSteps() < this.progressInfo.getCurStep()) {
      this.progressInfo.setTotalSteps(step);
    }
    this.progressInfo.setProcessOffset(0);
    this.progressInfo.setReadOffset(0);

    persistenceProgressStatus();
  }

  public void updateProgress(long batchId, long readOffset, long processOffset) {
    if (isFinished()) {
      throw new RuntimeException(
          "task is finished, can not change status, now status=" + this.status);
    }
    this.status = JobStatus.running;
    this.progressInfo.setBatchId(batchId);
    this.progressInfo.setReadOffset(readOffset);
    this.progressInfo.setProcessOffset(processOffset);
    if (this.progressInfo.getProcessOffset() > this.progressInfo.getTotal()) {
      log.warn(
          "ProgressStatus::updateProgress ProcessOffset("
              + this.progressInfo.getProcessOffset()
              + ") > Total("
              + this.progressInfo.getTotal()
              + ")");
      this.progressInfo.setTotal(this.progressInfo.getRealProcessOffset());
    }

    persistenceProgressStatus();
  }

  public void setTimeConsumeType(TimeConsumeType type) {
    long nowMs = System.currentTimeMillis();

    if (null == nowType) {
      nowType = type;
      nowTypeStartMs = nowMs;
    } else if (!nowType.equals(type)) {
      Long oldV = timeConsumeMap.getOrDefault(nowType, 0L);
      timeConsumeMap.put(nowType, oldV + (nowMs - nowTypeStartMs));

      nowType = type;
      nowTypeStartMs = nowMs;
    }
  }

  public enum JobStatus implements Serializable {
    running,
    pending,
    error,
    finished
  }

  @Getter
  @Setter
  public static class ProgressInfo implements Serializable {
    private long batchId;
    private long totalSteps;
    private long curStep;
    private long total;
    private long readOffset;
    private long processOffset;

    private long shrinkFactor = 1;

    public long getTotal() {
      return total / this.shrinkFactor;
    }

    @JSONField(serialize = false)
    public long getRealTotal() {
      return total;
    }

    public void setTotal(long total) {
      if (total <= Integer.MAX_VALUE) {
        shrinkFactor = 1;
      } else {
        long tmpTotal = total;
        while (tmpTotal > Integer.MAX_VALUE) {
          tmpTotal = tmpTotal / 10;
          shrinkFactor = shrinkFactor * 10;
        }
      }
      this.total = total;
    }

    @JSONField(serialize = false)
    public long getRealReadOffset() {
      return readOffset;
    }

    public long getReadOffset() {
      return readOffset / this.shrinkFactor;
    }

    public long getProcessOffset() {
      return processOffset / this.shrinkFactor;
    }

    @JSONField(serialize = false)
    public long getRealProcessOffset() {
      return processOffset;
    }
  }
}
