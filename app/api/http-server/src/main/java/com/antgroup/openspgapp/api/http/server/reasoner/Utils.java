package com.antgroup.openspgapp.api.http.server.reasoner;

import com.antgroup.openspg.common.constants.SpgAppConstant;
import com.antgroup.openspgapp.core.reasoner.model.Session;
import com.antgroup.openspgapp.core.reasoner.model.Tutorial;
import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Node;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.session.SessionResponse;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.task.TaskResponse;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.tutorial.TutorialRequest;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.tutorial.TutorialResponse;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/reasoner/Utils.class */
public class Utils {
  public static SessionResponse convert(Session session) {
    SessionResponse sessionResponse = new SessionResponse();
    sessionResponse.setId(session.getId());
    sessionResponse.setProjectId(session.getProjectId());
    sessionResponse.setUserId(session.getUserId());
    sessionResponse.setName(session.getName());
    if (null == session.getDescription()) {
      sessionResponse.setDescription("");
    } else {
      sessionResponse.setDescription(session.getDescription());
    }
    return sessionResponse;
  }

  public static TaskResponse convert(Task task) {
    TaskResponse taskResponse = new TaskResponse();
    if (null == task) {
      return taskResponse;
    }
    taskResponse.setId(task.getId());
    taskResponse.setProjectId(task.getProjectId());
    taskResponse.setUserId(task.getUserId());
    taskResponse.setSessionId(task.getSessionId());
    taskResponse.setDsl(task.getDsl());
    taskResponse.setNl(task.getNl());
    taskResponse.setParams(task.getParams());
    taskResponse.setMark(task.getMark());
    taskResponse.setStatus(task.getStatus());
    taskResponse.setResultMessage(task.getResultMessage());
    taskResponse.setResultTable(task.getResultTable());
    taskResponse.setResultNodes(task.getResultNodes());
    List<Node> resultNodes = taskResponse.getResultNodes();
    if (CollectionUtils.isNotEmpty(resultNodes)) {
      resultNodes.stream()
          .filter(
              node -> {
                return !node.getProperties().isEmpty();
              })
          .forEach(
              node2 -> {
                Map<String, Object> prop = node2.getProperties();
                prop.keySet()
                    .removeIf(
                        key -> {
                          return SpgAppConstant.HIDDEN_PROPERTY.contains(key);
                        });
                node2.setProperties(prop);
              });
    }
    taskResponse.setResultEdges(task.getResultEdges());
    taskResponse.setResultPaths(task.getResultPaths());
    return taskResponse;
  }

  public static TutorialResponse convert(Tutorial tutorial) {
    TutorialResponse tutorialResponse = new TutorialResponse();
    tutorialResponse.setId(tutorial.getId());
    tutorialResponse.setProjectId(tutorial.getProjectId());
    tutorialResponse.setName(tutorial.getName());
    tutorialResponse.setDsl(tutorial.getDsl());
    tutorialResponse.setNl(tutorial.getNl());
    tutorialResponse.setParams(tutorial.getParams());
    tutorialResponse.setDescription(tutorial.getDescription());
    return tutorialResponse;
  }

  public static Tutorial convert(TutorialRequest request) {
    Tutorial tutorial = new Tutorial();
    tutorial.setId(request.getId());
    tutorial.setProjectId(request.getProjectId());
    tutorial.setEnable(request.getEnable());
    tutorial.setName(request.getName());
    tutorial.setDsl(request.getDsl());
    tutorial.setNl(request.getNl());
    tutorial.setParams(request.getParams());
    tutorial.setDescription(request.getDescription());
    return tutorial;
  }
}
