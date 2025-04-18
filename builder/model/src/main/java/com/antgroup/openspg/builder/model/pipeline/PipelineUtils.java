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
package com.antgroup.openspg.builder.model.pipeline;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.builder.model.pipeline.config.ExtractPostProcessorNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.LLMNlExtractNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.Neo4jSinkNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.OperatorConfig;
import com.antgroup.openspg.builder.model.pipeline.config.ParagraphSplitNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.StringSourceNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.config.predicting.VectorizerProcessorNodeConfig;
import com.antgroup.openspg.common.constants.BuilderConstant;
import com.antgroup.openspg.common.util.pemja.PythonInvokeMethod;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.UUID;

public class PipelineUtils {

  public static Pipeline getKagDefaultPipeline(BuilderJob job) {
    List<Node> nodes = Lists.newArrayList();
    List<Edge> edges = Lists.newArrayList();
    String sourceId = UUID.randomUUID().toString();
    String fileUrl = job.getFileUrl();
    JSONObject extension = JSON.parseObject(job.getExtension());
    StringSourceNodeConfig sourceConfig = new StringSourceNodeConfig(fileUrl);
    Node source = new Node(sourceId, "Reader", sourceConfig);
    nodes.add(source);

    String splitId = UUID.randomUUID().toString();
    PythonInvokeMethod splitMethod = PythonInvokeMethod.BRIDGE_COMPONENT;
    JSONObject config = extension.getJSONObject(BuilderConstant.YU_QUE_CONFIG);
    String token = (config == null) ? null : config.getString(BuilderConstant.YU_QUE_TOKEN);
    OperatorConfig operatorConfigSplit = new OperatorConfig(splitMethod, Maps.newHashMap());
    Node split =
        new Node(
            splitId, "Splitter", new ParagraphSplitNodeConfig(operatorConfigSplit, token, job));
    nodes.add(split);
    edges.add(new Edge(sourceId, splitId));

    String extractId = UUID.randomUUID().toString();
    PythonInvokeMethod extractMethod = PythonInvokeMethod.BRIDGE_COMPONENT;
    OperatorConfig operatorConfig = new OperatorConfig(extractMethod, Maps.newHashMap());
    Node extract = new Node(extractId, "Extractor", new LLMNlExtractNodeConfig(operatorConfig));
    nodes.add(extract);
    edges.add(new Edge(splitId, extractId));

    String vectorizerId = UUID.randomUUID().toString();
    PythonInvokeMethod vectorizerMethod = PythonInvokeMethod.BRIDGE_COMPONENT;
    OperatorConfig operatorConfigVectorizer =
        new OperatorConfig(vectorizerMethod, Maps.newHashMap());
    Node vectorizerProcessor =
        new Node(
            vectorizerId,
            "Vectorizer",
            new VectorizerProcessorNodeConfig(operatorConfigVectorizer));
    nodes.add(vectorizerProcessor);
    edges.add(new Edge(extractId, vectorizerId));

    String alignmentId = UUID.randomUUID().toString();
    PythonInvokeMethod alignmentMethod = PythonInvokeMethod.BRIDGE_COMPONENT;
    OperatorConfig operatorConfigAlignment = new OperatorConfig(alignmentMethod, Maps.newHashMap());
    Node alignmentProcessor =
        new Node(
            alignmentId, "Alignment", new ExtractPostProcessorNodeConfig(operatorConfigAlignment));
    nodes.add(alignmentProcessor);
    edges.add(new Edge(vectorizerId, alignmentId));

    String sinkId = UUID.randomUUID().toString();
    JSONObject extractConfig = extension.getJSONObject(BuilderConstant.EXTRACT_CONFIG);
    Boolean autoWrite =
        (extractConfig == null) ? true : extractConfig.getBoolean(BuilderConstant.AUTO_WRITE);
    Node sink = new Node(sinkId, "Writer", new Neo4jSinkNodeConfig(autoWrite));
    nodes.add(sink);
    edges.add(new Edge(alignmentId, sinkId));

    Pipeline pipeline = new Pipeline(nodes, edges);
    return pipeline;
  }
}
