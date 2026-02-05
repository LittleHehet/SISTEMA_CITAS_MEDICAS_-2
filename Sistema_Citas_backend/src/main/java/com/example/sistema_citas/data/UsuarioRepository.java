package com.example.sistema_citas.data;

import com.example.sistema_citas.logic.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @Query("SELECT u FROM Usuario u WHERE u.cedula = :cedula")
    Optional<Usuario> findByCedula(@Param("cedula") Integer cedula);

    @Query("SELECT u FROM Usuario u WHERE u.cedula = :cedula AND u.clave = :clave")
    Optional<Usuario> findByIdAndClave(@Param("cedula") Integer cedula, @Param("clave") String clave);

    // Buscar todos los usuarios de un perfil específico
    List<Usuario> findByPerfil(String perfil);

    Optional<Usuario> findByGoogleSub(String googleSub);
    Optional<Usuario> findByEmail(String email);


}

