package com.antgroup.openspgapp.core.reasoner.service.impl;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.service.builder.BuilderJobRepository;
import com.antgroup.openspgapp.common.util.enums.BuilderJobStatus;
import com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum;
import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import com.antgroup.openspgapp.core.reasoner.service.repository.ReasonTaskRepository;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/impl/TaskCallBack.class */
public class TaskCallBack implements Function<Task, Integer> {
  private static final Logger log = LoggerFactory.getLogger(TaskCallBack.class);
  private final ReasonTaskRepository reasonTaskRepository;
  private final BuilderJobRepository builderJobRepository;

  public TaskCallBack(
      ReasonTaskRepository reasonTaskRepository, BuilderJobRepository builderJobRepository) {
    this.reasonTaskRepository = reasonTaskRepository;
    this.builderJobRepository = builderJobRepository;
  }

  @Override // java.util.function.Function
  public Integer apply(Task task) {
    Task taskInDb = this.reasonTaskRepository.query(task.getId());
    if (!StatusEnum.RUNNING.equals(taskInDb.getStatus())) {
      log.info("reasoner_task_id=" + task.getId() + ",status=" + taskInDb.getStatus());
      return 0;
    }
    log.info("reasoner_task_id=" + task.getId() + ",update_status=" + task.getStatus());
    updateBuilderJob(task);
    if (checkNoUpdate(task)) {
      log.info(
          "The task has no update field, reasoner_task_id=" + task.getId() + ",update_status=");
      return 0;
    }
    return Integer.valueOf(this.reasonTaskRepository.update(task));
  }

  private boolean checkNoUpdate(Task task) {
    if (task == null) {
      return true;
    }
    String jsonString = JSON.toJSONString(task);
    Task t = new Task();
    t.setId(task.getId());
    return jsonString.equals(JSON.toJSONString(t));
  }

  public Integer updateBuilderJob(Task task) {
    if (task.getParams() == null
        || StringUtils.isBlank((CharSequence) task.getParams().get("BuilderJobId"))) {
      return 0;
    }
    Long jobId = Long.valueOf((String) task.getParams().get("BuilderJobId"));
    BuilderJob job = new BuilderJob();
    job.setId(jobId);
    switch (AnonymousClass1.$SwitchMap$com$antgroup$openspgapp$core$reasoner$model$task$StatusEnum[
        task.getStatus().ordinal()]) {
      case 1:
        job.setStatus(BuilderJobStatus.WAIT.name());
        break;
      case 2:
        job.setStatus(BuilderJobStatus.RUNNING.name());
        break;
      case 3:
      case 4:
        job.setStatus(BuilderJobStatus.ERROR.name());
        break;
      case 5:
        job.setStatus(BuilderJobStatus.TERMINATE.name());
        break;
      case 6:
        String extension = (String) task.getParams().get("extension");
        Boolean autoWrite =
            JSON.parseObject(extension).getJSONObject("extractConfig").getBoolean("autoWrite");
        String status =
            autoWrite.booleanValue()
                ? BuilderJobStatus.FINISH.name()
                : BuilderJobStatus.PENDING.name();
        job.setStatus(status);
        break;
    }
    log.info("builder_job_id=" + jobId + ",update_status=" + job.getStatus());
    return Integer.valueOf(this.builderJobRepository.update(job).intValue());
  }

  /* renamed from: com.antgroup.openspgapp.core.reasoner.service.impl.TaskCallBack$1, reason: invalid class name */
  /* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/impl/TaskCallBack$1.class */
  static /* synthetic */ class AnonymousClass1 {
    static final /* synthetic */ int[]
        $SwitchMap$com$antgroup$openspgapp$core$reasoner$model$task$StatusEnum =
            new int[StatusEnum.values().length];

    static {
      try {
        $SwitchMap$com$antgroup$openspgapp$core$reasoner$model$task$StatusEnum[
                StatusEnum.INIT.ordinal()] =
            1;
      } catch (NoSuchFieldError e) {
      }
      try {
        $SwitchMap$com$antgroup$openspgapp$core$reasoner$model$task$StatusEnum[
                StatusEnum.RUNNING.ordinal()] =
            2;
      } catch (NoSuchFieldError e2) {
      }
      try {
        $SwitchMap$com$antgroup$openspgapp$core$reasoner$model$task$StatusEnum[
                StatusEnum.ERROR.ordinal()] =
            3;
      } catch (NoSuchFieldError e3) {
      }
      try {
        $SwitchMap$com$antgroup$openspgapp$core$reasoner$model$task$StatusEnum[
                StatusEnum.TIMEOUT.ordinal()] =
            4;
      } catch (NoSuchFieldError e4) {
      }
      try {
        $SwitchMap$com$antgroup$openspgapp$core$reasoner$model$task$StatusEnum[
                StatusEnum.CANCELED.ordinal()] =
            5;
      } catch (NoSuchFieldError e5) {
      }
      try {
        $SwitchMap$com$antgroup$openspgapp$core$reasoner$model$task$StatusEnum[
                StatusEnum.FINISH.ordinal()] =
            6;
      } catch (NoSuchFieldError e6) {
      }
    }
  }
}
