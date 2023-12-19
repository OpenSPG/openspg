package com.antgroup.openspg.server.testdata;

import static com.antgroup.openspg.server.testdata.StandardTypes.CHINA_MOBILE;
import static com.antgroup.openspg.server.testdata.TestCommons.*;
import static com.antgroup.openspg.server.testdata.TestCommons.THING;

import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.*;
import com.google.common.collect.Lists;

public class RiskMiningSchema {

  public static final String NAMESPACE = "RiskMining";

  /** basic type */
  public static final BasicType LONG = new BasicType.LongBasicType();

  public static final BasicType TEXT = new BasicType.TextBasicType();
  public static final BasicType DOUBLE = new BasicType.DoubleBasicType();

  /** concept type */
  public static final ConceptType TAX_OF_RISK_USER =
      new ConceptType(
          new BasicInfo<>(newSPGTypeIdentifier("TaxOfRiskUser")),
          THING,
          Lists.newArrayList(),
          Lists.newArrayList(),
          new SPGTypeAdvancedConfig(),
          new ConceptLayerConfig("isA", Lists.newArrayList()),
          new ConceptTaxonomicConfig(newSPGTypeIdentifier("Person")),
          null);

  public static final ConceptType TAX_OF_RISK_APP =
      new ConceptType(
          new BasicInfo<>(newSPGTypeIdentifier("TaxOfRiskApp")),
          THING,
          Lists.newArrayList(),
          Lists.newArrayList(),
          new SPGTypeAdvancedConfig(),
          new ConceptLayerConfig("isA", Lists.newArrayList()),
          new ConceptTaxonomicConfig(newSPGTypeIdentifier("App")),
          null);

  /** entity type */
  public static final EntityType CERT =
      new EntityType(
          new BasicInfo<>(newSPGTypeIdentifier("Cert")),
          THING,
          Lists.newArrayList(
              newProperty("id", "id", TEXT),
              newProperty("name", "name", TEXT),
              newProperty("certNum", "证书编号", TEXT)),
          Lists.newArrayList(),
          new SPGTypeAdvancedConfig());

  public static final EntityType APP =
      new EntityType(
          new BasicInfo<>(newSPGTypeIdentifier("App")),
          THING,
          Lists.newArrayList(
              newProperty("id", "id", TEXT),
              newProperty("name", "name", TEXT),
              newProperty("riskMark", "风险标记", TEXT),
              newProperty("useCert", "使用证书", CERT),
              newProperty("belongTo", "属于", TAX_OF_RISK_APP)),
          Lists.newArrayList(),
          new SPGTypeAdvancedConfig());

  public static final EntityType COMPANY =
      new EntityType(
          new BasicInfo<>(newSPGTypeIdentifier("Company")),
          THING,
          Lists.newArrayList(
              newProperty("id", "id", TEXT),
              newProperty("name", "name", TEXT),
              newProperty("hasPhone", "电话号码", CHINA_MOBILE)),
          Lists.newArrayList(newRelation("hasCert", "拥有证书", CERT)),
          new SPGTypeAdvancedConfig());

  static {
    COMPANY.getRelations().add(newRelation("holdShare", "持股", COMPANY));
  }

  public static final EntityType DEVICE =
      new EntityType(
          new BasicInfo<>(newSPGTypeIdentifier("Device")),
          THING,
          Lists.newArrayList(
              newProperty("id", "id", TEXT),
              newProperty("name", "name", TEXT),
              newProperty("umid", "设备umid", TEXT),
              newProperty("install", "安装", APP)),
          Lists.newArrayList(),
          new SPGTypeAdvancedConfig());

  public static final EntityType PERSON =
      new EntityType(
          new BasicInfo<>(newSPGTypeIdentifier("Person")),
          THING,
          Lists.newArrayList(
              newProperty("id", "id", TEXT),
              newProperty("name", "name", TEXT),
              newProperty("age", "年龄", LONG),
              newProperty("hasPhone", "电话号码", CHINA_MOBILE),
              newProperty("belongTo", "属于", TAX_OF_RISK_USER)),
          Lists.newArrayList(),
          new SPGTypeAdvancedConfig());

  private static SPGTypeIdentifier newSPGTypeIdentifier(String identifier) {
    return SPGTypeIdentifier.parse(NAMESPACE + "_" + identifier);
  }
}
