package com.antgroup.openspg.builder.core.physical.util;

import com.antgroup.openspg.builder.model.pipeline.config.MappingNodeConfig;
import com.antgroup.openspg.core.schema.model.identifier.BaseSPGIdentifier;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class MappingProcessorHelper {

  private final SubgraphPattern pattern;

  private final Map<BaseSPGIdentifier, List<MappingNodeConfig.MappingFilter>> mappingFiltersById =
      new HashMap<>();
  private final Map<BaseSPGIdentifier, List<MappingNodeConfig.MappingSchema>> mappingSchemasById =
      new HashMap<>();
  private final Map<BaseSPGIdentifier, List<MappingNodeConfig.MappingConfig>> mappingConfigsById =
      new HashMap<>();

  public MappingProcessorHelper(MappingNodeConfig config) {
    pattern = SubgraphPattern.from(config.getElements());
    if (pattern.isSingleVertex() || pattern.isSingleEdge()) {
      mappingFiltersById.put(config.getElements(), config.getMappingFilters());
      mappingSchemasById.put(config.getElements(), config.getMappingSchemas());
      mappingConfigsById.put(config.getElements(), config.getMappingConfigs());
    } else {
      mappingFiltersById.putAll(
          config.getMappingFilters().stream()
              .collect(Collectors.groupingBy(MappingNodeConfig.MappingFilter::getIdentifier)));
      mappingSchemasById.putAll(
          config.getMappingSchemas().stream()
              .collect(Collectors.groupingBy(MappingNodeConfig.MappingSchema::getIdentifier)));
      mappingConfigsById.putAll(
          config.getMappingConfigs().stream()
              .collect(Collectors.groupingBy(MappingNodeConfig.MappingConfig::getIdentifier)));
    }
  }

  public void checkPattern(ProjectSchema schema) {
    // todo
  }
}
