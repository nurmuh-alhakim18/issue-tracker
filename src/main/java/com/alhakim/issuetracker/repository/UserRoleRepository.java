package com.alhakim.issuetracker.repository;

import com.alhakim.issuetracker.entity.UserRole;
import com.alhakim.issuetracker.entity.UserRole.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    void deleteByIdUserId(Long userId);
}
