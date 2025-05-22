package com.antgroup.openspgapp.api.http.server.reasoner;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.PermissionManager;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.common.model.exception.IllegalParamsException;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspgapp.api.http.server.BaseController;
import com.antgroup.openspgapp.biz.reasoner.SessionManager;
import com.antgroup.openspgapp.core.reasoner.model.Session;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.session.CreateSessionRequest;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.session.ListSessionRequest;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.session.SessionResponse;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.session.UpdateSessionRequest;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping({"/public/v1/reasoner/session"})
@Controller
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/reasoner/SessionController.class */
public class SessionController extends BaseController {

  @Autowired private SessionManager sessionManager;

  @Autowired private PermissionManager permissionManager;

  @Autowired private ProjectManager projectManager;

  @RequestMapping(
      value = {"/create"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<SessionResponse> sessionCreate(
      @RequestBody final CreateSessionRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<SessionResponse>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.SessionController.1
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
            AssertUtils.assertParamObjectIsNotNull("name", request.getName());
            Project project =
                SessionController.this.projectManager.queryById(request.getProjectId());
            AssertUtils.assertParamObjectIsNotNull("project", project);
            JSONObject config = JSONObject.parseObject(project.getConfig());
            if (!config.containsKey("llm")) {
              throw new IllegalParamsException("llm is not configured", new Object[0]);
            }
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public SessionResponse action() {
            Session session =
                new Session(
                    (Long) null,
                    request.getProjectId(),
                    request.getUserId(),
                    request.getName(),
                    request.getDescription());
            return Utils.convert(SessionController.this.sessionManager.createSession(session));
          }
        });
  }

  @RequestMapping(
      value = {"/update"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<Integer> sessionUpdate(@RequestBody final UpdateSessionRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Integer>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.SessionController.2
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("id", request.getId());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Integer action() {
            Session session =
                new Session(
                    request.getId(),
                    (Long) null,
                    (Long) null,
                    request.getName(),
                    request.getDescription());
            int cnt = SessionController.this.sessionManager.updateSession(session).intValue();
            if (cnt <= 0) {
              throw new RuntimeException("update session failed");
            }
            return Integer.valueOf(cnt);
          }
        });
  }

  @RequestMapping(
      value = {"/delete"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<Integer> sessionDelete(@RequestBody final UpdateSessionRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Integer>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.SessionController.3
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("id", request.getId());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Integer action() {
            Session session =
                new Session(request.getId(), (Long) null, 0L, (String) null, (String) null);
            int cnt = SessionController.this.sessionManager.deleteSession(session).intValue();
            if (cnt <= 0) {
              throw new RuntimeException("delete session failed");
            }
            return Integer.valueOf(cnt);
          }
        });
  }

  @RequestMapping(
      value = {"/list"},
      method = {RequestMethod.GET})
  @ResponseBody
  public HttpResult<List<SessionResponse>> sessionList(final ListSessionRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<List<SessionResponse>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.SessionController.4
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public List<SessionResponse> action() {
            int limit = null == request.getLimit() ? 10 : request.getLimit().intValue();
            return (List)
                SessionController.this.sessionManager
                    .queryRecentSession(
                        request.getProjectId(), request.getUserId(), Integer.valueOf(limit))
                    .stream()
                    .map(Utils::convert)
                    .collect(Collectors.toList());
          }
        });
  }
}
