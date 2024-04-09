package com.antgroup.openspg.reasoner.thinker.logic.rule;

import com.antgroup.openspg.reasoner.thinker.logic.graph.Element;
import java.io.Serializable;
import java.util.List;

public class Rule implements Serializable {
  private String triggerName;
  private List<Element> body;
  private Element head;
  private Node root;

  /**
   * Getter method for property <tt>triggerName</tt>.
   *
   * @return property value of triggerName
   */
  public String getTriggerName() {
    return triggerName;
  }

  /**
   * Setter method for property <tt>triggerName</tt>.
   *
   * @param triggerName value to be assigned to property triggerName
   */
  public void setTriggerName(String triggerName) {
    this.triggerName = triggerName;
  }

  /**
   * Getter method for property <tt>body</tt>.
   *
   * @return property value of body
   */
  public List<Element> getBody() {
    return body;
  }

  /**
   * Setter method for property <tt>body</tt>.
   *
   * @param body value to be assigned to property body
   */
  public void setBody(List<Element> body) {
    this.body = body;
  }

  /**
   * Getter method for property <tt>head</tt>.
   *
   * @return property value of head
   */
  public Element getHead() {
    return head;
  }

  /**
   * Setter method for property <tt>head</tt>.
   *
   * @param head value to be assigned to property head
   */
  public void setHead(Element head) {
    this.head = head;
  }

  /**
   * Getter method for property <tt>root</tt>.
   *
   * @return property value of root
   */
  public Node getRoot() {
    return root;
  }

  /**
   * Setter method for property <tt>root</tt>.
   *
   * @param root value to be assigned to property root
   */
  public void setRoot(Node root) {
    this.root = root;
  }
}
