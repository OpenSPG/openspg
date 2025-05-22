package com.antgroup.openspgapp.api.http.server.reasoner;

import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspgapp.biz.reasoner.TutorialManager;
import com.antgroup.openspgapp.core.reasoner.model.Tutorial;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.tutorial.QueryTutorialRequest;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.tutorial.TutorialRequest;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.tutorial.TutorialResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping({"/public/v1/reasoner/tutorial"})
@Controller
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/reasoner/TutorialController.class */
public class TutorialController {

  @Autowired private TutorialManager tutorialManager;

  @RequestMapping(
      value = {"/list"},
      method = {RequestMethod.GET})
  @ResponseBody
  public HttpResult<List<TutorialResponse>> list(final QueryTutorialRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<List<TutorialResponse>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.TutorialController.1
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public List<TutorialResponse> action() {
            List<Tutorial> tutorialList =
                TutorialController.this.tutorialManager.queryTutorials(
                    request.getProjectId(), request.getKeyword());
            return (List) tutorialList.stream().map(Utils::convert).collect(Collectors.toList());
          }
        });
  }

  @RequestMapping(
      value = {"/create"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<TutorialResponse> create(@RequestBody final TutorialRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<TutorialResponse>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.TutorialController.2
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getProjectId());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public TutorialResponse action() {
            TutorialController.this.tutorialManager.create(Utils.convert(request));
            return null;
          }
        });
  }

  @RequestMapping(
      value = {"/update"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<TutorialResponse> update(@RequestBody final TutorialRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<TutorialResponse>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.TutorialController.3
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("projectId", request.getId());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public TutorialResponse action() {
            TutorialController.this.tutorialManager.update(Utils.convert(request));
            return null;
          }
        });
  }
}
