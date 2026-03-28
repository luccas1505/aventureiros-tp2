package com.infnet.aventureiros.entity.aventura;

import com.infnet.aventureiros.entity.Organizacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma Missão no schema aventura.
 *
 * Regras:
 *  - Pertence exclusivamente a uma organização
 *  - Apenas aventureiros da mesma organização podem participar
 *  - Status: PLANEJADA → EM_ANDAMENTO → CONCLUIDA | CANCELADA
 *  - Nível de perigo pertence a conjunto fixo (enum NivelPerigo)
 */
@Entity
@Table(name = "missoes", schema = "aventura")
@Getter
@Setter
@NoArgsConstructor
public class Missao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Organização à qual a missão pertence.
     * Referencia audit.organizacoes — cross-schema FK.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_missao_organizacao"))
    private Organizacao organizacao;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    /**
     * Nível de perigo da missão — valor fixo via enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_perigo", nullable = false, length = 20)
    private NivelPerigo nivelPerigo;

    /**
     * Status atual da missão.
     * Inicia como PLANEJADA automaticamente.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusMissao status = StatusMissao.PLANEJADA;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    /** Data de início — opcional. */
    @Column(name = "data_inicio", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime dataInicio;

    /** Data de término — opcional. */
    @Column(name = "data_termino", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime dataTermino;

    // ----------------------------------------------------------------
    // Relacionamentos
    // ----------------------------------------------------------------

    /**
     * Participações de aventureiros nesta missão.
     */
    @OneToMany(mappedBy = "missao", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ParticipacaoMissao> participacoes = new ArrayList<>();

    // ----------------------------------------------------------------
    // Helpers de negócio
    // ----------------------------------------------------------------

    /**
     * Verifica se a missão aceita novos participantes.
     * Só aceita quando PLANEJADA ou EM_ANDAMENTO.
     */
    public boolean aceitaParticipantes() {
        return status == StatusMissao.PLANEJADA
               || status == StatusMissao.EM_ANDAMENTO;
    }

    // ----------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        if (status == null) {
            status = StatusMissao.PLANEJADA;
        }
    }

    @Override
    public String toString() {
        return "Missao{id=" + id + ", titulo='" + titulo
               + "', nivelPerigo=" + nivelPerigo + ", status=" + status + "}";
    }
}
