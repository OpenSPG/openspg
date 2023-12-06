package com.antgroup.openspg.builder.runner.local;

import org.apache.commons.cli.*;

public class LocalBuilderMain {

  public static void main(String[] args) {
    CommandLineParser parser = new DefaultParser();
    Options options = new Options();

    options.addRequiredOption("p", "pipeline", true, "pipeline info");
    options.addOption("s", "schema", true, "schema info");

    CommandLine commandLine = null;
    HelpFormatter helper = new HelpFormatter();
    try {
      commandLine = parser.parse(options, args);
      String pipelineStr = commandLine.getOptionValue("p");
      String schemaStr = commandLine.getOptionValue("s");

    } catch (ParseException e) {
      System.out.println(e.getMessage());
      helper.printHelp("Usage: ", options);
      System.exit(0);
    }
  }
}
