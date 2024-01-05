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

package com.antgroup.openspg.reasoner.sink;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.reasoner.io.model.AbstractTableInfo;
import com.antgroup.openspg.reasoner.io.model.HiveTableInfo;
import com.antgroup.openspg.reasoner.runner.ConfigKey;
import java.util.Map;

public class KgReasonerSinkUtils {

  /** get sink type from config params */
  public static KgReasonerSinkType getKgReasonerSinkType(Map<String, Object> params) {
    JSONObject outputTableConfig = getOutputTableConfig(params);
    if (null == outputTableConfig) {
      return KgReasonerSinkType.LOG;
    }
    String outputType = outputTableConfig.getString("type");
    return KgReasonerSinkType.valueOf(outputType);
  }

  /** get sink table info from config */
  public static AbstractTableInfo getSinkTableInfo(Map<String, Object> params) {
    KgReasonerSinkType sinkType = getKgReasonerSinkType(params);
    if (KgReasonerSinkType.HIVE.equals(sinkType)) {
      String tableInfoStr = String.valueOf(params.get(ConfigKey.KG_REASONER_SINK_TABLE_INFO));
      return JSON.parseObject(tableInfoStr, HiveTableInfo.class);
    }
    return null;
  }

  /** get write file */
  public static String getKgReasonerSinkFile(Map<String, Object> params) {
    return String.valueOf(params.get(ConfigKey.KG_REASONER_SINK_FILE));
  }

  private static JSONObject getOutputTableConfig(Map<String, Object> params) {
    JSONObject outputTableConfig;
    Object outputTableConfigObj = params.get(ConfigKey.KG_REASONER_OUTPUT_TABLE_CONFIG);
    if (null == outputTableConfigObj) {
      return null;
    }
    if (outputTableConfigObj instanceof String) {
      outputTableConfig = JSON.parseObject(String.valueOf(outputTableConfigObj));
    } else {
      outputTableConfig = (JSONObject) outputTableConfigObj;
    }
    return outputTableConfig;
  }
}
