package com.alhakim.issuetracker.dto;

import com.alhakim.issuetracker.entity.Issue;
import com.alhakim.issuetracker.entity.Issue.Status;
import com.alhakim.issuetracker.entity.Issue.Priority;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
public class IssueResponse {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private List<String> tags;
    private CreatedBy createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreatedBy {
        private Long id;
        private String username;
    }

    public static IssueResponse create(Issue issue, List<String> tagNames, CreatedBy createdBy) {
        return IssueResponse.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .status(issue.getStatus())
                .priority(issue.getPriority())
                .tags(tagNames)
                .createdBy(createdBy)
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
                .build();
    }

    public static IssueResponse create(IssueIndex issueIndex) {
        CreatedBy createdBy = CreatedBy.builder()
                .id(issueIndex.getCreatedBy().getId())
                .username(issueIndex.getCreatedBy().getUsername())
                .build();

        return IssueResponse.builder()
                .id(Long.parseLong(issueIndex.getId()))
                .title(issueIndex.getTitle())
                .description(issueIndex.getDescription())
                .status(issueIndex.getStatus())
                .priority(issueIndex.getPriority())
                .tags(issueIndex.getTags())
                .createdBy(createdBy)
                .createdAt(convertDateToLocalDateTime(issueIndex.getCreatedAt()))
                .updatedAt(convertDateToLocalDateTime(issueIndex.getUpdatedAt()))
                .build();
    }

    private static LocalDateTime convertDateToLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
