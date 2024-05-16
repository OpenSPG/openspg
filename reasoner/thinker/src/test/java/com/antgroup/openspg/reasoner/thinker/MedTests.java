package com.antgroup.openspg.reasoner.thinker;

public class MedTests {
  private String rule1 =
      "Define (a:Med.Examination/`尿酸`)-[:abnormalRule]->(c: string) {\n"
          + " R1: 男性 AND (value > 416) AND (a)-[: highExplain]->(c) \n"
          + "}\n"
          + "Description: \"对于男性，尿酸的正常范围是[150-416]\"";
}
