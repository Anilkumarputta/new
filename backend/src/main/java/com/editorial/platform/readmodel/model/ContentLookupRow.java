package com.editorial.platform.readmodel.model;

import java.time.Instant;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("content_lookup")
public class ContentLookupRow {

    @PrimaryKeyClass
    public static class Key {

        @PrimaryKeyColumn(name = "content_type", type = PrimaryKeyType.PARTITIONED, ordinal = 0)
        private String contentType;

        @PrimaryKeyColumn(name = "content_id", type = PrimaryKeyType.CLUSTERED, ordinal = 1)
        private Long contentId;

        public Key() {
        }

        public Key(String contentType, Long contentId) {
            this.contentType = contentType;
            this.contentId = contentId;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public Long getContentId() {
            return contentId;
        }

        public void setContentId(Long contentId) {
            this.contentId = contentId;
        }
    }

    @org.springframework.data.cassandra.core.mapping.PrimaryKey
    private Key key;

    @Column("title")
    private String title;

    @Column("status")
    private String status;

    @Column("category_name")
    private String categoryName;

    @Column("trainer_name")
    private String trainerName;

    @Column("updated_at")
    private Instant updatedAt;

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
