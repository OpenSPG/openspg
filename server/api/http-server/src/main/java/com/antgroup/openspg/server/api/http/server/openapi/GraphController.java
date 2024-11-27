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
import com.antgroup.openspg.builder.core.reason.ReasonProcessor;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.runtime.impl.DefaultBuilderCatalog;
import com.antgroup.openspg.builder.model.pipeline.config.GraphStoreSinkNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.Neo4jSinkNodeConfig;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.RecordAlterOperationEnum;
import com.antgroup.openspg.builder.model.record.SubGraphRecord;
import com.antgroup.openspg.builder.runner.local.physical.sink.impl.GraphStoreSinkWriter;
import com.antgroup.openspg.builder.runner.local.physical.sink.impl.Neo4jSinkWriter;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ConceptList;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.server.api.facade.dto.service.request.*;
import com.antgroup.openspg.server.api.facade.dto.service.response.ExpendOneHopResponse;
import com.antgroup.openspg.server.api.facade.dto.service.response.ManipulateDataResponse;
import com.antgroup.openspg.server.api.facade.dto.service.response.PageRankScoreInstance;
import com.antgroup.openspg.server.api.facade.dto.service.response.QueryVertexResponse;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.biz.schema.ConceptManager;
import com.antgroup.openspg.server.biz.schema.SchemaManager;
import com.antgroup.openspg.server.biz.service.GraphManager;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/public/v1/graph")
public class GraphController {

  @Value("${python.exec:}")
  private String pythonExec;

  @Value("${python.paths:}")
  private String pythonPaths;

  @Value("${python.knext.path:}")
  private String pythonKnextPath;

  @Autowired private GraphManager graphManager;

  @Autowired private ProjectManager projectManager;

  @Autowired private SchemaManager schemaManager;

  @Autowired private ConceptManager conceptManager;

  @RequestMapping(method = RequestMethod.GET, value = "/allLabels")
  public ResponseEntity<Object> getAllLabels(GetAllLabelsRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<String>>() {
          @Override
          public void check() {}

          @Override
          public List<String> action() {
            return graphManager.getAllLabels(request);
          }
        });
  }

  @RequestMapping(method = RequestMethod.POST, value = "/getPageRankScores")
  public ResponseEntity<Object> getPageRankScores(@RequestBody GetPageRankScoresRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<PageRankScoreInstance>>() {
          @Override
          public void check() {}

          @Override
          public List<PageRankScoreInstance> action() {
            return graphManager.getPageRankScores(request);
          }
        });
  }

  @RequestMapping(method = RequestMethod.POST, value = "/upsertVertex")
  public ResponseEntity<Object> upsertVertex(@RequestBody UpsertVertexRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<ManipulateDataResponse>() {
          @Override
          public void check() {}

          @Override
          public ManipulateDataResponse action() {
            return graphManager.upsertVertex(request);
          }
        });
  }

  @RequestMapping(method = RequestMethod.POST, value = "/upsertEdge")
  public ResponseEntity<Object> upsertEdge(@RequestBody UpsertEdgeRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<ManipulateDataResponse>() {
          @Override
          public void check() {}

          @Override
          public ManipulateDataResponse action() {
            return graphManager.upsertEdge(request);
          }
        });
  }

  @RequestMapping(method = RequestMethod.POST, value = "/deleteVertex")
  public ResponseEntity<Object> deleteVertex(@RequestBody DeleteVertexRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<ManipulateDataResponse>() {
          @Override
          public void check() {}

          @Override
          public ManipulateDataResponse action() {
            return graphManager.deleteVertex(request);
          }
        });
  }

  @RequestMapping(method = RequestMethod.POST, value = "/deleteEdge")
  public ResponseEntity<Object> deleteEdge(@RequestBody DeleteEdgeRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<ManipulateDataResponse>() {
          @Override
          public void check() {}

          @Override
          public ManipulateDataResponse action() {
            return graphManager.deleteEdgeRequest(request);
          }
        });
  }

  @RequestMapping(value = "/writerGraph", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<Boolean> writerGraph(@RequestBody WriterGraphRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
            AssertUtils.assertParamObjectIsNotNull("operation", request.getOperation());
            AssertUtils.assertParamObjectIsNotNull("subGraph", request.getSubGraph());
          }

          @Override
          public Boolean action() {
            ProjectSchema projectSchema = schemaManager.getProjectSchema(request.getProjectId());
            boolean enableLeadTo =
                (request.getEnableLeadTo() == null) ? false : request.getEnableLeadTo();
            Map<SPGTypeIdentifier, ConceptList> conceptLists =
                getConceptLists(enableLeadTo, projectSchema);
            Neo4jSinkWriter writer =
                new Neo4jSinkWriter(
                    UUID.randomUUID().toString(), "图存储", new Neo4jSinkNodeConfig(true));
            BuilderContext context =
                new BuilderContext()
                    .setProjectId(request.getProjectId())
                    .setJobName("writer")
                    .setCatalog(new DefaultBuilderCatalog(projectSchema, conceptLists))
                    .setPythonExec(pythonExec)
                    .setPythonPaths(pythonPaths)
                    .setPythonKnextPath(pythonKnextPath)
                    .setOperation(RecordAlterOperationEnum.valueOf(request.getOperation()))
                    .setEnableLeadTo(enableLeadTo)
                    .setProject(JSON.toJSONString(projectManager.queryById(request.getProjectId())))
                    .setGraphStoreUrl(projectManager.getGraphStoreUrl(request.getProjectId()));
            writer.init(context);

            SubGraphRecord subGraph =
                JSON.parseObject(JSON.toJSONString(request.getSubGraph()), SubGraphRecord.class);
            writer.writeToNeo4j(subGraph);
            if (context.isEnableLeadTo()) {
              ReasonProcessor reasonProcessor = new ReasonProcessor();
              reasonProcessor.init(context);
              List<BaseRecord> records = Lists.newArrayList();
              records.add(subGraph);
              List<BaseRecord> reasonResults = reasonProcessor.process(records);
              if (CollectionUtils.isNotEmpty(reasonResults)) {
                GraphStoreSinkWriter sinkWriter =
                    new GraphStoreSinkWriter(
                        UUID.randomUUID().toString(), "图存储", new GraphStoreSinkNodeConfig(true));
                sinkWriter.init(context);
                sinkWriter.write(reasonResults);
              }
            }
            return true;
          }
        });
  }

  private Map<SPGTypeIdentifier, ConceptList> getConceptLists(
      boolean enableLeadTo, ProjectSchema projectSchema) {
    if (!enableLeadTo) {
      return null;
    }
    Map<SPGTypeIdentifier, ConceptList> results = new HashMap<>();

    for (BaseSPGType spgType : projectSchema.getSpgTypes()) {
      if (!spgType.isConceptType()) {
        continue;
      }
      ConceptList conceptList = conceptManager.getConceptDetail(spgType.getName(), null);
      results.put(spgType.getBaseSpgIdentifier(), conceptList);
    }
    return results;
  }

  @RequestMapping(value = "/expendOneHop", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<ExpendOneHopResponse> expendOneHop(@RequestBody ExpendOneHopRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<ExpendOneHopResponse>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
            AssertUtils.assertParamObjectIsNotNull("typeName", request.getTypeName());
            AssertUtils.assertParamObjectIsNotNull("bizId", request.getBizId());
          }

          @Override
          public ExpendOneHopResponse action() {
            return graphManager.expendOneHop(request);
          }
        });
  }

  @RequestMapping(value = "/queryVertex", method = RequestMethod.POST)
  @ResponseBody
  public HttpResult<QueryVertexResponse> queryVertex(@RequestBody QueryVertexRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<QueryVertexResponse>() {
          @Override
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
            AssertUtils.assertParamObjectIsNotNull("typeName", request.getTypeName());
            AssertUtils.assertParamObjectIsNotNull("bizId", request.getBizId());
          }

          @Override
          public QueryVertexResponse action() {
            return graphManager.queryVertex(request);
          }
        });
  }
}
