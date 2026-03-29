package com.infnet.aventureiros.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;


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


    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios = new HashSet<>();


    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

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
