package com.infnet.aventureiros.spec;

import com.infnet.aventureiros.entity.aventura.Missao;
import com.infnet.aventureiros.entity.aventura.NivelPerigo;
import com.infnet.aventureiros.entity.aventura.StatusMissao;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification para filtros dinâmicos de Missão.
 * Evita o problema de inferência de tipo NULL do PostgreSQL com enums,
 * pois só adiciona predicados para parâmetros não nulos.
 */
public class MissaoSpec {

    public static Specification<Missao> comFiltros(
            Long orgId,
            StatusMissao status,
            NivelPerigo nivelPerigo,
            OffsetDateTime dataInicio,
            OffsetDateTime dataFim) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("organizacao").get("id"), orgId));

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (nivelPerigo != null) {
                predicates.add(cb.equal(root.get("nivelPerigo"), nivelPerigo));
            }
            if (dataInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), dataInicio));
            }
            if (dataFim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), dataFim));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
