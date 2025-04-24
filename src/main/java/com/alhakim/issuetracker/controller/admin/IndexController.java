package com.alhakim.issuetracker.controller.admin;

import com.alhakim.issuetracker.dto.BaseResponse;
import com.alhakim.issuetracker.service.IssueIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/admin/index")
@RequiredArgsConstructor
public class IndexController {

    private final IssueIndexService issueIndexService;

    @PostMapping("/issues")
    public ResponseEntity<BaseResponse<Void>> bulkIndexIssues() {
        try {
            issueIndexService.bulkIndexIssues();
            return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success("Bulk Index Issues Success"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BaseResponse.fail(e.getMessage()));
        }
    }
}
