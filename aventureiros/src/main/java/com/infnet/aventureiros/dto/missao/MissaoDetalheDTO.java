package com.infnet.aventureiros.dto.missao;

import com.infnet.aventureiros.entity.aventura.NivelPerigo;
import com.infnet.aventureiros.entity.aventura.StatusMissao;

import java.time.OffsetDateTime;
import java.util.List;


public record MissaoDetalheDTO(
    Long id,
    String titulo,
    StatusMissao status,
    NivelPerigo nivelPerigo,
    OffsetDateTime createdAt,
    OffsetDateTime dataInicio,
    OffsetDateTime dataTermino,
    List<ParticipanteDTO> participantes  // lista vazia se não houver
) {}
