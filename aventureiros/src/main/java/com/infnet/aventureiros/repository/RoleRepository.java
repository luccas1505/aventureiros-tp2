package com.infnet.aventureiros.repository;

import com.infnet.aventureiros.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByOrganizacaoIdAndNome(Long organizacaoId, String nome);

    List<Role> findByOrganizacaoId(Long organizacaoId);

    @Query("""
            SELECT DISTINCT r FROM Role r
            LEFT JOIN FETCH r.permissions
            WHERE r.id = :id
           """)
    Optional<Role> findByIdWithPermissions(@Param("id") Long id);

    @Query("""
            SELECT DISTINCT r FROM Role r
            LEFT JOIN FETCH r.permissions
            WHERE r.organizacao.id = :orgId
           """)
    List<Role> findByOrganizacaoIdWithPermissions(@Param("orgId") Long orgId);
}
