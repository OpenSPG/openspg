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

package com.antgroup.openspg.builder.runner.local;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.runtime.impl.DefaultBuilderCatalog;
import com.antgroup.openspg.builder.model.BuilderConstants;
import com.antgroup.openspg.builder.model.exception.PipelineConfigException;
import com.antgroup.openspg.builder.model.pipeline.Pipeline;
import com.antgroup.openspg.builder.model.record.RecordAlterOperationEnum;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.schema.model.SchemaException;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ConceptList;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.server.api.facade.ApiResponse;
import com.antgroup.openspg.server.api.facade.client.ConceptFacade;
import com.antgroup.openspg.server.api.facade.client.SchemaFacade;
import com.antgroup.openspg.server.api.facade.dto.schema.request.ConceptRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.request.ProjectSchemaRequest;
import com.antgroup.openspg.server.api.http.client.HttpConceptFacade;
import com.antgroup.openspg.server.api.http.client.HttpSchemaFacade;
import com.antgroup.openspg.server.api.http.client.util.ConnectionInfo;
import com.antgroup.openspg.server.api.http.client.util.HttpClientBootstrap;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.slf4j.LoggerFactory;

@Slf4j
public class LocalBuilderMain {

  public static void main(String[] args) {
    CommandLine commandLine = parseArgs(args);
    try {
      run(commandLine);
    } catch (Throwable e) {
      log.error("unknown exception.", e);
      System.exit(1);
    }
  }

  public static CommandLine parseArgs(String[] args) {
    CommandLineParser parser = new DefaultParser();
    Options options = new Options();

    options.addRequiredOption(
        BuilderConstants.PROJECT_ID_OPTION, BuilderConstants.PROJECT_ID_OPTION, true, "project id");
    options.addRequiredOption(
        BuilderConstants.JOB_NAME_OPTION, BuilderConstants.JOB_NAME_OPTION, true, "job name");
    options.addRequiredOption(
        BuilderConstants.PIPELINE_OPTION, BuilderConstants.PIPELINE_OPTION, true, "pipeline info");
    options.addRequiredOption(
        BuilderConstants.PYTHON_EXEC_OPTION,
        BuilderConstants.PYTHON_EXEC_OPTION,
        true,
        "python exec");
    options.addRequiredOption(
        BuilderConstants.PYTHON_PATHS_OPTION,
        BuilderConstants.PYTHON_PATHS_OPTION,
        true,
        "python path");
    options.addRequiredOption(
        BuilderConstants.SCHEMA_URL_OPTION, BuilderConstants.SCHEMA_URL_OPTION, true, "schema url");
    options.addOption(
        BuilderConstants.PARALLELISM_OPTION,
        BuilderConstants.PARALLELISM_OPTION,
        true,
        "parallelism");
    options.addOption(
        BuilderConstants.ALTER_OPERATION_OPTION,
        BuilderConstants.ALTER_OPERATION_OPTION,
        true,
        "alter operation, upsert or delete");
    options.addOption(
        BuilderConstants.LOG_FILE_OPTION, BuilderConstants.LOG_FILE_OPTION, true, "log file");
    options.addOption(
        BuilderConstants.LEAD_TO_OPTION, BuilderConstants.LEAD_TO_OPTION, false, "enable leadTo");
    options.addRequiredOption(
        BuilderConstants.GRAPH_STORE_URL_OPTION,
        BuilderConstants.GRAPH_STORE_URL_OPTION,
        true,
        "graph store url");
    options.addRequiredOption(
        BuilderConstants.SEARCH_ENGINE_URL_OPTION,
        BuilderConstants.SEARCH_ENGINE_URL_OPTION,
        true,
        "search engine url");
    options.addRequiredOption(
        BuilderConstants.PROJECT_OPTION, BuilderConstants.PROJECT_OPTION, true, "project");
    options.addOption(
        BuilderConstants.MODEL_EXECUTE_NUM_OPTION,
        BuilderConstants.MODEL_EXECUTE_NUM_OPTION,
        true,
        "model execute num");

    CommandLine commandLine = null;
    HelpFormatter helper = new HelpFormatter();
    try {
      commandLine = parser.parse(options, args);
    } catch (ParseException e) {
      helper.printHelp("Usage: ", options);
      System.exit(0);
    }
    return commandLine;
  }

  private static void run(CommandLine commandLine) throws Exception {
    ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    String logFileName = commandLine.getOptionValue(BuilderConstants.LOG_FILE_OPTION);
    setUpLogFile(logFileName);

    long projectId = Long.parseLong(commandLine.getOptionValue(BuilderConstants.PROJECT_ID_OPTION));
    String jobName = commandLine.getOptionValue(BuilderConstants.JOB_NAME_OPTION);

    String pipelineStr = commandLine.getOptionValue(BuilderConstants.PIPELINE_OPTION);
    Pipeline pipeline = JSONObject.parseObject(pipelineStr, Pipeline.class);

    String pythonExec = commandLine.getOptionValue(BuilderConstants.PYTHON_EXEC_OPTION);
    String pythonPaths = commandLine.getOptionValue(BuilderConstants.PYTHON_PATHS_OPTION);
    String schemaUrl = commandLine.getOptionValue(BuilderConstants.SCHEMA_URL_OPTION);

    String parallelismStr = commandLine.getOptionValue(BuilderConstants.PARALLELISM_OPTION);
    int parallelism = (parallelismStr == null ? 1 : Integer.parseInt(parallelismStr));

    String modelExecuteNumStr =
        commandLine.getOptionValue(BuilderConstants.MODEL_EXECUTE_NUM_OPTION);
    Integer modelExecuteNum =
        (modelExecuteNumStr == null ? 5 : Integer.parseInt(modelExecuteNumStr));

    String alterOperation = commandLine.getOptionValue(BuilderConstants.ALTER_OPERATION_OPTION);
    RecordAlterOperationEnum alterOperationEnum = RecordAlterOperationEnum.valueOf(alterOperation);

    boolean enableLeadTo = commandLine.hasOption(BuilderConstants.LEAD_TO_OPTION);

    String graphStoreUrl = commandLine.getOptionValue(BuilderConstants.GRAPH_STORE_URL_OPTION);
    String searchEngineUrl = commandLine.getOptionValue(BuilderConstants.SEARCH_ENGINE_URL_OPTION);

    String project = commandLine.getOptionValue(BuilderConstants.PROJECT_OPTION);

    ProjectSchema projectSchema = getProjectSchema(projectId, schemaUrl);
    Map<SPGTypeIdentifier, ConceptList> conceptLists = getConceptLists(enableLeadTo, projectSchema);
    BuilderContext builderContext =
        new BuilderContext()
            .setProjectId(projectId)
            .setJobName(jobName)
            .setCatalog(new DefaultBuilderCatalog(projectSchema, conceptLists))
            .setPythonExec(pythonExec)
            .setPythonPaths(pythonPaths)
            .setOperation(alterOperationEnum)
            .setEnableLeadTo(enableLeadTo)
            .setGraphStoreUrl(graphStoreUrl)
            .setSearchEngineUrl(searchEngineUrl)
            .setProject(project)
            .setModelExecuteNum(modelExecuteNum)
            .setSchemaUrl(schemaUrl);

    LocalBuilderRunner runner = new LocalBuilderRunner(parallelism);
    runner.init(pipeline, builderContext);

    try {
      runner.execute();
    } catch (Exception e) {
      throw new RuntimeException("runner execute exception ", e);
    } finally {
      runner.close();
    }
    System.exit(0);
  }

  private static ProjectSchema getProjectSchema(long projectId, String schemaUrl) {
    HttpClientBootstrap.init(
        new ConnectionInfo(schemaUrl).setConnectTimeout(6000).setReadTimeout(600000));

    SchemaFacade schemaFacade = new HttpSchemaFacade();
    ApiResponse<ProjectSchema> response =
        schemaFacade.queryProjectSchema(new ProjectSchemaRequest(projectId));
    if (response.isSuccess()) {
      return response.getData();
    }
    throw new PipelineConfigException(
        "get schema error={}, schemaUrl={}, projectId={}",
        response.getErrorMsg(),
        schemaUrl,
        projectId);
  }

  private static Map<SPGTypeIdentifier, ConceptList> getConceptLists(
      boolean enableLeadTo, ProjectSchema projectSchema) {
    if (!enableLeadTo) {
      return null;
    }

    Map<SPGTypeIdentifier, ConceptList> results = new HashMap<>();

    ConceptFacade conceptFacade = new HttpConceptFacade();
    for (BaseSPGType spgType : projectSchema.getSpgTypes()) {
      if (!spgType.isConceptType()) {
        continue;
      }
      ApiResponse<ConceptList> response =
          conceptFacade.queryConcept(new ConceptRequest().setConceptTypeName(spgType.getName()));
      if (response.isSuccess()) {
        results.put(spgType.getBaseSpgIdentifier(), response.getData());
      } else {
        throw new SchemaException("get schema error");
      }
    }
    return results;
  }

  private static void setUpLogFile(String logFileName) {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    loggerContext.reset();

    PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder();
    patternLayoutEncoder.setPattern("%d [%X{traceId}] [%X{rpcId}] [%t] %-5p %c{2} - %m%n");
    patternLayoutEncoder.setContext(loggerContext);
    patternLayoutEncoder.start();

    FileAppender<ILoggingEvent> fileAppender = null;
    if (StringUtils.isNotBlank(logFileName)) {
      fileAppender = new FileAppender<>();
      fileAppender.setFile(logFileName);
      fileAppender.setEncoder(patternLayoutEncoder);
      fileAppender.setContext(loggerContext);
      fileAppender.setAppend(false);
      fileAppender.start();
    }

    ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
    consoleAppender.setEncoder(patternLayoutEncoder);
    consoleAppender.setContext(loggerContext);
    consoleAppender.start();

    Logger brpcLogger = loggerContext.getLogger("com.baidu.brpc");
    brpcLogger.setLevel(Level.ERROR);
    brpcLogger.setAdditive(false);
    if (fileAppender != null) {
      brpcLogger.addAppender(fileAppender);
    }
    brpcLogger.addAppender(consoleAppender);

    Logger dtflysLogger = loggerContext.getLogger("com.dtflys.forest");
    dtflysLogger.setLevel(Level.ERROR);
    dtflysLogger.setAdditive(false);
    if (fileAppender != null) {
      dtflysLogger.addAppender(fileAppender);
    }
    dtflysLogger.addAppender(consoleAppender);

    Logger rootLogger = loggerContext.getLogger("root");
    if (fileAppender != null) {
      rootLogger.addAppender(fileAppender);
    }
    rootLogger.addAppender(consoleAppender);
    rootLogger.setLevel(Level.INFO);
  }
}
