package com.alhakim.issuetracker.controller;

import com.alhakim.issuetracker.dto.BaseResponse;
import com.alhakim.issuetracker.dto.CommentRequest;
import com.alhakim.issuetracker.dto.CommentResponse;
import com.alhakim.issuetracker.dto.PaginatedResponse;
import com.alhakim.issuetracker.service.CommentService;
import com.alhakim.issuetracker.service.CurrentUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/issues/{id}/comments")
public class CommentController {

    private final CurrentUserService currentUserService;
    private final CommentService commentService;

    @PostMapping
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<BaseResponse<Void>> createComment(@PathVariable Long id, @RequestBody CommentRequest commentRequest) {
        Long userId = currentUserService.getCurrentUserId();
        commentService.createComment(commentRequest, id, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success("Comment created"));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PaginatedResponse<CommentResponse>>> getComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        int pageIndex = Math.max(0, page - 1);
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(orderBy).descending() : Sort.by(orderBy);
        Pageable pageable = PageRequest.of(pageIndex, size, sort);

        PaginatedResponse<CommentResponse> comments = commentService.getComments(id, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success("Get comments success", comments));
    }
}
