package com.antgroup.openspg.common.util.neo4j;

import com.antgroup.openspg.common.util.tuple.Tuple2;
import com.antgroup.openspg.core.schema.model.predicate.IndexTypeEnum;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;

@Slf4j
public class Neo4jIndexUtils {

  private final Driver driver;
  private final String database;

  private Map<String, Set<String>> vectorIndexMeta;
  private long vectorIndexMetaTimestampNano;
  private static final long VECTOR_INDEX_META_TIMEOUT_NANO = 60_000_000_000L; // 60 seconds

  public Neo4jIndexUtils(Driver driver, String database) {
    this.driver = driver;
    this.database = database;
  }

  private static String createVectorIndexName(String label, String propertyKey) {
    String name = String.format("%s_%s_vector_index", label, propertyKey);
    return "_" + Neo4jCommonUtils.toSnakeCase(name);
  }

  private static String createVectorFieldName(String propertyKey) {
    String name = String.format("%s_vector", propertyKey);
    return "_" + Neo4jCommonUtils.toSnakeCase(name);
  }

  public void createIndex(@NonNull String label, @NonNull String propertyKey) {
    createIndex(label, propertyKey, null);
  }

  public void createIndex(
      @NonNull String label, @NonNull String propertyKey, @Nullable String indexName) {
    if (label.isEmpty() || propertyKey.isEmpty()) return;
    if (indexName != null) deleteIndex(indexName);
    try (Session session = driver.session(SessionConfig.forDatabase(database))) {
      session.writeTransaction(
          tx -> {
            String query;
            if (indexName == null) {
              query =
                  String.format(
                      "CREATE INDEX IF NOT EXISTS FOR (n:%s) ON (n.%s)",
                      Neo4jCommonUtils.escapeNeo4jIdentifier(label),
                      Neo4jCommonUtils.escapeNeo4jIdentifier(propertyKey));
            } else {
              query =
                  String.format(
                      "CREATE INDEX %s IF NOT EXISTS FOR (n:%s) ON (n.%s)",
                      Neo4jCommonUtils.escapeNeo4jIdentifier(indexName),
                      Neo4jCommonUtils.escapeNeo4jIdentifier(label),
                      Neo4jCommonUtils.escapeNeo4jIdentifier(propertyKey));
            }
            System.out.println(query);
            tx.run(query);
            tx.commit();
            return null;
          });
    }
  }

  public String createTextIndex(@NonNull List<String> labels, @NonNull List<String> propertyKeys) {
    return createTextIndex(labels, propertyKeys, null);
  }

  public String createTextIndex(
      @NonNull List<String> labels,
      @NonNull List<String> propertyKeys,
      @Nullable String indexName) {
    if (labels.isEmpty() || propertyKeys.isEmpty()) return null;
    if (indexName == null) indexName = "_default_text_index";
    deleteIndex(indexName);
    StringBuilder sb = new StringBuilder();
    for (String label : labels) {
      if (sb.length() > 0) sb.append('|');
      sb.append(Neo4jCommonUtils.escapeNeo4jIdentifier(label));
    }
    String labelSpec = sb.toString();
    sb.setLength(0);
    for (String propertyKey : propertyKeys) {
      if (sb.length() > 0) sb.append(", ");
      sb.append("n.");
      sb.append(Neo4jCommonUtils.escapeNeo4jIdentifier(propertyKey));
    }
    String propertySpec = sb.toString();
    String query =
        String.format(
            "CREATE FULLTEXT INDEX %s IF NOT EXISTS FOR (n:%s) ON EACH [%s]",
            Neo4jCommonUtils.escapeNeo4jIdentifier(indexName), labelSpec, propertySpec);
    try (Session session = driver.session(SessionConfig.forDatabase(database))) {
      session.writeTransaction(
          tx -> {
            tx.run(query);
            tx.commit();
            return null;
          });
    }
    return indexName;
  }

  public String createVectorIndex(@NonNull String label, @NonNull String propertyKey) {
    return createVectorIndex(label, propertyKey, -1);
  }

  public String createVectorIndex(
      @NonNull String label, @NonNull String propertyKey, int vectorDimensions) {
    return createVectorIndex(label, propertyKey, vectorDimensions, null);
  }

  public String createVectorIndex(
      @NonNull String label,
      @NonNull String propertyKey,
      int vectorDimensions,
      @Nullable String metricType) {
    return createVectorIndex(label, propertyKey, vectorDimensions, metricType, null);
  }

  public String createVectorIndex(
      @NonNull String label,
      @NonNull String propertyKey,
      int vectorDimensions,
      @Nullable String metricType,
      @Nullable String indexName) {
    return createVectorIndex(label, propertyKey, vectorDimensions, metricType, indexName, -1, -1);
  }

  public String createVectorIndex(
      @NonNull String label,
      @NonNull String propertyKey,
      int vectorDimensions,
      @Nullable String metricType,
      @Nullable String indexName,
      int hnswM,
      int hnswEfConstruction) {
    if (vectorDimensions != -1 && vectorDimensions <= 0)
      throw new IllegalArgumentException(
          String.format("vectorDimensions must be positive; %d is invalid", vectorDimensions));
    if (metricType != null && !metricType.equals("cosine") && !metricType.equals("euclidean"))
      throw new IllegalArgumentException(
          String.format(
              "metricType must be \"cosine\" or \"euclidean\"; \"%s\" is invalid", metricType));
    if (hnswM != -1 && hnswM <= 0)
      throw new IllegalArgumentException(
          String.format("hnswM must be positive; %d is invalid", hnswM));
    if (hnswEfConstruction != -1 && hnswEfConstruction <= 0)
      throw new IllegalArgumentException(
          String.format("hnswEfConstruction must be positive; %d is invalid", hnswEfConstruction));
    if (indexName == null) indexName = createVectorIndexName(label, propertyKey);
    if (!propertyKey.toLowerCase().endsWith("vector"))
      propertyKey = createVectorFieldName(propertyKey);
    if (vectorDimensions == -1) vectorDimensions = Neo4jCommonUtils.DEFAULT_VECTOR_DIMENSIONS;
    if (metricType == null) metricType = Neo4jCommonUtils.DEFAULT_METRIC_TYPE;
    deleteIndex(indexName);
    StringBuilder sb = new StringBuilder();
    sb.append(
        String.format(
            "CREATE VECTOR INDEX %s IF NOT EXISTS ",
            Neo4jCommonUtils.escapeNeo4jIdentifier(indexName)));
    sb.append(
        String.format(
            "FOR (n:%s) ON (n.%s)\n",
            Neo4jCommonUtils.escapeNeo4jIdentifier(label),
            Neo4jCommonUtils.escapeNeo4jIdentifier(propertyKey)));
    sb.append("OPTIONS { indexConfig: {\n");
    sb.append(String.format("  `vector.dimensions`: %d,\n", vectorDimensions));
    sb.append(String.format("  `vector.similarity_function`: \"%s\"", metricType));
    if (hnswM != -1) sb.append(String.format(",\n  `vector.hnsw.m`: %d", hnswM));
    if (hnswEfConstruction != -1)
      sb.append(String.format(",\n  `vector.hnsw.ef_construction`: %d", hnswEfConstruction));
    sb.append("\n}}");
    String query = sb.toString();
    refreshVectorIndexMeta(true);
    try (Session session = driver.session(SessionConfig.forDatabase(database))) {
      session.writeTransaction(
          tx -> {
            tx.run(query);
            tx.commit();
            return null;
          });
    }
    return indexName;
  }

  public void deleteIndex(@NonNull String indexName) {
    String query =
        String.format("DROP INDEX %s IF EXISTS", Neo4jCommonUtils.escapeNeo4jIdentifier(indexName));
    try (Session session = driver.session(SessionConfig.forDatabase(database))) {
      session.writeTransaction(
          tx -> {
            tx.run(query);
            tx.commit();
            return null;
          });
    }
  }

  public List<Record> vectorSearch(
      @NonNull String label, @NonNull String propertyKey, float @NonNull [] queryVector) {
    return vectorSearch(label, propertyKey, queryVector, -1);
  }

  public List<Record> vectorSearch(
      @NonNull String label, @NonNull String propertyKey, float @NonNull [] queryVector, int topk) {
    return vectorSearch(label, propertyKey, queryVector, topk, null);
  }

  public List<Record> vectorSearch(
      @NonNull String label,
      @NonNull String propertyKey,
      float @NonNull [] queryVector,
      int topk,
      @Nullable String indexName) {
    return vectorSearch(label, propertyKey, queryVector, topk, indexName, -1);
  }

  public List<Record> textSearch(@NonNull String queryString) {
    return textSearch(queryString, null);
  }

  public List<Record> textSearch(
      @NonNull String queryString, @Nullable List<String> labelConstraints) {
    return textSearch(queryString, labelConstraints, -1);
  }

  public List<Record> textSearch(
      @NonNull String queryString, @Nullable List<String> labelConstraints, int topk) {
    return textSearch(queryString, labelConstraints, topk, null);
  }

  public List<Record> textSearch(
      @NonNull String queryString,
      @Nullable List<String> labelConstraints,
      int topk,
      @Nullable String indexName) {
    if (topk != -1 && topk <= 0)
      throw new IllegalArgumentException(
          String.format("topk must be positive; %d is invalid", topk));
    if (topk == -1) topk = 10;
    if (indexName == null) indexName = "_default_text_index";
    queryString = Neo4jCommonUtils.makeLuceneQuery(queryString);
    boolean hasLabelConstraints = labelConstraints != null && !labelConstraints.isEmpty();
    StringBuilder sb = new StringBuilder();
    sb.append(
        String.format(
            "CALL db.index.fulltext.queryNodes(%s, ",
            Neo4jCommonUtils.escapeNeo4jStringLiteral(indexName)));
    sb.append(String.format("%s", Neo4jCommonUtils.escapeNeo4jStringLiteral(queryString)));
    sb.append(")\nYIELD node, score");
    if (hasLabelConstraints) {
      int index = 0;
      sb.append("\nWHERE (node:");
      for (String labelConstraint : labelConstraints)
        sb.append(
            String.format(
                "%s%s",
                index++ > 0 ? "|" : "", Neo4jCommonUtils.escapeNeo4jIdentifier(labelConstraint)));
      sb.append(")");
    }
    sb.append("\nRETURN node, score");
    sb.append(String.format("\nLIMIT %d", topk));
    String query = sb.toString();
    try (Session session = driver.session(SessionConfig.forDatabase(database))) {
      return session.readTransaction(
          tx -> {
            Result result = tx.run(query);
            List<Record> records = result.list();
            tx.commit();
            return records;
          });
    }
  }

  public List<Record> vectorSearch(
      @NonNull String label,
      @NonNull String propertyKey,
      float @NonNull [] queryVector,
      int topk,
      @Nullable String indexName,
      int efSearch) {
    if (topk != -1 && topk <= 0)
      throw new IllegalArgumentException(
          String.format("topk must be positive; %d is invalid", topk));
    if (topk == -1) topk = 10;
    if (efSearch != -1 && efSearch < topk)
      throw new IllegalArgumentException(
          String.format("efSearch must be greater than or equal to topk; %d is invalid", efSearch));
    refreshVectorIndexMeta();
    if (indexName == null) {
      Map<String, Set<String>> vectorIndexMeta = this.vectorIndexMeta;
      if (!vectorIndexMeta.containsKey(label)) {
        log.warn(
            "vector index not defined for label, return empty. label: {}, propertyKey: {}",
            label,
            propertyKey);
        return Collections.emptyList();
      }
      String vectorField = createVectorFieldName(propertyKey);
      if (!vectorIndexMeta.get(label).contains(vectorField)) {
        log.warn(
            "vector index not defined for field, return empty. label: {}, propertyKey: {}",
            label,
            propertyKey);
        return Collections.emptyList();
      }
    }
    if (indexName == null) indexName = createVectorIndexName(label, propertyKey);
    StringBuilder sb = new StringBuilder();
    sb.append(
        String.format(
            "CALL db.index.vector.queryNodes(%s, ",
            Neo4jCommonUtils.escapeNeo4jStringLiteral(indexName)));
    sb.append(String.format("%d, ", efSearch != -1 ? efSearch : topk));
    sb.append('[');
    for (int i = 0; i < queryVector.length; i++)
      sb.append(String.format("%s%.6g", i > 0 ? ", " : "", queryVector[i]));
    sb.append(']');
    sb.append(")\nYIELD node, score");
    sb.append("\nRETURN node, score");
    if (efSearch != -1) sb.append(String.format("\nLIMIT %d", topk));
    String query = sb.toString();
    try (Session session = driver.session(SessionConfig.forDatabase(database))) {
      return session.readTransaction(
          tx -> {
            Result result = tx.run(query);
            List<Record> records = result.list();
            tx.commit();
            return records;
          });
    }
  }

  public Map<String, Set<String>> getVectorIndexMeta() {
    String query = "SHOW VECTOR INDEX";
    try (Session session = driver.session(SessionConfig.forDatabase(database))) {
      return session.readTransaction(
          tx -> {
            Result result = tx.run(query);
            Map<String, Set<String>> vectorIndexMeta = new HashMap<>();
            while (result.hasNext()) {
              Record record = result.next();
              if (record.get("entityType").asString().equals("NODE")) {
                String label = record.get("labelsOrTypes").get(0).asString();
                String vectorField = record.get("properties").get(0).asString();
                if (vectorField.startsWith("_") && vectorField.endsWith("_vector")) {
                  if (!vectorIndexMeta.containsKey(label))
                    vectorIndexMeta.put(label, new HashSet<>());
                  vectorIndexMeta.get(label).add(vectorField);
                }
              }
            }
            tx.commit();
            return vectorIndexMeta;
          });
    }
  }

  public void refreshVectorIndexMeta() {
    refreshVectorIndexMeta(false);
  }

  public void refreshVectorIndexMeta(boolean force) {
    if (!force && System.nanoTime() - vectorIndexMetaTimestampNano < VECTOR_INDEX_META_TIMEOUT_NANO)
      return;
    this.vectorIndexMeta = getVectorIndexMeta();
    this.vectorIndexMetaTimestampNano = System.nanoTime();
  }

  private static <T> void addItem(@NonNull Set<T> set, @NonNull List<T> list, T item) {
    if (!set.contains(item)) {
      set.add(item);
      list.add(item);
    }
  }

  private static boolean shouldCreateTextIndex(@NonNull Property property) {
    String propertyKey = property.getName();
    if (propertyKey.equals("name")) {
      // Always create text index for the "name" property.
      return true;
    }
    IndexTypeEnum indexType = property.getIndexType();
    if (indexType != null) {
      switch (indexType) {
        case TEXT:
        case TEXT_AND_VECTOR:
          return true;
        default:
          return false;
      }
    }
    return false;
  }

  public Tuple2<List<String>, List<String>> collectTextIndexInfo(
      @NonNull List<BaseSPGType> schemaTypes) {
    Set<String> labelSet = new HashSet<>();
    List<String> labelList = new ArrayList<>();
    Set<String> propertyKeySet = new HashSet<>();
    List<String> propertyKeyList = new ArrayList<>();
    for (BaseSPGType schemaType : schemaTypes) {
      String label = schemaType.getName();
      List<Property> properties = schemaType.getProperties();
      if (properties != null) {
        Set<String> labelPropertyKeySet = new HashSet<>();
        List<String> labelPropertyKeyList = new ArrayList<>();
        for (Property property : properties) {
          if (shouldCreateTextIndex(property))
            addItem(labelPropertyKeySet, labelPropertyKeyList, property.getName());
        }
        if (!labelPropertyKeySet.isEmpty()) {
          addItem(labelSet, labelList, label);
          for (String propertyKey : labelPropertyKeyList)
            addItem(propertyKeySet, propertyKeyList, propertyKey);
        }
      }
    }
    return Tuple2.of(labelList, propertyKeyList);
  }
}
