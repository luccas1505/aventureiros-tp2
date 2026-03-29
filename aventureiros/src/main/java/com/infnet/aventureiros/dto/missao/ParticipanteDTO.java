package com.infnet.aventureiros.dto.missao;

import com.infnet.aventureiros.entity.aventura.PapelMissao;

import java.math.BigDecimal;

public record ParticipanteDTO(
    Long aventureiroId,
    String aventureiroNome,
    PapelMissao papel,
    BigDecimal recompensaOuro,
    Boolean mvp
) {}
