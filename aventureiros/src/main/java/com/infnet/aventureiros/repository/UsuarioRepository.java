package com.infnet.aventureiros.repository;

import com.infnet.aventureiros.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para audit.usuarios.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /** Busca por email dentro de uma organização (UK composta no banco). */
    Optional<Usuario> findByOrganizacaoIdAndEmail(Long organizacaoId, String email);

    /** Lista todos os usuários de uma organização. */
    List<Usuario> findByOrganizacaoId(Long organizacaoId);

    /**
     * Carrega um usuário com seus roles e permissões em uma única query
     * usando JOIN FETCH para evitar o problema N+1.
     *
     * A estratégia de dois fetches separados evita o produto cartesiano
     * quando há múltiplos relacionamentos @ManyToMany.
     */
    @Query("""
            SELECT DISTINCT u FROM Usuario u
            LEFT JOIN FETCH u.roles r
            LEFT JOIN FETCH r.permissions
            WHERE u.id = :id
           """)
    Optional<Usuario> findByIdWithRolesAndPermissions(@Param("id") Long id);

    /**
     * Lista usuários com seus roles — útil para relatórios.
     * Usamos DISTINCT para evitar duplicatas geradas pelo JOIN.
     */
    @Query("""
            SELECT DISTINCT u FROM Usuario u
            LEFT JOIN FETCH u.roles
            WHERE u.organizacao.id = :orgId
           """)
    List<Usuario> findByOrganizacaoIdWithRoles(@Param("orgId") Long orgId);
}
