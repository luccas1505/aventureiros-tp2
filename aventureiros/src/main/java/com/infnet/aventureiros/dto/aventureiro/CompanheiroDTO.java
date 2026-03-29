package com.infnet.aventureiros.dto.aventureiro;

import com.infnet.aventureiros.entity.aventura.EspecieCompanheiro;

public record CompanheiroDTO(
    String nome,
    EspecieCompanheiro especie,
    Integer indiceLealdade
) {}
