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
    name = "usuarios",
    schema = "audit",
    uniqueConstraints = {
        // Documenta a UK composta (org + email) sem criar via DDL
        @UniqueConstraint(name = "uq_usuarios_org_email",
                          columnNames = {"organizacao_id", "email"})
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_usuarios_organizacao"))
    private Organizacao organizacao;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "email", nullable = false, length = 255)
    private String email;


    @Column(name = "senha_hash", length = 255)
    private String senhaHash;


    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "ultimo_login_em", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime ultimoLoginEm;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        schema = "audit",
        joinColumns = @JoinColumn(
            name = "usuario_id",
            foreignKey = @ForeignKey(name = "fk_usuario_roles_usuario")
        ),
        inverseJoinColumns = @JoinColumn(
            name = "role_id",
            foreignKey = @ForeignKey(name = "fk_usuario_roles_role")
        )
    )
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsuarios().add(this);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getUsuarios().remove(this);
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        updatedAt = OffsetDateTime.now();
        if (status == null) {
            status = "ATIVO";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome
               + "', email='" + email + "', status='" + status + "'}";
    }
}
