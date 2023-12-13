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
package com.antgroup.openspg.reasoner.progress;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;


public class FileUtil {
    public final static String LOCAL_FILE_PREFIX = "file://";

    /**
     * @param path
     * @return
     */
    public static String getFile(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        if (path.startsWith(LOCAL_FILE_PREFIX)) {
            try {
                return getFileFromLocalFS(path);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return getFileFromOSS(path);
        }
    }

    public static String getFileFromLocalFS(String filePath) throws Exception {
        String file = filePath.substring(LOCAL_FILE_PREFIX.length());
        FileInputStream inputStream = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            sb.append(line).append("\n");
            line = reader.readLine();
        }
        return sb.toString();
    }

    public static String getFileFromOSS(String key) {
        return OSSClientHelper.getInstance().getFileContent(key);
    }
}