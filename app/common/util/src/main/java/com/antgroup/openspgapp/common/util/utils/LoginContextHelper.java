package com.antgroup.openspgapp.common.util.utils;

/* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/utils/LoginContextHelper.class */
public class LoginContextHelper {
  private static final ThreadLocal<Object> USER_LOCAL_HOLDER = new ThreadLocal<>();

  public static Object getUserFromCtx() {
    return USER_LOCAL_HOLDER.get();
  }

  public static void putUserToCtx(Object object) {
    USER_LOCAL_HOLDER.set(object);
  }

  public static void clearUserInCtx() {
    USER_LOCAL_HOLDER.remove();
  }
}
