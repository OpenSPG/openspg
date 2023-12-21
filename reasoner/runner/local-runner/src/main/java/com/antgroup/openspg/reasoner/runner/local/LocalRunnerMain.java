/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.reasoner.runner.local;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.antgroup.openspg.reasoner.catalog.impl.KgSchemaConnectionInfo;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class LocalRunnerMain {

  /** KGReasoner main */
  public static void main(String[] args) {
    LocalReasonerTask task = parseArgs(args);
    if (null == task) {
      System.exit(1);
    }
    KGReasonerLocalRunner runner = new KGReasonerLocalRunner();
    LocalReasonerResult result = runner.run(task);
    if (null == result) {
      log.error("local runner return null");
      return;
    }
    if (StringUtils.isNotEmpty(result.getErrMsg())) {
      log.error(result.getErrMsg());
    }
    if (StringUtils.isNotEmpty(task.getOutputFile())) {
      writeOutputFile(result, task.getOutputFile());
    }
  }

  private static void writeOutputFile(LocalReasonerResult result, String file) {
    Path path = Paths.get(file);
    try {
      if (Files.notExists(path.getParent())) {
        Files.createDirectories(path.getParent());
      }
      if (Files.exists(path)) {
        Files.delete(path);
      }
    } catch (IOException e) {
      log.error("write result file error, file=" + file, e);
      return;
    }

    if (StringUtils.isNotEmpty(result.getErrMsg())) {
      writeFile(path, result.getErrMsg());
    } else if (result.isGraphResult()) {
      // write graph result
      writeFile(path, result.toString());
    } else {
      // write csv
      writeCsv(path, result.getColumns(), result.getRows());
    }
  }

  private static void writeCsv(Path path, List<String> columns, List<Object[]> rows) {
    List<String[]> allLines = new ArrayList<>(rows.size() + 1);
    allLines.add(columns.toArray(new String[] {}));
    for (Object[] rowObj : rows) {
      String[] row = new String[rowObj.length];
      for (int i = 0; i < rowObj.length; ++i) {
        if (null != rowObj[i]) {
          row[i] = String.valueOf(rowObj[i]);
        } else {
          row[i] = null;
        }
      }
      allLines.add(row);
    }

    CSVWriter csvWriter;
    try {
      csvWriter = new CSVWriter(new FileWriter(path.toString()));
      csvWriter.writeAll(allLines);
      csvWriter.close();
    } catch (IOException e) {
      log.error("csvwriter error, file=" + path, e);
    }
  }

  private static void writeFile(Path path, String content) {
    try {
      Files.write(path, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
    } catch (IOException e) {
      log.error("write result file error, file=" + path, e);
    }
  }

  private static LocalReasonerTask parseArgs(String[] args) {
    Options options = getOptions();

    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    CommandLine cmd;

    String dsl;
    String outputFile;
    String schemaUri;
    String schemaToken;
    String graphStateClass;
    String graphStateUrl;
    List<List<String>> startIdList;
    Map<String, Object> params = null;
    try {
      cmd = parser.parse(options, args);
      dsl = cmd.getOptionValue("q");
      if (StringUtils.isEmpty(dsl)) {
        throw new ParseException("please provide query dsl!");
      }
      outputFile = cmd.getOptionValue("o");
      if (StringUtils.isEmpty(outputFile)) {
        outputFile = null;
      }
      schemaUri = cmd.getOptionValue("s");
      if (StringUtils.isEmpty(schemaUri)) {
        throw new ParseException("please provide openspg schema uri!");
      }
      schemaToken = cmd.getOptionValue("st");
      if (StringUtils.isEmpty(schemaToken)) {
        throw new ParseException("please provide openspg schema api token!");
      }
      graphStateClass = cmd.getOptionValue("g");
      if (StringUtils.isEmpty(graphStateClass)) {
        throw new ParseException("please provide graph state class name!");
      }
      graphStateUrl = cmd.getOptionValue("gs");
      if (StringUtils.isEmpty(graphStateUrl)) {
        graphStateUrl = null;
      }
      String startIdListJson = cmd.getOptionValue("start");
      if (StringUtils.isEmpty(startIdListJson)) {
        throw new ParseException("please provide start id");
      }
      startIdList = JSON.parseObject(startIdListJson, new TypeReference<List<List<String>>>() {});
      String paramsJson = cmd.getOptionValue("params");
      if (StringUtils.isNotEmpty(paramsJson)) {
        params = new HashMap<>(JSON.parseObject(paramsJson));
      }
    } catch (ParseException e) {
      log.error(e.getMessage());
      formatter.printHelp("ReasonerLocalRunner", options);
      return null;
    }

    LocalReasonerTask task = new LocalReasonerTask();
    task.setId(UUID.randomUUID().toString());
    task.setDsl(dsl);
    task.setOutputFile(outputFile);
    task.setConnInfo(new KgSchemaConnectionInfo(schemaUri, schemaToken));
    task.setGraphStateClassName(graphStateClass);
    task.setGraphStateInitString(graphStateUrl);
    task.setStartIdList(new ArrayList<>());
    task.addStartId(startIdList);
    task.setParams(params);
    return task;
  }

  private static Options getOptions() {
    Options options = new Options();

    Option optDsl = new Option("q", "query", true, "query dsl string");
    optDsl.setRequired(true);
    options.addOption(optDsl);

    Option optOutputFile = new Option("o", "output", true, "output file");
    optOutputFile.setRequired(false);
    options.addOption(optOutputFile);

    Option optSchemaUri = new Option("s", "schema_uri", true, "provide schema uri");
    optSchemaUri.setRequired(true);
    options.addOption(optSchemaUri);

    Option optSchemaToken = new Option("st", "schema_token", true, "provide schema token");
    optSchemaToken.setRequired(true);
    options.addOption(optSchemaToken);

    Option optGraphStateClass =
        new Option("g", "graph_state_class", true, "graph state class name");
    optGraphStateClass.setRequired(true);
    options.addOption(optGraphStateClass);

    Option optGraphStateUrl = new Option("gs", "graph_state_url", true, "graph state url");
    optGraphStateUrl.setRequired(false);
    options.addOption(optGraphStateUrl);

    Option optStartIdList = new Option("start", "start_id_list", true, "start id json list");
    optStartIdList.setRequired(true);
    options.addOption(optStartIdList);

    Option optParamsJson =
        new Option("params", "param_map_json_str", true, "parameter map json string");
    optParamsJson.setRequired(false);
    options.addOption(optParamsJson);
    return options;
  }
}
