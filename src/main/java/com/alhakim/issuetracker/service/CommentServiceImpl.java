package com.alhakim.issuetracker.service;

import com.alhakim.issuetracker.dto.CommentRequest;
import com.alhakim.issuetracker.dto.CommentResponse;
import com.alhakim.issuetracker.dto.CommentResponse.UserComment;
import com.alhakim.issuetracker.dto.PaginatedResponse;
import com.alhakim.issuetracker.entity.Comment;
import com.alhakim.issuetracker.entity.User;
import com.alhakim.issuetracker.exception.ResourceNotFoundException;
import com.alhakim.issuetracker.repository.CommentRepository;
import com.alhakim.issuetracker.repository.IssueRepository;
import com.alhakim.issuetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    @Override
    public void createComment(CommentRequest commentRequest, Long issueId, Long userId) {
        issueRepository.findById(issueId).orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        Comment comment = Comment.builder()
                .issueId(issueId)
                .userId(userId)
                .content(commentRequest.getContent())
                .build();

        commentRepository.save(comment);
    }

    @Override
    public PaginatedResponse<CommentResponse> getComments(Long id, Pageable pageable) {
        issueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
        Page<Comment> comments = commentRepository.findByIssueId(id, pageable);
        Set<Long> userIds = comments.stream().map(Comment::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userRepository.findByIdIn(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<CommentResponse> commentResponses = comments.getContent().stream().map(comment -> {
            User user = userMap.get(comment.getUserId());
            Long userId = user != null ? user.getId() : null;
            String username = user != null ? user.getUsername() : "Unknown";
            UserComment userComment = UserComment.builder().id(userId).username(username).build();

            return CommentResponse.create(comment, userComment);
        }).toList();

        Page<CommentResponse> commentResponsePage = new PageImpl<>(commentResponses, pageable, comments.getTotalElements());
        return PaginatedResponse.create(commentResponsePage);
    }
}
