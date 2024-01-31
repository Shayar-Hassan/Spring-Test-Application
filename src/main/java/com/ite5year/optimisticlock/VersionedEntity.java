package com.ite5year.optimisticlock;

public interface VersionedEntity {
    Long getId();

    Long getVersion();

    void setVersion(Long version);

    String getTableName();
}
