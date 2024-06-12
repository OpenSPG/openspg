/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.reasoner.thinker;

import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RuleGenerator {
    private SimplifyThinkerParser parser = new SimplifyThinkerParser();

    private List<String> readFile(String path) {
        BufferedReader reader;
        List<String> lines = new LinkedList<>();
        try {
            reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                lines.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private String toRule(String name, String unit, String population, String bound, String range, boolean isBigger) {
        StringBuilder sb = new StringBuilder();
        if (isBigger) {
            sb.append("Define (a:Med.Examination/`").append(name).append("`)-[:abnormalValue]->(c: Med.ExaminationResult/`偏高`) {\n");
        } else {
            sb.append("Define (a:Med.Examination/`").append(name).append("`)-[:abnormalValue]->(c: Med.ExaminationResult/`偏低`) {\n");
        }
        sb.append("  R1: ");
        if (StringUtils.isNotBlank(population)) {
            sb.append("contains(population, '").append(population).append("')").append(" AND ");
        }
        if (isBigger) {
            sb.append("(value>").append(bound).append(" || value in [\"高\", \"阳性\"]").append(")");
        } else {
            sb.append("(value<").append(bound).append(" || value in [\"低\", \"阴性\"]").append(")");
        }
        sb.append("\n}\n");
        if (StringUtils.isNotBlank(population) && isBigger) {
            sb.append("Description: \"对于").append(population).append("，").append("当前指标偏高，");
        } else if (StringUtils.isNotBlank(population) && !isBigger) {
            sb.append("Description: \"对于").append(population).append("，").append("当前指标偏低，");
        } else if (isBigger) {
            sb.append("Description: \"当前指标偏高，");
        } else {
            sb.append("Description: \"当前指标偏低，");
        }
        sb.append(name).append("的正常范围是[").append(range).append("]").append(unit).append("\"");
        return sb.toString();
    }

    private String toNormalRule(String name, String unit, String population, String lower, String upper, String range) {
        StringBuilder sb = new StringBuilder();
        sb.append("Define (a:Med.Examination/`").append(name).append("`)-[:abnormalValue]->(c: Med.ExaminationResult/`正常`) {\n");
        sb.append("  R1: ");
        if (StringUtils.isNotBlank(population)) {
            sb.append("contains(population, '").append(population).append("')").append(" AND ");
        }
        sb.append("(value>=").append(lower).append(")").append(" AND ");
        sb.append("(value<=").append(upper).append(")");
        sb.append("\n}\n");
        if (StringUtils.isNotBlank(population)) {
            sb.append("Description: \"对于").append(population).append("，").append("当前指标正常，");
        } else {
            sb.append("Description: \"当前指标正常，");
        }
        sb.append(name).append("的正常范围是[").append(range).append("]").append(unit).append("\"");
        return sb.toString();
    }

    private Pair<String, List<String>> toRule(String line) {
        String[] parts = line.split(",");
        String name = parts[0];
        String unit = parts[2];
        String population = parts[3];
        String range = parts[4];
        List<String> rules = new LinkedList<>();
        if (!range.split("-")[0].equals("0")) {
            rules.add(toRule(name, unit, population, range.split("-")[0], range, false));
        }
        rules.add(toRule(name, unit, population, range.split("-")[1], range, true));
        rules.add(toNormalRule(name, unit, population, range.split("-")[0], range.split("-")[1], range));
        return new ImmutablePair<>(name, rules);
    }


    @Test
    public void genRule() {
        String path = "/Users/albert/Desktop/医疗QA/检验检查文本0422.csv";
        String mappingPath = "/Users/albert/Desktop/医疗QA/增加的检验数据.csv";
        Map<String, List<String>> ruleMap = new HashMap<>();
        List<Rule> rules = new LinkedList<>();
        List<String> ruleStr = new LinkedList<>();
        List<String> lines = readFile(path);
        lines = lines.subList(0, lines.size());
        for (String line : lines) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            try {
                Pair<String, List<String>> rule = toRule(line);
                ruleStr.addAll(rule.getRight());
                ruleMap.put(rule.getLeft(), rule.getRight());
                for (String r : rule.getRight()) {
                    rules.add(parser.parseSimplifyDsl(r, null).head());
                }
            } catch (Exception ex) {
                System.out.println(line);
            }
        }

        lines = readFile(mappingPath);
        for (String line : lines) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            String[] parts = line.split(",");
            List<String> ruleList = ruleMap.get(parts[1]);
            if (ruleList == null) {
                System.out.println(line);
                continue;
            }
            for (String r : ruleList) {
                r = r.replace(parts[1], parts[0]);
                ruleStr.add(r);
                rules.add(parser.parseSimplifyDsl(r, null).head());
            }
        }

        String content = StringUtils.join(ruleStr, "\n\n");
        scala.collection.immutable.List<Rule> newRules = parser.parseSimplifyDsl(content, null);
        System.out.println(newRules.size());
    }
}
