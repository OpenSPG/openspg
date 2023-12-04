/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.model.query;

import java.util.Date;

import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerTask;

/**
 * @author yangjin
 * @version : SchedulerTaskQuery.java, v 0.1 2023年12月04日 11:26 yangjin Exp $
 */
public class SchedulerTaskQuery extends SchedulerTask {

    private static final long serialVersionUID = -5297026143837437982L;

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
     * start CreateTime Date
     */
    private Date startCreateTime;
    /**
     * end CreateTime Date
     */
    private Date endCreateTime;

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

    public Date getStartCreateTime() {
        return startCreateTime;
    }

    public void setStartCreateTime(Date startCreateTime) {
        this.startCreateTime = startCreateTime;
    }

    public Date getEndCreateTime() {
        return endCreateTime;
    }

    public void setEndCreateTime(Date endCreateTime) {
        this.endCreateTime = endCreateTime;
    }
}
