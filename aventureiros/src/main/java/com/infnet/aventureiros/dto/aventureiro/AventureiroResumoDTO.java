package com.infnet.aventureiros.dto.aventureiro;

import com.infnet.aventureiros.entity.aventura.ClasseAventureiro;

/**
 * Dados resumidos de um aventureiro para listagens.
 */
public record AventureiroResumoDTO(
    Long id,
    String nome,
    ClasseAventureiro classe,
    Integer nivel,
    Boolean ativo
) {}
