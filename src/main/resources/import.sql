insert into pessoa (nome, cpf, data_nascimento, nacionalidade) values ('Aluno 1', '111.222.333-44','2003-01-01', 'BRASILEIRA');
insert into pessoa (nome, cpf, data_nascimento, nacionalidade) values ('Aluno 2', '222.222.333-44','2004-01-01', 'JAPONESA');
insert into pessoa (nome, cpf, data_nascimento, nacionalidade) values ('Aluno 3', '333.222.333-44','2005-01-01', 'ITALIANA');
insert into pessoa (nome, cpf, data_nascimento, nacionalidade) values ('Aluno 4', '444.222.333-44','2006-01-01', 'BRASILEIRA');
insert into pessoa (nome, cpf, data_nascimento, nacionalidade) values ('Aluno 5', '555.222.333-44','2007-01-01', 'ARGENTINA');


insert into discente (id_pessoa, rm, status, nivel) values (1, 'RM1234', 'ATIVO', 'TECNOLOGO'); 
insert into discente (id_pessoa, rm, status, nivel) values (2, 'RM1235', 'INATIVO', 'TECNICO');
insert into discente (id_pessoa, rm, status, nivel) values (3, 'RM1236', 'TRANCADO', 'MBA');
insert into discente (id_pessoa, rm, status, nivel) values (4, 'RM1237', 'FORMADO', 'TECNOLOGO');
insert into discente (id_pessoa, rm, status, nivel) values (5, 'RM1237', 'EM_MOBILIDADE', 'MESTRADO');

-- usename: admin | senha: admin
insert into usuario (username, senha, img_perfil, nome_perfil) values('admin','$2a$12$h227p1QzQEB2cIW/BrzZletfr20O0lNDBMYZM0K6z5faY6bJ17kpO','https://i0.wp.com/media.tumblr.com/tumblr_lga4hf2NWD1qfdzua.jpg','Administrador FIAP');

insert into funcao (nome) values ('ADMIN');
insert into funcao (nome) values ('COORDENADOR');
insert into funcao (nome) values ('PROFESSOR');
insert into funcao (nome) values ('DISCENTE');

insert into usuario_funcao_tab (id_usuario,id_funcao) values (1,1);
