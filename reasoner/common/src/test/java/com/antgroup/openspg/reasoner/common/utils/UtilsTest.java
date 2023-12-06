/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common.utils;

import com.antgroup.openspg.reasoner.common.Utils;
import com.antgroup.openspg.reasoner.common.table.Field;
import com.antgroup.openspg.reasoner.common.table.FieldType;
import com.antgroup.openspg.reasoner.common.types.KTBoolean$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author donghai.ydh
 * @version UtilsTest.java, v 0.1 2023年10月20日 16:28 donghai.ydh
 */
public class UtilsTest {
  @Test
  public void testGetResultTableColumns() {
    java.util.List<String> asList = Lists.newArrayList("A.id", "b.t.id", "XX123");
    List<KgType> typeList = Lists.newArrayList(KTString$.MODULE$, KTBoolean$.MODULE$);
    List<Field> result = Utils.getResultTableColumns(asList, typeList);
    Assert.assertEquals(3, result.size());
    Assert.assertEquals(result.get(0).getName(), "a_id");
    Assert.assertEquals(result.get(0).getType(), FieldType.STRING);
    Assert.assertEquals(result.get(1).getName(), "b_t_id");
    Assert.assertEquals(result.get(1).getType(), FieldType.BOOLEAN);
    Assert.assertEquals(result.get(2).getName(), "xx123");
    Assert.assertEquals(result.get(2).getType(), FieldType.UNKNOWN);
  }
}
