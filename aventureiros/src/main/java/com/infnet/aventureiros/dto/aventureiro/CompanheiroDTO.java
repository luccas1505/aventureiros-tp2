package com.infnet.aventureiros.dto.aventureiro;

import com.infnet.aventureiros.entity.aventura.EspecieCompanheiro;

/**
 * Dados do companheiro de um aventureiro.
 */
public record CompanheiroDTO(
    String nome,
    EspecieCompanheiro especie,
    Integer indiceLealdade
) {}
