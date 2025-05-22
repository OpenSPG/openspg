package com.antgroup.openspgapp.api.http.server.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.common.constants.SpgAppConstant;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.facade.dto.common.request.DataQueryRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.DataReasonerRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.ReasonerTaskRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.TextSearchRequest;
import com.antgroup.openspg.server.api.facade.dto.service.response.ReasonerTaskResponse;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.biz.schema.SchemaManager;
import com.antgroup.openspg.server.biz.service.ReasonerManager;
import com.antgroup.openspg.server.biz.service.SearchManager;
import com.antgroup.openspg.server.common.model.data.DataRecord;
import com.antgroup.openspg.server.common.model.data.EntitySampleData;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspgapp.api.http.server.BaseController;
import com.antgroup.openspgapp.biz.reasoner.DialogManager;
import com.antgroup.openspgapp.biz.reasoner.TaskManager;
import com.antgroup.openspgapp.biz.schema.DataManager;
import com.antgroup.openspgapp.common.util.utils.EnumSelectAnnotation;
import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.task.TaskResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping({"v1/datas"})
@RestController
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/data/DatasController.class */
public class DatasController extends BaseController {
  private static final Logger log = LoggerFactory.getLogger(DatasController.class);

  @Autowired private DataManager dataManager;

  @Autowired private SearchManager searchManager;

  @Autowired private DialogManager dialogManager;

  @Autowired private TaskManager taskManager;

  @Autowired private SchemaManager schemaManager;

  @Autowired private ReasonerManager reasonerManager;

  @Autowired private ProjectManager projectManager;
  private Map<String, Class<?>> annotatedEnumClasses = Maps.newConcurrentMap();

  public DatasController() {
    ClassPathScanningCandidateComponentProvider scanner =
        new ClassPathScanningCandidateComponentProvider(false);
    scanner.addIncludeFilter(new AnnotationTypeFilter(EnumSelectAnnotation.class));
    List<String> packagesToScan = Arrays.asList("com.antgroup.openspgapp", "com.antgroup.openspg");
    for (String basePackage : packagesToScan) {
      Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
      for (BeanDefinition bd : candidateComponents) {
        try {
          Class<?> enumClass = Class.forName(bd.getBeanClassName());
          if (enumClass.isEnum()) {
            this.annotatedEnumClasses.put(enumClass.getSimpleName(), enumClass);
          }
        } catch (ClassNotFoundException e) {
          log.warn(bd.getBeanClassName() + " NotFound", e);
        }
      }
    }
  }

  @GetMapping({"/search"})
  @ResponseBody
  public HttpResult<Paged<DataRecord>> search(
      final Long projectId,
      final String queryStr,
      final String label,
      final Integer page,
      final Integer size) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Paged<DataRecord>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.data.DatasController.1
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("projectId", projectId);
            AssertUtils.assertParamStringIsNotBlank("label", label);
            AssertUtils.assertParamStringIsNotBlank("queryStr", queryStr);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Paged<DataRecord> action() {
            TextSearchRequest request = new TextSearchRequest();
            request.setProjectId(projectId);
            Set set = new HashSet();
            if ("all".equalsIgnoreCase(label)) {
              ProjectSchema projectSchema =
                  DatasController.this.schemaManager.getProjectSchema(projectId);
              projectSchema.getSpgTypes().stream()
                  .filter(
                      baseSPGType -> {
                        return (SPGTypeEnum.BASIC_TYPE.equals(baseSPGType.getSpgTypeEnum())
                                || SPGTypeEnum.STANDARD_TYPE.equals(baseSPGType.getSpgTypeEnum()))
                            ? false
                            : true;
                      })
                  .forEach(
                      baseSPGType2 -> {
                        set.add(baseSPGType2.getName());
                      });
            } else {
              set.add(label);
            }
            request.setLabelConstraints(set);
            request.setQueryString(queryStr);
            request.setPage(null == page ? SpgAppConstant.DEFAULT_PAGE : page);
            request.setTopk(null == size ? SpgAppConstant.DEFAULT_PAGE_SIZE : size);
            List<IdxRecord> records = DatasController.this.searchManager.textSearch(request);
            Paged paged = new Paged();
            paged.setTotal(Long.valueOf(records.size()));
            paged.setResults(
                (List)
                    records.stream()
                        .map(
                            idx -> {
                              return Utils.toRecord(idx);
                            })
                        .collect(Collectors.toList()));
            return paged;
          }
        });
  }

  @PostMapping({"/asyncSubmit"})
  @ResponseBody
  public HttpResult<TaskResponse> asyncSubmit(@RequestBody final DataQueryRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<TaskResponse>() { // from class:
          // com.antgroup.openspgapp.api.http.server.data.DatasController.2
          public void check() {
            AssertUtils.assertParamStringIsNotBlank("type", request.getType());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public TaskResponse action() {
            Task task;
            task = new Task();
            switch (request.getType()) {
              case "DSL":
                task =
                    DatasController.this.taskManager.submit(
                        request.getSessionId(),
                        10000L,
                        request.getInstruction(),
                        "",
                        request.getParams());
                break;
              case "NL":
                JSONArray message = new JSONArray();
                task =
                    DatasController.this.dialogManager.submit(
                        request.getSessionId(),
                        request.getInstruction(),
                        request.getInstruction(),
                        message.toJSONString(),
                        request.getParams());
                break;
            }
            return com.antgroup.openspgapp.api.http.server.reasoner.Utils.convert(task);
          }
        });
  }

  @GetMapping({"/query/{id}"})
  @ResponseBody
  public HttpResult<TaskResponse> query(@PathVariable final Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<TaskResponse>() { // from class:
          // com.antgroup.openspgapp.api.http.server.data.DatasController.3
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public TaskResponse action() {
            Task task = DatasController.this.taskManager.query(id);
            return com.antgroup.openspgapp.api.http.server.reasoner.Utils.convert(task);
          }
        });
  }

  @PostMapping({"/getEntityDetail"})
  @ResponseBody
  public HttpResult<TaskResponse> getEntityDetail(@RequestBody final DataReasonerRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<TaskResponse>() { // from class:
          // com.antgroup.openspgapp.api.http.server.data.DatasController.4
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
            AssertUtils.assertParamObjectIsNotNull("label", request.getLabel());
            AssertUtils.assertParamIsTrue("param", !request.getParams().isEmpty());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public TaskResponse action() {
            String dsl = "match (n:`" + request.getLabel() + "`) %s return n";
            String param = DatasController.this.packParam(request.getParams());
            String dsl2 = String.format(dsl, param);
            ReasonerTaskRequest dtr = Utils.toReasonerTask(request);
            dtr.setDsl(dsl2);
            ReasonerTaskResponse response = DatasController.this.reasonerManager.reason(dtr);
            DatasController.log.info("reason reason dsl: {}", dsl2);
            TaskResponse res = Utils.npmRow2TaskResponse(response);
            Project project = DatasController.this.projectManager.queryById(request.getProjectId());
            String chunkDsl =
                "match (n:`"
                    + request.getLabel()
                    + "`) -[p:rdf_expand('relation')]-> (m:`"
                    + project.getNamespace()
                    + ".Chunk`) %s return n, p, m, n.id";
            DatasController.log.info("reason getOneDegreeRelation dsl: {}", chunkDsl);
            ReasonerTaskResponse chunkRes =
                DatasController.this.getOneDegreeRelation(request, param, chunkDsl);
            TaskResponse chunk = Utils.npmRow2TaskResponse(chunkRes);
            Utils.addChunkRes(res, chunk);
            return res;
          }
        });
  }

  @PostMapping({"/getOneHopGraph"})
  @ResponseBody
  public HttpResult<TaskResponse> getOneHopGraph(@RequestBody final DataReasonerRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<TaskResponse>() { // from class:
          // com.antgroup.openspgapp.api.http.server.data.DatasController.5
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
            AssertUtils.assertParamObjectIsNotNull("label", request.getLabel());
            AssertUtils.assertParamIsTrue("param", !request.getParams().isEmpty());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public TaskResponse action() {
            String param = DatasController.this.packParam(request.getParams());
            String dsl =
                "match (n:`"
                    + request.getLabel()
                    + "`) -[p:rdf_expand('relation')]- (m:Entity) %s return n,p,m,n.id";
            ReasonerTaskResponse response =
                DatasController.this.getOneDegreeRelation(request, param, dsl);
            TaskResponse res = Utils.npmRow2TaskResponse(response);
            return res;
          }
        });
  }

  /* JADX INFO: Access modifiers changed from: private */
  public ReasonerTaskResponse getOneDegreeRelation(
      DataReasonerRequest request, String param, String dsl) {
    String dsl2 = String.format(dsl, param);
    ReasonerTaskRequest dtr = Utils.toReasonerTask(request);
    dtr.setDsl(dsl2);
    ReasonerTaskResponse response = this.reasonerManager.reason(dtr);
    log.info("getOneDegreeRelation dsl response: {}", JSONObject.toJSONString(response));
    Utils.npmRow2TaskResponse(response);
    return response;
  }

  /* JADX INFO: Access modifiers changed from: private */
  public String packParam(Map<String, String> params) {
    if (params.isEmpty()) {
      return "";
    }
    StringBuilder bui = new StringBuilder("where ");
    int i = 0;
    for (Map.Entry<String, String> param : params.entrySet()) {
      if (i > 0) {
        bui.append(" and ");
      }
      bui.append("n." + param.getKey() + "='" + param.getValue() + "'");
      i++;
    }
    return bui.toString();
  }

  @GetMapping({"/getSampleData"})
  public HttpResult<List<EntitySampleData>> getSampleData(
      final Long projectId, final String name, final Integer limit) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<List<EntitySampleData>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.data.DatasController.6
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("projectId", projectId);
            AssertUtils.assertParamObjectIsNotNull("name", name);
            AssertUtils.assertParamObjectIsNotNull("limit", limit);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public List<EntitySampleData> action() {
            return DatasController.this.dataManager.getTypeSampleData(projectId, name, limit);
          }
        });
  }

  @GetMapping({"/getEnumValues/{name}"})
  public HttpResult<List<Map<String, String>>> getEnumValues(@PathVariable final String name) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<List<Map<String, String>>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.data.DatasController.7
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("name", name);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public List<Map<String, String>> action() {
            Class<?> enumClass = (Class) DatasController.this.annotatedEnumClasses.get(name);
            if (enumClass == null) {
              return Lists.newArrayList();
            }
            Object[] enumConstants = enumClass.getEnumConstants();
            List<Map<String, String>> enumValues =
                (List)
                    Arrays.stream(enumConstants)
                        .map(
                            e -> {
                              Enum<?> enumConst = (Enum) e;
                              Map<String, String> valueMap = new HashMap<>(2);
                              valueMap.put("name", enumConst.name());
                              try {
                                Method textMethod =
                                    enumClass.getDeclaredMethod("getText", new Class[0]);
                                textMethod.setAccessible(true);
                                String text = (String) textMethod.invoke(enumConst, new Object[0]);
                                valueMap.put("text", text);
                              } catch (IllegalAccessException
                                  | NoSuchMethodException
                                  | InvocationTargetException ex) {
                                valueMap.put("text", enumConst.name());
                                DatasController.log.warn(
                                    enumConst.name() + " getText Exception", ex);
                              }
                              return valueMap;
                            })
                        .collect(Collectors.toList());
            return enumValues;
          }
        });
  }

  @GetMapping({"/getLlmSelect"})
  public HttpResult<JSONArray> getLlmSelect(final Long projectId) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<JSONArray>() { // from class:
          // com.antgroup.openspgapp.api.http.server.data.DatasController.8
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("projectId", projectId);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public JSONArray action() {
            Project project = DatasController.this.projectManager.queryById(projectId);
            JSONObject oldConfigJson = JSONObject.parseObject(project.getConfig());
            JSONArray llmSelect = oldConfigJson.getJSONArray("llm_select");
            return llmSelect;
          }
        });
  }
}
