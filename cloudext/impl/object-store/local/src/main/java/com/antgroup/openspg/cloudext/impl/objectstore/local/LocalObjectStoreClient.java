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

package com.antgroup.openspg.cloudext.impl.objectstore.local;

import com.antgroup.openspg.cloudext.interfaces.objectstore.ObjectStoreClient;
import com.antgroup.openspg.cloudext.interfaces.objectstore.cmd.ObjectStoreSaveCmd;
import com.antgroup.openspg.cloudext.interfaces.objectstore.model.ObjectStorePath;
import com.antgroup.openspg.server.common.model.datasource.connection.ObjectStoreConnectionInfo;
import java.io.File;
import java.io.IOException;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

public class LocalObjectStoreClient implements ObjectStoreClient {

  @Getter private final ObjectStoreConnectionInfo connInfo;
  private final String localRootDir;

  public LocalObjectStoreClient(ObjectStoreConnectionInfo connInfo) {
    this.connInfo = connInfo;
    this.localRootDir = (String) connInfo.getNotNullParam("localDir");
  }

  @Override
  public ObjectStorePath save(ObjectStoreSaveCmd cmd) {
    ObjectStorePath path = cmd.getPath();
    File file = new File("./" + localRootDir + "/" + path.getRelativePath());
    try {
      FileUtils.copyInputStreamToFile(cmd.getInputStream(), file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    path.setAbsolutePath(file.getAbsolutePath());
    return path;
  }
}
