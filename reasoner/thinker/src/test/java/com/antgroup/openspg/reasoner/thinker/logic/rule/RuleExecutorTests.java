package com.antgroup.openspg.reasoner.thinker.logic.rule;

import com.antgroup.openspg.reasoner.thinker.SimplifyThinkerParser;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Entity;
import com.antgroup.openspg.reasoner.thinker.logic.rule.visitor.RuleExecutor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class RuleExecutorTests {
  private Rule getR1() {
    String rule =
        "Define (危险水平分层/`很高危`) {\n"
            + "  R1:高血压分层/`临床并发症` and (\"有并发症的糖尿病\" in 症状) and 伸缩压>=140\n"
            + "}";
    SimplifyThinkerParser parser = new SimplifyThinkerParser();
    return parser.parseSimplifyDsl(rule, null).head();
  }

  @Test
  public void testRuleExe() {
    Rule rule = getR1();
    Node root = rule.getRoot();
    TreeLogger logger = new TreeLogger(root.toString());
    Map<String, Object> session = new HashMap<>();
    session.put("伸缩压", 141);
    session.put("症状", "有并发症的糖尿病");
    Boolean ret =
        rule.getRoot()
            .accept(
                Arrays.asList(new Entity("临床并发症", "高血压分层")), session, new RuleExecutor(), logger);
    Assert.assertTrue(ret);
  }

  @Test
  public void testRuleExeInContext() {
    Rule rule = getR1();
    Node root = rule.getRoot();
    TreeLogger logger = new TreeLogger(root.toString());
    Map<String, Object> session = new HashMap<>();
    session.put("伸缩压", 141);
    session.put("症状", "有并发症的糖尿病");
    session.put(new Entity("临床并发症", "高血压分层").toString(), true);
    Boolean ret = rule.getRoot().accept(Arrays.asList(), session, new RuleExecutor(), logger);
    Assert.assertTrue(ret);
  }
}
