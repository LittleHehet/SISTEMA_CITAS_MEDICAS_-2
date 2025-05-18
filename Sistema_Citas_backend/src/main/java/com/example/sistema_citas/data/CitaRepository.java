package com.example.sistema_citas.data;

import com.example.sistema_citas.logic.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    //antes: extends JpaRepository<Cita, Long>

    @Query("SELECT c FROM Cita c JOIN FETCH c.medico m JOIN FETCH m.usuario WHERE c.usuario.id = :usuarioId ORDER BY c.fechaHora DESC")
    List<Cita> findCitasByUsuarioOrdenadas(@Param("usuarioId") Integer usuarioId);

    @Query("SELECT c FROM Cita c JOIN FETCH c.medico m JOIN FETCH m.usuario u WHERE m.id = :medicoId ORDER BY c.fechaHora DESC")
    List<Cita> findCitasByMedicoOrdenadas(@Param("medicoId") Integer medicoId);


    @Query("SELECT c FROM Cita c WHERE c.medico.id = :medicoId " +
            "AND c.horainicio = :horainicio " +
            "AND c.horafinal = :horafinal " +
            "AND c.dia = :dia " +
            "AND c.fechaHora = :fechaHora")
    Cita findCitaByMedicoAndHorario(@Param("medicoId") Integer medicoId,
                                    @Param("horainicio") String horainicio,
                                    @Param("horafinal") String horafinal,
                                    @Param("dia") String dia,
                                    @Param("fechaHora") LocalDate fechaHora);

    @Modifying
    @Query("UPDATE Cita c SET c.estado = 'cancelada' " +
            "WHERE DATE(c.fechaHora) < DATE(CURRENT_DATE) AND c.estado = 'pendiente'")
    int cancelarCitasPasadas();


}
