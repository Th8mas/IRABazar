package com.droow.irabazar.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by droow on 18.7.16.
 */
@MappedSuperclass
public class BaseEntity implements Serializable {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    private Timestamp created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated", nullable = false)
    private Timestamp updated;

    @PrePersist
    protected void onCreate() {
        this.created = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdated() {
        this.updated = new Timestamp(System.currentTimeMillis());
    }

    public void setCreated(Timestamp time) {
        this.created = time;
    }

    public Timestamp getCreated() {
        return created;
    }
}
