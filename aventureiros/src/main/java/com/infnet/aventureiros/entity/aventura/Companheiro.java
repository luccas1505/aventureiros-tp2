package com.infnet.aventureiros.entity.aventura;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "companheiros", schema = "aventura")
@Getter
@Setter
@NoArgsConstructor
public class Companheiro {

    @Id
    @Column(name = "aventureiro_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "aventureiro_id",
                foreignKey = @ForeignKey(name = "fk_companheiro_aventureiro"))
    private Aventureiro aventureiro;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "especie", nullable = false, length = 50)
    private EspecieCompanheiro especie;

    @Column(name = "indice_lealdade", nullable = false)
    private Integer indiceLealdade = 50;

    @Override
    public String toString() {
        return "Companheiro{nome='" + nome + "', especie=" + especie
               + ", lealdade=" + indiceLealdade + "}";
    }
}
