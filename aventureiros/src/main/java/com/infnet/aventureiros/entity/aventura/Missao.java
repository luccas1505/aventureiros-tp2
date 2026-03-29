package com.infnet.aventureiros.entity.aventura;

import com.infnet.aventureiros.entity.Organizacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


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


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_missao_organizacao"))
    private Organizacao organizacao;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;


    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_perigo", nullable = false, length = 20)
    private NivelPerigo nivelPerigo;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusMissao status = StatusMissao.PLANEJADA;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @Column(name = "data_inicio", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime dataInicio;

    @Column(name = "data_termino", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime dataTermino;

    @OneToMany(mappedBy = "missao", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ParticipacaoMissao> participacoes = new ArrayList<>();

    public boolean aceitaParticipantes() {
        return status == StatusMissao.PLANEJADA
               || status == StatusMissao.EM_ANDAMENTO;
    }

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
