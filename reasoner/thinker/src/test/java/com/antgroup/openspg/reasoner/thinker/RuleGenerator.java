package com.antgroup.openspg.reasoner.thinker;

import com.antgroup.openspg.reasoner.thinker.logic.rule.Rule;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
        sb.append("Define (a:Med.Examination/`").append(name).append("`)-[:abnormalRule]->(c: string) {\n");
        sb.append("  R1: ");
        if (StringUtils.isNotBlank(population)) {
            sb.append("contains(population, '").append(population).append("')").append(" AND ");
        }
        if (isBigger) {
            sb.append("(value>").append(bound).append(")").append(" AND ");
            sb.append("((a)-[:highExplain]->(c))");
        } else {
            sb.append("(value<").append(bound).append(")").append(" AND ");
            sb.append("((a)-[:lowExplain]->(c))");
        }
        sb.append("\n}\n");
        if (StringUtils.isNotBlank(population)) {
            sb.append("Description: \"对于").append(population).append(",");
        } else {
            sb.append("Description: \"");
        }
        sb.append(name).append("的正常范围是[").append(range).append("]").append(unit).append("\"");
        return sb.toString();
    }

    private List<String> toRule(String line) {
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
        return rules;
    }


    @Test
    public void genRule() {
        String path = "/Users/albert/Desktop/检验检查文本0422.csv";
        List<Rule> rules = new LinkedList<>();
        List<String> ruleStr = new LinkedList<>();
        List<String> lines = readFile(path);
        lines = lines.subList(1, lines.size());
        for (String line : lines) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            try {
                List<String> rule = toRule(line);
                ruleStr.addAll(rule);
                for (String r : rule) {
                    rules.add(parser.parseSimplifyDsl(r, null).head());
                }
            } catch (Exception ex) {
                System.out.println(line);
            }
        }

        String content = StringUtils.join(ruleStr, "\n\n");
        scala.collection.immutable.List<Rule> newRules = parser.parseSimplifyDsl(content, null);
        System.out.println(newRules.size());
    }
}
