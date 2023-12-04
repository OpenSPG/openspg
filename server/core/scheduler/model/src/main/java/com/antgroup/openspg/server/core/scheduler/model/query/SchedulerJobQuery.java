/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.model.query;

import java.util.List;

import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.google.common.collect.Lists;

/**
 * @author yangjin
 * @version : SchedulerJobQuery.java, v 0.1 2023年12月04日 11:23 yangjin Exp $
 */
public class SchedulerJobQuery extends SchedulerJob {

    private static final long serialVersionUID = -857200975331899039L;

    /**
     * page No
     */
    private Integer pageNo;
    /**
     * page Size
     */
    private Integer pageSize;
    /**
     * sort
     */
    private String  sort;
    /**
     * order asc, desc
     */
    private String  order;

    /**
     * keyword, like query name/id/createUserName
     */
    private String       keyword;
    /**
     * types
     */
    private List<String> types = Lists.newArrayList();

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
