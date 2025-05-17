package com.example.sistema_citas.data;

import com.example.sistema_citas.logic.Especialidad;
import com.example.sistema_citas.logic.Localidad;
import com.example.sistema_citas.logic.Medico;
import com.example.sistema_citas.logic.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico,Integer> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO medicos (medico_id, especialidad, costo, localidad, horario, frecuencia_citas, nota, foto_id, estado) " +
            "SELECT u.usuarios_id, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'pendiente' " +
            "FROM usuarios u " +
            "WHERE u.cedula = :cedula AND NOT EXISTS (SELECT 1 FROM medicos m WHERE m.medico_id = u.usuarios_id)",
            nativeQuery = true)
    void saveMedicoWithCedula(@Param("cedula") Integer cedula);

    // Consulta para obtener todos los médicos con sus usuarios, especialidades y localidades asociadas
    @Modifying
    @Transactional
    @Query("SELECT m FROM Medico m " +
            "JOIN FETCH m.usuario u " +
            "JOIN FETCH m.especialidad e " +
            "JOIN FETCH m.localidad l " +
            "WHERE m.estado = 'aprobado'")
    List<Medico> findAllMedicosConUsuariosEspecialidadYLocalidadYEstado();
//    @Query("SELECT m FROM Medico m " +
//            "JOIN FETCH m.usuario u " +
//            "JOIN FETCH m.especialidad e " +
//            "JOIN FETCH m.localidad l")
//    List<Medico> findAllMedicosConUsuariosEspecialidadYLocalidad();


    // Filtra médicos por especialidad y localidad usando las entidades completas
    //List<Medico> findByEspecialidadAndLocalidad(Especialidad especialidad, Localidad localidad);

    // Filtra médicos por especialidad
    @Query("SELECT m FROM Medico m " +
            "JOIN FETCH m.usuario u " +
            "JOIN FETCH m.especialidad e " +
            "JOIN FETCH m.localidad l " +
            "WHERE m.estado = 'aprobado' " +
            "AND (:especialidad IS NULL OR e = :especialidad)")
    List<Medico> findByEspecialidadAndEstado(Especialidad especialidad);

    // Filtra médicos por localidad
//    @Modifying
//    @Transactional
//    @Query("SELECT m FROM Medico m " +
//            "JOIN FETCH m.usuario u " +
//            "JOIN FETCH m.especialidad e " +
//            "JOIN FETCH m.localidad l " +
//            "WHERE m.estado = 'aprobado'")
//    List<Medico> findByLocalidadAndEstado(Localidad localidad,String estado);
    @Modifying
    @Transactional
    @Query("SELECT m FROM Medico m " +
            "JOIN FETCH m.usuario u " +
            "JOIN FETCH m.especialidad e " +
            "JOIN FETCH m.localidad l " +
            "WHERE m.estado = 'aprobado' " +
            "AND (:localidad IS NULL OR l = :localidad)")
    List<Medico> findByLocalidadAndEstado(@Param("localidad") Localidad localidad);

    //Filtrar medicos por especialidad, localidad y estado
//    @Modifying
//    @Transactional
//    @Query("SELECT m FROM Medico m " +
//            "JOIN FETCH m.usuario u " +
//            "JOIN FETCH m.especialidad e " +
//            "JOIN FETCH m.localidad l " +
//            "WHERE m.estado = 'aprobado'")
//    List<Medico> findMedicosByEstadoAndEspecialidadAndLocalidad(String estado, Especialidad especialidad, Localidad localidad);
    @Modifying
    @Transactional
    @Query("SELECT m FROM Medico m " +
            "JOIN FETCH m.usuario u " +
            "JOIN FETCH m.especialidad e " +
            "JOIN FETCH m.localidad l " +
            "WHERE m.estado = 'aprobado' " +
            "AND (:especialidad IS NULL OR e = :especialidad) " +
            "AND (:localidad IS NULL OR l = :localidad)")
    List<Medico> findMedicosByEstadoAndEspecialidadAndLocalidad(@Param("especialidad") Especialidad especialidad, @Param("localidad") Localidad localidad);



    @Transactional
    @Query("SELECT m FROM Medico m JOIN m.usuario u WHERE u.cedula = :cedula")
    Optional<Medico> findByCedula(@Param("cedula") Integer cedula);

    @Transactional
    @Query("SELECT m FROM Medico m  WHERE m.id = :id")
    Optional<Medico> findById2(@Param("id") Integer id);

    Optional<Medico> findByUsuario(Usuario usuario);

}
