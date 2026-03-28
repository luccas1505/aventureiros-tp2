package com.infnet.aventureiros.dto.relatorio;

import com.infnet.aventureiros.entity.aventura.NivelPerigo;
import com.infnet.aventureiros.entity.aventura.StatusMissao;

import java.math.BigDecimal;

/**
 * Relatório gerencial de missões com métricas agregadas.
 */
public record RelatorioMissaoDTO(
    Long missaoId,
    String titulo,
    StatusMissao status,
    NivelPerigo nivelPerigo,
    Long totalParticipantes,
    BigDecimal totalRecompensas
) {}
