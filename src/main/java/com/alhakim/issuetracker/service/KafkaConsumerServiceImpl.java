package com.alhakim.issuetracker.service;

import com.alhakim.issuetracker.entity.Issue;
import com.alhakim.issuetracker.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private final IssueIndexService issueIndexService;
    private final IssueRepository issueRepository;

    @Override
    @KafkaListener(topics = "${kafka.topic.index-issue.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenIssueIndex(String message) {
        String[] parts = message.split(",");
        String action = parts[0];
        Long id = Long.parseLong(parts[1]);

        if (!List.of("INDEX", "DELETE").contains(action)) {
            log.info("Issue index action invalid: " + action);
            return;
        }

        Issue issue = issueRepository.findById(id).orElse(null);
        if (issue == null) {
            log.info("Issue not found: " + id);
            return;
        }

        if (action.equals("INDEX")) {
            log.info("Issue indexed: " + issue);
            issueIndexService.indexIssue(issue);
        } else if (action.equals("DELETE")) {
            log.info("Issue deleted: " + issue);
            issueIndexService.deleteIssue(issue);
        }
    }
}
