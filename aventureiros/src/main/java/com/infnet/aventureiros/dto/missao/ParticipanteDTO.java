package com.infnet.aventureiros.dto.missao;

import com.infnet.aventureiros.entity.aventura.PapelMissao;

import java.math.BigDecimal;

/**
 * Dados de um participante dentro do detalhe de uma missão.
 */
public record ParticipanteDTO(
    Long aventureiroId,
    String aventureiroNome,
    PapelMissao papel,
    BigDecimal recompensaOuro,
    Boolean mvp
) {}
