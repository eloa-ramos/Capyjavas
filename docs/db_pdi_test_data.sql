-- ESTE CÓDIGO DEVE SER COLOCADO AO FINAL
-- DO CÓDIGO DO ARQUIVO  db_pdi.sql  CASO
-- QUEIRA TESTAR ALGUMAS FUNCIONALIDADES.

USE db_pdi;

-- ==============================
-- USUÁRIOS
-- ==============================
-- Senhas simples para teste (plaintext neste exemplo, mas o banco espera case-sensitive)
-- Formato: email / senha / tipoAcesso

-- RH
INSERT INTO Usuarios (email, senha, nome, cpf, data_nascimento, cargo, tipo_acesso)
VALUES
('rh@empresa.com', '123RH', 'Maria RH', '11111111111', '1980-01-01', 'RH Manager', 'RH');

-- Gestor Geral
INSERT INTO Usuarios (email, senha, nome, cpf, data_nascimento, cargo, tipo_acesso)
VALUES
('gg@empresa.com', '123GG', 'João Gestor Geral', '22222222222', '1975-05-10', 'Gestor Geral', 'Gestor Geral');

-- Gestores de Área
INSERT INTO Usuarios (email, senha, nome, cpf, data_nascimento, cargo, tipo_acesso, id_gestor_geral)
VALUES
('ga1@empresa.com', '123GA1', 'Ana Gestor Area 1', '33333333333', '1982-02-15', 'Gestor de Área', 'Gestor de Area', 2),
('ga2@empresa.com', '123GA2', 'Carlos Gestor Area 2', '44444444444', '1983-03-20', 'Gestor de Área', 'Gestor de Area', 2);

-- Colaboradores (ligados aos gestores de área)
INSERT INTO Usuarios (email, senha, nome, cpf, data_nascimento, cargo, tipo_acesso, id_gestor_de_area, id_gestor_geral)
VALUES
('colab1@empresa.com', '123C1', 'Pedro Colab 1', '55555555555', '1990-06-01', 'Desenvolvedor', 'Colaborador', 3, 2),
('colab2@empresa.com', '123C2', 'Julia Colab 2', '66666666666', '1992-07-15', 'Analista', 'Colaborador', 3, 2),
('colab3@empresa.com', '123C3', 'Rafael Colab 3', '77777777777', '1988-08-10', 'Desenvolvedor', 'Colaborador', 4, 2),
('colab4@empresa.com', '123C4', 'Beatriz Colab 4', '88888888888', '1991-09-25', 'Analista', 'Colaborador', 4, 2);

-- ==============================
-- SKILLS
-- ==============================
INSERT INTO Skills (nome_skill, tipo_skill) VALUES
('Java', 'Hard Skill'),
('SQL', 'Hard Skill'),
('Comunicação', 'Soft Skill'),
('Gestão de Tempo', 'Soft Skill');

-- ==============================
-- PDIs
-- ==============================
INSERT INTO PDI (id_colaborador, data_inicio, data_fim, observacoes) VALUES
(5, '2025-01-01', '2025-03-31', 'PDI Pedro 1'),
(6, '2025-01-15', '2025-04-15', 'PDI Julia 2'),
(7, '2025-02-01', '2025-05-01', 'PDI Rafael 3'),
(8, '2025-02-15', '2025-05-15', 'PDI Beatriz 4');

-- ==============================
-- METAS
-- ==============================
-- Pedro Colab 1
INSERT INTO Metas (id_pdi, id_skill, meta_pontuacao, pontuacao_obtida) VALUES
(1, 1, 10.0, 7.0),
(1, 3, 5.0, 5.0);

-- Julia Colab 2
INSERT INTO Metas (id_pdi, id_skill, meta_pontuacao, pontuacao_obtida) VALUES
(2, 2, 8.0, 8.0),
(2, 4, 5.0, 3.0);

-- Rafael Colab 3
INSERT INTO Metas (id_pdi, id_skill, meta_pontuacao, pontuacao_obtida) VALUES
(3, 1, 10.0, 10.0),
(3, 3, 5.0, 4.0);

-- Beatriz Colab 4
INSERT INTO Metas (id_pdi, id_skill, meta_pontuacao, pontuacao_obtida) VALUES
(4, 2, 8.0, 6.0),
(4, 4, 5.0, 5.0);

-- ==============================
-- TESTE DE SELEÇÃO (para verificar dados)
-- ==============================
SELECT * FROM Usuarios;
SELECT * FROM PDI;
SELECT * FROM Metas;
SELECT * FROM Skills;
