package com.infnet.aventureiros.repository;

import com.infnet.aventureiros.entity.aventura.Aventureiro;
import com.infnet.aventureiros.entity.aventura.ClasseAventureiro;
import com.infnet.aventureiros.dto.aventureiro.AventureiroResumoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AventureiroRepository extends JpaRepository<Aventureiro, Long> {

    @Query("""
        SELECT new com.infnet.aventureiros.dto.aventureiro.AventureiroResumoDTO(
            a.id, a.nome, a.classe, a.nivel, a.ativo)
        FROM Aventureiro a
        WHERE a.organizacao.id = :orgId
          AND (:ativo IS NULL OR a.ativo = :ativo)
          AND (:classe IS NULL OR a.classe = :classe)
          AND (:nivelMinimo IS NULL OR a.nivel >= :nivelMinimo)
        """)
    Page<AventureiroResumoDTO> listarComFiltros(
        @Param("orgId") Long orgId,
        @Param("ativo") Boolean ativo,
        @Param("classe") ClasseAventureiro classe,
        @Param("nivelMinimo") Integer nivelMinimo,
        Pageable pageable
    );

    @Query("""
        SELECT new com.infnet.aventureiros.dto.aventureiro.AventureiroResumoDTO(
            a.id, a.nome, a.classe, a.nivel, a.ativo)
        FROM Aventureiro a
        WHERE a.organizacao.id = :orgId
          AND LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
        """)
    Page<AventureiroResumoDTO> buscarPorNome(
        @Param("orgId") Long orgId,
        @Param("nome") String nome,
        Pageable pageable
    );

    @Query("""
        SELECT a FROM Aventureiro a
        LEFT JOIN FETCH a.companheiro
        WHERE a.id = :id
        """)
    Optional<Aventureiro> findByIdWithCompanheiro(@Param("id") Long id);

    @Query("""
        SELECT COUNT(p) FROM ParticipacaoMissao p
        WHERE p.aventureiro.id = :aventureiroId
        """)
    Long countParticipacoes(@Param("aventureiroId") Long aventureiroId);
}
