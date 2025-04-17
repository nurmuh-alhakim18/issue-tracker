package com.alhakim.issuetracker.repository;

import com.alhakim.issuetracker.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {

    @Query(value = """
    SELECT *
    FROM tags
    WHERE id in (:tagIds)
    """, nativeQuery = true)
    List<Tag> findByIdIn(List<Long> tagIds);

    @Query(value = """
    SELECT *
    FROM tags
    WHERE name IN (:names)
    """, nativeQuery = true)
    List<Tag> findByNameIn(List<String> names);

    @Query(value = """
    SELECT *
    FROM tags
    WHERE LOWER(name) IN (:names)
    """, nativeQuery = true)
    List<Tag> findByNameInIgnoreCase(List<String> names);
}
