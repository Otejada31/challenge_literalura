package com.aluracursos.literalura.principal;

//import com.aluracursos.literalura.model.Datos;
//import com.aluracursos.literalura.model.DatosLibro;
import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final Scanner teclado = new Scanner(System.in);

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final List<Libro> datosLibro = new ArrayList<>();
    private List<Autor> autores;
    private List<DatosLibro> datosLibros;
    private List<DatosAutor> datosAutor;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }


    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 -Buscar libro en la web  
                    2 -Mostrar todos los libros
                    3 -Mostrar todos los autores registrados
                    4 -Mostrar todos los autores vivos a partir del año
                    0 -Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1 -> buscarLibroWeb();
                case 2 -> mostrarLibros();
                case 3 -> listarAutoresRegistrados();
                case 4 -> buscarAutoresPorFecha();
                case 0 -> {
                    System.out.println("Cerrando la aplicación...");
                    break;
                }
                default -> System.out.println("Opción inválida");
            }
        }


    }

    private DatosLibro getDatosLibro(){

        System.out.print("Escribe el nombre del libro que quieres buscar ");
        var libroBuscado = teclado.nextLine();

        String json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + libroBuscado.replace(" ", "+"));
        System.out.println("DATOS Json----> " + json);
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        return datosBusqueda.resultadoLibros().stream()
                .filter(datosLibros -> datosLibros.titulo().toUpperCase().contains(libroBuscado.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

    private void buscarLibroWeb() {
        DatosLibro libros = getDatosLibro();
        System.out.println("DATOS DE LIBROS ---->" + libros);
        if (libros == null) {
            System.out.println("Regresando al menú principal.");
            return;
        }
        List<Autor> autores = libros.autores().stream()
                .map(datosAutor -> autorRepository.findByNombreIgnoreCase(datosAutor.nombre())
                        .orElseGet(() -> {

                            Autor nuevoAutor = new Autor();
                            nuevoAutor.setNombre(datosAutor.nombre());
                            nuevoAutor.setFechaNacimiento(datosAutor.fechaNacimiento());
                            nuevoAutor.setFechaFallecimiento(datosAutor.fechaFallecimiento());
                            autorRepository.save(nuevoAutor);
                            return nuevoAutor;
                        })
                ).toList();

        try {
            Libro libro = new Libro(libros);
            libro.setAutor(autores.get(0));
//            libro.setAutor(autores.get(0))
            libroRepository.save(libro);
            System.out.println("LIBRO GUARDADO EN LA BASE DE DATOS ES: " + libros);


        } catch(DataIntegrityViolationException e) {
            System.out.println("""
                       
                       ¡EL LIBRO  YA ESTA REGISTRADO EN LA BD!:
                       """+libros);
        }

    }

    private void mostrarLibros() {
        List<Libro> libros = libroRepository.findAll();

        libros.stream()
                .sorted(Comparator.comparing(Libro::getTitulo))
                .forEach(System.out::println);
    }


    private void buscarAutoresPorFecha() {
        System.out.println("Ingrese el año para mostrar autores vivos en esa fecha");
        var year = teclado.nextInt();
        teclado.nextLine();
        autores = autorRepository.findAutoresVivosEnAnioConLibros(year);

        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año" + year);
        } else {
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(System.out::println);
        }

}
    private void listarAutoresRegistrados(){

        autores = autorRepository.findAll();

        autores.stream()
                .sorted(Comparator.comparing(Autor::getNombre))
                .forEach(System.out::println);
    }
    /*private void buscarLibroPorTitulo() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        var tituloLibro = teclado.nextLine();
        Optional<Libro> libroBuscado = libroRepository.findByTituloContainingIgnoreCase(tituloLibro);

        if (libroBuscado.isPresent()) {
            System.out.println("El libro buscado es: " + libroBuscado.get());
        } else {
            System.out.println("El libro buscado no ha sido encontrado.");
        }
    }*/



}