package com.antgroup.openspgapp.arks.sofaboot;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.server.biz.common.ConfigManager;
import com.antgroup.openspg.server.common.model.config.Config;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspg.server.common.service.spring.SpringContextHolder;
import java.lang.reflect.Field;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

@Component
/* loaded from: DatabaseEnvironmentPostProcessor.class */
public class DatabaseEnvironmentPostProcessor {
  private static final Logger log = LoggerFactory.getLogger(DatabaseEnvironmentPostProcessor.class);

  @Autowired private ConfigManager configManager;

  @Autowired private ConfigurableEnvironment environment;

  @Autowired private DefaultValue defaultValue;

  @EventListener({ApplicationReadyEvent.class})
  public void loadDatabaseProperties() {
    log.info("getGraphStoreUrl:" + this.defaultValue.getGraphStoreUrl());
    Map<String, Object> dbProperties = getPropertiesFromDatabase();
    MapPropertySource propertySource = new MapPropertySource("databaseProperties", dbProperties);
    log.info("databaseProperties:" + JSONObject.toJSONString(dbProperties));
    this.environment.getPropertySources().addFirst(propertySource);
    refreshValues(this.environment);
    log.info("getGraphStoreUrl:" + this.defaultValue.getGraphStoreUrl());
  }

  private Map<String, Object> getPropertiesFromDatabase() {
    Config kagConfig = this.configManager.query("APPLICATION_PROPERTIES", "1");
    if (kagConfig == null) {
      return new JSONObject();
    }
    JSONObject json = JSONObject.parseObject(kagConfig.getConfig());
    return json;
  }

  public void refreshValues(ConfigurableEnvironment environment) {
    String[] beanNames = SpringContextHolder.getBeanDefinitionNames();
    for (String beanName : beanNames) {
      try {
        Object bean = SpringContextHolder.getBean(beanName);
        Class<?> beanClass = bean.getClass();
        for (Field field : beanClass.getDeclaredFields()) {
          if (field.isAnnotationPresent(Value.class)) {
            Value valueAnnotation = field.getAnnotation(Value.class);
            String valueExpression = valueAnnotation.value();
            String resolvedValue = environment.resolvePlaceholders(valueExpression);
            field.setAccessible(true);
            try {
              Object convertedValue = convertToFieldType(resolvedValue, field.getType());
              field.set(bean, convertedValue);
            } catch (IllegalAccessException e) {
              throw new RuntimeException(
                  "Failed to update field " + field.getName() + " in bean " + beanName, e);
            }
          }
        }
      } catch (Exception e2) {
        log.warn("set environment exception beanName:" + beanName, e2);
      }
    }
  }

  private Object convertToFieldType(String value, Class<?> targetType) {
    if (targetType == String.class) {
      return value;
    }
    if (targetType == Integer.class || targetType == Integer.TYPE) {
      return Integer.valueOf(Integer.parseInt(value));
    }
    if (targetType == Long.class || targetType == Long.TYPE) {
      return Long.valueOf(Long.parseLong(value));
    }
    if (targetType == Double.class || targetType == Double.TYPE) {
      return Double.valueOf(Double.parseDouble(value));
    }
    if (targetType == Boolean.class || targetType == Boolean.TYPE) {
      return Boolean.valueOf(Boolean.parseBoolean(value));
    }
    throw new IllegalArgumentException("Unsupported field type: " + targetType.getName());
  }
}
