/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.model.service;

import java.util.Date;

import com.antgroup.openspg.common.util.DateTimeUtils;
import com.antgroup.openspg.common.util.IpUtils;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import com.antgroup.openspg.server.common.model.scheduler.TaskStatus;
import com.antgroup.openspg.server.core.scheduler.model.common.WorkflowDag;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yangjin
 * @version : SchedulerTask.java, v 0.1 2023年11月30日 09:50 yangjin Exp $
 */
public class SchedulerTask extends BaseModel {

    private static final long serialVersionUID = -5515352651327338741L;

    /**
     * primary key
     */
    private Long id;

    /**
     * Create time
     */
    private Date gmtCreate;

    /**
     * Modified time
     */
    private Date gmtModified;

    /**
     * create User
     */
    private String createUser;

    /**
     * update User
     */
    private String updateUser;

    /**
     * type
     */
    private String type;

    /**
     * title
     */
    private String title;

    /**
     * status
     */
    private String status;

    /**
     * SchedulerJob Id
     */
    private Long jobId;

    /**
     * instance id
     */
    private Long instanceId;

    /**
     * task Status
     */
    private String taskStatus;

    /**
     * execute Num
     */
    private Integer executeNum;

    /**
     * execute begin Time
     */
    private Date beginTime;

    /**
     * execute finish Time
     */
    private Date finishTime;

    /**
     * estimate Finish Time
     */
    private Date estimateFinishTime;

    /**
     * config
     */
    private String config;

    /**
     * remark
     */
    private String remark;

    /**
     * extension，JSON
     */
    private String extension;

    /**
     * lock Time
     */
    private Date lockTime;

    /**
     * resource
     */
    private String resource;

    /**
     * input
     */
    private String input;

    /**
     * output
     */
    private String output;

    /**
     * node id
     */
    private String nodeId;

    public SchedulerTask() {

    }

    /**
     * constructor
     *
     * @param createUser
     * @param instanceId
     * @param status
     * @param node
     */
    public SchedulerTask(String createUser, Long instanceId, TaskStatus status, WorkflowDag.Node node) {
        this.createUser = createUser;
        this.updateUser = createUser;
        this.type = node.getType();
        if (StringUtils.isNotBlank(node.getName())) {
            this.title = node.getName();
        } else {
            this.title = node.getType();
        }
        this.status = status.name();
        this.nodeId = node.getId();
        this.instanceId = instanceId;
        this.executeNum = 0;
        this.beginTime = new Date();
        StringBuffer log = new StringBuffer(DateTimeUtils.getDate2LongStr(new Date()));
        log.append("(").append(IpUtils.IP_LIST).append(")：").append("新建流程，当前等待前置节点执行完成.....").append(System.getProperty("line.separator"));
        this.remark = log.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getExecuteNum() {
        return executeNum;
    }

    public void setExecuteNum(Integer executeNum) {
        this.executeNum = executeNum;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Date getEstimateFinishTime() {
        return estimateFinishTime;
    }

    public void setEstimateFinishTime(Date estimateFinishTime) {
        this.estimateFinishTime = estimateFinishTime;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
