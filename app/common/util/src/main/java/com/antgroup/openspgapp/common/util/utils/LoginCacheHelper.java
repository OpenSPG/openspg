package com.antgroup.openspgapp.common.util.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

/* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/utils/LoginCacheHelper.class */
public class LoginCacheHelper {
  private static final Cache<String, Object> LOCAL_LOGIN_ACCOUNT_CACHE =
      CacheBuilder.newBuilder().maximumSize(10240).expireAfterWrite(300, TimeUnit.SECONDS).build();

  public static Object getLocalLoginAccount(String key) {
    return LOCAL_LOGIN_ACCOUNT_CACHE.getIfPresent(key);
  }

  public static void putLocalLoginAccount(String key, Object o) {
    LOCAL_LOGIN_ACCOUNT_CACHE.put(key, o);
  }

  public static void removeLocalLoginAccount(String key) {
    LOCAL_LOGIN_ACCOUNT_CACHE.invalidate(key);
  }
}
