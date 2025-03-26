/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.cloudext.impl.objectstorage.minio;

import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClient;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClientDriver;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClientDriverManager;

public class MinioClientDriver implements ObjectStorageClientDriver {

  static {
    ObjectStorageClientDriverManager.registerDriver(new MinioClientDriver());
  }

  @Override
  public String driverScheme() {
    return "minio";
  }

  @Override
  public ObjectStorageClient connect(String url) {
    return new MinioClient(url);
  }
}
