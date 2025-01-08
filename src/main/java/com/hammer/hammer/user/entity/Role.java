package com.hammer.hammer.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private Long roleId; // 역할 ID (Primary Key)

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;; // 역할 이름 (e.g., ADMIN, SELLER, BUYER, USER)
 
  
}