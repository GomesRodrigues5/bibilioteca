CREATE DATABASE BibliotecaDB;
USE BibliotecaDB;

CREATE TABLE Departamento (
    id_departamento INT AUTO_INCREMENT PRIMARY KEY,
    departamento VARCHAR(100) NOT NULL
);

CREATE TABLE Curso (
    id_curso INT AUTO_INCREMENT PRIMARY KEY,
    curso VARCHAR(100) NOT NULL,
    id_departamento INT,
    FOREIGN KEY (id_departamento) REFERENCES Departamento(id_departamento)
);

CREATE TABLE Endereco (
    id_endereco INT AUTO_INCREMENT PRIMARY KEY,
    municipio VARCHAR(100),
    bairro VARCHAR(100)
);

CREATE TABLE Contacto (
    id_contacto INT AUTO_INCREMENT PRIMARY KEY,
    telefone1 VARCHAR(15) NOT NULL,
    telefone2 VARCHAR(15)
);

CREATE TABLE Funcao (
    id_funcao INT AUTO_INCREMENT PRIMARY KEY,
    funcao VARCHAR(50) NOT NULL,
    descricao TEXT
);

CREATE TABLE Funcionario (
    id_funcionario INT AUTO_INCREMENT PRIMARY KEY,
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
    id_estudante INT AUTO_INCREMENT PRIMARY KEY,
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
    id_armario INT AUTO_INCREMENT PRIMARY KEY,
    armario VARCHAR(50) NOT NULL
);

CREATE TABLE Livro (
    id_livro INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    cor VARCHAR(30),
    pagina INT,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    id_armario INT,
    disponivel BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_armario) REFERENCES Armario(id_armario)
);

CREATE TABLE Computador (
    id_computador INT AUTO_INCREMENT PRIMARY KEY,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    numero VARCHAR(20) UNIQUE NOT NULL,
    em_uso BOOLEAN DEFAULT FALSE
);

CREATE TABLE Emprestimo (
    id_emprestimo INT AUTO_INCREMENT PRIMARY KEY,
    id_estudante INT,
    id_livro INT,
    data_de_emprestimo DATE NOT NULL,
    hora_de_emprestimo TIME NOT NULL,
    data_entrega DATE NOT NULL CURRENT_DATE,
    hora_de_entrega TIME NOT NULL ,
    valor DECIMAL(10,2) DEFAULT 0,
    acao ENUM('Devolvido','Nao') DEFAULT 'Nao',
    data_e_hora_da_entrega DATETIME,
    id_funcionario INT,
    FOREIGN KEY (id_estudante) REFERENCES Estudante(id_estudante),
    FOREIGN KEY (id_livro) REFERENCES Livro(id_livro),
    FOREIGN KEY (id_funcionario) REFERENCES Funcionario(id_funcionario)
);

CREATE TABLE Atendimento (
    id_atendimento INT AUTO_INCREMENT PRIMARY KEY,
    id_estudante INT,
    id_computador INT,
    data_de_entrada DATE NOT NULL DEFAULT CURRENT_DATE,
    hora_de_entrada TIME NOT NULL DEFAULT CURRENT_TIME,
    hora_de_saida TIME NOT NULL,
    tempo_limite_minutos INT DEFAULT 60,
    acao ENUM('Em Uso','Finalizado') DEFAULT 'Em Uso',
    FOREIGN KEY (id_estudante) REFERENCES Estudante(id_estudante),
    FOREIGN KEY (id_computador) REFERENCES Computador(id_computador)
);