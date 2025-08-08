package com.example.literalura_challenge.repository;

import com.example.literalura_challenge.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {

    @Query("SELECT a FROM Autor a WHERE a.fechaDeNacimiento <= :ano AND (a.fechaDeFallecimiento > :ano OR a.fechaDeFallecimiento IS NULL)")
    List<Autor> findAutoresVivosEnAno(String ano);
}
