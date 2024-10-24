package com.antgroup.openspg.common.util.neo4j;

import com.antgroup.openspg.common.util.tuple.Tuple2;
import com.antgroup.openspg.core.schema.model.predicate.IndexTypeEnum;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.summary.ResultSummary;

@Slf4j
public class Neo4jGraphUtils {

  private static final String ALL_GRAPH = "allGraph";

  private Driver driver;
  private String database;
  private Neo4jIndexUtils neo4jIndex;

  public Neo4jGraphUtils(Driver driver, String database) {
    this.driver = driver;
    this.database = database;
    this.neo4jIndex = new Neo4jIndexUtils(driver, database);
  }

  public void initializeSchema(List<BaseSPGType> schemaTypes, int vectorDimensions) {
    for (BaseSPGType schemaType : schemaTypes) {
      List<Property> properties = schemaType.getProperties();

      if (properties != null) {
        for (Property property : properties) {

          if ("name".equals(property.getName())) {
            neo4jIndex.createVectorIndex(
                schemaType.getName(), property.getName(), vectorDimensions);
          }

          IndexTypeEnum indexType = property.getIndexType();
          if (indexType != null) {
            switch (indexType) {
              case TEXT:
                // Handle text index if needed
                break;
              case VECTOR:
              case TEXT_AND_VECTOR:
                neo4jIndex.createVectorIndex(
                    schemaType.getName(), property.getName(), vectorDimensions);
                break;
              default:
                log.info("Undefined IndexTypeEnum {}", indexType);
            }
          }
        }
      }
    }

    Tuple2<List<String>, List<String>> tuple2 = neo4jIndex.collectTextIndexInfo(schemaTypes);
    neo4jIndex.createTextIndex(tuple2.first, tuple2.second);

    neo4jIndex.createVectorIndex("Entity", "name", vectorDimensions);
    neo4jIndex.createVectorIndex("Entity", "desc", vectorDimensions);

    neo4jIndex.refreshVectorIndexMeta(true);
  }

  public List<String> createLabelsUniqueConstraint() {
    Session session = driver.session(SessionConfig.forDatabase(this.database));
    Result result = session.run("CALL db.labels()");
    List<String> labels =
        result.list(record -> record.get(0).asString()).stream()
            .filter(label -> !label.equals("Entity"))
            .collect(Collectors.toList());
    for (String label : labels) {
      createUniqueConstraint(label);
    }
    return labels;
  }

  public void createUniqueConstraint(String label) {
    Session session = driver.session(SessionConfig.forDatabase(database));
    String constraintName = "uniqueness_" + label.replace('.', '_') + "_id";
    String createConstraintQuery =
        String.format(
            "CREATE CONSTRAINT %s IF NOT EXISTS FOR (n:%s) REQUIRE n.id IS UNIQUE",
            constraintName, Neo4jCommonUtils.escapeNeo4jIdentifier(label));
    try {
      session.writeTransaction(
          tx -> {
            tx.run(createConstraintQuery);
            tx.commit();
            return null;
          });
      log.info("Unique constraint created for constraint_name: " + constraintName);
    } catch (Exception e) {
      log.warn("warn creating constraint for " + constraintName + ": " + e.getMessage());
    }
  }

  public List<String> getAllLabels() {
    Session session = driver.session(SessionConfig.forDatabase(this.database));
    Result result = session.run("CALL db.labels()");
    List<String> labels = result.list(record -> record.get(0).asString());
    return labels;
  }

  public void createAllGraph() {
    Session session = driver.session(SessionConfig.forDatabase(this.database));
    String existsQuery =
        String.format(
            "CALL gds.graph.exists('%s') YIELD exists "
                + "WHERE exists "
                + "CALL gds.graph.drop('%s') YIELD graphName "
                + "RETURN graphName",
            ALL_GRAPH, ALL_GRAPH);

    Result result = session.run(existsQuery);
    ResultSummary summary = result.consume();
    log.debug(
        "create pagerank graph exists graph_name: {} database: {} succeed "
            + "executed: {} consumed: {}",
        ALL_GRAPH,
        database,
        summary.resultAvailableAfter(TimeUnit.MILLISECONDS),
        summary.resultConsumedAfter(TimeUnit.MILLISECONDS));

    String projectQuery =
        String.format(
            "CALL gds.graph.project('%s','*','*') "
                + "YIELD graphName, nodeCount AS nodes, relationshipCount AS rels "
                + "RETURN graphName, nodes, rels",
            ALL_GRAPH);

    result = session.run(projectQuery);
    summary = result.consume();
    log.debug(
        "create pagerank graph graph_name: {} database: {} succeed " + "executed: {} consumed: {}",
        ALL_GRAPH,
        database,
        summary.resultAvailableAfter(TimeUnit.MILLISECONDS),
        summary.resultConsumedAfter(TimeUnit.MILLISECONDS));
  }

  public List<Map<String, Object>> getPageRankScores(
      List<Map<String, String>> startNodes, String targetType) {
    Session session = driver.session(SessionConfig.forDatabase(this.database));
    createAllGraph();
    return session.writeTransaction(tx -> getPageRankScores(tx, startNodes, targetType));
  }

  private List<Map<String, Object>> getPageRankScores(
      Transaction tx, List<Map<String, String>> startNodes, String returnType) {
    List<String> matchClauses = new ArrayList<>();
    List<String> matchIdentifiers = new ArrayList<>();

    for (int index = 0; index < startNodes.size(); index++) {
      Map<String, String> node = startNodes.get(index);
      String nodeType = node.get("type");
      String nodeIdValue = node.get("id").replace("'", "\\'");
      String nodeIdentifier = "node_" + index;

      matchClauses.add(
          String.format(
              "MATCH (%s:%s {id: '%s'})",
              nodeIdentifier, Neo4jCommonUtils.escapeNeo4jIdentifier(nodeType), nodeIdValue));
      matchIdentifiers.add(nodeIdentifier);
    }

    String matchQuery = String.join(" ", matchClauses);
    String matchIdentifierStr = String.join(", ", matchIdentifiers);

    String pageRankQuery =
        String.format(
            "%s "
                + "CALL gds.pageRank.stream('%s', { "
                + "maxIterations: 20, "
                + "dampingFactor: 0.85, "
                + "sourceNodes: [%s] "
                + "}) "
                + "YIELD nodeId, score "
                + "MATCH (m:%s) WHERE id(m) = nodeId "
                + "RETURN id(m) AS g_id, gds.util.asNode(nodeId).id AS id, score "
                + "ORDER BY score DESC",
            matchQuery,
            ALL_GRAPH,
            matchIdentifierStr,
            Neo4jCommonUtils.escapeNeo4jIdentifier(returnType));

    Result result = tx.run(pageRankQuery);
    List<Map<String, Object>> data = new ArrayList<>();
    while (result.hasNext()) {
      Record record = result.next();
      Map<String, Object> row = new HashMap<>();
      row.put("id", record.get("id").asString());
      row.put("score", record.get("score").asDouble());
      data.add(row);
    }
    return data;
  }

  public void createDatabase(String database) {
    Session session = driver.session(SessionConfig.forDatabase(this.database));
    session.writeTransaction(
        tx -> {
          tx.run(String.format("CREATE DATABASE %s IF NOT EXISTS", database));
          tx.commit();
          return null;
        });
  }

  public void deleteAllData(String database) {
    if (!this.database.equals(database)) {
      throw new IllegalArgumentException(
          String.format(
              "Error: Current database (%s) is not the same as the target database (%s).",
              this.database, database));
    }

    Session session = driver.session();
    while (true) {
      Result result = session.run("MATCH (n) WITH n LIMIT 100000 DETACH DELETE n RETURN count(*)");
      int count = result.single().get(0).asInt();
      log.info("Deleted {} nodes in this batch.", count);
      if (count == 0) {
        log.info("All data has been deleted.");
        break;
      }
    }
  }

  public List<Record> runCypherQuery(
      String database, String query, Map<String, Object> parameters) {
    if (database != null && !this.database.equals(database)) {
      throw new IllegalArgumentException(
          String.format(
              "Current database (%s) is not the same as the target database (%s).",
              this.database, database));
    }

    Session session = driver.session();
    Result result = session.run(query, parameters != null ? parameters : new HashMap<>());
    return result.list();
  }
}
