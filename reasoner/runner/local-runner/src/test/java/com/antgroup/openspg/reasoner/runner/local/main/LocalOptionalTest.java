/*
 * Copyright 2023 Ant Group CO., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.reasoner.runner.local.main;

import com.antgroup.openspg.reasoner.common.constants.Constants;
import com.antgroup.openspg.reasoner.common.graph.edge.IEdge;
import com.antgroup.openspg.reasoner.common.graph.property.IProperty;
import com.antgroup.openspg.reasoner.common.graph.vertex.IVertex;
import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.lube.catalog.impl.PropertyGraphCatalog;
import com.antgroup.openspg.reasoner.recorder.DefaultRecorder;
import com.antgroup.openspg.reasoner.runner.local.LocalReasonerRunner;
import com.antgroup.openspg.reasoner.runner.local.load.graph.AbstractLocalGraphLoader;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerResult;
import com.antgroup.openspg.reasoner.runner.local.model.LocalReasonerTask;
import com.antgroup.openspg.reasoner.util.Convert2ScalaUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import scala.Tuple2;
import scala.collection.immutable.Set;

public class LocalOptionalTest {
  @Test
  public void doTestOptional4() {
    String dsl =
        "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`PI-RADs`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // PI-RADs：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"PI-RAD\", \"PIRAD\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos: Rpos0\n"
            + "\n"
            + "    // PI-RADs：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"PI-RAD\", \"PIRAD\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺-低回声`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺-低回声：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"外周带\", \"移行带\", \"周围带\", \"\", \"外腺\", \"内腺\"]) and contains(s.inspection, [\"b超\",\"MRI\"]) and contains(s.status, [\"低回声结节\", \"混杂回声\", \"低回声占位\", \"混杂信号\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos: Rpos0\n"
            + "\n"
            + "    // 前列腺-低回声：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"外周带\", \"移行带\", \"周围带\", \"\", \"外腺\", \"内腺\"]) and contains(s.inspection, [\"b超\"]) and contains(s.status, [\"\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺-体积`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺-体积：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"体积\"]) and contains(s.inspection, [\"b超\"]) and contains(s.status, [\"\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos: Rpos0\n"
            + "\n"
            + "    // 前列腺-体积：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"体积\"]) and contains(s.inspection, [\"b超\"]) and contains(s.status, [\"\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺-外周带异常`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺-外周带异常：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"外腺\", \"外周带\", \"周围带\"]) and contains(s.inspection, [\"b超\"]) and contains(s.status, [\"形态欠规则\", \"内外腺分界模糊\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos: Rpos0\n"
            + "\n"
            + "    // 前列腺-外周带异常：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"外腺\", \"外周带\", \"周围带\"]) and contains(s.inspection, [\"b超\"]) and contains(s.status, [\"轮廓规则\", \"分界清晰\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺-强回声`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺-强回声：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"尿道周围\", \"内外腺\", \"外周带\", \"移行带\", \"周围带\", \"\", \"外腺\", \"内腺\"]) and contains(s.inspection, [\"b超\"]) and contains(s.status, [\"强回声团\", \"内见散在斑点状强回声\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos: Rpos0\n"
            + "\n"
            + "    // 前列腺-强回声：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"尿道周围\", \"内外腺\", \"外周带\", \"移行带\", \"周围带\", \"\", \"外腺\", \"内腺\"]) and contains(s.inspection, [\"b超\"]) and contains(s.status, [\"\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺-影像异常`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺-影像异常：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"结节\", \"团块\", \"病灶\", \"肿块\", \"异常信号灶\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"累及中央带\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos1: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"外周带\", \"移行带\", \"周围带\", \"\", \"外腺\", \"内腺\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"略低信号结节\", \"\", \"分界不清\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos: Rpos0 or Rpos1\n"
            + "\n"
            + "    // 前列腺-影像异常：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"结节\", \"团块\", \"病灶\", \"肿块\", \"异常信号灶\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg1: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"外周带\", \"移行带\", \"周围带\", \"\", \"外腺\", \"内腺\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"未见明显异常\", \"未见异常信号\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0 or Rneg1\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-侵犯精囊`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺肿瘤-侵犯精囊：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"精囊腺\", \"精囊\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"见强化影\", \"异常信号结节影\", \"点状高信号影\", \"分界不清\", \"信号不均\"])\n"
            + "    Rpos1: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"结节\", \"团块\", \"病灶\", \"肿块\", \"异常信号灶\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"累及精囊腺\", \"与精囊腺分界不清\"])\n"
            + "    Rpos2: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"膀胱精囊角\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"消失\"])\n"
            + "    Rpos: Rpos0 or Rpos1 or Rpos2\n"
            + "\n"
            + "    // 前列腺肿瘤-侵犯精囊：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"精囊腺\", \"精囊\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"未见异常\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg1: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"结节\", \"团块\", \"病灶\", \"肿块\", \"异常信号灶\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg2: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"膀胱精囊角\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"存在\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0 or Rneg1 or Rneg2\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-区域淋巴结转移`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺肿瘤-区域淋巴结转移：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"髂血管旁淋巴结\", \"盆腔淋巴结\", \"盆底淋巴结\", \"腹股沟淋巴结\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\">10mm\", \"多发结节样\", \"稍高信号影\", \"散在淋巴结\", \"斑片状异常信号影\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos: Rpos0\n"
            + "\n"
            + "    // 前列腺肿瘤-区域淋巴结转移：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"髂血管旁淋巴结\", \"盆腔淋巴结\", \"盆底淋巴结\", \"腹股沟淋巴结\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"未见明显肿大淋巴结影\", \"\", \"多发小淋巴结\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-固定或侵犯除精囊外的其他邻近组织`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺肿瘤-固定或侵犯除精囊外的其他邻近组织：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"病灶与膀胱颈部分界\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"无法分界\", \"分界不清\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos1: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"结节\", \"团块\", \"病灶\", \"肿块\", \"异常信号灶\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"累及膀胱\", \"与膀胱分界不清\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos2: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"结节\", \"团块\", \"病灶\", \"肿块\", \"异常信号灶\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"累及直肠\", \"与直肠分界不清\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos3: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"直肠膀胱间隙\", \"前列腺直肠分界\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"消失\", \"分界不清\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos4: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"膀胱\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"结节样低信号影\", \"多发囊袋状突起\", \"团块状异常信号\", \"异常强化灶\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos: Rpos0 or Rpos1 or Rpos2 or Rpos3 or Rpos4\n"
            + "\n"
            + "    // 前列腺肿瘤-固定或侵犯除精囊外的其他邻近组织：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"病灶与膀胱颈部分界\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"清晰\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg1: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"结节\", \"团块\", \"病灶\", \"肿块\", \"异常信号灶\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg2: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"结节\", \"团块\", \"病灶\", \"肿块\", \"异常信号灶\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg3: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"直肠膀胱间隙\", \"前列腺直肠分界\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"清晰\", \"存在\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg4: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"膀胱\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"光滑\", \"未见异常\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0 or Rneg1 or Rneg2 or Rneg3 or Rneg4\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-突破前列腺包膜`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺肿瘤-突破前列腺包膜：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"包膜\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"突破\", \"侵犯\", \"累及\", \"不清\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos1: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"结节\", \"团块\", \"病灶\", \"肿块\", \"异常信号灶\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"突破包膜\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos: Rpos0 or Rpos1\n"
            + "\n"
            + "    // 前列腺肿瘤-突破前列腺包膜：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"包膜\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"完整\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg1: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"结节\", \"团块\", \"病灶\", \"肿块\", \"异常信号灶\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0 or Rneg1\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-肝转移`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺肿瘤-肝转移：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"腹部\"]) and contains(s.entity, [\"肝脏\"]) and contains(s.inspection, [\"CT\", \"MRI\", \"PET\"]) and contains(s.status, [\"多发转移\", \"多发病灶\", \"可疑转移\", \"多发结节灶\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos: Rpos0\n"
            + "\n"
            + "    // 前列腺肿瘤-肝转移：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"腹部\"]) and contains(s.entity, [\"肝脏\"]) and contains(s.inspection, [\"CT\", \"MRI\", \"PET\"]) and contains(s.status, [\"\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-肺转移`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺肿瘤-肺转移：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"胸部\"]) and contains(s.entity, [\"肺\"]) and contains(s.inspection, [\"CT\", \"MRI\", \"PET\"]) and contains(s.status, [\"多发转移\", \"多发病灶\", \"可疑转移\", \"多发结节灶\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos: Rpos0\n"
            + "\n"
            + "    // 前列腺肿瘤-肺转移：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"胸部\"]) and contains(s.entity, [\"肺\"]) and contains(s.inspection, [\"CT\", \"MRI\", \"PET\"]) and contains(s.status, [\"\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-脑转移`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺肿瘤-脑转移：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"头颅\"]) and contains(s.entity, [\"脑\"]) and contains(s.inspection, [\"CT\", \"MRI\", \"PET\"]) and contains(s.status, [\"多发转移\", \"多发病灶\", \"可疑转移\", \"多发结节灶\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos: Rpos0\n"
            + "\n"
            + "    // 前列腺肿瘤-脑转移：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"头颅\"]) and contains(s.entity, [\"脑\"]) and contains(s.inspection, [\"CT\", \"MRI\", \"PET\"]) and contains(s.status, [\"\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-非区域淋巴结转移`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺肿瘤-非区域淋巴结转移：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"盆腔\", \"腹部\", \"胸部\", \"头颅\"]) and contains(s.entity, [\"锁骨上\", \"纵膈\", \"腹主动脉旁\", \"腹股沟\", \"淋巴结\"]) and contains(s.inspection, [\"CT\", \"MRI\", \"PET\"]) and contains(s.status, [\">10mm\", \"多发结节样\", \"稍高信号影\", \"散在淋巴结\", \"斑片状异常信号影\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos: Rpos0\n"
            + "\n"
            + "    // 前列腺肿瘤-非区域淋巴结转移：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"盆腔\", \"腹部\", \"胸部\", \"头颅\"]) and contains(s.entity, [\"锁骨上\", \"纵膈\", \"腹主动脉旁\", \"腹股沟\", \"淋巴结\"]) and contains(s.inspection, [\"CT\", \"MRI\", \"PET\"]) and contains(s.status, [\"未见明显肿大淋巴结影\", \"\", \"多发小淋巴结\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-骨转移`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺肿瘤-骨转移：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"骨\"]) and contains(s.entity, [\"全身骨骼\", \"骨盆各骨骨质\", \"骨盆\", \"坐骨\", \"耻骨\", \"髋骨\", \"骶骨\", \"骶椎\", \"骶髂骨\", \"骶椎\", \"肋骨\", \"颅骨\", \"腰椎\", \"胸椎\", \"脊柱\", \"股骨\", \"肱骨\", \"胫骨\", \"腓骨\", \"尺骨\", \"桡骨\"]) and contains(s.inspection, [\"CT\", \"MRI\", \"PET\"]) and contains(s.status, [\"骨转移\", \"\", \"多发骨异常信号/病灶\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos1: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"骨盆各骨骨质\", \"骨盆\", \"坐骨\", \"耻骨\", \"髋骨\", \"骶骨\", \"骶椎\", \"骶髂骨\", \"骶椎\", \"腰椎\", \"脊柱\", \"股骨\", \"肱骨\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"骨质结构破坏\", \"多发结节样稍高信号影\", \"散在小斑片诸序列低信号影\", \"多发片状信号异常区\", \"斑片状异常信号影\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos2: contains(s.bodypart, [\"全身骨\"]) and contains(s.entity, [\"全身骨骼\", \"骨盆\", \"各骨骨质\", \"骨盆\", \"坐骨\", \"耻骨\", \"髋骨\", \"骶骨\", \"骶椎\", \"骶髂骨\", \"骶椎\", \"肋骨\", \"颅骨\", \"腰椎\", \"胸椎\", \"脊柱\", \"股骨\", \"肱骨\", \"胫骨\", \"腓骨\", \"尺骨\", \"桡骨\"]) and contains(s.inspection, [\"骨扫描\"]) and contains(s.status, [\"肿瘤骨转移\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos: Rpos0 or Rpos1 or Rpos2\n"
            + "\n"
            + "    // 前列腺肿瘤-骨转移：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"骨\"]) and contains(s.entity, [\"全身骨骼\", \"骨盆各骨骨质\", \"骨盆\", \"坐骨\", \"耻骨\", \"髋骨\", \"骶骨\", \"骶椎\", \"骶髂骨\", \"骶椎\", \"肋骨\", \"颅骨\", \"腰椎\", \"胸椎\", \"脊柱\", \"股骨\", \"肱骨\", \"胫骨\", \"腓骨\", \"尺骨\", \"桡骨\"]) and contains(s.inspection, [\"CT\", \"MRI\", \"PET\"]) and contains(s.status, [\"良性病变\", \"退变\", \"退行性变\", \"外伤\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg1: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"骨盆各骨骨质\", \"骨盆\", \"坐骨\", \"耻骨\", \"髋骨\", \"骶骨\", \"骶椎\", \"骶髂骨\", \"骶椎\", \"腰椎\", \"脊柱\", \"股骨\", \"肱骨\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"未见明显异常信号改变\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg2: contains(s.bodypart, [\"全身骨\"]) and contains(s.entity, [\"全身骨骼\", \"骨盆\", \"各骨骨质\", \"骨盆\", \"坐骨\", \"耻骨\", \"髋骨\", \"骶骨\", \"骶椎\", \"骶髂骨\", \"骶椎\", \"肋骨\", \"颅骨\", \"腰椎\", \"胸椎\", \"脊柱\", \"股骨\", \"肱骨\", \"胫骨\", \"腓骨\", \"尺骨\", \"桡骨\"]) and contains(s.inspection, [\"骨扫描\"]) and contains(s.status, [\"良性病变\", \"退变\", \"退行性变\", \"外伤\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0 or Rneg1 or Rneg2\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`确诊-前列腺肿瘤`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 确诊-前列腺肿瘤：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"前列腺癌\"]) and contains(s.inspection, [\"穿刺\"]) and contains(s.status, [\"腺泡腺癌，腺癌，导管内癌，导管腺癌\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rpos: Rpos0\n"
            + "\n"
            + "    // 确诊-前列腺肿瘤：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"前列腺癌\"]) and contains(s.inspection, [\"穿刺\"]) and contains(s.status, [\"前列腺增生\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-病理组织占比`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    p.indexValue = \"\"\n"
            + "    Fail(\"未匹配到条件\"): false\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺增生手术后病理确诊前列腺癌`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    p.indexValue = \"\"\n"
            + "    Fail(\"未匹配到条件\"): false\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o:`ProfMedV1.Index`/`前列腺-影像学检查`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    p.indexValue = \"TRUE\"\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺-侵犯两叶`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    p.indexValue = \"\"\n"
            + "    Fail(\"未匹配到条件\"): false\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-远处淋巴结转移`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    p.indexValue = \"\"\n"
            + "    Fail(\"未匹配到条件\"): false\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺-超过单叶的1/2`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    p.indexValue = \"\"\n"
            + "    Fail(\"未匹配到条件\"): false\n"
            + "  }\n"
            + "}\n"
            + "// TODO \n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺-指检异常`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    p.indexValue = \"\"\n"
            + "    Fail(\"未匹配到条件\"): false\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "// TODO \n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o:`ProfMedV1.Index`/`前列腺肿瘤-区域淋巴转移`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    p.indexValue = \"\"\n"
            + "    Fail(\"未匹配到条件\"): false\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "// TODO \n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o:`ProfMedV1.Index`/`确诊-前列腺癌`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    p.indexValue = \"\"\n"
            + "    Fail(\"未匹配到条件\"): false\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "Define (s: ProfMedV1.Patient)-[p: belongTo]->(o: `ProfMedV1.Disease`/`前列腺癌`) {\n"
            + "    GraphStructure {\n"
            + "\t\t(s)-[p1:inspectionIndex]->(o1:ProfMedV1.PatientIndex)\n"
            + "    (o1)-[p21:belongTo]->?(o21: `ProfMedV1.Index`/`确诊-前列腺癌`)\n"
            + "  \t(o1)-[p22:belongTo]->?(o22: `ProfMedV1.Index`/`前列腺-影像学检查`)\n"
            + "  \t// T-Stage\n"
            + "    (o1)-[pT03:belongTo]->?(oT03: `ProfMedV1.Index`/`前列腺增生手术后病理确诊前列腺癌`)\n"
            + "    (o1)-[pT04:belongTo]->?(oT04: `ProfMedV1.Index`/`前列腺肿瘤-病理组织占比`)\n"
            + "    (o1)-[pT05:belongTo]->?(oT05: `ProfMedV1.Index`/`前列腺肿瘤-固定或侵犯除精囊外的其他邻近组织`)\n"
            + "    (o1)-[pT06:belongTo]->?(oT06: `ProfMedV1.Index`/`前列腺肿瘤-侵犯精囊`)\n"
            + "    (o1)-[pT07:belongTo]->?(oT07: `ProfMedV1.Index`/`前列腺肿瘤-突破前列腺包膜`)\n"
            + "    (o1)-[pT08:belongTo]->?(oT08: `ProfMedV1.Index`/`前列腺-影像异常`)\n"
            + "    (o1)-[pT09:belongTo]->?(oT09: `ProfMedV1.Index`/`前列腺-指检异常`)\n"
            + "    (o1)-[pT10:belongTo]->?(oT10: `ProfMedV1.Index`/`前列腺-侵犯两叶`)\n"
            + "    (o1)-[pT11:belongTo]->?(oT11: `ProfMedV1.Index`/`前列腺-超过单叶的1/2`)\n"
            + "    // N-Stage\n"
            + "    (o1)-[pN03:belongTo]->?(oN03: `ProfMedV1.Index`/`前列腺肿瘤-区域淋巴转移`)\n"
            + "    // M-Stage\n"
            + "    (o1)-[pM03:belongTo]->?(oM03: `ProfMedV1.Index`/`前列腺肿瘤-脑转移`)\n"
            + "    (o1)-[pM04:belongTo]->?(oM04: `ProfMedV1.Index`/`前列腺肿瘤-肺转移`)\n"
            + "    (o1)-[pM05:belongTo]->?(oM05: `ProfMedV1.Index`/`前列腺肿瘤-肝转移`)\n"
            + "    (o1)-[pM06:belongTo]->?(oM06: `ProfMedV1.Index`/`前列腺肿瘤-骨转移`)\n"
            + "    (o1)-[pM07:belongTo]->?(oM07: `ProfMedV1.Index`/`前列腺肿瘤-远处淋巴结转移`)\n"
            + "    }\n"
            + "    Rule {\n"
            + "      R11(\"是否存在指标-确诊-前列腺癌\"): exists(p21)\n"
            + "      R12(\"确诊-前列腺癌\"): R11 and p21.indexValue == \"TRUE\"\n"
            + "\n"
            + "      R21(\"是否存在指标-前列腺-影像学检查\"): exists(p22)\n"
            + "      R22(\"前列腺-影像学检查\"): R21 and p22.indexValue == \"TRUE\"\n"
            + "\n"
            + "    \t// T-Stage\n"
            + "      RT031(\"是否存在指标-前列腺增生手术后病理确诊前列腺癌\"): exists(pT03)\n"
            + "      RT032(\"前列腺增生手术后病理确诊前列腺癌\"): RT031 and pT03.indexValue == \"TRUE\"\n"
            + "      \n"
            + "      //RT041(\"是否存在指标-前列腺肿瘤-病理组织占比\"): exists(pT04)\n"
            + "      //RT042(\"前列腺肿瘤-病理组织占比\"): (RT041 and pT04.indexValue)\n"
            + "      \n"
            + "      RT051(\"是否存在指标-前列腺肿瘤-固定或侵犯除精囊外的其他邻近组织\"): exists(pT05)\n"
            + "      RT052(\"前列腺肿瘤-固定或侵犯除精囊外的其他邻近组织\"): RT051 and pT05.indexValue == \"TRUE\"\n"
            + "      \n"
            + "      RT061(\"是否存在指标-前列腺肿瘤-侵犯精囊\"): exists(pT06)\n"
            + "      RT062(\"前列腺肿瘤-侵犯精囊\"): RT061 and pT06.indexValue == \"TRUE\"\n"
            + "      \n"
            + "      RT071(\"是否存在指标-前列腺肿瘤-突破前列腺包膜\"): exists(pT07)\n"
            + "      RT072(\"前列腺肿瘤-突破前列腺包膜\"): RT071 and pT07.indexValue == \"TRUE\"\n"
            + "      \n"
            + "      RT081(\"是否存在指标-前列腺-影像异常\"): exists(pT08)\n"
            + "      RT082(\"前列腺-影像异常\"): RT081 and pT08.indexValue == \"TRUE\"\n"
            + "      \n"
            + "      RT091(\"是否存在指标-前列腺-指检异常\"): exists(pT09)\n"
            + "      RT092(\"前列腺-指检异常\"): RT091 and pT09.indexValue == \"TRUE\"\n"
            + "      \n"
            + "      //RT101(\"是否存在指标-前列腺肿瘤-侵犯两叶\"): exists(pT10)\n"
            + "      //RT102(\"前列腺肿瘤-侵犯两叶\"): RT101 and pT10.indexValue == \"TRUE\"\n"
            + "      \n"
            + "      //RT111(\"是否存在指标-前列腺肿瘤-超过单叶的1/2\"): exists(pT11)\n"
            + "      //RT112(\"前列腺肿瘤-超过单叶的1/2\"): RT111 and pT11.indexValue == \"TRUE\"\n"
            + "\n"
            + "      //T4(\"T4分期\"): not RT051\n"
            + "      p.tStage = rule_value(not R12, \"未确诊无法判断T分期\", rule_value(RT032, \"T1\", rule_value(not R22, \"Tx\", rule_value(RT052, \"T4\", rule_value(RT062, \"T3b\", rule_value(RT072, \"T3a\", rule_value(RT082 or RT092, \"T2\", \"T0\")))))))\n"
            + "\n"
            + "      // N-Stage\n"
            + "      RN31(\"是否存在指标-前列腺癌-区域淋巴转移\"): exists(pN03)\n"
            + "      RN32(\"前列腺癌-区域淋巴转移\"): RN31 and pN03.indexValue == \"TRUE\"\n"
            + "       \n"
            + "      //N1(\"N1分期\"): R12 and RN32\n"
            + "      //N0(\"N0分期\"): R12 and (not RN32) and R22\n"
            + "      //Nx(\"Nx分期\"): R12 and (not R22)\n"
            + "      //p.nStage = rule_value(N1, \"N1\", rule_value(N0, \"N0\", rule_value(Nx, \"Nx\", \"无法判断\")))\n"
            + "      p.nStage = rule_value(not R12, \"未确诊无法判断T分期\", rule_value(not R22, \"Nx\", rule_value(RN32, \"N1\", \"N0\")))\n"
            + "      \n"
            + "    \t// M-Stage\n"
            + "      RM31(\"是否存在指标-前列腺癌-脑转移\"): exists(pM03)\n"
            + "      RM32(\"前列腺癌-脑转移\"): RM31 and pM03.indexValue == \"TRUE\"\n"
            + "    \n"
            + "      RM41(\"是否存在指标-前列腺癌-肺转移\"): exists(pM04)\n"
            + "      RM42(\"前列腺癌-肺转移\"): RM41 and pM04.indexValue == \"TRUE\"\n"
            + "    \n"
            + "      RM51(\"是否存在指标-前列腺癌-肝转移\"): exists(pM05)\n"
            + "      RM52(\"前列腺癌-肝转移\"): RM51 and pM05.indexValue == \"TRUE\"\n"
            + "    \n"
            + "      RM61(\"是否存在指标-前列腺癌-骨转移\"): exists(pM06)\n"
            + "      RM62(\"前列腺癌-骨转移\"): RM61 and pM06.indexValue == \"TRUE\"\n"
            + "    \n"
            + "      RM71(\"是否存在指标-前列腺癌-远处淋巴结转移\"): exists(pM07)\n"
            + "      RM72(\"前列腺癌-远处淋巴结转移\"): RM71 and pM07.indexValue == \"TRUE\"\n"
            + "\n"
            + "    \n"
            + "      p.mStage = rule_value(not R12, \"未确诊无法判断T分期\", rule_value(not R22, \"Mx\", rule_value(RM32 or RM42 or RM52, \"M1c\", rule_value(RM62, \"M1b\", rule_value(RM72, \"M1a\", \"M0\")))))\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "    (s: ProfMedV1.Patient)-[p: belongTo]->(o: `ProfMedV1.Disease`/`前列腺癌`)\n"
            + "}\n"
            + "Rule {\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, p.mStage, p.tStage)\n"
            + "}\n";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.LocalOptionalTest$MedicalGraphLoader3");

    // add mock catalog
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put(
        "ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "entity", "inspection", "index", "status", "bodypart")));
    schema.put("ProfMedV1.Disease", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Patient", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Index", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    //        schema.put("ProfMedV1.Patient_belongTo_ProfMedV1.Disease",
    // Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("value", "nStage")));
    schema.put(
        "ProfMedV1.PatientIndex_belongTo_ProfMedV1.Index",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("indexValue")));
    schema.put(
        "ProfMedV1.Patient_inspectionIndex_ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);
    task.getParams().put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);

    task.setExecutionRecorder(new DefaultRecorder());

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    System.out.println(task.getExecutionRecorder().toReadableString());

    Assert.assertEquals(result.getRows().size(), 1);
    Assert.assertEquals(result.getRows().get(0)[0], "u1");
    Assert.assertEquals(result.getRows().get(0)[1], "未确诊无法判断T分期");
    Assert.assertEquals(result.getRows().get(0)[2], "未确诊无法判断T分期");
  }

  @Test
  public void doTestOptional3() {
    String dsl =
        "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-突破前列腺包膜`) {\n"
            + "\tGraphStructure {\n"
            + "      (s)\n"
            + "\t}\n"
            + "\tRule {\n"
            + "    // 前列腺肿瘤突破前列腺包膜：是\n"
            + "\t\tR01: contains_any(s.entity, [\"包膜\"]) and contains_any(s.inspection, [\"MRI\", \"CT\", \"PETCT\", \"PETMRI\"]) and contains_any(s.status, [\"累及\", \"侵犯\", \"膨隆\", \"突破包膜\"]) and not contains_any(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "\t\tR02: contains_any(s.entity, [\"神经血管束\", \"血管神经束\", \"DVC\"]) and contains_any(s.inspection, [\"MRI\", \"PETMRI\"]) and contains_any(s.status, [\"累及\", \"侵犯\"]) and not contains_any(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "\t\tR03: R01 or R02\n"
            + "\n"
            + "    // 前列腺肿瘤突破前列腺包膜：否\n"
            + "    R11: contains_any(s.entity, [\"包膜\"]) and contains_any(s.inspection, [\"MRI\", \"CT\", \"PETCT\", \"PETMRI\"]) and contains_any(s.status, [\"完整\"]) and not contains_any(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "\t\tR12: contains_any(s.entity, [\"神经血管束\", \"血管神经束\", \"DVC\"]) and contains_any(s.inspection, [\"MRI\", \"PETMRI\"]) and contains_any(s.status, [\"完整\"]) and not contains_any(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "\t\tR13: R11 or R12\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(R03, \"TRUE\", rule_value(R13, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "\t}\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "\t\t(s1:ProfMedV1.Patient)-[p1:inspectionIndex]->(o1:ProfMedV1.PatientIndex)-[pT07: belongTo]->?(o: `ProfMedV1.Index`/`前列腺肿瘤-突破前列腺包膜`)\n"
            + " }\n"
            + " Rule {\n"
            + "\tRT071(\"是否存在指标-前列腺肿瘤-突破前列腺包膜\"): exists(pT07)\n"
            + "      RT072(\"前列腺肿瘤-突破前列腺包膜\"): RT071 and pT07.indexValue == \"TRUE\"\n"
            + "     num = group(s1).countIf(pT07.indexValue == \"TRUE\", pT07)\n"
            + "     res = rule_value(num > 0, \"T3a\", \"无法判断\")\n"
            + " }\n"
            + " Action {\n"
            + " \tget(s1.id, o1.id, res)\n"
            + " }";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.LocalOptionalTest$MedicalGraphLoader3");

    // add mock catalog
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put(
        "ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "entity", "inspection", "index", "status", "bodypart")));
    schema.put("ProfMedV1.Disease", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Patient", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Index", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    //        schema.put("ProfMedV1.Patient_belongTo_ProfMedV1.Disease",
    // Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("value", "nStage")));
    schema.put(
        "ProfMedV1.PatientIndex_belongTo_ProfMedV1.Index",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("indexValue")));
    schema.put(
        "ProfMedV1.Patient_inspectionIndex_ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);
    task.getParams().put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);

    task.setExecutionRecorder(new DefaultRecorder());

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    System.out.println(task.getExecutionRecorder().toReadableString());

    Assert.assertEquals(result.getRows().size(), 1);
    Assert.assertEquals(result.getRows().get(0)[0], "u1");
    Assert.assertEquals(result.getRows().get(0)[1], "index1");
    Assert.assertEquals(result.getRows().get(0)[2], "T3a");
  }

  public static class MedicalGraphLoader3 extends AbstractLocalGraphLoader {
    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("u1", "ProfMedV1.Patient"),
          constructionVertex(
              "index1",
              "ProfMedV1.PatientIndex",
              "entity",
              "包膜",
              "inspection",
              "MRI",
              "status",
              "突破包膜"),
          constructionVertex(
              "index2",
              "ProfMedV1.PatientIndex",
              "entity",
              "神经血管束",
              "inspection",
              "MRI",
              "status",
              "完整"),
          constructionVertex("前列腺癌", "ProfMedV1.Disease"),
          constructionVertex("前列腺肿瘤-突破前列腺包膜", "ProfMedV1.Index"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("u1", "inspectionIndex", "index1"),
          constructionEdge("u1", "inspectionIndex", "index2"));
    }
  }

  @Test
  public void doTestOptional5() {
    String dsl =
        "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-侵犯精囊`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺肿瘤-侵犯精囊：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"精囊腺\", \"精囊\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"见强化影\", \"异常信号结节影\", \"点状高信号影\", \"分界不清\", \"信号不均\"])\n"
            + "    Rpos1: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"结节\", \"团块\", \"病灶\", \"肿块\", \"异常信号灶\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"累及精囊腺\", \"与精囊腺分界不清\"])\n"
            + "    Rpos2: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"膀胱精囊角\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"消失\"])\n"
            + "    Rpos: Rpos0 or Rpos1 or Rpos2\n"
            + "\n"
            + "    // 前列腺肿瘤-侵犯精囊：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"精囊腺\", \"精囊\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"未见异常\"])\n"
            + "    Rneg1: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"结节\", \"团块\", \"病灶\", \"肿块\", \"异常信号灶\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"\"])\n"
            + "    Rneg2: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"膀胱精囊角\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"存在\"])\n"
            + "    Rneg: Rneg0 or Rneg1 or Rneg2\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "\n"
            + "GraphStructure {\n"
            + "    (s: ProfMedV1.Patient)-[p1:inspectionIndex]->(o1:ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-侵犯精囊`)\n"
            + "}\n"
            + "Rule {\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, o1.id, p.indexValue)\n"
            + "}";

    System.out.println(dsl);

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.LocalOptionalTest$MedicalGraphLoader5");

    // add mock catalog
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put(
        "ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "entity", "inspection", "index", "status", "bodypart")));
    schema.put("ProfMedV1.Disease", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Patient", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Index", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "ProfMedV1.Patient_belongTo_ProfMedV1.Disease",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("value", "nStage")));
    schema.put(
        "ProfMedV1.PatientIndex_belongTo_ProfMedV1.Index",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("value")));
    schema.put(
        "ProfMedV1.Patient_inspectionIndex_ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);
    task.getParams().put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setExecutionRecorder(new DefaultRecorder());
    task.setExecutorTimeoutMs(99999999999999999L);
    task.setStartIdList(Lists.newArrayList(new Tuple2<>("u1", "ProfMedV1.Patient")));

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    System.out.println(task.getExecutionRecorder().toReadableString());
    Assert.assertEquals(result.getRows().size(), 1);
    Assert.assertEquals(result.getRows().get(0)[0], "u1");
    Assert.assertEquals(result.getRows().get(0)[1], "index1");
    Assert.assertEquals(result.getRows().get(0)[2], "TRUE");
  }

  @Test
  public void doTestOptional7() {
    String dsl =
        "\n"
            + "// TODO \n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o:`ProfMedV1.Index`/`确诊-前列腺癌`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    p.indexValue = \"TRUE\"\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "GraphStructure {\n"
            + "    (s: ProfMedV1.Patient)\n"
            + "    (s)-[p1:inspectionIndex]->(o1:ProfMedV1.PatientIndex)\n"
            + "        (o1)-[p21:belongTo]->(o21: `ProfMedV1.Index`/`确诊-前列腺癌`)\n"
            + "}\n"
            + "Rule {\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, o1.id, p21.indexValue)\n"
            + "}\n";

    System.out.println(dsl);

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.LocalOptionalTest$MedicalGraphLoader5");

    // add mock catalog
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put(
        "ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "entity", "inspection", "index", "status", "bodypart")));
    schema.put("ProfMedV1.Disease", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Patient", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Index", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "ProfMedV1.Patient_belongTo_ProfMedV1.Disease",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("value", "nStage")));
    schema.put(
        "ProfMedV1.PatientIndex_belongTo_ProfMedV1.Index",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("value")));
    schema.put(
        "ProfMedV1.Patient_inspectionIndex_ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);
    task.getParams().put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setExecutionRecorder(new DefaultRecorder());
    task.setExecutorTimeoutMs(99999999999999999L);
    task.setStartIdList(Lists.newArrayList(new Tuple2<>("u1", "ProfMedV1.Patient")));

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    System.out.println(task.getExecutionRecorder().toReadableString());
    Assert.assertEquals(result.getRows().size(), 5);
  }

  @Test
  public void doTestOptional6() {
    String dsl =
        "\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o:`ProfMedV1.Index`/`确诊-前列腺癌`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    p.indexValue = \"TRUE\"\n"
            + "    Fail(\"未匹配到条件\"): true\n"
            + "  }\n"
            + "}\n"
            + "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-侵犯精囊`) {\n"
            + "  GraphStructure {\n"
            + "    (s)\n"
            + "  }\n"
            + "  Rule {\n"
            + "    // 前列腺肿瘤-侵犯精囊：是\n"
            + "    \n"
            + "    Rpos0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"精囊腺\", \"精囊\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"见强化影\", \"异常信号结节影\", \"点状高信号影\", \"分界不清\", \"信号不均\"])\n"
            + "    Rpos1: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"结节\", \"团块\", \"病灶\", \"肿块\", \"异常信号灶\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"累及精囊腺\", \"与精囊腺分界不清\"])\n"
            + "    Rpos2: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"膀胱精囊角\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"消失\"])\n"
            + "    Rpos: Rpos0 or Rpos1 or Rpos2\n"
            + "\n"
            + "    // 前列腺肿瘤-侵犯精囊：否\n"
            + "    \n"
            + "    Rneg0: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"精囊腺\", \"精囊\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"未见异常\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg1: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"结节\", \"团块\", \"病灶\", \"肿块\", \"异常信号灶\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg2: contains(s.bodypart, [\"前列腺\"]) and contains(s.entity, [\"膀胱精囊角\"]) and contains(s.inspection, [\"MRI\"]) and contains(s.status, [\"存在\"]) and not contains(s.status, [\"无\", \"不\", \"未见\"])\n"
            + "    Rneg: Rneg0 or Rneg1 or Rneg2\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(Rpos, \"TRUE\", rule_value(Rneg, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.indexValue = value\n"
            + "  }\n"
            + "}\n"
            + "\n"
            + "\n"
            + "\n"
            + "GraphStructure {\n"
            + "    (s: ProfMedV1.Patient)-[p1:inspectionIndex]->"
            + "(o1:ProfMedV1.PatientIndex)-[p3: belongTo]->?(o3:`ProfMedV1.Index`/`确诊-前列腺癌`)\n"
            + "(o1:ProfMedV1.PatientIndex)-[p: belongTo]->?(o: `ProfMedV1.Index`/`前列腺肿瘤-侵犯精囊`)\n"
            + "}\n"
            + "Rule {\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "    get(s.id, o1.id, p.indexValue,p3.indexValue)\n"
            + "}";

    System.out.println(dsl);

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.LocalOptionalTest$MedicalGraphLoader5");

    // add mock catalog
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put(
        "ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "entity", "inspection", "index", "status", "bodypart")));
    schema.put("ProfMedV1.Disease", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Patient", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Index", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "ProfMedV1.Patient_belongTo_ProfMedV1.Disease",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("value", "nStage")));
    schema.put(
        "ProfMedV1.PatientIndex_belongTo_ProfMedV1.Index",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("value")));
    schema.put(
        "ProfMedV1.Patient_inspectionIndex_ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);
    task.getParams().put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setExecutionRecorder(new DefaultRecorder());
    task.setExecutorTimeoutMs(99999999999999999L);
    task.setStartIdList(Lists.newArrayList(new Tuple2<>("u1", "ProfMedV1.Patient")));

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    System.out.println(task.getExecutionRecorder().toReadableString());
    Assert.assertEquals(result.getRows().size(), 5);
    Assert.assertEquals(result.getRows().get(0)[0], "u1");
    Assert.assertEquals(result.getRows().get(0)[1], "index1");
    Assert.assertEquals(result.getRows().get(0)[2], "TRUE");
    Assert.assertEquals(result.getRows().get(0)[3], "TRUE");
  }

  public static class MedicalGraphLoader5 extends AbstractLocalGraphLoader {
    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("u1", "ProfMedV1.Patient"),
          constructionVertex(
              "index1",
              "ProfMedV1.PatientIndex",
              "entity",
              "精囊腺",
              "bodypart",
              "前列腺",
              "status",
              "信号不均伴局部DWI稍高信号",
              "inspection",
              "MRI"),
          constructionVertex(
              "index2",
              "ProfMedV1.PatientIndex",
              "entity",
              "包膜",
              "inspection",
              "MRI",
              "index",
              "突破包膜"),
          constructionVertex(
              "index3",
              "ProfMedV1.PatientIndex",
              "entity",
              "包膜2",
              "inspection",
              "MRI",
              "index",
              "突破包膜"),
          constructionVertex(
              "index4",
              "ProfMedV1.PatientIndex",
              "entity",
              "包膜2",
              "inspection",
              "MRI",
              "index",
              "突破包膜"),
          constructionVertex(
              "index5",
              "ProfMedV1.PatientIndex",
              "entity",
              "包膜2",
              "inspection",
              "MRI",
              "index",
              "突破包膜"),
          constructionVertex("前列腺癌", "ProfMedV1.Disease"),
          constructionVertex("前列腺肿瘤-侵犯精囊", "ProfMedV1.Index"),
          constructionVertex("确诊-前列腺癌", "ProfMedV1.Index"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("u1", "inspectionIndex", "index1"),
          constructionEdge("u1", "inspectionIndex", "index2"),
          constructionEdge("u1", "inspectionIndex", "index3"),
          constructionEdge("u1", "inspectionIndex", "index4"),
          constructionEdge("u1", "inspectionIndex", "index5"));
    }
  }

  public static class MedicalGraphLoader2 extends AbstractLocalGraphLoader {
    @Override
    public List<IVertex<String, IProperty>> genVertexList() {
      return Lists.newArrayList(
          constructionVertex("u1", "ProfMedV1.Patient"),
          constructionVertex("index1", "ProfMedV1.PatientIndex", "entity", "影像学检查"),
          constructionVertex("前列腺肿瘤-突破前列腺包膜", "ProfMedV1.Index"),
          constructionVertex("前列腺-影像学检查", "ProfMedV1.Index"),
          constructionVertex(
              "index2",
              "ProfMedV1.PatientIndex",
              "entity",
              "包膜",
              "inspection",
              "MRI",
              "index",
              "突破包膜"),
          constructionVertex("前列腺癌", "ProfMedV1.Disease"));
    }

    @Override
    public List<IEdge<String, IProperty>> genEdgeList() {
      return Lists.newArrayList(
          constructionEdge("u1", "inspectionIndex", "index1"),
          constructionEdge("u1", "inspectionIndex", "index2"));
    }
  }

  @Test
  public void doTestOptional2() {
    String dsl =
        "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-突破前列腺包膜`) {\n"
            + "\tGraphStructure {\n"
            + "      (s)\n"
            + "\t}\n"
            + "\tRule {\n"
            + "    // 前列腺肿瘤突破前列腺包膜：是\n"
            + "\t\tR01: contains(s.entity, [\"包膜\"]) and contains(s.inspection, [\"MRI\", \"CT\", \"PETCT\", \"PETMRI\"]) and contains(s.index, [\"累及\", \"侵犯\", \"膨隆\", \"突破包膜\"])\n"
            + "\t\tR02: contains(s.entity, [\"神经血管束\", \"血管神经束\", \"DVC\"]) and contains(s.inspection, [\"MRI\", \"PETMRI\"]) and contains(s.index, [\"累及\", \"侵犯\"])\n"
            + "\t\tR03: R01 or R02\n"
            + "\n"
            + "    // 前列腺肿瘤突破前列腺包膜：否\n"
            + "    R11: contains(s.entity, [\"包膜\"]) and contains(s.inspection, [\"MRI\", \"CT\", \"PETCT\", \"PETMRI\"]) and contains(s.index, [\"完整\"])\n"
            + "\t\tR12: contains(s.entity, [\"神经血管束\", \"血管神经束\", \"DVC\"]) and contains(s.inspection, [\"MRI\", \"PETMRI\"]) and contains(s.index, [\"完整\"])\n"
            + "\t\tR13: R11 or R12\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(R03, \"TRUE\", rule_value(R13, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.value = value\n"
            + "\t}\n"
            + "}";
    dsl +=
        "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺-影像学检查`) {\n"
            + "\tGraphStructure {\n"
            + "      (s)\n"
            + "\t}\n"
            + "\tRule {\n"
            + "    // 前列腺肿瘤突破前列腺包膜：是\n"
            + "\t\tR01: contains(s.entity, [\"影像学检查\"])\n"
            + "    \tp.value = \"TRUE\"\n"
            + "\t}\n"
            + "}";

    dsl +=
        "Define (s: ProfMedV1.Patient)-[p: belongTo]->(o: `ProfMedV1.Disease`/`前列腺癌`) {\n"
            + "    GraphStructure {\n"
            + "\t\t(s:ProfMedV1.Patient)-[p1:inspectionIndex]->(o1:ProfMedV1.PatientIndex)\n"
            + "\t\t(s:ProfMedV1.Patient)-[p2:inspectionIndex]->(o2:ProfMedV1.PatientIndex)\n"
            + "    (o1)-[p21:belongTo]->?(o21: `ProfMedV1.Index`/`前列腺肿瘤-突破前列腺包膜`)\n"
            + "  \t(o2)-[p22:belongTo]->?(o22: `ProfMedV1.Index`/`前列腺-影像学检查`)\n"
            + "    }\n"
            + "    Rule {\n"
            + "      R11(\"是否存在指标-前列腺肿瘤-突破前列腺包膜\"): exists(p21)\n"
            + "      R12(\"前列腺肿瘤-突破前列腺包膜\"): R11 and p21.value == \"TRUE\"\n"
            + "\n"
            + "      R21(\"是否存在指标-前列腺-影像学检查\"): exists(p22)\n"
            + "      R22(\"前列腺-影像学检查\"): R21 and p22.value == \"TRUE\"\n"
            + "\n"
            + "       \n"
            + "      N1(\"N1分期\"): R12\n"
            + "      N0(\"N0分期\"): R12 and R22\n"
            + "      Nx(\"Nx分期\"): R12 and (not R22)\n"
            + "      n = rule_value(N1, \"N1\", rule_value(N0, \"N0\", rule_value(Nx, \"Nx\", \"无法判断\")))\n"
            + "      p.nStage = group(s).concat_agg(n)\n"
            //               + "      p.nStage = rule_value(N1, \"N1\", rule_value(N0, \"N0\",
            // rule_value(Nx, \"Nx\", \"无法判断\")))\n"
            + "      //p.json_value = \"{'nStage': p.nStage}\"\n"
            + "      //UDF_JSONGet(p.json_value, '$.nStage')\n"
            + "    }\n"
            + "}";
    dsl +=
        "GraphStructure {\n"
            + "   (s: ProfMedV1.Patient)-[p: belongTo]->(o: `ProfMedV1.Disease`/`前列腺癌`) \n"
            + "}\n"
            + "Rule {\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "  get(s.id, p.nStage)\n"
            + "}";

    System.out.println(dsl);

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass(
        "com.antgroup.openspg.reasoner.runner.local.main.LocalOptionalTest$MedicalGraphLoader2");

    // add mock catalog
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put(
        "ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "entity", "inspection", "index", "status")));
    schema.put("ProfMedV1.Disease", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Patient", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Index", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "ProfMedV1.Patient_belongTo_ProfMedV1.Disease",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("value", "nStage")));
    schema.put(
        "ProfMedV1.PatientIndex_belongTo_ProfMedV1.Index",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("value")));
    schema.put(
        "ProfMedV1.Patient_inspectionIndex_ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);
    task.getParams().put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setExecutionRecorder(new DefaultRecorder());
    task.setExecutorTimeoutMs(99999999999999999L);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    System.out.println(task.getExecutionRecorder().toReadableString());
    Assert.assertEquals(result.getRows().size(), 1);
    Assert.assertEquals(result.getRows().get(0)[0], "u1");
    Assert.assertTrue(String.valueOf(result.getRows().get(0)[1]).contains("N1"));
  }

  @Test
  public void doTestOptional01() {
    String dsl =
        "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-突破前列腺包膜`) {\n"
            + "\tGraphStructure {\n"
            + "      (s)\n"
            + "\t}\n"
            + "\tRule {\n"
            + "    \tp.value = \"\"\n"
            + "  R1: false"
            + "\t}\n"
            + "}";
    dsl +=
        "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺-影像学检查`) {\n"
            + "\tGraphStructure {\n"
            + "      (s)\n"
            + "\t}\n"
            + "\tRule {\n"
            + "    // 前列腺肿瘤突破前列腺包膜：是\n"
            + "\t\tR01: contains(s.entity, \"影像学检查\")\n"
            + "    \tp.value = \"TRUE\"\n"
            + "  R1: false"
            + "\t}\n"
            + "}";

    dsl +=
        "Define (s: ProfMedV1.Patient)-[p: belongTo]->(o: `ProfMedV1.Disease`/`前列腺癌`) {\n"
            + "    GraphStructure {\n"
            + "\t\t(s:ProfMedV1.Patient)-[p1:inspectionIndex]->(o1:ProfMedV1.PatientIndex)\n"
            + "    (o1)-[p21:belongTo]->?(o21: `ProfMedV1.Index`/`前列腺肿瘤-突破前列腺包膜`)\n"
            + "  \t(o1)-[p22:belongTo]->?(o22: `ProfMedV1.Index`/`前列腺-影像学检查`)\n"
            + "    }\n"
            + "    Rule {\n"
            + "      R11(\"是否存在指标-前列腺肿瘤-突破前列腺包膜\"): exists(p21)\n"
            + "      R12(\"前列腺肿瘤-突破前列腺包膜\"): R11 and p21.value == \"TRUE\"\n"
            + "\n"
            + "      R21(\"是否存在指标-前列腺-影像学检查\"): exists(p22)\n"
            + "      R22(\"前列腺-影像学检查\"): R21 and p22.value == \"TRUE\"\n"
            + "\n"
            + "       \n"
            + "      N1(\"N1分期\"): R12\n"
            + "      N0(\"N0分期\"): R12 and R22\n"
            + "      Nx(\"Nx分期\"): R12 and (not R22)\n"
            + "      p.nStage = rule_value(N1, \"N1\", rule_value(N0, \"N0\", rule_value(Nx, \"Nx\", \"无法判断\")))\n"
            + "      //p.json_value = \"{'nStage': p.nStage}\"\n"
            + "      //UDF_JSONGet(p.json_value, '$.nStage')\n"
            + "    }\n"
            + "}";
    dsl +=
        "GraphStructure {\n"
            + "   (s: ProfMedV1.Patient)-[p: belongTo]->(o: `ProfMedV1.Disease`/`前列腺癌`) \n"
            + "}\n"
            + "Rule {\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "  get(s.id, p.nStage)\n"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass("com.antgroup.openspg.reasoner.runner.local.loader.MedicalGraphLoader");

    // add mock catalog
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put(
        "ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "entity", "inspection", "index")));
    schema.put("ProfMedV1.Disease", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Patient", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Index", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "ProfMedV1.Patient_belongTo_ProfMedV1.Disease",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("value", "nStage")));
    schema.put(
        "ProfMedV1.PatientIndex_belongTo_ProfMedV1.Index",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("value")));
    schema.put(
        "ProfMedV1.Patient_inspectionIndex_ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);
    task.getParams().put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);
    task.setExecutionRecorder(new DefaultRecorder());
    task.setExecutorTimeoutMs(99999999999999999L);
    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    System.out.println(task.getExecutionRecorder().toReadableString());
    Assert.assertEquals(result.getRows().size(), 1);
    Assert.assertEquals(result.getRows().get(0)[0], "u1");
    Assert.assertEquals(result.getRows().get(0)[1], "无法判断");
  }

  @Test
  public void doTestOptional() {
    String dsl =
        "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺肿瘤-突破前列腺包膜`) {\n"
            + "\tGraphStructure {\n"
            + "      (s)\n"
            + "\t}\n"
            + "\tRule {\n"
            + "    // 前列腺肿瘤突破前列腺包膜：是\n"
            + "\t\tR01: contains(s.entity, \"包膜\") and contains(s.inspection, [\"MRI\", \"CT\", \"PETCT\", \"PETMRI\"]) and contains(s.index, [\"累及\", \"侵犯\", \"膨隆\", \"突破包膜\"])\n"
            + "\t\tR02: contains(s.entity, [\"神经血管束\", \"血管神经束\", \"DVC\"]) and contains(s.inspection, [\"MRI\", \"PETMRI\"]) and contains(s.index, [\"累及\", \"侵犯\"])\n"
            + "\t\tR03: R01 or R02\n"
            + "\n"
            + "    // 前列腺肿瘤突破前列腺包膜：否\n"
            + "    R11: contains(s.entity, \"包膜\") and contains(s.inspection, [\"MRI\", \"CT\", \"PETCT\", \"PETMRI\"]) and contains(s.index, [\"完整\"])\n"
            + "\t\tR12: contains(s.entity, [\"神经血管束\", \"血管神经束\", \"DVC\"]) and contains(s.inspection, [\"MRI\", \"PETMRI\"]) and contains(s.index, [\"完整\"])\n"
            + "\t\tR13: R11 or R12\n"
            + "\n"
            + "    // rule_value(R, res0, res1)的参数含义：R为判断条件，res0/res1分别为R0为True/False时的输出\n"
            + "    value = rule_value(R03, \"TRUE\", rule_value(R13, \"FALSE\", \"\"))\n"
            + "    // 未匹配到条件，则规则不成立（缺省，不连边）\n"
            + "    Fail(\"未匹配到条件\"): value != \"\"\n"
            + "    p.value = value\n"
            + "\t}\n"
            + "}";
    dsl +=
        "Define (s: ProfMedV1.PatientIndex)-[p: belongTo]->(o: `ProfMedV1.Index`/`前列腺-影像学检查`) {\n"
            + "\tGraphStructure {\n"
            + "      (s)\n"
            + "\t}\n"
            + "\tRule {\n"
            + "    // 前列腺肿瘤突破前列腺包膜：是\n"
            + "\t\tR01: contains(s.entity, \"影像学检查\")\n"
            + "    \tp.value = \"TRUE\"\n"
            + "\t}\n"
            + "}";

    dsl +=
        "Define (s: ProfMedV1.Patient)-[p: belongTo]->(o: `ProfMedV1.Disease`/`前列腺癌`) {\n"
            + "    GraphStructure {\n"
            + "\t\t(s:ProfMedV1.Patient)-[p1:inspectionIndex]->(o1:ProfMedV1.PatientIndex)\n"
            + "    (o1)-[p21:belongTo]->?(o21: `ProfMedV1.Index`/`前列腺肿瘤-突破前列腺包膜`)\n"
            + "  \t(o1)-[p22:belongTo]->?(o22: `ProfMedV1.Index`/`前列腺-影像学检查`)\n"
            + "    }\n"
            + "    Rule {\n"
            + "      R11(\"是否存在指标-前列腺肿瘤-突破前列腺包膜\"): exists(p21)\n"
            + "      R12(\"前列腺肿瘤-突破前列腺包膜\"): R11 and p21.value == \"TRUE\"\n"
            + "\n"
            + "      R21(\"是否存在指标-前列腺-影像学检查\"): exists(p22)\n"
            + "      R22(\"前列腺-影像学检查\"): R21 and p22.value == \"TRUE\"\n"
            + "\n"
            + "       \n"
            + "      N1(\"N1分期\"): R12\n"
            + "      N0(\"N0分期\"): R12 and R22\n"
            + "      Nx(\"Nx分期\"): R12 and (not R22)\n"
            + "      p.nStage = rule_value(N1, \"N1\", rule_value(N0, \"N0\", rule_value(Nx, \"Nx\", \"无法判断\")))\n"
            + "      //p.json_value = \"{'nStage': p.nStage}\"\n"
            + "      //UDF_JSONGet(p.json_value, '$.nStage')\n"
            + "    }\n"
            + "}";
    dsl +=
        "GraphStructure {\n"
            + "   (s: ProfMedV1.Patient)-[p: belongTo]->(o: `ProfMedV1.Disease`/`前列腺癌`) \n"
            + "}\n"
            + "Rule {\n"
            + "\n"
            + "}\n"
            + "Action {\n"
            + "  get(s.id, p.nStage)\n"
            + "}";

    LocalReasonerTask task = new LocalReasonerTask();
    task.setDsl(dsl);
    task.setGraphLoadClass("com.antgroup.openspg.reasoner.runner.local.loader.MedicalGraphLoader");

    // add mock catalog
    Map<String, Set<String>> schema = new HashMap<>();
    schema.put(
        "ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(
            Sets.newHashSet("id", "entity", "inspection", "index")));
    schema.put("ProfMedV1.Disease", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Patient", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put("ProfMedV1.Index", Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("id")));
    schema.put(
        "ProfMedV1.Patient_belongTo_ProfMedV1.Disease",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("value", "nStage")));
    schema.put(
        "ProfMedV1.PatientIndex_belongTo_ProfMedV1.Index",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet("value")));
    schema.put(
        "ProfMedV1.Patient_inspectionIndex_ProfMedV1.PatientIndex",
        Convert2ScalaUtil.toScalaImmutableSet(Sets.newHashSet()));
    Catalog catalog = new PropertyGraphCatalog(Convert2ScalaUtil.toScalaImmutableMap(schema));
    catalog.init();
    task.setCatalog(catalog);
    task.getParams().put(Constants.SPG_REASONER_PLAN_PRETTY_PRINT_LOGGER_ENABLE, true);
    task.getParams().put(Constants.SPG_REASONER_LUBE_SUBQUERY_ENABLE, true);

    LocalReasonerRunner runner = new LocalReasonerRunner();
    LocalReasonerResult result = runner.run(task);
    System.out.println(result);
    Assert.assertEquals(result.getRows().size(), 1);
    Assert.assertEquals(result.getRows().get(0)[0], "u1");
    Assert.assertEquals(result.getRows().get(0)[1], "无法判断");
  }
}
