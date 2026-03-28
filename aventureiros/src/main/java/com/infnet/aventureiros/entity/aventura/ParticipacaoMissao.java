package com.infnet.aventureiros.entity.aventura;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Representa a participação de um Aventureiro em uma Missão.
 *
 * Regras:
 *  - Par (missao, aventureiro) é único — chave composta via @EmbeddedId
 *  - Aventureiro inativo não pode ser associado
 *  - A missão deve estar em estado compatível (PLANEJADA ou EM_ANDAMENTO)
 *  - Aventureiro e missão devem pertencer à mesma organização
 *  - Papel na missão pertence a conjunto fixo (enum PapelMissao)
 *  - Recompensa em ouro >= 0 (opcional)
 *  - MVP (destaque) é obrigatório, padrão false
 */
@Entity
@Table(name = "participacoes_missao", schema = "aventura")
@Getter
@Setter
@NoArgsConstructor
public class ParticipacaoMissao {

    /**
     * Chave composta (missao_id, aventureiro_id).
     * Garante unicidade do par no banco sem índice extra.
     */
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

    /**
     * Papel exercido pelo aventureiro nesta missão específica.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "papel", nullable = false, length = 30)
    private PapelMissao papel;

    /**
     * Recompensa em ouro recebida — opcional, mínimo 0.
     */
    @Column(name = "recompensa_ouro", precision = 10, scale = 2)
    private BigDecimal recompensaOuro;

    /**
     * Indica se o aventureiro foi destaque (MVP) na missão.
     */
    @Column(name = "mvp", nullable = false)
    private Boolean mvp = false;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    // ----------------------------------------------------------------
    // Factory method para garantir regras de negócio
    // ----------------------------------------------------------------

    /**
     * Cria uma participação já validando as regras de negócio.
     *
     * @throws IllegalStateException se o aventureiro estiver inativo
     *         ou a missão não aceitar novos participantes
     * @throws IllegalArgumentException se aventureiro e missão forem de
     *         organizações diferentes
     */
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

    // ----------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------
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
