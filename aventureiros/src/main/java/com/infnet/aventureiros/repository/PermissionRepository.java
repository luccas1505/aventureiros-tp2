package com.infnet.aventureiros.repository;

import com.infnet.aventureiros.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para audit.permissions.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /** Busca por código único (UK no banco). */
    Optional<Permission> findByCode(String code);
}
