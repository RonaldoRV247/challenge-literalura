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
                    6- Mostrar estadísticas de libros por idioma
                    7- Top 10 libros más descargados
                    
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
                    case 6:
                        mostrarEstadisticasPorIdioma();
                        break;
                    case 7:
                        mostrarTop10LibrosMasDescargados();
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

        if (nombreLibro.trim().isEmpty()) {
            System.out.println("Error: Debe ingresar un nombre de libro válido");
            return null;
        }

        try {
            var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "%20"));
            var datosBusqueda = conversor.obtenerDatos(json, DatosRespuesta.class);

            Optional<DatosLibro> libroBuscado = datosBusqueda.resultados().stream()
                    .filter(l -> l.titulo().toUpperCase().contains(nombreLibro.toUpperCase()))
                    .findFirst();

            if (libroBuscado.isPresent()) {
                System.out.println("Libro encontrado en la API!");
                return libroBuscado.get();
            } else {
                System.out.println("Libro no encontrado en la API");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error al consultar la API: " + e.getMessage());
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
            System.out.println("Total de libros registrados: " + libros.size());
        }
    }

    private void mostrarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados en la base de datos.");
        } else {
            System.out.println("\n--- AUTORES REGISTRADOS ---");
            autores.forEach(System.out::println);
            System.out.println("Total de autores registrados: " + autores.size());
        }
    }

    private void mostrarAutoresVivosEnAno() {
        System.out.println("Ingrese el año que desea consultar:");
        try {
            var anoStr = teclado.nextLine();
            Integer ano = Integer.valueOf(anoStr);

            if (ano < 0 || ano > 2024) {
                System.out.println("Error: Ingrese un año válido (entre 1 y 2024)");
                return;
            }

            List<Autor> autoresVivos = autorRepository.findAutoresVivosEnAno(ano);
            if (autoresVivos.isEmpty()) {
                System.out.println("No hay autores vivos registrados en el año " + ano);
            } else {
                System.out.println("\n--- AUTORES VIVOS EN " + ano + " ---");
                autoresVivos.forEach(System.out::println);
                System.out.println("Total de autores vivos en " + ano + ": " + autoresVivos.size());
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese un año válido (número entero)");
        } catch (Exception e) {
            System.out.println("Error al consultar autores: " + e.getMessage());
        }
    }

    private void mostrarLibrosPorIdioma() {
        List<String> idiomasDisponibles = libroRepository.findDistinctIdiomas();

        if (idiomasDisponibles.isEmpty()) {
            System.out.println("No hay libros registrados en la base de datos.");
            return;
        }

        System.out.println("Idiomas disponibles en la base de datos:");
        idiomasDisponibles.forEach(idioma -> {
            Long cantidad = libroRepository.countByIdioma(idioma);
            System.out.println("- " + idioma + " (" + cantidad + " libros)");
        });

        System.out.println("\nIngrese el código del idioma que desea consultar:");
        var idioma = teclado.nextLine().toLowerCase().trim();

        if (idioma.isEmpty()) {
            System.out.println("Error: Debe ingresar un código de idioma válido");
            return;
        }

        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idioma);
        if (librosPorIdioma.isEmpty()) {
            System.out.println("No hay libros registrados en el idioma: " + idioma);
        } else {
            System.out.println("\n--- LIBROS EN " + idioma.toUpperCase() + " ---");
            librosPorIdioma.forEach(System.out::println);
            System.out.println("Total de libros en " + idioma + ": " + librosPorIdioma.size());
        }
    }

    private void mostrarEstadisticasPorIdioma() {
        List<Object[]> estadisticas = libroRepository.obtenerEstadisticasPorIdioma();

        if (estadisticas.isEmpty()) {
            System.out.println("No hay libros registrados en la base de datos.");
            return;
        }

        System.out.println("\n--- ESTADÍSTICAS DE LIBROS POR IDIOMA ---");
        System.out.println("Idioma\t\tCantidad de Libros");
        System.out.println("------------------------------------");

        estadisticas.forEach(stat -> {
            String idioma = (String) stat[0];
            Long cantidad = (Long) stat[1];
            System.out.printf("%-10s\t%d libros%n", idioma.toUpperCase(), cantidad);
        });

        // Mostrar total general
        Long totalLibros = estadisticas.stream()
            .mapToLong(stat -> (Long) stat[1])
            .sum();
        System.out.println("------------------------------------");
        System.out.println("TOTAL: " + totalLibros + " libros");
    }

    private void mostrarTop10LibrosMasDescargados() {
        List<Libro> topLibros = libroRepository.findTop10ByOrderByNumeroDeDescargasDesc();

        if (topLibros.isEmpty()) {
            System.out.println("No hay libros registrados en la base de datos.");
            return;
        }

        System.out.println("\n--- TOP 10 LIBROS MÁS DESCARGADOS ---");
        for (int i = 0; i < Math.min(10, topLibros.size()); i++) {
            Libro libro = topLibros.get(i);
            System.out.printf("%d. %s - %.0f descargas%n",
                (i + 1), libro.getTitulo(), libro.getNumeroDeDescargas());
        }
    }
}
