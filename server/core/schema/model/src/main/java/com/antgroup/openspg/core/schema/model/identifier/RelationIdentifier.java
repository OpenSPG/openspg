package com.antgroup.openspg.core.schema.model.identifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false)
public class RelationIdentifier extends BaseSPGIdentifier {

  private final SPGTypeIdentifier start;

  private final PredicateIdentifier predicate;

  private final SPGTypeIdentifier end;

  public RelationIdentifier(
      SPGTypeIdentifier start, PredicateIdentifier predicate, SPGTypeIdentifier end) {
    super(SPGIdentifierTypeEnum.RELATION);
    this.start = start;
    this.predicate = predicate;
    this.end = end;
  }

  public static RelationIdentifier parse(String identifier) {
    String[] splits = identifier.split("_");
    if (splits.length != 3) {
      throw new IllegalArgumentException("illegal relation identifier=" + identifier);
    }
    return new RelationIdentifier(
        SPGTypeIdentifier.parse(splits[0]),
        new PredicateIdentifier(splits[1]),
        SPGTypeIdentifier.parse(splits[2]));
  }

  @Override
  public String toString() {
    return String.format("%s_%s_%s", start, predicate, end);
  }
}
