package com.infnet.aventureiros.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Mapeia a tabela audit.permissions.
 *
 * Regras do banco legado:
 *  - PK: BIGINT id
 *  - UK: code (global, independente de organização)
 *  - descricao: VARCHAR
 *
 * Nota: Permissions são globais no sistema — não pertencem a
 * uma organização específica. São associadas a roles via N:N.
 */
@Entity
@Table(
    name = "permissions",
    schema = "audit"
)
@Getter
@Setter
@NoArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Código único da permissão (ex: "AVENTUREIRO_CREATE", "GUILD_MANAGE").
     * UK no banco.
     */
    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "descricao", length = 500)
    private String descricao;

    // ----------------------------------------------------------------
    // Lado inverso do N:N com Role
    // ----------------------------------------------------------------

    /**
     * mappedBy aponta para o campo "permissions" em Role,
     * que é o lado proprietário da relação (dono da join table).
     */
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    @Override
    public String toString() {
        return "Permission{id=" + id + ", code='" + code + "'}";
    }
}
