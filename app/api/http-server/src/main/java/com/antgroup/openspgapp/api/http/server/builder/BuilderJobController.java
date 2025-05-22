package com.antgroup.openspgapp.api.http.server.builder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.pipeline.Pipeline;
import com.antgroup.openspg.builder.model.pipeline.PipelineUtils;
import com.antgroup.openspg.builder.model.pipeline.config.Neo4jSinkNodeConfig;
import com.antgroup.openspg.builder.model.record.ChunkRecord;
import com.antgroup.openspg.builder.model.record.RecordAlterOperationEnum;
import com.antgroup.openspg.builder.model.record.SubGraphRecord;
import com.antgroup.openspg.builder.runner.local.physical.sink.impl.Neo4jSinkWriter;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClient;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClientDriverManager;
import com.antgroup.openspg.common.util.CommonUtils;
import com.antgroup.openspg.common.util.pemja.PemjaUtils;
import com.antgroup.openspg.common.util.pemja.model.PemjaConfig;
import com.antgroup.openspg.core.schema.model.type.BasicTypeEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.common.model.account.Account;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.datasource.DataSource;
import com.antgroup.openspg.server.common.model.exception.IllegalParamsException;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.common.model.scheduler.SchedulerEnum;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspg.server.common.service.datasource.meta.DataSourceMeta;
import com.antgroup.openspg.server.common.service.datasource.meta.client.CloudDataSource;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerInstanceQuery;
import com.antgroup.openspg.server.core.scheduler.model.query.SchedulerTaskQuery;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;
import com.antgroup.openspg.server.core.scheduler.service.api.SchedulerService;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerTaskService;
import com.antgroup.openspgapp.api.http.server.BaseController;
import com.antgroup.openspgapp.biz.builder.BuilderJobManager;
import com.antgroup.openspgapp.biz.reasoner.TaskManager;
import com.antgroup.openspgapp.biz.schema.AppSchemaManager;
import com.antgroup.openspgapp.biz.schema.dto.EntityTypeDTO;
import com.antgroup.openspgapp.biz.schema.dto.ProjectSchemaDTO;
import com.antgroup.openspgapp.biz.schema.dto.PropertyDTO;
import com.antgroup.openspgapp.biz.schema.dto.RelationTypeDTO;
import com.antgroup.openspgapp.common.util.enums.BuilderJobStatus;
import com.antgroup.openspgapp.common.util.enums.BuilderJobType;
import com.antgroup.openspgapp.core.reasoner.model.SubGraph;
import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Edge;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Node;
import com.antgroup.openspgapp.core.reasoner.service.utils.Utils;
import com.antgroup.openspgapp.server.api.facade.dto.Page;
import com.antgroup.openspgapp.server.api.facade.dto.builder.BuilderJobSubGraphRequest;
import com.antgroup.openspgapp.server.api.facade.dto.builder.WriterGraphRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RequestMapping({"/public/v1/builder/job"})
@Controller
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/builder/BuilderJobController.class */
public class BuilderJobController extends BaseController {
  private static final Logger log = LoggerFactory.getLogger(BuilderJobController.class);
  private static final String YU_QUE_REPOS = "/api/v2/repos/%s/%s/docs/%s";

  @Autowired private BuilderJobManager builderJobManager;

  @Autowired private TaskManager taskManager;

  @Autowired private AppSchemaManager appSchemaManager;

  @Autowired private DefaultValue value;

  @Autowired private ProjectManager projectManager;

  @Autowired private SchedulerService schedulerService;

  @Autowired private SchedulerTaskService schedulerTaskService;

  @Autowired private DataSourceMeta dataSourceMeta;

  @RequestMapping(
      value = {"/get"},
      method = {RequestMethod.GET})
  @ResponseBody
  public HttpResult<BuilderJob> getById(final Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<BuilderJob>() { // from class:
          // com.antgroup.openspgapp.api.http.server.builder.BuilderJobController.1
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public BuilderJob action() {
            return BuilderJobController.this.builderJobManager.queryById(id);
          }
        });
  }

  @RequestMapping(
      value = {"/list"},
      method = {RequestMethod.GET})
  @ResponseBody
  public HttpResult<Page<List<BuilderJob>>> list(
      final Long projectId,
      final String createUser,
      final String keyword,
      final Long start,
      final Long limit) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Page<List<BuilderJob>>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.builder.BuilderJobController.2
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("start", start);
            AssertUtils.assertParamObjectIsNotNull("limit", limit);
            if (null == projectId
                && StringUtils.isBlank(createUser)
                && StringUtils.isBlank(keyword)) {
              throw new IllegalParamsException(
                  "projectId and createUser and sessionId are all null", new Object[0]);
            }
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Page<List<BuilderJob>> action() {
            Page page = new Page(limit, start);
            Paged<BuilderJob> data =
                BuilderJobController.this.builderJobManager.query(
                    projectId,
                    createUser,
                    keyword,
                    Integer.valueOf(start.intValue()),
                    Integer.valueOf(limit.intValue()));
            page.setData(data.getResults());
            page.setTotal(data.getTotal());
            return page;
          }
        });
  }

  @RequestMapping(
      value = {"/submit"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<BuilderJob> submit(@RequestBody final BuilderJob job) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<BuilderJob>() { // from class:
          // com.antgroup.openspgapp.api.http.server.builder.BuilderJobController.3
          public void check() {
            Project project =
                BuilderJobController.this.projectManager.queryById(job.getProjectId());
            JSONObject projectConfig = JSONObject.parseObject(project.getConfig());
            Utils.checkVectorizer(BuilderJobController.this.value, projectConfig);
            AssertUtils.assertParamObjectIsNotNull("BuilderJob", job);
            AssertUtils.assertParamObjectIsNotNull("jobName", job.getJobName());
            if (StringUtils.isNotBlank(job.getPipeline())) {
              JSON.parseObject(job.getPipeline(), Pipeline.class);
            }
            String extension = job.getExtension();
            JSONObject extractConfig = JSON.parseObject(extension).getJSONObject("extractConfig");
            Utils.checkLLM(BuilderJobController.this.value, extractConfig);
            if (SchedulerEnum.LifeCycle.PERIOD.name().equals(job.getLifeCycle())) {
              String cron = StringUtils.isBlank(job.getCron()) ? "0 0 0 * * ?" : job.getCron();
              job.setCron(cron);
              AssertUtils.assertParamIsTrue("cron is Day Level", CommonUtils.isDayLevelCron(cron));
            }
            if ("odps".equalsIgnoreCase(job.getDataSourceType())) {
              BuilderJobController.this.checkOdps(job);
            }
            if (BuilderJobType.YUQUE_EXTRACT.name().equalsIgnoreCase(job.getType())) {
              JSONObject config = JSON.parseObject(extension).getJSONObject("yuqueConfig");
              String yuQueUrl = config.getString("yuQueUrl");
              AssertUtils.assertParamObjectIsNotNull("yuQueUrl", yuQueUrl);
              String yuQueToken = config.getString("yuQueToken");
              AssertUtils.assertParamObjectIsNotNull("yuQueToken", yuQueToken);
              String[] paths = BuilderJobController.parseURL(yuQueUrl);
              String url =
                  String.format(
                      BuilderJobController.this.value.getYuQueApiUrl()
                          + BuilderJobController.YU_QUE_REPOS,
                      paths[0],
                      paths[1],
                      paths[2]);
              BuilderJobController.getYuqueApiData(yuQueToken, url);
              job.setFileUrl(url);
            } else if (StringUtils.isNotBlank(job.getFileUrl())) {
              UriComponents uri = UriComponentsBuilder.fromUriString(job.getFileUrl()).build();
              String fileExtension = FilenameUtils.getExtension(uri.getPath()).toLowerCase();
              AssertUtils.assertParamIsTrue(
                  "file extension need .csv、.md、.txt、.json、.pdf、.doc、.docx  ",
                  "csv".equals(fileExtension)
                      || "md".equals(fileExtension)
                      || "txt".equals(fileExtension)
                      || "json".equals(fileExtension)
                      || "pdf".equals(fileExtension)
                      || "doc".equals(fileExtension)
                      || "docx".equals(fileExtension));
            }
            if (job.getId() != null) {
              BuilderJob old = BuilderJobController.this.builderJobManager.queryById(job.getId());
              AssertUtils.assertParamObjectIsNotNull("query by id", old);
              BuilderJobStatus status = BuilderJobStatus.valueOf(old.getStatus());
              AssertUtils.assertParamIsTrue(
                  "Status is FINISH and cannot be edited", !BuilderJobStatus.FINISH.equals(status));
            }
            if (StringUtils.isBlank(job.getFileUrl())) {
              job.setFileUrl(job.getJobName());
            }
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public BuilderJob action() {
            BuilderJob updateJob;
            job.setGmtModified(new Date());
            job.setStatus(BuilderJobStatus.RUNNING.name());
            job.setVersion("V3");
            if (StringUtils.isBlank(job.getPipeline())) {
              Pipeline pipeline = PipelineUtils.getKagDefaultPipeline(job);
              job.setPipeline(
                  JSON.toJSONString(
                      pipeline, new SerializerFeature[] {SerializerFeature.WriteClassName}));
            }
            if (job.getId() != null) {
              BuilderJob old = BuilderJobController.this.builderJobManager.queryById(job.getId());
              if (old.getTaskId() != null) {
                BuilderJobController.this.schedulerService.deleteJob(
                    Long.valueOf(old.getTaskId().longValue()));
              }
              Account account = BuilderJobController.this.getLoginAccount();
              String user = account != null ? account.getAccount() : "system";
              job.setModifyUser(user);
              BuilderJobController.this.builderJobManager.update(job);
              updateJob = job;
            } else {
              job.setGmtCreate(new Date());
              if (StringUtils.isBlank(job.getCreateUser())) {
                Account account2 = BuilderJobController.this.getLoginAccount();
                String user2 = account2 != null ? account2.getAccount() : "system";
                job.setCreateUser(user2);
              }
              job.setModifyUser(job.getCreateUser());
              if (StringUtils.isBlank(job.getType())) {
                job.setType(BuilderJobType.FILE_EXTRACT.name());
              }
              if (StringUtils.isBlank(job.getComputingConf())) {
                job.setComputingConf("{}");
              }
              if (StringUtils.isBlank(job.getDependence())) {
                job.setDependence(SchedulerEnum.Dependence.INDEPENDENT.name());
              }
              updateJob = BuilderJobController.this.builderJobManager.submit(job);
            }
            SchedulerJob job2 = BuilderJobController.this.createSchedulerJob(updateJob);
            BuilderJob taskJob = new BuilderJob();
            taskJob.setId(updateJob.getId());
            taskJob.setTaskId(job2.getId());
            BuilderJobController.this.builderJobManager.update(taskJob);
            return updateJob;
          }
        });
  }

  /* JADX INFO: Access modifiers changed from: private */
  public void checkOdps(BuilderJob job) {
    JSONObject extension = JSON.parseObject(job.getExtension());
    JSONObject dataSourceConfig = extension.getJSONObject("dataSourceConfig");
    DataSource dataSource =
        (DataSource) JSON.parseObject(dataSourceConfig.getString("dataSource"), DataSource.class);
    AssertUtils.assertParamObjectIsNotNull("odps dataSource", dataSource);
    String project = dataSourceConfig.getString("database");
    AssertUtils.assertParamObjectIsNotNull("odps project", project);
    String table = dataSourceConfig.getString("table");
    AssertUtils.assertParamObjectIsNotNull("odps table", table);
    String partition = dataSourceConfig.getString("partition");
    AssertUtils.assertParamObjectIsNotNull("odps partition", partition);
    AssertUtils.assertParamIsTrue("partition contain *", !partition.contains("*"));
    CloudDataSource source = CloudDataSource.toCloud(dataSource);
    String dataSourceId = project + "." + table;
    try {
      boolean isPartitionTable =
          this.dataSourceMeta.isPartitionTable(source, project, table).booleanValue();
      if (!isPartitionTable) {
        long recordCount =
            this.dataSourceMeta
                .getRecordCount(source, dataSourceId, partition, (String) null)
                .longValue();
        AssertUtils.assertParamIsTrue("odps table data size is not 0", recordCount > 0);
        return;
      }
      if (SchedulerEnum.LifeCycle.ONCE.name().equals(job.getLifeCycle())) {
        AssertUtils.assertParamIsTrue(
            "odps partition exists",
            this.dataSourceMeta
                .hasPartition(source, dataSourceId, partition, (String) null)
                .booleanValue());
        long recordCount2 =
            this.dataSourceMeta
                .getRecordCount(source, dataSourceId, partition, (String) null)
                .longValue();
        AssertUtils.assertParamIsTrue("odps partition table data size is not 0", recordCount2 > 0);
      }
      if (SchedulerEnum.LifeCycle.PERIOD.name().equals(job.getLifeCycle())) {
        AssertUtils.assertParamIsTrue(
            "period job partition must contain $", partition.contains("$"));
        Date preDate =
            CommonUtils.getPreviousValidTime(job.getCron(), DateUtils.addDays(new Date(), -1));
        String bizDate = CommonUtils.replacePartition(partition, preDate);
        boolean hasPartition =
            this.dataSourceMeta
                .hasPartition(source, dataSourceId, partition, bizDate)
                .booleanValue();
        if (!hasPartition) {
          bizDate =
              CommonUtils.replacePartition(
                  partition, CommonUtils.getPreviousValidTime(job.getCron(), preDate));
          hasPartition =
              this.dataSourceMeta
                  .hasPartition(source, dataSourceId, partition, bizDate)
                  .booleanValue();
          AssertUtils.assertParamIsTrue("period job odps previous partition exists", hasPartition);
        }
        if (hasPartition) {
          this.dataSourceMeta.getRecordCount(source, dataSourceId, partition, bizDate);
        }
      }
    } catch (Exception e) {
      String message = ExceptionUtils.getStackTrace(e);
      if (message.indexOf("Authorization Failed [4021]") > 0) {
        new RuntimeException(
            String.format(
                "No %s table download permissions. stack trace:%s", project, e.getMessage()));
        return;
      }
      if (message.indexOf("Authorization Failed [4002]") > 0) {
        new RuntimeException(
            String.format(
                "No %s project read permission. Please add the ODPS account as a project member. stack trace:%s",
                project, e.getMessage()));
        return;
      }
      if (message.indexOf("Authorization Failed [4019]") > 0) {
        new RuntimeException(
            String.format(
                "No %s table read permission. stack trace:%s", dataSourceId, e.getMessage()));
      } else if (message.indexOf("Table not found") > 0) {
        new RuntimeException(
            String.format("%s table does not exist. stack trace:%s", dataSourceId, e.getMessage()));
      } else {
        new RuntimeException(
            String.format("odps permission check exception. stack trace:%s", e.getMessage()));
      }
    }
  }

  public static String[] parseURL(String url) {
    UriComponents uri = UriComponentsBuilder.fromUriString(url).build();
    String path = uri.getPath();
    String[] pathSegments = path.split("/");
    String segment = pathSegments.length > 1 ? pathSegments[1] : "";
    String segment1 = pathSegments.length > 2 ? pathSegments[2] : "";
    String segment2 = pathSegments.length > 3 ? pathSegments[3] : "";
    return new String[] {segment, segment1, segment2};
  }

  /* JADX INFO: Access modifiers changed from: private */
  public SchedulerJob createSchedulerJob(BuilderJob taskJob) {
    SchedulerJob job = new SchedulerJob();
    job.setProjectId(taskJob.getProjectId());
    job.setName(taskJob.getJobName());
    job.setCreateUser(taskJob.getCreateUser());
    job.setModifyUser(taskJob.getModifyUser());
    job.setLifeCycle(SchedulerEnum.LifeCycle.valueOf(taskJob.getLifeCycle()));
    job.setSchedulerCron(taskJob.getCron());
    String extension = taskJob.getExtension();
    JSONObject datasourceConfig = JSON.parseObject(extension).getJSONObject("dataSourceConfig");
    Boolean structure =
        (Boolean)
            (datasourceConfig == null ? new JSONObject() : datasourceConfig)
                .getOrDefault("structure", Boolean.FALSE);
    JSONObject conf = JSON.parseObject(taskJob.getComputingConf());
    String type = (String) conf.getOrDefault("computingType", "local");
    String builderType = conf.getString("builderType");
    SchedulerEnum.TranslateType translateType = SchedulerEnum.TranslateType.KAG_BUILDER;
    if (!"local".equals(type)) {
      translateType = SchedulerEnum.TranslateType.KAG_COMMAND_BUILDER;
    } else if ("kag".equals(builderType)
        || SchedulerEnum.LifeCycle.REAL_TIME.name().equalsIgnoreCase(taskJob.getLifeCycle())) {
      translateType = SchedulerEnum.TranslateType.KAG_ENTIRETY_BUILDER;
    } else if (structure.booleanValue()) {
      translateType = SchedulerEnum.TranslateType.KAG_STRUCTURE_BUILDER;
    }
    job.setTranslateType(translateType);
    job.setStatus(SchedulerEnum.Status.ENABLE);
    job.setDependence(SchedulerEnum.Dependence.valueOf(taskJob.getDependence()));
    job.setInvokerId(taskJob.getId().toString());
    return this.schedulerService.submitJob(job);
  }

  @RequestMapping(
      value = {"/delete"},
      method = {RequestMethod.GET})
  @ResponseBody
  public HttpResult<Boolean> delete(final Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() { // from class:
          // com.antgroup.openspgapp.api.http.server.builder.BuilderJobController.4
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Boolean action() {
            BuilderJob job = BuilderJobController.this.builderJobManager.queryById(id);
            if (job == null) {
              return true;
            }
            BuilderJobStatus status = BuilderJobStatus.valueOf(job.getStatus());
            if (BuilderJobStatus.FINISH.equals(status)) {
              SubGraph graph = BuilderJobController.this.getSubGraph(job);
              BuilderJobController.this.writerGraph(
                  new WriterGraphRequest(
                      graph, RecordAlterOperationEnum.DELETE.name(), job.getProjectId()));
            }
            if ("V3".equals(job.getVersion()) && job.getTaskId() != null) {
              ObjectStorageClient objectStorageClient =
                  ObjectStorageClientDriverManager.getClient(
                      BuilderJobController.this.value.getObjectStorageUrl());
              SchedulerInstanceQuery query = new SchedulerInstanceQuery();
              query.setJobId(job.getTaskId());
              List<SchedulerInstance> instances =
                  BuilderJobController.this.schedulerService.searchInstances(query).getResults();
              for (SchedulerInstance instance : instances) {
                String key =
                    CommonUtils.getInstanceStorageFileKey(
                        instance.getProjectId(), instance.getId());
                objectStorageClient.removeDirectory(
                    BuilderJobController.this.value.getBuilderBucketName(), key);
              }
              BuilderJobController.this.schedulerService.deleteJob(job.getTaskId());
            }
            BuilderJobController.this.builderJobManager.delete(id);
            return true;
          }
        });
  }

  /* JADX INFO: Access modifiers changed from: private */
  public SubGraph getSubGraph(BuilderJob job) {
    SubGraph graph = new SubGraph();
    if ("V3".equals(job.getVersion())) {
      SchedulerTaskQuery query = new SchedulerTaskQuery();
      query.setJobId(job.getTaskId());
      List<SchedulerTask> tasks = this.schedulerTaskService.query(query).getResults();
      List<Node> resultNodes = Lists.newArrayList();
      List<Edge> resultEdges = Lists.newArrayList();
      ObjectStorageClient objectStorageClient =
          ObjectStorageClientDriverManager.getClient(this.value.getObjectStorageUrl());
      tasks.forEach(
          schedulerTask -> {
            if ("kagWriterAsyncTask".equalsIgnoreCase(schedulerTask.getType())
                && StringUtils.isNotBlank(schedulerTask.getOutput())) {
              try {
                String data =
                    objectStorageClient.getString(
                        this.value.getBuilderBucketName(), schedulerTask.getOutput());
                List<SubGraphRecord> subGraphs =
                    (List)
                        JSON.parseObject(
                            data,
                            new TypeReference<List<SubGraphRecord>>() { // from class:
                              // com.antgroup.openspgapp.api.http.server.builder.BuilderJobController.5
                            },
                            new Feature[0]);
                subGraphs.forEach(
                    subGraphRecord -> {
                      resultNodes.addAll(
                          (Collection)
                              JSON.parseObject(
                                  JSON.toJSONString(subGraphRecord.getResultNodes()),
                                  new TypeReference<List<Node>>() { // from class:
                                    // com.antgroup.openspgapp.api.http.server.builder.BuilderJobController.6
                                  },
                                  new Feature[0]));
                      resultEdges.addAll(
                          (Collection)
                              JSON.parseObject(
                                  JSON.toJSONString(subGraphRecord.getResultEdges()),
                                  new TypeReference<List<Edge>>() { // from class:
                                    // com.antgroup.openspgapp.api.http.server.builder.BuilderJobController.7
                                  },
                                  new Feature[0]));
                    });
              } catch (Exception e) {
                log.error("get subGraphs Exception id:" + job.getId(), e);
              }
            }
          });
      graph.setResultNodes(resultNodes);
      graph.setResultEdges(resultEdges);
    } else {
      Task task = this.taskManager.query(job.getTaskId());
      graph.setResultNodes(task.getResultNodes());
      graph.setResultEdges(task.getResultEdges());
    }
    return graph;
  }

  @RequestMapping(
      value = {"/delete/subgraph"},
      method = {RequestMethod.GET})
  @ResponseBody
  public HttpResult<Boolean> deleteSubgraph(final Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() { // from class:
          // com.antgroup.openspgapp.api.http.server.builder.BuilderJobController.8
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Boolean action() {
            BuilderJob job = BuilderJobController.this.builderJobManager.queryById(id);
            if (job != null) {
              SubGraph graph = BuilderJobController.this.getSubGraph(job);
              BuilderJobController.this.writerGraph(
                  new WriterGraphRequest(
                      graph, RecordAlterOperationEnum.DELETE.name(), job.getProjectId()));
              BuilderJob taskJob = new BuilderJob();
              taskJob.setId(id);
              taskJob.setStatus(BuilderJobStatus.PENDING.name());
              BuilderJobController.this.builderJobManager.update(taskJob);
              return true;
            }
            return true;
          }
        });
  }

  @RequestMapping(
      value = {"/download"},
      method = {RequestMethod.GET})
  public ResponseEntity<Resource> download(String fileUrl) {
    AssertUtils.assertParamObjectIsNotNull("fileUrl", fileUrl);
    File file = new File(fileUrl);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=" + file.getName());
    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    headers.add("Pragma", "no-cache");
    headers.add("Expires", "0");
    return ResponseEntity.ok()
        .headers(headers)
        .contentLength(file.length())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(new FileSystemResource(fileUrl));
  }

  @RequestMapping(
      value = {"/writer/subgraph"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<Object> writerSubgraph(@RequestBody final BuilderJobSubGraphRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Object>() { // from class:
          // com.antgroup.openspgapp.api.http.server.builder.BuilderJobController.9
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
          }

          public Object action() {
            BuilderJobController.this.getSchemaDiff(request);
            BuilderJobController.this.writerGraph(
                new WriterGraphRequest(
                    request.getSubGraph(),
                    RecordAlterOperationEnum.UPSERT.name(),
                    request.getJob().getProjectId()));
            BuilderJob taskJob = new BuilderJob();
            taskJob.setId(request.getJob().getId());
            taskJob.setStatus(BuilderJobStatus.FINISH.name());
            return BuilderJobController.this.builderJobManager.update(taskJob);
          }
        });
  }

  public static String getYuqueApiData(String token, String url) {
    try {
      CloseableHttpClient httpClient = HttpClients.createDefault();
      HttpGet httpGet = new HttpGet(url);
      httpGet.setHeader("X-Auth-Token", token);
      CloseableHttpResponse response = httpClient.execute(httpGet);
      Throwable th = null;
      try {
        try {
          AssertUtils.assertParamIsTrue(
              "Yuque API access failed: " + response.getStatusLine().toString(),
              response.getStatusLine().getStatusCode() == 200);
          HttpEntity entity = response.getEntity();
          if (entity != null) {
            String entityUtils = EntityUtils.toString(entity, "UTF-8");
            if (response != null) {
              if (0 != 0) {
                try {
                  response.close();
                } catch (Throwable th2) {
                  th.addSuppressed(th2);
                }
              } else {
                response.close();
              }
            }
            return entityUtils;
          }
          if (response == null) {
            return null;
          }
          if (0 != 0) {
            try {
              response.close();
              return null;
            } catch (Throwable th3) {
              th.addSuppressed(th3);
              return null;
            }
          }
          response.close();
          return null;
        } finally {
        }
      } catch (Throwable th4) {
        th = th4;
        throw th4;
      }
    } catch (Exception e) {
      throw new RuntimeException("Yuque API access Exception:" + e.getMessage(), e);
    }
  }

  @RequestMapping(
      value = {"/writerGraph"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<Boolean> writerGraph(@RequestBody final WriterGraphRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() { // from class:
          // com.antgroup.openspgapp.api.http.server.builder.BuilderJobController.10
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
            AssertUtils.assertParamObjectIsNotNull("operation", request.getOperation());
            AssertUtils.assertParamObjectIsNotNull("subGraph", request.getSubGraph());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Boolean action() {
            Neo4jSinkWriter writer =
                new Neo4jSinkWriter(
                    UUID.randomUUID().toString(), "图存储", new Neo4jSinkNodeConfig(true));
            BuilderContext context =
                new BuilderContext()
                    .setProjectId(request.getProjectId().longValue())
                    .setJobName("writer")
                    .setPythonExec(BuilderJobController.this.value.getPythonExec())
                    .setPythonPaths(BuilderJobController.this.value.getPythonPaths())
                    .setPythonEnv(BuilderJobController.this.value.getPythonEnv())
                    .setOperation(RecordAlterOperationEnum.valueOf(request.getOperation()))
                    .setEnableLeadTo(false)
                    .setProject(
                        JSON.toJSONString(
                            BuilderJobController.this.projectManager.queryById(
                                request.getProjectId())))
                    .setModelExecuteNum(BuilderJobController.this.value.getModelExecuteNum())
                    .setGraphStoreUrl(
                        BuilderJobController.this.projectManager.getGraphStoreUrl(
                            request.getProjectId()))
                    .setSearchEngineUrl(BuilderJobController.this.value.getSearchEngineUrl());
            writer.init(context);
            SubGraphRecord subGraph =
                (SubGraphRecord)
                    JSON.parseObject(
                        JSON.toJSONString(request.getSubGraph()), SubGraphRecord.class);
            writer.writeToNeo4j(subGraph);
            return true;
          }
        });
  }

  @RequestMapping(
      value = {"/schema/diff"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<ProjectSchemaDTO> schemaDiff(
      @RequestBody final BuilderJobSubGraphRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<ProjectSchemaDTO>() { // from class:
          // com.antgroup.openspgapp.api.http.server.builder.BuilderJobController.11
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public ProjectSchemaDTO action() {
            return BuilderJobController.this.getSchemaDiff(request);
          }
        });
  }

  public ProjectSchemaDTO getSchemaDiff(BuilderJobSubGraphRequest request) {
    EntityTypeDTO entity;
    EntityTypeDTO endEntity;
    ProjectSchemaDTO schemaDiff = new ProjectSchemaDTO();
    List<EntityTypeDTO> entityList = Lists.newArrayList();
    List<RelationTypeDTO> relationList = Lists.newArrayList();
    schemaDiff.setEntityTypeDTOList(entityList);
    schemaDiff.setRelationTypeDTOList(relationList);
    List<String> labels = Lists.newArrayList();
    Long projectId = request.getJob().getProjectId();
    Project project = this.projectManager.queryById(projectId);
    Map<String, String> schemaMap = this.appSchemaManager.getSchemaPropertyNameMap(projectId);
    ProjectSchemaDTO schema = this.appSchemaManager.getProjectSchemaDetail(projectId);
    Map<String, EntityTypeDTO> entityMap =
        (Map)
            schema.getEntityTypeDTOList().stream()
                .collect(
                    Collectors.toMap(
                        (v0) -> {
                          return v0.getName();
                        },
                        e -> {
                          return e;
                        }));
    SubGraph graph = getSubGraph(request.getJob());
    List<Node> nodes =
        graph.getResultNodes() == null ? Lists.newArrayList() : graph.getResultNodes();
    for (Node node : nodes) {
      String label =
          com.antgroup.openspg.builder.core.physical.utils.CommonUtils.labelPrefix(
              project.getNamespace(), node.getLabel());
      if (!labels.contains(label)) {
        labels.add(label);
        if (!schemaMap.containsKey(label)) {
          EntityTypeDTO entity2 = new EntityTypeDTO();
          entity2.setName(label);
          entity2.setNameZh(label);
          entity2.setParentName("Thing");
          entity2.setEntityCategory(SPGTypeEnum.ENTITY_TYPE.name());
          for (String key : node.getProperties().keySet()) {
            PropertyDTO propertyDTO = new PropertyDTO();
            propertyDTO.setRangeName(BasicTypeEnum.TEXT.getFlag());
            propertyDTO.setRangeNameZh("文本");
            propertyDTO.setPropertyCategoryEnum(SPGTypeEnum.BASIC_TYPE.name());
            propertyDTO.setName(key);
            propertyDTO.setNameZh(key);
            entity2.getPropertyList().add(propertyDTO);
          }
          entityList.add(entity2);
        } else {
          EntityTypeDTO entityTypeDTO = this.appSchemaManager.getEntityTypeByName(label);
          Map<String, Object> properties = node.getProperties();
          entityTypeDTO.getPropertyList().addAll(entityTypeDTO.getInheritedPropertyList());
          for (PropertyDTO property : entityTypeDTO.getPropertyList()) {
            if (properties.containsKey(property.getName())) {
              properties.remove(property.getName());
            }
          }
          if (MapUtils.isNotEmpty(properties)) {
            entityTypeDTO.setPropertyList(Lists.newArrayList());
            for (String key2 : properties.keySet()) {
              PropertyDTO propertyDTO2 = new PropertyDTO();
              propertyDTO2.setRangeName(BasicTypeEnum.TEXT.getFlag());
              propertyDTO2.setRangeNameZh("文本");
              propertyDTO2.setPropertyCategoryEnum(SPGTypeEnum.BASIC_TYPE.name());
              propertyDTO2.setName(key2);
              propertyDTO2.setNameZh(key2);
              entityTypeDTO.getPropertyList().add(propertyDTO2);
            }
            entityList.add(entityTypeDTO);
          }
        }
      }
    }
    List<Edge> edges =
        graph.getResultEdges() == null ? Lists.newArrayList() : graph.getResultEdges();
    for (Edge edge : edges) {
      String fromType =
          com.antgroup.openspg.builder.core.physical.utils.CommonUtils.labelPrefix(
              project.getNamespace(), edge.getFromType());
      String toType =
          com.antgroup.openspg.builder.core.physical.utils.CommonUtils.labelPrefix(
              project.getNamespace(), edge.getToType());
      String spo = String.format("%s_%s_%s", fromType, edge.getLabel(), toType);
      if (!labels.contains(spo)) {
        labels.add(spo);
        if (!schemaMap.containsKey(spo)) {
          RelationTypeDTO relation = new RelationTypeDTO();
          relation.setName(edge.getLabel());
          relation.setNameZh(edge.getLabel());
          relation.setRelationCategory(SPGTypeEnum.ENTITY_TYPE.name());
          for (String key3 : edge.getProperties().keySet()) {
            PropertyDTO propertyDTO3 = new PropertyDTO();
            propertyDTO3.setRangeName(BasicTypeEnum.TEXT.getFlag());
            propertyDTO3.setPropertyCategoryEnum(SPGTypeEnum.BASIC_TYPE.name());
            propertyDTO3.setName(key3);
            propertyDTO3.setNameZh(key3);
            relation.getPropertyList().add(propertyDTO3);
          }
          if (entityMap.containsKey(fromType)) {
            entity = entityMap.get(fromType);
          } else {
            entity = new EntityTypeDTO();
            entity.setName(fromType);
            entity.setNameZh(fromType);
          }
          relation.setStartEntity(entity);
          if (entityMap.containsKey(toType)) {
            endEntity = entityMap.get(toType);
          } else {
            endEntity = new EntityTypeDTO();
            endEntity.setName(toType);
            endEntity.setNameZh(toType);
          }
          relation.setEndEntity(endEntity);
          relationList.add(relation);
        } else {
          RelationTypeDTO relationTypeDTO =
              this.appSchemaManager.getRelationTypeBySpo(fromType, edge.getLabel(), toType);
          Map<String, Object> properties2 = edge.getProperties();
          for (PropertyDTO property2 : relationTypeDTO.getPropertyList()) {
            if (properties2.containsKey(property2.getName())) {
              properties2.remove(property2.getName());
            }
          }
          if (MapUtils.isNotEmpty(properties2)) {
            relationTypeDTO.setPropertyList(Lists.newArrayList());
            for (String key4 : properties2.keySet()) {
              PropertyDTO propertyDTO4 = new PropertyDTO();
              propertyDTO4.setRangeName(BasicTypeEnum.TEXT.getFlag());
              propertyDTO4.setPropertyCategoryEnum(SPGTypeEnum.BASIC_TYPE.name());
              propertyDTO4.setName(key4);
              propertyDTO4.setNameZh(key4);
              relationTypeDTO.getPropertyList().add(propertyDTO4);
            }
            relationList.add(relationTypeDTO);
          }
        }
      }
    }
    return schemaDiff;
  }

  @RequestMapping(
      value = {"/split/preview"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<List<String>> splitPreview(@RequestBody final BuilderJob job) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<List<String>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.builder.BuilderJobController.12
          public void check() {
            if (BuilderJobType.FILE_EXTRACT.name().equalsIgnoreCase(job.getType())) {
              AssertUtils.assertParamObjectIsNotNull("FileUrl", job.getFileUrl());
            }
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public List<String> action() {
            if (StringUtils.isBlank(job.getExtension()) && job.getId() != null) {
              BuilderJob dbJob = BuilderJobController.this.builderJobManager.queryById(job.getId());
              job.setExtension(dbJob.getExtension());
            }
            String url = job.getFileUrl();
            if (BuilderJobType.YUQUE_EXTRACT.name().equalsIgnoreCase(job.getType())) {
              JSONObject config = JSON.parseObject(job.getExtension()).getJSONObject("yuqueConfig");
              if (config.containsKey("yuQueUrl")) {
                String yuQueUrl = config.getString("yuQueUrl");
                String[] paths = BuilderJobController.parseURL(yuQueUrl);
                url =
                    String.format(
                        BuilderJobController.this.value.getYuQueApiUrl()
                            + BuilderJobController.YU_QUE_REPOS,
                        paths[0],
                        paths[1],
                        paths[2]);
              } else {
                String groupLogin = config.getString("groupLogin");
                String bookSlug = config.getString("bookSlug");
                String docSlug = config.getString("docSlug");
                url =
                    String.format(
                        BuilderJobController.this.value.getYuQueApiUrl()
                            + BuilderJobController.YU_QUE_REPOS,
                        groupLogin,
                        bookSlug,
                        docSlug);
                config.put("yuQueToken", config.getString("token"));
              }
              if (StringUtils.isBlank(job.getDataSourceType())) {
                job.setDataSourceType("yuque");
              }
            }
            if (StringUtils.isBlank(job.getDataSourceType())
                && BuilderJobType.FILE_EXTRACT.name().equals(job.getType())) {
              UriComponents uri = UriComponentsBuilder.fromUriString(job.getFileUrl()).build();
              String extension = FilenameUtils.getExtension(uri.getPath()).toLowerCase();
              job.setDataSourceType(extension);
            }
            job.setFileUrl(url);
            Project project =
                BuilderJobController.this.projectManager.queryById(job.getProjectId());
            List<ChunkRecord.Chunk> chunkReader =
                com.antgroup.openspg.builder.core.physical.utils.CommonUtils.readSource(
                    BuilderJobController.this.value.getPythonExec(),
                    BuilderJobController.this.value.getPythonPaths(),
                    BuilderJobController.this.value.getPythonEnv(),
                    BuilderJobController.this.value.getSchemaUrlHost(),
                    project,
                    job,
                    (Date) null);
            return BuilderJobController.this.paragraphSplit(chunkReader, project, job);
          }
        });
  }

  public List<String> paragraphSplit(
      List<ChunkRecord.Chunk> chunkReader, Project project, BuilderJob job) {
    List<String> chunks = Lists.newArrayList();
    JSONObject pyConfig = new JSONObject();
    JSONObject extension = JSON.parseObject(job.getExtension());
    PemjaConfig pemjaConfig =
        com.antgroup.openspg.builder.core.physical.utils.CommonUtils.getSplitterConfig(
            pyConfig,
            this.value.getPythonExec(),
            this.value.getPythonPaths(),
            this.value.getPythonEnv(),
            this.value.getSchemaUrlHost(),
            project,
            extension);
    for (ChunkRecord.Chunk chunk : chunkReader) {
      Map map = (Map) new ObjectMapper().convertValue(chunk, Map.class);
      List<Object> result =
          (List)
              PemjaUtils.invoke(
                  pemjaConfig, new Object[] {"SplitterABC", pyConfig.toJSONString(), map});
      List<ChunkRecord.Chunk> chunkList =
          (List)
              JSON.parseObject(
                  JSON.toJSONString(result),
                  new TypeReference<List<ChunkRecord.Chunk>>() { // from class:
                    // com.antgroup.openspgapp.api.http.server.builder.BuilderJobController.13
                  },
                  new Feature[0]);
      for (ChunkRecord.Chunk splitChunk : chunkList) {
        if (chunks.size() > 20) {
          return chunks;
        }
        chunks.add(splitChunk.getContent());
      }
    }
    return chunks;
  }
}
