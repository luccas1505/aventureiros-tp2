package com.infnet.aventureiros.service;

import com.infnet.aventureiros.dto.missao.*;
import com.infnet.aventureiros.dto.relatorio.RankingAventureiroDTO;
import com.infnet.aventureiros.dto.relatorio.RelatorioMissaoDTO;
import com.infnet.aventureiros.entity.aventura.Missao;
import com.infnet.aventureiros.entity.aventura.NivelPerigo;
import com.infnet.aventureiros.entity.aventura.StatusMissao;
import com.infnet.aventureiros.repository.MissaoRepository;
import com.infnet.aventureiros.repository.ParticipacaoMissaoRepository;
import com.infnet.aventureiros.spec.MissaoSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissaoService {

    private final MissaoRepository missaoRepo;
    private final ParticipacaoMissaoRepository participacaoRepo;

    /** Listagem com filtros dinâmicos via Specification — sem problema de tipo null com enums. */
    public Page<MissaoResumoDTO> listar(MissaoFiltroDTO filtro, Pageable pageable) {
        return missaoRepo.findAll(
            MissaoSpec.comFiltros(
                filtro.getOrganizacaoId(),
                filtro.getStatus(),
                filtro.getNivelPerigo(),
                filtro.getDataInicio(),
                filtro.getDataFim()
            ), pageable
        ).map(m -> new MissaoResumoDTO(
            m.getId(), m.getTitulo(), m.getStatus(), m.getNivelPerigo(),
            m.getCreatedAt(), m.getDataInicio(), m.getDataTermino()
        ));
    }

    /** Detalhamento completo com participantes — lista vazia se não houver. */
    public MissaoDetalheDTO detalhe(Long id) {
        Missao missao = missaoRepo.findByIdWithParticipacoes(id)
            .orElseThrow(() -> new NoSuchElementException("Missão não encontrada: " + id));

        List<ParticipanteDTO> participantes = missao.getParticipacoes().stream()
            .map(p -> new ParticipanteDTO(
                p.getAventureiro().getId(),
                p.getAventureiro().getNome(),
                p.getPapel(),
                p.getRecompensaOuro(),
                p.getMvp()
            )).toList();

        return new MissaoDetalheDTO(
            missao.getId(), missao.getTitulo(), missao.getStatus(),
            missao.getNivelPerigo(), missao.getCreatedAt(),
            missao.getDataInicio(), missao.getDataTermino(),
            participantes
        );
    }

    public List<RankingAventureiroDTO> ranking(Long orgId, OffsetDateTime dataInicio,
                                               OffsetDateTime dataFim, StatusMissao statusMissao) {
        return participacaoRepo.ranking(orgId, dataInicio, dataFim, statusMissao);
    }

    public List<RelatorioMissaoDTO> relatorio(Long orgId, OffsetDateTime dataInicio,
                                              OffsetDateTime dataFim) {
        return missaoRepo.relatorioMissoes(orgId, dataInicio, dataFim);
    }
}
