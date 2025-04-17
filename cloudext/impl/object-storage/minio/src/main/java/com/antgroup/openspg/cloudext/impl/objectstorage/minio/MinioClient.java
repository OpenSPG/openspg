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
import com.antgroup.openspg.common.util.StringUtils;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.SetBucketPolicyArgs;
import io.minio.StatObjectArgs;
import io.minio.http.Method;
import io.minio.messages.Item;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class MinioClient implements ObjectStorageClient {

  private final io.minio.MinioClient minioClient;

  @Getter private final String connUrl;

  public MinioClient(String connUrl) {
    this.connUrl = connUrl;
    this.minioClient = initMinioClient(UriComponentsBuilder.fromUriString(connUrl).build());
  }

  private io.minio.MinioClient initMinioClient(UriComponents uriComponents) {
    String scheme = uriComponents.getQueryParams().getFirst(MinioConstants.SCHEME);
    scheme = StringUtils.isBlank(scheme) ? "http" : scheme;
    String endpoint = String.format("%s://%s", scheme, uriComponents.getHost());
    if (uriComponents.getPort() > 0) {
      endpoint = String.format("%s:%s", endpoint, uriComponents.getPort());
    }
    String accessKey = uriComponents.getQueryParams().getFirst(MinioConstants.ACCESS_KEY);
    String secretKey = uriComponents.getQueryParams().getFirst(MinioConstants.SECRET_KEY);
    io.minio.MinioClient client =
        io.minio.MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
    return client;
  }

  @Override
  public Boolean saveData(String bucketName, byte[] data, String fileKey) {
    ByteArrayInputStream inputStream = null;
    try {
      inputStream = new ByteArrayInputStream(data);
      makeBucket(bucketName);
      PutObjectArgs putObjectArgs =
          PutObjectArgs.builder().bucket(bucketName).object(fileKey).stream(
                  inputStream, data.length, -1)
              .contentType("application/octet-stream")
              .build();
      minioClient.putObject(putObjectArgs);
      return true;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("minio saveData Exception:" + e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  @Override
  public byte[] getData(String bucketName, String fileKey) {
    try {
      GetObjectArgs getObjectArgs =
          GetObjectArgs.builder().bucket(bucketName).object(fileKey).build();
      return inputStreamToByteArray(minioClient.getObject(getObjectArgs));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("minio getData Exception:" + e.getMessage(), e);
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
      return new String(data, StandardCharsets.UTF_8);
    } else {
      throw new RuntimeException("Data not found.");
    }
  }

  @Override
  public Boolean saveFile(String bucketName, File file, String fileKey) {
    try {
      InputStream inputStream = new FileInputStream(file);
      return saveFile(bucketName, inputStream, file.length(), fileKey);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("minio saveFile Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public Boolean saveFile(
      String bucketName, InputStream inputStream, long fileSize, String fileKey) {
    try {
      makeBucket(bucketName);
      PutObjectArgs putObjectArgs =
          PutObjectArgs.builder().bucket(bucketName).object(fileKey).stream(
                  inputStream, fileSize, -1)
              .contentType("application/octet-stream")
              .build();
      minioClient.putObject(putObjectArgs);
      return true;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("minio saveFile inputStream Exception:" + e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  @Override
  public InputStream downloadFile(String bucketName, String fileKey) {
    try {
      return minioClient.getObject(
          GetObjectArgs.builder().bucket(bucketName).object(fileKey).build());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("minio getObject Exception:" + e.getMessage(), e);
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
      throw new RuntimeException("minio downloadFile Exception:" + e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(stream);
      IOUtils.closeQuietly(outputStream);
    }
  }

  @Override
  public String getUrl(String bucketName, String fileKey, Date expiration) {
    try {
      GetPresignedObjectUrlArgs args =
          GetPresignedObjectUrlArgs.builder()
              .method(Method.GET)
              .bucket(bucketName)
              .object(fileKey)
              .expiry((int) (expiration.getTime() - System.currentTimeMillis()) / 1000)
              .build();
      return minioClient.getPresignedObjectUrl(args);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("minio getUrl Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public String getUrlWithoutExpiration(String bucketName, String fileKey) {
    try {
      GetPresignedObjectUrlArgs args =
          GetPresignedObjectUrlArgs.builder()
              .method(Method.GET)
              .bucket(bucketName)
              .object(fileKey)
              .build();
      String baseUrl = minioClient.getPresignedObjectUrl(args);
      URI uri = new URI(baseUrl);
      String publicUrl =
          uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort() + uri.getPath();
      return publicUrl;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("minio getUrlWithoutExpiration Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public Boolean removeObject(String bucketName, String fileKey) {
    try {
      RemoveObjectArgs removeObjectArgs =
          RemoveObjectArgs.builder().bucket(bucketName).object(fileKey).build();
      minioClient.removeObject(removeObjectArgs);
      return true;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("minio removeObject Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public Boolean removeDirectory(String bucketName, String directoryPath) {
    try {
      Iterable<Result<Item>> objects =
          minioClient.listObjects(
              ListObjectsArgs.builder()
                  .bucket(bucketName)
                  .prefix(directoryPath)
                  .recursive(true)
                  .build());

      for (Result<Item> result : objects) {
        Item item = result.get();
        log.info("minio Deleting: " + item.objectName());
        if (item.objectName().startsWith(directoryPath)) {
          removeObject(bucketName, item.objectName());
        }
      }
      return true;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("minio removeDirectory Exception:" + e.getMessage(), e);
    }
  }

  @Override
  public Long getContentLength(String bucketName, String objectName) {
    try {
      makeBucket(bucketName);
      StatObjectArgs statObjectArgs =
          StatObjectArgs.builder().bucket(bucketName).object(objectName).build();
      return minioClient.statObject(statObjectArgs).size();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("minio getContentLength Exception:" + e.getMessage(), e);
    }
  }

  public void makeBucket(String bucketName) {
    try {
      boolean isExist =
          minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
      if (!isExist) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        setBucketPublic(bucketName);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("minio makeBucket Exception:" + e.getMessage(), e);
    }
  }

  public void setBucketPublic(String bucketName) {
    try {
      if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
      }

      String publicReadPolicy =
          "{\n"
              + "  \"Version\": \"2012-10-17\",\n"
              + "  \"Statement\": [\n"
              + "    {\n"
              + "      \"Effect\": \"Allow\",\n"
              + "      \"Principal\": \"*\",\n"
              + "      \"Action\": \"s3:GetObject\",\n"
              + "      \"Resource\": \"arn:aws:s3:::"
              + bucketName
              + "/*\"\n"
              + "    }\n"
              + "  ]\n"
              + "}";

      minioClient.setBucketPolicy(
          SetBucketPolicyArgs.builder().bucket(bucketName).config(publicReadPolicy).build());
    } catch (Exception e) {
      log.error("Error making bucket public: " + bucketName, e);
      throw new RuntimeException("Failed to set public access for bucket: " + bucketName, e);
    }
  }
}
