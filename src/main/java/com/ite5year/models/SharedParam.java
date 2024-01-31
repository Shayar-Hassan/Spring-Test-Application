package com.ite5year.models;

import javax.persistence.*;
import java.io.Serializable;

// INSERT INTO shared_parameters(field_key, field_value) VALUES ('seatsNumber', 4)
// INSERT INTO shared_parameters(field_key, field_value) VALUES ('profitPercentage', 1000)
@Entity
public class SharedParam implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id long id;
    String fieldKey, fieldValue;

    public SharedParam() {
    }

    public SharedParam(String fieldKey, String fieldValue) {
        this.fieldKey = fieldKey;
        this.fieldValue = fieldValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

}
