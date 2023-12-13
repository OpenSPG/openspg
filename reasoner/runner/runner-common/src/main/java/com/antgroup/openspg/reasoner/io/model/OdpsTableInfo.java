/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.io.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;


public class OdpsTableInfo extends AbstractTableInfo implements Comparable<OdpsTableInfo> {
    private String accessID;
    private String accessKey;
    private String endPoint = "http://service.odps.aliyun-inc.com/api";
    private String tunnelEndPoint;

    /**
     * use for odps writer
     */
    private String uploadSessionId;

    /**
     * Getter method for property <tt>accessID</tt>.
     *
     * @return property value of accessID
     */
    public String getAccessID() {
        return accessID;
    }

    /**
     * Setter method for property <tt>accessID</tt>.
     *
     * @param accessID value to be assigned to property accessID
     */
    public void setAccessID(String accessID) {
        this.accessID = accessID;
    }

    /**
     * Getter method for property <tt>accessKey</tt>.
     *
     * @return property value of accessKey
     */
    @JSONField(serialize = false)
    public String getAccessKey() {
        return accessKey;
    }

    /**
     * Setter method for property <tt>accessKey</tt>.
     *
     * @param accessKey value to be assigned to property accessKey
     */
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * Getter method for property <tt>endPoint</tt>.
     *
     * @return property value of endPoint
     */
    public String getEndPoint() {
        return endPoint;
    }

    /**
     * Setter method for property <tt>endPoint</tt>.
     *
     * @param endPoint value to be assigned to property endPoint
     */
    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    /**
     * Getter method for property <tt>tunnelEndPoint</tt>.
     *
     * @return property value of tunnelEndPoint
     */
    public String getTunnelEndPoint() {
        return tunnelEndPoint;
    }

    /**
     * Setter method for property <tt>tunnelEndPoint</tt>.
     *
     * @param tunnelEndPoint value to be assigned to property tunnelEndPoint
     */
    public void setTunnelEndPoint(String tunnelEndPoint) {
        this.tunnelEndPoint = tunnelEndPoint;
    }

    /**
     * toString
     */
    @Override
    public String toString() {
        String str = "table=" + this.project + "." + this.table + ",accessId=" + this.accessID + ",endPoint=" + this.endPoint;
        if (StringUtils.isNotEmpty(this.tunnelEndPoint)) {
            str += ",tunnelEndPoint=" + this.tunnelEndPoint;
        }
        String partitionString = getPartitionString();
        if (StringUtils.isNotEmpty(partitionString)) {
            str += ",partition[" + partitionString + "]";
        }
        str += ",uploadSessionId=" + this.uploadSessionId;
        return str;
    }

    @Override
    public int compareTo(OdpsTableInfo that) {
        return this.getTableInfoKeyString().compareTo(that.getTableInfoKeyString());
    }

    /**
     * Getter method for property <tt>sessionId</tt>.
     *
     * @return property value of sessionId
     */
    public String getUploadSessionId() {
        return uploadSessionId;
    }

    /**
     * Setter method for property <tt>sessionId</tt>.
     *
     * @param uploadSessionId value to be assigned to property sessionId
     */
    public void setUploadSessionId(String uploadSessionId) {
        this.uploadSessionId = uploadSessionId;
    }
}