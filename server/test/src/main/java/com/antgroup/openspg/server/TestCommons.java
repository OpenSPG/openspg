package com.antgroup.openspg.server;

import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.identifier.PredicateIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.PropertyAdvancedConfig;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ParentTypeInfo;
import java.util.ArrayList;

public class TestCommons {

  public static final ParentTypeInfo THING =
      new ParentTypeInfo(1L, 1L, SPGTypeIdentifier.parse("THING"), new ArrayList<>());

  public static Property newProperty(String propertyName, String desc, BaseSPGType objectType) {
    return new Property(
        new BasicInfo<>(new PredicateIdentifier(propertyName), desc, desc),
        null,
        objectType.toRef(),
        Boolean.FALSE,
        new PropertyAdvancedConfig());
  }

  public static Relation newRelation(String propertyName, String desc, BaseSPGType objectType) {
    return new Relation(
        new BasicInfo<>(new PredicateIdentifier(propertyName), desc, desc),
        null,
        objectType.toRef(),
        Boolean.FALSE,
        new PropertyAdvancedConfig());
  }
}
