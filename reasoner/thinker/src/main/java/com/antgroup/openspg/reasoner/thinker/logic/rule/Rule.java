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

package com.antgroup.openspg.reasoner.thinker.logic.rule;

import java.io.Serializable;
import java.util.List;

public class Rule implements Serializable {
  private String name;
  private List<ClauseEntry> body;
  private ClauseEntry head;
  private Node root;
  private String desc;

  /**
   * Getter method for property <tt>body</tt>.
   *
   * @return property value of body
   */
  public List<ClauseEntry> getBody() {
    return body;
  }

  /**
   * Setter method for property <tt>body</tt>.
   *
   * @param body value to be assigned to property body
   */
  public void setBody(List<ClauseEntry> body) {
    this.body = body;
  }

  /**
   * Getter method for property <tt>head</tt>.
   *
   * @return property value of head
   */
  public ClauseEntry getHead() {
    return head;
  }

  /**
   * Setter method for property <tt>head</tt>.
   *
   * @param head value to be assigned to property head
   */
  public void setHead(ClauseEntry head) {
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

  /**
   * Getter method for property <tt>desc</tt>.
   *
   * @return property value of desc
   */
  public String getDesc() {
    return desc;
  }

  /**
   * Setter method for property <tt>desc</tt>.
   *
   * @param desc value to be assigned to property desc
   */
  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
