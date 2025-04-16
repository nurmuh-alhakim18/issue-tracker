package com.alhakim.issuetracker.repository;

import com.alhakim.issuetracker.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value = """
    SELECT id
    FROM roles
    WHERE name = :roleName
    """, nativeQuery = true)
    Optional<Long> findRoleIdByName(String roleName);

    @Query(value = """
        SELECT r.*
        FROM roles AS r
        JOIN user_role AS ur ON r.id = ur.role_id
        JOIN users AS u ON ur.user_id = u.id
        WHERE u.id = :userId
        """, nativeQuery = true)
    List<Role> findByUserId(Long userId);
}
