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

package com.antgroup.openspg.reasoner.runner.local.thinker;

import com.alibaba.fastjson.JSON;
import com.antgroup.kg.reasoner.thinker.Thinker;
import com.antgroup.kg.reasoner.thinker.catalog.LogicCatalog;
import com.antgroup.kg.reasoner.thinker.engine.DefaultThinker;
import com.antgroup.kg.reasoner.thinker.logic.Result;
import com.antgroup.kg.reasoner.thinker.logic.graph.*;
import com.antgroup.openspg.reasoner.catalog.impl.KgSchemaConnectionInfo;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.graphstate.GraphState;
import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import com.antgroup.openspg.reasoner.runner.local.LogUtil;
import com.antgroup.openspg.reasoner.runner.local.ParamsKey;
import com.antgroup.openspg.reasoner.runner.local.load.graph.AbstractLocalGraphLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class LocalThinkerMain {
  public static void main(String[] args) {
    doMain(args);
    System.exit(0);
  }

  public static void doMain(String[] args) {
    ThinkerParams task = parseArgs(args);
    if (null == task) {
      System.exit(1);
    }
    GraphState<IVertexId> graphState = loadGraph(task);
    LogicCatalog logicCatalog = new OpenSPGLogicCatalog(task.getProjectId(), task.getConnInfo());
    logicCatalog.init();
    Thinker thinker = new DefaultThinker(graphState, logicCatalog);
    List<Result> result;
    if (task.getMode().toLowerCase().equals("spo")) {
      result =
          thinker.find(
              task.getTriple().getSubject(),
              task.getTriple().getPredicate(),
              task.getTriple().getObject(),
              task.getParams());
    } else {
      result = thinker.find((Node) task.getTriple().getSubject(), task.getParams());
    }
    if (CollectionUtils.isEmpty(result)) {
      log.error("local runner return null");
      return;
    }
    log.info("result:\n {}", result);
  }

  private static ThinkerParams parseArgs(String[] args) {
    Options options = getThinkerOptions();

    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    CommandLine cmd;

    long projectId;
    String schemaUri;
    String graphStateClass;
    String graphLoaderClass;
    String graphStateUrl;
    Element s = Element.ANY;
    Element p = Element.ANY;
    Element o = Element.ANY;
    String mode = "spo";
    Map<String, Object> params = new HashMap<>(3);
    try {
      cmd = parser.parse(options, args);

      String logFileName = cmd.getOptionValue(ParamsKey.LOG_FILE_OPTION);
      LogUtil.setUpLogFile(logFileName);

      projectId = Long.parseLong(cmd.getOptionValue(ParamsKey.PROJECT_ID_OPTION));

      schemaUri = cmd.getOptionValue(ParamsKey.SCHEMA_URL_OPTION);
      if (StringUtils.isEmpty(schemaUri)) {
        throw new ParseException("please provide openspg schema uri!");
      }
      graphLoaderClass = cmd.getOptionValue(ParamsKey.GRAPH_LOADER_CLASS_OPTION);
      graphStateClass = cmd.getOptionValue(ParamsKey.GRAPH_STATE_CLASS_OPTION);
      graphStateUrl = cmd.getOptionValue(ParamsKey.GRAPH_STORE_URL_OPTION);
      if (StringUtils.isEmpty(graphStateUrl)) {
        graphStateUrl = null;
      }
      String paramsJson = cmd.getOptionValue(ParamsKey.PARAMs_OPTION);
      if (StringUtils.isNotEmpty(paramsJson)) {
        params = new HashMap<>(JSON.parseObject(paramsJson));
      }
      String subject = cmd.getOptionValue(ParamsKey.SUBJECT);
      if (StringUtils.isNotBlank(subject)) {
        s = strToElement(subject, false);
      }
      String predicate = cmd.getOptionValue(ParamsKey.PREDICATE);
      if (StringUtils.isNotBlank(predicate)) {
        p = strToElement(predicate, true);
      }
      String object = cmd.getOptionValue(ParamsKey.OBJECT);
      if (StringUtils.isNotBlank(object)) {
        o = strToElement(object, false);
      }
      if (s == Element.ANY && p == Element.ANY && o == Element.ANY) {
        throw new RuntimeException(
            "subject, predicate, object cannot all be empty at the same time.");
      }
      String m = cmd.getOptionValue(ParamsKey.MODE);
      if (StringUtils.isNotBlank(m)) {
        mode = m;
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      formatter.printHelp("ThinkerLocalMain", options);
      return null;
    }

    ThinkerParams task = new ThinkerParams();
    task.setTriple(new Triple(s, p, o));
    task.setConnInfo(new KgSchemaConnectionInfo(schemaUri, ""));
    task.setGraphLoadClass(graphLoaderClass);
    task.setGraphStateClassName(graphStateClass);
    task.setGraphStateInitString(graphStateUrl);
    task.setProjectId(projectId);
    task.setParams(params);
    task.setMode(mode);
    return task;
  }

  private static Element strToElement(String content, Boolean isPredicate) {
    String[] parts = StringUtils.split(content, ",");
    if (parts.length == 2) {
      return new Entity(parts[0], parts[1]);
    } else if (parts.length == 1) {
      if (isPredicate) {
        return new Predicate(parts[0]);
      } else {
        return new Node(parts[0]);
      }
    } else {
      throw new RuntimeException("format error, require id,type or type");
    }
  }

  private static Options getThinkerOptions() {
    Options options = new Options();
    options.addRequiredOption(
        ParamsKey.PROJECT_ID_OPTION, ParamsKey.PROJECT_ID_OPTION, true, "project id");
    options.addRequiredOption(
        ParamsKey.SCHEMA_URL_OPTION, ParamsKey.SCHEMA_URL_OPTION, true, "schema url");
    options.addOption(
        ParamsKey.SUBJECT, ParamsKey.SUBJECT, true, "query subject, eg: id,type or type");
    options.addOption(ParamsKey.PREDICATE, ParamsKey.PREDICATE, true, "query predicate, eg: type");
    options.addOption(
        ParamsKey.OBJECT, ParamsKey.OBJECT, true, "query object, eg: id,type or type");
    options.addOption(ParamsKey.OUTPUT_OPTION, ParamsKey.OUTPUT_OPTION, true, "output file name");
    options.addOption(
        ParamsKey.GRAPH_STATE_CLASS_OPTION,
        ParamsKey.GRAPH_STATE_CLASS_OPTION,
        true,
        "graph state class name");
    options.addOption(
        ParamsKey.GRAPH_LOADER_CLASS_OPTION,
        ParamsKey.GRAPH_LOADER_CLASS_OPTION,
        true,
        "graph loader class name");
    options.addOption(
        ParamsKey.GRAPH_STORE_URL_OPTION,
        ParamsKey.GRAPH_STORE_URL_OPTION,
        true,
        "graph store url");
    options.addOption(ParamsKey.PARAMs_OPTION, ParamsKey.PARAMs_OPTION, true, "params");
    options.addOption(ParamsKey.LOG_FILE_OPTION, ParamsKey.LOG_FILE_OPTION, true, "log file name");
    options.addOption(ParamsKey.MODE, ParamsKey.MODE, true, "infer mode, eg: spo or node");
    return options;
  }

  protected static GraphState<IVertexId> loadGraph(ThinkerParams params) {
    GraphState<IVertexId> graphState;
    String graphStateClass = params.getGraphStateClassName();
    if (StringUtils.isNotEmpty(graphStateClass)) {
      try {
        graphState =
            (GraphState<IVertexId>)
                Class.forName(graphStateClass)
                    .getConstructor(String.class)
                    .newInstance(params.getGraphStateInitString());
      } catch (Exception e) {
        throw new RuntimeException("can not create graph state from " + graphStateClass, e);
      }
      return graphState;
    }

    MemGraphState memGraphState = new MemGraphState();
    String graphLoadClass = params.getGraphLoadClass();
    if (StringUtils.isBlank(graphLoadClass)) {
      return memGraphState;
    }
    AbstractLocalGraphLoader graphLoader;
    try {
      graphLoader =
          (AbstractLocalGraphLoader) Class.forName(graphLoadClass).getConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("can not create graph loader from name " + graphLoadClass, e);
    }
    graphLoader.setGraphState(memGraphState);
    graphLoader.load();
    return memGraphState;
  }
}
