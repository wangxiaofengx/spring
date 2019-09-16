package com.zy.bo;

import java.util.Date;

public class OaMessage {

    private Long id;

    private String summary;

    private Date createTime;

    private Date modifyTime;

    private String businessKey;

    private String taskDefinitionKey;

    private String executionId;

    private String receiveUserId;

    private String senderUserId;

    private String data;

    private String type;

    private Integer readFlag;

    private String mobilePhone;

    private String ywlx;

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYwlx() {
        return ywlx;
    }

    public void setYwlx(String ywlx) {
        this.ywlx = ywlx;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(String receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Integer getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(Integer readFlag) {
        this.readFlag = readFlag;
    }

    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    @Override
    public String toString() {
        return "OaMessage{" +
                "id=" + id +
                ", summary='" + summary + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", businessKey='" + businessKey + '\'' +
                ", taskDefinitionKey='" + taskDefinitionKey + '\'' +
                ", executionId='" + executionId + '\'' +
                ", receiveUserId='" + receiveUserId + '\'' +
                ", senderUserId='" + senderUserId + '\'' +
                ", data='" + data + '\'' +
                ", type='" + type + '\'' +
                ", readFlag=" + readFlag +
                ", mobilePhone='" + mobilePhone + '\'' +
                '}';
    }
}
