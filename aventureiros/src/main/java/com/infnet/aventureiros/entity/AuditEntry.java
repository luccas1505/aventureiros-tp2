package com.infnet.aventureiros.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Mapeia a tabela audit.audit_entries.
 *
 * Esta tabela registra cada ação realizada no sistema.
 * O ator pode ser um usuário humano (actor_user_id) OU uma API key
 * (actor_api_key_id) — ambos são nullable, pois uma ação pode vir
 * de apenas um dos dois.
 *
 * Campos especiais:
 *  - diff: JSONB — diferença antes/depois da entidade modificada
 *  - metadata: JSONB — dados extras livres
 *  - ip: INET — endereço IP do solicitante
 */
@Entity
@Table(name = "audit_entries", schema = "audit")
@Getter
@Setter
@NoArgsConstructor
public class AuditEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // ----------------------------------------------------------------
    // Relacionamentos (todos FK no banco)
    // ----------------------------------------------------------------

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_audit_entries_organizacao"))
    private Organizacao organizacao;

    /**
     * Usuário que realizou a ação — nullable.
     * Nulo quando a ação foi disparada via API key.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_id", nullable = true,
                foreignKey = @ForeignKey(name = "fk_audit_entries_usuario"))
    private Usuario actorUser;

    /**
     * API key que realizou a ação — nullable.
     * Nulo quando a ação foi disparada por um usuário humano.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_api_key_id", nullable = true,
                foreignKey = @ForeignKey(name = "fk_audit_entries_api_key"))
    private ApiKey actorApiKey;

    // ----------------------------------------------------------------
    // Campos de auditoria
    // ----------------------------------------------------------------

    /** Ação realizada (ex: CREATE, UPDATE, DELETE, LOGIN). */
    @Column(name = "action", nullable = false, length = 100)
    private String action;

    /** Schema da entidade afetada (ex: "audit", "aventura"). */
    @Column(name = "entity_schema", length = 100)
    private String entitySchema;

    /** Nome da entidade/tabela afetada (ex: "usuarios", "aventureiros"). */
    @Column(name = "entity_name", length = 100)
    private String entityName;

    /** ID da entidade afetada (armazenado como VARCHAR para suportar qualquer tipo de PK). */
    @Column(name = "entity_id", length = 255)
    private String entityId;

    @Column(name = "occurred_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime occurredAt;

    /**
     * IP do solicitante — tipo INET no PostgreSQL.
     * Mapeado como String no JPA; o driver converte automaticamente.
     */
    @Column(name = "ip", columnDefinition = "INET")
    private String ip;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * Diferença JSON da entidade (antes → depois).
     * JSONB no banco — mapeado como String para manter compatibilidade.
     * Em projetos que usam Hibernate 6+, pode-se mapear como Map<String,Object>
     * com o tipo @JdbcTypeCode(SqlTypes.JSON).
     */
    @Column(name = "diff", columnDefinition = "JSONB")
    private String diff;

    /** Metadados extras em formato JSON livre. */
    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata;

    @Column(name = "success", nullable = false)
    private Boolean success = true;

    // ----------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------
    @PrePersist
    protected void onCreate() {
        if (occurredAt == null) {
            occurredAt = OffsetDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "AuditEntry{id=" + id + ", action='" + action
               + "', entityName='" + entityName + "', occurredAt=" + occurredAt + "}";
    }
}
