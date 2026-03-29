package com.infnet.aventureiros.dto.relatorio;

import java.math.BigDecimal;


public record RankingAventureiroDTO(
    Long aventureiroId,
    String nome,
    Long totalParticipacoes,
    BigDecimal totalRecompensas,
    Long totalDestaques
) {}
