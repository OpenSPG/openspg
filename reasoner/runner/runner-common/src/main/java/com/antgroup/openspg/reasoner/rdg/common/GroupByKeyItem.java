/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.rdg.common;

import java.util.Arrays;


public class GroupByKeyItem {
    private final Object[] keys;

    public GroupByKeyItem(Object[] keys) {
        this.keys = keys;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(keys);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GroupByKeyItem)) {
            return false;
        }
        GroupByKeyItem other = (GroupByKeyItem) obj;
        return Arrays.equals(this.keys, other.keys);
    }
}