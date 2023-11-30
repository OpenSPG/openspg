/**
 * Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.translate;

import com.antgroup.openspg.server.core.scheduler.model.common.WorkflowDag;

/**
 * 调度DAG 翻译层
 *
 * @author 庄舟
 * @Title: DagTranslate.java
 * @Description:
 * @date 2022/3/11 16:41
 */
public interface Translate<T> {

    /**
     * translate to workflow Dag
     *
     * @param t
     * @return
     */
    WorkflowDag translate(T t);
}