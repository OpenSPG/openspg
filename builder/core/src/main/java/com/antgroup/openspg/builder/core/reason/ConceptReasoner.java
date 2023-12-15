package com.antgroup.openspg.builder.core.reason;

import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.core.schema.model.semantic.BaseConceptSemantic;
import java.util.List;

public interface ConceptReasoner<T extends BaseConceptSemantic> {

  List<BaseSPGRecord> reason(List<BaseSPGRecord> records, T conceptSemantic);
}
