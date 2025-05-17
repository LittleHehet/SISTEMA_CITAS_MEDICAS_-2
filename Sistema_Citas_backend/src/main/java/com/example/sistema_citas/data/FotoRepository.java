package com.example.sistema_citas.data;
import org.springframework.data.repository.CrudRepository;
import com.example.sistema_citas.logic.Foto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FotoRepository extends JpaRepository<Foto, Long>{
}
