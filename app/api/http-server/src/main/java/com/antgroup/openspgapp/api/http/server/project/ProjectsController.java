package com.antgroup.openspgapp.api.http.server.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.enums.PermissionEnum;
import com.antgroup.openspg.common.util.enums.ResourceTagEnum;
import com.antgroup.openspg.common.util.exception.SpgException;
import com.antgroup.openspg.common.util.exception.message.SpgMessageEnum;
import com.antgroup.openspg.reasoner.udf.utils.DateUtils;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.facade.dto.common.request.PermissionRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectCreateRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.SchemaAlterRequest;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.api.http.server.openapi.SchemaController;
import com.antgroup.openspg.server.biz.common.ConfigManager;
import com.antgroup.openspg.server.biz.common.PermissionManager;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.common.model.account.Account;
import com.antgroup.openspg.server.common.model.config.Config;
import com.antgroup.openspg.server.common.model.exception.IllegalParamsException;
import com.antgroup.openspg.server.common.model.permission.Permission;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspgapp.api.http.server.BaseController;
import com.antgroup.openspgapp.common.model.project.dto.ProjectDTO;
import com.antgroup.openspgapp.common.service.project.AppProjectManager;
import com.antgroup.openspgapp.core.reasoner.service.utils.Utils;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping({"v1/projects"})
@RestController
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/project/ProjectsController.class */
public class ProjectsController extends BaseController {

  @Autowired private SchemaController schemaController;

  @Autowired private AppProjectManager appProjectManager;

  @Autowired private ProjectManager projectManager;

  @Autowired private DefaultValue defaultValue;

  @Autowired private PermissionManager permissionManager;

  @Autowired private ConfigManager configManager;

  @GetMapping({"/list"})
  public HttpResult<Paged<ProjectDTO>> getProjectList(
      final boolean all,
      final Long tenantId,
      final String keyword,
      final Integer page,
      final Integer size,
      final String sortBy,
      final String sort) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Paged<ProjectDTO>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.project.ProjectsController.1
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("page", page);
            AssertUtils.assertParamObjectIsNotNull("size", size);
            AssertUtils.assertParamIsTrue("page > 0", page.intValue() > 0);
            AssertUtils.assertParamIsTrue("size > 0", size.intValue() > 0);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Paged<ProjectDTO> action() {
            List<Long> projectIdList;
            Account loginAccount = ProjectsController.this.getLoginAccount();
            if (ProjectsController.this.permissionManager.isSuper(loginAccount.getWorkNo())) {
              projectIdList = null;
            } else {
              projectIdList =
                  (List)
                      ProjectsController.this.permissionManager
                          .getPermissionByUserNoAndResourceTag(
                              loginAccount.getWorkNo(), ResourceTagEnum.PROJECT.name())
                          .stream()
                          .map(
                              (v0) -> {
                                return v0.getResourceId();
                              })
                          .collect(Collectors.toList());
              if (CollectionUtils.isEmpty(projectIdList)) {
                return new Paged<>();
              }
            }
            Paged<ProjectDTO> projectDTOPaged =
                ProjectsController.this.appProjectManager.getProjectList(
                    Boolean.valueOf(all),
                    tenantId,
                    keyword,
                    projectIdList,
                    page,
                    size,
                    sort,
                    sortBy);
            if (projectDTOPaged.getTotal().longValue() != 0
                && CollectionUtils.isNotEmpty(projectDTOPaged.getResults())) {
              projectDTOPaged
                  .getResults()
                  .forEach(
                      projectDTO -> {
                        JSONObject configJson = JSON.parseObject(projectDTO.getConfig());
                        if (configJson != null) {
                          ProjectsController.this.configManager.backwardCompatible(configJson);
                          projectDTO.setConfig(
                              ProjectsController.this.configManager.setApiKeyDesensitization(
                                  JSON.toJSONString(configJson)));
                        }
                      });
            }
            return projectDTOPaged;
          }
        });
  }

  @GetMapping({"/{projectId}"})
  public HttpResult<Project> getProjectInfo(@PathVariable final Long projectId) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Project>() { // from class:
          // com.antgroup.openspgapp.api.http.server.project.ProjectsController.2
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("projectId", projectId);
            Project project = ProjectsController.this.projectManager.queryById(projectId);
            if (project == null) {
              throw new SpgException(SpgMessageEnum.PROJECT_NOT_EXIST);
            }
            String userNo = ProjectsController.this.getLoginAccount().getWorkNo();
            boolean isSuper = ProjectsController.this.permissionManager.isSuper(userNo);
            boolean isProjectRole =
                ProjectsController.this.permissionManager.isProjectRole(userNo, projectId);
            if (!isSuper && !isProjectRole) {
              List<String> userNameList =
                  ProjectsController.this.permissionManager.getOwnerUserNameByProjectId(projectId);
              throw new SpgException(
                  SpgMessageEnum.PROJECT_MEMBER_NOT_EXIST.getCode(),
                  StringUtils.join(new List[] {userNameList}));
            }
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Project action() {
            Project project = ProjectsController.this.projectManager.queryById(projectId);
            if (project != null) {
              JSONObject configJson = JSON.parseObject(project.getConfig());
              if (configJson != null) {
                ProjectsController.this.configManager.backwardCompatible(configJson);
                project =
                    new Project(
                        project.getId(),
                        project.getName(),
                        project.getDescription(),
                        project.getNamespace(),
                        project.getTenantId(),
                        ProjectsController.this.configManager.setApiKeyDesensitization(
                            JSON.toJSONString(configJson)));
              } else {
                return project;
              }
            }
            return project;
          }
        });
  }

  @PostMapping
  @ResponseBody
  public HttpResult<Project> create(@RequestBody final ProjectCreateRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Project>() { // from class:
          // com.antgroup.openspgapp.api.http.server.project.ProjectsController.3
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("Project", request);
            AssertUtils.assertParamObjectIsNotNull("name", request.getName());
            AssertUtils.assertParamObjectIsNotNull("namespace", request.getNamespace());
            AssertUtils.assertParamIsTrue(
                "namespace length >= 3", request.getNamespace().length() >= 3);
            JSONObject config = JSONObject.parseObject(request.getConfig());
            Config kagConfig = ProjectsController.this.configManager.query("KAG_CONFIG", "1");
            ProjectsController.this.configManager.handleApiKey(config, kagConfig.getConfig());
            ProjectsController.this.configManager.backwardCompatible(config);
            ProjectsController.this.configManager.generateLLMIdCompletionLLM(config);
            if (!config.containsKey("llm")) {
              ProjectsController.this.setLLM(config, kagConfig.getConfig());
            }
            Utils.checkLLM(ProjectsController.this.defaultValue, config);
            String dimensions = Utils.checkVectorizer(ProjectsController.this.defaultValue, config);
            request.setConfig(
                JSONObject.toJSONString(ProjectsController.this.setDimensions(config, dimensions)));
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Project action() {
            Project project = ProjectsController.this.projectManager.create(request);
            if (project != null && project.getId() != null) {
              PermissionRequest permissionRequest = new PermissionRequest();
              permissionRequest.setResourceIds(Lists.newArrayList(new Long[] {project.getId()}));
              permissionRequest.setResourceTag(ResourceTagEnum.PROJECT.name());
              permissionRequest.setUserNos(
                  Lists.newArrayList(
                      new String[] {ProjectsController.this.getLoginAccount().getWorkNo()}));
              permissionRequest.setRoleType(PermissionEnum.OWNER.name());
              ProjectsController.this.permissionManager.create(permissionRequest);
            }
            if (request.getAutoSchema() == null || Boolean.TRUE.equals(request.getAutoSchema())) {
              SchemaAlterRequest request2 = new SchemaAlterRequest();
              request2.setProjectId(project.getId());
              request2.setSchemaDraft(
                  SchemaController.getDefaultSchemaDraft(project.getNamespace()));
              ProjectsController.this.schemaController.alterSchema(request2);
            }
            return project;
          }
        });
  }

  /* JADX INFO: Access modifiers changed from: private */
  public void setLLM(JSONObject config, String oldConfig) {
    JSONObject oldConfigJson = JSONObject.parseObject(oldConfig);
    JSONArray llmSelect = oldConfigJson.getJSONArray("llm_select");
    if (CollectionUtils.isEmpty(llmSelect)) {
      return;
    }
    for (int i = 0; i < llmSelect.size(); i++) {
      JSONObject llm = llmSelect.getJSONObject(i);
      if (llm != null && llm.getBooleanValue("default")) {
        config.put("llm", JSON.parseObject(llm.toJSONString()));
        return;
      }
    }
  }

  @PutMapping({"/{id}"})
  @ResponseBody
  public HttpResult<Project> update(
      @PathVariable Long id, @RequestBody final ProjectCreateRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Project>() { // from class:
          // com.antgroup.openspgapp.api.http.server.project.ProjectsController.4
          public void check() {
            JSONObject oldLLM;
            AssertUtils.assertParamObjectIsNotNull("Project", request);
            AssertUtils.assertParamObjectIsNotNull("id", request.getId());
            Project project = ProjectsController.this.projectManager.queryById(request.getId());
            AssertUtils.assertParamObjectIsNotNull("query project by id", project);
            AssertUtils.assertParamObjectIsNotNull("namespace", request.getNamespace());
            AssertUtils.assertParamIsTrue(
                "namespace are immutable",
                project.getNamespace().equalsIgnoreCase(request.getNamespace()));
            AssertUtils.assertParamObjectIsNotNull("name", request.getName());
            AssertUtils.assertParamIsTrue(
                "namespace length >= 3", request.getNamespace().length() >= 3);
            ProjectsController.this.assertIsSuperOrIsOwner(request.getId());
            JSONObject oldConfig = JSONObject.parseObject(project.getConfig());
            boolean unifiedLLMId = false;
            if (oldConfig != null
                && (oldLLM = oldConfig.getJSONObject("llm")) != null
                && StringUtils.isBlank(oldLLM.getString("llm_id"))) {
              unifiedLLMId = true;
            }
            ProjectsController.this.configManager.backwardCompatible(oldConfig);
            ProjectsController.this.configManager.generateLLMIdCompletionLLM(oldConfig);
            String oldLlmId = ProjectsController.this.configManager.getLLMIdByConfig(oldConfig);
            JSONObject config = JSONObject.parseObject(request.getConfig());
            Account account = ProjectsController.this.getLoginAccount();
            ProjectsController.this.configManager.handleApiKey(config, project.getConfig());
            ProjectsController.this.configManager.backwardCompatible(config);
            ProjectsController.this.configManager.generateLLMIdCompletionLLM(config);
            String llmId = ProjectsController.this.configManager.getLLMIdByConfig(config);
            if (unifiedLLMId) {
              oldLlmId = llmId;
            }
            if (!StringUtils.equals(oldLlmId, llmId)
                || ProjectsController.this.configManager.isLLMChange(oldConfig, config)) {
              Utils.checkLLM(ProjectsController.this.defaultValue, config);
            }
            ProjectsController.this.compatibleLLMSelector(config, account);
            String dimensions = Utils.checkVectorizer(ProjectsController.this.defaultValue, config);
            if (StringUtils.isNotBlank(project.getConfig())) {
              String oldDimensions =
                  JSONObject.parseObject(project.getConfig())
                      .getJSONObject("vectorizer")
                      .getString("vector_dimensions");
              if (StringUtils.isNotBlank(oldDimensions)) {
                AssertUtils.assertParamIsTrue(
                    String.format(
                        "vector_dimensions are immutable dimensions:%s old dimensions:%s",
                        dimensions, oldDimensions),
                    dimensions.equalsIgnoreCase(oldDimensions));
              }
            }
            request.setConfig(
                JSONObject.toJSONString(ProjectsController.this.setDimensions(config, dimensions)));
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Project action() {
            Project project = ProjectsController.this.projectManager.update(request);
            return project;
          }
        });
  }

  /* JADX INFO: Access modifiers changed from: private */
  public void compatibleLLMSelector(JSONObject config, Account account) {
    if (null == config) {
      return;
    }
    JSONArray llmArray = config.getJSONArray("llm_select");
    if (CollectionUtils.isEmpty(llmArray)) {
      return;
    }
    for (int i = 0; i < llmArray.size(); i++) {
      JSONObject llm = llmArray.getJSONObject(i);
      if (llm != null) {
        if (!llm.containsKey("createTime")) {
          llm.put("createTime", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        if (!llm.containsKey("creator")) {
          llm.put("creator", account.getRealName());
        }
      }
    }
    config.put("llm_select", llmArray);
  }

  /* JADX INFO: Access modifiers changed from: private */
  public JSONObject setDimensions(JSONObject config, String dimensions) {
    if (config.containsKey("vectorizer")) {
      config.getJSONObject("vectorizer").put("vector_dimensions", dimensions);
    } else {
      JSONObject vectorizer = new JSONObject();
      vectorizer.put("vector_dimensions", dimensions);
      config.put("vectorizer", vectorizer);
    }
    return config;
  }

  @DeleteMapping({"/{projectId}"})
  public HttpResult<Boolean> delete(@PathVariable final Long projectId) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() { // from class:
          // com.antgroup.openspgapp.api.http.server.project.ProjectsController.5
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("projectId", projectId);
            ProjectsController.this.assertIsSuperOrIsOwner(projectId);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Boolean action() {
            Integer data = ProjectsController.this.projectManager.deleteById(projectId);
            return Boolean.valueOf(data != null && data.intValue() > 0);
          }
        });
  }

  public void assertIsSuperOrIsOwner(Long resourceId) {
    String userNo = getLoginAccount().getWorkNo();
    List<Permission> ownerPermissions =
        this.permissionManager.getPermissionByUserRolesAndId(
            Lists.newArrayList(new Long[] {resourceId}),
            userNo,
            PermissionEnum.OWNER.name(),
            ResourceTagEnum.PROJECT.name());
    boolean isSuper = this.permissionManager.isSuper(userNo);
    boolean isOwner = CollectionUtils.isNotEmpty(ownerPermissions);
    if (!isSuper && !isOwner) {
      throw new IllegalParamsException("permission denied", new Object[0]);
    }
  }
}
