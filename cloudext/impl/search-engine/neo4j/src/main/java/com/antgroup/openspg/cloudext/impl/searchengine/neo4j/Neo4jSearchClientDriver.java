package com.antgroup.openspg.cloudext.impl.searchengine.neo4j;

import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClient;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClientDriver;
import com.antgroup.openspg.cloudext.interfaces.searchengine.SearchEngineClientDriverManager;

public class Neo4jSearchClientDriver implements SearchEngineClientDriver {

  static {
    SearchEngineClientDriverManager.registerDriver(new Neo4jSearchClientDriver());
  }

  @Override
  public String driverScheme() {
    return "neo4j";
  }

  @Override
  public SearchEngineClient connect(String url) {
    return new Neo4jSearchClient(url);
  }
}
