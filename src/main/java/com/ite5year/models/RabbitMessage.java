package com.ite5year.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Date;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = RabbitMessage.class)
public class RabbitMessage {

    private String email;
    private String content;
    private String date;
    private String fileName;


    public RabbitMessage(){}
    public RabbitMessage(String email, String content, String date) {
        this.email = email;
        this.content = content;
        this.date = date;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "RabbitMessage{" +
                "email='" + email + '\'' +
                ", content='" + content + '\'' +
                ", date=" + date +
                '}';
    }
}
