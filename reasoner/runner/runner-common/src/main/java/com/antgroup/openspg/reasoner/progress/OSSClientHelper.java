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

import com.alipay.cloudsdk.object.storage.api.ObjectStorageClient;
import com.alipay.cloudsdk.object.storage.api.ObjectStorageClientManager;
import com.alipay.cloudsdk.object.storage.config.ObjectStorageClientConfig;
import com.alipay.cloudsdk.object.storage.enums.ObjectStorageType;
import com.alipay.cloudsdk.object.storage.model.AtomicObject;
import com.alipay.cloudsdk.object.storage.model.DeleteObjectRequest;
import com.alipay.cloudsdk.object.storage.model.GetObjectRequest;
import com.alipay.cloudsdk.object.storage.model.GetObjectResponse;
import com.alipay.cloudsdk.object.storage.model.ListObjectsRequest;
import com.alipay.cloudsdk.object.storage.model.ListObjectsResponse;
import com.alipay.cloudsdk.object.storage.model.PutObjectRequest;
import com.alipay.cloudsdk.object.storage.model.PutObjectResponse;
import com.alipay.cloudsdk.result.SDKResult;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import scala.Tuple2;

/** @author donghai.ydh@alibaba-inc.com */
@Slf4j(topic = "userlogger")
public class OSSClientHelper implements Serializable {
  private transient ObjectStorageClient client;
  @Getter private String bucketName;

  /**
   * Get file content
   *
   * @param fileName
   * @return
   */
  public String getFileContent(String fileName) {
    try {
      InputStream objectContent = getFileInputStream(fileName);
      if (null == objectContent) {
        return null;
      }
      byte[] data = readStreamAsByteArray(objectContent);
      safeClose(objectContent);
      return new String(data);
    } catch (IOException e) {
      log.error("get oss file error", e);
    }
    return null;
  }

  private static byte[] readStreamAsByteArray(InputStream in) throws IOException {

    if (in == null) {
      return new byte[0];
    }

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int len = -1;
    while ((len = in.read(buffer)) != -1) {
      output.write(buffer, 0, len);
    }
    output.flush();
    return output.toByteArray();
  }

  private static void safeClose(InputStream inputStream) {
    if (inputStream != null) {
      try {
        inputStream.close();
      } catch (IOException e) {
        log.warn("close error", e);
      }
    }
  }

  /**
   * Get the input stream of file
   *
   * @param fileName
   * @return
   */
  public InputStream getFileInputStream(String fileName) {
    log.info("get_file_for_oss, file_name=" + fileName);

    GetObjectRequest request = new GetObjectRequest();
    request.setBucketName(this.bucketName);
    request.setObjectName(fileName);
    SDKResult<GetObjectResponse> getResult = client.getObject(request);
    if (!getResult.isSuccess()) {
      log.error("get object storage error, " + getResult.getErrorMsg() + ",request=" + getResult);
      return null;
    }
    GetObjectResponse response = getResult.getData();
    return response.getData();
  }

  /**
   * Write file to object storage
   *
   * @param fileName
   * @param content
   * @return
   */
  public boolean putFileContent(String fileName, String content) {
    InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    PutObjectRequest putRequest = new PutObjectRequest();
    putRequest.setBucketName(this.bucketName);
    putRequest.setContent(stream);
    putRequest.setObjectName(fileName);
    SDKResult<PutObjectResponse> putResult = client.putObject(putRequest);
    return putResult.isSuccess();
  }

  /**
   * Delete the file
   *
   * @param fileName
   */
  public void removeFile(String fileName) {
    DeleteObjectRequest deleteRequest = new DeleteObjectRequest();
    deleteRequest.setBucketName(this.bucketName);
    deleteRequest.setObjectName(fileName);
    client.deleteObject(deleteRequest);
  }

  /**
   * List files in the directory with key
   *
   * @param key
   * @return
   */
  public List<String> listChildren(String key) {
    return listChildren(key, null, null, null);
  }

  /**
   * List files in the directory with prefix and suffix
   *
   * @param prefix
   * @param suffix
   * @return
   */
  public List<String> listChildren(String prefix, String suffix) {
    Preconditions.checkArgument(!isEmpty(suffix));
    List<String> childrenList = listChildren(prefix, null, null, null);
    return childrenList.stream().filter(s -> s.endsWith(suffix)).collect(Collectors.toList());
  }

  /**
   * List files in the directory
   *
   * @param prefix
   * @param maker
   * @param delimiter
   * @param maxObjects
   * @return
   */
  public List<String> listChildren(
      String prefix, String maker, String delimiter, Integer maxObjects) {
    Tuple2<List<AtomicObject>, List<String>> resultTuple =
        listChildrenRow(prefix, maker, delimiter, maxObjects);
    ArrayList<String> arrayList = new ArrayList<>();
    resultTuple
        ._1()
        .forEach(
            (o) -> {
              arrayList.add(o.getObjectName());
            });
    arrayList.trimToSize();
    return arrayList;
  }

  /**
   * List the files and subdirectories in the directory.
   *
   * @param prefix
   * @param maker
   * @param delimiter
   * @param maxObjects
   * @return
   */
  public Tuple2<List<AtomicObject>, List<String>> listChildrenRow(
      String prefix, String maker, String delimiter, Integer maxObjects) {
    Preconditions.checkArgument(!isEmpty(prefix));
    ListObjectsRequest listRequest = new ListObjectsRequest();
    listRequest.setBucketName(this.bucketName);
    listRequest.setPrefix(prefix);
    if (!isEmpty(maker)) {
      listRequest.setMarker(maker);
    }
    if (!isEmpty(delimiter)) {
      listRequest.setDelimiter(delimiter);
    }
    if (null != maxObjects) {
      listRequest.setMaxObjects(maxObjects);
    }
    SDKResult<ListObjectsResponse> listResult = this.client.listObjects(listRequest);
    if (!listResult.isSuccess()) {
      log.error(
          "list object storage error, prefix="
              + prefix
              + ",maker="
              + maker
              + ",delimiter="
              + delimiter
              + ",maxObjects="
              + maxObjects
              + ",msg="
              + listResult.getErrorMsg());
      return null;
    }
    ListObjectsResponse response = listResult.getData();
    return new Tuple2<>(response.getObjects(), response.getCommonPrefixes());
  }

  private static volatile OSSClientHelper instance = null;

  private void init(Map<String, String> params) {
    ObjectStorageType type =
        ObjectStorageType.valueOf(
            String.valueOf(params.getOrDefault("type", ObjectStorageType.OSS.name())));
    String endpoint = "cn-hangzhou.alipay-internal.aliyun-inc.com";
    String region = String.valueOf(params.getOrDefault("region", endpoint));
    String accessKeyId = String.valueOf(params.getOrDefault("akId", "LTAIyCZQ14COZ08J"));
    String accessKeySecret =
        String.valueOf(
            params.getOrDefault("akSecret", "/C2/1dTfMmYElx9m0asGNSVdua7mB8HRP7Ti1lmUT9w="));
    String serviceHost = String.valueOf(params.getOrDefault("serviceHost", endpoint));
    // Decrypt the accessKeySecret
    accessKeySecret = DecryptUtils.decryptAccessInfo(accessKeySecret);
    this.bucketName = String.valueOf(params.getOrDefault("bucket", "alipay-solutions"));

    ObjectStorageClientConfig config = new ObjectStorageClientConfig();
    config.setRegion(region);
    config.setServiceHost(serviceHost);
    config.setAccessKeyId(accessKeyId);
    config.setAccessKeySecret(accessKeySecret);
    config.setConnectionTimeout(5 * 60);
    log.info(
        "create object storage client, type="
            + type.name()
            + ", conf="
            + config
            + ", bucket="
            + this.bucketName);
    client = ObjectStorageClientManager.createClient(config, type);
    if (null == client) {
      throw new RuntimeException("init oss client error");
    }
  }

  /**
   * Get the string by delimit
   *
   * @param fileKey
   * @param delimit
   * @return
   * @throws IOException
   */
  public String fetchStringByDelimit(String fileKey, String delimit) throws IOException {

    log.info("get_file_for_oss, file_name=" + fileKey);

    GetObjectRequest request = new GetObjectRequest();
    request.setBucketName(this.bucketName);
    request.setObjectName(fileKey);
    SDKResult<GetObjectResponse> getResult = client.getObject(request);
    if (!getResult.isSuccess()) {
      log.error("get object storage error, " + getResult.getErrorMsg());
      return null;
    }
    GetObjectResponse response = getResult.getData();
    InputStream objectContent = response.getData();

    InputStreamReader inputStreamReader =
        new InputStreamReader(objectContent, StandardCharsets.UTF_8);

    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    String line = bufferedReader.readLine();
    StringBuilder stringBuilder = new StringBuilder();
    while (line != null) {
      stringBuilder.append(line.trim()).append(delimit);
      line = bufferedReader.readLine();
    }

    return stringBuilder.toString();
  }

  /** Download data to a local file. */
  public String dumpToFile(String key, String dumpName) throws IOException {
    String localPath = System.getProperty("user.dir").concat("/").concat(dumpName);
    dumpToLocalFile(key, localPath);
    return localPath;
  }

  /** Download data to a local file. */
  public String dumpToLocalFile(String key, String localPath) throws IOException {

    GetObjectRequest request = new GetObjectRequest();
    request.setBucketName(this.bucketName);
    request.setObjectName(key);
    SDKResult<GetObjectResponse> getResult = client.getObject(request);
    if (!getResult.isSuccess()) {
      log.error("get object storage error, " + getResult.getErrorMsg());
      return null;
    }
    GetObjectResponse response = getResult.getData();
    InputStream is = getFileInputStream(key);
    if (is == null) {
      throw new FileNotFoundException("OssFile ".concat(key).concat(" not exist"));
    }

    // Get the current container running path
    File tm = new File(localPath);
    if (tm.exists()) {
      tm.delete();
    } else if (!tm.getParentFile().exists()) {
      tm.getParentFile().mkdirs();
    }

    log.info("dump data to file:".concat(localPath));
    File temp = new File(localPath);
    long contentLength = response.getContentLength();
    log.info("contentLength:" + contentLength + " bytes");
    float dumpRate = 0.1f;
    long dumpStart = System.currentTimeMillis();

    long dataSize = 0L;

    byte[] buffer = new byte[1024];
    int readBytes;

    OutputStream os = Files.newOutputStream(temp.toPath());
    StringBuilder logStr = new StringBuilder("content dump finished ");
    try {
      while ((readBytes = is.read(buffer)) != -1) {
        os.write(buffer, 0, readBytes);
        dataSize += 1024;
        if (dataSize / (contentLength + 0.1) > dumpRate) {
          long end = System.currentTimeMillis();
          DecimalFormat df = new DecimalFormat("0.00%");
          logStr.append("-----");
          log.info(
              logStr
                  + ">"
                  + df.format(dataSize / (contentLength + 0.1))
                  + " || time use(ms):"
                  + (end - dumpStart));

          dumpRate += 0.1;
        }
      }
    } finally {
      os.flush();
      os.close();
      is.close();
    }
    return localPath;
  }

  /**
   * Get the client
   *
   * @return
   */
  public ObjectStorageClient getClient() {
    return client;
  }

  /** Get singletons */
  public static OSSClientHelper getInstance() {
    if (null != instance) {
      return instance;
    }
    synchronized (OSSClientHelper.class) {
      if (instance == null) {
        OSSClientHelper instanceTmp = new OSSClientHelper();
        instanceTmp.init(new HashMap<>());
        instance = instanceTmp;
      }
    }
    return instance;
  }

  /** Initialize the oss configuration */
  public static void initInstance(String holmesOssConfigStr) {
    if (null != instance) {
      return;
    }
    synchronized (OSSClientHelper.class) {
      if (instance == null) {
        OSSClientHelper instanceTmp = new OSSClientHelper();
        Map<String, String> params = new HashMap<>();
        if (!isEmpty(holmesOssConfigStr)) {
          params = Splitter.on("|").withKeyValueSeparator("^").split(holmesOssConfigStr);
        }
        instanceTmp.init(params);
        instance = instanceTmp;
      }
    }
  }

  private static boolean isEmpty(CharSequence cs) {
    return cs == null || cs.length() == 0;
  }
}
