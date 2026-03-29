package com.infnet.aventureiros.repository;

import com.infnet.aventureiros.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByOrganizacaoIdAndEmail(Long organizacaoId, String email);

    List<Usuario> findByOrganizacaoId(Long organizacaoId);

    @Query("""
            SELECT DISTINCT u FROM Usuario u
            LEFT JOIN FETCH u.roles r
            LEFT JOIN FETCH r.permissions
            WHERE u.id = :id
           """)
    Optional<Usuario> findByIdWithRolesAndPermissions(@Param("id") Long id);

    @Query("""
            SELECT DISTINCT u FROM Usuario u
            LEFT JOIN FETCH u.roles
            WHERE u.organizacao.id = :orgId
           """)
    List<Usuario> findByOrganizacaoIdWithRoles(@Param("orgId") Long orgId);
}
