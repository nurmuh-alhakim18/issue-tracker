package com.alhakim.issuetracker.controller;

import com.alhakim.issuetracker.dto.BaseResponse;
import com.alhakim.issuetracker.dto.IssueRequest;
import com.alhakim.issuetracker.dto.IssueResponse;
import com.alhakim.issuetracker.dto.IssueUpdateRequest;
import com.alhakim.issuetracker.service.CurrentUserService;
import com.alhakim.issuetracker.service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/issues")
public class IssueController {

    private final CurrentUserService currentUserService;
    private final IssueService issueService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createIssue(@RequestBody @Valid IssueRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        issueService.createIssue(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success("Issue created"));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<IssueResponse>>> getIssues() {
        List<IssueResponse> issueResponses = issueService.getIssues();
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success("Get issue list success", issueResponses));
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
