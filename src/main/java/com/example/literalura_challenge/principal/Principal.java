package com.example.literalura_challenge.principal;

import com.example.literalura_challenge.model.*;
import com.example.literalura_challenge.repository.AutorRepository;
import com.example.literalura_challenge.repository.LibroRepository;
import com.example.literalura_challenge.service.ConsumoAPI;
import com.example.literalura_challenge.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private final String URL_BASE = "https://gutendx.com/books/";

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutorRepository autorRepository;

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    *********************************
                    Elije la opción a través de su número:
                    1- Buscar libro por título
                    2- Listar libros registrados
                    3- Listar autores registrados
                    4- Listar autores vivos en un determinado año
                    5- Listar libros por idioma
                    
                    0- Salir
                    *********************************
                    """;
            System.out.println(menu);
            try {
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 1:
                        buscarLibroWeb();
                        break;
                    case 2:
                        mostrarLibrosRegistrados();
                        break;
                    case 3:
                        mostrarAutoresRegistrados();
                        break;
                    case 4:
                        mostrarAutoresVivosEnAno();
                        break;
                    case 5:
                        mostrarLibrosPorIdioma();
                        break;
                    case 0:
                        System.out.println("Cerrando la aplicación...");
                        break;
                    default:
                        System.out.println("Opción inválida");
                }
            } catch (Exception e) {
                System.out.println("Error: Ingrese un número válido");
                teclado.nextLine(); // Limpiar el buffer
            }
        }
    }

    private DatosLibro getDatosLibro() {
        System.out.println("Escribe el nombre del libro que deseas buscar:");
        var nombreLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "%20"));
        System.out.println("JSON recibido: " + json);
        var datosBusqueda = conversor.obtenerDatos(json, DatosRespuesta.class);
        Optional<DatosLibro> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(nombreLibro.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()) {
            System.out.println("Libro encontrado");
            return libroBuscado.get();
        } else {
            System.out.println("Libro no encontrado");
            return null;
        }
    }

    private void buscarLibroWeb() {
        DatosLibro datos = getDatosLibro();
        if (datos != null) {
            try {
                // Verificar si el libro ya existe en la base de datos
                Optional<Libro> libroExistente = libroRepository.findByTituloContainsIgnoreCase(datos.titulo());
                if (libroExistente.isPresent()) {
                    System.out.println("El libro ya está registrado en la base de datos:");
                    System.out.println(libroExistente.get());
                } else {
                    Libro libro = new Libro(datos);
                    libroRepository.save(libro);
                    System.out.println("¡Libro guardado exitosamente!");
                    System.out.println(libro);
                }
            } catch (Exception e) {
                System.out.println("Error al guardar el libro: " + e.getMessage());
            }
        }
    }

    private void mostrarLibrosRegistrados() {
        List<Libro> libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados en la base de datos.");
        } else {
            System.out.println("\n--- LIBROS REGISTRADOS ---");
            libros.forEach(System.out::println);
        }
    }

    private void mostrarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados en la base de datos.");
        } else {
            System.out.println("\n--- AUTORES REGISTRADOS ---");
            autores.forEach(System.out::println);
        }
    }

    private void mostrarAutoresVivosEnAno() {
        System.out.println("Ingrese el año que desea consultar:");
        try {
            var ano = teclado.nextLine();
            List<Autor> autoresVivos = autorRepository.findAutoresVivosEnAno(ano);
            if (autoresVivos.isEmpty()) {
                System.out.println("No hay autores vivos en el año " + ano);
            } else {
                System.out.println("\n--- AUTORES VIVOS EN " + ano + " ---");
                autoresVivos.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Error: Ingrese un año válido");
        }
    }

    private void mostrarLibrosPorIdioma() {
        var menuIdiomas = """
                Ingrese el idioma para buscar los libros:
                es - español
                en - inglés
                fr - francés
                pt - portugués
                """;
        System.out.println(menuIdiomas);
        var idioma = teclado.nextLine();
        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idioma);
        if (librosPorIdioma.isEmpty()) {
            System.out.println("No hay libros registrados en el idioma: " + idioma);
        } else {
            System.out.println("\n--- LIBROS EN " + idioma.toUpperCase() + " ---");
            librosPorIdioma.forEach(System.out::println);
        }
    }
}
