package com.infnet.aventureiros.dto.missao;

import com.infnet.aventureiros.entity.aventura.NivelPerigo;
import com.infnet.aventureiros.entity.aventura.StatusMissao;

import java.time.OffsetDateTime;


public record MissaoResumoDTO(
    Long id,
    String titulo,
    StatusMissao status,
    NivelPerigo nivelPerigo,
    OffsetDateTime createdAt,
    OffsetDateTime dataInicio,
    OffsetDateTime dataTermino
) {}
