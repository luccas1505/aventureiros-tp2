package com.infnet.aventureiros.repository;

import com.infnet.aventureiros.entity.AuditEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório para audit.audit_entries.
 *
 * Suporta paginação, pois audit_entries pode crescer muito.
 */
@Repository
public interface AuditEntryRepository extends JpaRepository<AuditEntry, Long> {

    /** Histórico de ações de uma organização (paginado). */
    Page<AuditEntry> findByOrganizacaoIdOrderByOccurredAtDesc(
            Long organizacaoId, Pageable pageable);

    /** Histórico de ações de um usuário específico. */
    Page<AuditEntry> findByActorUserIdOrderByOccurredAtDesc(
            Long userId, Pageable pageable);

    /** Entradas de auditoria por tipo de ação. */
    Page<AuditEntry> findByOrganizacaoIdAndActionOrderByOccurredAtDesc(
            Long organizacaoId, String action, Pageable pageable);
}
