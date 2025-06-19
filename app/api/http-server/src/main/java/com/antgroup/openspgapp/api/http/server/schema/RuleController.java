package com.antgroup.openspgapp.api.http.server.schema;

import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspgapp.api.http.server.BaseController;
import com.antgroup.openspgapp.biz.schema.RuleManager;
import com.antgroup.openspgapp.biz.schema.dto.LogicRuleDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping({"/rule/api/"})
@RestController
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/schema/RuleController.class */
public class RuleController extends BaseController {

  @Autowired private RuleManager ruleManager;

  @RequestMapping(
      value = {"/getRuleList.json"},
      method = {RequestMethod.GET})
  public HttpResult<List<LogicRuleDTO>> getRuleList(final Long projectId) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<List<LogicRuleDTO>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.schema.RuleController.1
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("projectId", projectId);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public List<LogicRuleDTO> action() {
            return RuleController.this.ruleManager.getProjectRule(projectId);
          }
        });
  }
}
