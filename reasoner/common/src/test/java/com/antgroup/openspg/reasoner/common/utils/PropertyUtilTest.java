/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
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

/**
 * @author kejian
 * @version PropertyUtilTest.java, v 0.1 2023年05月30日 3:54 PM kejian
 */
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
