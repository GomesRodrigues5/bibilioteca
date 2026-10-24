CREATE DATABASE IF NOT EXISTS BibliotecaDBA;
USE BibliotecaDBA;

-- ========================================
-- TABELAS DE ESTRUTURA ORGANIZACIONAL
-- ========================================

CREATE TABLE Departamento (
    id_departamento INT(11) AUTO_INCREMENT PRIMARY KEY,
    nome_departamento VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_nome (nome_departamento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Curso (
    id_curso INT(11) AUTO_INCREMENT PRIMARY KEY,
    nome_curso VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    id_departamento INT NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_departamento) REFERENCES Departamento(id_departamento) ON DELETE RESTRICT,
    INDEX idx_nome (nome_curso),
    INDEX idx_departamento (id_departamento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Endereco (
    id_endereco INT(11) AUTO_INCREMENT PRIMARY KEY,
    municipio VARCHAR(100) NOT NULL,
    bairro VARCHAR(100) NOT NULL,
    rua VARCHAR(150),
    numero VARCHAR(10),
    complemento VARCHAR(150),
    codigo_postal VARCHAR(20),
    pais VARCHAR(50) DEFAULT 'Portugal',
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_municipio (municipio),
    INDEX idx_bairro (bairro)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Contacto (
    id_contacto INT(11) AUTO_INCREMENT PRIMARY KEY,
    telefone_principal VARCHAR(15) NOT NULL,
    telefone_secundario VARCHAR(15),
    email_institucional VARCHAR(100),
    email_pessoal VARCHAR(100),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email_institucional)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Funcao (
    id_funcao INT(11) AUTO_INCREMENT PRIMARY KEY,
    nome_funcao VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nome (nome_funcao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Funcionario (
    id_funcionario INT(11) AUTO_INCREMENT PRIMARY KEY,
    nome_completo VARCHAR(100) NOT NULL,
    id_funcao INT NOT NULL,
    id_departamento INT NOT NULL,
    id_contacto INT NOT NULL,
    tipo_funcao ENUM('Funcionario', 'Administrador') NOT NULL DEFAULT 'Funcionario',
    email VARCHAR(100) UNIQUE,
    nome_usuario VARCHAR(50) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ultimo_acesso DATETIME,
    FOREIGN KEY (id_funcao) REFERENCES Funcao(id_funcao) ON DELETE RESTRICT,
    FOREIGN KEY (id_departamento) REFERENCES Departamento(id_departamento) ON DELETE RESTRICT,
    FOREIGN KEY (id_contacto) REFERENCES Contacto(id_contacto) ON DELETE RESTRICT,
    INDEX idx_nome (nome_completo),
    INDEX idx_usuario (nome_usuario),
    INDEX idx_email (email),
    INDEX idx_ativo (ativo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Estudante (
    id_estudante INT(11) AUTO_INCREMENT PRIMARY KEY,
    nome_completo VARCHAR(100) NOT NULL,
    numero_estudante VARCHAR(20) UNIQUE NOT NULL,
    id_curso INT NOT NULL,
    id_contacto INT NOT NULL,
    id_endereco INT,
    data_nascimento DATE,
    genero ENUM('Masculino', 'Feminino', 'Outro'),
    numero_documento VARCHAR(20) UNIQUE,
    tipo_documento ENUM('CC', 'Passport', 'BI'),
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_curso) REFERENCES Curso(id_curso) ON DELETE RESTRICT,
    FOREIGN KEY (id_contacto) REFERENCES Contacto(id_contacto) ON DELETE RESTRICT,
    FOREIGN KEY (id_endereco) REFERENCES Endereco(id_endereco) ON DELETE SET NULL,
    INDEX idx_nome (nome_completo),
    INDEX idx_numero (numero_estudante),
    INDEX idx_ativo (ativo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Armario (
    id_armario INT(11) AUTO_INCREMENT PRIMARY KEY,
    codigo_armario VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(100),
    numero_prateleiras INT DEFAULT 5,
    localizacao VARCHAR(100),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_codigo (codigo_armario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Computador (
    id_computador INT(11) AUTO_INCREMENT PRIMARY KEY,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    numero_serie VARCHAR(20) UNIQUE NOT NULL,
    em_uso BOOLEAN DEFAULT FALSE,
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_numero (numero_serie),
    INDEX idx_em_uso (em_uso)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Atendimento (
    id_atendimento INT(11) AUTO_INCREMENT PRIMARY KEY,
    id_estudante INT(11) NOT NULL,
    id_computador INT(11) NOT NULL,
    data_entrada DATE DEFAULT (CURDATE()),
    hora_entrada TIME DEFAULT (CURTIME()),
    hora_saida TIME,
    tempo_limite_minutos INT(11) DEFAULT 60,
    status ENUM('Em Uso', 'Finalizado') DEFAULT 'Em Uso',
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudante) REFERENCES Estudante(id_estudante) ON DELETE RESTRICT,
    FOREIGN KEY (id_computador) REFERENCES Computador(id_computador) ON DELETE RESTRICT,
    INDEX idx_estudante (id_estudante),
    INDEX idx_computador (id_computador),
    INDEX idx_data (data_entrada)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE AgendaComputador (
    id_agenda INT(11) AUTO_INCREMENT PRIMARY KEY,
    id_estudante INT(11) NOT NULL,
    id_computador INT(11) NOT NULL,
    data_agendada DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    status ENUM('Agendado', 'Utilizado', 'Cancelado', 'Nao Utilizado') DEFAULT 'Agendado',
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudante) REFERENCES Estudante(id_estudante) ON DELETE RESTRICT,
    FOREIGN KEY (id_computador) REFERENCES Computador(id_computador) ON DELETE RESTRICT,
    INDEX idx_estudante (id_estudante),
    INDEX idx_computador (id_computador),
    INDEX idx_data (data_agendada)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- TABELAS DE CATALOGAÇÃO BIBLIOGRÁFICA
-- ========================================

-- Classificação Decimal Universal (CDU)
CREATE TABLE CDU (
    id_cdu INT(11) AUTO_INCREMENT PRIMARY KEY,
    codigo_cdu VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(500) NOT NULL,
    classe_pai_id INT,
    tipo_tabela VARCHAR(50) DEFAULT 'principal',
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_classe_pai FOREIGN KEY (classe_pai_id) 
        REFERENCES CDU(id_cdu) ON DELETE SET NULL,
    INDEX idx_codigo (codigo_cdu),
    INDEX idx_classe_pai (classe_pai_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Catálogo de Livros
CREATE TABLE Livro (
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
    FOREIGN KEY (id_armario) REFERENCES Armario(id_armario) ON DELETE SET NULL,
    INDEX idx_titulo (titulo),
    INDEX idx_autor (autor),
    INDEX idx_isbn (isbn),
    INDEX idx_codigo (codigo_interno),
    INDEX idx_ativo (ativo),
    FULLTEXT INDEX ft_pesquisa (titulo, autor, sinopse)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Cabeçalhos de Catalogação (Indexação de Livros)
CREATE TABLE CabecalhoLivro (
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
    CONSTRAINT fk_livro FOREIGN KEY (id_livro) REFERENCES Livro(id_livro) ON DELETE CASCADE,
    CONSTRAINT fk_cdu FOREIGN KEY (id_cdu) REFERENCES CDU(id_cdu) ON DELETE RESTRICT,
    INDEX idx_livro (id_livro),
    INDEX idx_cdu (id_cdu),
    INDEX idx_chamada (numero_chamada),
    FULLTEXT INDEX ft_indexacao (indexacao_descritiva)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- TABELAS DE GESTÃO DE EMPRÉSTIMOS
-- ========================================

CREATE TABLE Emprestimo (
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
    numero_renovacoes INT DEFAULT 0,
    multa_aplicada BOOLEAN DEFAULT FALSE,
    valor_multa DECIMAL(10,2) DEFAULT 0,
    multa_paga BOOLEAN DEFAULT FALSE,
    data_pagamento_multa DATE,
    observacoes TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudante) REFERENCES Estudante(id_estudante) ON DELETE RESTRICT,
    FOREIGN KEY (id_cabecalho) REFERENCES CabecalhoLivro(id_cabecalho) ON DELETE RESTRICT,
    FOREIGN KEY (id_funcionario) REFERENCES Funcionario(id_funcionario) ON DELETE RESTRICT,
    INDEX idx_estudante (id_estudante),
    INDEX idx_cabecalho (id_cabecalho),
    INDEX idx_status (status_emprestimo),
    INDEX idx_data (data_emprestimo),
    INDEX idx_devolucao (data_devolucao_prevista)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Reserva (
    id_reserva INT(11) AUTO_INCREMENT PRIMARY KEY,
    id_estudante INT(11) NOT NULL,
    id_cabecalho INT(11) NOT NULL,
    id_funcionario INT(11) NOT NULL,
    data_reserva DATE DEFAULT (CURDATE()),
    hora_reserva TIME DEFAULT (CURTIME()),
    data_retirada_prevista DATE NOT NULL,
    hora_retirada_prevista TIME,
    data_retirada_real DATE,
    hora_retirada_real TIME,
    status_reserva ENUM('Reservado', 'Retirado', 'Cancelado', 'Expirado') DEFAULT 'Reservado',
    dias_reserva INT DEFAULT 7,
    observacoes TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudante) REFERENCES Estudante(id_estudante) ON DELETE RESTRICT,
    FOREIGN KEY (id_cabecalho) REFERENCES CabecalhoLivro(id_cabecalho) ON DELETE RESTRICT,
    FOREIGN KEY (id_funcionario) REFERENCES Funcionario(id_funcionario) ON DELETE RESTRICT,
    INDEX idx_estudante (id_estudante),
    INDEX idx_cabecalho (id_cabecalho),
    INDEX idx_status (status_reserva),
    INDEX idx_data (data_reserva)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- TABELAS DE CONTROLE DE MULTAS
-- ========================================

CREATE TABLE ControleMulta (
    id_multa INT(11) AUTO_INCREMENT PRIMARY KEY,
    id_emprestimo INT(11) NOT NULL,
    id_estudante INT(11) NOT NULL,
    valor_multa_diaria DECIMAL(10,2) NOT NULL,
    numero_dias_atraso INT NOT NULL,
    valor_total_multa DECIMAL(10,2) NOT NULL,
    data_calculo DATE DEFAULT (CURDATE()),
    status_pagamento ENUM('Pendente', 'Parcialmente Pago', 'Pago') DEFAULT 'Pendente',
    data_pagamento DATE,
    valor_pago DECIMAL(10,2) DEFAULT 0,
    metodo_pagamento VARCHAR(50),
    numero_recibo VARCHAR(50),
    observacoes TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modificado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_emprestimo) REFERENCES Emprestimo(id_emprestimo) ON DELETE RESTRICT,
    FOREIGN KEY (id_estudante) REFERENCES Estudante(id_estudante) ON DELETE RESTRICT,
    INDEX idx_estudante (id_estudante),
    INDEX idx_status (status_pagamento),
    INDEX idx_data (data_calculo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- INSERÇÃO DE DADOS DE CATALOGAÇÃO PADRÃO (CDU)
-- ========================================

INSERT INTO CDU (codigo_cdu, descricao, classe_pai_id, tipo_tabela) VALUES  
(1, '0', 'Ciência, Conhecimento, Informação', NULL, 'principal'),
('0/9', 'Tabelas de propósitos gerais', NULL, 'principal'),
('1', 'Filosofia, Psicologia', NULL, 'principal'),
('2', 'Religião, Teologia', NULL, 'principal'),
('3', 'Ciências Sociais', NULL, 'principal'),
('4', 'Linguística, Filologia', NULL, 'principal'),
('5', 'Ciências Naturais, Matemática', NULL, 'principal'),
('6', 'Tecnologia (Ciências Aplicadas)', NULL, 'principal'),
('7', 'Belas Artes, Recreação', NULL, 'principal'),
('8', 'Língua, Literatura', NULL, 'principal'),
('9', 'Geografia, Biografia, História', NULL, 'principal'),
('82', 'Literatura', 8, 'secundaria'),
('821', 'Literatura de Línguas Individuais', 82, 'secundaria'),
('821.134.3', 'Literatura de Língua Portuguesa', 821, 'terciaria');
