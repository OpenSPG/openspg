package com.antgroup.openspg.builder.model.pipeline.config;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import com.google.common.collect.Lists;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;

@Getter
@AllArgsConstructor
public class OperatorConfig extends BaseValObj {

  private final String filePath;

  private final String modulePath;

  private final String className;

  private final String method;

  private final Map<String, String> params;

  @Getter(lazy = true)
  private final String uniqueKey = genUniqueKey();

  private String genUniqueKey() {
    return DigestUtils.md5Hex(
        String.join(
            ";",
            Lists.newArrayList(
                filePath, modulePath, className, method, JSON.toJSONString(params))));
  }
}
