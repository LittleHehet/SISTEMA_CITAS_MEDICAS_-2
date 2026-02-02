CREATE UNIQUE INDEX IF NOT EXISTS ux_localidad_nombre ON localidad(localidad_nombre);
CREATE UNIQUE INDEX IF NOT EXISTS ux_especialidad_nombre ON especialidad(especialidad_nombre);
CREATE UNIQUE INDEX IF NOT EXISTS ux_usuarios_cedula ON usuarios(cedula);


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

DELETE FROM usuarios WHERE cedula IN (111111111, 0);


-- Usuarios por defecto (password: Hola213)
INSERT INTO usuarios (cedula, nombre, apellido, clave, perfil) VALUES
                                                                   (111111111, 'Administrador', 'Administrador', '$2a$10$sx8wxSC8/eXOy9lSOegE9.HNaGi/sZFJCV6zgbksuEVU736lp34yW', 'ROLE_ADMINISTRADOR'),
                                                                   (0,         'Anonimo',       'Anonimo',       '$2a$10$sx8wxSC8/eXOy9lSOegE9.HNaGi/sZFJCV6zgbksuEVU736lp34yW', 'ROLE_ANONIMO')
    ON CONFLICT (cedula) DO NOTHING;
