package com.alhakim.issuetracker.dto;

import com.alhakim.issuetracker.entity.Issue;
import com.alhakim.issuetracker.entity.Issue.Priority;
import com.alhakim.issuetracker.entity.Issue.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueIndex {
    private String id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private List<String> tags;
    private CreatedBy createdBy;
    private Date createdAt;
    private Date updatedAt;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreatedBy {
        private Long id;
        private String username;
    }

    public static IssueIndex create(Issue issue, List<String> tagNames, CreatedBy createdBy) {
        Date createdAt = convertLocalDateTimeToDate(issue.getCreatedAt());
        Date updatedAt = convertLocalDateTimeToDate(issue.getUpdatedAt());

        return IssueIndex.builder()
                .id(issue.getId().toString())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .status(issue.getStatus())
                .priority(issue.getPriority())
                .tags(tagNames)
                .createdBy(createdBy)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return Date.from(zonedDateTime.toInstant());
    }
}
