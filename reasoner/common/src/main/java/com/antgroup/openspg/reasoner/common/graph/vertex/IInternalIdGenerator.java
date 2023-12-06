/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common.graph.vertex;

/**
 * @author donghai.ydh
 * @version IInternalIdGenerator.java, v 0.1 2023年04月17日 15:44 donghai.ydh
 */
public interface IInternalIdGenerator {
  long gen(String bizId, String type);
}
