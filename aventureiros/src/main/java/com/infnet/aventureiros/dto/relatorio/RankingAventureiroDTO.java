package com.infnet.aventureiros.dto.relatorio;

import java.math.BigDecimal;

/**
 * Item do ranking de aventureiros.
 */
public record RankingAventureiroDTO(
    Long aventureiroId,
    String nome,
    Long totalParticipacoes,
    BigDecimal totalRecompensas,
    Long totalDestaques
) {}
