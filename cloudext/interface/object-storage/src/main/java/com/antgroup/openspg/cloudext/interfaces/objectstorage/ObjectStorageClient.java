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

package com.antgroup.openspg.cloudext.interfaces.objectstorage;

import com.antgroup.openspg.common.util.cloudext.CloudExtClient;
import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

public interface ObjectStorageClient extends CloudExtClient {

  Boolean saveData(String bucketName, byte[] data, String fileKey);

  byte[] getData(String bucketName, String fileKey);

  Boolean saveString(String bucketName, String text, String fileKey);

  String getString(String bucketName, String fileKey);

  Boolean saveFile(String bucketName, File file, String fileKey);

  Boolean saveFile(String bucketName, InputStream inputStream, long fileSize, String fileKey);

  public InputStream downloadFile(String bucketName, String fileKey);

  Boolean downloadFile(String bucketName, String fileKey, String directoryPath);

  String getUrl(String bucketName, String fileKey, Date expiration);

  String getUrlWithoutExpiration(String bucketName, String fileKey);

  Boolean removeObject(String bucketName, String fileKey);

  Boolean removeDirectory(String bucketName, String directoryPath);

  Boolean isDirectory(String bucketName, String path);

  List<String> getAllFilesRecursively(String bucketName, String directoryPath);

  Long getContentLength(String bucketName, String objectName);

  long getStorageSize(String bucketName, String path);
}
