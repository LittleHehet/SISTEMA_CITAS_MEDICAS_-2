package com.example.sistema_citas.service;

import com.example.sistema_citas.data.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.sistema_citas.logic.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

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
  private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

  /* Usario
  ------------------------------------------*/

      // Método para encontrar un usuario por su cédula
      public Optional<Usuario> findByCedula(Integer cedula) {
      return usuarioRepository.findByCedula(cedula);
    }


      // Método para encontrar un usuario por su cédula y clave
      public Optional<Usuario> findByIdAndClave(Integer cedula, String clave) {
        return usuarioRepository.findByIdAndClave(cedula, clave);
      }

      // Método para encontrar todos los usuarios de un perfil específico
      public List<Usuario> findByPerfil(String perfil) {
        return usuarioRepository.findByPerfil(perfil);
      }

      // Método para encontrar todos los usuarios
      public List<Usuario> findAll(){return usuarioRepository.findAll();}

     public Optional<Usuario> findbyId(Integer id)
     {
       return usuarioRepository.findById(id);
     }

      // Método para guardar un nuevo usuario
      public Usuario saveUsuario(Usuario usuario) {
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        return usuarioRepository.save(usuario);
      }


      // Método para login
      public Optional<Usuario> login(Integer cedula, String clave) {
        Optional<Usuario> usuario = usuarioRepository.findByIdAndClave(cedula, clave);
        return usuario.isPresent() ? usuario : Optional.empty();
      }


    /* Medico
   ------------------------------------------*/

    public Optional<Medico> findMedicoById(Integer id) {
      return medicoRepository.findById2(id);
    }

    public void updateMedico(Medico medico) {
      medicoRepository.save(medico);
    }

      public List<Medico> findAllMedicos() {
        return medicoRepository.findAll();
      }

      public List<Medico> findAllMedicosEyL() {
      //return medicoRepository.findAllMedicosConUsuariosEspecialidadYLocalidad();
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

  public List<Foto> viewAll() {
    return (List<Foto>) fotoRepository.findAll();
  }

  public Foto viewById(long id) {
    return fotoRepository.findById(id).get();
  }


  /*--------horarios --------------------*/
  //public boolean isHorarioOcupado(Medico medico, String dia, int inicio, int fin);


  /*--------CITAS--------------------*/

  public void saveCita(Cita cita) {
    citaRepository.save(cita);
  }

  public List<Cita> findAllCitas() {
     return citaRepository.findAll();
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

  public Cita findCitaByMedicoHorario(Integer medico_id,String horaInicio, String horaFin, String dia) {
    return citaRepository.findCitaByMedicoAndHorario(medico_id,horaInicio,horaFin,dia);
  }

  public int cancelarCitasPasadas() {
    return citaRepository.cancelarCitasPasadas();
  }


} //fin service
