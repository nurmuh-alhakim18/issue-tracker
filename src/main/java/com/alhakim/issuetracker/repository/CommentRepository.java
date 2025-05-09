package com.alhakim.issuetracker.repository;

import com.alhakim.issuetracker.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByIssueId(Long issueId, Pageable pageable);

    void deleteByIssueId(Long issueId);
}
