package com.example.literalura_challenge.repository;

import com.example.literalura_challenge.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {

    Optional<Autor> findByNombreContainsIgnoreCase(String nombre);

    @Query("SELECT a FROM Autor a WHERE " +
           "(a.fechaDeNacimiento IS NULL OR CAST(a.fechaDeNacimiento AS integer) <= :ano) AND " +
           "(a.fechaDeFallecimiento IS NULL OR CAST(a.fechaDeFallecimiento AS integer) > :ano)")
    List<Autor> findAutoresVivosEnAno(@Param("ano") Integer ano);
}
