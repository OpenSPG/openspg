/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner;

import com.alibaba.fastjson.JSON;
import com.antgroup.openspg.reasoner.io.model.AbstractTableInfo;
import com.antgroup.openspg.reasoner.io.model.OdpsTableInfo;
import com.antgroup.openspg.reasoner.progress.DecryptUtils;
import com.antgroup.openspg.reasoner.runner.ConfigKey;
import com.antgroup.openspg.reasoner.sink.KgReasonerSinkType;
import com.antgroup.openspg.reasoner.sink.KgReasonerSinkUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author donghai.ydh
 * @version SinkUtilsTest.java, v 0.1 2023年05月09日 17:38 donghai.ydh
 */
public class SinkUtilsTest {

    @Test
    public void defaultConfigTest() {
        Map<String, Object> params = new HashMap<>();
        Assert.assertEquals(KgReasonerSinkType.LOG, KgReasonerSinkUtils.getKgReasonerSinkType(params));
    }

    @Test
    public void odpsConfigTest() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> outputConfigMap = new HashMap<>();
        outputConfigMap.put("type", "ODPS");

        Map<String, Object> odpsConfigMap = new HashMap<>();
        outputConfigMap.put("ODPS", Lists.newArrayList(odpsConfigMap));
        odpsConfigMap.put("project", "project1");
        odpsConfigMap.put("table", "table1");
        odpsConfigMap.put("accessId", "accessId1");
        odpsConfigMap.put("accessKey", DecryptUtils.encryptAccessInfo("accessKey1"));
        odpsConfigMap.put("endPoint", "endPoint1");
        odpsConfigMap.put("tunnelEndpoint", "tunnelEndpoint1");

        Map<String, Object> partitionMap = new HashMap<>();
        partitionMap.put("dt", "20230505");
        partitionMap.put("hh", "11");
        outputConfigMap.put("partition", partitionMap);

        params.put(ConfigKey.KG_REASONER_OUTPUT_TABLE_CONFIG, outputConfigMap);

        params = JSON.parseObject(JSON.toJSONString(params));

        AbstractTableInfo tableInfo = KgReasonerSinkUtils.getSinkTableInfo(params);
        Assert.assertNotNull(tableInfo);
        OdpsTableInfo odpsTableInfo = (OdpsTableInfo) tableInfo;
        Assert.assertEquals(odpsTableInfo.getProject(), "project1");
        Assert.assertEquals(odpsTableInfo.getTable(), "table1");
        Assert.assertEquals(odpsTableInfo.getAccessID(), "accessId1");
        Assert.assertEquals(odpsTableInfo.getAccessKey(), "accessKey1");
        Assert.assertEquals(odpsTableInfo.getEndPoint(), "endPoint1");
        Assert.assertEquals(odpsTableInfo.getTunnelEndPoint(), "tunnelEndpoint1");

        Assert.assertEquals(odpsTableInfo.getPartition().get("dt"), "20230505");
        Assert.assertEquals(odpsTableInfo.getPartition().get("hh"), "11");
    }

    @Test
    public void testHashSetClone() {
        HashSet<String> set = Sets.newHashSet("a", "b", "c");
        HashSet<String> set2 = (HashSet<String>) set.clone();
        set.remove("a");
        Assert.assertEquals(2, set.size());
        Assert.assertEquals(3, set2.size());
    }
}