package com.example.sistema_citas.service;

import com.example.sistema_citas.data.*;
import com.example.sistema_citas.logic.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Transactional
@org.springframework.stereotype.Service("service")
public class Service {
  @Autowired
     private UsuarioRepository usuarioRepository;

  @Autowired
  private MedicoRepository medicoRepository;

  @Autowired
  private EspecialidadRepository especialidadRepository;

  @Autowired
  private LocalidadRepository localidadRepository;

  @Autowired
  private FotoRepository fotoRepository;

  @Autowired
  private CitaRepository citaRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /* Usario
  ------------------------------------------*/

      // Método para encontrar un usuario por su cédula
      public Optional<Usuario> findByCedula(Integer cedula) {
      return usuarioRepository.findByCedula(cedula);
    }

    // Método para encontrar todos los usuarios de un perfil específico
    public List<Usuario> findByPerfil(String perfil) {
        return usuarioRepository.findByPerfil(perfil);
      }

    // Método para guardar un nuevo usuario
    public Usuario saveUsuario(Usuario usuario) {
      usuario.setClave(passwordEncoder.encode(usuario.getClave()));
      return usuarioRepository.save(usuario);
    }


  public UserDetails load(String cedula) throws UsernameNotFoundException {
    Integer cedulaInt = Integer.parseInt(cedula);
    Optional<Usuario> userOpt = usuarioRepository.findByCedula(cedulaInt);
    if (userOpt.isEmpty()) {
      System.out.println("Usuario no encontrado para cedula: " + cedula);
      throw new UsernameNotFoundException("Usuario no encontrado: " + cedula);
    }
    Usuario user = userOpt.get();
    System.out.println("Usuario encontrado: " + user.getNombre() + ", perfil: " + user.getPerfil());
    return new org.springframework.security.core.userdetails.User(
            String.valueOf(user.getCedula()),
            user.getClave(),
            Collections.singletonList(new SimpleGrantedAuthority(user.getPerfil()))
    );
  }



    /* Medico
   ------------------------------------------*/

    public Optional<Medico> findMedicoById(Integer id) {
      return medicoRepository.findById2(id);
    }

    public List<Medico> findAllMedicosEyL() {
      return medicoRepository.findAllMedicosConUsuariosEspecialidadYLocalidadYEstado();
    }

  public void updateMedico(Medico medico, Foto nuevaFoto) {
    Optional<Medico> medicoExistenteOpt = medicoRepository.findById(medico.getId());

    if (medicoExistenteOpt.isPresent()) {
      Medico medicoExistente = medicoExistenteOpt.get();

      // Actualiza los otros campos...
      medicoExistente.setEspecialidad(medico.getEspecialidad());
      medicoExistente.setCosto(medico.getCosto());
      medicoExistente.setLocalidad(medico.getLocalidad());
      medicoExistente.setHorario(medico.getHorario());
      medicoExistente.setFrecuenciaCitas(medico.getFrecuenciaCitas());
      medicoExistente.setNota(medico.getNota());
      if (nuevaFoto != null && nuevaFoto.getImagen() != null) {
        medicoExistente.setFoto(nuevaFoto);  // <-- Aquí está bien
      }
      medicoRepository.save(medicoExistente);
    }
  }

    public  List<Medico> findMedicobyEspecialidad(Especialidad especialidadId){
      return medicoRepository.findByEspecialidadAndEstado(especialidadId);
    }
    public  List<Medico> findMedicobyLocalidad(Localidad localidadId){
      return  medicoRepository.findByLocalidadAndEstado(localidadId);
    }
    public List<Medico> findMedicobyLocalidadAndEspecialidad(Especialidad especialidadId,Localidad localidadId){
      //return medicoRepository.findByEspecialidadAndLocalidad(especialidadId,localidadId);
      return medicoRepository.findMedicosByEstadoAndEspecialidadAndLocalidad(especialidadId,localidadId);
    }

    public List<Especialidad> getAllEspecialidades() {
      return especialidadRepository.findAll();
    }

    public List<Localidad> getAllLocalidades() {
      return localidadRepository.findAll();
    }

    public void saveMedicoByCedula(Integer cedula) {
      medicoRepository.saveMedicoWithCedula(cedula);
    }

    // Método para actualizar el estado del médico
    public void updateMedicoEstado(Integer cedula, String estado) {
      // Verificamos si el médico existe por su cédula
      Optional<Medico> medicoOpt = medicoRepository.findById(cedula);
      if (medicoOpt.isPresent()) {
        Medico medico = medicoOpt.get();
        medico.setEstado(estado); // Actualizamos el estado
        medicoRepository.save(medico); // Guardamos el médico actualizado
      }
    }

    public Optional<Medico> findMedicobyCedula(Integer cedula) {
      return medicoRepository.findByCedula(cedula);
    }
  public Optional<Medico> findMedicoByUsuario(Usuario usuario) {
    return medicoRepository.findByUsuario(usuario);
  }


  /*---------foto-----------------*/
  public Foto create(Foto image) {
      return fotoRepository.save(image);
    }



  /*--------CITAS--------------------*/

  public void saveCita(Cita cita) {
    citaRepository.save(cita);
  }


  public List<Cita> findAllCitasbyUser(Integer id) {
    return citaRepository.findCitasByUsuarioOrdenadas(id);
  }

  public List<Cita> findAllCitasbyMedico(Integer id) {
    return citaRepository.findCitasByMedicoOrdenadas(id);
  }

  public Optional<Cita> findCitaById(Integer id) {
    return citaRepository.findById(id);
  }

  public void cambiarEstadoCita(Integer id, String estado) {
    Cita cita = citaRepository.findById(id).orElseThrow();
    cita.setEstado(estado);
    citaRepository.save(cita);
  }

  public Cita findCitaByMedicoHorario(Integer medico_id,String horaInicio,
                                      String horaFin, String  dia , LocalDate fechaHora) {
    return citaRepository.findCitaByMedicoAndHorario(medico_id,horaInicio,horaFin,dia ,fechaHora );
  }


  public int cancelarCitasPasadas() {
    return citaRepository.cancelarCitasPasadas();
  }


}
