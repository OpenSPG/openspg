package com.antgroup.openspgapp.biz.reasoner;

import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import java.util.Map;

/* loaded from: biz-reasoner-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/reasoner/DialogManager.class */
public interface DialogManager {
  Task submit(
      Long sessionId, String dsl, String nl, String resultMessage, Map<String, String> params);
}
