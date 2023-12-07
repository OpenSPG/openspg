package com.antgroup.openspg.core.schema.model.identifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false)
public class RelationIdentifier extends BaseSPGIdentifier {

  private final SPGTypeIdentifier start;

  private final PredicateIdentifier predicate;

  private final SPGTypeIdentifier end;

  protected RelationIdentifier(
      SPGTypeIdentifier start, PredicateIdentifier predicate, SPGTypeIdentifier end) {
    super(SPGIdentifierTypeEnum.RELATION);
    this.start = start;
    this.predicate = predicate;
    this.end = end;
  }

  @Override
  public String toString() {
    return String.format("%s_%s_%s", start, predicate, end);
  }
}
