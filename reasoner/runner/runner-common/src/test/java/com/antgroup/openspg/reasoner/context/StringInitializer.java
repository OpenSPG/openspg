/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.context;

import org.junit.Assert;


public class StringInitializer extends BaseContextInitializer<String> {

    private int count = 0;

    @Override
    public String initOnDriver() {
        return String.valueOf(count++);
    }

    @Override
    public void dispatchToWorker(String obj) {
        Assert.assertEquals(obj, "0");
    }
}