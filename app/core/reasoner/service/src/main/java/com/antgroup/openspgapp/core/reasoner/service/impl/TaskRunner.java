package com.antgroup.openspgapp.core.reasoner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.pipeline.ExecuteNode;
import com.antgroup.openspg.builder.model.pipeline.Node;
import com.antgroup.openspg.builder.model.pipeline.Pipeline;
import com.antgroup.openspg.builder.model.pipeline.enums.StatusEnum;
import com.antgroup.openspg.builder.model.record.RecordAlterOperationEnum;
import com.antgroup.openspg.builder.model.record.SubGraphRecord;
import com.antgroup.openspg.builder.runner.local.LocalBuilderRunner;
import com.antgroup.openspg.common.util.pemja.PemjaUtils;
import com.antgroup.openspg.common.util.pemja.PythonInvokeMethod;
import com.antgroup.openspg.common.util.pemja.model.PemjaConfig;
import com.antgroup.openspg.reasoner.catalog.impl.KgSchemaConnectionInfo;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.runner.local.LocalReasonerRunner;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.core.reasoner.service.CatalogService;
import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Edge;
import com.antgroup.openspgapp.core.reasoner.model.task.result.GraphResult;
import com.antgroup.openspgapp.core.reasoner.model.task.result.TableResult;
import com.antgroup.openspgapp.core.reasoner.service.utils.ReasonerValue;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

/* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/impl/TaskRunner.class */
public class TaskRunner {
  private static final Logger log = LoggerFactory.getLogger(TaskRunner.class);
  private final ThreadPoolExecutor threadPoolExecutor;
  private final Map<Long, TaskRuntime> runningTask;
  private final Thread waitThread;
  private static ReasonerValue reasonerValue;
  private static ProjectManager projectManager;
  private static CatalogService catalogService;
  private static String schemaUrl;

  public TaskRunner(
      ReasonerValue reasonerValue2,
      ProjectManager projectManager2,
      CatalogService catalogService2) {
    reasonerValue = reasonerValue2;
    projectManager = projectManager2;
    schemaUrl = reasonerValue2.getSchemaUrlHost();
    catalogService = catalogService2;
    this.threadPoolExecutor =
        new ThreadPoolExecutor(
            3,
            10,
            3600L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue(10),
            new ThreadPoolExecutor.CallerRunsPolicy());
    this.runningTask = new ConcurrentHashMap();
    this.waitThread = new Thread(new WaitThread(this.runningTask));
    this.waitThread.start();
  }

  public void run(Task task, Function<Task, Integer> callback) {
    String graphStoreUrl = projectManager.getGraphStoreUrl(task.getProjectId());
    Future<Task> future =
        this.threadPoolExecutor.submit(new ReasonerLocalTask(task, graphStoreUrl, schemaUrl));
    this.runningTask.put(task.getId(), new TaskRuntime(task.getId(), null, future, callback));
  }

  public void dialog(Task task, Function<Task, Integer> callback) {
    Pipeline pipeline = (Pipeline) task.getExtend();
    Map<String, ExecuteNode> nodes = Maps.newHashMap();
    for (int i = 0; i < pipeline.getNodes().size(); i++) {
      ExecuteNode executeNode = new ExecuteNode((Node) pipeline.getNodes().get(i));
      executeNode.setIndex(i);
      if (i == 0) {
        executeNode.setStatus(StatusEnum.RUNNING);
      }
      nodes.put(executeNode.getId(), executeNode);
    }
    Future<Task> future = this.threadPoolExecutor.submit(new AutoSchemaTask(task, nodes));
    this.runningTask.put(task.getId(), new TaskRuntime(task.getId(), nodes, future, callback));
  }

  public void nlQuery(Task task, Function<Task, Integer> callback) {
    Future<Task> future = this.threadPoolExecutor.submit(new NlQueryTask(task));
    this.runningTask.put(task.getId(), new TaskRuntime(task.getId(), null, future, callback));
  }

  public Boolean cancel(Long id) {
    TaskRuntime runtime = this.runningTask.get(id);
    if (null == runtime) {
      return null;
    }
    runtime.getFuture().cancel(true);
    return Boolean.valueOf(runtime.getFuture().isCancelled());
  }

  /* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/impl/TaskRunner$NlQueryTask.class */
  private static class NlQueryTask implements Callable<Task> {
    protected final Task task;

    public NlQueryTask(Task task) {
      this.task = task;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.concurrent.Callable
    public Task call() throws Exception {
      this.task.setResultMessage((String) null);
      Project project = TaskRunner.projectManager.queryById(this.task.getProjectId());
      JSONObject config = JSONObject.parseObject(project.getConfig());
      JSONObject llm = config.getJSONObject("llm");
      JSONObject prompt = config.getJSONObject("prompt");
      if (llm != null && llm.containsKey("maya_http")) {
        try {
          mayaSolver(llm.getString("maya_http"), llm.getString("host_addr"), this.task);
          this.task.setStatus(com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum.RUNNING);
        } catch (Exception e) {
          TaskRunner.log.error("mayaSolver execute error,", e);
          this.task.setResultMessage(ExceptionUtils.getStackTrace(e));
          this.task.setStatus(com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum.ERROR);
        }
      } else if (prompt != null
          && prompt.containsKey("maya_server")
          && StringUtils.isNotBlank(prompt.getString("maya_server"))) {
        try {
          JSONObject mayaServer = JSONObject.parseObject(prompt.getString("maya_server"));
          mayaSolver(
              mayaServer.getString("maya_http"), mayaServer.getString("host_addr"), this.task);
          this.task.setStatus(com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum.RUNNING);
        } catch (Exception e2) {
          TaskRunner.log.error("mayaSolver execute error,", e2);
          this.task.setResultMessage(ExceptionUtils.getStackTrace(e2));
          this.task.setStatus(com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum.ERROR);
        }
      } else {
        try {
          PemjaConfig pemjaConfig =
              new PemjaConfig(
                  TaskRunner.reasonerValue.getPythonExec(),
                  TaskRunner.reasonerValue.getPythonPaths(),
                  TaskRunner.reasonerValue.getPythonEnv(),
                  TaskRunner.reasonerValue.getSchemaUrlHost(),
                  this.task.getProjectId(),
                  PythonInvokeMethod.BRIDGE_SOLVER_MAIN,
                  Maps.newHashMap());
          JSONObject args = new JSONObject();
          JSONObject json = (JSONObject) JSON.toJSON(this.task.getParams());
          args.put("args", json);
          PemjaUtils.invoke(
              pemjaConfig,
              new Object[] {
                this.task.getProjectId().toString(),
                this.task.getSessionId().toString(),
                this.task.getId(),
                this.task.getNl(),
                args.toJSONString()
              });
          this.task.setStatus(com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum.FINISH);
        } catch (Exception e3) {
          TaskRunner.log.error("nl query execute error,", e3);
          this.task.setResultMessage(ExceptionUtils.getStackTrace(e3));
          this.task.setStatus(com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum.ERROR);
        }
      }
      return this.task;
    }

    public static void mayaSolver(String http, String hostAddr, Task task) throws Exception {
      JSONObject reqObj = new JSONObject();
      reqObj.put("query", task.getNl());
      reqObj.put("report", true);
      if (StringUtils.isNotBlank(hostAddr)) {
        reqObj.put("host_addr", hostAddr);
      }
      JSONObject mayaParamFeature = new JSONObject();
      mayaParamFeature.put("project_id", task.getProjectId().toString());
      mayaParamFeature.put("req_id", task.getId().toString());
      mayaParamFeature.put("cmd", "submit");
      mayaParamFeature.put("mode", "async");
      mayaParamFeature.put("req", reqObj.toString());
      JSONObject features = new JSONObject();
      features.put("in_string", mayaParamFeature.toString());
      JSONObject mayaParam = new JSONObject();
      mayaParam.put("features", features);
      URL url = new URL(http);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
      conn.setRequestProperty("MPS-app-name", "test");
      conn.setRequestProperty("MPS-http-version", "1.0");
      conn.setRequestProperty("MPS-debug", "true");
      conn.setDoOutput(true);
      OutputStream os = conn.getOutputStream();
      Throwable th = null;
      try {
        try {
          byte[] input = mayaParam.toString().getBytes("utf-8");
          os.write(input, 0, input.length);
          if (os != null) {
            if (0 != 0) {
              try {
                os.close();
              } catch (Throwable th2) {
                th.addSuppressed(th2);
              }
            } else {
              os.close();
            }
          }
          int status = conn.getResponseCode();
          BufferedReader br =
              new BufferedReader(
                  new InputStreamReader(
                      (status < 200 || status >= 300)
                          ? conn.getErrorStream()
                          : conn.getInputStream(),
                      "utf-8"));
          StringBuilder response = new StringBuilder();
          while (true) {
            String responseLine = br.readLine();
            if (responseLine == null) {
              break;
            } else {
              response.append(responseLine.trim());
            }
          }
          br.close();
          if (status != 200) {
            TaskRunner.log.error("Invoke service status failed," + ((Object) response));
            task.setResultMessage(response.toString());
            task.setStatus(com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum.ERROR);
            return;
          }
          JSONObject responseJson = JSON.parseObject(response.toString());
          if (!responseJson.getBoolean("success").booleanValue()) {
            TaskRunner.log.error("Invoke service failed," + ((Object) response));
            task.setResultMessage(responseJson.toJSONString());
            task.setStatus(com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum.ERROR);
          } else {
            task.setStatus(com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum.RUNNING);
            TaskRunner.log.info("Invoke service success," + ((Object) response));
          }
        } catch (Throwable th3) {
          th = th3;
          throw th3;
        }
      } catch (Throwable th4) {
        if (os != null) {
          if (th != null) {
            try {
              os.close();
            } catch (Throwable th5) {
              th.addSuppressed(th5);
            }
          } else {
            os.close();
          }
        }
        throw th4;
      }
    }
  }

  /* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/impl/TaskRunner$AutoSchemaTask.class */
  private static class AutoSchemaTask implements Callable<Task> {
    protected final Task task;
    protected final Map<String, ExecuteNode> nodes;

    public AutoSchemaTask(Task task, Map<String, ExecuteNode> nodes) {
      this.task = task;
      this.nodes = nodes;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.concurrent.Callable
    public Task call() throws Exception {
      Pipeline pipeline = (Pipeline) this.task.getExtend();
      BuilderContext builderContext =
          new BuilderContext()
              .setProjectId(this.task.getProjectId().longValue())
              .setJobName("nl_extract_job")
              .setPythonExec(TaskRunner.reasonerValue.getPythonExec())
              .setPythonPaths(TaskRunner.reasonerValue.getPythonPaths())
              .setPythonEnv(TaskRunner.reasonerValue.getPythonEnv())
              .setOperation(RecordAlterOperationEnum.UPSERT)
              .setEnableLeadTo(false)
              .setProject(
                  JSON.toJSONString(TaskRunner.projectManager.queryById(this.task.getProjectId())))
              .setGraphStoreUrl(
                  TaskRunner.projectManager.getGraphStoreUrl(this.task.getProjectId()))
              .setSearchEngineUrl(TaskRunner.reasonerValue.getSearchEngineUrl())
              .setModelExecuteNum(TaskRunner.reasonerValue.getModelExecuteNum())
              .setExecuteNodes(this.nodes)
              .setSchemaUrl(TaskRunner.schemaUrl);
      LocalBuilderRunner runner = new LocalBuilderRunner(1);
      try {
        try {
          runner.init(pipeline, builderContext);
          runner.execute();
          this.task.setStatus(com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum.FINISH);
          runner.close();
        } catch (Exception e) {
          TaskRunner.log.error("runner execute error,", e);
          this.task.setStatus(com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum.ERROR);
          Iterator<String> it = this.nodes.keySet().iterator();
          while (true) {
            if (!it.hasNext()) {
              break;
            }
            String id = it.next();
            ExecuteNode node = this.nodes.get(id);
            if (StatusEnum.RUNNING.equals(node.getStatus())) {
              node.setStatus(StatusEnum.ERROR);
              node.addTraceLog("execute error:%s", new Object[] {ExceptionUtils.getStackTrace(e)});
              break;
            }
          }
          runner.close();
        }
        Iterator<String> it2 = this.nodes.keySet().iterator();
        while (true) {
          if (!it2.hasNext()) {
            break;
          }
          String id2 = it2.next();
          ExecuteNode node2 = this.nodes.get(id2);
          if (node2.getOutputs() != null && (node2.getOutputs() instanceof SubGraphRecord)) {
            SubGraphRecord record = (SubGraphRecord) node2.getOutputs();
            List<com.antgroup.openspgapp.core.reasoner.model.task.result.Node> resultNodes =
                (List)
                    JSON.parseObject(
                        JSON.toJSONString(record.getResultNodes()),
                        new TypeReference<
                            List<
                                com.antgroup.openspgapp.core.reasoner.model.task.result
                                    .Node>>() { // from class:
                          // com.antgroup.openspgapp.core.reasoner.service.impl.TaskRunner.AutoSchemaTask.1
                        },
                        new Feature[0]);
            List<Edge> resultEdge =
                (List)
                    JSON.parseObject(
                        JSON.toJSONString(record.getResultEdges()),
                        new TypeReference<List<Edge>>() { // from class:
                          // com.antgroup.openspgapp.core.reasoner.service.impl.TaskRunner.AutoSchemaTask.2
                        },
                        new Feature[0]);
            this.task.setResultNodes(resultNodes);
            this.task.setResultEdges(resultEdge);
            break;
          }
        }
        this.task.setResultMessage(JSON.toJSONString(this.nodes.values()));
        return this.task;
      } catch (Throwable th) {
        runner.close();
        throw th;
      }
    }
  }

  /* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/impl/TaskRunner$ReasonerLocalTask.class */
  private static class ReasonerLocalTask implements Callable<Task> {
    protected final Task request;
    private final String graphStoreUrl;
    private final String schemaUrl;
    private final String graphStateClass =
        "com.antgroup.openspg.reasoner.warehouse.cloudext.CloudExtGraphState";

    public ReasonerLocalTask(Task request, String graphStoreUrl, String schemaUrl) {
      this.request = request;
      this.graphStoreUrl = graphStoreUrl;
      this.schemaUrl = schemaUrl;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.concurrent.Callable
    public Task call() throws Exception {
      LocalReasonerTask task = new LocalReasonerTask();
      task.setId(String.valueOf(this.request.getId()));
      Catalog catalog =
          TaskRunner.catalogService.getCatalog(this.request.getProjectId(), this.graphStoreUrl);
      task.setDsl(this.request.getDsl());
      task.setConnInfo(new KgSchemaConnectionInfo(this.schemaUrl, ""));
      task.setCatalog(catalog);
      task.setGraphStateClassName(
          "com.antgroup.openspg.reasoner.warehouse.cloudext.CloudExtGraphState");
      task.setGraphStateInitString(this.graphStoreUrl);
      task.setStartIdList(getStartIdListFromParams());
      task.setParams(getTaskParams());
      task.setExecutorTimeoutMs(180000L);
      LocalReasonerRunner runner = new LocalReasonerRunner();
      LocalReasonerResult result = runner.run(task);
      String errorMsg = null;
      if (null == result) {
        errorMsg = "";
      } else if (StringUtils.isNotEmpty(result.getErrMsg())) {
        errorMsg = result.getErrMsg();
      }
      if (null != errorMsg) {
        Task updateErrorTask = new Task();
        updateErrorTask.setId(this.request.getId());
        updateErrorTask.setStatus(
            com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum.ERROR);
        updateErrorTask.setResultMessage(errorMsg);
        return updateErrorTask;
      }
      Task taskResult = new Task();
      taskResult.setStatus(com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum.FINISH);
      TableResult resultTableResult = getTableResult(result);
      taskResult.setResultTable(resultTableResult);
      GraphResult graphResult = getGraphResult(result);
      taskResult.setResultNodes(graphResult.getNodeList());
      taskResult.setResultEdges(graphResult.getEdgeList());
      taskResult.setResultPaths(graphResult.getPathList());
      return taskResult;
    }

    protected TableResult getTableResult(LocalReasonerResult result) {
      TableResult tableResult = new TableResult();
      if (null != result.getColumns()) {
        tableResult.setHeader((String[]) result.getColumns().toArray(new String[0]));
      }
      if (null != result.getRows()) {
        tableResult.setRows(result.getRows());
        tableResult.setTotal(tableResult.getRows().size());
      }
      return tableResult;
    }

    protected GraphResult getGraphResult(LocalReasonerResult result) {
      Map<String, String> vertexIdMap = new HashMap<>();
      GraphResult graphResult = new GraphResult();
      List<IVertex<IVertexId, IProperty>> vertexList = result.getVertexList();
      if (CollectionUtils.isNotEmpty(vertexList)) {
        List<com.antgroup.openspgapp.core.reasoner.model.task.result.Node> nodeList =
            new ArrayList<>(vertexList.size());
        for (IVertex<IVertexId, IProperty> vertex : vertexList) {
          nodeList.add(new com.antgroup.openspgapp.core.reasoner.model.task.result.Node(vertex));
        }
        graphResult.setNodeList(nodeList);
      }
      List<IEdge<IVertexId, IProperty>> edgeList = result.getEdgeList();
      if (CollectionUtils.isNotEmpty(edgeList)) {
        List<Edge> resultEdgeList = new ArrayList<>(edgeList.size());
        for (IEdge<IVertexId, IProperty> edge : edgeList) {
          Edge newEdge = new Edge(edge);
          resultEdgeList.add(newEdge);
          vertexIdMap.put(newEdge.getFrom(), newEdge.getFromId());
          vertexIdMap.put(newEdge.getTo(), newEdge.getToId());
        }
        graphResult.setEdgeList(resultEdgeList);
      }
      for (com.antgroup.openspgapp.core.reasoner.model.task.result.Node node :
          graphResult.getNodeList()) {
        String bizId = vertexIdMap.get(node.getId());
        if (StringUtils.isNotEmpty(bizId)) {
          node.setName(bizId);
        }
      }
      return graphResult;
    }

    protected List<Tuple2<String, String>> getStartIdListFromParams() {
      String id = (String) this.request.getParams().get("id");
      String type = (String) this.request.getParams().get("type");
      if (StringUtils.isEmpty(id) || StringUtils.isEmpty(type)) {
        return null;
      }
      return Lists.newArrayList(new Tuple2[] {new Tuple2(id, type)});
    }

    protected Map<String, Object> getTaskParams() {
      Map<String, Object> params = new HashMap<>();
      params.put("projId", String.valueOf(this.request.getProjectId()));
      params.put("kg.reasoner.output.graph", "true");
      params.putAll(this.request.getParams());
      return params;
    }
  }

  /* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/impl/TaskRunner$WaitThread.class */
  private static class WaitThread implements Runnable {
    private final Map<Long, TaskRuntime> runningTask;

    public WaitThread(Map<Long, TaskRuntime> runningTask) {
      this.runningTask = runningTask;
    }

    @Override // java.lang.Runnable
    public void run() {
      while (true) {
        try {
          waitAll();
          Thread.sleep(1000L);
        } catch (Throwable e) {
          TaskRunner.log.error("TaskRunner_WaitThread_Error", e);
        }
      }
    }

    private void waitAll() {
      Iterator<Map.Entry<Long, TaskRuntime>> it = this.runningTask.entrySet().iterator();
      while (it.hasNext()) {
        Task resultTask = new Task();
        Map.Entry<Long, TaskRuntime> entry = it.next();
        long id = entry.getKey().longValue();
        try {
          Future<Task> future = entry.getValue().getFuture();
          if (MapUtils.isNotEmpty(entry.getValue().getNodes())) {
            resultTask.setId(Long.valueOf(id));
            resultTask.setResultMessage(JSON.toJSONString(entry.getValue().getNodes().values()));
            Function<Task, Integer> callback = entry.getValue().getCallback();
            callback.apply(resultTask);
          }
          if (future.isDone()) {
            it.remove();
            TaskRunner.log.info("reasoner_task_done,id=" + id);
            try {
              resultTask = future.get();
              resultTask.setId(Long.valueOf(id));
              Function<Task, Integer> callback2 = entry.getValue().getCallback();
              callback2.apply(resultTask);
            } catch (Throwable th) {
              resultTask.setId(Long.valueOf(id));
              Function<Task, Integer> callback3 = entry.getValue().getCallback();
              callback3.apply(resultTask);
              throw th;
            }
          }
        } catch (InterruptedException e) {
          resultTask.setStatus(
              com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum.CANCELED);
        } catch (Throwable e2) {
          TaskRunner.log.error("get task result error,", e2);
          resultTask.setStatus(com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum.ERROR);
          resultTask.setResultMessage("get task result error, " + e2.getMessage());
        }
      }
    }
  }
}
