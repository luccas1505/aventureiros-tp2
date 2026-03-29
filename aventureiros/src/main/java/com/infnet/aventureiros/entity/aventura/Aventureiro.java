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

@Entity
@Table(
    name = "aventureiros",
    schema = "aventura",
    uniqueConstraints = {
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_aventureiro_organizacao"))
    private Organizacao organizacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_aventureiro_usuario"))
    private Usuario usuarioCadastro;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "classe", nullable = false, length = 50)
    private ClasseAventureiro classe;

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

    @OneToOne(mappedBy = "aventureiro", cascade = CascadeType.ALL,
              orphanRemoval = true, fetch = FetchType.LAZY)
    private Companheiro companheiro;

    @OneToMany(mappedBy = "aventureiro", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ParticipacaoMissao> participacoes = new ArrayList<>();

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
