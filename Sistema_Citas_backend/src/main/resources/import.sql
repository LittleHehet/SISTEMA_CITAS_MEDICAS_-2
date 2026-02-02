-- Localidades (Si el nombre ya existe, no hace nada)
INSERT INTO localidad (localidad_nombre) VALUES
                                             ('San Jose'), ('Alajuela'), ('Cartago'), ('Heredia'), ('Guanacaste'), ('Puntarenas'), ('Limon')
    ON CONFLICT (localidad_nombre) DO NOTHING;

-- Especialidades
INSERT INTO especialidad (especialidad_nombre) VALUES
                                                   ('Alergologia'), ('Anestesiologia y reanimacion'), ('Aparato digestivo'), ('Cardiologia'),
                                                   ('Endocrinologia y nutricion'), ('Geriatria'), ('Hematologia y hemoterapia'),
                                                   ('Medicina de la educacion fisica y del deporte'), ('Medicina espacial'), ('Medicina intensiva'),
                                                   ('Medicina interna'), ('Medicina legal y forense'), ('Medicina preventiva y salud publica'),
                                                   ('Medicina del trabajo'), ('Nefrologia'), ('Neumologia'), ('Neurologia'), ('Neurofisiologia Clinica'),
                                                   ('Oncologia medica'), ('Oncologia radioterapica'), ('Pediatria'), ('Psiquiatria'),
                                                   ('Rehabilitacion'), ('Reumatologia'), ('Medicina familiar y comunitaria'), ('Biomedicina')
    ON CONFLICT (especialidad_nombre) DO NOTHING;

-- Usuarios por defecto
INSERT INTO usuarios (cedula, nombre, apellido, clave, perfil) VALUES
                                                                   (111111111, 'Administrador', 'Administrador', '$2b$12$a0NvQ8Gin2hFGcRm4TTlquNpeMwuYXDiw5RzJBPwwsDrLdHvGQnI.', 'ROLE_ADMINISTRADOR'),
                                                                   (000000000, 'Anonimo', 'Anonimo', '$2b$12$a0NvQ8Gin2hFGcRm4TTlquNpeMwuYXDiw5RzJBPwwsDrLdHvGQnI.', 'ROLE_ANONIMO')
    ON CONFLICT (cedula) DO NOTHING;