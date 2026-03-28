package com.infnet.aventureiros.repository;

import com.infnet.aventureiros.entity.Organizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para audit.organizacoes.
 */
@Repository
public interface OrganizacaoRepository extends JpaRepository<Organizacao, Long> {

    /** Busca por nome (UK no banco). */
    Optional<Organizacao> findByNome(String nome);

    /** Busca apenas organizações ativas. */
    java.util.List<Organizacao> findByAtivoTrue();
}
