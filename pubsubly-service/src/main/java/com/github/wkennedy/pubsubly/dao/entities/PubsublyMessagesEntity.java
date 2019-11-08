package com.github.wkennedy.pubsubly.dao.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Arrays;

@Entity
@Table(name = "PUBSUBLY_MESSAGES")
public class PubsublyMessagesEntity {
    private String id;
    private byte[] headers;
    private byte[] payload;
    private Timestamp createdDt;

    @Id
    @Column(name = "ID", nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Lob
    @Column(name = "HEADERS")
    public byte[] getHeaders() {
        return headers;
    }

    public void setHeaders(byte[] headers) {
        this.headers = headers;
    }

    @Lob
    @Column(name = "PAYLOAD")
    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    @Basic
    @Column(name = "CREATED_DT")
    public Timestamp getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Timestamp createdDt) {
        this.createdDt = createdDt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PubsublyMessagesEntity that = (PubsublyMessagesEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (headers != null ? !headers.equals(that.headers) : that.headers != null) return false;
        if (payload != null ? !payload.equals(that.payload) : that.payload != null) return false;
        return createdDt != null ? createdDt.equals(that.createdDt) : that.createdDt == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (headers != null ? headers.hashCode() : 0);
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        result = 31 * result + (createdDt != null ? createdDt.hashCode() : 0);
        return result;
    }
}
