package com.alhakim.issuetracker.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.alhakim.issuetracker.dto.IssueIndex;
import com.alhakim.issuetracker.entity.Issue;
import com.alhakim.issuetracker.entity.IssueTag;
import com.alhakim.issuetracker.entity.Tag;
import com.alhakim.issuetracker.entity.User;
import com.alhakim.issuetracker.exception.ResourceNotFoundException;
import com.alhakim.issuetracker.repository.IssueRepository;
import com.alhakim.issuetracker.repository.IssueTagRepository;
import com.alhakim.issuetracker.repository.TagRepository;
import com.alhakim.issuetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IssueIndexServiceImpl implements IssueIndexService {

    private final ElasticsearchClient elasticsearchClient;
    private final IssueRepository issueRepository;
    private final TagRepository tagRepository;
    private final IssueTagRepository issueTagRepository;
    private final UserRepository userRepository;

    private static final String INDEX_NAME = "issues";

    @Override
    @Async
    public void bulkIndexIssues() throws IOException {
        BulkRequest.Builder br = new BulkRequest.Builder();
        List<IssueIndex> issuesIndexes = processIssueToIssueIndexes();

        issuesIndexes.forEach(issueIndex -> br
                .operations(op -> op
                        .index(idx -> idx
                                .index(INDEX_NAME)
                                .id(issueIndex.getId())
                                .document(issueIndex)
                        )
                )
        );

        BulkResponse result = elasticsearchClient.bulk(br.build());
        if (result.errors()) {
            log.error("Error while bulk indexing issues");
            for (BulkResponseItem item : result.items()) {
                if (item.error() != null) {
                    log.error(item.error().reason());
                }
            }
        }
    }

    private List<IssueIndex> processIssueToIssueIndexes() {
        List<Issue> issues = issueRepository.findAll();
        Set<Long> issuesIds = issues.stream().map(Issue::getId).collect(Collectors.toSet());
        Set<Long> userIds = issues.stream().map(Issue::getCreatedBy).collect(Collectors.toSet());

        List<IssueTag> allIssueTags = issueTagRepository.findByIdIssueIdIn(issuesIds);
        Map<Long, List<Long>> issueIdToTagIds = allIssueTags.stream()
                .collect(Collectors.groupingBy(
                        it -> it.getId().getIssueId(),
                        Collectors.mapping(it -> it.getId().getTagId(), Collectors.toList())
                ));

        List<Long> allTagIds = allIssueTags.stream().map(it -> it.getId().getTagId()).toList();
        Map<Long, String> tagIdToName = tagRepository.findByIdIn(allTagIds).stream()
                .collect(Collectors.toMap(Tag::getId, Tag::getName));

        Map<Long, User> userMap = userRepository.findByIdIn(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return issues.stream().map(issue -> {
            List<String> tagNames = issueIdToTagIds.getOrDefault(issue.getId(), List.of()).stream()
                    .map(tagIdToName::get)
                    .toList();

            User user = userMap.get(issue.getCreatedBy());
            Long userId = user != null ? user.getId() : null;
            String username = user != null ? user.getUsername() : "Unknown";
            IssueIndex.CreatedBy createdBy = IssueIndex.CreatedBy.builder().id(userId).username(username).build();

            return IssueIndex.create(issue, tagNames, createdBy);
        }).toList();
    }

    @Override
    @Async
    public void indexIssue(Issue issue) {
        List<Long> tagIds = issueTagRepository.findByIdIssueId(issue.getId()).stream()
                .map(it -> it.getId().getTagId()).toList();

        List<String> tagNames = tagRepository.findByIdIn(tagIds).stream().map(Tag::getName).toList();

        User user = userRepository.findById(issue.getCreatedBy()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Long userId = user.getId();
        String username = user.getUsername();
        IssueIndex.CreatedBy createdBy = IssueIndex.CreatedBy.builder().id(userId).username(username).build();

        IssueIndex issueIndex = IssueIndex.create(issue, tagNames, createdBy);

        try {
            elasticsearchClient.index(i -> i
                    .index(INDEX_NAME)
                    .id(issueIndex.getId())
                    .document(issueIndex)
            );
        } catch (IOException e) {
            log.error("Failed to index issue with ID {}: {}", issue.getId(), e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void deleteIssue(Issue issue) {
        try {
            elasticsearchClient.delete(d -> d
                    .index(INDEX_NAME)
                    .id(issue.getId().toString())
            );
        } catch (IOException e) {
            log.error("Failed to delete issue with ID {}: {}", issue.getId(), e.getMessage(), e);
        }
    }
}
