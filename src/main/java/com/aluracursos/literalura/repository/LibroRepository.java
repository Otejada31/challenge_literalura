package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface LibroRepository extends JpaRepository<Libro,Long> {
    List<Libro> findByIdioma(String idioma);

    Optional<Libro> findByTituloIgnoreCase(String titulo);

    Optional<Libro> findByTituloContainingIgnoreCase(String tituloLibro);

    //Optional<Libro> findByTituloContainsIgnoreCase(String nombreLibro);
    //@Query("SELECT l FROM libro l WHERE l.idioma = :idioma")
    //List<Libro> findByIdioma(@Param("idioma")String idioma);

    //Optional<Libro> findByTituloIgnoreCase(String titulo);
}
