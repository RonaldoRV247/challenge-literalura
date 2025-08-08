package com.example.literalura_challenge.repository;

import com.example.literalura_challenge.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    Optional<Libro> findByTituloContainsIgnoreCase(String titulo);

    List<Libro> findByIdioma(String idioma);

    @Query("SELECT l FROM Libro l ORDER BY l.numeroDeDescargas DESC")
    List<Libro> findTop10ByOrderByNumeroDeDescargasDesc();

    // Contar libros por idioma específico
    Long countByIdioma(String idioma);

    // Obtener todos los idiomas disponibles
    @Query("SELECT DISTINCT l.idioma FROM Libro l ORDER BY l.idioma")
    List<String> findDistinctIdiomas();

    // Estadísticas por idioma
    @Query("SELECT l.idioma, COUNT(l) FROM Libro l GROUP BY l.idioma ORDER BY COUNT(l) DESC")
    List<Object[]> obtenerEstadisticasPorIdioma();
}
