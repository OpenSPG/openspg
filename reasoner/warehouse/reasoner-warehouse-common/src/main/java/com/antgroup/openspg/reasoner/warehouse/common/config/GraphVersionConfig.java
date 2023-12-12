/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.warehouse.common.config;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class GraphVersionConfig implements Serializable {
    private final Long snapshotVersion;
    private final Long startVersion;
    private final Long endVersion;

    public GraphVersionConfig() {
        this.snapshotVersion = null;
        this.startVersion = 0L;
        this.endVersion = System.currentTimeMillis();
    }

    public GraphVersionConfig(Long snapshot, Long start, Long end) {
        this.snapshotVersion = snapshot;
        this.startVersion = start;
        this.endVersion = end;
    }

    public GraphVersionConfig(String configStr) {
        List<Long> versionList = new ArrayList<>(3);
        Splitter.on(",").split(configStr).forEach(s -> {
            s = s.trim();
            if (StringUtils.isEmpty(s) || "null".equals(s.toLowerCase(Locale.ROOT))) {
                versionList.add(null);
                return;
            }
            versionList.add(Long.parseLong(s));
        });
        Long snapshot = null;
        Long start = null;
        Long end = null;
        for (int i = 0; i < versionList.size(); ++i) {
            if (0 == i) {
                snapshot = versionList.get(i);
            } else if (1 == i) {
                start = versionList.get(i);
            } else if (2 == i) {
                end = versionList.get(i);
            }
        }
        this.snapshotVersion = snapshot;
        this.startVersion = start;
        this.endVersion = end;
    }

    /**
     * Getter method for property <tt>snapshotVersion</tt>.
     *
     * @return property value of snapshotVersion
     */
    public Long getSnapshotVersion() {
        return snapshotVersion;
    }

    /**
     * Getter method for property <tt>startVersion</tt>.
     *
     * @return property value of startVersion
     */
    public Long getStartVersion() {
        return startVersion;
    }

    /**
     * Getter method for property <tt>endVersion</tt>.
     *
     * @return property value of endVersion
     */
    public Long getEndVersion() {
        return endVersion;
    }
}