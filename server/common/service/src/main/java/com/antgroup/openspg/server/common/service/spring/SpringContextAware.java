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

package com.antgroup.openspg.server.common.service.spring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;

public class SpringContextAware implements ApplicationContextAware {

  /** 应用上下文 */
  protected ApplicationContext applicationContext;

  /** 类初始化，子类可覆盖 */
  @EventListener(ApplicationStartedEvent.class)
  public void init() {}

  /**
   * 从应用上下文中获取beans
   *
   * @param tClass bean类
   * @param <C> 类型
   * @return 类实例列表
   */
  protected <C> List<C> getBeansOf(Class<C> tClass) {
    Collection<C> beans = applicationContext.getBeansOfType(tClass).values();
    return new ArrayList<>(beans);
  }

  /**
   * 从应用上下文中获取bean
   *
   * @param tClass bean类
   * @param <C> 类型
   * @return 类实例
   */
  protected <C> C getOnlyBeanOf(Class<C> tClass) {
    Collection<C> beans = applicationContext.getBeansOfType(tClass).values();
    if (CollectionUtils.isEmpty(beans) || beans.size() > 1) {
      throw new RuntimeException(String.format("bean of tClass=%s error", tClass.getName()));
    } else {
      return beans.stream().findFirst().get();
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
