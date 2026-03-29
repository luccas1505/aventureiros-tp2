package com.infnet.aventureiros.repository;

import com.infnet.aventureiros.entity.AuditEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuditEntryRepository extends JpaRepository<AuditEntry, Long> {

    Page<AuditEntry> findByOrganizacaoIdOrderByOccurredAtDesc(
            Long organizacaoId, Pageable pageable);

    Page<AuditEntry> findByActorUserIdOrderByOccurredAtDesc(
            Long userId, Pageable pageable);

    Page<AuditEntry> findByOrganizacaoIdAndActionOrderByOccurredAtDesc(
            Long organizacaoId, String action, Pageable pageable);
}
