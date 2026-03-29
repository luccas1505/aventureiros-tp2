package com.infnet.aventureiros.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(
    name = "api_keys",
    schema = "audit",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_api_keys_org_nome",
                          columnNames = {"organizacao_id", "nome"})
    }
)
@Getter
@Setter
@NoArgsConstructor
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_api_keys_organizacao"))
    private Organizacao organizacao;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "key_hash", nullable = false, length = 255)
    private String keyHash;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @Column(name = "last_used_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime lastUsedAt;


    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "ApiKey{id=" + id + ", nome='" + nome + "', ativo=" + ativo + "}";
    }
}
