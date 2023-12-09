package com.antgroup.openspg.builder.runner.local;

import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.BuilderJsonUtils;
import com.antgroup.openspg.builder.model.exception.PipelineConfigException;
import com.antgroup.openspg.builder.model.pipeline.Pipeline;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.server.api.facade.ApiResponse;
import com.antgroup.openspg.server.api.facade.client.SchemaFacade;
import com.antgroup.openspg.server.api.facade.dto.schema.request.ProjectSchemaRequest;
import com.antgroup.openspg.server.api.http.client.HttpSchemaFacade;
import com.antgroup.openspg.server.api.http.client.util.ConnectionInfo;
import com.antgroup.openspg.server.api.http.client.util.HttpClientBootstrap;
import org.apache.commons.cli.*;

public class LocalBuilderMain {

  private static final String PROJECT_ID_OPTION = "projectId";
  private static final String JOB_NAME_OPTION = "jobName";
  private static final String PIPELINE_OPTION = "pipeline";
  private static final String PYTHON_EXEC_OPTION = "pythonExec";
  private static final String PYTHON_PATHS_OPTION = "pythonPaths";
  private static final String SCHEMA_URL_OPTION = "schemaUrl";
  private static final String PARALLELISM_OPTION = "parallelism";

  public static void main(String[] args) throws Exception {
    CommandLine commandLine = parseArgs(args);
    run(commandLine);
  }

  public static CommandLine parseArgs(String[] args) {
    CommandLineParser parser = new DefaultParser();
    Options options = new Options();

    options.addRequiredOption(PROJECT_ID_OPTION, null, true, "project id");
    options.addRequiredOption(JOB_NAME_OPTION, null, true, "job name");
    options.addRequiredOption(PIPELINE_OPTION, null, true, "pipeline info");
    options.addRequiredOption(PYTHON_EXEC_OPTION, null, true, "python exec");
    options.addRequiredOption(PYTHON_PATHS_OPTION, null, true, "python path");
    options.addRequiredOption(SCHEMA_URL_OPTION, null, true, "schema url");
    options.addOption(PARALLELISM_OPTION, null, true, "parallelism");

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
    long projectId = Long.parseLong(commandLine.getOptionValue(PROJECT_ID_OPTION));
    String jobName = commandLine.getOptionValue(JOB_NAME_OPTION);

    String pipelineStr = commandLine.getOptionValue(PIPELINE_OPTION);
    Pipeline pipeline = BuilderJsonUtils.deserialize(pipelineStr, Pipeline.class);

    String pythonExec = commandLine.getOptionValue(PYTHON_EXEC_OPTION);
    String pythonPaths = commandLine.getOptionValue(PYTHON_PATHS_OPTION);
    String schemaUrl = commandLine.getOptionValue(SCHEMA_URL_OPTION);

    String parallelismStr = commandLine.getOptionValue(PARALLELISM_OPTION);
    int parallelism = (parallelismStr == null ? 1 : Integer.parseInt(parallelismStr));

    ProjectSchema projectSchema = getProjectSchema(projectId, schemaUrl);
    BuilderContext builderContext =
        new BuilderContext()
            .setProjectId(projectId)
            .setJobName(jobName)
            .setProjectSchema(projectSchema)
            .setPythonExec(pythonExec)
            .setPythonPaths(pythonPaths);

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
    throw new PipelineConfigException("");
  }
}
