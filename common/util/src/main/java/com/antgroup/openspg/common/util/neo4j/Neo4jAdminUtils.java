package com.antgroup.openspg.common.util.neo4j;

import org.neo4j.driver.*;

public class Neo4jAdminUtils {

  private Driver driver;
  private String database;

  public Neo4jIndexUtils neo4jIndex;
  public Neo4jDataUtils neo4jData;
  public Neo4jGraphUtils neo4jGraph;

  public Neo4jAdminUtils(String uri, String user, String password, String database) {
    this.driver = Neo4jDriverManager.getNeo4jDriver(uri, user, password);
    this.database = database;
    this.neo4jIndex = new Neo4jIndexUtils(this.driver, this.database);
    this.neo4jData = new Neo4jDataUtils(this.driver, this.database);
    this.neo4jGraph = new Neo4jGraphUtils(this.driver, this.database);
  }
}
