create database db_pdi;
use db_pdi;

CREATE TABLE `Usuarios` (
  `id_usuario` INT NOT NULL AUTO_INCREMENT,

  -- CAMPOS ADICIONADOS DO MODELO JAVA
  `email` VARCHAR(255) NOT NULL UNIQUE, -- O email deve ser único

  -- *** MUDANÇA AQUI: Adiciona a COLLATION binária para case-sensitive ***
  `senha` VARCHAR(255) NOT NULL COLLATE utf8mb4_bin,

  -- CAMPOS EXISTENTES
  `nome` VARCHAR(255) NOT NULL,
  `cpf` VARCHAR(14) NOT NULL UNIQUE,
  `data_nascimento` DATE,
  `cargo` VARCHAR(100),
  `experiencia` TEXT,
  `observacoes` TEXT,
  `id_gestor_de_area` INT,
  `id_gestor_geral` INT,
  `tipo_acesso` ENUM('RH', 'Gestor Geral', 'Gestor de Area', 'Colaborador') NOT NULL,

  PRIMARY KEY (`id_usuario`),

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

-- Tabela de Skills (Habilidades)
CREATE TABLE `Skills` (
  `id_skill` INT NOT NULL AUTO_INCREMENT,
  `nome_skill` VARCHAR(255) NOT NULL,
  `tipo_skill` ENUM('Hard Skill', 'Soft Skill') NOT NULL,
  PRIMARY KEY (`id_skill`)
);

-- Tabela de PDI (Plano de Desenvolvimento Individual)
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

-- Tabela de Metas (Avaliações por PDI e Skill)
CREATE TABLE `Metas` (
  `id_meta` INT NOT NULL AUTO_INCREMENT,
  `id_pdi` INT NOT NULL,
  `id_skill` INT NOT NULL,
  `meta_pontuacao` DECIMAL(5, 2) NOT NULL,
  `pontuacao_obtida` DECIMAL(5, 2) NOT NULL,
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

-- Tabela de Anexos
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