package com.infnet.aventureiros.repository;

import com.infnet.aventureiros.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para audit.api_keys.
 */
@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    /** Busca por hash (para autenticação via API key). */
    Optional<ApiKey> findByKeyHashAndAtivoTrue(String keyHash);

    /** Lista as API keys ativas de uma organização. */
    List<ApiKey> findByOrganizacaoIdAndAtivoTrue(Long organizacaoId);
}
