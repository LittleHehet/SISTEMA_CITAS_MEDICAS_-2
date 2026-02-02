DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_localidad_nombre') THEN
ALTER TABLE localidad ADD CONSTRAINT uq_localidad_nombre UNIQUE (localidad_nombre);
END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_especialidad_nombre') THEN
ALTER TABLE especialidad ADD CONSTRAINT uq_especialidad_nombre UNIQUE (especialidad_nombre);
END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_usuarios_cedula') THEN
ALTER TABLE usuarios ADD CONSTRAINT uq_usuarios_cedula UNIQUE (cedula);
END IF;
END$$;

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

-- Usuarios por defecto (password: Hola213)
INSERT INTO usuarios (cedula, nombre, apellido, clave, perfil) VALUES
                                                                   (111111111, 'Administrador', 'Administrador', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36YvY0oG5c1yV5qW8kZpG9y', 'ROLE_ADMINISTRADOR'),
                                                                   (0,         'Anonimo',       'Anonimo',       '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36YvY0oG5c1yV5qW8kZpG9y', 'ROLE_ANONIMO')
    ON CONFLICT (cedula) DO NOTHING;
