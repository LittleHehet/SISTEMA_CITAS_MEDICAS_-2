-- Selección de la base de datos
USE Sistema_citas;

-- =================================
-- Dropeamos Tablas

DROP TABLE IF EXISTS cita;
DROP TABLE IF EXISTS medicos;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS localidad;
DROP TABLE IF EXISTS especialidad;
DROP TABLE IF EXISTS foto;
-- =================================
-- Creamos Tablas
CREATE TABLE localidad(
	localidad_id 	 int 		 NOT NULL AUTO_INCREMENT,
	localidad_nombre varchar(20) DEFAULT NULL,
	CONSTRAINT localidad_pk PRIMARY KEY(localidad_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE especialidad(
	especialidad_id     INT         NOT NULL AUTO_INCREMENT,
	especialidad_nombre varchar(50) DEFAULT NULL,
	CONSTRAINT especialidad_pk PRIMARY KEY(especialidad_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE usuarios(
    usuarios_id INT 		 NOT NULL AUTO_INCREMENT,
    cedula 		INT 		 NOT NULL,
    nombre 		VARCHAR(30)  NOT NULL,
    apellido 	VARCHAR(30)  NOT NULL,
    clave 		VARCHAR(100) NOT NULL,
    perfil   	ENUM('ROLE_PACIENTE', 'ROLE_MEDICO', 'ROLE_ADMINISTRADOR', 'ROLE_ANONIMO') NOT NULL,
    CONSTRAINT usuarios_pk PRIMARY KEY(usuarios_id),
	CONSTRAINT cedula_unique UNIQUE (cedula) 
);

CREATE TABLE foto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    imagen LONGBLOB NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE medicos(
    medico_id         INT         	 NOT NULL,
    especialidad      INT   		 NULL,
    costo             DECIMAL(10,2)  NULL,
    localidad         INT   		 NULL,
	horario 		  VARCHAR(100)	 NULL,
    frecuencia_citas  INT            NULL,
    nota              TEXT           NULL,
	foto_id           BIGINT         NULL,
	estado 			  ENUM('pendiente', 'aprobado') NOT NULL, 
    CONSTRAINT medico_pk 		 PRIMARY KEY(medico_id),
    CONSTRAINT medico_usua_fk 	 FOREIGN KEY(medico_id)    REFERENCES usuarios(usuarios_id),
	CONSTRAINT medico_local_fk2  FOREIGN KEY(localidad)    REFERENCES localidad(localidad_id),
	CONSTRAINT medico_especi_fk3 FOREIGN KEY(especialidad) REFERENCES especialidad(especialidad_id),
	CONSTRAINT medico_foto_fk4    FOREIGN KEY(foto_id)      REFERENCES foto(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE cita(
    codigo_cita     INT        NOT NULL AUTO_INCREMENT,
    usuario_id      INT        NOT NULL,
    medico_id       INT        NOT NULL,
    horainicio		VARCHAR(20)NOT NULL,
	horafinal       VARCHAR(20)NOT NULL,
	inicio			INT 	   NOT NULL,
	fin 			INT 	   NOT NULL,
	dia				VARCHAR(15)NOT NULL,
    nota            TEXT       NULL,
    estado 			ENUM('pendiente', 'confirmada', 'cancelada', 'completada') NOT NULL, 
	fecha_hora     DATETIME    NOT NULL,
    
    CONSTRAINT cita_pk      PRIMARY KEY(codigo_cita),
    CONSTRAINT cita_pac_fk1 FOREIGN KEY(usuario_id) REFERENCES usuarios(usuarios_id),
    CONSTRAINT cita_med_fk2 FOREIGN KEY(medico_id) 	 REFERENCES medicos(medico_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =================================
-- Insertamos datos

-- Insertar localidad
INSERT INTO localidad (localidad_nombre) VALUES
('San Jose'),
('Alajuela'),
('Cartago'),
('Heredia'),
('Guanacaste'),
('Puntarenas'),
('Limon');


-- Insertar especialidad
INSERT INTO especialidad (especialidad_nombre) VALUES
('Alergologia'),
('Anestesiologia y reanimacion'),
('Aparato digestivo'),
('Cardiologia'),
('Endocrinologia y nutricion'),
('Geriatria'),
('Hematologia y hemoterapia'),
('Medicina de la educacion fisica y del deporte'),
('Medicina espacial'),
('Medicina intensiva'),
('Medicina interna'),
('Medicina legal y forense'),
('Medicina preventiva y salud publica'),
('Medicina del trabajo'),
('Nefrologia'),
('Neumologia'),
('Neurologia'),
('Neurofisiologia Clinica'),
('Oncologia medica'),
('Oncologia radioterapica'),
('Pediatria'),
('Psiquiatria'),
('Rehabilitacion'),
('Reumatologia'),
('Medicina familiar y comunitaria'),
('Biomedicina');


INSERT INTO usuarios (cedula, nombre, apellido, clave, perfil) VALUES
(402580003, 'Kendra', 'Artavia', '$2b$12$a0NvQ8Gin2hFGcRm4TTlquNpeMwuYXDiw5RzJBPwwsDrLdHvGQnI.', 'ROLE_PACIENTE'),
(402580319, 'Alexia', 'Alvarado','$2b$12$a0NvQ8Gin2hFGcRm4TTlquNpeMwuYXDiw5RzJBPwwsDrLdHvGQnI.' , 'ROLE_MEDICO'),
(118070563, 'William', 'Rodriguez', '$2b$12$a0NvQ8Gin2hFGcRm4TTlquNpeMwuYXDiw5RzJBPwwsDrLdHvGQnI.', 'ROLE_MEDICO'),
(111111111, 'Administrador', 'Administrador', '$2b$12$a0NvQ8Gin2hFGcRm4TTlquNpeMwuYXDiw5RzJBPwwsDrLdHvGQnI.', 'ROLE_ADMINISTRADOR'),
(000000000, 'Anonimo', 'Anonimo', '$2b$12$a0NvQ8Gin2hFGcRm4TTlquNpeMwuYXDiw5RzJBPwwsDrLdHvGQnI.', 'ROLE_ANONIMO'),
(100000000, 'Paciente', 'Paciente','$2b$12$a0NvQ8Gin2hFGcRm4TTlquNpeMwuYXDiw5RzJBPwwsDrLdHvGQnI.' , 'ROLE_PACIENTE'),
(200000000, 'Medico', 'Medico','$2b$12$a0NvQ8Gin2hFGcRm4TTlquNpeMwuYXDiw5RzJBPwwsDrLdHvGQnI.' , 'ROLE_MEDICO');

-- Insertar médicos
INSERT INTO medicos (medico_id, especialidad, costo, localidad, horario, frecuencia_citas, nota , estado ) 
VALUES
((SELECT usuarios_id FROM usuarios WHERE nombre = 'Alexia' LIMIT 1), 
 (SELECT especialidad_id FROM especialidad WHERE especialidad_nombre = 'Cardiologia'),
 1000.00,(SELECT localidad_id FROM localidad WHERE localidad_nombre = 'San Jose')
 ,'7-10,1-6;7-10,1-6;7-10,1-6;7-10,1-6;7-10,1-6;7-10,1-6;7-10,1-6;',30,'Experto en cardiologia' , 'pendiente'),
((SELECT usuarios_id FROM usuarios WHERE nombre = 'William' LIMIT 1), 
 (SELECT especialidad_id FROM especialidad WHERE especialidad_nombre = 'Geriatria'),
 2000.00, (SELECT localidad_id FROM localidad WHERE localidad_nombre = 'Heredia')
 ,'8-11,2-7;8-11,2-7;8-11,2-7;8-11,2-7;8-11,2-7;8-11,2-7;8-11,2-7;',20,'Especialista en piel','aprobado'  ),
 ((SELECT usuarios_id FROM usuarios WHERE nombre = 'Medico' LIMIT 1), 
 (SELECT especialidad_id FROM especialidad WHERE especialidad_nombre = 'Biomedicina'),
 5000.00, (SELECT localidad_id FROM localidad WHERE localidad_nombre = 'Limon'),
 '6-10,1-5;6-10,1-5;6-10,1-5;6-10,1-5;6-10,1-5;6-10,1-5;6-10,1-5;',20,'aAaaa', 'aprobado' );



	
-- =================================
-- Select de las tablas

-- Seleccionar todos los usuarios
SELECT * FROM usuarios;

-- Seleccionar todos los médicos
SELECT * FROM medicos;

-- Seleccionar todas las citas
SELECT * FROM cita;

-- =================================
-- Final