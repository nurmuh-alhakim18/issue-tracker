package com.alhakim.issuetracker.controller;

import com.alhakim.issuetracker.dto.*;
import com.alhakim.issuetracker.service.CurrentUserService;
import com.alhakim.issuetracker.service.IssueService;
import com.alhakim.issuetracker.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/issues")
public class IssueController {

    private final CurrentUserService currentUserService;
    private final IssueService issueService;
    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createIssue(@RequestBody @Valid IssueRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        issueService.createIssue(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success("Issue created"));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PaginatedResponse<IssueResponse>>> getIssues(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String direction

    ) {
        int pageIndex = Math.max(0, page - 1);
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(orderBy).descending() : Sort.by(orderBy);
        Pageable pageable = PageRequest.of(pageIndex, size, sort);

        PaginatedResponse<IssueResponse> issueResponses = issueService.getIssues(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success("Get issue list success", issueResponses));
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<PaginatedResponse<IssueResponse>>> searchIssues(@RequestBody IssueSearchRequest request) {
        PaginatedResponse<IssueResponse> issueResponse = searchService.search(request);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success("Search issues success", issueResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<IssueResponse>> getIssue(@PathVariable Long id) {
        IssueResponse issueResponse = issueService.getIssue(id);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success("Get issue success", issueResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> updateIssue(@PathVariable Long id, @RequestBody @Valid IssueUpdateRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        IssueResponse issue = issueService.getIssue(id);
        if (!issue.getCreatedBy().getId().equals(userId)) {
            Map<String, String> errors = Map.of("general", "User is not authorized to update this issue");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(BaseResponse.fail("Update issue failed", errors));
        }

        issueService.updateIssue(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success("Issue updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteIssue(@PathVariable Long id) {
        Long userId = currentUserService.getCurrentUserId();
        IssueResponse issue = issueService.getIssue(id);
        if (!issue.getCreatedBy().getId().equals(userId)) {
            Map<String, String> errors = Map.of("general", "User is not authorized to delete this issue");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(BaseResponse.fail("Delete issue failed", errors));
        }

        issueService.deleteIssue(id);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success("Issue deleted"));
    }
}
