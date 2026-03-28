package com.infnet.aventureiros.entity.aventura;

import com.infnet.aventureiros.entity.Organizacao;
import com.infnet.aventureiros.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um Aventureiro no schema aventura.
 *
 * Regras:
 *  - Pertence exclusivamente a uma organização (não pode ser nula)
 *  - Referencia o usuário responsável pelo cadastro
 *  - Classe pertence a conjunto fixo (enum ClasseAventureiro)
 *  - Nível mínimo: 1
 *  - Aventureiro inativo não pode ser associado a novas missões
 *  - Companheiro é removido automaticamente junto com o aventureiro (orphanRemoval)
 */
@Entity
@Table(
    name = "aventureiros",
    schema = "aventura",
    uniqueConstraints = {
        // Um aventureiro é único por organização + nome (regra de negócio)
        @UniqueConstraint(name = "uq_aventureiro_org_nome",
                          columnNames = {"organizacao_id", "nome"})
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Aventureiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Organização à qual o aventureiro pertence.
     * Referencia audit.organizacoes — cross-schema FK.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_aventureiro_organizacao"))
    private Organizacao organizacao;

    /**
     * Usuário que cadastrou o aventureiro.
     * Referencia audit.usuarios — cross-schema FK.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_aventureiro_usuario"))
    private Usuario usuarioCadastro;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    /**
     * Classe do aventureiro — valor fixo via enum.
     * Armazenado como String no banco (EnumType.STRING) para legibilidade.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "classe", nullable = false, length = 50)
    private ClasseAventureiro classe;

    /**
     * Nível do aventureiro — mínimo 1.
     * A constraint de CHECK no banco garante nivel >= 1.
     */
    @Column(name = "nivel", nullable = false)
    private Integer nivel = 1;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;

    // ----------------------------------------------------------------
    // Relacionamentos
    // ----------------------------------------------------------------

    /**
     * Companheiro do aventureiro — relação 1:1.
     * orphanRemoval=true garante remoção automática do companheiro
     * quando o aventureiro for removido.
     */
    @OneToOne(mappedBy = "aventureiro", cascade = CascadeType.ALL,
              orphanRemoval = true, fetch = FetchType.LAZY)
    private Companheiro companheiro;

    /**
     * Participações do aventureiro em missões.
     */
    @OneToMany(mappedBy = "aventureiro", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ParticipacaoMissao> participacoes = new ArrayList<>();

    // ----------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        if (nivel == null || nivel < 1) {
            nivel = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    @Override
    public String toString() {
        return "Aventureiro{id=" + id + ", nome='" + nome
               + "', classe=" + classe + ", nivel=" + nivel + ", ativo=" + ativo + "}";
    }
}
