package com.infnet.aventureiros.dto.relatorio;

import com.infnet.aventureiros.entity.aventura.NivelPerigo;
import com.infnet.aventureiros.entity.aventura.StatusMissao;

import java.math.BigDecimal;

public record RelatorioMissaoDTO(
        Long missaoId,
        String titulo,
        StatusMissao status,
        NivelPerigo nivelPerigo,
        Long totalParticipantes,
        BigDecimal totalRecompensas
) {
    public RelatorioMissaoDTO(Long missaoId, String titulo, StatusMissao status,
                              NivelPerigo nivelPerigo, Long totalParticipantes,
                              Number totalRecompensas) {
        this(missaoId, titulo, status, nivelPerigo, totalParticipantes,
                totalRecompensas == null ? BigDecimal.ZERO
                        : new BigDecimal(totalRecompensas.toString()));
    }
}