package com.alhakim.issuetracker.service;

import com.alhakim.issuetracker.dto.IssueRequest;
import com.alhakim.issuetracker.dto.IssueResponse;
import com.alhakim.issuetracker.dto.IssueResponse.CreatedBy;
import com.alhakim.issuetracker.dto.IssueUpdateRequest;
import com.alhakim.issuetracker.entity.Issue;
import com.alhakim.issuetracker.entity.IssueTag;
import com.alhakim.issuetracker.entity.IssueTag.IssueTagId;
import com.alhakim.issuetracker.entity.Tag;
import com.alhakim.issuetracker.entity.User;
import com.alhakim.issuetracker.exception.ResourceNotFoundException;
import com.alhakim.issuetracker.repository.IssueRepository;
import com.alhakim.issuetracker.repository.IssueTagRepository;
import com.alhakim.issuetracker.repository.TagRepository;
import com.alhakim.issuetracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    }

    @Override
    public List<IssueResponse> getIssues() {
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

            Long userId = Optional.ofNullable(userMap.get(issue.getCreatedBy())).map(User::getId).orElse(null);
            String username = Optional.ofNullable(userMap.get(issue.getCreatedBy())).map(User::getUsername).orElse("Unknown");
            CreatedBy createdBy = IssueResponse.CreatedBy.builder().id(userId).username(username).build();

            return IssueResponse.create(issue, tagNames, createdBy);
        }).toList();
    }

    @Override
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

        if (issueUpdateRequest.getTags() != null) {
            issueTagRepository.deleteByIdIssueId(issueId);

            processTags(issue, issueUpdateRequest.getTags());
        }
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
    public void deleteIssue(Long issueId) {
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        issueTagRepository.deleteByIdIssueId(issueId);
        issueRepository.delete(issue);
    }
}
