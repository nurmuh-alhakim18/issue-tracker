package com.alhakim.issuetracker.service;

import com.alhakim.issuetracker.dto.IssueRequest;
import com.alhakim.issuetracker.dto.IssueResponse;
import com.alhakim.issuetracker.dto.IssueResponse.CreatedBy;
import com.alhakim.issuetracker.dto.IssueUpdateRequest;
import com.alhakim.issuetracker.dto.PaginatedResponse;
import com.alhakim.issuetracker.entity.Issue;
import com.alhakim.issuetracker.entity.IssueTag;
import com.alhakim.issuetracker.entity.IssueTag.IssueTagId;
import com.alhakim.issuetracker.entity.Tag;
import com.alhakim.issuetracker.entity.User;
import com.alhakim.issuetracker.exception.ResourceNotFoundException;
import com.alhakim.issuetracker.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final TagRepository tagRepository;
    private final IssueTagRepository issueTagRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final IssueIndexService issueIndexService;

    @Override
    @Transactional
    public void createIssue(IssueRequest issueRequest, Long userId) {
        Issue issue = Issue.builder()
                .title(issueRequest.getTitle())
                .description(issueRequest.getDescription())
                .status(Issue.Status.OPEN)
                .priority(issueRequest.getPriority())
                .createdBy(userId)
                .build();

        issueRepository.save(issue);

        processTags(issue, issueRequest.getTags());
        issueIndexService.indexIssue(issue);
    }

    @Override
    public PaginatedResponse<IssueResponse> getIssues(Pageable pageable) {
        Page<Issue> issuesPage = issueRepository.findAll(pageable);
        Set<Long> issuesIds = issuesPage.getContent().stream().map(Issue::getId).collect(Collectors.toSet());
        Set<Long> userIds = issuesPage.getContent().stream().map(Issue::getCreatedBy).collect(Collectors.toSet());

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

        List<IssueResponse> issues = issuesPage.getContent().stream().map(issue -> {
            List<String> tagNames = issueIdToTagIds.getOrDefault(issue.getId(), List.of()).stream()
                    .map(tagIdToName::get)
                    .toList();

            User user = userMap.get(issue.getCreatedBy());
            Long userId = user != null ? user.getId() : null;
            String username = user != null ? user.getUsername() : "Unknown";
            CreatedBy createdBy = IssueResponse.CreatedBy.builder().id(userId).username(username).build();

            return IssueResponse.create(issue, tagNames, createdBy);
        }).toList();

        Page<IssueResponse> issueResponsePage = new PageImpl<>(issues, pageable, issuesPage.getTotalElements());
        return PaginatedResponse.create(issueResponsePage);
    }

    @Override
    @Cacheable(value = "issue", key = "#issueId")
    public IssueResponse getIssue(Long issueId) {
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        List<Long> tagIds = issueTagRepository.findByIdIssueId(issueId).stream()
                .map(it -> it.getId().getTagId()).toList();

        List<String> tagNames = tagRepository.findByIdIn(tagIds).stream().map(Tag::getName).toList();

        User user = userRepository.findById(issue.getCreatedBy()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Long userId = user.getId();
        String username = user.getUsername();
        CreatedBy createdBy = IssueResponse.CreatedBy.builder().id(userId).username(username).build();

        return IssueResponse.create(issue, tagNames, createdBy);
    }

    @Override
    @Transactional
    @CacheEvict(value = "issue", key = "#issueId")
    @CachePut(value = "issue", key = "#issueId")
    public void updateIssue(Long issueId, IssueUpdateRequest issueUpdateRequest) {
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        if (issueUpdateRequest.getTitle() != null && !issueUpdateRequest.getTitle().isBlank()) {
            issue.setTitle(issueUpdateRequest.getTitle());
        }

        if (issueUpdateRequest.getDescription() != null && !issueUpdateRequest.getDescription().isBlank()) {
            issue.setDescription(issueUpdateRequest.getDescription());
        }

        if (issueUpdateRequest.getStatus() != null) {
            issue.setStatus(issueUpdateRequest.getStatus());
        }

        if (issueUpdateRequest.getPriority() != null) {
            issue.setPriority(issueUpdateRequest.getPriority());
        }

        issueRepository.save(issue);

        if (issueUpdateRequest.getTags() != null) {
            issueTagRepository.deleteByIdIssueId(issueId);

            processTags(issue, issueUpdateRequest.getTags());
        }

        issueIndexService.indexIssue(issue);
    }

    private void processTags(Issue issue, List<String> tagsInput) {
        List<String> tagNames = tagsInput.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .distinct()
                .toList();

        List<Tag> tags = tagRepository.findByNameInIgnoreCase(tagNames);
        List<IssueTag> issueTags = new ArrayList<>();
        tags.forEach(tag -> issueTags.add(
                        IssueTag.builder()
                                .id(new IssueTagId(issue.getId(), tag.getId()))
                                .build()
                )
        );

        if (!issueTags.isEmpty()) {
            issueTagRepository.saveAll(issueTags);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "issue", key = "#issueId")
    public void deleteIssue(Long issueId) {
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        issueTagRepository.deleteByIdIssueId(issueId);
        commentRepository.deleteByIssueId(issueId);
        issueRepository.delete(issue);
        issueIndexService.deleteIssue(issue);
    }
}
