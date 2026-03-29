package com.infnet.aventureiros.dto.aventureiro;

import com.infnet.aventureiros.entity.aventura.ClasseAventureiro;

import java.time.OffsetDateTime;


public record AventureiroDetalheDTO(
    Long id,
    String nome,
    ClasseAventureiro classe,
    Integer nivel,
    Boolean ativo,
    OffsetDateTime createdAt,
    CompanheiroDTO companheiro,           // null se não tiver
    Long totalParticipacoes,
    String ultimaMissaoTitulo,            // null se nunca participou
    OffsetDateTime ultimaMissaoData       // null se nunca participou
) {}
