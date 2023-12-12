package com.antgroup.openspg.builder.model;

import com.antgroup.openspg.builder.model.pipeline.config.*;
import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import com.antgroup.openspg.builder.model.pipeline.enums.PropertyNormalizerTypeEnum;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import java.lang.reflect.Type;

public class BuilderJsonUtils {

  public static final String DEFAULT_TYPE_FIELD_NAME = "@type";

  public static Gson gson = null;

  static {
    gson =
        new GsonBuilder()
            .registerTypeAdapterFactory(
                RuntimeTypeAdapterFactory.of(BaseNodeConfig.class, DEFAULT_TYPE_FIELD_NAME)
                    .registerSubtype(CsvSourceNodeConfig.class, NodeTypeEnum.CSV_SOURCE.name())
                    .registerSubtype(
                        UserDefinedExtractNodeConfig.class,
                        NodeTypeEnum.USER_DEFINED_EXTRACT.name())
                    .registerSubtype(
                        LLMBasedExtractNodeConfig.class, NodeTypeEnum.LLM_BASED_EXTRACT.name())
                    .registerSubtype(
                        SPGTypeMappingNodeConfig.class, NodeTypeEnum.SPG_TYPE_MAPPING.name())
                    .registerSubtype(
                        RelationMappingNodeConfig.class, NodeTypeEnum.RELATION_MAPPING.name())
                    .registerSubtype(
                        SubGraphMappingNodeConfig.class, NodeTypeEnum.SUBGRAPH_MAPPING.name())
                    .registerSubtype(GraphStoreSinkNodeConfig.class, NodeTypeEnum.GRAPH_SINK.name())
                    .recognizeSubtypes())
            .registerTypeAdapterFactory(
                RuntimeTypeAdapterFactory.of(
                        PropertyNormalizerConfig.class, DEFAULT_TYPE_FIELD_NAME)
                    .registerSubtype(
                        OperatorPropertyNormalizerConfig.class,
                        PropertyNormalizerTypeEnum.OPERATOR.name())
                    .registerSubtype(
                        IdEqualsPropertyNormalizerConfig.class,
                        PropertyNormalizerTypeEnum.ID_EQUALS.name())
                    .recognizeSubtypes())
            .create();
  }

  /**
   * Serialize the given Java object into JSON string.
   *
   * @param obj Object
   * @return String representation of the JSON
   */
  public static String serialize(Object obj) {
    return gson.toJson(obj);
  }

  /**
   * Deserialize the given JSON string to Java object.
   *
   * @param <T> Type
   * @param body The JSON string
   * @param type The class to deserialize into
   * @return The deserialized Java object
   */
  public static <T> T deserialize(String body, Type type) {
    return gson.fromJson(body, type);
  }

  /**
   * Deserialize the given JSON string to Java object.
   *
   * @param <T> Type
   * @param body The JSON string
   * @param clazz The class to deserialize into
   * @return The deserialized Java object
   */
  public static <T> T deserialize(String body, Class<T> clazz) {
    return gson.fromJson(body, clazz);
  }
}
