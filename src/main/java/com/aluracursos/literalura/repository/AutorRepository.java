package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AutorRepository extends JpaRepository<Autor,Long> {

    @Query("SELECT a FROM Autor a LEFT JOIN FETCH a.libros")
    List<Autor> findAllWithLibros();
    @Query("SELECT a FROM Autor a LEFT JOIN FETCH a.libros WHERE(a.fechaFallecimiento IS NULL OR a.fechaFallecimiento > :anio)AND a.fechaNacimiento <= :anio")
    List<Autor> findAutoresVivosEnAnioConLibros(@Param("anio") int anio);

    Optional<Autor> findByNombre(String nombre);

    Optional<Autor> findByNombreIgnoreCase(String nombre);


    /*Optional<Autor> findFirstByNombreContainsIgnoreCase(String escritor);

    List<Autor> findByFechaNacimientoLessThanOrFechaFallecimientoGreaterThanEqual(int añoBuscado, int añoBuscado1);
    @Query
    List<Autor> findAllWithLibros();*/
}
