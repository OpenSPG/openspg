package com.antgroup.openspgapp.biz.schema.convertor;

import com.antgroup.openspg.core.schema.model.semantic.LogicalRule;
import com.antgroup.openspgapp.biz.schema.dto.LogicRuleDTO;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/convertor/RuleConvertor.class */
public class RuleConvertor {
  public static LogicRuleDTO toRuleDTO(LogicalRule logicalRule) {
    if (null == logicalRule) {
      return null;
    }
    LogicRuleDTO ruleDTO = new LogicRuleDTO();
    ruleDTO.setModifiedDate(logicalRule.getModifiedDate());
    ruleDTO.setRuleId(logicalRule.getCode().getCode());
    ruleDTO.setVersion(logicalRule.getVersion());
    ruleDTO.setExpression(logicalRule.getContent());
    ruleDTO.setStatus(logicalRule.getStatus().name());
    ruleDTO.setIsMaster(logicalRule.getMaster());
    ruleDTO.setUserNo(logicalRule.getCreator().getUserId());
    return ruleDTO;
  }
}
