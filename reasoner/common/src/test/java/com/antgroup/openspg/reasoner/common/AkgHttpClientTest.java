/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.reasoner.common;

import com.antgroup.openspg.reasoner.common.http.AkgHttpClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;

/**
 * @author chengqiang.cq
 * @version $Id: AkgHttpClientTest.java, v 0.1 2023-05-17 16:10 chengqiang.cq Exp $$
 */
public class AkgHttpClientTest {

  // @Test
  public void testPost() {
    String url = "http://kgengine.stable.alipay.net/dispatch/task/datasource";
    Map<String, String> params = new HashMap<>();
    params.put("token", "3450e1e9Dd360C08");
    params.put("projectId", "355000009");
    try {
      AkgHttpClient.doPost(url, params, "UTF-8", 10, 10);
      Assert.assertTrue(false);
    } catch (IOException ex) {
      Assert.assertTrue(true);
    }
  }

  // @Test
  public void testGet() {
    String url = "https://kgengine.alipay.com/home/schema/getEntityTypeDetailList.json";
    Map<String, String> headers = new HashMap<>();
    Map<String, String> params = new HashMap<>();
    headers.put("Referer", url);
    params.put("token", "3450e1e9Dd360C08");
    params.put("projectId", "355000009");
    params.put("isDraft", "false");
    try {
      AkgHttpClient.doGet(url, headers, params, "UTF-8", 10, 10);
      Assert.assertTrue(false);
    } catch (RuntimeException ex) {
      Assert.assertTrue(true);
    }
  }
}
