package com.infnet.aventureiros.repository;

import com.infnet.aventureiros.entity.Organizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizacaoRepository extends JpaRepository<Organizacao, Long> {

    Optional<Organizacao> findByNome(String nome);

    java.util.List<Organizacao> findByAtivoTrue();
}
