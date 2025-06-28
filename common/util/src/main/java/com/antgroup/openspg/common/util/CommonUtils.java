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
package com.antgroup.openspg.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.antgroup.openspg.common.constants.BuilderConstant;
import com.antgroup.openspg.common.util.constants.CommonConstant;
import com.antgroup.openspg.server.common.model.CommonConstants;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.datasource.DataSource;
import com.antgroup.openspg.server.common.model.project.Project;
import com.google.common.collect.Maps;
import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class CommonUtils {

  /** The maximum number of non-paging entries for a DB query */
  public static final int INNER_QUERY_MAX_COUNT = 10000;

  public static final String BIZ_DATE = "bizdate";

  public static void checkQueryPage(int count, Integer pageNo, Integer pageSize) {
    // pageNo is empty to indicate no paging
    if (pageNo == null) {
      // If the query is all, it must be less than the maximum limit to prevent OOM
      Assert.isTrue(
          count <= INNER_QUERY_MAX_COUNT,
          String.format(
              "The current query data volume %s exceeds the maximum limit %s, please use pagination query",
              count, INNER_QUERY_MAX_COUNT));
      return;
    }
    // When pageNo is not empty, pageSize cannot be empty either
    Assert.notNull(pageSize, "pageSize cannot be null");
    // pageSize cannot be larger than the maximum value
    Assert.isTrue(
        pageSize <= INNER_QUERY_MAX_COUNT,
        String.format(
            "The current query data volume %s exceeds the maximum limit %s, please use pagination query",
            pageSize, INNER_QUERY_MAX_COUNT));
  }

  public static String getInstanceStorageFileKey(Long projectId, Long instanceId) {
    return "builder"
        + File.separator
        + "project_"
        + projectId
        + File.separator
        + "instance_"
        + instanceId
        + File.separator;
  }

  public static String getTaskStorageFileKey(
      Long projectId, Long instanceId, Long taskId, String type) {
    return getInstanceStorageFileKey(projectId, instanceId) + taskId + "_" + type + ".kag";
  }

  public static String getTaskStoragePathKey(
      Long projectId, Long instanceId, Long taskId, String type) {
    return getInstanceStorageFileKey(projectId, instanceId) + taskId + "_" + type + File.separator;
  }

  public static String getTaskStorageFileKey(String path, String name) {
    return path + name + ".kag";
  }

  public static JSONObject getKagBuilderConfig(
      Project project, BuilderJob builderJob, String hostAddr) {
    JSONObject extension = JSON.parseObject(builderJob.getExtension());

    JSONObject config = new JSONObject();
    JSONObject projectConfig = new JSONObject();
    projectConfig.put(BuilderConstant.BIZ_SCENE, BuilderConstant.DEFAULT);
    projectConfig.put(BuilderConstant.HOST_ADDR, hostAddr);
    projectConfig.put(BuilderConstant.ID, project.getId());
    JSONObject projConfig = JSONObject.parseObject(project.getConfig());
    JSONObject prompt = projConfig.getJSONObject(CommonConstants.PROMPT);
    projectConfig.put(BuilderConstant.LANGUAGE, prompt.getString(BuilderConstant.LANGUAGE));
    projectConfig.put(BuilderConstant.NAMESPACE, project.getNamespace());
    config.put(BuilderConstant.PROJECT, projectConfig);

    JSONObject datasourceConfig = extension.getJSONObject(BuilderConstant.DATASOURCE_CONFIG);
    datasourceConfig = (datasourceConfig == null) ? new JSONObject() : datasourceConfig;
    Boolean structure =
        (Boolean) datasourceConfig.getOrDefault(BuilderConstant.STRUCTURE, Boolean.FALSE);

    JSONObject scanner = new JSONObject();
    JSONObject reader = new JSONObject();
    getScannerReaderConfig(project, builderJob, scanner, reader);

    JSONObject kagBuilderPipeline = new JSONObject();
    JSONObject chain = new JSONObject();

    if (structure) {
      chain.put(BuilderConstant.TYPE, BuilderConstant.STRUCTURED_BUILDER_CHAIN);
      JSONObject mapping = getMappingConfig(extension, null);
      chain.put(BuilderConstant.MAPPING, mapping);
    } else {
      chain.put(BuilderConstant.TYPE, BuilderConstant.UNSTRUCTURED_BUILDER_CHAIN);

      chain.put(BuilderConstant.READER, reader);

      JSONObject splitter = getSplitterConfig(project, extension, null);
      chain.put(BuilderConstant.SPLITTER, splitter);

      JSONObject extractor = getExtractorConfig(project, extension, null);
      chain.put(BuilderConstant.EXTRACTOR, extractor);

      JSONObject postProcessor = new JSONObject();
      postProcessor.put(BuilderConstant.TYPE, BuilderConstant.KAG_POST_PROCESSOR);
      chain.put(BuilderConstant.POST_PROCESSOR, postProcessor);
    }
    JSONObject vectorizer = new JSONObject();
    vectorizer.put(BuilderConstant.TYPE, BuilderConstant.BATCH_VECTORIZER);
    JSONObject projectVectorizer = projConfig.getJSONObject(CommonConstants.VECTORIZER);
    vectorizer.put(BuilderConstant.VECTORIZE_MODEL, projectVectorizer);
    chain.put(BuilderConstant.VECTORIZER, vectorizer);

    JSONObject writer = new JSONObject();
    writer.put(BuilderConstant.TYPE, BuilderConstant.KG_WRITER);
    writer.put(BuilderConstant.PROJECT_ID, project.getId());
    if (BuilderConstant.DELETE.equalsIgnoreCase(builderJob.getAction())) {
      writer.put(BuilderConstant.DELETE, true);
    }
    chain.put(BuilderConstant.WRITER, writer);

    kagBuilderPipeline.put(BuilderConstant.CHAIN, chain);
    kagBuilderPipeline.put(BuilderConstant.NUM_THREADS_PER_CHAIN, 2);
    kagBuilderPipeline.put(BuilderConstant.NUM_CHAINS, 4);
    kagBuilderPipeline.put(BuilderConstant.SCANNER, scanner);

    config.put(BuilderConstant.KAG_BUILDER_PIPELINE, kagBuilderPipeline);
    return config;
  }

  public static JSONObject getMappingConfig(JSONObject builderExtension, JSONObject mappingConfig) {
    if (mappingConfig == null) {
      mappingConfig = new JSONObject();
    }
    JSONObject property = new JSONObject();
    JSONObject mappingConf = builderExtension.getJSONObject(BuilderConstant.MAPPING_CONFIG);
    String type =
        (String)
            mappingConf.getOrDefault(BuilderConstant.MAPPING_TYPE, BuilderConstant.ENTITY_MAPPING);
    JSONArray filter = mappingConf.getJSONArray(BuilderConstant.FILTER);
    List<Map<String, Object>> configList =
        JSON.parseObject(
            mappingConf.getString(BuilderConstant.CONFIG),
            new TypeReference<List<Map<String, Object>>>() {});
    configList.forEach(
        config -> {
          Map<String, List<String>> mapping =
              (Map<String, List<String>>) config.get(BuilderConstant.MAPPING);
          mapping
              .keySet()
              .forEach(
                  name -> mapping.get(name).forEach(schemaName -> property.put(schemaName, name)));
        });

    if (BuilderConstant.RELATION_MAPPING.equalsIgnoreCase(type)) {
      mappingConfig.put(BuilderConstant.TYPE, BuilderConstant.RELATION);
      mappingConfig.put(
          BuilderConstant.SUBJECT_NAME,
          unLabelPrefix(filter.getJSONObject(0).getString(BuilderConstant.S)));
      mappingConfig.put(
          BuilderConstant.PREDICATE_NAME,
          unLabelPrefix(filter.getJSONObject(0).getString(BuilderConstant.P)));
      mappingConfig.put(
          BuilderConstant.OBJECT_NAME,
          unLabelPrefix(filter.getJSONObject(0).getString(BuilderConstant.O)));
      mappingConfig.put(BuilderConstant.SRC_ID_FIELD, property.get(BuilderConstant.START_ID));
      mappingConfig.put(BuilderConstant.DST_ID_FIELD, property.get(BuilderConstant.END_ID));
      property.put(BuilderConstant.SRC_ID, property.remove(BuilderConstant.START_ID));
      property.put(BuilderConstant.DST_ID, property.remove(BuilderConstant.END_ID));
    } else {
      mappingConfig.put(BuilderConstant.TYPE, BuilderConstant.SPG_MAPPING);
      mappingConfig.put(
          BuilderConstant.SPG_TYPE_NAME,
          unLabelPrefix(filter.getJSONObject(0).getString(BuilderConstant.S)));
    }
    mappingConfig.put(BuilderConstant.PROPERTY_MAPPING, property);
    return mappingConfig;
  }

  public static JSONObject getExtractorConfig(
      Project project, JSONObject builderExtension, JSONObject extractorConfig) {
    if (extractorConfig == null) {
      extractorConfig = new JSONObject();
    }
    JSONObject extractConfig = builderExtension.getJSONObject(BuilderConstant.EXTRACT_CONFIG);
    JSONObject llm = JSONObject.parseObject(project.getConfig()).getJSONObject(CommonConstants.LLM);
    if (extractConfig != null && extractConfig.containsKey(CommonConstants.LLM)) {
      llm = JSONObject.parseObject(extractConfig.getString(CommonConstants.LLM));
    }
    extractorConfig.put(BuilderConstant.LLM, llm);

    Boolean autoSchema = true;
    if (extractConfig != null && extractConfig.containsKey(BuilderConstant.AUTO_SCHEMA)) {
      autoSchema = extractConfig.getBoolean(BuilderConstant.AUTO_SCHEMA);
    }
    if (autoSchema) {
      extractorConfig.put(BuilderConstant.TYPE, BuilderConstant.SCHEMA_FREE);
    } else {
      extractorConfig.put(BuilderConstant.TYPE, BuilderConstant.SCHEMA_CONSTRAINT_EXTRACTOR);
    }
    return extractorConfig;
  }

  public static JSONObject getSplitterConfig(
      Project project, JSONObject builderExtension, JSONObject splitterConfig) {
    if (splitterConfig == null) {
      splitterConfig = new JSONObject();
    }
    JSONObject extractConfig = builderExtension.getJSONObject(BuilderConstant.EXTRACT_CONFIG);
    JSONObject llm = JSONObject.parseObject(project.getConfig()).getJSONObject(CommonConstants.LLM);
    if (extractConfig != null && extractConfig.containsKey(CommonConstants.LLM)) {
      llm = JSONObject.parseObject(extractConfig.getString(CommonConstants.LLM));
    }
    JSONObject config = builderExtension.getJSONObject(BuilderConstant.SPLIT_CONFIG);
    Boolean semanticSplit = false;
    if (config != null && config.containsKey(BuilderConstant.SEMANTIC_SPLIT)) {
      semanticSplit = config.getBoolean(BuilderConstant.SEMANTIC_SPLIT);
    }
    Long splitLength = 2000L;
    if (config != null && config.containsKey(BuilderConstant.SPLIT_LENGTH)) {
      splitLength = config.getLong(BuilderConstant.SPLIT_LENGTH);
    }
    if (semanticSplit != null && semanticSplit) {
      splitterConfig.put(BuilderConstant.TYPE, BuilderConstant.SEMANTIC);
      splitterConfig.put(BuilderConstant.LLM, llm);
      splitterConfig.put(BuilderConstant.PY_SPLIT_LENGTH, splitLength);
    } else {
      splitterConfig.put(BuilderConstant.TYPE, BuilderConstant.LENGTH);
      splitterConfig.put(BuilderConstant.PY_SPLIT_LENGTH, splitLength);
      splitterConfig.put(BuilderConstant.PY_WINDOW_LENGTH, 0);
    }
    return splitterConfig;
  }

  public static void getScannerReaderConfig(
      Project project, BuilderJob job, JSONObject scanner, JSONObject reader) {
    if (scanner == null) {
      scanner = new JSONObject();
    }
    if (reader == null) {
      reader = new JSONObject();
    }
    JSONObject builderExtension = JSON.parseObject(job.getExtension());
    JSONObject llm = JSONObject.parseObject(project.getConfig()).getJSONObject(CommonConstants.LLM);
    String dataSourceType = job.getDataSourceType();

    if (BuilderConstant.YU_QUE.equalsIgnoreCase(dataSourceType)) {
      scanner.put(BuilderConstant.TYPE, BuilderConstant.YU_QUE);
      JSONObject config = builderExtension.getJSONObject(BuilderConstant.YU_QUE_CONFIG);
      scanner.put(BuilderConstant.TOKEN, config.getString(BuilderConstant.YU_QUE_TOKEN));
      reader.put(BuilderConstant.TYPE, BuilderConstant.YU_QUE);
      reader.put(BuilderConstant.CUT_DEPTH, 3);
    } else if (BuilderConstant.ODPS.equalsIgnoreCase(dataSourceType)) {
      scanner.put(BuilderConstant.TYPE, BuilderConstant.ODPS_SCANNER);
      JSONObject dataSourceConfig =
          builderExtension.getJSONObject(BuilderConstant.DATASOURCE_CONFIG);
      DataSource dataSource =
          JSON.parseObject(
              dataSourceConfig.getString(BuilderConstant.DATASOURCE), DataSource.class);
      scanner.put(BuilderConstant.ACCESS_ID, dataSource.getDbUser());
      String password = ECBUtil.decrypt(dataSource.getEncrypt(), CommonConstant.ECB_PASSWORD_KEY);
      scanner.put(BuilderConstant.ACCESS_KEY, password);
      scanner.put(BuilderConstant.ENDPOINT, dataSource.getDbUrl());
      scanner.put(BuilderConstant.PROJECT, dataSourceConfig.getString(BuilderConstant.DATABASE));
      scanner.put(BuilderConstant.TABLE, dataSourceConfig.getString(BuilderConstant.TABLE));
      Boolean structure =
          (Boolean) dataSourceConfig.getOrDefault(BuilderConstant.STRUCTURE, Boolean.FALSE);
      if (!structure) {
        JSONArray colNames = new JSONArray();
        JSONArray columns = dataSourceConfig.getJSONArray(BuilderConstant.COLUMNS);
        for (int i = 0; i < columns.size(); i++) {
          String name = columns.getJSONObject(i).getString(BuilderConstant.NAME);
          if (StringUtils.isNotBlank(name)) {
            colNames.add(name);
          }
        }
        scanner.put(BuilderConstant.COL_NAMES, colNames);
      }
      reader.put(BuilderConstant.TYPE, BuilderConstant.DICT);
    } else if (BuilderConstant.SLS.equalsIgnoreCase(dataSourceType)) {
      scanner.put(BuilderConstant.TYPE, BuilderConstant.SLS_CONSUMER_SCANNER);
      JSONObject dataSourceConfig =
          builderExtension.getJSONObject(BuilderConstant.DATASOURCE_CONFIG);
      scanner.put(
          BuilderConstant.ENDPOINT, dataSourceConfig.getString(BuilderConstant.SLS_END_POINT));
      scanner.put(
          BuilderConstant.LOG_STORE, dataSourceConfig.getString(BuilderConstant.SLS_LOG_STORE));
      scanner.put(BuilderConstant.PROJECT, dataSourceConfig.getString(BuilderConstant.PROJECT));
      scanner.put(
          BuilderConstant.ACCESS_ID, dataSourceConfig.getString(BuilderConstant.SLS_ACCESS_ID));
      scanner.put(
          BuilderConstant.ACCESS_KEY, dataSourceConfig.getString(BuilderConstant.SLS_ACCESS_KEY));
      Boolean structure =
          (Boolean) dataSourceConfig.getOrDefault(BuilderConstant.STRUCTURE, Boolean.FALSE);
      if (!structure) {
        JSONArray colNames = new JSONArray();
        JSONArray columns = dataSourceConfig.getJSONArray(BuilderConstant.COLUMNS);
        for (int i = 0; i < columns.size(); i++) {
          String name = columns.getJSONObject(i).getString(BuilderConstant.NAME);
          if (StringUtils.isNotBlank(name)) {
            colNames.add(name);
          }
        }
        scanner.put(BuilderConstant.COL_NAMES, colNames);
      }
      reader.put(BuilderConstant.TYPE, BuilderConstant.DICT);
    } else {
      UriComponents uri = UriComponentsBuilder.fromUriString(job.getFileUrl()).build();
      String extension = FilenameUtils.getExtension(uri.getPath());
      extension = StringUtils.isBlank(extension) ? "" : extension.toLowerCase();
      switch (extension) {
        case BuilderConstant.CSV:
          JSONObject dataSourceConfig =
              builderExtension.getJSONObject(BuilderConstant.DATASOURCE_CONFIG);

          Boolean ignoreHeader = true;
          if (dataSourceConfig != null
              && dataSourceConfig.containsKey(BuilderConstant.IGNORE_HEADER)) {
            ignoreHeader = dataSourceConfig.getBoolean(BuilderConstant.IGNORE_HEADER);
          }
          scanner.put(BuilderConstant.HEADER, ignoreHeader);

          Boolean structure = Boolean.FALSE;
          if (dataSourceConfig != null && dataSourceConfig.containsKey(BuilderConstant.STRUCTURE)) {
            structure = dataSourceConfig.getBoolean(BuilderConstant.STRUCTURE);
          }
          if (structure) {
            scanner.put(BuilderConstant.TYPE, BuilderConstant.CSV_STRUCTURED);
            Map<String, String> colMap = Maps.newHashMap();
            JSONArray columns = dataSourceConfig.getJSONArray(BuilderConstant.COLUMNS);
            for (int i = 0; i < columns.size(); i++) {
              colMap.put(
                  columns.getJSONObject(i).getString(BuilderConstant.INDEX),
                  columns.getJSONObject(i).getString(BuilderConstant.NAME));
            }
            scanner.put(BuilderConstant.COL_MAP, colMap);

          } else {
            scanner.put(BuilderConstant.TYPE, BuilderConstant.CSV);
            if (dataSourceConfig != null && dataSourceConfig.containsKey(BuilderConstant.COLUMNS)) {
              JSONArray colIds = new JSONArray();
              JSONArray columns = dataSourceConfig.getJSONArray(BuilderConstant.COLUMNS);
              for (int i = 0; i < columns.size(); i++) {
                Integer index = columns.getJSONObject(i).getInteger(BuilderConstant.INDEX);
                if (index != null) {
                  colIds.add(index);
                }
              }
              scanner.put(BuilderConstant.COL_IDS, colIds);
            } else {
              JSONArray colNames = new JSONArray();
              colNames.add(BuilderConstant.CONTENT);
              scanner.put(BuilderConstant.COL_NAMES, colNames);
            }
          }
          reader.put(BuilderConstant.TYPE, BuilderConstant.DICT);
          break;
        case BuilderConstant.JSON:
          scanner.put(BuilderConstant.TYPE, BuilderConstant.JSON);
          reader.put(BuilderConstant.TYPE, BuilderConstant.DICT);
          reader.put(BuilderConstant.ID_COL, BuilderConstant.ID);
          reader.put(BuilderConstant.NAME_COL, BuilderConstant.NAME);
          reader.put(BuilderConstant.CONTENT_COL, BuilderConstant.CONTENT);
          break;
        case BuilderConstant.TXT:
          scanner.put(BuilderConstant.TYPE, BuilderConstant.FILE);
          reader.put(BuilderConstant.TYPE, BuilderConstant.TXT);
          break;
        case BuilderConstant.PDF:
          scanner.put(BuilderConstant.TYPE, BuilderConstant.FILE);
          reader.put(BuilderConstant.TYPE, BuilderConstant.PDF);
          reader.put(BuilderConstant.CUT_DEPTH, 3);
          reader.put(BuilderConstant.LLM, llm);
          break;
        case BuilderConstant.MD:
          scanner.put(BuilderConstant.TYPE, BuilderConstant.FILE);
          reader.put(BuilderConstant.TYPE, BuilderConstant.MD);
          reader.put(BuilderConstant.CUT_DEPTH, 3);
          reader.put(BuilderConstant.LLM, llm);
          break;
        case BuilderConstant.DOC:
        case BuilderConstant.DOCX:
          scanner.put(BuilderConstant.TYPE, BuilderConstant.FILE);
          reader.put(BuilderConstant.TYPE, BuilderConstant.DOCX);
          reader.put(BuilderConstant.LLM, llm);
          break;
      }
    }
  }

  public static String getKagBuilderInput(BuilderJob builderJob, Date bizDate) {
    String input = builderJob.getFileUrl();
    String dataSourceType = builderJob.getDataSourceType().toLowerCase();
    if (BuilderConstant.ODPS.equalsIgnoreCase(dataSourceType)) {
      JSONObject builderExtension = JSON.parseObject(builderJob.getExtension());
      JSONObject dataSourceConfig =
          builderExtension.getJSONObject(BuilderConstant.DATASOURCE_CONFIG);
      String partition = dataSourceConfig.getString(BuilderConstant.PARTITION);
      input = replacePartition(partition, bizDate);
    }
    return input;
  }

  public static String replacePartition(String partitionWithVariable, Date bizDate) {
    if (bizDate == null) {
      bizDate = new Date();
    }
    String format = getDateFormatByPartition(partitionWithVariable);
    String date = DateTimeUtils.getDate2Str(format, bizDate);
    return PartitionUtils.replaceDtVariable(partitionWithVariable, date, ",");
  }

  public static String unLabelPrefix(String label) {
    if (!label.contains(BuilderConstant.DOT)) {
      return label;
    }
    return label.split("\\" + BuilderConstant.DOT)[1];
  }

  public static String getDateFormatByPartition(String partition) {
    String regex = "\\$\\[(.*?)\\]";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(partition);

    while (matcher.find()) {
      String format = matcher.group(1);
      if (BIZ_DATE.equalsIgnoreCase(format)) {
        return DateTimeUtils.YYYY_MM_DD1;
      }
      return format;
    }
    return DateTimeUtils.YYYY_MM_DD1;
  }

  /** get Previous ValidTime */
  public static Date getPreviousValidTime(String cron, Date date) {
    CronExpression expression = getCronExpression(cron);
    Date endDate = expression.getNextValidTimeAfter(expression.getNextValidTimeAfter(date));
    Long time = 2 * date.getTime() - endDate.getTime();

    Date nextDate = expression.getNextValidTimeAfter(new Date(time));
    Date preDate = nextDate;
    while (nextDate != null && nextDate.before(date)) {
      preDate = nextDate;
      nextDate = expression.getNextValidTimeAfter(nextDate);
    }
    return preDate;
  }

  /** get CronExpression */
  public static CronExpression getCronExpression(String cron) {
    try {
      return new CronExpression(cron);
    } catch (ParseException e) {
      throw new RuntimeException("Cron ParseException:" + cron, e);
    }
  }

  public static boolean isDayLevelCron(String cron) {
    try {
      CronExpression expression = getCronExpression(cron);
      Date next = expression.getNextValidTimeAfter(new Date());
      Date secondNext = expression.getNextValidTimeAfter(next);
      long interval = secondNext.getTime() - next.getTime();
      return interval >= 24 * 60 * 60 * 1000;
    } catch (Exception e) {
      return false;
    }
  }
}
