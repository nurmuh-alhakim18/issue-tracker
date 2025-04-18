package com.alhakim.issuetracker.service;

import com.alhakim.issuetracker.dto.CommentRequest;
import com.alhakim.issuetracker.dto.CommentResponse;
import com.alhakim.issuetracker.dto.PaginatedResponse;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    void createComment(CommentRequest commentRequest, Long issueId, Long userId);
    PaginatedResponse<CommentResponse> getComments(Long id, Pageable pageable);
}
