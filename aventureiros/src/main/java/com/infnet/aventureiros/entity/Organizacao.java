package com.infnet.aventureiros.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapeia a tabela audit.organizacoes.
 *
 * Regras do banco legado:
 *  - PK: BIGINT id (gerado pelo banco via sequence)
 *  - UK: nome
 *  - ativo: BOOLEAN
 *  - created_at: TIMESTAMPTZ
 */
@Entity
@Table(name = "organizacoes", schema = "audit")
@Getter
@Setter
@NoArgsConstructor
public class Organizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * UK constraint no banco: nome único por organização.
     * unique=true apenas documenta a constraint — não a cria (ddl-auto=validate).
     */
    @Column(name = "nome", nullable = false, unique = true, length = 255)
    private String nome;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    // ----------------------------------------------------------------
    // Relacionamentos (mapeados pelo lado "um")
    // ----------------------------------------------------------------

    /** Uma organização possui vários usuários. */
    @OneToMany(mappedBy = "organizacao", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Usuario> usuarios = new ArrayList<>();

    /** Uma organização possui vários roles. */
    @OneToMany(mappedBy = "organizacao", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Role> roles = new ArrayList<>();

    /** Uma organização possui várias API keys. */
    @OneToMany(mappedBy = "organizacao", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = false)
    private List<ApiKey> apiKeys = new ArrayList<>();

    // ----------------------------------------------------------------
    // Lifecycle: popula created_at antes de persistir
    // ----------------------------------------------------------------
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Organizacao{id=" + id + ", nome='" + nome + "', ativo=" + ativo + "}";
    }
}
