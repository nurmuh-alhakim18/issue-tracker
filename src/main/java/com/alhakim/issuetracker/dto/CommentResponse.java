package com.alhakim.issuetracker.dto;

import com.alhakim.issuetracker.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    private Long id;
    private UserComment user;
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserComment {
        private Long id;
        private String username;
    }

    public static CommentResponse create(Comment comment, UserComment userComment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .user(userComment)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
