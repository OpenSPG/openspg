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

package com.antgroup.openspg.cloudext.impl.objectstorage.oss;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyuncs.utils.IOUtils;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClient;
import com.antgroup.openspg.common.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class OSSClient implements ObjectStorageClient {

  private final OSS ossClient;

  @Getter private final String connUrl;

  public OSSClient(String connUrl) {
    this.connUrl = connUrl;
    this.ossClient = initOSSClient(UriComponentsBuilder.fromUriString(connUrl).build());
  }

  private OSS initOSSClient(UriComponents uriComponents) {
    String scheme = uriComponents.getQueryParams().getFirst(OSSConstants.SCHEME);
    String endpoint = uriComponents.getHost();
    if (StringUtils.isNotBlank(scheme)) {
      endpoint = String.format("%s://%s", scheme, endpoint);
    }
    if (uriComponents.getPort() > 0) {
      endpoint = String.format("%s:%s", endpoint, uriComponents.getPort());
    }
    String accessKey = uriComponents.getQueryParams().getFirst(OSSConstants.ACCESS_KEY);
    String secretKey = uriComponents.getQueryParams().getFirst(OSSConstants.SECRET_KEY);
    String timout = uriComponents.getQueryParams().getFirst(OSSConstants.CONNECTION_TIMEOUT);
    if (StringUtils.isNotBlank(timout)) {
      ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
      configuration.setConnectionTimeout(Integer.valueOf(timout));
      return new OSSClientBuilder().build(endpoint, accessKey, secretKey, configuration);
    } else {
      return new OSSClientBuilder().build(endpoint, accessKey, secretKey);
    }
  }

  @Override
  public Boolean saveData(String bucketName, byte[] data, String fileKey) {
    try (InputStream inputStream = new ByteArrayInputStream(data)) {
      ossClient.putObject(bucketName, fileKey, inputStream);
      return true;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("OSS saveData Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public byte[] getData(String bucketName, String fileKey) {
    try (OSSObject ossObject = ossClient.getObject(bucketName, fileKey);
        InputStream inputStream = ossObject.getObjectContent()) {
      return inputStreamToByteArray(inputStream);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("OSS getData Exception:" + e.getMessage(), e);
    }
  }

  public static byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      byte[] buffer = new byte[1024];
      int length;
      while ((length = inputStream.read(buffer)) != -1) {
        byteArrayOutputStream.write(buffer, 0, length);
      }
      return byteArrayOutputStream.toByteArray();
    }
  }

  @Override
  public Boolean saveString(String bucketName, String text, String fileKey) {
    byte[] data = text.getBytes(StandardCharsets.UTF_8);
    return saveData(bucketName, data, fileKey);
  }

  @Override
  public String getString(String bucketName, String fileKey) {
    byte[] data = getData(bucketName, fileKey);
    if (data != null) {
      return new String(data);
    } else {
      throw new RuntimeException("Data not found.");
    }
  }

  @Override
  public Boolean saveFile(String bucketName, File file, String fileKey) {
    try {
      InputStream inputStream = new FileInputStream(file);
      return saveFile(bucketName, inputStream, file.length(), fileKey);
    } catch (FileNotFoundException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("OSS saveFile Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public Boolean saveFile(
      String bucketName, InputStream inputStream, long fileSize, String fileKey) {
    try {
      ossClient.putObject(bucketName, fileKey, inputStream);
      return true;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("OSS saveFile InputStream Exception:" + e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  @Override
  public InputStream downloadFile(String bucketName, String fileKey) {
    try {
      OSSObject ossObject = ossClient.getObject(bucketName, fileKey);
      return ossObject.getObjectContent();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("OSS getObject Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public Boolean downloadFile(String bucketName, String fileKey, String directoryPath) {
    InputStream stream = downloadFile(bucketName, fileKey);
    OutputStream outputStream = null;
    try {
      String filePathName = directoryPath + File.separator + new File(fileKey).getName();
      File file = new File(filePathName);
      if (!file.exists()) {
        file.getParentFile().mkdirs();
        file.createNewFile();
      }
      outputStream = new FileOutputStream(filePathName);
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = stream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      return true;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("OSS downloadFile Exception:" + e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(stream);
      IOUtils.closeQuietly(outputStream);
    }
  }

  @Override
  public String getUrl(String bucketName, String fileKey, Date expiration) {
    try {
      return ossClient.generatePresignedUrl(bucketName, fileKey, expiration).toString();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("OSS getUrl Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public String getUrlWithoutExpiration(String bucketName, String fileKey) {
    try {
      return ossClient
          .generatePresignedUrl(bucketName, fileKey, new Date(Long.MAX_VALUE))
          .toString();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("OSS getUrlWithoutExpiration Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public Boolean removeObject(String bucketName, String fileKey) {
    try {
      ossClient.deleteObject(bucketName, fileKey);
      return true;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("OSS removeObject Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public Boolean removeDirectory(String bucketName, String directoryPath) {
    try {
      ObjectListing objectListing;

      do {
        objectListing = ossClient.listObjects(bucketName, directoryPath);
        for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
          String objectName = objectSummary.getKey();
          log.info("OSS Deleting: " + objectName);
          if (objectName.startsWith(directoryPath)) {
            removeObject(bucketName, objectName);
          }
        }
        objectListing.getNextMarker();
      } while (objectListing.isTruncated());

      return true;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("OSS removeDirectory Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public Long getContentLength(String bucketName, String objectName) {
    try {
      ObjectMetadata metadata = ossClient.getObjectMetadata(bucketName, objectName);
      return metadata.getContentLength();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("OSS getContentLength Exception:" + e.getMessage(), e);
    }
  }
}
