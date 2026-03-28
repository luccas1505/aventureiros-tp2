package com.infnet.aventureiros.repository;

import com.infnet.aventureiros.dto.relatorio.RankingAventureiroDTO;
import com.infnet.aventureiros.entity.aventura.ParticipacaoMissao;
import com.infnet.aventureiros.entity.aventura.ParticipacaoMissaoId;
import com.infnet.aventureiros.entity.aventura.StatusMissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ParticipacaoMissaoRepository
        extends JpaRepository<ParticipacaoMissao, ParticipacaoMissaoId> {

    List<ParticipacaoMissao> findByMissaoId(Long missaoId);

    boolean existsByMissaoIdAndAventureiroId(Long missaoId, Long aventureiroId);

    /** Ranking de aventureiros por participações, recompensas e destaques. */
    @Query("""
        SELECT new com.infnet.aventureiros.dto.relatorio.RankingAventureiroDTO(
            p.aventureiro.id,
            p.aventureiro.nome,
            COUNT(p),
            COALESCE(SUM(p.recompensaOuro), 0),
            SUM(CASE WHEN p.mvp = true THEN 1 ELSE 0 END))
        FROM ParticipacaoMissao p
        WHERE p.aventureiro.organizacao.id = :orgId
          AND p.createdAt >= :dataInicio
          AND p.createdAt <= :dataFim
          AND (:statusMissao IS NULL OR p.missao.status = :statusMissao)
        GROUP BY p.aventureiro.id, p.aventureiro.nome
        ORDER BY COUNT(p) DESC, SUM(p.recompensaOuro) DESC
        """)
    List<RankingAventureiroDTO> ranking(
        @Param("orgId") Long orgId,
        @Param("dataInicio") OffsetDateTime dataInicio,
        @Param("dataFim") OffsetDateTime dataFim,
        @Param("statusMissao") StatusMissao statusMissao
    );

    /** Última participação de um aventureiro (para perfil completo). */
    @Query("""
        SELECT p FROM ParticipacaoMissao p
        WHERE p.aventureiro.id = :aventureiroId
        ORDER BY p.createdAt DESC
        """)
    List<ParticipacaoMissao> findUltimaParticipacao(
        @Param("aventureiroId") Long aventureiroId,
        org.springframework.data.domain.Pageable pageable
    );
}
