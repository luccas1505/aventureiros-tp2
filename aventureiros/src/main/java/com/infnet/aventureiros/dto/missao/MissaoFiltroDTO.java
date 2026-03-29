package com.infnet.aventureiros.dto.missao;

import com.infnet.aventureiros.entity.aventura.NivelPerigo;
import com.infnet.aventureiros.entity.aventura.StatusMissao;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class MissaoFiltroDTO {
    private Long organizacaoId;
    private StatusMissao status;
    private NivelPerigo nivelPerigo;
    private OffsetDateTime dataInicio;
    private OffsetDateTime dataFim;
}
