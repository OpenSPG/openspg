package com.antgroup.openspg.common.util.neo4j;

import com.antgroup.openspg.common.util.neo4j.model.RelationLabelConstraint;
import com.antgroup.openspg.common.util.neo4j.model.RelationWithAdjacentNodes;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.springframework.util.CollectionUtils;

public class Neo4jDataUtils {

  public static final String KEY_START_NODE_ID = "start_node_id";
  public static final String KEY_END_NODE_ID = "end_node_id";
  public static final String KEY_PROPERTIES = "properties";

  private Driver driver;
  private String database;
  private Neo4jGraphUtils neo4jGraph;
  private List<String> uniqueConstraintLabels = Lists.newArrayList();

  public Neo4jDataUtils(Driver driver, String database) {
    this.driver = driver;
    this.database = database;
    this.neo4jGraph = new Neo4jGraphUtils(driver, database);
  }

  public void upsertNode(
      String label, Map<String, Object> properties, String idKey, Set<String> extraLabels) {
    try (Session session = driver.session(SessionConfig.forDatabase(database))) {
      if (!uniqueConstraintLabels.contains(label)) {
        neo4jGraph.createUniqueConstraint(label);
        uniqueConstraintLabels.add(label);
      }
      session.writeTransaction(
          tx -> {
            String setLabelAction =
                CollectionUtils.isEmpty(extraLabels)
                    ? StringUtils.EMPTY
                    : ", n:"
                        + extraLabels.stream()
                            .map(Neo4jCommonUtils::escapeNeo4jIdentifier)
                            .collect(Collectors.joining(":"));

            String query =
                String.format(
                    "MERGE (n:%s {%s: $properties.%s}) SET n += $properties %s RETURN n;",
                    Neo4jCommonUtils.escapeNeo4jIdentifier(label), idKey, idKey, setLabelAction);
            Map<String, Object> parameters = Maps.newHashMap();
            parameters.put(KEY_PROPERTIES, properties);
            tx.run(query, parameters);
            tx.commit();
            return null;
          });
    }
  }

  public void upsertNodes(
      String label,
      List<Map<String, Object>> propertiesList,
      String idKey,
      Set<String> extraLabels) {
    try (Session session = driver.session(SessionConfig.forDatabase(database))) {
      if (!uniqueConstraintLabels.contains(label)) {
        neo4jGraph.createUniqueConstraint(label);
        uniqueConstraintLabels.add(label);
      }
      session.writeTransaction(
          tx -> {
            String setLabelAction =
                CollectionUtils.isEmpty(extraLabels)
                    ? StringUtils.EMPTY
                    : ", n:"
                        + extraLabels.stream()
                            .map(Neo4jCommonUtils::escapeNeo4jIdentifier)
                            .collect(Collectors.joining(":"));

            String query =
                String.format(
                    "UNWIND $properties_list AS properties MERGE (n:%s {%s: properties.%s}) SET n += properties %s RETURN n;",
                    Neo4jCommonUtils.escapeNeo4jIdentifier(label), idKey, idKey, setLabelAction);
            Map<String, Object> parameters = Maps.newHashMap();
            parameters.put("properties_list", propertiesList);
            tx.run(query, parameters);
            tx.commit();
            return null;
          });
    }
  }

  public void deleteNode(String label, String idValue, String idKey) {
    try (Session session = driver.session(SessionConfig.forDatabase(database))) {
      session.writeTransaction(
          tx -> {
            String query =
                String.format(
                    "MATCH (n:%s {%s: $id_value}) DETACH DELETE n",
                    Neo4jCommonUtils.escapeNeo4jIdentifier(label), idKey);
            Map<String, Object> parameters = Maps.newHashMap();
            parameters.put("id_value", idValue);
            tx.run(query, parameters);
            tx.commit();
            return null;
          });
    }
  }

  public void deleteNodes(String label, List<String> idValues, String idKey) {
    try (Session session = driver.session(SessionConfig.forDatabase(database))) {
      session.writeTransaction(
          tx -> {
            String query =
                String.format(
                    "UNWIND $id_values AS id_value MATCH (n:%s {%s: id_value}) DETACH DELETE n",
                    Neo4jCommonUtils.escapeNeo4jIdentifier(label), idKey);
            Map<String, Object> parameters = Maps.newHashMap();
            parameters.put("id_values", idValues);
            tx.run(query, parameters);
            tx.commit();
            return null;
          });
    }
  }

  public void upsertRelationship(
      String startNodeLabel,
      String startNodeIdValue,
      String endNodeLabel,
      String endNodeIdValue,
      String relType,
      Map<String, Object> properties,
      boolean upsertNodes,
      String startNodeIdKey,
      String endNodeIdKey) {
    try (Session session = driver.session(SessionConfig.forDatabase(database))) {
      session.writeTransaction(
          tx -> {
            String query;
            if (upsertNodes) {
              query =
                  String.format(
                      "MERGE (a:%s {%s: $start_node_id_value}) "
                          + "MERGE (b:%s {%s: $end_node_id_value}) "
                          + "MERGE (a)-[r:%s]->(b) SET r += $properties RETURN r",
                      Neo4jCommonUtils.escapeNeo4jIdentifier(startNodeLabel),
                      startNodeIdKey,
                      Neo4jCommonUtils.escapeNeo4jIdentifier(endNodeLabel),
                      endNodeIdKey,
                      Neo4jCommonUtils.escapeNeo4jIdentifier(relType));
            } else {
              query =
                  String.format(
                      "MATCH (a:%s {%s: $start_node_id_value}), "
                          + "(b:%s {%s: $end_node_id_value}) "
                          + "MERGE (a)-[r:%s]->(b) SET r += $properties RETURN r",
                      Neo4jCommonUtils.escapeNeo4jIdentifier(startNodeLabel),
                      startNodeIdKey,
                      Neo4jCommonUtils.escapeNeo4jIdentifier(endNodeLabel),
                      endNodeIdKey,
                      Neo4jCommonUtils.escapeNeo4jIdentifier(relType));
            }
            Map<String, Object> parameters = Maps.newHashMap();
            parameters.put("start_node_id_value", startNodeIdValue);
            parameters.put("end_node_id_value", endNodeIdValue);
            parameters.put(KEY_PROPERTIES, properties);
            tx.run(query, parameters);
            tx.commit();
            return null;
          });
    }
  }

  public void upsertRelationships(
      String startNodeLabel,
      String endNodeLabel,
      String relType,
      List<Map<String, Object>> relations,
      boolean upsertNodes,
      String startNodeIdKey,
      String endNodeIdKey) {
    try (Session session = driver.session(SessionConfig.forDatabase(database))) {
      session.writeTransaction(
          tx -> {
            String query;
            if (upsertNodes) {
              query =
                  String.format(
                      "UNWIND $relations AS relationship "
                          + "MERGE (a:%s {%s: relationship.start_node_id}) "
                          + "MERGE (b:%s {%s: relationship.end_node_id}) "
                          + "MERGE (a)-[r:%s]->(b) SET r += relationship.properties RETURN r",
                      Neo4jCommonUtils.escapeNeo4jIdentifier(startNodeLabel),
                      startNodeIdKey,
                      Neo4jCommonUtils.escapeNeo4jIdentifier(endNodeLabel),
                      endNodeIdKey,
                      Neo4jCommonUtils.escapeNeo4jIdentifier(relType));
            } else {
              query =
                  String.format(
                      "UNWIND $relations AS relationship "
                          + "MATCH (a:%s {%s: relationship.start_node_id}) "
                          + "MATCH (b:%s {%s: relationship.end_node_id}) "
                          + "MERGE (a)-[r:%s]->(b) SET r += relationship.properties RETURN r",
                      Neo4jCommonUtils.escapeNeo4jIdentifier(startNodeLabel),
                      startNodeIdKey,
                      Neo4jCommonUtils.escapeNeo4jIdentifier(endNodeLabel),
                      endNodeIdKey,
                      Neo4jCommonUtils.escapeNeo4jIdentifier(relType));
            }

            Map<String, Object> parameters = Maps.newHashMap();
            parameters.put("relations", relations);
            tx.run(query, parameters);
            tx.commit();
            return null;
          });
    }
  }

  public void deleteRelationship(
      String startNodeLabel,
      String startNodeIdValue,
      String endNodeLabel,
      String endNodeIdValue,
      String relType,
      String startNodeIdKey,
      String endNodeIdKey) {
    try (Session session = driver.session(SessionConfig.forDatabase(database))) {
      session.writeTransaction(
          tx -> {
            String query =
                String.format(
                    "MATCH (a:%s {%s: $start_node_id_value})-[r:%s]->(b:%s {%s: $end_node_id_value}) DELETE r",
                    Neo4jCommonUtils.escapeNeo4jIdentifier(startNodeLabel),
                    startNodeIdKey,
                    Neo4jCommonUtils.escapeNeo4jIdentifier(relType),
                    Neo4jCommonUtils.escapeNeo4jIdentifier(endNodeLabel),
                    endNodeIdKey);

            Map<String, Object> parameters = Maps.newHashMap();
            parameters.put("start_node_id_value", startNodeIdValue);
            parameters.put("end_node_id_value", endNodeIdValue);
            tx.run(query, parameters);
            tx.commit();
            return null;
          });
    }
  }

  public void deleteRelationships(
      String startNodeLabel,
      List<String> startNodeIdValues,
      String endNodeLabel,
      List<String> endNodeIdValues,
      String relType,
      String startNodeIdKey,
      String endNodeIdKey) {
    try (Session session = driver.session(SessionConfig.forDatabase(database))) {
      session.writeTransaction(
          tx -> {
            String query =
                String.format(
                    "UNWIND $start_node_id_values AS start_node_id_value "
                        + "UNWIND $end_node_id_values AS end_node_id_value "
                        + "MATCH (a:%s {%s: start_node_id_value})-[r:%s]->(b:%s {%s: end_node_id_value}) DELETE r",
                    Neo4jCommonUtils.escapeNeo4jIdentifier(startNodeLabel),
                    startNodeIdKey,
                    Neo4jCommonUtils.escapeNeo4jIdentifier(relType),
                    Neo4jCommonUtils.escapeNeo4jIdentifier(endNodeLabel),
                    endNodeIdKey);

            Map<String, Object> parameters = Maps.newHashMap();
            parameters.put("start_node_id_values", startNodeIdValues);
            parameters.put("end_node_id_values", endNodeIdValues);
            tx.run(query, parameters);
            tx.commit();
            return null;
          });
    }
  }

  public Node querySingleNode(String idKey, Object idValue, String label) {
    Value vars = Values.parameters(idKey, idValue);
    String cypherTemplate =
        "MATCH (n:"
            + Neo4jCommonUtils.escapeNeo4jIdentifier(label)
            + " WHERE n.id=$"
            + idKey
            + ") RETURN n;";

    Session session = driver.session(SessionConfig.forDatabase(database));
    Result rs = session.run(cypherTemplate, vars);
    if (rs.hasNext()) {
      Record record = rs.next();
      return record.get("n").asNode();
    }

    return null;
  }

  public List<Node> queryNodeByIdValues(String idKey, Set idValueList, String label) {
    Value vars = Values.parameters(idKey, idValueList);
    String cypherTemplate =
        "MATCH (n:"
            + Neo4jCommonUtils.escapeNeo4jIdentifier(label)
            + " WHERE n.id IN $"
            + idKey
            + ") RETURN n;";

    Session session = driver.session(SessionConfig.forDatabase(database));
    Result rs = session.run(cypherTemplate, vars);
    List<Node> nodes = Lists.newArrayList();
    while (rs.hasNext()) {
      Record record = rs.next();
      nodes.add(record.get("n").asNode());
    }

    return nodes;
  }

  public List<Node> scanNodes(String label, Integer limit) {
    List<Node> resultList = Lists.newArrayList();
    String cypher = "MATCH (n:" + Neo4jCommonUtils.escapeNeo4jIdentifier(label) + ") RETURN n";
    if (limit != null) {
      cypher += (" LIMIT " + limit);
    }
    cypher += ";";

    Session session = driver.session(SessionConfig.forDatabase(database));
    Result rs = session.run(cypher);
    while (rs.hasNext()) {
      org.neo4j.driver.Record record = rs.next();
      Node node = record.get("n").asNode();
      resultList.add(node);
    }
    return resultList;
  }

  public List<RelationWithAdjacentNodes> queryRelationships(
      @NonNull String sourceIdKey,
      @NonNull Object sourceIdValue,
      @NonNull String sourceLabel,
      boolean queryOutEdge,
      Set<RelationLabelConstraint> edgeLabel) {
    List<RelationWithAdjacentNodes> resultList = Lists.newArrayList();

    String cypherTemplate =
        queryOutEdge
            ? "MATCH (s:%s WHERE s.id=$id)-[p]->(o) %s RETURN s, p, o;"
            : "MATCH (o:%s WHERE o.id=$id)<-[p]-(s) %s RETURN s, p, o;";
    String whereClause =
        edgeLabel == null
            ? StringUtils.EMPTY
            : "WHERE "
                + edgeLabel.stream().map(this::toTypeCondition).collect(Collectors.joining(" OR "));
    String cypher =
        String.format(
            cypherTemplate, Neo4jCommonUtils.escapeNeo4jIdentifier(sourceLabel), whereClause);
    Value vars = Values.parameters(sourceIdKey, sourceIdValue);

    Session session = driver.session(SessionConfig.forDatabase(database));
    Result rs = session.run(cypher, vars);
    while (rs.hasNext()) {
      Record record = rs.next();
      Node s = record.get("s").asNode();
      Node o = record.get("o").asNode();
      Relationship p = record.get("p").asRelationship();
      RelationWithAdjacentNodes relation = new RelationWithAdjacentNodes(s, p, o);
      resultList.add(relation);
    }

    return resultList;
  }

  private String toTypeCondition(RelationLabelConstraint constraint) {
    return "(s:"
        + Neo4jCommonUtils.escapeNeo4jIdentifier(constraint.getStartNodeLabel())
        + " AND type(p)='"
        + Neo4jCommonUtils.escapeNeo4jIdentifier(constraint.getEdgeLabel())
        + "'"
        + " AND o:"
        + Neo4jCommonUtils.escapeNeo4jIdentifier(constraint.getEndNodeLabel())
        + ")";
  }
}
