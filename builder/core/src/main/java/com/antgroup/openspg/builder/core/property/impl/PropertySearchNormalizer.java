package com.antgroup.openspg.builder.core.property.impl;

import com.antgroup.openspg.builder.core.property.PropertyNormalizer;
import com.antgroup.openspg.builder.core.runtime.BuilderContext;
import com.antgroup.openspg.builder.model.exception.BuilderException;
import com.antgroup.openspg.builder.model.exception.PropertyNormalizeException;
import com.antgroup.openspg.builder.model.record.property.BasePropertyRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClient;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClientDriverManager;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.SearchRequest;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.BaseQuery;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.MatchQuery;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.OperatorType;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.QueryGroup;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class PropertySearchNormalizer implements PropertyNormalizer {

  private SearchEngineClient searchEngineClient;

  @Override
  public void init(BuilderContext context) throws BuilderException {
    searchEngineClient =
        SearchEngineClientDriverManager.getClient(context.getCatalog().getSearchEngineConnInfo());
  }

  @Override
  public void propertyNormalize(BasePropertyRecord record) throws PropertyNormalizeException {
    SPGTypeRef objectTypeRef = record.getObjectTypeRef();

    if (!objectTypeRef.isEntityType() && !objectTypeRef.isConceptType()) {
      return;
    }
    String objectTypeRefName = objectTypeRef.getName();

    List<String> rawValues = record.getRawValues();
    List<String> ids = new ArrayList<>(rawValues.size());
    for (String rawValue : rawValues) {
      // build search request
      SearchRequest request = new SearchRequest();
      request.setIndexName(
          searchEngineClient.getIdxNameConvertor().convertIdxName(objectTypeRefName));

      List<BaseQuery> queries = new ArrayList<>(4);
      queries.add(new MatchQuery("id", rawValue));
      queries.add(new MatchQuery("name", rawValue));
      if (objectTypeRef.isConceptType()) {
        queries.add(new MatchQuery("alias", rawValue));
        queries.add(new MatchQuery("stdId", rawValue));
      }
      QueryGroup queryGroup = new QueryGroup(queries, OperatorType.OR);
      request.setQuery(queryGroup);
      request.setSize(50);

      // search and parse response
      List<IdxRecord> idxRecords = searchEngineClient.search(request);
      if (CollectionUtils.isEmpty(idxRecords)) {
        throw new PropertyNormalizeException("");
      }
      IdxRecord idxRecord = idxRecords.get(0);
      if (!rawValue.equals(idxRecord.getDocId()) && idxRecord.getScore() < 0.5) {
        throw new PropertyNormalizeException("");
      }
      ids.add(idxRecord.getDocId());
    }

    record.getValue().setStds(Collections.singletonList(ids));
    record.getValue().setIds(ids);
  }
}
