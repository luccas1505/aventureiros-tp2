package com.infnet.aventureiros.dto.aventureiro;

import com.infnet.aventureiros.entity.aventura.ClasseAventureiro;
import lombok.Getter;
import lombok.Setter;

/**
 * Filtros para listagem de aventureiros.
 */
@Getter
@Setter
public class AventureiroFiltroDTO {
    private Long organizacaoId;
    private Boolean ativo;
    private ClasseAventureiro classe;
    private Integer nivelMinimo;
    private String nome; // busca parcial
}
