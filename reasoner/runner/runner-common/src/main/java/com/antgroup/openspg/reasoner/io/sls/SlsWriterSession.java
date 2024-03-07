/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.io.sls;

import com.antgroup.openspg.reasoner.io.model.SLSTableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlsWriterSession {
    private static final Logger log = LoggerFactory.getLogger(SlsWriterSession.class);
    private final SLSTableInfo slsTableInfo;

    public SlsWriterSession(SLSTableInfo slsTableInfo) {
        this.slsTableInfo = slsTableInfo;
    }

    public String getSessionId() {
        return String.valueOf(this.slsTableInfo.hashCode());
    }

}