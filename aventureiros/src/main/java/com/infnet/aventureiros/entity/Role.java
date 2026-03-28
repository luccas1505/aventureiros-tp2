package com.infnet.aventureiros.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Mapeia a tabela audit.roles.
 *
 * Regras do banco legado:
 *  - PK: BIGINT id
 *  - FK: organizacao_id → audit.organizacoes(id)
 *  - UK composta: (organizacao_id, nome)
 *  - Relacionamento N:N com audit.permissions via tabela de junção
 *  - Relacionamento N:N com audit.usuarios (lado inverso)
 */
@Entity
@Table(
    name = "roles",
    schema = "audit",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_roles_org_nome",
                          columnNames = {"organizacao_id", "nome"})
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_roles_organizacao"))
    private Organizacao organizacao;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    // ----------------------------------------------------------------
    // Relacionamento N:N com Permission
    // ----------------------------------------------------------------

    /**
     * Relacionamento muitos-para-muitos com permissões.
     * Tabela de junção: audit.role_permissions
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permissions",
        schema = "audit",
        joinColumns = @JoinColumn(
            name = "role_id",
            foreignKey = @ForeignKey(name = "fk_role_permissions_role")
        ),
        inverseJoinColumns = @JoinColumn(
            name = "permission_id",
            foreignKey = @ForeignKey(name = "fk_role_permissions_permission")
        )
    )
    private Set<Permission> permissions = new HashSet<>();

    // ----------------------------------------------------------------
    // Lado inverso do N:N com Usuario
    // ----------------------------------------------------------------

    /**
     * mappedBy indica que Usuario é o lado proprietário
     * (dono da tabela de junção usuario_roles).
     */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios = new HashSet<>();

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    // ----------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Role{id=" + id + ", nome='" + nome + "'}";
    }
}
