/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.context;

import org.junit.Assert;


public class ConfigCheckInitializer extends BaseContextInitializer<String> {

    @Override
    public String initOnDriver() {
        Assert.assertEquals(this.taskRecord.getParams().get("config1"), "1");
        Assert.assertEquals(this.taskRecord.getParams().get("config2"), "2");
        return "";
    }

    @Override
    public void dispatchToWorker(String obj) {
        Assert.assertEquals(this.taskRecord.getParams().get("config1"), "1");
        Assert.assertEquals(this.taskRecord.getParams().get("config2"), "2");
    }
}
