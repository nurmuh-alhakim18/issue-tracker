package com.alhakim.issuetracker.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "issue_tags")
public class IssueTag {

    @EmbeddedId
    private IssueTagId id;

    @Data
    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IssueTagId implements Serializable {
        private Long issueId;
        private Long tagId;
    }
}
