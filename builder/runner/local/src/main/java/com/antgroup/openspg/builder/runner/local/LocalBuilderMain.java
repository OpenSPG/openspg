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
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.core.runtime.impl.DefaultBuilderCatalog;
import com.antgroup.openspg.builder.model.BuilderJsonUtils;
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

  private static final String PROJECT_ID_OPTION = "projectId";
  private static final String JOB_NAME_OPTION = "jobName";
  private static final String PIPELINE_OPTION = "pipeline";
  private static final String PYTHON_EXEC_OPTION = "pythonExec";
  private static final String PYTHON_PATHS_OPTION = "pythonPaths";
  private static final String SCHEMA_URL_OPTION = "schemaUrl";
  private static final String PARALLELISM_OPTION = "parallelism";
  private static final String ALTER_OPERATION_OPTION = "alterOperation";
  private static final String LOG_FILE_OPTION = "logFile";
  private static final String LEAD_TO_OPTION = "leadTo";
  private static final String GRAPH_STORE_URL_OPTION = "graphStoreUrl";
  private static final String SEARCH_ENGINE_URL_OPTION = "searchEngineUrl";


  public static void main(String[] args) {
    String[] args1 = {
            "--projectId",
            "2",
            "--jobName",
            "default_job",
            "--pipeline",
            "{\"nodes\": [{\"id\": \"4412328672\", \"name\": \"CSVReader\", \"nodeConfig\": {\"type\": \"CSV_SOURCE\", \"startRow\": 1, \"url\": \"/Users/workspace/ant/openspg/python/knext/knext/examples/medicine/builder/job/data/Disease.csv\", \"columns\": [\"input\"], \"@type\": \"CSV_SOURCE\"}}, {\"id\": \"4450446768\", \"name\": \"LLMBasedExtractor\", \"nodeConfig\": {\"type\": \"USER_DEFINED_EXTRACT\", \"operatorConfig\": {\"filePath\": \"/Users/yangjin/openspg_venv/miniconda3/envs/openspgapp/lib/python3.8/site-packages/knext/builder/operator/builtin/online_runner.py\", \"modulePath\": \"online_runner\", \"className\": \"_BuiltInOnlineExtractor\", \"method\": \"_handle\", \"params\": {\"model_config\": \"{\\\"nn_name\\\": \\\"gpt-3.5-turbo\\\", \\\"openai_api_key\\\": \\\"EMPTY\\\", \\\"openai_api_base\\\": \\\"http://0.0.0.0:38080/v1\\\", \\\"openai_max_tokens\\\": 1000}\", \"prompt_config\": \"[{\\\"filePath\\\": \\\"/Users/yangjin/openspg_venv/miniconda3/envs/openspgapp/lib/python3.8/site-packages/knext/builder/operator/builtin/auto_prompt.py\\\", \\\"modulePath\\\": \\\"auto_prompt\\\", \\\"className\\\": \\\"REPrompt\\\", \\\"method\\\": \\\"_handle\\\", \\\"params\\\": {\\\"spg_type_name\\\": \\\"Medicine.Disease\\\", \\\"property_names\\\": [\\\"complication\\\", \\\"commonSymptom\\\", \\\"applicableDrug\\\", \\\"department\\\", \\\"diseaseSite\\\"], \\\"relation_names\\\": [[\\\"abnormal\\\", \\\"Medicine.Indicator\\\"]], \\\"with_description\\\": false}}]\", \"max_retry_times\": \"3\"}}, \"@type\": \"USER_DEFINED_EXTRACT\"}}, {\"id\": \"4459892144\", \"name\": \"_SPGTypeMappings\", \"nodeConfig\": {\"type\": \"SPG_TYPE_MAPPINGS\", \"mappingNodeConfigs\": [{\"type\": \"SPG_TYPE_MAPPING\", \"spgType\": \"Medicine.Disease\", \"mappingFilters\": [], \"mappingConfigs\": [{\"source\": \"description\", \"target\": \"description\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"id\", \"target\": \"id\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"name\", \"target\": \"name\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"applicableDrug\", \"target\": \"applicableDrug\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"commonSymptom\", \"target\": \"commonSymptom\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"complication\", \"target\": \"complication\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"department\", \"target\": \"department\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"diseaseSite\", \"target\": \"diseaseSite\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"abnormal\", \"target\": \"abnormal#Medicine.Indicator\", \"mappingType\": \"RELATION\"}], \"@type\": \"SPG_TYPE_MAPPING\"}, {\"type\": \"SPG_TYPE_MAPPING\", \"spgType\": \"Medicine.BodyPart\", \"mappingFilters\": [], \"mappingConfigs\": [{\"source\": \"description\", \"target\": \"description\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"id\", \"target\": \"id\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"name\", \"target\": \"name\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"alias\", \"target\": \"alias\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"stdId\", \"target\": \"stdId\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"isA\", \"target\": \"isA#Medicine.BodyPart\", \"mappingType\": \"RELATION\"}], \"@type\": \"SPG_TYPE_MAPPING\"}, {\"type\": \"SPG_TYPE_MAPPING\", \"spgType\": \"Medicine.Drug\", \"mappingFilters\": [], \"mappingConfigs\": [{\"source\": \"description\", \"target\": \"description\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"id\", \"target\": \"id\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"name\", \"target\": \"name\", \"mappingType\": \"PROPERTY\"}], \"@type\": \"SPG_TYPE_MAPPING\"}, {\"type\": \"SPG_TYPE_MAPPING\", \"spgType\": \"Medicine.HospitalDepartment\", \"mappingFilters\": [], \"mappingConfigs\": [{\"source\": \"description\", \"target\": \"description\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"id\", \"target\": \"id\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"name\", \"target\": \"name\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"alias\", \"target\": \"alias\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"stdId\", \"target\": \"stdId\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"isA\", \"target\": \"isA#Medicine.HospitalDepartment\", \"mappingType\": \"RELATION\"}], \"@type\": \"SPG_TYPE_MAPPING\"}, {\"type\": \"SPG_TYPE_MAPPING\", \"spgType\": \"Medicine.Symptom\", \"mappingFilters\": [], \"mappingConfigs\": [{\"source\": \"description\", \"target\": \"description\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"id\", \"target\": \"id\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"name\", \"target\": \"name\", \"mappingType\": \"PROPERTY\"}], \"@type\": \"SPG_TYPE_MAPPING\"}, {\"type\": \"SPG_TYPE_MAPPING\", \"spgType\": \"Medicine.Indicator\", \"mappingFilters\": [], \"mappingConfigs\": [{\"source\": \"description\", \"target\": \"description\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"id\", \"target\": \"id\", \"mappingType\": \"PROPERTY\"}, {\"source\": \"name\", \"target\": \"name\", \"mappingType\": \"PROPERTY\"}], \"@type\": \"SPG_TYPE_MAPPING\"}], \"@type\": \"SPG_TYPE_MAPPINGS\"}}, {\"id\": \"4456957408\", \"name\": \"KGWriter\", \"nodeConfig\": {\"type\": \"GRAPH_SINK\", \"@type\": \"GRAPH_SINK\"}}], \"edges\": [{\"from\": \"4412328672\", \"to\": \"4450446768\"}, {\"from\": \"4450446768\", \"to\": \"4459892144\"}, {\"from\": \"4459892144\", \"to\": \"4456957408\"}]}",
            "--pythonExec",
            "/Users/yangjin/openspg_venv/miniconda3/envs/openspgapp/bin/python",
            "--pythonPaths",
            "/Users/workspace/ant/openspgapp/openspg/python/knext/knext/examples/medicine/builder/job;/Users/yangjin/Library/Application Support/JetBrains/IntelliJIdea2021.3/plugins/python/helpers/pydev;/Users/workspace/ant/openspgapp/openspg;/Users/workspace/ant/openspgapp/openspg/python/nn4k;/Users/workspace/ant/openspgapp/openspg/python/knext;/Users/yangjin/.m2/repository/org/projectlombok/lombok/1.18.22/lombok-1.18.22.jar;/Users/yangjin/.m2/repository/ch/qos/logback/logback-core/1.2.11/logback-core-1.2.11.jar;/Users/yangjin/.m2/repository/ch/qos/logback/logback-classic/1.2.11/logback-classic-1.2.11.jar;/Users/yangjin/.m2/repository/org/slf4j/slf4j-api/1.7.32/slf4j-api-1.7.32.jar;/Users/yangjin/.m2/repository/org/spockframework/spock-core/2.2-M1-groovy-3.0/spock-core-2.2-M1-groovy-3.0.jar;/Users/yangjin/.m2/repository/org/codehaus/groovy/groovy/3.0.9/groovy-3.0.9.jar;/Users/yangjin/.m2/repository/org/junit/platform/junit-platform-engine/1.8.1/junit-platform-engine-1.8.1.jar;/Users/yangjin/.m2/repository/org/opentest4j/opentest4j/1.2.0/opentest4j-1.2.0.jar;/Users/yangjin/.m2/repository/org/junit/platform/junit-platform-commons/1.8.1/junit-platform-commons-1.8.1.jar;/Users/yangjin/.m2/repository/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar;/Users/yangjin/.m2/repository/org/hamcrest/hamcrest/2.2/hamcrest-2.2.jar;/Users/yangjin/.m2/repository/org/codehaus/groovy/groovy-sql/3.0.9/groovy-sql-3.0.9.jar;/Users/yangjin/.m2/repository/junit/junit/4.13.2/junit-4.13.2.jar;/Users/yangjin/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar;/Users/yangjin/.m2/repository/cglib/cglib-nodep/3.2.5/cglib-nodep-3.2.5.jar;/Users/yangjin/.m2/repository/org/junit/jupiter/junit-jupiter/5.7.1/junit-jupiter-5.7.1.jar;/Users/yangjin/.m2/repository/org/junit/jupiter/junit-jupiter-api/5.7.1/junit-jupiter-api-5.7.1.jar;/Users/yangjin/.m2/repository/org/junit/jupiter/junit-jupiter-params/5.7.1/junit-jupiter-params-5.7.1.jar;/Users/yangjin/.m2/repository/org/junit/jupiter/junit-jupiter-engine/5.7.1/junit-jupiter-engine-5.7.1.jar;/Users/yangjin/Library/Application Support/JetBrains/IntelliJIdea2021.3/plugins/python/helpers/pycharm_display;/Users/yangjin/Library/Application Support/JetBrains/IntelliJIdea2021.3/plugins/python/helpers/third_party/thriftpy;/Users/yangjin/Library/Application Support/JetBrains/IntelliJIdea2021.3/plugins/python/helpers/pydev;/Users/yangjin/Library/Caches/JetBrains/IntelliJIdea2021.3/cythonExtensions;/Users/workspace/ant/openspgapp/openspg/python/knext/knext/examples/medicine/builder/job;/Users/yangjin/openspg_venv/miniconda3/envs/openspgapp/lib/python38.zip;/Users/yangjin/openspg_venv/miniconda3/envs/openspgapp/lib/python3.8;/Users/yangjin/openspg_venv/miniconda3/envs/openspgapp/lib/python3.8/lib-dynload;/Users/yangjin/openspg_venv/miniconda3/envs/openspgapp/lib/python3.8/site-packages;/Users/yangjin/Library/Application Support/JetBrains/IntelliJIdea2021.3/plugins/python/helpers/pycharm_matplotlib_backend;/Users/workspace/ant/openspgapp/openspg/python/knext/knext/examples/medicine;/Users/workspace/ant/openspgapp/openspg/python/knext/knext/examples/medicine/builder/operator;/Users/workspace/ant/openspgapp/openspg/python/knext/knext/builder/operator/builtin",
            "--schemaUrl",
            "http://127.0.0.1:8887",
            "--parallelism",
            "1",
            "--alterOperation",
            "UPSERT",
            "--logFile",
            "2024-06-28_16-02-05.log",
            "--graphStoreUrl",
            "tugraph://127.0.0.1:9090?graphName=default&timeout=50000&accessId=admin&accessKey=73@TuGraph",
            "--searchEngineUrl",
            "elasticsearch://127.0.0.1:9200?scheme=http",
    };
    main1(args1);
  }

  public static void main1(String[] args) {
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

    options.addRequiredOption(PROJECT_ID_OPTION, PROJECT_ID_OPTION, true, "project id");
    options.addRequiredOption(JOB_NAME_OPTION, JOB_NAME_OPTION, true, "job name");
    options.addRequiredOption(PIPELINE_OPTION, PIPELINE_OPTION, true, "pipeline info");
    options.addRequiredOption(PYTHON_EXEC_OPTION, PYTHON_EXEC_OPTION, true, "python exec");
    options.addRequiredOption(PYTHON_PATHS_OPTION, PYTHON_PATHS_OPTION, true, "python path");
    options.addRequiredOption(SCHEMA_URL_OPTION, SCHEMA_URL_OPTION, true, "schema url");
    options.addOption(PARALLELISM_OPTION, PARALLELISM_OPTION, true, "parallelism");
    options.addOption(
        ALTER_OPERATION_OPTION, ALTER_OPERATION_OPTION, true, "alter operation, upsert or delete");
    options.addOption(LOG_FILE_OPTION, LOG_FILE_OPTION, true, "log file");
    options.addOption(LEAD_TO_OPTION, LEAD_TO_OPTION, false, "enable leadTo");
    options.addRequiredOption(
        GRAPH_STORE_URL_OPTION, GRAPH_STORE_URL_OPTION, true, "graph store url");
    options.addRequiredOption(
        SEARCH_ENGINE_URL_OPTION, SEARCH_ENGINE_URL_OPTION, true, "search engine url");

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
    String logFileName = commandLine.getOptionValue(LOG_FILE_OPTION);
    setUpLogFile(logFileName);

    long projectId = Long.parseLong(commandLine.getOptionValue(PROJECT_ID_OPTION));
    String jobName = commandLine.getOptionValue(JOB_NAME_OPTION);

    String pipelineStr = commandLine.getOptionValue(PIPELINE_OPTION);
    Pipeline pipeline = BuilderJsonUtils.deserialize(pipelineStr, Pipeline.class);

    String pythonExec = commandLine.getOptionValue(PYTHON_EXEC_OPTION);
    String pythonPaths = commandLine.getOptionValue(PYTHON_PATHS_OPTION);
    String schemaUrl = commandLine.getOptionValue(SCHEMA_URL_OPTION);

    String parallelismStr = commandLine.getOptionValue(PARALLELISM_OPTION);
    int parallelism = (parallelismStr == null ? 1 : Integer.parseInt(parallelismStr));

    String alterOperation = commandLine.getOptionValue(ALTER_OPERATION_OPTION);
    RecordAlterOperationEnum alterOperationEnum = RecordAlterOperationEnum.valueOf(alterOperation);

    boolean enableLeadTo = commandLine.hasOption(LEAD_TO_OPTION);

    String graphStoreUrl = commandLine.getOptionValue(GRAPH_STORE_URL_OPTION);
    String searchEngineUrl = commandLine.getOptionValue(SEARCH_ENGINE_URL_OPTION);

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
            .setSearchEngineUrl(searchEngineUrl);

    LocalBuilderRunner runner = new LocalBuilderRunner(parallelism);
    runner.init(pipeline, builderContext);

    try {
      runner.execute();
    } finally {
      runner.close();
    }
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
