package com.ite5year.models;

import com.ite5year.optimisticlock.VersionedEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Logs {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date createdAt;
    private String processName;
    private String doerName;
    private String doerEmail;
    private String targetName;

    public Logs() {
    }

    public Logs(Date createdAt, String processName, String doerName, String doerEmail, String targetName) {
        this.createdAt = createdAt;
        this.processName = processName;
        this.doerName = doerName;
        this.doerEmail = doerEmail;
        this.targetName = targetName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getDoerName() {
        return doerName;
    }

    public void setDoerName(String doerName) {
        this.doerName = doerName;
    }

    public String getDoerEmail() {
        return doerEmail;
    }

    public void setDoerEmail(String doerEmail) {
        this.doerEmail = doerEmail;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
}


