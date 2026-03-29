package com.infnet.aventureiros.entity.aventura;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "participacoes_missao", schema = "aventura")
@Getter
@Setter
@NoArgsConstructor
public class ParticipacaoMissao {

    @EmbeddedId
    private ParticipacaoMissaoId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("missaoId")
    @JoinColumn(name = "missao_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_participacao_missao"))
    private Missao missao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("aventureiroId")
    @JoinColumn(name = "aventureiro_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_participacao_aventureiro"))
    private Aventureiro aventureiro;

    @Enumerated(EnumType.STRING)
    @Column(name = "papel", nullable = false, length = 30)
    private PapelMissao papel;

    @Column(name = "recompensa_ouro", precision = 10, scale = 2)
    private BigDecimal recompensaOuro;


    @Column(name = "mvp", nullable = false)
    private Boolean mvp = false;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;


    public static ParticipacaoMissao criar(Missao missao, Aventureiro aventureiro,
                                           PapelMissao papel) {
        if (!aventureiro.getAtivo()) {
            throw new IllegalStateException(
                "Aventureiro inativo não pode ser associado a missões: "
                + aventureiro.getNome());
        }
        if (!missao.aceitaParticipantes()) {
            throw new IllegalStateException(
                "Missão não aceita novos participantes. Status: " + missao.getStatus());
        }
        if (!missao.getOrganizacao().getId()
                   .equals(aventureiro.getOrganizacao().getId())) {
            throw new IllegalArgumentException(
                "Aventureiro e missão pertencem a organizações diferentes.");
        }

        ParticipacaoMissao p = new ParticipacaoMissao();
        p.setId(new ParticipacaoMissaoId(missao.getId(), aventureiro.getId()));
        p.setMissao(missao);
        p.setAventureiro(aventureiro);
        p.setPapel(papel);
        p.setMvp(false);
        return p;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        if (mvp == null) {
            mvp = false;
        }
    }

    @Override
    public String toString() {
        return "ParticipacaoMissao{missaoId=" + (missao != null ? missao.getId() : null)
               + ", aventureiroId=" + (aventureiro != null ? aventureiro.getId() : null)
               + ", papel=" + papel + ", mvp=" + mvp + "}";
    }
}
