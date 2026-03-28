package com.infnet.aventureiros.repository;

import com.infnet.aventureiros.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para audit.roles.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /** Busca por nome dentro de uma organização (UK composta). */
    Optional<Role> findByOrganizacaoIdAndNome(Long organizacaoId, String nome);

    /** Lista roles de uma organização. */
    List<Role> findByOrganizacaoId(Long organizacaoId);

    /**
     * Carrega role com suas permissões (JOIN FETCH para evitar N+1).
     */
    @Query("""
            SELECT DISTINCT r FROM Role r
            LEFT JOIN FETCH r.permissions
            WHERE r.id = :id
           """)
    Optional<Role> findByIdWithPermissions(@Param("id") Long id);

    /**
     * Lista roles de uma organização com suas permissões.
     */
    @Query("""
            SELECT DISTINCT r FROM Role r
            LEFT JOIN FETCH r.permissions
            WHERE r.organizacao.id = :orgId
           """)
    List<Role> findByOrganizacaoIdWithPermissions(@Param("orgId") Long orgId);
}
