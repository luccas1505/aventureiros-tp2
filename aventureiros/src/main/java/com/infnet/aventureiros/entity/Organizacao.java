package com.infnet.aventureiros.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "organizacoes", schema = "audit")
@Getter
@Setter
@NoArgsConstructor
public class Organizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "nome", nullable = false, unique = true, length = 255)
    private String nome;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;


    @OneToMany(mappedBy = "organizacao", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Usuario> usuarios = new ArrayList<>();

    @OneToMany(mappedBy = "organizacao", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "organizacao", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = false)
    private List<ApiKey> apiKeys = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Organizacao{id=" + id + ", nome='" + nome + "', ativo=" + ativo + "}";
    }
}
