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

    CommandLine commandLine = null;
    HelpFormatter helper = new HelpFormatter();
    try {
      commandLine = parser.parse(options, args);
    } catch (ParseException e) {
      System.out.println(e.getMessage());
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
            .setEnableLeadTo(enableLeadTo);

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
