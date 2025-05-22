package com.antgroup.openspgapp.api.http.server.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.neo4j.Neo4jDriverManager;
import com.antgroup.openspg.server.api.facade.dto.common.request.ConfigRequest;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.ConfigManager;
import com.antgroup.openspg.server.biz.common.PermissionManager;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.common.model.config.Config;
import com.antgroup.openspg.server.common.model.exception.IllegalParamsException;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspgapp.api.http.server.BaseController;
import com.antgroup.openspgapp.common.util.enums.CloudSiteEnum;
import com.antgroup.openspgapp.core.reasoner.service.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping({"v1/configs"})
@RestController
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/config/ConfigController.class */
public class ConfigController extends BaseController {

  @Autowired private ConfigManager configManager;

  @Autowired private PermissionManager permissionManager;

  @Autowired private DefaultValue defaultValue;

  @Value("${env:ant}")
  private String env;

  private static final String SYSTEM_CONFIG = "SYSTEM_CONFIG";

  @GetMapping({"/{configId}/version/{version}"})
  @ResponseBody
  public HttpResult<Config> getConfigListByCondition(
      @PathVariable final String configId, @PathVariable final String version) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Config>() { // from class:
          // com.antgroup.openspgapp.api.http.server.config.ConfigController.1
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("configId", configId);
            AssertUtils.assertParamObjectIsNotNull("version", version);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Config action() {
            Config config = ConfigController.this.configManager.query(configId, version);
            if (null != config) {
              if (!StringUtils.equals(CloudSiteEnum.PUBLIC.getValue(), ConfigController.this.env)) {
                if (StringUtils.equals(CloudSiteEnum.ANT.getValue(), ConfigController.this.env)) {
                  config.setShowProfilePicture(true);
                  config.setShowUserConfig(false);
                }
              } else {
                config.setShowProfilePicture(false);
                config.setShowUserConfig(true);
              }
              if (StringUtils.equals(config.getConfigId(), "KAG_CONFIG")) {
                JSONObject configJson = JSON.parseObject(config.getConfig());
                if (configJson != null) {
                  ConfigController.this.configManager.backwardCompatible(configJson);
                  config.setConfig(
                      ConfigController.this.configManager.setApiKeyDesensitization(
                          JSON.toJSONString(configJson)));
                } else {
                  return config;
                }
              }
            }
            return config;
          }
        });
  }

  @PostMapping
  @ResponseBody
  public HttpResult<Integer> create(@RequestBody final ConfigRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Integer>() { // from class:
          // com.antgroup.openspgapp.api.http.server.config.ConfigController.2
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("Config", request);
            AssertUtils.assertParamObjectIsNotNull("configId", request.getConfig());
            AssertUtils.assertParamObjectIsNotNull("version", request.getVersion());
            ConfigController.this.assertCurrentUserIsSuper();
            JSONObject configJson = JSON.parseObject(request.getConfig());
            ConfigController.this.configManager.backwardCompatible(configJson);
            ConfigController.this.configManager.generateLLMIdCompletionLLM(configJson);
            request.setConfig(JSON.toJSONString(configJson));
            ConfigController.this.checkGraphStore(configJson);
            Utils.checkVectorizer(ConfigController.this.defaultValue, configJson);
            if (configJson.containsKey("llm")) {
              Utils.checkLLM(ConfigController.this.defaultValue, configJson);
            }
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Integer action() {
            Integer count = ConfigController.this.configManager.create(request);
            return count;
          }
        });
  }

  @PutMapping({"/{id}"})
  @ResponseBody
  public HttpResult<Integer> update(
      @PathVariable final Long id, @RequestBody final ConfigRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Integer>() { // from class:
          // com.antgroup.openspgapp.api.http.server.config.ConfigController.3
          public void check() {
            JSONObject oldLLM;
            AssertUtils.assertParamObjectIsNotNull("Config", request);
            AssertUtils.assertParamObjectIsNotNull("id", request.getId());
            Config config = ConfigController.this.configManager.getById(id);
            AssertUtils.assertParamObjectIsNotNull("config", config);
            if (!StringUtils.equals(config.getResourceType(), ConfigController.SYSTEM_CONFIG)) {
              ConfigController.this.assertCurrentUserIsSuper();
              JSONObject oldConfig = JSON.parseObject(config.getConfig());
              boolean unifiedLLMId = false;
              if (oldConfig != null
                  && (oldLLM = oldConfig.getJSONObject("llm")) != null
                  && StringUtils.isBlank(oldLLM.getString("llm_id"))) {
                unifiedLLMId = true;
              }
              ConfigController.this.configManager.backwardCompatible(oldConfig);
              ConfigController.this.configManager.generateLLMIdCompletionLLM(oldConfig);
              String oldLlmId = ConfigController.this.configManager.getLLMIdByConfig(oldConfig);
              JSONObject configJson = JSON.parseObject(request.getConfig());
              ConfigController.this.configManager.handleApiKey(configJson, config.getConfig());
              ConfigController.this.configManager.backwardCompatible(configJson);
              ConfigController.this.configManager.generateLLMIdCompletionLLM(configJson);
              request.setConfig(JSON.toJSONString(configJson));
              ConfigController.this.checkGraphStore(configJson);
              Utils.checkVectorizer(ConfigController.this.defaultValue, configJson);
              String llmId = ConfigController.this.configManager.getLLMIdByConfig(configJson);
              if (unifiedLLMId) {
                oldLlmId = llmId;
              }
              if (configJson.containsKey("llm")) {
                if (!StringUtils.equals(oldLlmId, llmId)
                    || ConfigController.this.configManager.isLLMChange(oldConfig, configJson)) {
                  Utils.checkLLM(ConfigController.this.defaultValue, configJson);
                  return;
                }
                return;
              }
              return;
            }
            throw new IllegalParamsException("system config can not be updated", new Object[0]);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Integer action() {
            Integer count = ConfigController.this.configManager.update(request);
            return count;
          }
        });
  }

  /* JADX INFO: Access modifiers changed from: private */
  public void checkGraphStore(JSONObject config) {
    JSONObject graphStore = config.getJSONObject("graph_store");
    AssertUtils.assertParamObjectIsNotNull("graphStore", graphStore);
    String uri = graphStore.getString("uri");
    AssertUtils.assertParamObjectIsNotNull("uri", uri);
    String user = graphStore.getString("user");
    AssertUtils.assertParamObjectIsNotNull("user", user);
    String password = graphStore.getString("password");
    AssertUtils.assertParamObjectIsNotNull("password", password);
    Neo4jDriverManager.getNeo4jDriver(uri, user, password);
  }

  public String getEnv() {
    return this.env;
  }

  public void setEnv(String env) {
    this.env = env;
  }

  /* JADX INFO: Access modifiers changed from: private */
  public void assertCurrentUserIsSuper() {
    String userNo = getLoginAccount().getWorkNo();
    if (!this.permissionManager.isSuper(userNo)) {
      throw new IllegalParamsException("only super administrator operation", new Object[0]);
    }
  }
}
