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

package com.antgroup.openspg.server.biz.common.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.cloudext.impl.graphstore.neo4j.Neo4jConstants;
import com.antgroup.openspg.common.constants.BuilderConstant;
import com.antgroup.openspg.common.constants.SpgAppConstant;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.constants.CommonConstant;
import com.antgroup.openspg.common.util.enums.PermissionEnum;
import com.antgroup.openspg.common.util.enums.ProjectTagEnum;
import com.antgroup.openspg.common.util.enums.ResourceTagEnum;
import com.antgroup.openspg.common.util.enums.VisibilityEnum;
import com.antgroup.openspg.common.util.neo4j.Neo4jAdminUtils;
import com.antgroup.openspg.common.util.pemja.PemjaUtils;
import com.antgroup.openspg.common.util.pemja.PythonInvokeMethod;
import com.antgroup.openspg.common.util.pemja.model.PemjaConfig;
import com.antgroup.openspg.core.schema.model.SPGSchema;
import com.antgroup.openspg.core.schema.model.SPGSchemaAlterCmd;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.server.api.facade.dto.common.request.PermissionRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectCreateRequest;
import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectQueryRequest;
import com.antgroup.openspg.server.biz.common.PermissionManager;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.biz.common.RefManager;
import com.antgroup.openspg.server.biz.common.UserModelManager;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.common.model.CommonConstants;
import com.antgroup.openspg.server.common.model.exception.IllegalParamsException;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.common.model.ref.RefInfo;
import com.antgroup.openspg.server.common.model.ref.RefTypeEnum;
import com.antgroup.openspg.server.common.model.ref.RefedTypeEnum;
import com.antgroup.openspg.server.common.model.usermodel.UserModelDTO;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspg.server.common.service.project.ProjectRepository;
import com.antgroup.openspg.server.common.service.project.ProjectService;
import com.antgroup.openspg.server.core.schema.service.alter.sync.BaseSchemaSyncer;
import com.antgroup.openspg.server.core.schema.service.alter.sync.SchemaStorageEnum;
import com.antgroup.openspg.server.core.schema.service.alter.sync.SchemaSyncerFactory;
import com.antgroup.openspg.server.core.schema.service.type.SPGTypeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class ProjectManagerImpl implements ProjectManager {

  @Autowired private ProjectRepository projectRepository;
  @Autowired private ProjectService projectService;
  @Autowired private SchemaSyncerFactory schemaSyncerFactory;
  @Autowired private SPGTypeService spgTypeService;
  @Autowired private UserModelManager userModelManager;
  @Autowired private PermissionManager permissionManager;
  @Autowired private RefManager refManager;
  @Autowired private DefaultValue defaultValue;

  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  @Override
  public Project create(ProjectCreateRequest request) {
    if (!ProjectTagEnum.PUBLIC_NET.name().equalsIgnoreCase(request.getTag())) {
      setLocalVectorizer(request);
    }
    JSONObject config = request.getConfig();
    if (!ProjectTagEnum.PUBLIC_NET.name().equalsIgnoreCase(request.getTag())) {
      config = setDatabase(config, request.getNamespace());
      setGraphStore(request.getNamespace(), config, true);
      config.remove(SpgAppConstant.MCP_SERVERS);
    } else {
      config.remove(SpgAppConstant.VECTORIZER);
      config.remove(SpgAppConstant.GRAPH_STORE);
    }
    Project project =
        new Project(
            null,
            request.getName(),
            request.getDescription(),
            request.getNamespace(),
            request.getTenantId(),
            config.toJSONString(),
            StringUtils.isBlank(request.getTag()) ? ProjectTagEnum.LOCAL.name() : request.getTag());
    if (StringUtils.isNotBlank(request.getVisibility())) {
      VisibilityEnum visibilityEnum = VisibilityEnum.getVisibilityEnum(request.getVisibility());
      project.setVisibility(visibilityEnum.name());
    }
    Long projectId = projectRepository.save(project);
    project.setId(projectId);
    PermissionRequest permissionRequest = new PermissionRequest();
    permissionRequest.setResourceIds(Lists.newArrayList(project.getId()));
    permissionRequest.setResourceTag(ResourceTagEnum.KNOWLEDGE_BASE.name());
    permissionRequest.setUserNos(Lists.newArrayList(request.getUserNo()));
    permissionRequest.setRoleType(PermissionEnum.OWNER.name());
    permissionManager.create(permissionRequest);
    if (ProjectTagEnum.LOCAL.name().equalsIgnoreCase(request.getTag())) {
      createRefInfo(project);
    }
    return project;
  }

  private void createRefInfo(Project project) {
    JSONObject jsonObject = JSON.parseObject(project.getConfig());
    String embeddingModeId = getEmbeddingModelId(jsonObject);
    RefInfo refInfo =
        new RefInfo(
            "PROJECT_EMBEDDING",
            String.valueOf(project.getId()),
            RefTypeEnum.KNOWLEDGE_BASE.name(),
            embeddingModeId,
            RefedTypeEnum.EMBEDDING.name(),
            1);
    JSONObject config = new JSONObject();
    refInfo.setConfig(config.toJSONString());
    Long id = refManager.create(refInfo);
    log.info(
        "create project embedding ref info, refId: {}, refInfo: {}",
        id,
        JSON.toJSONString(refInfo));
  }

  @Override
  public Project update(ProjectCreateRequest request) {
    Project project = projectRepository.queryById(request.getId());
    if (StringUtils.isBlank(request.getTag())) {
      request.setTag(project.getTag());
    }
    if (!ProjectTagEnum.PUBLIC_NET.name().equalsIgnoreCase(request.getTag())
        && !StringUtils.equals(request.getTag(), project.getTag())) {
      setLocalVectorizer(request);
    }
    String database = "";
    if (!ProjectTagEnum.PUBLIC_NET.name().equalsIgnoreCase(request.getTag())) {
      JSONObject oldConfig = JSON.parseObject(project.getConfig());
      if (null != oldConfig && null != oldConfig.getJSONObject(SpgAppConstant.GRAPH_STORE)) {
        database =
            oldConfig
                .getJSONObject(CommonConstants.GRAPH_STORE)
                .getString(CommonConstants.DATABASE);
      }
      JSONObject config = request.getConfig();
      if (StringUtils.equals(project.getTag(), request.getTag())) {
        JSONObject vectorConfig = oldConfig.getJSONObject(SpgAppConstant.VECTORIZER);
        config.put(SpgAppConstant.VECTORIZER, vectorConfig);
      }
      // graphStore password special treatment
      graphStoreDeserialization(config, oldConfig);
      request.setConfig(config);
    }
    if (StringUtils.isBlank(request.getName())) {
      request.setName(project.getName());
    }
    JSONObject config = request.getConfig();
    if (!ProjectTagEnum.PUBLIC_NET.name().equalsIgnoreCase(request.getTag())) {
      config = setDatabase(request.getConfig(), database);
      setGraphStore(database, config, true);
      config = setVectorDimensions(config, project);
      config.remove(SpgAppConstant.MCP_SERVERS);
    } else {
      config.remove(SpgAppConstant.VECTORIZER);
      config.remove(SpgAppConstant.GRAPH_STORE);
    }
    Project update =
        new Project(
            request.getId(),
            request.getName(),
            request.getDescription(),
            null,
            null,
            config.toJSONString(),
            request.getTag());
    if (StringUtils.isNotBlank(request.getVisibility())) {
      VisibilityEnum visibilityEnum = VisibilityEnum.getVisibilityEnum(request.getVisibility());
      update.setVisibility(visibilityEnum.name());
    }
    update = projectRepository.update(update);
    long start = System.currentTimeMillis();
    log.info("createSchema cost {} ms", System.currentTimeMillis() - start);
    return update;
  }

  private static void graphStoreDeserialization(JSONObject config, JSONObject oldConfig) {
    JSONObject graphStore = config.getJSONObject(SpgAppConstant.GRAPH_STORE);
    if (null == graphStore) {
      return;
    }
    String password = graphStore.getString(SpgAppConstant.PASSWORD);
    if (com.antgroup.openspg.common.util.StringUtils.equals(
        password, SpgAppConstant.DEFAULT_VECTORIZER_API_KEY)) {
      String oldPassword =
          oldConfig.getJSONObject(SpgAppConstant.GRAPH_STORE).getString(SpgAppConstant.PASSWORD);
      graphStore.put(SpgAppConstant.PASSWORD, oldPassword);
      config.put(SpgAppConstant.GRAPH_STORE, graphStore);
    }
  }

  private JSONObject setDatabase(JSONObject config, String namespace) {
    if (config == null) {
      config = new JSONObject();
    }
    if (config.containsKey(CommonConstants.GRAPH_STORE)) {
      JSONObject graphStore = config.getJSONObject(CommonConstants.GRAPH_STORE);
      String uri = graphStore.getString("uri");
      if (StringUtils.isNotBlank(uri) && !uri.contains(CommonConstant.KGFABRIC_GRAPH_STORE)) {
        namespace = namespace.toLowerCase();
      }
      config.getJSONObject(CommonConstants.GRAPH_STORE).put(CommonConstants.DATABASE, namespace);
    } else {
      JSONObject graphStore = new JSONObject();
      graphStore.put(CommonConstants.DATABASE, namespace.toLowerCase());
      config.put(CommonConstants.GRAPH_STORE, graphStore);
    }
    return config;
  }

  private JSONObject setVectorDimensions(JSONObject config, Project project) {
    JSONObject oldConfig = JSON.parseObject(project.getConfig());
    String vectorDimensions = null;
    if (oldConfig.containsKey(CommonConstants.VECTORIZER)) {
      vectorDimensions =
          oldConfig
              .getJSONObject(CommonConstants.VECTORIZER)
              .getString(CommonConstants.VECTOR_DIMENSIONS);
    }
    if (StringUtils.isBlank(vectorDimensions)) {
      return config;
    }
    if (config.containsKey(CommonConstants.VECTORIZER)) {
      config
          .getJSONObject(CommonConstants.VECTORIZER)
          .put(CommonConstants.VECTOR_DIMENSIONS, vectorDimensions);
    } else {
      JSONObject graphStore = new JSONObject();
      graphStore.put(CommonConstants.VECTOR_DIMENSIONS, vectorDimensions);
      config.put(CommonConstants.VECTORIZER, graphStore);
    }
    return config;
  }

  @Override
  public Project queryById(Long projectId) {
    return projectRepository.queryById(projectId);
  }

  @Override
  public Integer deleteById(Long projectId) {
    Project project = projectRepository.queryById(projectId);
    if (project == null) {
      return 0;
    }
    try {
      deleteDatabase(project);
    } catch (Exception e) {
      log.error("delete project database Exception:" + project, e);
    }

    return projectRepository.deleteById(projectId);
  }

  public void deleteDatabase(Project project) {
    JSONObject config = JSON.parseObject(project.getConfig());
    UriComponents uriComponents =
        UriComponentsBuilder.fromUriString(defaultValue.getGraphStoreUrl()).build();
    String database = uriComponents.getQueryParams().getFirst(Neo4jConstants.DATABASE);
    String host =
        String.format(
            "%s://%s:%s",
            uriComponents.getScheme(), uriComponents.getHost(), uriComponents.getPort());
    String user = uriComponents.getQueryParams().getFirst(Neo4jConstants.USER);
    String password = uriComponents.getQueryParams().getFirst(Neo4jConstants.PASSWORD);
    JSONObject graphStore = config.getJSONObject(CommonConstants.GRAPH_STORE);
    if (graphStore.containsKey(Neo4jConstants.URI)) {
      host = graphStore.getString(Neo4jConstants.URI);
    }
    if (graphStore.containsKey(Neo4jConstants.USER)) {
      user = graphStore.getString(Neo4jConstants.USER);
    }
    if (graphStore.containsKey(Neo4jConstants.PASSWORD)) {
      password = graphStore.getString(Neo4jConstants.PASSWORD);
    }
    String dropDatabase = project.getNamespace().toLowerCase();
    Neo4jAdminUtils driver = new Neo4jAdminUtils(host, user, password, database);
    driver.neo4jGraph.dropDatabase(dropDatabase);
  }

  public void setGraphStore(String namespace, JSONObject config, boolean createDatabase) {
    UriComponents uriComponents =
        UriComponentsBuilder.fromUriString(defaultValue.getGraphStoreUrl()).build();
    String database = uriComponents.getQueryParams().getFirst(Neo4jConstants.DATABASE);
    String host =
        String.format(
            "%s://%s:%s",
            uriComponents.getScheme(), uriComponents.getHost(), uriComponents.getPort());
    String user = uriComponents.getQueryParams().getFirst(Neo4jConstants.USER);
    String password = uriComponents.getQueryParams().getFirst(Neo4jConstants.PASSWORD);

    JSONObject graphStore = config.getJSONObject(CommonConstants.GRAPH_STORE);
    if (graphStore.containsKey(Neo4jConstants.URI)) {
      host = graphStore.getString(Neo4jConstants.URI);
      uriComponents = UriComponentsBuilder.fromUriString(host).build();
    } else {
      graphStore.put(Neo4jConstants.URI, host);
    }
    if (graphStore.containsKey(Neo4jConstants.USER)) {
      user = graphStore.getString(Neo4jConstants.USER);
    } else {
      graphStore.put(Neo4jConstants.USER, user);
    }
    if (graphStore.containsKey(Neo4jConstants.PASSWORD)) {
      password = graphStore.getString(Neo4jConstants.PASSWORD);
    } else {
      graphStore.put(Neo4jConstants.PASSWORD, password);
    }
    // TODO: 这里先跳过，kgfabric driver schema/索引同步打通后放开 @秉初 底层支持索引灵活变更的接口5月底给出，排期530开发
    if (!CommonConstant.KGFABRIC_GRAPH_STORE.equalsIgnoreCase(uriComponents.getScheme())
        && createDatabase) {
      Neo4jAdminUtils driver = new Neo4jAdminUtils(host, user, password, database);
      String projectDatabase = namespace.toLowerCase();
      driver.neo4jGraph.createDatabase(projectDatabase);
    }
  }

  public void createSchema(Long projectId) {
    try {
      BaseSchemaSyncer schemaSyncer = schemaSyncerFactory.getSchemaSyncer(SchemaStorageEnum.GRAPH);
      if (schemaSyncer != null) {
        Set<SPGTypeIdentifier> spreadStdTypeNames = spgTypeService.querySpreadStdTypeName();
        List<BaseSPGType> spgTypes = spgTypeService.queryProjectSchema(projectId).getSpgTypes();
        SPGSchemaAlterCmd schemaEditCmd =
            new SPGSchemaAlterCmd(new SPGSchema(spgTypes, spreadStdTypeNames));
        schemaSyncer.syncSchema(projectId, schemaEditCmd);
      }
    } catch (Exception e) {
      log.error("createSchema Exception:" + projectId, e);
    }
  }

  @Override
  public List<Project> query(ProjectQueryRequest request) {
    return projectRepository.query(request);
  }

  @Override
  public List<Project> queryPageData(ProjectQueryRequest request, int start, int size) {
    return projectRepository.queryPageData(request, start, size);
  }

  @Override
  public Long queryPageCount(ProjectQueryRequest request) {
    return projectRepository.queryPageCount(request);
  }

  @Override
  public String getGraphStoreUrl(Long projectId) {
    return projectService.getGraphStoreUrl(projectId);
  }

  @Override
  public String getSearchEngineUrl(Long projectId) {
    // For Neo4j, GraphStore and SearchEngine are the same.
    return getGraphStoreUrl(projectId);
  }

  @Override
  public Project queryByNamespace(String namespace) {
    return projectRepository.queryByNamespace(namespace);
  }

  @Override
  public void completionVectorizer(JSONObject projectConfig) {
    JSONObject vectorizer = projectConfig.getJSONObject(CommonConstants.VECTORIZER);
    if (vectorizer == null) {
      return;
    }
    String modelId = vectorizer.getString(SpgAppConstant.MODEL_ID);
    JSONObject llmInfo = userModelManager.getByModelId(modelId);
    if (llmInfo != null) {
      for (Map.Entry<String, Object> entry : llmInfo.entrySet()) {
        String key = entry.getKey();
        Object value = entry.getValue();
        vectorizer.put(key, value);
      }
      projectConfig.put(BuilderConstant.VECTORIZER, vectorizer);
    }
  }

  private JSONObject setDimensions(JSONObject config, String dimensions) {
    if (config.containsKey(CommonConstants.VECTORIZER)) {
      config
          .getJSONObject(CommonConstants.VECTORIZER)
          .put(CommonConstants.VECTOR_DIMENSIONS, dimensions);
    } else {
      JSONObject vectorizer = new JSONObject();
      vectorizer.put(CommonConstants.VECTOR_DIMENSIONS, dimensions);
      config.put(CommonConstants.VECTORIZER, vectorizer);
    }
    return config;
  }

  private void setLocalVectorizer(ProjectCreateRequest request) {
    Boolean isKnext = request.getIsKnext();
    if (isKnext != null && isKnext) {
      setLocalVectorizerKnext(request);
    } else {
      setLocalVectorizerPlatform(request);
    }
  }

  private void setLocalVectorizerPlatform(ProjectCreateRequest request) {
    JSONObject jsonObject = request.getConfig();
    String modelId = getEmbeddingModelId(jsonObject);
    AssertUtils.assertParamStringIsNotBlank("vectorizer", modelId);
    JSONObject userModelConfig = userModelManager.getByModelId(modelId);
    // Construct a new parameter object
    JSONObject param = new JSONObject();
    JSONObject vector = new JSONObject();
    vector.putAll(jsonObject.getJSONObject(SpgAppConstant.VECTORIZER));
    param.put(SpgAppConstant.VECTORIZER, vector);
    JSONObject vectorConfig = param.getJSONObject(CommonConstants.VECTORIZER);
    vectorConfig.putAll(userModelConfig);
    String dimensions = checkVectorizer(param);
    // Set the dimensions to the original object
    jsonObject = setDimensions(jsonObject, dimensions);
    request.setConfig(jsonObject);
  }

  private void setLocalVectorizerKnext(ProjectCreateRequest request) {
    JSONObject jsonObject = request.getConfig();
    JSONObject vectorizer = jsonObject.getJSONObject(SpgAppConstant.VECTORIZER);
    AssertUtils.assertParamObjectIsNotNull("tag is LOCAL, config.vectorizer", vectorizer);
    String dimensions = checkVectorizer(jsonObject);
    // Construct a new parameter object
    UserModelDTO userModel = new UserModelDTO();
    userModel.setProvider("OpenAI");
    userModel.setName(request.getNamespace());
    userModel.setVisibility(VisibilityEnum.PRIVATE.name());
    userModel.setUserNo(request.getUserNo());
    JSONObject modelConfig = new JSONObject();
    modelConfig.putAll(vectorizer);
    userModel.setConfig(modelConfig);
    Map<String, Object> modelTypeMap = userModelManager.getModelTypeMap();
    AssertUtils.assertParamObjectIsNotNull("modelTypeMap", modelTypeMap);
    JSONObject model =
        userModelManager.getModelByProviderAndModel(
            userModel.getProvider(),
            userModel.getName(),
            userModel.getVisibility(),
            vectorizer.getString(SpgAppConstant.MODEL));
    if (model == null) {
      Long num = userModelManager.insert(userModel, modelTypeMap);
      if (num < 1) {
        throw new IllegalParamsException("model insert failed");
      }
    }
    JSONObject modelInfo =
        userModelManager.getModelByProviderAndModel(
            userModel.getProvider(),
            userModel.getName(),
            userModel.getVisibility(),
            vectorizer.getString(SpgAppConstant.MODEL));
    AssertUtils.assertParamObjectIsNotNull("modelInfo", modelInfo);
    JSONObject newVectorizer = new JSONObject();
    newVectorizer.put(SpgAppConstant.MODEL_ID, modelInfo.getString(SpgAppConstant.MODEL_ID));
    // Set the dimensions to the original object
    jsonObject.put(SpgAppConstant.VECTORIZER, newVectorizer);
    jsonObject = setDimensions(jsonObject, dimensions);
    request.setConfig(jsonObject);
  }

  private String getEmbeddingModelId(JSONObject config) {
    JSONObject vectorConfig = config.getJSONObject(SpgAppConstant.VECTORIZER);
    if (vectorConfig != null) {
      return vectorConfig.getString("modelId");
    }
    return null;
  }

  private String checkVectorizer(JSONObject config) {
    if (config == null || !config.containsKey(CommonConstants.VECTORIZER)) {
      return "";
    }
    JSONObject vectorizerConfig = config.getJSONObject(CommonConstants.VECTORIZER);
    PemjaConfig pemjaConfig =
        new PemjaConfig(
            defaultValue.getPythonExec(),
            defaultValue.getPythonPaths(),
            defaultValue.getPythonEnv(),
            defaultValue.getSchemaUrlHost(),
            null,
            PythonInvokeMethod.BRIDGE_VECTORIZER_CHECKER,
            Maps.newHashMap());
    Object result = PemjaUtils.invoke(pemjaConfig, JSON.toJSONString(vectorizerConfig));
    return result.toString();
  }
}
