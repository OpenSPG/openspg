/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.context;

import org.junit.Assert;

/**
 * @author donghai.ydh
 * @version ConfigCheckInitializer.java, v 0.1 2023年07月12日 21:37 donghai.ydh
 */
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
