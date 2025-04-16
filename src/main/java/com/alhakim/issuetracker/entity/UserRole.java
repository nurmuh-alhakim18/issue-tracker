package com.alhakim.issuetracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_role")
public class UserRole {

    @EmbeddedId
    private UserRoleId id;

    @Data
    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserRoleId implements Serializable {
        private Long userId;
        private Long roleId;
    }
}
