package com.alhakim.issuetracker.repository;

import com.alhakim.issuetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = """
        SELECT *
        FROM users
        WHERE email = :identity OR username = :identity
        """, nativeQuery = true)
    Optional<User> findByIdentity(String identity);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
