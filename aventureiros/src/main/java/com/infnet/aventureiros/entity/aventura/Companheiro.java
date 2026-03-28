package com.infnet.aventureiros.entity.aventura;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Companheiro de um Aventureiro — relação 1:1 exclusiva.
 *
 * Regras:
 *  - Não pode existir sem um aventureiro (PK = FK para aventureiro)
 *  - Não pode ser compartilhado entre aventureiros
 *  - Removido automaticamente quando o aventureiro é removido
 *  - Índice de lealdade: 0 a 100
 *  - Espécie pertence a conjunto fixo (enum EspecieCompanheiro)
 *
 * Estratégia de PK: usa o mesmo ID do aventureiro (@MapsId).
 * Isso garante que um companheiro não existe sem aventureiro
 * e que a relação é verdadeiramente 1:1 no banco.
 */
@Entity
@Table(name = "companheiros", schema = "aventura")
@Getter
@Setter
@NoArgsConstructor
public class Companheiro {

    /**
     * PK compartilhada com o aventureiro via @MapsId.
     * O banco terá uma coluna aventureiro_id que é PK e FK ao mesmo tempo.
     */
    @Id
    @Column(name = "aventureiro_id", nullable = false)
    private Long id;

    /**
     * Relação 1:1 com o aventureiro.
     * @MapsId faz o id deste companheiro ser o mesmo que o do aventureiro.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "aventureiro_id",
                foreignKey = @ForeignKey(name = "fk_companheiro_aventureiro"))
    private Aventureiro aventureiro;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    /**
     * Espécie do companheiro — valor fixo via enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "especie", nullable = false, length = 50)
    private EspecieCompanheiro especie;

    /**
     * Índice de lealdade: 0 a 100.
     * Constraints de CHECK no banco garantem o intervalo.
     */
    @Column(name = "indice_lealdade", nullable = false)
    private Integer indiceLealdade = 50;

    @Override
    public String toString() {
        return "Companheiro{nome='" + nome + "', especie=" + especie
               + ", lealdade=" + indiceLealdade + "}";
    }
}
