package com.alhakim.issuetracker.repository;

import com.alhakim.issuetracker.entity.IssueTag;
import com.alhakim.issuetracker.entity.IssueTag.IssueTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IssueTagRepository extends JpaRepository<IssueTag, IssueTagId> {

    List<IssueTag> findByIdIssueId(Long issueId);

    @Query(value = """
    SELECT *
    FROM issue_tags
    WHERE issue_id IN (:issueIds)
    """, nativeQuery = true)
    List<IssueTag> findByIdIssueIdIn(Set<Long> issueIds);

    void deleteByIdIssueId(Long issueId);
}
