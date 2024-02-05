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

package com.antgroup.openspg.reasoner.common.utils;

import static org.junit.Assert.assertTrue;

import com.antgroup.openspg.reasoner.common.Utils;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.property.IVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.EdgeProperty;
import com.antgroup.openspg.reasoner.common.graph.property.impl.VertexVersionProperty;
import com.antgroup.openspg.reasoner.common.graph.propertybuild.impl.BasePropertyBuilder;
import org.junit.Assert;
import org.junit.Test;

public class PropertyUtilTest {
  @Test
  public void test2Str() {
    Double i = 11341324.0;
    String out = Utils.objValue2Str(i);
    System.out.println(out);
    Assert.assertTrue(!out.contains("E"));
  }

  @Test
  public void testBuildVertexProperty() {
    PropertyUtil.useBinaryProperty = false;
    PropertyUtil.propertyBuilder = new BasePropertyBuilder();
    IVersionProperty versionProperty = PropertyUtil.buildVertexProperty(null, null);
    assertTrue(versionProperty instanceof VertexVersionProperty);
  }

  @Test
  public void testBuildEdgeProperty() {
    PropertyUtil.useBinaryProperty = false;
    PropertyUtil.propertyBuilder = new BasePropertyBuilder();
    IProperty property = PropertyUtil.buildEdgeProperty(null, null);
    assertTrue(property instanceof EdgeProperty);
    EdgeProperty edgeProperty = new EdgeProperty(property);
    assertTrue(edgeProperty.getSize() == 0);
  }
}
