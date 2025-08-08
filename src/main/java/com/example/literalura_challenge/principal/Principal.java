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
    private final String URL_BASE = "https://gutendex.com/books/";

    @Autowired
    private LibroRepository libroRepository;
    
    @Autowired
    private AutorRepository autorRepository;

    // Metodo para pausar y esperar input del usuario
    private void esperarEnter() {
        System.out.println("\nPresione Enter para continuar...");
        teclado.nextLine();
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            limpiarPantalla();
            var menu = """
                    =======================================
                    ===    CATALOGO DE LIBROS LITERALURA ===
                    =======================================
                    
                    Elije la opcion a traves de su numero:
                    
                    1- Buscar libro por titulo
                    2- Listar libros registrados
                    3- Listar autores registrados
                    4- Listar autores vivos en un determinado ano
                    5- Listar libros por idioma
                    6- Mostrar estadisticas de libros por idioma
                    7- Top 10 libros mas descargados
                    
                    0- Salir
                    =======================================
                    """;
            System.out.println(menu);
            System.out.print("Seleccione una opcion: ");

            try {
                opcion = teclado.nextInt();
                teclado.nextLine(); // Limpiar buffer

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
                        System.out.println("\nGracias por usar Literalura! Hasta pronto!");
                        break;
                    default:
                        System.out.println("\nX Opcion invalida. Por favor, seleccione un numero del 0 al 7.");
                        esperarEnter();
                }
            } catch (Exception e) {
                System.out.println("\nX Error: Ingrese un numero valido (0-7)");
                teclado.nextLine(); // Limpiar el buffer
                esperarEnter();
            }
        }
    }

    private void limpiarPantalla() {
        // Simular limpieza de pantalla con líneas en blanco
        for (int i = 0; i < 3; i++) {
            System.out.println();
        }
    }

    private DatosLibro getDatosLibro() {
        System.out.println("\nBUSCAR LIBRO");
        System.out.println("============");
        System.out.print("Escriba el titulo del libro: ");
        var nombreLibro = teclado.nextLine();
        
        if (nombreLibro.trim().isEmpty()) {
            System.out.println("\nPor favor escriba el titulo de un libro.");
            return null;
        }
        
        try {
            System.out.println("\nBuscando '" + nombreLibro + "'...");
            var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "%20"));
            var datosBusqueda = conversor.obtenerDatos(json, DatosRespuesta.class);
            
            Optional<DatosLibro> libroBuscado = datosBusqueda.resultados().stream()
                    .filter(l -> l.titulo().toUpperCase().contains(nombreLibro.toUpperCase()))
                    .findFirst();
                    
            if (libroBuscado.isPresent()) {
                System.out.println("Libro encontrado!");
                return libroBuscado.get();
            } else {
                System.out.println("No se encontro el libro. Intente con otro titulo.");
                return null;
            }
        } catch (Exception e) {
            System.out.println("No se pudo buscar el libro en este momento.");
            System.out.println("Verifique su conexion a internet e intente nuevamente.");
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
                    System.out.println("\nEste libro ya esta en su biblioteca:");
                    System.out.println(libroExistente.get());
                } else {
                    System.out.println("\nGuardando libro en su biblioteca...");
                    Libro libro = new Libro(datos);
                    libroRepository.save(libro);
                    System.out.println("Libro agregado exitosamente a su biblioteca!");
                    System.out.println(libro);
                }
            } catch (Exception e) {
                System.out.println("\nNo se pudo guardar el libro en su biblioteca.");
                System.out.println("Intente nuevamente mas tarde.");
            }
        }
        esperarEnter();
    }

    private void mostrarLibrosRegistrados() {
        System.out.println("\nMI BIBLIOTECA DE LIBROS");
        System.out.println("=======================");

        List<Libro> libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("Su biblioteca esta vacia.");
            System.out.println("Use la opcion 1 para agregar libros.");
        } else {
            System.out.println("Tiene " + libros.size() + " libro(s) en su biblioteca:");
            System.out.println();
            
            for (int i = 0; i < libros.size(); i++) {
                System.out.println("LIBRO " + (i + 1) + ":");
                System.out.println(libros.get(i));
            }
        }
        esperarEnter();
    }

    private void mostrarAutoresRegistrados() {
        System.out.println("\nAUTORES EN MI BIBLIOTECA");
        System.out.println("========================");

        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores en su biblioteca aun.");
            System.out.println("Los autores aparecen cuando agrega libros.");
        } else {
            System.out.println("Tiene " + autores.size() + " autor(es) en su biblioteca:");
            System.out.println();
            
            for (int i = 0; i < autores.size(); i++) {
                System.out.println("AUTOR " + (i + 1) + ":");
                System.out.println(autores.get(i));
            }
        }
        esperarEnter();
    }

    private void mostrarAutoresVivosEnAno() {
        System.out.println("\nAUTORES VIVOS EN UN ANO");
        System.out.println("=======================");
        System.out.print("Escriba un ano (ejemplo: 1800): ");

        try {
            var anoStr = teclado.nextLine();
            Integer ano = Integer.valueOf(anoStr);
            
            if (ano < 1 || ano > 2024) {
                System.out.println("\nPor favor escriba un ano entre 1 y 2024.");
                esperarEnter();
                return;
            }
            
            System.out.println("\nBuscando autores que estaban vivos en " + ano + "...");
            List<Autor> autoresVivos = autorRepository.findAutoresVivosEnAno(ano);
            
            if (autoresVivos.isEmpty()) {
                System.out.println("No se encontraron autores vivos en " + ano + ".");
                System.out.println("Agregue mas libros para obtener mas informacion.");
            } else {
                System.out.println("Autores que estaban vivos en " + ano + ":");
                System.out.println();
                
                for (int i = 0; i < autoresVivos.size(); i++) {
                    System.out.println("AUTOR " + (i + 1) + ":");
                    System.out.println(autoresVivos.get(i));
                }
                
                System.out.println("Total: " + autoresVivos.size() + " autor(es)");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nPor favor escriba solo numeros.");
        } catch (Exception e) {
            System.out.println("\nOcurrio un problema. Intente nuevamente.");
        }
        esperarEnter();
    }

    private void mostrarLibrosPorIdioma() {
        System.out.println("\nLIBROS POR IDIOMA");
        System.out.println("=================");

        List<String> idiomasDisponibles = libroRepository.findDistinctIdiomas();
        
        if (idiomasDisponibles.isEmpty()) {
            System.out.println("No hay libros en su biblioteca aun.");
            System.out.println("Agregue algunos libros primero.");
            esperarEnter();
            return;
        }
        
        System.out.println("Idiomas disponibles en su biblioteca:");
        System.out.println();
        idiomasDisponibles.forEach(idioma -> {
            Long cantidad = libroRepository.countByIdioma(idioma);
            String nombreIdioma = obtenerNombreIdioma(idioma);
            System.out.printf("- %s (%s): %d libro(s)%n", nombreIdioma, idioma, cantidad);
        });
        
        System.out.println();
        System.out.print("Escriba el codigo del idioma (ej: es, en, fr): ");
        var idioma = teclado.nextLine().toLowerCase().trim();
        
        if (idioma.isEmpty()) {
            System.out.println("\nPor favor escriba un codigo de idioma.");
            esperarEnter();
            return;
        }
        
        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idioma);
        if (librosPorIdioma.isEmpty()) {
            System.out.println("\nNo tiene libros en " + obtenerNombreIdioma(idioma) + ".");
        } else {
            System.out.println("\nLibros en " + obtenerNombreIdioma(idioma) + ":");
            System.out.println("=====================================");
            
            for (int i = 0; i < librosPorIdioma.size(); i++) {
                System.out.println("LIBRO " + (i + 1) + ":");
                System.out.println(librosPorIdioma.get(i));
            }
            
            System.out.println("Total: " + librosPorIdioma.size() + " libro(s)");
        }
        esperarEnter();
    }

    private void mostrarEstadisticasPorIdioma() {
        System.out.println("\nESTADISTICAS DE MI BIBLIOTECA");
        System.out.println("=============================");

        List<Object[]> estadisticas = libroRepository.obtenerEstadisticasPorIdioma();
        
        if (estadisticas.isEmpty()) {
            System.out.println("Su biblioteca esta vacia.");
            System.out.println("Agregue algunos libros para ver estadisticas.");
            esperarEnter();
            return;
        }
        
        System.out.println("Libros por idioma en su biblioteca:");
        System.out.println();

        estadisticas.forEach(stat -> {
            String idioma = (String) stat[0];
            Long cantidad = (Long) stat[1];
            String nombreIdioma = obtenerNombreIdioma(idioma);
            System.out.printf("- %s: %d libro(s)%n", nombreIdioma, cantidad);
        });
        
        // Mostrar total general
        Long totalLibros = estadisticas.stream()
            .mapToLong(stat -> (Long) stat[1])
            .sum();
        
        System.out.println();
        System.out.println("Total de libros: " + totalLibros);
        System.out.println("Idiomas diferentes: " + estadisticas.size());
        esperarEnter();
    }

    private void mostrarTop10LibrosMasDescargados() {
        System.out.println("\nLIBROS MAS POPULARES");
        System.out.println("====================");

        List<Libro> topLibros = libroRepository.findTop10ByOrderByNumeroDeDescargasDesc();
        
        if (topLibros.isEmpty()) {
            System.out.println("Su biblioteca esta vacia.");
            System.out.println("Agregue algunos libros para ver los mas populares.");
            esperarEnter();
            return;
        }
        
        System.out.println("Los libros mas descargados en su biblioteca:");
        System.out.println();
        
        for (int i = 0; i < Math.min(10, topLibros.size()); i++) {
            Libro libro = topLibros.get(i);
            String posicion = obtenerMedalla(i + 1);
            System.out.printf("%s %s%n", posicion, libro.getTitulo());
            System.out.printf("   Autor: %s%n",
                libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido");
            System.out.printf("   Popularidad: %.0f descargas%n", libro.getNumeroDeDescargas());
            System.out.printf("   Idioma: %s%n", obtenerNombreIdioma(libro.getIdioma()));
            System.out.println();
        }
        
        System.out.println("Mostrando " + Math.min(10, topLibros.size()) + " libro(s)");
        esperarEnter();
    }

    // Metodos auxiliares para mejor presentacion
    private String obtenerNombreIdioma(String codigo) {
        return switch (codigo.toLowerCase()) {
            case "es" -> "Espanol";
            case "en" -> "Ingles";
            case "fr" -> "Frances";
            case "pt" -> "Portugues";
            case "de" -> "Aleman";
            case "it" -> "Italiano";
            case "ru" -> "Ruso";
            case "zh" -> "Chino";
            case "ja" -> "Japones";
            default -> "Idioma desconocido";
        };
    }

    private String obtenerMedalla(int posicion) {
        return switch (posicion) {
            case 1 -> "[1er]";
            case 2 -> "[2do]";
            case 3 -> "[3er]";
            default -> "[" + posicion + "]";
        };
    }
}
