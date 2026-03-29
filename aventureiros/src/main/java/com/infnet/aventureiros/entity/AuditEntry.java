package com.infnet.aventureiros.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

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


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_audit_entries_organizacao"))
    private Organizacao organizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_id", nullable = true,
                foreignKey = @ForeignKey(name = "fk_audit_entries_usuario"))
    private Usuario actorUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_api_key_id", nullable = true,
                foreignKey = @ForeignKey(name = "fk_audit_entries_api_key"))
    private ApiKey actorApiKey;


    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "entity_schema", length = 100)
    private String entitySchema;

    @Column(name = "entity_name", length = 100)
    private String entityName;

    @Column(name = "entity_id", length = 255)
    private String entityId;

    @Column(name = "occurred_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime occurredAt;

    @Column(name = "ip", columnDefinition = "INET")
    private String ip;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "diff", columnDefinition = "JSONB")
    private String diff;

    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata;

    @Column(name = "success", nullable = false)
    private Boolean success = true;

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
