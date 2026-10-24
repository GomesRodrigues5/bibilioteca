-- =============================================================================
-- MIGRAÇÃO IDEMPOTENTE PARA BIBLIOTECADEDBA
-- Base de referência: BibliotecaDBA_NOVO.sql
-- Objetivo: aplicar alterações novas sem apagar dados existentes
-- Compatível com MySQL 8.0+
-- =============================================================================

CREATE DATABASE IF NOT EXISTS BibliotecaDBA CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE BibliotecaDBA;

ALTER DATABASE BibliotecaDBA CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================================================
-- 1) TABELAS PRINCIPAIS QUE PODEM FALTAR
-- ============================================================================

CREATE TABLE IF NOT EXISTS Departamento (
    id_departamento INT(11) AUTO_INCREMENT PRIMARY KEY,
    departamento VARCHAR(100) NOT NULL,
    UNIQUE KEY uk_departamento (departamento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Curso (
    id_curso INT(11) AUTO_INCREMENT PRIMARY KEY,
    curso VARCHAR(100) NOT NULL,
    id_departamento INT(11),
    UNIQUE KEY uk_curso (curso),
    KEY idx_departamento (id_departamento),
    CONSTRAINT fk_curso_departamento FOREIGN KEY (id_departamento)
        REFERENCES Departamento(id_departamento) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Endereco (
    id_endereco INT(11) AUTO_INCREMENT PRIMARY KEY,
    municipio VARCHAR(100),
    bairro VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Contacto (
    id_contacto INT(11) AUTO_INCREMENT PRIMARY KEY,
    telefone1 VARCHAR(15) NOT NULL,
    telefone2 VARCHAR(15)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Funcao (
    id_funcao INT(11) AUTO_INCREMENT PRIMARY KEY,
    funcao VARCHAR(50) NOT NULL,
    descricao TEXT,
    UNIQUE KEY uk_funcao (funcao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Funcionario (
    id_funcionario INT(11) AUTO_INCREMENT PRIMARY KEY,
    funcionario VARCHAR(100) NOT NULL,
    id_funcao INT(11),
    id_departamento INT(11),
    id_contacto INT(11),
    Func ENUM('Func','Admin') NOT NULL DEFAULT 'Func',
    senha VARCHAR(255) NOT NULL,
    UNIQUE KEY uk_funcionario_usuario (funcionario),
    KEY idx_funcao (id_funcao),
    KEY idx_departamento (id_departamento),
    KEY idx_contacto (id_contacto),
    CONSTRAINT fk_funcionario_funcao FOREIGN KEY (id_funcao)
        REFERENCES Funcao(id_funcao) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_funcionario_departamento FOREIGN KEY (id_departamento)
        REFERENCES Departamento(id_departamento) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_funcionario_contacto FOREIGN KEY (id_contacto)
        REFERENCES Contacto(id_contacto) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Estudante (
    id_estudante INT(11) AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    numero_estudante VARCHAR(20) NOT NULL,
    id_curso INT(11),
    id_contacto INT(11),
    email VARCHAR(100),
    id_endereco INT(11),
    UNIQUE KEY uk_numero_estudante (numero_estudante),
    KEY idx_curso (id_curso),
    KEY idx_contacto (id_contacto),
    KEY idx_endereco (id_endereco),
    CONSTRAINT fk_estudante_curso FOREIGN KEY (id_curso)
        REFERENCES Curso(id_curso) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_estudante_contacto FOREIGN KEY (id_contacto)
        REFERENCES Contacto(id_contacto) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_estudante_endereco FOREIGN KEY (id_endereco)
        REFERENCES Endereco(id_endereco) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Armario (
    id_armario INT(11) AUTO_INCREMENT PRIMARY KEY,
    armario VARCHAR(50) NOT NULL,
    Partileira VARCHAR(50) NOT NULL,
    UNIQUE KEY uk_armario (armario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Computador (
    id_computador INT(11) AUTO_INCREMENT PRIMARY KEY,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    numero VARCHAR(20) NOT NULL,
    em_uso BOOLEAN DEFAULT FALSE,
    UNIQUE KEY uk_numero_computador (numero)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS AgendaComputador (
    id_agenda INT(11) AUTO_INCREMENT PRIMARY KEY,
    id_estudante INT(11) NOT NULL,
    id_computador INT(11) NOT NULL,
    data_agendada DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    status ENUM('Agendado', 'Utilizado', 'Cancelado', 'Nao Utilizado') DEFAULT 'Agendado',
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_estudante (id_estudante),
    KEY idx_computador (id_computador),
    KEY idx_data (data_agendada),
    CONSTRAINT fk_agendamento_estudante FOREIGN KEY (id_estudante)
        REFERENCES Estudante(id_estudante) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_agendamento_computador FOREIGN KEY (id_computador)
        REFERENCES Computador(id_computador) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Atendimento (
    id_atendimento INT(11) AUTO_INCREMENT PRIMARY KEY,
    id_estudante INT(11),
    id_computador INT(11),
    data_entrada DATE NOT NULL DEFAULT (CURRENT_DATE),
    hora_entrada TIME NOT NULL DEFAULT (CURRENT_TIME),
    hora_saida TIME,
    tempo_limite_minutos INT(11) DEFAULT 60,
    status ENUM('Em Uso', 'Finalizado') DEFAULT 'Em Uso',
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    KEY idx_estudante (id_estudante),
    KEY idx_computador (id_computador),
    KEY idx_data (data_entrada),
    CONSTRAINT fk_atendimento_estudante FOREIGN KEY (id_estudante)
        REFERENCES Estudante(id_estudante) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_atendimento_computador FOREIGN KEY (id_computador)
        REFERENCES Computador(id_computador) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS CDU (
    id_cdu INT(11) AUTO_INCREMENT PRIMARY KEY,
    codigo_cdu VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(500) NOT NULL,
    classe_pai_id INT(11),
    tipo_tabela VARCHAR(50) DEFAULT 'principal',
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_codigo (codigo_cdu),
    KEY idx_classe_pai (classe_pai_id),
    CONSTRAINT fk_cdu_pai FOREIGN KEY (classe_pai_id)
        REFERENCES CDU(id_cdu) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Livro (
    id_livro INT(11) AUTO_INCREMENT PRIMARY KEY,
    autor VARCHAR(200) NOT NULL,
    titulo VARCHAR(300) NOT NULL,
    subtitulo VARCHAR(300),
    editora VARCHAR(150),
    edicao VARCHAR(50),
    ano_publicacao INT(4),
    isbn VARCHAR(20) UNIQUE,
    issn VARCHAR(20),
    eissn VARCHAR(20),
    ndi VARCHAR(50),
    material VARCHAR(100) DEFAULT 'Papel',
    idioma VARCHAR(50) DEFAULT 'Português',
    ilustracao VARCHAR(200),
    encadernacao VARCHAR(100),
    altura_mm INT(5),
    largura_mm INT(5),
    profundidade_mm INT(5),
    numero_paginas INT(5),
    peso_gramas INT(5),
    codigo_interno VARCHAR(100) UNIQUE NOT NULL,
    quantidade_exemplares INT(11) NOT NULL DEFAULT 1,
    quantidade_disponivel INT(11) NOT NULL DEFAULT 1,
    capa_livro LONGBLOB,
    arquivo_pdf LONGBLOB,
    id_armario INT(11),
    sinopse LONGTEXT,
    palavras_chave VARCHAR(500),
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_titulo (titulo),
    KEY idx_autor (autor),
    KEY idx_isbn (isbn),
    KEY idx_codigo (codigo_interno),
    KEY idx_ativo (ativo),
    FULLTEXT KEY ft_pesquisa (titulo, autor, sinopse),
    CONSTRAINT fk_livro_armario FOREIGN KEY (id_armario)
        REFERENCES Armario(id_armario) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS CabecalhoLivro (
    id_cabecalho INT(11) AUTO_INCREMENT PRIMARY KEY,
    id_livro INT(11) NOT NULL,
    id_cdu INT(11) NOT NULL,
    indexacao_descritiva VARCHAR(500) NOT NULL,
    quantidade_exemplares INT(11) NOT NULL,
    numero_capitulo INT,
    titulo_capitulo VARCHAR(300),
    pagina_inicio INT,
    pagina_fim INT,
    numero_chamada VARCHAR(100),
    palavras_chave_capitulo VARCHAR(500),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_livro (id_livro),
    KEY idx_cdu (id_cdu),
    KEY idx_chamada (numero_chamada),
    FULLTEXT KEY ft_indexacao (indexacao_descritiva),
    CONSTRAINT fk_cabecalho_livro FOREIGN KEY (id_livro)
        REFERENCES Livro(id_livro) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_cabecalho_cdu FOREIGN KEY (id_cdu)
        REFERENCES CDU(id_cdu) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Emprestimo (
    id_emprestimo INT(11) AUTO_INCREMENT PRIMARY KEY,
    id_estudante INT(11) NOT NULL,
    id_cabecalho INT(11) NOT NULL,
    id_funcionario INT(11) NOT NULL,
    data_emprestimo DATE DEFAULT (CURDATE()),
    hora_emprestimo TIME DEFAULT (CURTIME()),
    data_devolucao_prevista DATE NOT NULL,
    hora_devolucao_prevista TIME,
    data_devolucao_real DATE,
    hora_devolucao_real TIME,
    data_hora_devolucao_real DATETIME,
    status_emprestimo ENUM('Ativo', 'Devolvido', 'Atraso', 'Perdido') DEFAULT 'Ativo',
    numero_renovacoes INT(11) DEFAULT 0,
    multa_aplicada BOOLEAN DEFAULT FALSE,
    valor_multa DECIMAL(10,2) DEFAULT 0,
    multa_paga BOOLEAN DEFAULT FALSE,
    data_pagamento_multa DATE,
    observacoes TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_estudante (id_estudante),
    KEY idx_cabecalho (id_cabecalho),
    KEY idx_funcionario (id_funcionario),
    KEY idx_status (status_emprestimo),
    CONSTRAINT fk_emprestimo_estudante FOREIGN KEY (id_estudante)
        REFERENCES Estudante(id_estudante) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_emprestimo_cabecalho FOREIGN KEY (id_cabecalho)
        REFERENCES CabecalhoLivro(id_cabecalho) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_emprestimo_funcionario FOREIGN KEY (id_funcionario)
        REFERENCES Funcionario(id_funcionario) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Reserva (
    id_reserva INT(11) AUTO_INCREMENT PRIMARY KEY,
    id_estudante INT(11) NOT NULL,
    id_cabecalho INT(11) NOT NULL,
    data_reserva DATE DEFAULT (CURDATE()),
    hora_reserva TIME DEFAULT (CURTIME()),
    data_prevista DATE NOT NULL,
    hora_prevista TIME,
    status_reserva ENUM('Reservado', 'Expirado', 'Cancelado', 'Utilizado') DEFAULT 'Reservado',
    id_funcionario INT(11),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_reserva_estudante (id_estudante),
    KEY idx_reserva_cabecalho (id_cabecalho),
    KEY idx_status_reserva (status_reserva),
    CONSTRAINT fk_reserva_estudante FOREIGN KEY (id_estudante)
        REFERENCES Estudante(id_estudante) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_reserva_cabecalho FOREIGN KEY (id_cabecalho)
        REFERENCES CabecalhoLivro(id_cabecalho) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_reserva_funcionario FOREIGN KEY (id_funcionario)
        REFERENCES Funcionario(id_funcionario) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS ControleMulta (
    id_multa INT(11) AUTO_INCREMENT PRIMARY KEY,
    id_emprestimo INT(11) NOT NULL,
    id_estudante INT(11) NOT NULL,
    valor_multa_diaria DECIMAL(10,2) NOT NULL DEFAULT 200.00,
    numero_dias_atraso INT(11) NOT NULL DEFAULT 0,
    valor_total_multa DECIMAL(10,2) NOT NULL DEFAULT 0,
    data_calculo DATE DEFAULT (CURDATE()),
    status_pagamento ENUM('Pendente', 'Pago', 'Parcialmente Pago') DEFAULT 'Pendente',
    data_pagamento DATE,
    valor_pago DECIMAL(10,2) DEFAULT 0,
    metodo_pagamento VARCHAR(50),
    numero_recibo VARCHAR(50),
    observacoes TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_multa_emprestimo (id_emprestimo),
    KEY idx_multa_estudante (id_estudante),
    CONSTRAINT fk_multa_emprestimo FOREIGN KEY (id_emprestimo)
        REFERENCES Emprestimo(id_emprestimo) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_multa_estudante FOREIGN KEY (id_estudante)
        REFERENCES Estudante(id_estudante) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 2) ALTERAÇÕES EM TABELAS EXISTENTES, SEM APAGAR DADOS
-- ============================================================================

ALTER TABLE Livro
    ADD COLUMN IF NOT EXISTS subtitulo VARCHAR(300) AFTER titulo,
    ADD COLUMN IF NOT EXISTS editora VARCHAR(150) AFTER subtitulo,
    ADD COLUMN IF NOT EXISTS edicao VARCHAR(50) AFTER editora,
    ADD COLUMN IF NOT EXISTS ano_publicacao INT(4) AFTER edicao,
    ADD COLUMN IF NOT EXISTS isbn VARCHAR(20) AFTER ano_publicacao,
    ADD COLUMN IF NOT EXISTS issn VARCHAR(20) AFTER isbn,
    ADD COLUMN IF NOT EXISTS eissn VARCHAR(20) AFTER issn,
    ADD COLUMN IF NOT EXISTS ndi VARCHAR(50) AFTER eissn,
    ADD COLUMN IF NOT EXISTS material VARCHAR(100) DEFAULT 'Papel' AFTER ndi,
    ADD COLUMN IF NOT EXISTS idioma VARCHAR(50) DEFAULT 'Português' AFTER material,
    ADD COLUMN IF NOT EXISTS ilustracao VARCHAR(200) AFTER idioma,
    ADD COLUMN IF NOT EXISTS encadernacao VARCHAR(100) AFTER ilustracao,
    ADD COLUMN IF NOT EXISTS altura_mm INT(5) AFTER encadernacao,
    ADD COLUMN IF NOT EXISTS largura_mm INT(5) AFTER altura_mm,
    ADD COLUMN IF NOT EXISTS profundidade_mm INT(5) AFTER largura_mm,
    ADD COLUMN IF NOT EXISTS numero_paginas INT(5) AFTER profundidade_mm,
    ADD COLUMN IF NOT EXISTS peso_gramas INT(5) AFTER numero_paginas,
    ADD COLUMN IF NOT EXISTS codigo_interno VARCHAR(100) AFTER peso_gramas,
    ADD COLUMN IF NOT EXISTS quantidade_exemplares INT(11) NOT NULL DEFAULT 1 AFTER codigo_interno,
    ADD COLUMN IF NOT EXISTS quantidade_disponivel INT(11) NOT NULL DEFAULT 1 AFTER quantidade_exemplares,
    ADD COLUMN IF NOT EXISTS capa_livro LONGBLOB AFTER quantidade_disponivel,
    ADD COLUMN IF NOT EXISTS arquivo_pdf LONGBLOB AFTER capa_livro,
    ADD COLUMN IF NOT EXISTS id_armario INT(11) AFTER arquivo_pdf,
    ADD COLUMN IF NOT EXISTS sinopse LONGTEXT AFTER id_armario,
    ADD COLUMN IF NOT EXISTS palavras_chave VARCHAR(500) AFTER sinopse,
    ADD COLUMN IF NOT EXISTS ativo BOOLEAN DEFAULT TRUE AFTER palavras_chave,
    ADD COLUMN IF NOT EXISTS criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER ativo,
    ADD COLUMN IF NOT EXISTS modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER criado_em;

ALTER TABLE Livro
    ADD UNIQUE KEY IF NOT EXISTS uk_livro_codigo_interno (codigo_interno),
    ADD KEY IF NOT EXISTS idx_livro_armario (id_armario),
    ADD KEY IF NOT EXISTS idx_livro_ativo (ativo);

ALTER TABLE Funcionario
    ADD COLUMN IF NOT EXISTS Func ENUM('Func','Admin') NOT NULL DEFAULT 'Func' AFTER id_contacto,
    ADD COLUMN IF NOT EXISTS senha VARCHAR(255) AFTER Func;

ALTER TABLE Estudante
    ADD COLUMN IF NOT EXISTS id_endereco INT(11) AFTER email;

ALTER TABLE Armario
    ADD COLUMN IF NOT EXISTS Partileira VARCHAR(50) AFTER armario;

ALTER TABLE Computador
    ADD COLUMN IF NOT EXISTS em_uso BOOLEAN DEFAULT FALSE AFTER numero;

ALTER TABLE Atendimento
    ADD COLUMN IF NOT EXISTS data_entrada DATE DEFAULT (CURRENT_DATE) AFTER id_computador,
    ADD COLUMN IF NOT EXISTS hora_entrada TIME DEFAULT (CURRENT_TIME) AFTER data_entrada,
    ADD COLUMN IF NOT EXISTS hora_saida TIME AFTER hora_entrada,
    ADD COLUMN IF NOT EXISTS tempo_limite_minutos INT(11) DEFAULT 60 AFTER hora_saida,
    ADD COLUMN IF NOT EXISTS status ENUM('Em Uso', 'Finalizado') DEFAULT 'Em Uso' AFTER tempo_limite_minutos,
    ADD COLUMN IF NOT EXISTS criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER status;

ALTER TABLE CDU
    ADD COLUMN IF NOT EXISTS ativo BOOLEAN DEFAULT TRUE AFTER tipo_tabela,
    ADD COLUMN IF NOT EXISTS criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER ativo,
    ADD COLUMN IF NOT EXISTS modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER criado_em;

ALTER TABLE Emprestimo
    ADD COLUMN IF NOT EXISTS id_cabecalho INT(11) AFTER id_estudante,
    ADD COLUMN IF NOT EXISTS id_funcionario INT(11) AFTER id_cabecalho,
    ADD COLUMN IF NOT EXISTS data_emprestimo DATE DEFAULT (CURDATE()) AFTER id_funcionario,
    ADD COLUMN IF NOT EXISTS hora_emprestimo TIME DEFAULT (CURTIME()) AFTER data_emprestimo,
    ADD COLUMN IF NOT EXISTS data_devolucao_prevista DATE AFTER hora_emprestimo,
    ADD COLUMN IF NOT EXISTS hora_devolucao_prevista TIME AFTER data_devolucao_prevista,
    ADD COLUMN IF NOT EXISTS data_devolucao_real DATE AFTER hora_devolucao_prevista,
    ADD COLUMN IF NOT EXISTS hora_devolucao_real TIME AFTER data_devolucao_real,
    ADD COLUMN IF NOT EXISTS data_hora_devolucao_real DATETIME AFTER hora_devolucao_real,
    ADD COLUMN IF NOT EXISTS status_emprestimo ENUM('Ativo', 'Devolvido', 'Atraso', 'Perdido') DEFAULT 'Ativo' AFTER data_hora_devolucao_real,
    ADD COLUMN IF NOT EXISTS numero_renovacoes INT(11) DEFAULT 0 AFTER status_emprestimo,
    ADD COLUMN IF NOT EXISTS multa_aplicada BOOLEAN DEFAULT FALSE AFTER numero_renovacoes,
    ADD COLUMN IF NOT EXISTS valor_multa DECIMAL(10,2) DEFAULT 0 AFTER multa_aplicada,
    ADD COLUMN IF NOT EXISTS multa_paga BOOLEAN DEFAULT FALSE AFTER valor_multa,
    ADD COLUMN IF NOT EXISTS data_pagamento_multa DATE AFTER multa_paga,
    ADD COLUMN IF NOT EXISTS observacoes TEXT AFTER data_pagamento_multa,
    ADD COLUMN IF NOT EXISTS criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER observacoes,
    ADD COLUMN IF NOT EXISTS modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER criado_em;

ALTER TABLE Reserva
    ADD COLUMN IF NOT EXISTS data_reserva DATE DEFAULT (CURDATE()) AFTER idCabecalho,
    ADD COLUMN IF NOT EXISTS hora_reserva TIME DEFAULT (CURTIME()) AFTER data_reserva,
    ADD COLUMN IF NOT EXISTS data_prevista DATE AFTER hora_reserva,
    ADD COLUMN IF NOT EXISTS hora_prevista TIME AFTER data_prevista,
    ADD COLUMN IF NOT EXISTS status_reserva ENUM('Reservado', 'Expirado', 'Cancelado', 'Utilizado') DEFAULT 'Reservado' AFTER hora_prevista,
    ADD COLUMN IF NOT EXISTS id_funcionario INT(11) AFTER status_reserva,
    ADD COLUMN IF NOT EXISTS criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER id_funcionario,
    ADD COLUMN IF NOT EXISTS modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER criado_em;

-- ============================================================================
-- 3) ÍNDICES E CONSTRAINTS NOVOS, SEM EXCLUIR DADOS
-- ============================================================================

-- Garantir os índices essenciais para catálogo e empréstimos
CREATE INDEX IF NOT EXISTS idx_livro_armario ON Livro (id_armario);
CREATE INDEX IF NOT EXISTS idx_livro_ativo ON Livro (ativo);
CREATE INDEX IF NOT EXISTS idx_cdu_codigo ON CDU (codigo_cdu);
CREATE INDEX IF NOT EXISTS idx_cabecalho_livro ON CabecalhoLivro (id_livro);
CREATE INDEX IF NOT EXISTS idx_emprestimo_status ON Emprestimo (status_emprestimo);
CREATE INDEX IF NOT EXISTS idx_reserva_status ON Reserva (status_reserva);

-- ============================================================================
-- 4) DADOS INICIAIS ESSENCIAIS
-- ============================================================================

INSERT INTO CDU (id_cdu, codigo_cdu, descricao, classe_pai_id, tipo_tabela, ativo)
SELECT 1, '0', 'Ciência e conhecimento. Organização. Informação. Documentação. Biblioteconomia', NULL, 'principal', TRUE
WHERE NOT EXISTS (SELECT 1 FROM CDU WHERE codigo_cdu = '0');

INSERT INTO CDU (id_cdu, codigo_cdu, descricao, classe_pai_id, tipo_tabela, ativo)
SELECT 2, '8', 'Língua. Linguística. Literatura', NULL, 'principal', TRUE
WHERE NOT EXISTS (SELECT 1 FROM CDU WHERE codigo_cdu = '8');

INSERT INTO CDU (id_cdu, codigo_cdu, descricao, classe_pai_id, tipo_tabela, ativo)
SELECT 3, '82', 'Literatura', 2, 'principal', TRUE
WHERE NOT EXISTS (SELECT 1 FROM CDU WHERE codigo_cdu = '82');

INSERT INTO CDU (id_cdu, codigo_cdu, descricao, classe_pai_id, tipo_tabela, ativo)
SELECT 4, '821', 'Literatura de línguas individuais', 3, 'principal', TRUE
WHERE NOT EXISTS (SELECT 1 FROM CDU WHERE codigo_cdu = '821');

INSERT INTO CDU (id_cdu, codigo_cdu, descricao, classe_pai_id, tipo_tabela, ativo)
SELECT 5, '821.134.3', 'Literatura de língua portuguesa', 4, 'principal', TRUE
WHERE NOT EXISTS (SELECT 1 FROM CDU WHERE codigo_cdu = '821.134.3');

-- ============================================================================
-- FIM DA MIGRAÇÃO
-- ============================================================================
SELECT 'Migração BibliotecaDBA concluída com sucesso. Dados preservados.' AS status;
