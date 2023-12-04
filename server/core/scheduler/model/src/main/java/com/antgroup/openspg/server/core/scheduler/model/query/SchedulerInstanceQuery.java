/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.model.query;

import java.util.Date;
import java.util.List;

import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerInstance;
import com.google.common.collect.Lists;

/**
 * @author yangjin
 * @version : SchedulerInstanceQuery.java, v 0.1 2023年12月04日 11:15 yangjin Exp $
 */
public class SchedulerInstanceQuery extends SchedulerInstance {

    private static final long serialVersionUID = 6125004435485785299L;

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
     * keyword, like query name/uniqueId/createUser
     */
    private String       keyword;
    /**
     * types
     */
    private List<String> types = Lists.newArrayList();
    /**
     * start SchedulerDate Date
     */
    private Date         startSchedulerDate;
    /**
     * end SchedulerDate Date
     */
    private Date         endSchedulerDate;
    /**
     * start CreateTime Date
     */
    private Date         startCreateTime;
    /**
     * end CreateTime Date
     */
    private Date         endCreateTime;
    /**
     * start FinishTime Date
     */
    private Date         startFinishTime;
    /**
     * end FinishTime Date
     */
    private Date         endFinishTime;

    public Date getStartSchedulerDate() {
        return startSchedulerDate;
    }

    public void setStartSchedulerDate(Date startSchedulerDate) {
        this.startSchedulerDate = startSchedulerDate;
    }

    public Date getEndSchedulerDate() {
        return endSchedulerDate;
    }

    public void setEndSchedulerDate(Date endSchedulerDate) {
        this.endSchedulerDate = endSchedulerDate;
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

    public Date getStartFinishTime() {
        return startFinishTime;
    }

    public void setStartFinishTime(Date startFinishTime) {
        this.startFinishTime = startFinishTime;
    }

    public Date getEndFinishTime() {
        return endFinishTime;
    }

    public void setEndFinishTime(Date endFinishTime) {
        this.endFinishTime = endFinishTime;
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
}
