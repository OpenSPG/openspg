package com.antgroup.openspgapp.biz.schema;

import com.antgroup.openspgapp.biz.schema.dto.LogicRuleDTO;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/RuleManager.class */
public interface RuleManager {
  List<LogicRuleDTO> getProjectRule(Long projectId);
}
