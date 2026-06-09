DROP TABLE IF EXISTS mensagem CASCADE;
DROP TABLE IF EXISTS avaliacao CASCADE;
DROP TABLE IF EXISTS qualificacao CASCADE;
DROP TABLE IF EXISTS amizade CASCADE;
DROP TABLE IF EXISTS skill CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;

-- =========================================
-- USUARIO
-- =========================================

CREATE TABLE usuario (

    id_usuario SERIAL PRIMARY KEY,

    email VARCHAR(100)
        NOT NULL
        UNIQUE,

    senha VARCHAR(255)
        NOT NULL,

    bio VARCHAR(1000),

    nome VARCHAR(60)
        NOT NULL,

    nota NUMERIC(3,2),

    num_avaliacoes INT
        NOT NULL
        DEFAULT 0
);

-- =========================================
-- SKILL
-- =========================================

CREATE TABLE skill (

    id_skill SERIAL PRIMARY KEY,

    name VARCHAR(50)
        NOT NULL
        UNIQUE
);

-- =========================================
-- AMIZADE
-- STATUS:
-- 0 = pendente
-- 1 = aceita
-- 2 = recusada
-- 3 = bloqueado
-- =========================================

CREATE TABLE amizade (

    id_amizade SERIAL PRIMARY KEY,

    status INT
        NOT NULL,

    usuario1 INT
        NOT NULL,

    usuario2 INT
        NOT NULL,

    CONSTRAINT fk_amizade_usuario1
        FOREIGN KEY (usuario1)
        REFERENCES usuario(id_usuario),

    CONSTRAINT fk_amizade_usuario2
        FOREIGN KEY (usuario2)
        REFERENCES usuario(id_usuario),

    CONSTRAINT unique_amizade
        UNIQUE (usuario1, usuario2)
);

-- =========================================
-- AVALIACAO
-- =========================================

CREATE TABLE avaliacao (

    id_avaliacao VARCHAR(45)
        PRIMARY KEY,

    avaliado INT
        NOT NULL,

    avaliador INT
        NOT NULL,

    nota FLOAT
        NOT NULL
        CHECK (nota >= 0 AND nota <= 5),

    data_hora TIMESTAMP
        NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_avaliacao_avaliado
        FOREIGN KEY (avaliado)
        REFERENCES usuario(id_usuario),

    CONSTRAINT fk_avaliacao_avaliador
        FOREIGN KEY (avaliador)
        REFERENCES usuario(id_usuario),

    CONSTRAINT unique_avaliacao
        UNIQUE (avaliador, avaliado)
);

-- =========================================
-- MENSAGEM
-- TIPO:
-- 1 = texto
-- 2 = imagem
-- 3 = áudio
-- 4 = vídeo
-- 5 = chamada
-- =========================================

CREATE TABLE mensagem (

    id_mensagem SERIAL PRIMARY KEY,

    tipo INT
        NOT NULL,

    conteudo VARCHAR(1000),

    data_hora TIMESTAMP
        NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    remetente INT
        NOT NULL,

    amizade INT
        NOT NULL,

    CONSTRAINT fk_mensagem_remetente
        FOREIGN KEY (remetente)
        REFERENCES usuario(id_usuario),

    CONSTRAINT fk_mensagem_amizade
        FOREIGN KEY (amizade)
        REFERENCES amizade(id_amizade)
);

-- =========================================
-- QUALIFICACAO
-- =========================================

CREATE TABLE qualificacao (

    qualificado INT
        NOT NULL,

    skill INT
        NOT NULL,

    PRIMARY KEY (qualificado, skill),

    CONSTRAINT fk_qualificacao_usuario
        FOREIGN KEY (qualificado)
        REFERENCES usuario(id_usuario),

    CONSTRAINT fk_qualificacao_skill
        FOREIGN KEY (skill)
        REFERENCES skill(id_skill)
);
