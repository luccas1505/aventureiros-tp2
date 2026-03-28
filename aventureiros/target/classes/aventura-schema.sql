-- ============================================================
-- Schema aventura — criação das tabelas do domínio de aventura
-- Execute este script UMA VEZ no banco Docker antes de rodar
-- a aplicação com ddl-auto=validate
-- ============================================================

CREATE SCHEMA IF NOT EXISTS aventura;

-- Aventureiros
CREATE TABLE IF NOT EXISTS aventura.aventureiros (
    id              BIGSERIAL PRIMARY KEY,
    organizacao_id  BIGINT NOT NULL REFERENCES audit.organizacoes(id),
    usuario_id      BIGINT NOT NULL REFERENCES audit.usuarios(id),
    nome            VARCHAR(120) NOT NULL,
    classe          VARCHAR(50)  NOT NULL,
    nivel           INTEGER      NOT NULL DEFAULT 1 CHECK (nivel >= 1),
    ativo           BOOLEAN      NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_aventureiro_org_nome UNIQUE (organizacao_id, nome)
);

-- Companheiros (1:1 com aventureiros — PK = FK)
CREATE TABLE IF NOT EXISTS aventura.companheiros (
    aventureiro_id  BIGINT PRIMARY KEY REFERENCES aventura.aventureiros(id) ON DELETE CASCADE,
    nome            VARCHAR(120) NOT NULL,
    especie         VARCHAR(50)  NOT NULL,
    indice_lealdade INTEGER      NOT NULL DEFAULT 50 CHECK (indice_lealdade BETWEEN 0 AND 100)
);

-- Missões
CREATE TABLE IF NOT EXISTS aventura.missoes (
    id              BIGSERIAL PRIMARY KEY,
    organizacao_id  BIGINT      NOT NULL REFERENCES audit.organizacoes(id),
    titulo          VARCHAR(150) NOT NULL,
    nivel_perigo    VARCHAR(20)  NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PLANEJADA',
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    data_inicio     TIMESTAMPTZ,
    data_termino    TIMESTAMPTZ
);

-- Participações em missão (chave composta)
CREATE TABLE IF NOT EXISTS aventura.participacoes_missao (
    missao_id       BIGINT      NOT NULL REFERENCES aventura.missoes(id) ON DELETE CASCADE,
    aventureiro_id  BIGINT      NOT NULL REFERENCES aventura.aventureiros(id) ON DELETE CASCADE,
    papel           VARCHAR(30)  NOT NULL,
    recompensa_ouro NUMERIC(10,2) CHECK (recompensa_ouro >= 0),
    mvp             BOOLEAN      NOT NULL DEFAULT false,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    PRIMARY KEY (missao_id, aventureiro_id)
);

-- Índices úteis
CREATE INDEX IF NOT EXISTS idx_aventureiros_org   ON aventura.aventureiros(organizacao_id);
CREATE INDEX IF NOT EXISTS idx_missoes_org         ON aventura.missoes(organizacao_id);
CREATE INDEX IF NOT EXISTS idx_participacoes_missao ON aventura.participacoes_missao(missao_id);
CREATE INDEX IF NOT EXISTS idx_participacoes_av     ON aventura.participacoes_missao(aventureiro_id);
