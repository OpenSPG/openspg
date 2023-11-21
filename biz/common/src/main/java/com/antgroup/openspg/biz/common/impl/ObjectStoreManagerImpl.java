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

package com.antgroup.openspg.biz.common.impl;

import com.antgroup.openspg.api.facade.dto.common.request.ObjectStoreRequest;
import com.antgroup.openspg.api.facade.dto.common.response.ObjectStoreResponse;
import com.antgroup.openspg.biz.common.ObjectStoreManager;
import com.antgroup.openspg.cloudext.interfaces.objectstore.ObjectStoreClient;
import com.antgroup.openspg.cloudext.interfaces.objectstore.cmd.ObjectStoreSaveCmd;
import com.antgroup.openspg.cloudext.interfaces.objectstore.model.ObjectStorePath;
import com.antgroup.openspg.common.service.datasource.DataSourceService;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObjectStoreManagerImpl implements ObjectStoreManager {

  @Autowired private DataSourceService dataSourceService;

  @Override
  public ObjectStoreResponse objectStore(ObjectStoreRequest request, InputStream file) {
    ObjectStoreClient objectStoreClient = dataSourceService.buildSharedFileStoreClient();

    ObjectStorePath filePath =
        objectStoreClient.save(
            new ObjectStoreSaveCmd(new ObjectStorePath(request.getName()), file));
    return new ObjectStoreResponse()
        .setRelativePath(filePath.getRelativePath())
        .setAbsolutePath(filePath.getAbsolutePath());
  }
}
