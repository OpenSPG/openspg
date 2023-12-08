package com.antgroup.openspg.builder.core.physical.process.pattern;

import com.antgroup.openspg.builder.core.semantic.PropertyMounter;
import com.antgroup.openspg.builder.model.exception.PipelineConfigException;
import com.antgroup.openspg.builder.model.pipeline.config.MappingNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.MappingPatternEnum;
import com.antgroup.openspg.builder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.BuilderRecord;
import com.antgroup.openspg.core.schema.model.identifier.BaseSPGIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.RelationIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class SubgraphMappingPattern extends BaseMappingPattern {

  private final Graph<BaseSPGIdentifier, LabeledEdge> pattern;

  private ProjectSchema projectSchema;
  private List<BaseSPGIdentifier> mappingOrder;
  private Map<BaseSPGIdentifier, Map<String, List<PropertyMounter>>> propertyMounters;

  private Map<BaseSPGIdentifier, List<MappingNodeConfig.MappingFilter>> mappingFilters;
  private Map<BaseSPGIdentifier, List<MappingNodeConfig.MappingSchema>> mappingSchemas;
  private Map<BaseSPGIdentifier, List<MappingNodeConfig.MappingConfig>> mappingConfigs;

  public SubgraphMappingPattern() {
    super(MappingPatternEnum.SUBGRAPH);
    this.pattern = newGraph();
  }

  @Override
  public void loadMappingConfig(MappingNodeConfig config) {
    this.mappingFilters =
        config.getMappingFilters().stream()
            .collect(
                Collectors.groupingBy(
                    x -> MappingPatternFactory.identifierParse(x.getIdentifier())));
    this.mappingSchemas =
        config.getMappingSchemas().stream()
            .collect(
                Collectors.groupingBy(
                    x -> MappingPatternFactory.identifierParse(x.getIdentifier())));
    this.mappingConfigs =
        config.getMappingConfigs().stream()
            .collect(
                Collectors.groupingBy(
                    x -> MappingPatternFactory.identifierParse(x.getIdentifier())));
  }

  @Override
  public void loadAndCheckSchema(ProjectSchema schema) {
    this.projectSchema = schema;
    this.mappingOrder = mappingOrder();
  }

  @Override
  public void loadPropertyMounter() {
    propertyMounters = new HashMap<>(mappingSchemas.size());
    mappingSchemas.forEach(
        (identifier, schemas) -> propertyMounters.put(identifier, loadPropertyMounter(schemas)));
  }

  @Override
  public List<BaseRecord> mapping(List<BaseRecord> inputs) {
    List<BaseRecord> results = new ArrayList<>(inputs.size());
    for (BaseSPGIdentifier identifier : mappingOrder) {
      List<BaseRecord> leftRecords = new ArrayList<>(inputs.size());
      for (BaseRecord baseRecord : inputs) {
        BuilderRecord record = (BuilderRecord) baseRecord;
        BaseSPGIdentifier recordIdentifier = record.getIdentifier();
        if (recordIdentifier == null) {
          //          throw new BuilderRecordException();
        }

        if (recordIdentifier != identifier) {
          leftRecords.add(baseRecord);
          continue;
        }

        if (isFiltered(record, mappingFilters.get(recordIdentifier))) {
          continue;
        }

        BuilderRecord newRecord = doMapping(record, mappingConfigs.get(recordIdentifier));
        BaseSPGRecord spgRecord = null;
        switch (recordIdentifier.getIdentifierType()) {
          case SPG_TYPE:
            spgRecord =
                toSPGRecord(
                    newRecord, projectSchema.getByName((SPGTypeIdentifier) recordIdentifier));
            propertyMount((BaseAdvancedRecord) spgRecord, propertyMounters.get(recordIdentifier));
            break;
          case RELATION:
            spgRecord = toSPGRecord(newRecord, (Relation) null);
            break;
          default:
            //            throw new PipelineConfigException();
        }

        results.add(spgRecord);
      }
      inputs = leftRecords;
    }
    return results;
  }

  @Getter
  @AllArgsConstructor
  private static class LabeledEdge extends DefaultEdge {
    private final PredicateIdentifier predicate;

    @Override
    public String toString() {
      return String.format("%s_%s_%s", getSource(), predicate, getTarget());
    }
  }

  private static Graph<BaseSPGIdentifier, LabeledEdge> newGraph() {
    return GraphTypeBuilder.<BaseSPGIdentifier, DefaultEdge>directed()
        .allowingSelfLoops(false)
        .allowingMultipleEdges(true)
        .edgeClass(LabeledEdge.class)
        .weighted(false)
        .buildGraph();
  }

  private List<BaseSPGIdentifier> mappingOrder() {
    List<BaseSPGIdentifier> topoVertices = new ArrayList<>();

    try {
      TopologicalOrderIterator<BaseSPGIdentifier, LabeledEdge> iterator =
          new TopologicalOrderIterator<>(pattern);
      while (iterator.hasNext()) {
        BaseSPGIdentifier spgType = iterator.next();
        topoVertices.add(spgType);
      }

      Collections.reverse(topoVertices);
      for (LabeledEdge edge : pattern.edgeSet()) {
        topoVertices.add(RelationIdentifier.parse(edge.toString()));
      }
    } catch (IllegalArgumentException e) {
      throw new PipelineConfigException(e.getMessage(), e.getCause());
    }
    return topoVertices;
  }

  public SubgraphMappingPattern addIntoSubgraph(
      String startVertices, String edges, String endVertices) {
    if (StringUtils.isBlank(startVertices)) {
      throw new PipelineConfigException("");
    }

    List<SPGTypeIdentifier> ss =
        Arrays.stream(startVertices.split("\\|"))
            .map(SPGTypeIdentifier::parse)
            .collect(Collectors.toList());
    for (SPGTypeIdentifier s : ss) {
      pattern.addVertex(s);
    }
    if (edges != null) {
      if (StringUtils.isBlank(edges) || StringUtils.isBlank(endVertices)) {
        throw new PipelineConfigException("");
      }

      List<PredicateIdentifier> ps =
          Arrays.stream(edges.split("\\|"))
              .map(PredicateIdentifier::new)
              .collect(Collectors.toList());
      List<SPGTypeIdentifier> os =
          Arrays.stream(endVertices.split("\\|"))
              .map(SPGTypeIdentifier::parse)
              .collect(Collectors.toList());
      for (SPGTypeIdentifier o : os) {
        pattern.addVertex(o);
      }
      for (SPGTypeIdentifier s : ss) {
        for (PredicateIdentifier p : ps) {
          for (SPGTypeIdentifier o : os) {
            pattern.addEdge(s, o, new LabeledEdge(p));
          }
        }
      }
    }
    return this;
  }

  @Override
  public String toString() {
    return pattern.toString();
  }
}
