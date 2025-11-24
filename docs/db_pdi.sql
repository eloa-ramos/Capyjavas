-- DESTRÓI E CRIA O BANCO DE DADOS
DROP DATABASE IF EXISTS db_pdi;

CREATE DATABASE db_pdi;
USE db_pdi;

-- =========================================================
-- 1. TABELA DE ÁREAS
-- =========================================================
CREATE TABLE `Areas` (
  `id_area` INT NOT NULL AUTO_INCREMENT,
  `nome_area` VARCHAR(100) NOT NULL UNIQUE,
  PRIMARY KEY (`id_area`)
);

-- INSERÇÃO DE DADOS INICIAIS (ÁREAS)
INSERT INTO `Areas` (`nome_area`) VALUES
('Tecnologia'), -- id_area = 1
('RH'),         -- id_area = 2
('Financeiro'), -- id_area = 3
('Marketing'),  -- id_area = 4
('Vendas');     -- id_area = 5


-- =========================================================
-- 2. TABELA DE USUÁRIOS
-- =========================================================
CREATE TABLE `Usuarios` (
  `id_usuario` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `senha` VARCHAR(255) NOT NULL COLLATE utf8mb4_bin,
  `nome` VARCHAR(255) NOT NULL,
  `cpf` VARCHAR(14) NOT NULL UNIQUE,
  `data_nascimento` DATE,
  `cargo` VARCHAR(100),
  `id_area` INT NOT NULL,
  `experiencia` TEXT,
  `observacoes` TEXT,
  `id_gestor_de_area` INT,
  `id_gestor_geral` INT,
  `tipo_acesso` ENUM('RH', 'Gestor Geral', 'Gestor de Area', 'Colaborador') NOT NULL,
  PRIMARY KEY (`id_usuario`),
  CONSTRAINT `fk_usuario_area`
    FOREIGN KEY (`id_area`)
    REFERENCES `Areas` (`id_area`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_gestor_area`
    FOREIGN KEY (`id_gestor_de_area`)
    REFERENCES `Usuarios` (`id_usuario`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_gestor_geral`
    FOREIGN KEY (`id_gestor_geral`)
    REFERENCES `Usuarios` (`id_usuario`)
    ON DELETE SET NULL
    ON UPDATE CASCADE
);


-- =========================================================
-- 3. TABELA DE SKILLS
-- =========================================================
CREATE TABLE `Skills` (
  `id_skill` INT NOT NULL AUTO_INCREMENT,
  `nome_skill` VARCHAR(255) NOT NULL,
  `tipo_skill` ENUM('Hard Skill', 'Soft Skill') NOT NULL,
  PRIMARY KEY (`id_skill`)
);


-- =========================================================
-- 4. TABELA DE PDI
-- =========================================================
CREATE TABLE `PDI` (
  `id_pdi` INT NOT NULL AUTO_INCREMENT,
  `id_colaborador` INT NOT NULL,
  `data_inicio` DATE NOT NULL,
  `data_fim` DATE NOT NULL,
  `observacoes` TEXT,
  PRIMARY KEY (`id_pdi`),
  CONSTRAINT `fk_pdi_colaborador`
    FOREIGN KEY (`id_colaborador`)
    REFERENCES `Usuarios` (`id_usuario`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);


-- =========================================================
-- 5. TABELA DE METAS
-- =========================================================
CREATE TABLE `Metas` (
  `id_meta` INT NOT NULL AUTO_INCREMENT,
  `id_pdi` INT NOT NULL,
  `id_skill` INT NOT NULL,
  `meta_pontuacao` DECIMAL(5, 2) NOT NULL,
  `pontuacao_obtida` DECIMAL(5, 2) NOT NULL,
  -- Coluna Gerada/Calculada
  `percentual_atingido` DECIMAL(5, 2) AS ( (`pontuacao_obtida` / NULLIF(`meta_pontuacao`, 0)) * 100 ) STORED,
  PRIMARY KEY (`id_meta`),
  CONSTRAINT `fk_metas_pdi`
    FOREIGN KEY (`id_pdi`)
    REFERENCES `PDI` (`id_pdi`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_metas_skill`
    FOREIGN KEY (`id_skill`)
    REFERENCES `Skills` (`id_skill`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);


-- =========================================================
-- 6. TABELA DE ANEXOS
-- =========================================================
CREATE TABLE `Anexos` (
  `id_anexo` INT NOT NULL AUTO_INCREMENT,
  `id_pdi` INT NOT NULL,
  `nome_arquivo` VARCHAR(255) NOT NULL,
  `caminho_arquivo` VARCHAR(500) NOT NULL,
  `tipo_arquivo` ENUM('pdf', 'png', 'jpeg') NOT NULL,
  `observacoes` TEXT,
  PRIMARY KEY (`id_anexo`),
  CONSTRAINT `fk_anexos_pdi`
    FOREIGN KEY (`id_pdi`)
    REFERENCES `PDI` (`id_pdi`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);


-- =========================================================
-- INSERÇÃO DE DADOS DE TESTE (EXPANDIDO)
-- AS SENHAS FORAM CRIPTOGRAFADAS COM SHA2(SENHA, 256)
-- =========================================================

-- ID_AREA: 1=Tecnologia, 2=RH, 3=Financeiro, 4=Marketing, 5=Vendas

-- 1. GESTOR GERAL (Área Financeiro ID 3) - Senha original: 'gg123'
INSERT INTO Usuarios (email, senha, nome, cpf, data_nascimento, cargo, tipo_acesso, id_area)
VALUES
('gg@youtan.com', SHA2('gg123', 256), 'Marcelo Gestor Geral', '22222222222', '1978-11-10', 'CEO', 'Gestor Geral', 3); -- ID 1

-- 2. RH (Área RH ID 2) - Senha original: 'rh123'
INSERT INTO Usuarios (email, senha, nome, cpf, data_nascimento, cargo, tipo_acesso, id_area)
VALUES
('rh@youtan.com', SHA2('rh123', 256), 'Laura RH', '11111111111', '1985-05-20', 'Especialista em RH', 'RH', 2); -- ID 2

-- GESTORES DE ÁREA (Reportando ao GG ID 1)

-- 3. GESTOR DE ÁREA (Tecnologia ID 1) - Senha original: 'ga123'
INSERT INTO Usuarios (email, senha, nome, cpf, data_nascimento, cargo, tipo_acesso, id_gestor_geral, id_area)
VALUES
('ga1@youtan.com', SHA2('ga123', 256), 'Julia Gestor Dev', '33333333333', '1990-03-01', 'Gerente de Tecnologia', 'Gestor de Area', 1, 1); -- ID 3

-- 4. GESTOR DE ÁREA (Financeiro ID 3) - Senha original: 'ga223'
INSERT INTO Usuarios (email, senha, nome, cpf, data_nascimento, cargo, tipo_acesso, id_gestor_geral, id_area)
VALUES
('ga2@youtan.com', SHA2('ga223', 256), 'Roberto Gestor Financeiro', '44444444444', '1988-07-12', 'Gerente de Contas', 'Gestor de Area', 1, 3); -- ID 4

-- COLABORADORES (Reportando aos GAs e ao GG ID 1)

-- 5. COLABORADOR TECNOLOGIA - PDI ABERTO/EM ANDAMENTO - Senha original: 'c1123'
INSERT INTO Usuarios (email, senha, nome, cpf, data_nascimento, cargo, tipo_acesso, id_gestor_de_area, id_gestor_geral, id_area)
VALUES
('colab1@youtan.com', SHA2('c1123', 256), 'Lucas Colab Tech', '55555555555', '1995-09-15', 'Desenvolvedor Pleno', 'Colaborador', 3, 1, 1); -- ID 5

-- 6. COLABORADOR RH - PDI ATRASADO/VENCIDO (DATA FIM PASSADA) - Senha original: 'c2123'
INSERT INTO Usuarios (email, senha, nome, cpf, data_nascimento, cargo, tipo_acesso, id_gestor_de_area, id_gestor_geral, id_area)
VALUES
('colab2@youtan.com', SHA2('c2123', 256), 'Mariana Colab RH', '66666666666', '1992-04-22', 'Analista de Recrutamento', 'Colaborador', 2, 1, 2); -- ID 6

-- 7. COLABORADOR FINANCEIRO - PDI CONCLUÍDO (DATA FIM PASSADA + PONTUAÇÃO MÁXIMA) - Senha original: 'c3123'
INSERT INTO Usuarios (email, senha, nome, cpf, data_nascimento, cargo, tipo_acesso, id_gestor_de_area, id_gestor_geral, id_area)
VALUES
('colab3@youtan.com', SHA2('c3123', 256), 'Felipe Colab Fin', '77777777777', '1998-01-08', 'Analista Fiscal Jr', 'Colaborador', 4, 1, 3); -- ID 7

-- 8. COLABORADOR MARKETING - SEM PDI CADASTRADO - Senha original: 'c4123'
INSERT INTO Usuarios (email, senha, nome, cpf, data_nascimento, cargo, tipo_acesso, id_gestor_de_area, id_gestor_geral, id_area)
VALUES
('colab4@youtan.com', SHA2('c4123', 256), 'Beatriz Colab Mkt', '88888888888', '1993-11-20', 'Especialista em SEO', 'Colaborador', 1, 1, 4); -- ID 8


-- ==============================
-- SKILLS DE TESTE
-- ==============================
INSERT INTO Skills (nome_skill, tipo_skill) VALUES
('Java/Spring', 'Hard Skill'),          -- ID 1
('Liderança', 'Soft Skill'),           -- ID 2
('Banco de Dados SQL', 'Hard Skill'),   -- ID 3
('Resolução de Conflitos', 'Soft Skill'), -- ID 4
('Negociação', 'Soft Skill'),           -- ID 5
('Contabilidade', 'Hard Skill');        -- ID 6


-- ==============================
-- PDIs DE EXEMPLO (3 PDIs em áreas e status diferentes)
-- ==============================

-- PDI 1: Lucas (Tech) - ABERTO/EM ANDAMENTO (Data Fim Futura)
INSERT INTO PDI (id_colaborador, data_inicio, data_fim, observacoes)
VALUES
(5, DATE_SUB(CURDATE(), INTERVAL 2 MONTH), DATE_ADD(CURDATE(), INTERVAL 3 MONTH), 'Desenvolvimento de Habilidades Técnicas e Soft Skills.'); -- ID 1

-- PDI 2: Mariana (RH) - ATRASADO/VENCIDO (Data Fim Passada + Não atingiu 100%)
INSERT INTO PDI (id_colaborador, data_inicio, data_fim, observacoes)
VALUES
(6, '2024-01-01', '2024-06-30', 'Meta de aumentar a retenção de talentos.'); -- ID 2

-- PDI 3: Felipe (Fin) - CONCLUÍDO (Data Fim Passada + Pontuação Máxima)
INSERT INTO PDI (id_colaborador, data_inicio, data_fim, observacoes)
VALUES
(7, '2024-03-01', '2024-09-30', 'Obter certificação na área fiscal e melhorar negociação.'); -- ID 3


-- ==============================
-- METAS DE EXEMPLO (SIMULANDO STATUS)
-- ==============================

-- PDI 1 (Lucas - ABERTO): Atingiu 50%
INSERT INTO Metas (id_pdi, id_skill, meta_pontuacao, pontuacao_obtida) VALUES
(1, 1, 10.0, 5.0),  -- Java/Spring
(1, 2, 5.0, 3.0);   -- Liderança

-- PDI 2 (Mariana - ATRASADO/VENCIDO): Atingiu 60%
INSERT INTO Metas (id_pdi, id_skill, meta_pontuacao, pontuacao_obtida) VALUES
(2, 4, 10.0, 6.0),  -- Resolução de Conflitos
(2, 5, 8.0, 5.0);   -- Negociação

-- PDI 3 (Felipe - CONCLUÍDO): Atingiu 100%
INSERT INTO Metas (id_pdi, id_skill, meta_pontuacao, pontuacao_obtida) VALUES
(3, 6, 10.0, 10.0), -- Contabilidade
(3, 5, 5.0, 5.0);   -- Negociação


-- ==============================
-- VERIFICAÇÃO DOS DADOS (OPCIONAL)
-- ==============================
SELECT
    U.id_usuario,
    U.nome,
    U.email,
    U.senha,
    U.tipo_acesso,
    A.nome_area AS Area,
    P.data_fim AS PDI_Data_Fim
FROM Usuarios U
JOIN Areas A ON U.id_area = A.id_area
LEFT JOIN PDI P ON U.id_usuario = P.id_colaborador
ORDER BY U.id_usuario;

ALTER TABLE Skills MODIFY COLUMN tipo_skill
ENUM('Hard Skill', 'Soft Skill', 'A Definir') NOT NULL;
