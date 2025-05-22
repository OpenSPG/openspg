package com.antgroup.openspgapp.api.http.server.reasoner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.antgroup.openspg.builder.core.physical.utils.CommonUtils;
import com.antgroup.openspg.builder.model.pipeline.ExecuteNode;
import com.antgroup.openspg.builder.model.record.SubGraphRecord;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClient;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClientDriverManager;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.bulider.BuilderJobQuery;
import com.antgroup.openspg.server.common.model.exception.IllegalParamsException;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.service.builder.BuilderJobService;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerInstanceService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspgapp.api.http.server.BaseController;
import com.antgroup.openspgapp.biz.reasoner.TaskManager;
import com.antgroup.openspgapp.core.reasoner.model.SubGraph;
import com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum;
import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Edge;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Node;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.task.MarkTaskRequest;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.task.QueryTaskRequest;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.task.QueryTasksRequest;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.task.StopTaskRequest;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.task.SubmitTaskRequest;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.task.TaskResponse;
import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping({"/public/v1/reasoner/task"})
@Controller
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/reasoner/TaskController.class */
public class TaskController extends BaseController {
  private static final Logger log = LoggerFactory.getLogger(TaskController.class);

  @Autowired private TaskManager taskManager;

  @Autowired private ProjectManager projectManager;

  @Autowired private BuilderJobService builderJobService;

  @Autowired private SchedulerInstanceService schedulerInstanceService;

  @Autowired private SchedulerTaskService schedulerTaskService;

  @Autowired private DefaultValue value;

  @RequestMapping(
      value = {"/list"},
      method = {RequestMethod.GET})
  @ResponseBody
  public HttpResult<List<TaskResponse>> list(final QueryTasksRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<List<TaskResponse>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.TaskController.1
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("start", request.getStart());
            AssertUtils.assertParamObjectIsNotNull("limit", request.getLimit());
            if (null == request.getProjectId()
                && null == request.getUserId()
                && null == request.getSessionId()) {
              throw new IllegalParamsException(
                  "projectId and userId and sessionId are all null", new Object[0]);
            }
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public List<TaskResponse> action() {
            List<Task> taskList =
                TaskController.this.taskManager.queryTasks(
                    request.getProjectId(),
                    request.getUserId(),
                    request.getSessionId(),
                    request.getMark(),
                    request.getKeyword(),
                    request.getStart(),
                    request.getLimit());
            LocalDateTime now = LocalDateTime.now();
            taskList.forEach(
                task -> {
                  ChronoLocalDateTime<LocalDate> localDateTime =
                      task.getGmtModified()
                          .toInstant()
                          .atZone(ZoneId.systemDefault())
                          .toLocalDateTime();
                  if (StatusEnum.RUNNING.equals(task.getStatus())
                      && ChronoUnit.MINUTES.between(localDateTime, now) > 5) {
                    task.setStatus(StatusEnum.TIMEOUT);
                  }
                });
            return (List) taskList.stream().map(Utils::convert).collect(Collectors.toList());
          }
        });
  }

  @RequestMapping(
      value = {"/submit"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<TaskResponse> submit(@RequestBody final SubmitTaskRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<TaskResponse>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.TaskController.2
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("sessionId", request.getSessionId());
            if (StringUtils.isBlank(request.getDsl()) && StringUtils.isBlank(request.getNl())) {
              throw new IllegalParamsException("dsl and nl are blank", new Object[0]);
            }
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public TaskResponse action() {
            Task task =
                TaskController.this.taskManager.submit(
                    request.getSessionId(),
                    request.getUserId(),
                    request.getDsl(),
                    request.getNl(),
                    request.getParams());
            return Utils.convert(task);
          }
        });
  }

  @RequestMapping(
      value = {"/builder/query"},
      method = {RequestMethod.GET})
  @ResponseBody
  public HttpResult<TaskResponse> builderQuery(final QueryTaskRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<TaskResponse>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.TaskController.3
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("id", request.getId());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public TaskResponse action() {
            BuilderJob job = new BuilderJob();
            boolean jobTask = false;
            if (request.getJobId() != null) {
              job = TaskController.this.builderJobService.getById(request.getJobId());
              if ("V3".equals(job.getVersion())) {
                jobTask = true;
              }
            } else {
              BuilderJobQuery record = new BuilderJobQuery();
              record.setVersion("V3");
              record.setTaskId(request.getId());
              List<BuilderJob> jobs =
                  TaskController.this.builderJobService.query(record).getResults();
              if (CollectionUtils.isNotEmpty(jobs)) {
                job = jobs.get(0);
                jobTask = true;
              }
            }
            Task task = new Task();
            if (!jobTask) {
              task = TaskController.this.taskManager.query(request.getId());
            } else {
              task.setId(request.getId());
              task.setProjectId(job.getProjectId());
              SchedulerInstanceQuery record2 = new SchedulerInstanceQuery();
              record2.setPageNo(1);
              record2.setPageSize(1);
              record2.setJobId(job.getTaskId());
              List<SchedulerInstance> instances =
                  TaskController.this.schedulerInstanceService.query(record2).getResults();
              SchedulerTaskQuery query = new SchedulerTaskQuery();
              query.setInstanceId(instances.get(0).getId());
              List<SchedulerTask> tasks =
                  TaskController.this.schedulerTaskService.query(query).getResults();
              List<ExecuteNode> nodes = Lists.newArrayList();
              List<Node> resultNodes = Lists.newArrayList();
              List<Edge> resultEdges = Lists.newArrayList();
              ObjectStorageClient objectStorageClient =
                  ObjectStorageClientDriverManager.getClient(
                      TaskController.this.value.getObjectStorageUrl());
              tasks.forEach(
                  schedulerTask -> {
                    ExecuteNode node = new ExecuteNode();
                    node.setTraceLog(new StringBuffer());
                    node.setId(schedulerTask.getNodeId());
                    node.setIndex(schedulerTask.getId().intValue());
                    node.setName(schedulerTask.getTitle());
                    switch (AnonymousClass8
                        .$SwitchMap$com$antgroup$openspg$server$common$model$scheduler$SchedulerEnum$TaskStatus[
                        schedulerTask.getStatus().ordinal()]) {
                      case 1:
                        node.setStatus(
                            com.antgroup.openspg.builder.model.pipeline.enums.StatusEnum.WAITING);
                        break;
                      case 2:
                        node.setStatus(
                            com.antgroup.openspg.builder.model.pipeline.enums.StatusEnum.RUNNING);
                        break;
                      case 3:
                        node.setStatus(
                            com.antgroup.openspg.builder.model.pipeline.enums.StatusEnum.ERROR);
                        break;
                      default:
                        node.setStatus(
                            com.antgroup.openspg.builder.model.pipeline.enums.StatusEnum.FINISH);
                        break;
                    }
                    node.getTraceLog().append(schedulerTask.getTraceLog());
                    node.setType(schedulerTask.getType());
                    nodes.add(node);
                    if ("kagWriterAsyncTask".equalsIgnoreCase(schedulerTask.getType())
                        && StringUtils.isNotBlank(schedulerTask.getOutput())) {
                      try {
                        String data =
                            objectStorageClient.getString(
                                TaskController.this.value.getBuilderBucketName(),
                                schedulerTask.getOutput());
                        List<SubGraphRecord> subGraphs =
                            (List)
                                JSON.parseObject(
                                    data,
                                    new TypeReference<List<SubGraphRecord>>() { // from class:
                                      // com.antgroup.openspgapp.api.http.server.reasoner.TaskController.3.1
                                    },
                                    new Feature[0]);
                        subGraphs.forEach(
                            subGraphRecord -> {
                              resultNodes.addAll(
                                  (Collection)
                                      JSON.parseObject(
                                          JSON.toJSONString(subGraphRecord.getResultNodes()),
                                          new TypeReference<List<Node>>() { // from class:
                                            // com.antgroup.openspgapp.api.http.server.reasoner.TaskController.3.2
                                          },
                                          new Feature[0]));
                              resultEdges.addAll(
                                  (Collection)
                                      JSON.parseObject(
                                          JSON.toJSONString(subGraphRecord.getResultEdges()),
                                          new TypeReference<List<Edge>>() { // from class:
                                            // com.antgroup.openspgapp.api.http.server.reasoner.TaskController.3.3
                                          },
                                          new Feature[0]));
                            });
                      } catch (Exception e) {
                        TaskController.log.error(
                            "get subGraphs Exception schedulerTask:" + schedulerTask.getId(), e);
                      }
                    }
                  });
              task.setResultMessage(JSON.toJSONString(nodes));
              task.setResultNodes(resultNodes);
              task.setResultEdges(resultEdges);
            }
            Project project = TaskController.this.projectManager.queryById(task.getProjectId());
            String namespace = project != null ? project.getNamespace() : "";
            TaskController.this.sampleSubGraph(namespace, task, namespace + ".Chunk", 1);
            return Utils.convert(task);
          }
        });
  }

  /* renamed from: com.antgroup.openspgapp.api.http.server.reasoner.TaskController$8, reason: invalid class name */
  /* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/reasoner/TaskController$8.class */
  static /* synthetic */ class AnonymousClass8 {
    static final /* synthetic */ int[]
        $SwitchMap$com$antgroup$openspg$server$common$model$scheduler$SchedulerEnum$TaskStatus =
            new int[SchedulerEnum.TaskStatus.values().length];

    static {
      try {
        $SwitchMap$com$antgroup$openspg$server$common$model$scheduler$SchedulerEnum$TaskStatus[
                SchedulerEnum.TaskStatus.WAIT.ordinal()] =
            1;
      } catch (NoSuchFieldError e) {
      }
      try {
        $SwitchMap$com$antgroup$openspg$server$common$model$scheduler$SchedulerEnum$TaskStatus[
                SchedulerEnum.TaskStatus.RUNNING.ordinal()] =
            2;
      } catch (NoSuchFieldError e2) {
      }
      try {
        $SwitchMap$com$antgroup$openspg$server$common$model$scheduler$SchedulerEnum$TaskStatus[
                SchedulerEnum.TaskStatus.ERROR.ordinal()] =
            3;
      } catch (NoSuchFieldError e3) {
      }
    }
  }

  @RequestMapping(
      value = {"/query"},
      method = {RequestMethod.GET})
  @ResponseBody
  public HttpResult<TaskResponse> query(final QueryTaskRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<TaskResponse>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.TaskController.4
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("id", request.getId());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public TaskResponse action() {
            Task task = TaskController.this.taskManager.query(request.getId());
            Project project = TaskController.this.projectManager.queryById(task.getProjectId());
            String namespace = project != null ? project.getNamespace() : "";
            TaskController.this.sampleSubGraph(namespace, task, namespace + ".Chunk", 1);
            return Utils.convert(task);
          }
        });
  }

  public void sampleSubGraph(String namespace, Task task, String nodeLabel, int sampleSize) {
    if (sampleSize <= 0) {
      throw new IllegalArgumentException("Sample size must be positive");
    }
    if (CollectionUtils.isEmpty(task.getResultNodes())
        || CollectionUtils.isEmpty(task.getResultEdges())) {
      return;
    }
    List<Node> nodesOfLabel = Lists.newArrayList();
    List<Node> sampleNodes = Lists.newArrayList();
    task.getResultNodes()
        .forEach(
            node -> {
              String label = CommonUtils.labelPrefix(namespace, node.getLabel());
              node.setLabel(label);
              node.getProperties().put("id", node.getId());
              node.getProperties().put("name", node.getName());
              if (label.equals(nodeLabel)) {
                nodesOfLabel.add(node);
              }
              if (sampleNodes.size() < 3) {
                sampleNodes.add(node);
              }
            });
    SubGraph subGraph = new SubGraph();
    subGraph.setResultNodes(nodesOfLabel);
    if (nodesOfLabel.size() == 0) {
      subGraph.setResultNodes(sampleNodes);
    }
    if (nodesOfLabel.size() <= sampleSize) {
      createSubGraphFromNodes(namespace, subGraph, task);
      createSubGraphFromNodes(namespace, subGraph, task);
      task.setResultNodes(subGraph.getResultNodes());
      task.setResultEdges(subGraph.getResultEdges());
      return;
    }
    Random random = new Random();
    Set<Node> sampledNodes = new HashSet<>();
    int index = 0;
    while (sampledNodes.size() < sampleSize && index < 5 * sampleSize) {
      index++;
      Node sampledNode = nodesOfLabel.get(random.nextInt(nodesOfLabel.size()));
      if (hasConnectedEdges(task, sampledNode)) {
        sampledNodes.add(sampledNode);
      }
    }
    subGraph.setResultNodes(new ArrayList(sampledNodes));
    createSubGraphFromNodes(namespace, subGraph, task);
    createSubGraphFromNodes(namespace, subGraph, task);
    task.setResultNodes(subGraph.getResultNodes());
    task.setResultEdges(subGraph.getResultEdges());
  }

  private boolean hasConnectedEdges(Task task, Node node) {
    return task.getResultEdges().stream()
        .anyMatch(
            edge -> {
              return edge.getFrom().equals(node.getId()) || edge.getTo().equals(node.getId());
            });
  }

  private void createSubGraphFromNodes(String namespace, SubGraph subGraph, Task task) {
    List<Node> nodes = subGraph.getResultNodes();
    Set<String> nodeIds =
        (Set)
            nodes.stream()
                .map(
                    (v0) -> {
                      return v0.getId();
                    })
                .collect(Collectors.toSet());
    ArrayList<Edge> newArrayList = Lists.newArrayList();
    task.getResultEdges()
        .forEach(
            edge -> {
              String fromType = CommonUtils.labelPrefix(namespace, edge.getFromType());
              String toType = CommonUtils.labelPrefix(namespace, edge.getToType());
              edge.setFromType(fromType);
              edge.setToType(toType);
              edge.setId(UUID.randomUUID().toString());
              if (nodeIds.contains(edge.getFrom()) || nodeIds.contains(edge.getTo())) {
                newArrayList.add(edge);
              }
            });
    Set<String> ids =
        (Set)
            newArrayList.stream()
                .map(
                    (v0) -> {
                      return v0.getFrom();
                    })
                .collect(Collectors.toSet());
    Set<String> toIds =
        (Set)
            newArrayList.stream()
                .map(
                    (v0) -> {
                      return v0.getTo();
                    })
                .collect(Collectors.toSet());
    ids.addAll(toIds);
    for (Node node : task.getResultNodes()) {
      if (!nodeIds.contains(node.getId())) {
        nodeIds.add(node.getId());
        String label = CommonUtils.labelPrefix(namespace, node.getLabel());
        node.setLabel(label);
        if (ids.contains(node.getId())) {
          node.getProperties().put("id", node.getId());
          node.getProperties().put("name", node.getName());
          nodes.add(node);
        }
      }
    }
    subGraph.getResultEdges().addAll(newArrayList);
  }

  @RequestMapping(
      value = {"/stop"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<Object> stop(@RequestBody final StopTaskRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Object>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.TaskController.5
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("id", request.getId());
          }

          public Object action() {
            TaskController.this.taskManager.stop(request.getId());
            return null;
          }
        });
  }

  @RequestMapping(
      value = {"/mark"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<Object> mark(@RequestBody final MarkTaskRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Object>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.TaskController.6
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("id", request.getId());
          }

          public Object action() {
            TaskController.this.taskManager.mark(request.getId());
            return null;
          }
        });
  }

  @RequestMapping(
      value = {"/unmark"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<Object> unMark(@RequestBody final MarkTaskRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Object>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.TaskController.7
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("id", request.getId());
          }

          public Object action() {
            TaskController.this.taskManager.unMark(request.getId());
            return null;
          }
        });
  }
}
