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

package com.antgroup.openspg.reasoner.udf.model;

import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.UdfMngFactory;
import com.antgroup.openspg.reasoner.udf.utils.UdfUtils;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class LazyUdaf {

  protected final String name;
  protected final Object[] udfInitParams;

  protected UdafMeta udafMeta = null;
  protected BaseUdaf baseUdaf = null;

  public LazyUdaf(String name, Object[] udfInitParams) {
    this.name = name;
    this.udfInitParams = udfInitParams;
    List<UdafMeta> udafMetas = UdfMngFactory.getUdfMng().getUdafMetas(this.name);
    if (CollectionUtils.isEmpty(udafMetas)) {
      throw new RuntimeException("unsupported aggregator function, type=" + this.name);
    } else if (1 == udafMetas.size()) {
      this.udafMeta = udafMetas.get(0);
    }
  }

  public String getName() {
    return name;
  }

  public Object[] getUdfInitParams() {
    return udfInitParams;
  }

  public UdafMeta getUdafMeta() {
    return udafMeta;
  }

  public BaseUdaf getBaseUdaf() {
    if (null == baseUdaf) {
      createBaseUdaf(KTString$.MODULE$);
    }
    return baseUdaf;
  }

  public void reset() {
    this.baseUdaf = null;
  }

  public void update(Object row) {
    if (null == this.baseUdaf) {
      KgType inputParamType;
      try {
        List<KgType> inputParamTypeList = UdfUtils.getParamTypeList(row);
        inputParamType = inputParamTypeList.get(0);
      } catch (Throwable e) {
        inputParamType = KTObject$.MODULE$;
      }
      createBaseUdaf(inputParamType);
    }
    this.baseUdaf.update(row);
  }

  private void createBaseUdaf(KgType kgType) {
    if (null == this.udafMeta) {
      this.udafMeta = UdfMngFactory.getUdfMng().getUdafMeta(this.name, kgType);
    }
    if (null == this.baseUdaf) {
      this.baseUdaf = this.udafMeta.createAggregateFunction();
      if (null != this.udfInitParams) {
        this.baseUdaf.initialize(this.udfInitParams);
      }
    }
  }

  public Object evaluate() {
    return this.baseUdaf.evaluate();
  }
}
