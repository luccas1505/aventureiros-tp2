package com.infnet.aventureiros.repository;

import com.infnet.aventureiros.dto.relatorio.RelatorioMissaoDTO;
import com.infnet.aventureiros.entity.aventura.Missao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MissaoRepository extends JpaRepository<Missao, Long>,
        JpaSpecificationExecutor<Missao> {

    @Query("""
        SELECT DISTINCT m FROM Missao m
        LEFT JOIN FETCH m.participacoes p
        LEFT JOIN FETCH p.aventureiro
        WHERE m.id = :id
        """)
    Optional<Missao> findByIdWithParticipacoes(@Param("id") Long id);

    @Query("""
        SELECT new com.infnet.aventureiros.dto.relatorio.RelatorioMissaoDTO(
            m.id, m.titulo, m.status, m.nivelPerigo,
            COUNT(p.aventureiro),
            COALESCE(SUM(p.recompensaOuro), 0))
        FROM Missao m
        LEFT JOIN m.participacoes p
        WHERE m.organizacao.id = :orgId
          AND m.createdAt >= :dataInicio
          AND m.createdAt <= :dataFim
        GROUP BY m.id, m.titulo, m.status, m.nivelPerigo
        """)
    List<RelatorioMissaoDTO> relatorioMissoes(
        @Param("orgId") Long orgId,
        @Param("dataInicio") OffsetDateTime dataInicio,
        @Param("dataFim") OffsetDateTime dataFim
    );
}
