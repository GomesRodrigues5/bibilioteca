CREATE DATABASE BibliotecaDBA;
USE BibliotecaDBA;

CREATE TABLE Departamento (
    id_departamento INT(11) AUTO_INCREMENT PRIMARY KEY,
    departamento VARCHAR(100) NOT NULL
);

CREATE TABLE Curso (
    id_curso INT(11) AUTO_INCREMENT PRIMARY KEY,
    curso VARCHAR(100) NOT NULL,
    id_departamento INT,
    FOREIGN KEY (id_departamento) REFERENCES Departamento(id_departamento)
);

CREATE TABLE Endereco (
    id_endereco INT(11) AUTO_INCREMENT PRIMARY KEY,
    municipio VARCHAR(100),
    bairro VARCHAR(100)
);

CREATE TABLE Contacto (
    id_contacto INT(11) AUTO_INCREMENT PRIMARY KEY,
    telefone1 VARCHAR(15) NOT NULL,
    telefone2 VARCHAR(15)
);

CREATE TABLE Funcao (
    id_funcao INT AUTO_INCREMENT PRIMARY KEY,
    funcao VARCHAR(50) NOT NULL,
    descricao TEXT
);

CREATE TABLE Funcionario (
    id_funcionario INT(11) AUTO_INCREMENT PRIMARY KEY,
    funcionario VARCHAR(100) NOT NULL,
    id_funcao INT,
    id_departamento INT,
    id_contacto INT,
    Func enum('Func','Admin') NOT NULL DEFAULT 'Func'
    ,
    senha VARCHAR(255) NOT NULL,
    FOREIGN KEY (id_funcao) REFERENCES Funcao(id_funcao),
    FOREIGN KEY (id_departamento) REFERENCES Departamento(id_departamento),
    FOREIGN KEY (id_contacto) REFERENCES Contacto(id_contacto)
);

CREATE TABLE Estudante (
    id_estudante INT(11) AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    numero_estudante VARCHAR(20) UNIQUE NOT NULL,
    id_curso INT,
    id_contacto INT,
    email VARCHAR(100),
    id_endereco INT,
    FOREIGN KEY (id_curso) REFERENCES Curso(id_curso),
    FOREIGN KEY (id_contacto) REFERENCES Contacto(id_contacto),
    FOREIGN KEY (id_endereco) REFERENCES Endereco(id_endereco)
);

CREATE TABLE Armario (
    id_armario INT(11) AUTO_INCREMENT PRIMARY KEY,
    armario VARCHAR(50) NOT NULL,
    Partileira VARCHAR(50) NOT NULL
);

CREATE TABLE Computador (
    id_computador INT(11) AUTO_INCREMENT PRIMARY KEY,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    numero VARCHAR(20) UNIQUE NOT NULL,
    em_uso BOOLEAN DEFAULT FALSE
);

CREATE TABLE Atendimento (
    id_atendimento INT(11) AUTO_INCREMENT PRIMARY KEY,
    id_estudante INT(11),
    id_computador INT(11),
    data_de_entrada DATE default (current_date()),
    hora_de_entrada Time default (current_time()),
    hora_de_saida TIME NOT NULL,
    tempo_limite_minutos INT(11) DEFAULT 60,
    acao ENUM('Em Uso','Finalizado') DEFAULT 'Em Uso',
    FOREIGN KEY (id_estudante) REFERENCES Estudante(id_estudante),
    FOREIGN KEY (id_computador) REFERENCES Computador(id_computador)
);

CREATE TABLE agendar_computador (
    
    id_atendimento INT(11) AUTO_INCREMENT PRIMARY KEY,
    id_estudante INT(11),
    id_computador INT(11),
    data_de_agendar DATE default (current_date()),
    hora_de_agendar Time default (current_time()),
    hora_de_saida_agendar TIME NOT NULL,
    tempo_limite_minutos INT(11) DEFAULT 60,
    acao ENUM('Agendado','Não') DEFAULT 'Não',
    FOREIGN KEY (id_estudante) REFERENCES Estudante(id_estudante),
    FOREIGN KEY (id_computador) REFERENCES Computador(id_computador)
);

/*INFORMAÇÕES DA BASE DE DADOS DE LIVROS*/

CREATE TABLE Livro (
    id_livro INT(11) PRIMARY KEY AUTO_INCREMENT,
    autor VARCHAR(200) NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    subtitulo VARCHAR(200),
    edicao VARCHAR(200),
    isbn VARCHAR(20),
    issn VARCHAR(20),
    cdn VARCHAR(50),
    data_lancamento DATE,
    material VARCHAR(200),
    ilustracao VARCHAR(200),
    comprimento INT(11),
    largura INT(11),
    paginas INT(11),
    codigo VARCHAR(90) UNIQUE NOT NULL,
    quantidade_exemplares INT(11) NOT NULL DEFAULT 1,
    capado_livro LONGBLOB NOT NULL,
    pdf LONGBLOB NOT NULL,
    id_armario INT(11),
    disponivel BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_armario) REFERENCES Armario(id_armario)
);


CREATE TABLE cdu (

    id_cdu INT(11) PRIMARY KEY AUTO_INCREMENT,
    codigo_cdu VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(255) NOT NULL,
    classe_pai_id INT DEFAULT NULL,
    tipo_tabela VARCHAR(50) DEFAULT 'principal',
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Auto-relacionamento para manter a hierarquia decimal
    CONSTRAINT fk_classe_pai FOREIGN KEY (classe_pai_id) 
        REFERENCES cdu(id_cdu) ON DELETE SET NULL
);

CREATE TABLE cabecalhos_livro (

    idCabecalho INT(11) PRIMARY KEY AUTO_INCREMENT,
    id_livro INT(11) NOT NULL,
    Indexacao VARCHAR(255) NOT NULL,
    Quantidade_exemplares INT(11) NOT NULL,
    capitulo_numero INT NOT NULL,
    texto_pagina_par VARCHAR(255) NOT NULL,
    texto_pagina_impar VARCHAR(255) NOT NULL,
    ocultar_no_inicio_capitulo BOOLEAN DEFAULT TRUE,
    estilo_fonte VARCHAR(100) DEFAULT 'Arial',
    tamanho_fonte DECIMAL(4,1) DEFAULT 9.0,
    cadastrado_em TIME DEFAULT (current_time()),
    id_cdu INT(11) NOT NULL,
    CONSTRAINT fk_livro FOREIGN KEY (id_livro) REFERENCES livro(id_livro) ON DELETE CASCADE,
    CONSTRAINT fkCDU FOREIGN KEY (id_cdu) REFERENCES cdu (id_cdu) ON DELETE CASCADE
);

-- 1. Inserindo as classes principais (raiz)
INSERT INTO cdu (id_cdu, codigo_cdu, descricao, classe_pai_id) VALUES  
(1, '0', 'Ciência e conhecimento. Organização. Informação. Documentação. Biblioteconomia', NULL), 
(2, '8', 'Língua. Linguística. Literatura', NULL);

-- 2. Inserindo sub-classes (filhas da classe 8)
INSERT INTO cdu (id_cdu, codigo_cdu, descricao, classe_pai_id) VALUES 
(3, '82', 'Literatura', 2),
(4, '821', 'Literatura de línguas individuais', 3);
-- 3. Inserindo sub-classe específica
INSERT INTO cdu (id_cdu, codigo_cdu, descricao, classe_pai_id) VALUES
 (5, '821.134.3', 'Literatura de língua portuguesa', 4);


CREATE TABLE Emprestimo (

    id_emprestimo INT(11) AUTO_INCREMENT PRIMARY KEY,
    id_estudante INT(11),
    idCabecalho INT(11),
    data_de_emprestimo DATE default (current_date()),
    hora_de_emprestimo TIME default(current_time()),
    data_entrega DATE  NOT NULL,
    hora_de_entrega TIME NOT NULL ,
    valor DECIMAL(10,2) DEFAULT 0,
    acao ENUM('Devolvido','Nao') DEFAULT 'Nao',
    data_e_hora_da_entrega DATETIME,
    id_funcionario INT(11),
    FOREIGN KEY (id_estudante) REFERENCES Estudante(id_estudante),
    FOREIGN KEY (idCabecalho) REFERENCES cabecalhos_livro(idCabecalho),
    FOREIGN KEY (id_funcionario) REFERENCES Funcionario(id_funcionario)
);

CREATE TABLE reserva (
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    id_estudante INT,
    idCabecalho INT,
    data_da_reserva DATE default (current_date()),
    hora_da_reserva TIME default(current_time()),
    data_em DATE  NOT NULL,
    hora_em TIME NOT NULL ,
    acao ENUM('Reservado','Nao') DEFAULT 'Nao',
    id_funcionario INT,
    FOREIGN KEY (id_estudante) REFERENCES Estudante(id_estudante),
    FOREIGN KEY (idCabecalho) REFERENCES cabecalhos_livro(idCabecalho),
    FOREIGN KEY (id_funcionario) REFERENCES Funcionario(id_funcionario)
);
