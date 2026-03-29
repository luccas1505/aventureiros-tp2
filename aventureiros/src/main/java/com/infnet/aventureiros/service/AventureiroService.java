package com.infnet.aventureiros.service;

import com.infnet.aventureiros.dto.aventureiro.*;
import com.infnet.aventureiros.entity.aventura.Aventureiro;
import com.infnet.aventureiros.entity.aventura.Companheiro;
import com.infnet.aventureiros.entity.aventura.ParticipacaoMissao;
import com.infnet.aventureiros.repository.AventureiroRepository;
import com.infnet.aventureiros.repository.ParticipacaoMissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AventureiroService {

    private final AventureiroRepository aventureiroRepo;
    private final ParticipacaoMissaoRepository participacaoRepo;

    public Page<AventureiroResumoDTO> listar(AventureiroFiltroDTO filtro, Pageable pageable) {
        return aventureiroRepo.listarComFiltros(
            filtro.getOrganizacaoId(),
            filtro.getAtivo(),
            filtro.getClasse(),
            filtro.getNivelMinimo(),
            pageable
        );
    }

    public Page<AventureiroResumoDTO> buscarPorNome(Long orgId, String nome, Pageable pageable) {
        return aventureiroRepo.buscarPorNome(orgId, nome, pageable);
    }

    public AventureiroDetalheDTO detalhe(Long id) {
        Aventureiro av = aventureiroRepo.findByIdWithCompanheiro(id)
            .orElseThrow(() -> new NoSuchElementException("Aventureiro não encontrado: " + id));

        CompanheiroDTO companheiroDTO = null;
        Companheiro comp = av.getCompanheiro();
        if (comp != null) {
            companheiroDTO = new CompanheiroDTO(
                comp.getNome(), comp.getEspecie(), comp.getIndiceLealdade());
        }

        Long total = aventureiroRepo.countParticipacoes(id);

        String ultimaTitulo = null;
        java.time.OffsetDateTime ultimaData = null;
        List<ParticipacaoMissao> ultima =
            participacaoRepo.findUltimaParticipacao(id, PageRequest.of(0, 1));
        if (!ultima.isEmpty()) {
            ultimaTitulo = ultima.get(0).getMissao().getTitulo();
            ultimaData   = ultima.get(0).getCreatedAt();
        }

        return new AventureiroDetalheDTO(
            av.getId(), av.getNome(), av.getClasse(), av.getNivel(), av.getAtivo(),
            av.getCreatedAt(), companheiroDTO, total, ultimaTitulo, ultimaData
        );
    }
}
