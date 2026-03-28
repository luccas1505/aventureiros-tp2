package com.infnet.aventureiros.entity.aventura;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Chave composta para ParticipacaoMissao.
 *
 * Garante unicidade do par (missao_id, aventureiro_id) no banco,
 * impedindo que um aventureiro participe mais de uma vez da mesma missão.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ParticipacaoMissaoId implements Serializable {

    @Column(name = "missao_id", nullable = false)
    private Long missaoId;

    @Column(name = "aventureiro_id", nullable = false)
    private Long aventureiroId;

    public ParticipacaoMissaoId(Long missaoId, Long aventureiroId) {
        this.missaoId = missaoId;
        this.aventureiroId = aventureiroId;
    }
}
