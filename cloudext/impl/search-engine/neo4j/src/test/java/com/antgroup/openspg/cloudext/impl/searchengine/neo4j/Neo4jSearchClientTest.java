package com.antgroup.openspg.cloudext.impl.searchengine.neo4j;

import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.SearchRequest;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.FullTextSearchQuery;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.request.query.VectorSearchQuery;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class Neo4jSearchClientTest {

  private final String connUrl =
      "neo4j://localhost:7687?user=neo4j&password=neo4j@openspg&database=neo4j&namespace=Neo4jTestNamespace";

  private final Neo4jSearchClient client = new Neo4jSearchClient(connUrl);

  @Test
  public void testTextSearch() {
    SearchRequest request = new SearchRequest();
    request.setQuery(
        new FullTextSearchQuery("李连杰", Collections.singletonList("Neo4jTestNamespace.Person")));
    request.setSize(10);
    List<IdxRecord> records = client.search(request);
    for (IdxRecord record : records) {
      String[] labels = (String[]) record.getFields().get("__labels__");
      Map<String, Object> properties = record.getFields();
      System.out.printf(
          "%s %.6g %s %s\n",
          record.getFields().get("name"), record.getScore(), Arrays.toString(labels), properties);
    }
  }

  @Test
  public void testVectorSearch() {
    SearchRequest request = new SearchRequest();
    request.setQuery(
        new VectorSearchQuery("Neo4jTestNamespace.Work", "name", new float[] {0.866f, 0.5f}));
    request.setSize(10);
    List<IdxRecord> records = client.search(request);
    for (IdxRecord record : records) {
      String[] labels = (String[]) record.getFields().get("__labels__");
      Map<String, Object> properties = record.getFields();
      System.out.printf(
          "%s %.6g %s %s\n",
          record.getFields().get("name"), record.getScore(), Arrays.toString(labels), properties);
    }
  }
}
