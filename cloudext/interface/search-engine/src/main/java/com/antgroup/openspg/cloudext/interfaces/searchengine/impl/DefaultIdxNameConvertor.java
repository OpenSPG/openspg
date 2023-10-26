/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.cloudext.interfaces.searchengine.impl;

import com.antgroup.openspg.cloudext.interfaces.searchengine.IdxNameConvertor;


public class DefaultIdxNameConvertor implements IdxNameConvertor {

    private final static char UPPER_PREFIX = '$';

    @Override
    public String convertIdxName(String idxName) {
        StringBuilder result = new StringBuilder();

        int i = 0;
        while (i < idxName.length()) {
            char ch = idxName.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append(UPPER_PREFIX).append(Character.toLowerCase(ch));
            } else if ('.' == ch) {
                result.append("-");
            } else {
                result.append(ch);
            }
            i++;
        }
        return result.toString();
    }

    @Override
    public String restoreIdxName(String idxName) {
        StringBuilder result = new StringBuilder();

        int i = 0;
        while (i < idxName.length()) {
            char ch = idxName.charAt(i);
            if (UPPER_PREFIX == ch) {
                int j = i + 1;
                if (j < idxName.length() && Character.isLowerCase(idxName.charAt(j))) {
                    result.append(Character.toUpperCase(idxName.charAt(j)));
                } else {
                    throw new IllegalArgumentException("illegal idxName=" + idxName);
                }
                i++;
            } else if ('-' == ch) {
                result.append(".");
            } else {
                result.append(ch);
            }
            i++;
        }
        return result.toString();
    }
}
